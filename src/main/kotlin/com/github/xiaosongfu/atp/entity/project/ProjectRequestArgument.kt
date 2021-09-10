package com.github.xiaosongfu.atp.entity.project

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * 项目请求参数配置
 */
@Entity
@Table(name = "project_request_argument")
data class ProjectRequestArgument(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Schema(description = "项目 ID")
    @Column(name = "project_id")
    var projectId: Long,

    @Schema(description = "请求 ID")
    @Column(name = "request_id")
    var requestId: Long,

    @Schema(description = "参数名称")
    @Column(name = "argument_name")
    var argumentName: String
)
