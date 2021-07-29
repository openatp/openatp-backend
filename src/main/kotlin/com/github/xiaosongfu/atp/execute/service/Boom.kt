package com.github.xiaosongfu.atp.execute.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.xiaosongfu.atp.domain.vo.execute.BoomVO
import com.github.xiaosongfu.atp.entity.project.ProjectRequest
import com.github.xiaosongfu.atp.entity.testcase.TestCase
import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteHistory
import com.github.xiaosongfu.atp.execute.box.HttpBox
import com.github.xiaosongfu.atp.execute.box.HttpRequest
import com.github.xiaosongfu.atp.service.testcase.execute.TestCaseExecuteService
import com.github.xiaosongfu.jakarta.exception.service.ServiceException
import com.jayway.jsonpath.JsonPath
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class Boom {
    @Autowired
    private lateinit var testCaseExecuteService: TestCaseExecuteService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var httpBox: HttpBox

    private val log = LoggerFactory.getLogger(javaClass)

    fun boom(projectId: Long, projectServerId: Long, testCaseId: Long) {
        // 执行 ID
        val executeId = System.currentTimeMillis().toString()

        log.debug("$executeId :: 开始执行：Project: $projectId | Server: $projectServerId | TestCase: $testCaseId")

        // 获取测试案例数据
        val boom: BoomVO
        try {
            boom = testCaseExecuteService.readTestCaseFullData(projectId, projectServerId, testCaseId)
                ?: throw ServiceException(msg = "获取测试案例数据失败")
        } catch (e: ServiceException) {
            log.error("执行失败！[${e.msg}]")
            e.printStackTrace()

            // 一定要 return 以结束代码执行
            return
        }

        log.debug("$executeId :: case name(${boom.name}) | case type(${boom.type})")

        val testCaseExecuteHistories: List<TestCaseExecuteHistory>? = when (boom.type) {
            TestCase.TEST_CASE_TYPE_BENCHMARK -> {
                if (boom.benchmark != null) {
                    executeBenchmark(executeId, testCaseId, boom.benchmark!!)
                } else {
                    log.error("测试案例类型为[${boom.type}],但 benchmark 数据为空")
                    null
                }
            }
            TestCase.TEST_CASE_TYPE_REPLAY -> {
                if (boom.replay != null) {
                    executeReplay(executeId, testCaseId, boom.replay!!)
                } else {
                    log.error("测试案例类型为[${boom.type}],但 replay 数据为空")
                    null
                }
            }
            TestCase.TEST_CASE_TYPE_PIPELINE -> {
                if (boom.pipeline != null) {
                    executePipeline(executeId, testCaseId, boom.pipeline!!)
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
        testCaseExecuteHistories?.let {
            testCaseExecuteService.saveTestCaseExecuteHistory(it)
        }
    }

    private fun executeBenchmark(
        executeId: String,
        testCaseId: Long,
        benchmark: BoomVO.Benchmark
    ): List<TestCaseExecuteHistory> {
        TODO()
    }

    private fun executeReplay(
        executeId: String,
        testCaseId: Long,
        replay: BoomVO.Replay
    ): List<TestCaseExecuteHistory> {
        // 遍历并执行每个请求
        return replay.requests.map { bundle ->
            // STEP 1: 准备并发起 HTTP 请求
            val testCaseRequestParams = bundle.request.param?.split("##") ?: emptyList() // 切割请求参数
            log.debug("$executeId :: 请求参数 $testCaseRequestParams")
            replacePositionAndEnvParams(replay.fetchApi, testCaseRequestParams, BoomStore.readEnvs(executeId)) // 替换占位变量
            val httpRequest = HttpRequest(
                url = replay.fetchApi.url,
                method = replay.fetchApi.method,
                contentType = replay.fetchApi.contentType ?: ProjectRequest.ContentType.FORM,
                header = replay.fetchApi.header?.let {
                    objectMapper.readValue(it, object : TypeReference<HashMap<String, String>>() {})
                },
                param = replay.fetchApi.param?.let {
                    objectMapper.readValue(it, object : TypeReference<HashMap<String, Any>>() {})
                }
            )
            val httpResponse = httpBox.doHttp(executeId, httpRequest)
            log.debug("$executeId :: HTTP 响应码 ${httpResponse.responseCode}")
            // 请求成功
            if (httpResponse.responseCode == 200) {
                // STEP 2:
                // STEP 3:
                val ctx = JsonPath.parse(httpResponse.responseBody)
                val execCheckInfo = bundle.execCheck?.mapNotNull { check ->
                    val fieldValue = ctx.read<Any>(check.fieldPath)
                    log.debug("$executeId :: 执行响应验证 字段名称[${check.fieldName}] 期望值[${check.wantFieldValue}] 实际值[$fieldValue]")

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
                val saveEnvVariableInfo = bundle.saveEnvVariable?.mapNotNull { saveEnvVariable ->
                    val variableValue = ctx.read<Any>(saveEnvVariable.projectEnvVariableValuePath)
                    log.debug("$executeId :: 执行保存环境遍历 变量名称[${saveEnvVariable.variableName}] 默认值[${saveEnvVariable.defaultValue}] 实际值[$variableValue]")

                    // 保存环境遍历
                    if (variableValue != null) {
                        BoomStore.saveEnv(executeId, saveEnvVariable.variableName, variableValue.toString())
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
                TestCaseExecuteHistory(
                    testCaseId = testCaseId,
                    executeId = executeId,
                    testCaseRequestName = bundle.request.name,
                    httpRequest = objectMapper.writeValueAsString(httpRequest),
                    httpResponse = objectMapper.writeValueAsString(httpResponse),
                    execCheckInfo = objectMapper.writeValueAsString(execCheckInfo),
                    execCheckResult = if (execCheckInfo?.all { it.checkResult } == true) {
                        TestCaseExecuteHistory.EXEC_CHECK_RESULT_CORRECT
                    } else {
                        TestCaseExecuteHistory.EXEC_CHECK_RESULT_WRONG
                    },
                    saveEnvVariableInfo = objectMapper.writeValueAsString(saveEnvVariableInfo)
                )
            } else {
                // 转换
                TestCaseExecuteHistory(
                    testCaseId = testCaseId,
                    executeId = executeId,
                    testCaseRequestName = bundle.request.name,
                    httpRequest = objectMapper.writeValueAsString(httpRequest),
                    httpResponse = objectMapper.writeValueAsString(httpResponse),
                    execCheckInfo = null,
                    execCheckResult = TestCaseExecuteHistory.EXEC_CHECK_RESULT_WRONG,
                    saveEnvVariableInfo = null
                )
            }
        }
    }

    private fun executePipeline(
        executeId: String,
        testCaseId: Long,
        pipeline: BoomVO.Pipeline
    ): List<TestCaseExecuteHistory> {
        TODO()
    }

    // http.url http.header http.param
    //
    // PositionParam : $[0]
    // EnvParam : ${姓名}
    //
    private fun replacePositionAndEnvParams(
        fetchApi: BoomVO.FetchApi,
        testCaseRequestParams: List<String>,
        envs: HashMap<String, String>
    ) {
        // http.url
        fetchApi.url = replacePositionParams(fetchApi.url, testCaseRequestParams)
        fetchApi.url = replaceEnvParams(fetchApi.url, envs)

        // http.header
        fetchApi.header?.let {
            fetchApi.header = replacePositionParams(it, testCaseRequestParams)
            fetchApi.header = replaceEnvParams(it, envs)
        }

        // http.param
        fetchApi.param?.let {
            fetchApi.param = replacePositionParams(it, testCaseRequestParams)
            fetchApi.param = replaceEnvParams(it, envs)
        }
    }

    // 替换位置参数
    private fun replacePositionParams(source: String, testCaseRequestParams: List<String>): String {
        var result = source

        testCaseRequestParams.forEachIndexed { index, param ->
            result = result.replace("\$[${index}]", param) // PositionParam : $[0]
        }

        return result
    }

    // 替换环境变量参数
    private fun replaceEnvParams(source: String, envs: HashMap<String, String>): String {
        var result = source

        envs.forEach { env ->
            result = result.replace("\${${env.key}}", env.value) // EnvParam : ${姓名}
        }

        return result
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
