package com.github.xiaosongfu.atp.domain.vo.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Positive

data class TestCaseRequestSaveEnvVariableVO(
    @Schema(description = "环境变量 ID")
    @Positive(message ="必须为数字")
    var projectEnvVariableId: Long,

    @Schema(description = "环境变量路径[jsonpath xpath]")
    @NotEmpty(message = "环境变量路径不能为空")
    var projectEnvVariableValuePath: String
)
