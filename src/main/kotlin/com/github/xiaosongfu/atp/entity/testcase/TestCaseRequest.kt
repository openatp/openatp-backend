package com.github.xiaosongfu.atp.entity.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * 测试案例请求配置
 */
@Entity
@Table(name = "test_case_request")
data class TestCaseRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Schema(description = "测试案例 ID")
    @Column(name = "test_case_id")
    var testCaseId: Long,

    @Schema(description = "测试案例请求名称")
    @Column(name = "name")
    var name: String,

    @Schema(description = "项目请求 ID")
    @Column(name = "project_request_id")
    var projectRequestId: Long,

    @Schema(description = "请求参数")
    @Column(name = "param")
    var param: String? = null
)
