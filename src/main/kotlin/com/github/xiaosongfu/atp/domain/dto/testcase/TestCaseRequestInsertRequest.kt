package com.github.xiaosongfu.atp.domain.dto.testcase

import com.github.xiaosongfu.atp.domain.vo.testcase.TestCaseRequestExecCheckVO
import com.github.xiaosongfu.atp.domain.vo.testcase.TestCaseRequestSaveEnvVariableVO
import com.github.xiaosongfu.atp.domain.vo.testcase.TestCaseRequestVO
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotNull

data class TestCaseRequestInsertRequest(
    @Schema(description = "请求")
    @NotNull(message = "请求不能为空")
    var request: TestCaseRequestVO,

    @Schema(description = "响应字段验证")
    var requestExecCheck: List<TestCaseRequestExecCheckVO>?,

    @Schema(description = "保存环境变量")
    var requestSaveEnvVariable: List<TestCaseRequestSaveEnvVariableVO>?
)