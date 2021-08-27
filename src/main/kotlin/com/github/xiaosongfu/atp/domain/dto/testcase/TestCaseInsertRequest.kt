package com.github.xiaosongfu.atp.domain.dto.testcase

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotEmpty

data class TestCaseInsertRequest(
    @Schema(description = "测试案例名称")
    @NotEmpty(message = "测试案例名称不能为空")
    var name: String,

    @Schema(description = "类型 [pipeline replay benchmark]")
    @NotEmpty(message = "类型不能为空")
    var type: String,

    @Schema(description = "benchmark和replay类型的测试案例关联的唯一的项目请求 ID，更新测试案例的时不需要传该参数，因为不允许修改关联的请求")
    var projectRequestId: Long?
)