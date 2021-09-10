package com.github.xiaosongfu.atp.entity.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * 测试案例请求验证配置
 */
@Entity
@Table(name = "test_case_request_exec_check")
data class TestCaseRequestExecCheck(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Schema(description = "测试案例请求 ID")
    @Column(name = "test_case_request_id")
    var testCaseRequestId: Long,

    @Schema(description = "字段名称")
    @Column(name = "field_name")
    var fieldName: String,

    @Schema(description = "字段路径[jsonpath xpath]")
    @Column(name = "field_path")
    var fieldPath: String,

    @Schema(description = "期望的响应字段值")
    @Column(name = "want_response_field_value")
    var wantResponseFieldValue: String,
)
