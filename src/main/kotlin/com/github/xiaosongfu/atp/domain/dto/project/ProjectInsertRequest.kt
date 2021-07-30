package com.github.xiaosongfu.atp.domain.dto.project

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotEmpty

data class ProjectInsertRequest(
    @Schema(description = "名称")
    @NotEmpty(message = "名称不能为空")
    var name: String,

    @Schema(description = "描述")
    var desc: String? = null
)
