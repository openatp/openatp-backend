package com.github.xiaosongfu.atp.domain.dto.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotEmpty

data class TestCaseInsertRequest(
    @Schema(description = "测试案例名称")
    @NotEmpty(message = "测试案例名称不能为空")
    var name: String,

    @Schema(description = "类型 [pipeline replay benchmark]")
    @NotEmpty(message = "类型不能为空")
    var type: String
)