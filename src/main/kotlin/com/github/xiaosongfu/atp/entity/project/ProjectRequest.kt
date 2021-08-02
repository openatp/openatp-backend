package com.github.xiaosongfu.atp.entity.project

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * 项目请求配置
 */
@Entity
@Table(name = "project_request")
data class ProjectRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Schema(description = "项目 ID")
    @Column(name = "project_id")
    var projectId: Long,

    @Schema(description = "请求名称")
    @Column(name = "name")
    var name: String,

    @Schema(description = "请求路径,如 /v1/welcome,必须以/开头")
    @Column(name = "path")
    var path: String,

    @Schema(description = "http method [get post delete put]")
    @Column(name = "method")
    var method: String,

    @Schema(description = "content type [form json]")
    @Column(name = "content_type")
    var contentType: String? = null,

    @Schema(description = "请求参数 可以使用 HashMap<String, Any> 解析")
    @Column(name = "param")
    var param: String? = null,

    @Schema(description = "请求头")
    @Column(name = "header")
    var header: String? = null,

    @Schema(description = "请求超时")
    @Column(name = "timeout")
    var timeout: Long
) {
    // http 请求的 Method
    class Method {
        companion object {
            const val GET = "GET"
            const val POST = "POST"
            const val DELETE = "DELETE"
            const val PUT = "PUT"
        }
    }

    // http 请求的 Content-Type
    class ContentType {
        companion object {
            const val JSON = "JSON" // Content-Type:application/json
            const val FORM = "FORM" // Content-Type:application/x-www-form-urlencoded
        }
    }
}
