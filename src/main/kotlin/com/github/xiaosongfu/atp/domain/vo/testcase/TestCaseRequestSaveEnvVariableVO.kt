package com.github.xiaosongfu.atp.domain.vo.testcase

import io.swagger.v3.oas.annotations.media.Schema

data class TestCaseRequestSaveEnvVariableVO(
    @Schema(description = "环境变量 ID")
    var projectEnvVariableId: Long,

    @Schema(description = "环境变量路径[jsonpath xpath]")
    var projectEnvVariableValuePath: String
)
