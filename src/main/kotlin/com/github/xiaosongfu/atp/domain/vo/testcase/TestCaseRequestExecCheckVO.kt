package com.github.xiaosongfu.atp.domain.vo.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotEmpty

data class TestCaseRequestExecCheckVO(
    @Schema(description = "字段名称")
    var fieldName: String,

    @Schema(description = "字段路径[jsonpath xpath]")
    var fieldPath: String,

    @Schema(description = "期望的响应字段值")
    @NotEmpty(message = "期望的响应字段值不能为空")
    var wantResponseFieldValue: String
)
