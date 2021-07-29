package com.github.xiaosongfu.atp.service.testcase.batchimport

import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequestExecCheck
import com.github.xiaosongfu.atp.repository.project.ProjectRequestResponseRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestExecCheckRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReplayTestCaseRequestBatchImportService {
    @Autowired
    private lateinit var testCaseRequestRepository: TestCaseRequestRepository

    @Autowired
    private lateinit var testCaseRequestExecCheckRepository: TestCaseRequestExecCheckRepository

    @Autowired
    private lateinit var projectRequestResponseRepository: ProjectRequestResponseRepository

    fun downloadTemplateExcel(testCaseId: Long): List<String>? {
        return testCaseRequestRepository.findAllByTestCaseId(testCaseId)?.firstOrNull()?.let { testCaseRequest ->
            // 读取请求的响应验证
            projectRequestResponseRepository.findAllByRequestId(testCaseRequest.projectRequestId)?.map {
                it.fieldName
            }
        }
    }

    fun batchImport(testCaseId: Long, excelData: List<Map<Int, String>>) {
        testCaseRequestRepository.findAllByTestCaseId(testCaseId)?.firstOrNull()?.let { testCaseRequest ->
            // 读取请求的响应验证
            val projectRequestResponseList =
                projectRequestResponseRepository.findAllByRequestId(testCaseRequest.projectRequestId)

            // 遍历并保存
            excelData.forEach { row ->
                row.forEach { (index, value) ->
                    if (index == 0) {
                        if (value.isNotEmpty()) { // 要不为空才保存
                            val newTestCaseRequest = testCaseRequest.copy(param = value)
                            testCaseRequestRepository.save(newTestCaseRequest)
                        }
                    } else {
                        if (index <= (projectRequestResponseList?.size ?: 0)) {
                            projectRequestResponseList?.get(index - 1)?.let { projectRequestResponse ->
                                if (value.isNotEmpty()) { // 要不为空才保存
                                    testCaseRequestExecCheckRepository.save(
                                        TestCaseRequestExecCheck(
                                            testCaseRequestId = testCaseRequest.id,
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
