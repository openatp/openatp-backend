package com.github.xiaosongfu.atp.repository.testcase

import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteDetail
import org.springframework.data.repository.PagingAndSortingRepository

interface TestCaseExecuteDetailRepository : PagingAndSortingRepository<TestCaseExecuteDetail, Long> {
    fun findAllByExecuteHistoryId(executeHistoryId: String): List<TestCaseExecuteDetail>?
}
