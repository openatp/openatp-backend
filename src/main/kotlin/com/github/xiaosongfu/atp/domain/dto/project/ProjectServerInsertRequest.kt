package com.github.xiaosongfu.atp.domain.dto.project

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotEmpty

data class ProjectServerInsertRequest(
    @Schema(description = "服务器名称")
    @NotEmpty(message = "服务器名称不能为空")
    var serverName: String,

    @Schema(description = "服务器地址,结尾不能有/")
    @NotEmpty(message = "服务器地址不能为空")
    var serverAddr: String
)
