package com.github.xiaosongfu.atp.service.testcase

import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteDetail
import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteHistory
import com.github.xiaosongfu.atp.repository.testcase.TestCaseExecuteDetailRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseExecuteHistoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TestCaseExecuteService {

    @Autowired
    private lateinit var testCaseExecuteHistoryRepository: TestCaseExecuteHistoryRepository

    @Autowired
    private lateinit var testCaseExecuteDetailRepository: TestCaseExecuteDetailRepository

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    fun list(testCaseId: Long): List<TestCaseExecuteHistory>? {
        return testCaseExecuteHistoryRepository.findAllByTestCaseId(testCaseId)
    }

    fun detail(executeHistoryId: String): List<TestCaseExecuteDetail>? {
        return testCaseExecuteDetailRepository.findAllByExecuteHistoryId(executeHistoryId)
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    fun insertHistory(history: TestCaseExecuteHistory) {
        testCaseExecuteHistoryRepository.save(history)
    }

    fun insertDetails(details: List<TestCaseExecuteDetail>) {
        testCaseExecuteDetailRepository.saveAll(details)
    }
}
