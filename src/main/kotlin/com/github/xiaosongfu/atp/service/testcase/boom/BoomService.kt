package com.github.xiaosongfu.atp.service.testcase.boom

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.xiaosongfu.atp.domain.vo.boom.BoomVO
import com.github.xiaosongfu.atp.entity.project.ProjectRequest
import com.github.xiaosongfu.atp.entity.testcase.TestCase
import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteDetail
import com.github.xiaosongfu.atp.service.testcase.TestCaseExecuteService
import com.github.xiaosongfu.atp.service.testcase.boom.box.HttpBox
import com.github.xiaosongfu.atp.service.testcase.boom.box.HttpRequest
import com.github.xiaosongfu.atp.service.testcase.boom.box.ReplaceParamBox
import com.github.xiaosongfu.jakarta.exception.service.ServiceException
import com.jayway.jsonpath.JsonPath
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BoomService {
    @Autowired
    private lateinit var testCaseDataService: TestCaseDataService

    @Autowired
    private lateinit var testCaseExecuteService: TestCaseExecuteService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var httpBox: HttpBox

    @Autowired
    private lateinit var replaceParamBox: ReplaceParamBox

    private val log = LoggerFactory.getLogger(javaClass)

    fun boom(projectId: Long, projectServerId: Long, testCaseId: Long) {
        // 执行会话 ID
        val executeSessionId = System.currentTimeMillis().toString()

        // 获取测试案例数据
        val boom: BoomVO
        try {
            boom = testCaseDataService.readTestCaseFullData(projectId, projectServerId, testCaseId)
                ?: throw ServiceException(msg = "获取测试案例数据失败")
        } catch (e: ServiceException) {
            log.error("执行失败！[${e.msg}]")
            e.printStackTrace()

            // TAG :: 保存 测试案例执行记录 -> 开始执行就立即失败
            testCaseExecuteService.insertHistoryForStartAndDirectFailed(
                "[$projectServerId]",
                executeSessionId,
                testCaseId,
                e.msg
            )

            // 一定要 return 以结束代码执行
            return
        }

        // 开始执行
        log.debug("$executeSessionId :: 开始执行：ProjectId: $projectId | TestCaseId: $testCaseId | case name(${boom.name}) | case type(${boom.type}) | projectServerName(${boom.projectServerName})")
        // TAG :: 保存 测试案例执行记录 -- 开始执行
        testCaseExecuteService.insertHistoryForStartRunning(boom.projectServerName, executeSessionId, testCaseId)

        val testCaseExecuteHistories: List<TestCaseExecuteDetail>? = when (boom.type) {
            TestCase.TEST_CASE_TYPE_BENCHMARK -> {
                if (boom.benchmark != null) {
                    executeBenchmark(executeSessionId, boom.benchmark!!)
                } else {
                    log.error("测试案例类型为[${boom.type}],但 benchmark 数据为空")
                    null
                }
            }
            TestCase.TEST_CASE_TYPE_REPLAY -> {
                if (boom.replay != null) {
                    executeReplay(executeSessionId, boom.replay!!)
                } else {
                    log.error("测试案例类型为[${boom.type}],但 replay 数据为空")
                    null
                }
            }
            TestCase.TEST_CASE_TYPE_PIPELINE -> {
                if (boom.pipeline != null) {
                    executePipeline(executeSessionId, boom.pipeline!!)
                } else {
                    log.error("测试案例类型为[${boom.type}],但 pipeline 数据为空")
                    null
                }
            }
            else -> {
                log.error("测试案例类型[${boom.type}]不支持")
                null
            }
        }

        // 保存执行结果到数据
        if (testCaseExecuteHistories != null) {
            // 计算总的请求数
            val requestTotalCount = testCaseExecuteHistories.size
            // 请求验证成功的请求数
            val requestCheckCorrectCount = testCaseExecuteHistories.count {
                it.execCheckResult == TestCaseExecuteDetail.EXEC_CHECK_RESULT_CORRECT
            }
            // 请求验证失败的请求数
            val requestCheckWrongCount = testCaseExecuteHistories.count {
                it.execCheckResult == TestCaseExecuteDetail.EXEC_CHECK_RESULT_WRONG
            }
            // 请求错误的请求数
            val requestErrorCount = testCaseExecuteHistories.count {
                it.execCheckResult == TestCaseExecuteDetail.EXEC_CHECK_REQUEST_ERROR
            }

            // TAG :: 更新 测试案例执行记录 -- 成功
            testCaseExecuteService.updateHistoryForExecuteSuccess(
                executeSessionId,
                requestTotalCount,
                requestCheckCorrectCount,
                requestCheckWrongCount,
                requestErrorCount
            )
            // 保存 测试案例执行记录详情
            testCaseExecuteService.insertDetails(testCaseExecuteHistories)

            log.debug("${boom.name} 执行成功!")
        } else {
            // TAG :: 更新 测试案例执行记录 -- 失败
            testCaseExecuteService.updateHistoryForExecuteFailed(executeSessionId, "可能没有没有配置请求")

            log.debug("${boom.name} 执行失败!")
        }
    }

    private fun executeBenchmark(
        executeSessionId: String,
        benchmark: BoomVO.Benchmark
    ): List<TestCaseExecuteDetail> {
        // 切割请求参数
        val testCaseRequestParams = benchmark.request.param?.split("##") ?: emptyList()
        log.debug("$executeSessionId :: 请求[${benchmark.request.name}]的请求参数:$testCaseRequestParams")

        // 替换占位变量
        replaceParamBox.replacePositionAndEnvParams(
            benchmark.fetchApi,
            testCaseRequestParams,
            BoomStore.readEnvs(executeSessionId)
        )

        // 封装 http 请求
        val httpRequest = HttpRequest(
            url = benchmark.fetchApi.url,
            method = benchmark.fetchApi.method,
            contentType = benchmark.fetchApi.contentType ?: ProjectRequest.ContentType.FORM,
            header = benchmark.fetchApi.header?.let {
                objectMapper.readValue(it, object : TypeReference<HashMap<String, String>>() {})
            },
            param = benchmark.fetchApi.param?.let {
                objectMapper.readValue(it, object : TypeReference<HashMap<String, Any>>() {})
            }
        )

        // 执行 http 请求
        return (0..50).map {
            // 执行 http 请求
            val httpResponse = httpBox.doHttp(executeSessionId, httpRequest)
            log.debug("$executeSessionId :: HTTP 请求[${benchmark.request.name}]的响应结果 $httpResponse")
            // 处理 http 响应
            if (httpResponse.code == 200) { // ---> 请求成功
                val ctx = JsonPath.parse(httpResponse.body)
                // 执行响应验证
                val execCheckInfo = benchmark.execCheck?.mapNotNull { check ->
                    val fieldValue = ctx.read<Any>(check.fieldPath)
                    log.debug("$executeSessionId :: 执行 HTTP 请求[${benchmark.request.name}]的响应验证: 字段名称[${check.fieldName}] 期望值[${check.wantFieldValue}] 实际值[$fieldValue]")

                    // 判断验证是否成功
                    if (fieldValue != null) {
                        // xx
                        ExecCheckInfo(
                            fieldName = check.fieldName,
                            wantFieldValue = check.wantFieldValue,
                            gotFieldValue = fieldValue.toString(),
                            checkResult = fieldValue.toString() == check.wantFieldValue
                        )
                    } else {
                        null
                    }
                }
                // 执行保存环境变量
                val saveEnvVariableInfo = benchmark.saveEnvVariable?.mapNotNull { saveEnvVariable ->
                    val variableValue = ctx.read<Any>(saveEnvVariable.projectEnvVariableValuePath)
                    log.debug("$executeSessionId :: 执行 HTTP 请求[${benchmark.request.name}]的保存环境变量: 变量名称[${saveEnvVariable.variableName}] 默认值[${saveEnvVariable.defaultValue}] 实际值[$variableValue]")

                    // 保存环境变量
                    if (variableValue != null) {
                        BoomStore.saveEnv(executeSessionId, saveEnvVariable.variableName, variableValue.toString())
                        // xx
                        SaveEnvVariableInfo(
                            variableName = saveEnvVariable.variableName,
                            variableValue = variableValue.toString()
                        )
                    } else {
                        null
                    }
                }

                // 转换
                TestCaseExecuteDetail(
                    executeHistoryId = executeSessionId,
                    testCaseRequestName = benchmark.request.name,
                    httpRequest = objectMapper.writeValueAsString(httpRequest),
                    httpResponse = objectMapper.writeValueAsString(httpResponse),
                    execCheckInfo = objectMapper.writeValueAsString(execCheckInfo),
                    execCheckResult = if (execCheckInfo?.all { it.checkResult } == true) {
                        TestCaseExecuteDetail.EXEC_CHECK_RESULT_CORRECT
                    } else {
                        TestCaseExecuteDetail.EXEC_CHECK_RESULT_WRONG
                    },
                    saveEnvVariableInfo = objectMapper.writeValueAsString(saveEnvVariableInfo)
                )
            } else { // ---> 请求错误
                // 转换
                TestCaseExecuteDetail(
                    executeHistoryId = executeSessionId,
                    testCaseRequestName = benchmark.request.name,
                    httpRequest = objectMapper.writeValueAsString(httpRequest),
                    httpResponse = objectMapper.writeValueAsString(httpResponse),
                    execCheckInfo = null,
                    execCheckResult = TestCaseExecuteDetail.EXEC_CHECK_REQUEST_ERROR,
                    saveEnvVariableInfo = null
                )
            }
        }
    }

    private fun executeReplay(
        executeSessionId: String,
        replay: BoomVO.Replay
    ): List<TestCaseExecuteDetail> {
        // 遍历并执行每个请求
        return replay.requests.map { bundle ->
            // 切割请求参数
            val testCaseRequestParams = bundle.request.param?.split("##") ?: emptyList()
            log.debug("$executeSessionId :: 请求[${bundle.request.name}]的请求参数:$testCaseRequestParams")

            // 替换占位变量后会修改 replay.fetchApi 所以这里需要使用它的一个副本
            val willModifiedFetchApi = replay.fetchApi.copy()
            // 替换占位变量
            replaceParamBox.replacePositionAndEnvParams(
                willModifiedFetchApi, // ！使用 replay.fetchApi 的副本！
                testCaseRequestParams,
                BoomStore.readEnvs(executeSessionId)
            )

            // 封装 http 请求
            val httpRequest = HttpRequest(
                url = willModifiedFetchApi.url, // ！使用副本被替换后的值！
                method = replay.fetchApi.method,
                contentType = replay.fetchApi.contentType ?: ProjectRequest.ContentType.FORM,
                header = willModifiedFetchApi.header?.let { // ！使用副本被替换后的值！
                    objectMapper.readValue(it, object : TypeReference<HashMap<String, String>>() {})
                },
                param = willModifiedFetchApi.param?.let { // ！使用副本被替换后的值！
                    objectMapper.readValue(it, object : TypeReference<HashMap<String, Any>>() {})
                }
            )
            // 执行 http 请求
            val httpResponse = httpBox.doHttp(executeSessionId, httpRequest)
            log.debug("$executeSessionId :: HTTP 请求[${bundle.request.name}]的响应结果 $httpResponse")
            // 处理 http 响应
            if (httpResponse.code == 200) { // ---> 请求成功
                val ctx = JsonPath.parse(httpResponse.body)
                // 执行响应验证
                val execCheckInfo = bundle.execCheck?.mapNotNull { check ->
                    val fieldValue = ctx.read<Any>(check.fieldPath)
                    log.debug("$executeSessionId :: 执行 HTTP 请求[${bundle.request.name}]的响应验证: 字段名称[${check.fieldName}] 期望值[${check.wantFieldValue}] 实际值[$fieldValue]")

                    // 判断验证是否成功
                    if (fieldValue != null) {
                        // xx
                        ExecCheckInfo(
                            fieldName = check.fieldName,
                            wantFieldValue = check.wantFieldValue,
                            gotFieldValue = fieldValue.toString(),
                            checkResult = fieldValue.toString() == check.wantFieldValue
                        )
                    } else {
                        null
                    }
                }
                // 执行保存环境变量
                val saveEnvVariableInfo = bundle.saveEnvVariable?.mapNotNull { saveEnvVariable ->
                    val variableValue = ctx.read<Any>(saveEnvVariable.projectEnvVariableValuePath)
                    log.debug("$executeSessionId :: 执行 HTTP 请求[${bundle.request.name}]的保存环境变量: 变量名称[${saveEnvVariable.variableName}] 默认值[${saveEnvVariable.defaultValue}] 实际值[$variableValue]")

                    // 保存环境变量
                    if (variableValue != null) {
                        BoomStore.saveEnv(executeSessionId, saveEnvVariable.variableName, variableValue.toString())
                        // xx
                        SaveEnvVariableInfo(
                            variableName = saveEnvVariable.variableName,
                            variableValue = variableValue.toString()
                        )
                    } else {
                        null
                    }
                }

                // 转换
                TestCaseExecuteDetail(
                    executeHistoryId = executeSessionId,
                    testCaseRequestName = bundle.request.name,
                    httpRequest = objectMapper.writeValueAsString(httpRequest),
                    httpResponse = objectMapper.writeValueAsString(httpResponse),
                    execCheckInfo = objectMapper.writeValueAsString(execCheckInfo),
                    execCheckResult = if (execCheckInfo?.all { it.checkResult } == true) {
                        TestCaseExecuteDetail.EXEC_CHECK_RESULT_CORRECT
                    } else {
                        TestCaseExecuteDetail.EXEC_CHECK_RESULT_WRONG
                    },
                    saveEnvVariableInfo = objectMapper.writeValueAsString(saveEnvVariableInfo)
                )
            } else { // ---> 请求错误
                // 转换
                TestCaseExecuteDetail(
                    executeHistoryId = executeSessionId,
                    testCaseRequestName = bundle.request.name,
                    httpRequest = objectMapper.writeValueAsString(httpRequest),
                    httpResponse = objectMapper.writeValueAsString(httpResponse),
                    execCheckInfo = null,
                    execCheckResult = TestCaseExecuteDetail.EXEC_CHECK_REQUEST_ERROR,
                    saveEnvVariableInfo = null
                )
            }
        }
    }

    private fun executePipeline(
        executeSessionId: String,
        pipeline: BoomVO.Pipeline
    ): List<TestCaseExecuteDetail> {
        // 遍历并执行每个请求
        return pipeline.requests.map { bundle ->
            // 切割请求参数
            val testCaseRequestParams = bundle.request.param?.split("##") ?: emptyList()
            log.debug("$executeSessionId :: HTTP 请求[${bundle.request.name}]的请求参数:$testCaseRequestParams")

            // 替换占位变量
            replaceParamBox.replacePositionAndEnvParams(
                bundle.fetchApi,
                testCaseRequestParams,
                BoomStore.readEnvs(executeSessionId)
            )

            // 封装 http 请求
            val httpRequest = HttpRequest(
                url = bundle.fetchApi.url,
                method = bundle.fetchApi.method,
                contentType = bundle.fetchApi.contentType ?: ProjectRequest.ContentType.FORM,
                header = bundle.fetchApi.header?.let {
                    objectMapper.readValue(it, object : TypeReference<HashMap<String, String>>() {})
                },
                param = bundle.fetchApi.param?.let {
                    objectMapper.readValue(it, object : TypeReference<HashMap<String, Any>>() {})
                }
            )

            // 执行 http 请求
            val httpResponse = httpBox.doHttp(executeSessionId, httpRequest)
            log.debug("$executeSessionId :: HTTP 请求[${bundle.request.name}]的响应结果 $httpResponse")

            // 处理 http 响应
            if (httpResponse.code == 200) { // ---> 请求成功
                val ctx = JsonPath.parse(httpResponse.body)
                // 执行响应验证
                val execCheckInfo = bundle.execCheck?.mapNotNull { check ->
                    val fieldValue = ctx.read<Any>(check.fieldPath)
                    log.debug("$executeSessionId :: 执行 HTTP 请求[${bundle.request.name}]的响应验证: 字段名称[${check.fieldName}] 期望值[${check.wantFieldValue}] 实际值[$fieldValue]")

                    // 判断验证是否成功
                    if (fieldValue != null) {
                        // xx
                        ExecCheckInfo(
                            fieldName = check.fieldName,
                            wantFieldValue = check.wantFieldValue,
                            gotFieldValue = fieldValue.toString(),
                            checkResult = fieldValue.toString() == check.wantFieldValue
                        )
                    } else {
                        null
                    }
                }
                // 执行保存环境变量
                val saveEnvVariableInfo = bundle.saveEnvVariable?.mapNotNull { saveEnvVariable ->
                    val variableValue = ctx.read<Any>(saveEnvVariable.projectEnvVariableValuePath)
                    log.debug("$executeSessionId :: 执行 HTTP 请求[${bundle.request.name}]的保存环境变量: 变量名称[${saveEnvVariable.variableName}] 默认值[${saveEnvVariable.defaultValue}] 实际值[$variableValue]")

                    // 保存环境变量
                    if (variableValue != null) {
                        BoomStore.saveEnv(executeSessionId, saveEnvVariable.variableName, variableValue.toString())
                        // xx
                        SaveEnvVariableInfo(
                            variableName = saveEnvVariable.variableName,
                            variableValue = variableValue.toString()
                        )
                    } else {
                        null
                    }
                }

                // 转换
                TestCaseExecuteDetail(
                    executeHistoryId = executeSessionId,
                    testCaseRequestName = bundle.request.name,
                    httpRequest = objectMapper.writeValueAsString(httpRequest),
                    httpResponse = objectMapper.writeValueAsString(httpResponse),
                    execCheckInfo = objectMapper.writeValueAsString(execCheckInfo),
                    execCheckResult = if (execCheckInfo?.all { it.checkResult } == true) {
                        TestCaseExecuteDetail.EXEC_CHECK_RESULT_CORRECT
                    } else {
                        TestCaseExecuteDetail.EXEC_CHECK_RESULT_WRONG
                    },
                    saveEnvVariableInfo = objectMapper.writeValueAsString(saveEnvVariableInfo)
                )
            } else { // ---> 请求错误
                // 转换
                TestCaseExecuteDetail(
                    executeHistoryId = executeSessionId,
                    testCaseRequestName = bundle.request.name,
                    httpRequest = objectMapper.writeValueAsString(httpRequest),
                    httpResponse = objectMapper.writeValueAsString(httpResponse),
                    execCheckInfo = null,
                    execCheckResult = TestCaseExecuteDetail.EXEC_CHECK_REQUEST_ERROR,
                    saveEnvVariableInfo = null
                )
            }
        }
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    data class ExecCheckInfo(
        var fieldName: String,
        var wantFieldValue: String,
        var gotFieldValue: String,
        var checkResult: Boolean
    )

    data class SaveEnvVariableInfo(
        var variableName: String,
        var variableValue: String
    )
}
