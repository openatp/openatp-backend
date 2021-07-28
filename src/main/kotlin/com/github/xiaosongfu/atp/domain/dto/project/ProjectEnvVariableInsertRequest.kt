package com.github.xiaosongfu.atp.domain.dto.project

import io.swagger.v3.oas.annotations.media.Schema

data class ProjectEnvVariableInsertRequest(
    @Schema(description = "环境变量名称")
    var variableName: String,

    @Schema(description = "环境变量默认值,可以为空")
    var defaultValue: String? = null
)
