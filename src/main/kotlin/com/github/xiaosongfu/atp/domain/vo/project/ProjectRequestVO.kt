package com.github.xiaosongfu.atp.domain.vo.project

import io.swagger.v3.oas.annotations.media.Schema

data class ProjectRequestVO(
    @Schema(description = "请求名称")
    var name: String,

    @Schema(description = "请求路径,如 /v1/welcome,必须以/开头")
    var path: String,

    @Schema(description = "请求路径,如 /v1/welcome,必须以/开头")
    var method: String,

    @Schema(description = "content type [form json]")
    var contentType: String? = null,

    @Schema(description = "请求参数")
    var param: String? = null,

    @Schema(description = "请求头")
    var header: String? = null,

    @Schema(description = "请求超时")
    var timeout: Long
)
