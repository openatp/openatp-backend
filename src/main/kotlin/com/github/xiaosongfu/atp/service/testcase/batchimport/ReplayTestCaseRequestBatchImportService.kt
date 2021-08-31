package com.github.xiaosongfu.atp.service.testcase.batchimport

import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequest
import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequestExecCheck
import com.github.xiaosongfu.atp.repository.project.ProjectRequestResponseRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestExecCheckRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ReplayTestCaseRequestBatchImportService {
    @Autowired
    private lateinit var testCaseRepository: TestCaseRepository

    @Autowired
    private lateinit var testCaseRequestRepository: TestCaseRequestRepository

    @Autowired
    private lateinit var testCaseRequestExecCheckRepository: TestCaseRequestExecCheckRepository

    @Autowired
    private lateinit var projectRequestResponseRepository: ProjectRequestResponseRepository

    private val log = LoggerFactory.getLogger(javaClass)

    fun downloadTemplateExcel(testCaseId: Long): List<String>? {
        return testCaseRepository.findByIdOrNull(testCaseId)?.projectRequestId?.let { projectRequestId ->
            // 读取请求的响应验证
            projectRequestResponseRepository.findAllByRequestId(projectRequestId)?.map {
                it.fieldName
            }
        }
    }

    fun batchImport(testCaseId: Long, excelData: List<Map<Int, String>>) {
        testCaseRepository.findByIdOrNull(testCaseId)?.projectRequestId?.let { projectRequestId ->
            // 读取请求的响应验证
            val projectRequestResponseList = projectRequestResponseRepository.findAllByRequestId(projectRequestId)

            // 遍历并保存
            excelData.forEachIndexed { rowIndex, row ->
                log.debug("开始导入第 $rowIndex 行 : $row")

                // excel 一行是一条 '测试案例请求'，需要记住该ID用于插入 '测试案例请求验证配置'
                var testCaseRequestIdForThisRow: Long = -1
                // 遍历每一列
                row.forEach { (index, value) ->
                    log.debug("开始导入第 $index 列 : $value")
                    if (index == 0) {
                        if (value.isNotEmpty()) { // 要不为空才保存
                            val res = testCaseRequestRepository.save(
                                TestCaseRequest(
                                    testCaseId = testCaseId,
                                    name = rowIndex.toString(),
                                    projectRequestId = projectRequestId,
                                    param = value
                                )
                            )
                            testCaseRequestIdForThisRow = res.id
                        }
                    } else {
                        if (index <= (projectRequestResponseList?.size ?: 0)) {
                            projectRequestResponseList?.get(index - 1)?.let { projectRequestResponse ->
                                if (value.isNotEmpty()) { // 要不为空才保存
                                    testCaseRequestExecCheckRepository.save(
                                        TestCaseRequestExecCheck(
                                            testCaseRequestId = testCaseRequestIdForThisRow,
                                            projectRequestResponseId = projectRequestResponse.id,
                                            wantResponseFieldValue = value
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
