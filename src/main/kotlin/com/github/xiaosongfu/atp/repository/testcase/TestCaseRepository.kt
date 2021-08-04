package com.github.xiaosongfu.atp.repository.testcase

import com.github.xiaosongfu.atp.entity.testcase.TestCase
import org.springframework.data.repository.PagingAndSortingRepository

interface TestCaseRepository : PagingAndSortingRepository<TestCase, Long> {
    fun findAllByProjectId(projectId: Long): List<TestCase>?
}
