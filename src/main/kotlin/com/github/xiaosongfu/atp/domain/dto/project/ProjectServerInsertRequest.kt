package com.github.xiaosongfu.atp.domain.dto.project

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotEmpty

data class ProjectServerInsertRequest(
    @Schema(description = "环境名称")
    @NotEmpty(message = "环境名称不能为空")
    var envName: String,

    @Schema(description = "base url,结尾不能有/")
    @NotEmpty(message = "base ur 不能为空")
    var baseUrl: String
)
