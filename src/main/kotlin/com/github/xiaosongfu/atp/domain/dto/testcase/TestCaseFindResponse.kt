package com.github.xiaosongfu.atp.domain.dto.testcase

import com.github.xiaosongfu.atp.domain.vo.testcase.TestCaseRequestExecCheckVO
import com.github.xiaosongfu.atp.domain.vo.testcase.TestCaseRequestSaveEnvVariableVO
import com.github.xiaosongfu.atp.domain.vo.testcase.TestCaseRequestVO
import io.swagger.v3.oas.annotations.media.Schema

data class TestCaseFindResponse(
    @Schema(description = "请求 ID")
    var id: Long,

    @Schema(description = "请求")
    var request: TestCaseRequestVO,

    @Schema(description = "响应字段验证")
    var requestExecCheck: List<TestCaseRequestExecCheckVO>?,

    @Schema(description = "保存环境变量")
    var requestSaveEnvVariable: List<TestCaseRequestSaveEnvVariableVO>?
)
