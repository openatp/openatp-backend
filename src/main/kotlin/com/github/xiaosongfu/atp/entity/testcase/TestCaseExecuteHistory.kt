package com.github.xiaosongfu.atp.entity.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * 测试案例执行记录
 */
@Entity
@Table(name = "test_case_execute_history")
data class TestCaseExecuteHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Schema(description = "测试案例 ID")
    @Column(name = "test_case_id")
    var testCaseId: Long,

    @Schema(description = "执行 ID")
    @Column(name = "execute_id")
    var executeId: String,

    @Schema(description = "测试案例 ID")
    @Column(name = "test_case_request_name")
    var testCaseRequestName: String,

    @Schema(description = "测试案例 ID")
    @Column(name = "http_request")
    var httpRequest: String,

    @Schema(description = "测试案例 ID")
    @Column(name = "http_response")
    var httpResponse: String,

    @Schema(description = "测试案例 ID")
    @Column(name = "exec_check_info")
    var execCheckInfo: String?,

    @Schema(description = "测试案例 ID")
    @Column(name = "exec_check_result")
    var execCheckResult: Int,

    @Schema(description = "测试案例 ID")
    @Column(name = "save_env_variable_info")
    var saveEnvVariableInfo: String?
) {
    companion object {
        const val EXEC_CHECK_RESULT_WRONG = 0
        const val EXEC_CHECK_RESULT_CORRECT = 1
    }
}
