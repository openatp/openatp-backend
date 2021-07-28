package com.github.xiaosongfu.atp.entity.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

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
    var type: String
) {
    companion object {
        const val TEST_CASE_TYPE_BENCHMARK = "benchmark" // 性能：使用相同的请求参数和相同的响应验证调用一个接口
        const val TEST_CASE_TYPE_REPLAY = "replay"       // 叠放：使用不同的请求参数和不同的响应验证调用一个接口
        const val TEST_CASE_TYPE_PIPELINE = "pipeline"   // 顺序：使用不同的请求参数和不同的响应验证调用多个接口
    }
}
