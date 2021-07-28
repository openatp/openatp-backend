package com.github.xiaosongfu.atp.repository.testcase

import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequestSaveEnvVariable
import org.springframework.data.repository.PagingAndSortingRepository

interface TestCaseRequestSaveEnvVariableRepository : PagingAndSortingRepository<TestCaseRequestSaveEnvVariable, Long> {
    fun findAllByTestCaseRequestId(testCaseRequestId: Long): List<TestCaseRequestSaveEnvVariable>?
    fun deleteAllByTestCaseRequestId(testCaseRequestId: Long)
}
