package com.github.xiaosongfu.atp.service.testcase

import com.github.xiaosongfu.atp.domain.dto.testcase.TestCaseFindResponse
import com.github.xiaosongfu.atp.domain.dto.testcase.TestCaseRequestInsertRequest
import com.github.xiaosongfu.atp.domain.vo.testcase.TestCaseRequestExecCheckVO
import com.github.xiaosongfu.atp.domain.vo.testcase.TestCaseRequestSaveEnvVariableVO
import com.github.xiaosongfu.atp.domain.vo.testcase.TestCaseRequestVO
import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequest
import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequestExecCheck
import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequestSaveEnvVariable
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestExecCheckRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestSaveEnvVariableRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TestCaseRequestService {
    @Autowired
    private lateinit var testCaseRequestRepository: TestCaseRequestRepository

    @Autowired
    private lateinit var testCaseRequestExecCheckRepository: TestCaseRequestExecCheckRepository

    @Autowired
    private lateinit var testCaseRequestSaveEnvVariableRepository: TestCaseRequestSaveEnvVariableRepository

    @Transactional
    fun insert(testCaseId: Long, req: TestCaseRequestInsertRequest) {
        val request = testCaseRequestRepository.save(
            TestCaseRequest(
                testCaseId = testCaseId,
                name = req.request.name,
                projectRequestId = req.request.projectRequestId,
                arguments = req.request.arguments
            )
        )

        req.requestExecCheck?.forEach { check ->
            testCaseRequestExecCheckRepository.save(
                TestCaseRequestExecCheck(
                    testCaseRequestId = request.id,
                    fieldName = check.fieldName,
                    fieldPath = check.fieldPath,
                    wantResponseFieldValue = check.wantResponseFieldValue
                )
            )
        }

        req.requestSaveEnvVariable?.forEach { env ->
            testCaseRequestSaveEnvVariableRepository.save(
                TestCaseRequestSaveEnvVariable(
                    testCaseRequestId = request.id,
                    projectEnvVariableId = env.projectEnvVariableId,
                    projectEnvVariableValuePath = env.projectEnvVariableValuePath
                )
            )
        }
    }

    @Transactional
    fun delete(testCaseRequestId: Long) {
        testCaseRequestRepository.deleteById(testCaseRequestId)
        testCaseRequestExecCheckRepository.deleteAllByTestCaseRequestId(testCaseRequestId)
        testCaseRequestSaveEnvVariableRepository.deleteAllByTestCaseRequestId(testCaseRequestId)
    }

    @Transactional
    fun update(testCaseRequestId: Long, req: TestCaseRequestInsertRequest) {
        testCaseRequestRepository.findByIdOrNull(testCaseRequestId)?.let {
            delete(testCaseRequestId)
            insert(it.testCaseId, req)
        }
    }

    fun detail(testCaseRequestId: Long): TestCaseFindResponse? {
        return testCaseRequestRepository.findByIdOrNull(testCaseRequestId)?.let { req ->
            val requestExecCheck = testCaseRequestExecCheckRepository.findAllByTestCaseRequestId(req.id)?.map { check ->
                TestCaseRequestExecCheckVO(
                    fieldName = check.fieldName,
                    fieldPath = check.fieldPath,
                    wantResponseFieldValue = check.wantResponseFieldValue
                )
            }

            val requestSaveEnvVariable =
                testCaseRequestSaveEnvVariableRepository.findAllByTestCaseRequestId(req.id)?.map { env ->
                    TestCaseRequestSaveEnvVariableVO(
                        projectEnvVariableId = env.projectEnvVariableId,
                        projectEnvVariableValuePath = env.projectEnvVariableValuePath
                    )
                }

            TestCaseFindResponse(
                id = req.id,
                request = TestCaseRequestVO(
                    name = req.name,
                    projectRequestId = req.projectRequestId,
                    arguments = req.arguments
                ),
                requestExecCheck = requestExecCheck,
                requestSaveEnvVariable = requestSaveEnvVariable
            )
        }
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

//    fun list(testCaseId: Long): List<TestCaseRequest>? {
//        return testCaseRequestRepository.findAllByTestCaseId(testCaseId)
//    }

    fun listWithDetail(testCaseId: Long): List<TestCaseFindResponse>? {
        return testCaseRequestRepository.findAllByTestCaseId(testCaseId)?.map { req ->
            val requestExecCheck = testCaseRequestExecCheckRepository.findAllByTestCaseRequestId(req.id)?.map { check ->
                TestCaseRequestExecCheckVO(
                    fieldName = check.fieldName,
                    fieldPath = check.fieldPath,
                    wantResponseFieldValue = check.wantResponseFieldValue
                )
            }

            val requestSaveEnvVariable =
                testCaseRequestSaveEnvVariableRepository.findAllByTestCaseRequestId(req.id)?.map { env ->
                    TestCaseRequestSaveEnvVariableVO(
                        projectEnvVariableId = env.projectEnvVariableId,
                        projectEnvVariableValuePath = env.projectEnvVariableValuePath
                    )
                }

            TestCaseFindResponse(
                id = req.id,
                request = TestCaseRequestVO(
                    name = req.name,
                    projectRequestId = req.projectRequestId,
                    arguments = req.arguments
                ),
                requestExecCheck = requestExecCheck,
                requestSaveEnvVariable = requestSaveEnvVariable
            )
        }
    }
}
