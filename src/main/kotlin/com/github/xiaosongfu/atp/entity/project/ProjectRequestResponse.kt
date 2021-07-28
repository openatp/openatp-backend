package com.github.xiaosongfu.atp.entity.project

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "project_request_response")
data class ProjectRequestResponse(
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

    @Schema(description = "字段名称")
    @Column(name = "field_name")
    var fieldName: String,

    @Schema(description = "字段路径[jsonpath xpath]")
    @Column(name = "field_path")
    var fieldPath: String
)
