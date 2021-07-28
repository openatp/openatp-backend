package com.github.xiaosongfu.atp.domain.vo.testcase

import io.swagger.v3.oas.annotations.media.Schema

data class TestCaseRequestVO(
    @Schema(description = "测试案例请求名称")
    var name: String,

    @Schema(description = "项目请求 ID")
    var projectRequestId: Long,

    @Schema(description = "请求参数")
    var param: String? = null
)
