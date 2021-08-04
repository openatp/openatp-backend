package com.github.xiaosongfu.atp.entity.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * 测试案例执行记录
 */
@Entity
@Table(name = "test_case_execute_history")
data class TestCaseExecuteHistory(
    @Id
    @Column(name = "id")
    var id: String,

    @Schema(description = "测试案例 ID")
    @Column(name = "test_case_id")
    var testCaseId: Long,

    @Schema(description = "执行时间")
    @Column(name = "execute_datetime")
    var executeDatetime: String,

    @Schema(description = "项目服务器名称")
    @Column(name = "project_server_name")
    var projectServerName: String,

    @Schema(description = "执行状态")
    @Column(name = "execute_status")
    var executeStatus: Int,

    @Schema(description = "执行状态详情")
    @Column(name = "execute_status_detail")
    var executeStatusDetail: String? = null,

    @Schema(description = "总的请求数量")
    @Column(name = "requestT_total_count")
    var requestTotalCount: Int,

    @Schema(description = "请求成功的请求数量")
    @Column(name = "request_success_count")
    var requestSuccessCount: Int,

    @Schema(description = "请求成功率")
    @Column(name = "request_success_rate")
    var requestSuccessRate: Double,

    @Schema(description = "请求验证正确的请求数量")
    @Column(name = "request_check_correct_count")
    var requestCheckCorrectCount: Int,

    @Schema(description = "请求验证正确率")
    @Column(name = "request_check_correct_rate")
    var requestCheckCorrectRate: Double
) {
    companion object {
        const val EXECUTE_STATUS_RUNNING = 0
        const val EXECUTE_STATUS_ERROR = 1
        const val EXECUTE_STATUS_FAILED = 2
        const val EXECUTE_STATUS_SUCCESS = 3
    }
}
