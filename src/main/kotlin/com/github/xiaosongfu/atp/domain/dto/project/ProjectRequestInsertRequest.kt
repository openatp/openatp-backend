package com.github.xiaosongfu.atp.domain.dto.project

import com.github.xiaosongfu.atp.domain.vo.project.ProjectRequestResponseVO
import com.github.xiaosongfu.atp.domain.vo.project.ProjectRequestVO
import io.swagger.v3.oas.annotations.media.Schema

data class ProjectRequestInsertRequest(
    @Schema(description = "请求")
    var request: ProjectRequestVO,

    @Schema(description = "响应字段验证")
    var responseFieldValidate: List<ProjectRequestResponseVO>?
)
