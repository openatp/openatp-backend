package com.github.xiaosongfu.atp.domain.dto.project

import io.swagger.v3.oas.annotations.media.Schema

data class ProjectInsertRequest(
    @Schema(description = "名称")
    var name: String,

    @Schema(description = "描述")
    var desc: String? = null
)
