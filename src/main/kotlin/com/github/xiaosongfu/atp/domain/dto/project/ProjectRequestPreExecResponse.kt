package com.github.xiaosongfu.atp.domain.dto.project

import com.github.xiaosongfu.atp.service.testcase.boom.box.HttpRequest
import com.github.xiaosongfu.atp.service.testcase.boom.box.HttpResponse
import io.swagger.v3.oas.annotations.media.Schema

data class ProjectRequestPreExecResponse(
    @Schema(description = "http 请求")
    var request: HttpRequest,

    @Schema(description = "http 响应")
    var response: HttpResponse
)
