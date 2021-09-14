package com.github.xiaosongfu.atp.entity.testcase

import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteDetail.Companion.EXEC_CHECK_REQUEST_ERROR
import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteDetail.Companion.EXEC_CHECK_RESULT_CORRECT
import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteDetail.Companion.EXEC_CHECK_RESULT_WRONG
import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * 测试案例执行详情
 */
@Entity
@Table(name = "test_case_execute_detail")
data class TestCaseExecuteDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Schema(description = "执行 ID")
    @Column(name = "execute_id")
    var executeHistoryId: String,

    @Schema(description = "测试案例请求名称")
    @Column(name = "test_case_request_name")
    var testCaseRequestName: String,

    @Schema(description = "HTTP 请求")
    @Column(name = "http_request", columnDefinition = "text NOT NULL")
    var httpRequest: String,

    @Schema(description = "HTTP 响应")
    @Column(name = "http_response", columnDefinition = "text NOT NULL")
    var httpResponse: String,

    @Schema(description = "请求验证信息")
    @Column(name = "exec_check_info", columnDefinition = "text NULL")
    var execCheckInfo: String?,

    @Schema(description = "请求验证结果")
    @Column(name = "exec_check_result")
    var execCheckResult: Int,

    @Schema(description = "环境变量信息")
    @Column(name = "save_env_variable_info", columnDefinition = "text NULL")
    var saveEnvVariableInfo: String?
) {
    companion object {
        const val EXEC_CHECK_REQUEST_ERROR = 0  // HTTP请求错误
        const val EXEC_CHECK_RESULT_WRONG = 1   // 验证失败
        const val EXEC_CHECK_RESULT_CORRECT = 2 // 验证成功
    }
}

fun Int.execCheckResultName(): String {
    return when (this) {
        EXEC_CHECK_REQUEST_ERROR -> "HTTP请求错误"
        EXEC_CHECK_RESULT_WRONG -> "验证失败"
        EXEC_CHECK_RESULT_CORRECT -> "验证成功"
        else -> "未知"
    }
}
