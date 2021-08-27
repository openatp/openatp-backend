package com.github.xiaosongfu.atp.entity.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * 测试案例配置
 */
@Entity
@Table(name = "test_case")
data class TestCase(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Schema(description = "项目 ID")
    @Column(name = "project_id")
    var projectId: Long,

    @Schema(description = "测试案例名称")
    @Column(name = "name")
    var name: String,

    @Schema(description = "测试案例类型 [pipeline replay benchmark]")
    @Column(name = "type")
    var type: String,

    @Schema(description = "benchmark和replay类型的测试案例关联的唯一的项目请求 ID")
    @Column(name = "project_request_id")
    var projectRequestId: Long?
) {
    companion object {
        const val TEST_CASE_TYPE_BENCHMARK = "benchmark" // 性能：使用相同请求参数和相同响应验证的 一个请求 调用一个接口
        const val TEST_CASE_TYPE_REPLAY = "replay"       // 叠放：使用不同请求参数和不同响应验证的 一个请求 调用一个接口
        const val TEST_CASE_TYPE_PIPELINE = "pipeline"   // 顺序：使用不同请求参数和不同响应验证的 多个请求 调用多个接口
    }
}
