package com.github.xiaosongfu.atp.domain.dto.project

import com.github.xiaosongfu.atp.domain.vo.project.ProjectRequestResponseVO
import com.github.xiaosongfu.atp.domain.vo.project.ProjectRequestVO
import io.swagger.v3.oas.annotations.media.Schema

data class ProjectRequestFindResponse(
    @Schema(description = "请求 ID")
    var id: Long,

    @Schema(description = "请求信息")
    var request: ProjectRequestVO,

    @Schema(description = "响应字段验证")
    var responseFieldValidate: List<ProjectRequestResponseVO>?,

    @Schema(description = "参数")
    var arguments: List<String>?
)
