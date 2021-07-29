package com.github.xiaosongfu.atp.entity.project

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * 项目配置
 */
@Entity
@Table(name = "project")
data class Project(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Schema(description = "")
    @Column(name = "name")
    var name: String,

    @Schema(description = "描述")
    @Column(name = "description")
    var desc: String? = null
)
