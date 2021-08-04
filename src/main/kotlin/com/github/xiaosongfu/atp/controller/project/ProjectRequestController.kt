package com.github.xiaosongfu.atp.controller.project

import com.github.xiaosongfu.atp.domain.dto.project.ProjectRequestFindResponse
import com.github.xiaosongfu.atp.domain.dto.project.ProjectRequestInsertRequest
import com.github.xiaosongfu.atp.entity.project.ProjectRequest
import com.github.xiaosongfu.atp.service.project.ProjectRequestService
import com.github.xiaosongfu.jakarta.dto.R
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/project/request/v1", produces = ["application/json;charset=utf-8"])
@Tag(name = "1.4 项目请求管理")
class ProjectRequestController {
    @Autowired
    private lateinit var projectRequestService: ProjectRequestService

    @Operation(summary = "新建请求")
    @PostMapping("/{projectId}")
    fun insert(
        @Parameter(description = "项目 ID") @PathVariable projectId: Long,
        @RequestBody @Valid req: ProjectRequestInsertRequest
    ): R<Unit> {
        projectRequestService.insert(projectId, req)
        return R.success()
    }

    @DeleteMapping("/{projectRequestId}")
    @Operation(summary = "删除请求")
    fun delete(
        @Parameter(description = "请求 ID") @PathVariable projectRequestId: Long
    ): R<Unit> {
        projectRequestService.delete(projectRequestId)
        return R.success()
    }

    @PutMapping("/{projectRequestId}")
    @Operation(summary = "更新请求")
    fun update(
        @Parameter(description = "请求 ID") @PathVariable projectRequestId: Long,
        @RequestBody @Valid req: ProjectRequestInsertRequest
    ): R<Unit> {
        TODO()
    }

//    @GetMapping("/detail/{projectRequestId}")
//    @Operation(summary = "请求详情")
//    fun detail(
//        @Parameter(description = "请求 ID") @PathVariable projectRequestId: Long,
//    ): R<ProjectRequestFindResponse> {
//        return R.success(data = projectRequestService.detail(projectRequestId))
//    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

//    @GetMapping("/list/{projectId}")
//    @Operation(summary = "请求列表--分页")
//    fun list(
//        @Parameter(description = "项目 ID") @PathVariable projectId: Long
//    ): R<List<ProjectRequest>> {
//        return R.success(data = projectRequestService.list(projectId))
//    }

    @GetMapping("/list/{projectId}")
    @Operation(summary = "请求列表--分页")
    fun list(
        @Parameter(description = "项目 ID") @PathVariable projectId: Long
    ): R<List<ProjectRequestFindResponse>> {
        return R.success(data = projectRequestService.listWithDetail(projectId))
    }
}
