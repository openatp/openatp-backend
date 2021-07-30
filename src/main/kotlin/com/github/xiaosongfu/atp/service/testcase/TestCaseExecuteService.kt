package com.github.xiaosongfu.atp.service.testcase

import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteDetail
import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteHistory
import com.github.xiaosongfu.atp.repository.testcase.TestCaseExecuteDetailRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseExecuteHistoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    // 保存 测试案例执行记录 -> 开始执行状态
    fun insertHistoryForStartRunning(executeHistoryId: String, testCaseId: Long) {
        testCaseExecuteHistoryRepository.save(
            TestCaseExecuteHistory(
                id = executeHistoryId,
                testCaseId = testCaseId,
                executeDatetime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                executeStatus = TestCaseExecuteHistory.EXECUTE_STATUS_RUNNING,
                requestTotalCount = 0,
                requestCheckCorrectCount = 0,
                requestCheckCorrectRate = 0.0
            )
        )
    }

    // 更新 测试案例执行记录 -> 执行成功状态
    fun updateHistoryForExecuteSuccess(executeHistoryId: String, requestTotalCount: Int, requestCheckCorrectCount: Int) {
        testCaseExecuteHistoryRepository.findByIdOrNull(executeHistoryId)?.let {
            // 计算正确率
            val requestCheckCorrectRate = requestCheckCorrectCount.toDouble() / requestTotalCount.toDouble()
            // 更新
            testCaseExecuteHistoryRepository.save(
                it.copy(
                    executeStatus = TestCaseExecuteHistory.EXECUTE_STATUS_SUCCESS,
                    requestTotalCount = requestTotalCount,
                    requestCheckCorrectCount = requestCheckCorrectCount,
                    requestCheckCorrectRate = requestCheckCorrectRate
                )
            )
        }
    }

    // 更新 测试案例执行记录 -> 执行失败状态
    fun updateHistoryForExecuteFailed(executeHistoryId: String, executeFailedReason: String) {
        testCaseExecuteHistoryRepository.findByIdOrNull(executeHistoryId)?.let {
            testCaseExecuteHistoryRepository.save(
                it.copy(
                    executeStatus = TestCaseExecuteHistory.EXECUTE_STATUS_FAILED,
                    executeStatusDetail = executeFailedReason
                )
            )
        }
    }

    // 保存 测试案例执行记录详情
    fun insertDetails(details: List<TestCaseExecuteDetail>) {
        testCaseExecuteDetailRepository.saveAll(details)
    }
}
