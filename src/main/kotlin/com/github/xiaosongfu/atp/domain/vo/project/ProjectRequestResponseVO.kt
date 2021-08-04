package com.github.xiaosongfu.atp.domain.vo.project

import io.swagger.v3.oas.annotations.media.Schema

data class ProjectRequestResponseVO(
    @Schema(description = "id")
    var id: Long,

    @Schema(description = "字段名称")
    var fieldName: String,

    @Schema(description = "字段路径[jsonpath xpath]")
    var fieldPath: String
)
