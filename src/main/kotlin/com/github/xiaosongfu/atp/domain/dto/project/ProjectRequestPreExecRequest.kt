package com.github.xiaosongfu.atp.domain.dto.project

import com.github.xiaosongfu.atp.domain.vo.project.ProjectRequestVO
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

data class ProjectRequestPreExecRequest(
    @Schema(description = "请求")
    @NotNull(message = "请求不能为空")
    var request: ProjectRequestVO,

    @Schema(description = "参数,由Map转成的json字符串")
    var arguments: String?,

    @Schema(description = "环境变量,由Map转成的json字符串")
    var env: String?
)
