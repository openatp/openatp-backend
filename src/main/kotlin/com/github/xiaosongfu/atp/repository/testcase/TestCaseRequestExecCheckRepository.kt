package com.github.xiaosongfu.atp.repository.testcase

import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequestExecCheck
import org.springframework.data.repository.PagingAndSortingRepository

interface TestCaseRequestExecCheckRepository : PagingAndSortingRepository<TestCaseRequestExecCheck, Long> {
    fun findAllByTestCaseRequestId(testCaseRequestId: Long): List<TestCaseRequestExecCheck>?
    fun deleteAllByTestCaseRequestId(testCaseRequestId: Long)
}
