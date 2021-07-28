package com.github.xiaosongfu.atp.controller.project

import com.github.xiaosongfu.atp.domain.dto.project.ProjectServerInsertRequest
import com.github.xiaosongfu.atp.entity.project.ProjectServer
import com.github.xiaosongfu.atp.service.project.ProjectServerService
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
@RequestMapping("/project/server/v1", produces = ["application/json;charset=utf-8"])
@Tag(name = "1.2 项目服务器管理")
class ProjectServerController {
    @Autowired
    private lateinit var projectServerService: ProjectServerService

    @Operation(summary = "新建服务器")
    @PostMapping("/{projectId}")
    fun insert(
        @Parameter(description = "项目 ID") @PathVariable projectId: Long,
        @RequestBody req: ProjectServerInsertRequest // TODO @Valid 验证
    ): R<Unit> {
        projectServerService.insert(projectId, req)
        return R.success()
    }

    @DeleteMapping("/{projectServerId}")
    @Operation(summary = "删除服务器")
    fun delete(
        @Parameter(description = "服务器 ID") @PathVariable projectServerId: Long
    ): R<Unit> {
        projectServerService.delete(projectServerId)
        return R.success()
    }

    @PutMapping("/{projectServerId}")
    @Operation(summary = "更新服务器")
    fun update(
        @Parameter(description = "服务器 ID") @PathVariable projectServerId: Long,
        @RequestBody req: ProjectServerInsertRequest
    ): R<Unit> {
        projectServerService.update(projectServerId, req)
        return R.success()
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    @GetMapping("/all")
    @Operation(summary = "服务器列表--不分页")
    fun all(): R<List<ProjectServer>> {
        return R.success(data = projectServerService.all())
    }
}
