package com.github.xiaosongfu.atp.controller.project

import com.github.xiaosongfu.atp.domain.dto.project.ProjectEnvVariableInsertRequest
import com.github.xiaosongfu.atp.entity.project.ProjectEnvVariable
import com.github.xiaosongfu.atp.service.project.ProjectEnvVariableService
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

@RestController
@RequestMapping("/project/env_variable/v1", produces = ["application/json;charset=utf-8"])
@Tag(name = "1.3 项目环境变量管理")
class ProjectEnvVariableController {
    @Autowired
    private lateinit var projectEnvVariableService: ProjectEnvVariableService

    @Operation(summary = "新建环境变量")
    @PostMapping("/{projectId}")
    fun insert(
        @Parameter(description = "项目 ID") @PathVariable projectId: Long,
        @RequestBody req: ProjectEnvVariableInsertRequest // TODO @Valid 验证
    ): R<Unit> {
        projectEnvVariableService.insert(projectId, req)
        return R.success()
    }

    @DeleteMapping("/{projectEnvVariableId}")
    @Operation(summary = "删除环境变量")
    fun delete(
        @Parameter(description = "环境变量 ID") @PathVariable projectEnvVariableId: Long
    ): R<Unit> {
        projectEnvVariableService.delete(projectEnvVariableId)
        return R.success()
    }

    @PutMapping("/{projectEnvVariableId}")
    @Operation(summary = "更新环境变量")
    fun update(
        @Parameter(description = "环境变量 ID") @PathVariable projectEnvVariableId: Long,
        @RequestBody req: ProjectEnvVariableInsertRequest
    ): R<Unit> {
        projectEnvVariableService.update(projectEnvVariableId, req)
        return R.success()
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    @GetMapping("/all")
    @Operation(summary = "环境变量列表--不分页")
    fun all(): R<List<ProjectEnvVariable>> {
        return R.success(data = projectEnvVariableService.all())
    }
}
