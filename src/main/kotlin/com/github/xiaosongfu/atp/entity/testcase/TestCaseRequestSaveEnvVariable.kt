package com.github.xiaosongfu.atp.entity.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "test_case_request_save_env_variable")
data class TestCaseRequestSaveEnvVariable(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = -1,

    @Schema(description = "测试案例请求 ID")
    @Column(name = "test_case_request_id")
    var testCaseRequestId: Long,

    @Schema(description = "环境变量 ID")
    @Column(name = "project_env_variable_id")
    var projectEnvVariableId: Long,

    @Schema(description = "环境变量路径[jsonpath xpath]")
    @Column(name = "project_env_variable_value_path")
    var projectEnvVariableValuePath: String,
)
