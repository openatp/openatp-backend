package com.github.xiaosongfu.atp.domain.dto.project

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotEmpty

data class ProjectEnvVariableInsertRequest(
    @Schema(description = "环境变量名称")
    @NotEmpty(message = "环境变量名称不能为空")
    var variableName: String,

    @Schema(description = "环境变量默认值,可以为空")
    var defaultValue: String? = null
)
