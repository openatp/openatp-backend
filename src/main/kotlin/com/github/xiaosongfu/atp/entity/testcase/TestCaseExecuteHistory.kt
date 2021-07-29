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

    @Schema(description = "总的请求数量")
    @Column(name = "requestT_total_count")
    var requestTotalCount: Long,

    @Schema(description = "验证正确的请求数量")
    @Column(name = "request_check_correct_count")
    var requestCheckCorrectCount: Long,

    @Schema(description = "验证正确率")
    @Column(name = "request_check_correct_rate")
    var requestCheckCorrectRate: Double
)
