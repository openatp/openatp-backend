package com.github.xiaosongfu.atp.controller.project

import com.github.xiaosongfu.atp.domain.dto.project.ProjectInsertRequest
import com.github.xiaosongfu.atp.entity.project.Project
import com.github.xiaosongfu.atp.service.project.ProjectService
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
@RequestMapping("/project/v1", produces = ["application/json;charset=utf-8"])
@Tag(name = "1.1 项目管理")
class ProjectController {
    @Autowired
    private lateinit var projectService: ProjectService

    @Operation(summary = "新建项目")
    @PostMapping
    fun insert(
        @RequestBody @Valid req: ProjectInsertRequest
    ): R<Unit> {
        projectService.insert(req)
        return R.success()
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "删除项目")
    fun delete(
        @Parameter(description = "项目 ID") @PathVariable projectId: Long
    ): R<Unit> {
        projectService.delete(projectId)
        return R.success()
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "更新项目")
    fun update(
        @Parameter(description = "项目 ID") @PathVariable projectId: Long,
        @RequestBody @Valid req: ProjectInsertRequest
    ): R<Unit> {
        projectService.update(projectId, req)
        return R.success()
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    @GetMapping("/list")
    @Operation(summary = "项目列表--分页")
    fun list(): R<List<Project>> {
        return R.success(data = projectService.list())
    }
}
