package com.github.xiaosongfu.atp.domain.vo.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Positive

data class TestCaseRequestExecCheckVO(
    @Schema(description = "需要校验的响应 ID")
    @Positive(message = "必须为数字")
    var projectRequestResponseId: Long,

    @Schema(description = "期望的响应字段值")
    @NotEmpty(message = "期望的响应字段值不能为空")
    var wantResponseFieldValue: String
)
