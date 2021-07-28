package com.github.xiaosongfu.atp.entity.project

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "project_env_variable")
data class ProjectEnvVariable(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Schema(description = "项目 ID")
    @Column(name = "project_id")
    var projectId: Long,

    @Schema(description = "环境变量名称")
    @Column(name = "variable_name")
    var variableName: String,

    @Schema(description = "环境变量默认值")
    @Column(name = "default_value")
    var defaultValue: String? = null
)
