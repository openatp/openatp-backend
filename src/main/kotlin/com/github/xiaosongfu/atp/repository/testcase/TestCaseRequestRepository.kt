package com.github.xiaosongfu.atp.repository.testcase

import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequest
import org.springframework.data.repository.PagingAndSortingRepository

interface TestCaseRequestRepository : PagingAndSortingRepository<TestCaseRequest, Long> {

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
    fun findAllByTestCaseId(testCaseId: Long): List<TestCaseRequest>?
}
