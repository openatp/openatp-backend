package com.github.xiaosongfu.atp.domain.dto.project

import io.swagger.v3.oas.annotations.media.Schema

data class ProjectServerInsertRequest(
    @Schema(description = "环境名称")
    var envName: String,

    @Schema(description = "base url,结尾不能有/")
    var baseUrl: String
)
