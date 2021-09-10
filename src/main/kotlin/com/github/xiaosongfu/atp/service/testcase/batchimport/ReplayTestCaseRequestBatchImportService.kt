package com.github.xiaosongfu.atp.service.testcase.batchimport

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequest
import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequestExecCheck
import com.github.xiaosongfu.atp.repository.project.ProjectRequestArgumentRepository
import com.github.xiaosongfu.atp.repository.project.ProjectRequestResponseRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestExecCheckRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestRepository
import com.github.xiaosongfu.jakarta.exception.service.ServiceException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ReplayTestCaseRequestBatchImportService {

    @Autowired
    private lateinit var projectRequestResponseRepository: ProjectRequestResponseRepository

    @Autowired
    private lateinit var projectRequestArgumentRepository: ProjectRequestArgumentRepository

    @Autowired
    private lateinit var testCaseRepository: TestCaseRepository

    @Autowired
    private lateinit var testCaseRequestRepository: TestCaseRequestRepository

    @Autowired
    private lateinit var testCaseRequestExecCheckRepository: TestCaseRequestExecCheckRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val log = LoggerFactory.getLogger(javaClass)

    fun downloadTemplateExcel(testCaseId: Long): List<String> {
        // sheet 的 title
        val title = mutableListOf<String>()

        // arg
        testCaseRepository.findByIdOrNull(testCaseId)?.projectRequestId?.let { projectRequestId ->
            // 读取请求的参数
            projectRequestArgumentRepository.findAllByRequestId(projectRequestId)?.map { arg ->
                title.add("${ArgTitle}${arg.argumentName}")
            }
        }
        // resp
        testCaseRepository.findByIdOrNull(testCaseId)?.projectRequestId?.let { projectRequestId ->
            // 读取请求的响应验证
            projectRequestResponseRepository.findAllByRequestId(projectRequestId)?.map { check ->
                title.add("${RespTitle}${check.fieldName}")
            }
        }

        return title
    }

    fun batchImport(testCaseId: Long, excelData: List<Map<Int, String>>) {
        testCaseRepository.findByIdOrNull(testCaseId)?.projectRequestId?.let { projectRequestId ->
            // 读取请求的参数和响应验证
            val projectRequestArgumentList = projectRequestArgumentRepository.findAllByRequestId(projectRequestId)
                ?: throw ServiceException(msg = "")
            val projectRequestResponseList = projectRequestResponseRepository.findAllByRequestId(projectRequestId)
                ?: throw ServiceException(msg = "")
            // 请求的参数和响应验证的数量
            val argSize = projectRequestArgumentList.size
            val respSize = projectRequestResponseList.size
            // 每一列的参数和响应验证
            val arguments = hashMapOf<String, String>()
            val execCheck = hashMapOf<Long, String>()

            // 遍历每一行
            excelData.forEachIndexed { rowIndex, row ->
                log.debug("开始导入第 $rowIndex 行 : $row")

                // 遍历每一列
                // excel 的一行是一条 '测试案例请求' 数据
                row.forEach { (index, value) ->
                    log.debug("开始处理第 $index 列 : $value")

                    if (index < argSize) {
                        arguments[projectRequestArgumentList[index].argumentName] = value
                    } else if (index in argSize until argSize + respSize) {
                        execCheck[projectRequestResponseList[index - argSize].id] = value
                    } else {
                        log.warn("第 $index 列数据 [$value] 多余,不予处理,直接丢弃")
                    }
                }

                // 每一列处理完成后保存到数据库
                //
                // 保存请求和参数
                val res = testCaseRequestRepository.save(
                    TestCaseRequest(
                        testCaseId = testCaseId,
                        name = rowIndex.toString(),
                        projectRequestId = projectRequestId,
                        arguments = objectMapper.writeValueAsString(arguments)
                    )
                )
                // 保存响应验证
                execCheck.forEach {
                    testCaseRequestExecCheckRepository.save(
                        TestCaseRequestExecCheck(
                            testCaseRequestId = res.id,
                            projectRequestResponseId = it.key,
                            wantResponseFieldValue = it.value
                        )
                    )
                }

                // 清空
                arguments.clear()
                execCheck.clear()
            }
        }
    }

    companion object {
        const val ArgTitle = "请求参数-"
        const val RespTitle = "响应验证-"
    }
}
