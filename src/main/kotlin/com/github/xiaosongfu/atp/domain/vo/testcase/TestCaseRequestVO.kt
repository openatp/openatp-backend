package com.github.xiaosongfu.atp.domain.vo.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Positive

data class TestCaseRequestVO(
    @Schema(description = "测试案例请求名称")
    @NotEmpty(message = "试案例请求名称不能为空")
    var name: String,

    @Schema(description = "项目请求 ID")
    @Positive(message = "必须为数字")
    var projectRequestId: Long,

    @Schema(description = "请求参数")
    var param: String? = null
)
