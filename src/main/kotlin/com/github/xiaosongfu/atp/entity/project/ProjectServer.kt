package com.github.xiaosongfu.atp.entity.project

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * 项目服务器配置
 */
@Entity
@Table(name = "project_server")
data class ProjectServer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Schema(description = "项目 ID")
    @Column(name = "project_id")
    var projectId: Long,

    @Schema(description = "服务器名称")
    @Column(name = "server_name")
    var serverName: String,

    @Schema(description = "服务器地址,结尾不能有/")
    @Column(name = "server_addr")
    var serverAddr: String
)
