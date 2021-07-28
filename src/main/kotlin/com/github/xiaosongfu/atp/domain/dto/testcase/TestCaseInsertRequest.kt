package com.github.xiaosongfu.atp.domain.dto.testcase

import io.swagger.v3.oas.annotations.media.Schema

data class TestCaseInsertRequest(
    @Schema(description = "测试案例名称")
    var name: String,

    @Schema(description = "类型 [pipeline replay benchmark]")
    var type: String
)