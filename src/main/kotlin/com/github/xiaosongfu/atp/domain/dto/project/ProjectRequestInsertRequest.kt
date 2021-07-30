package com.github.xiaosongfu.atp.domain.dto.project

import com.github.xiaosongfu.atp.domain.vo.project.ProjectRequestResponseVO
import com.github.xiaosongfu.atp.domain.vo.project.ProjectRequestVO
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

data class ProjectRequestInsertRequest(
    @Schema(description = "请求")
    @NotNull(message = "请求不能为空")
    var request: ProjectRequestVO,

    @Schema(description = "响应字段验证")
    var responseFieldValidate: List<ProjectRequestResponseVO>?
)
