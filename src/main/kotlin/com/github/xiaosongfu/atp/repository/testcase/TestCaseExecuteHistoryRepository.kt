package com.github.xiaosongfu.atp.repository.testcase

import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteHistory
import org.springframework.data.repository.PagingAndSortingRepository

interface TestCaseExecuteHistoryRepository : PagingAndSortingRepository<TestCaseExecuteHistory, String> {
    fun findAllByTestCaseId(testCaseId: Long): List<TestCaseExecuteHistory>?
}
