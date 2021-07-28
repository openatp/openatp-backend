package com.github.xiaosongfu.atp.domain.vo.testcase

import io.swagger.v3.oas.annotations.media.Schema

data class TestCaseRequestExecCheckVO(
    @Schema(description = "需要校验的响应 ID")
    var projectRequestResponseId: Long,

    @Schema(description = "期望的响应字段值")
    var wantResponseFieldValue: String
)
