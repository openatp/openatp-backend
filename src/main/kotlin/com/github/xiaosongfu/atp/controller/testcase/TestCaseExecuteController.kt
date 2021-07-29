package com.github.xiaosongfu.atp.controller.testcase

import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteDetail
import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteHistory
import com.github.xiaosongfu.atp.service.testcase.TestCaseExecuteService
import com.github.xiaosongfu.jakarta.dto.R
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/testcase/execute/v1", produces = ["application/json;charset=utf-8"])
@Tag(name = "2.3 测试案例执行管理")
class TestCaseExecuteController {
    @Autowired
    private lateinit var testCaseExecuteService: TestCaseExecuteService

    @GetMapping("/detail/{executeHistoryId}")
    @Operation(summary = "测试案例执行详情")
    fun detail(
        @Parameter(description = "测试案例执行记录 ID") @PathVariable executeHistoryId: String,
    ): R<List<TestCaseExecuteDetail>> {
        return R.success(data = testCaseExecuteService.detail(executeHistoryId))
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    @GetMapping("/list/{testCaseId}")
    @Operation(summary = "测试案例执行记录列表--分页")
    fun list(
        @Parameter(description = "测试案例 ID") @PathVariable testCaseId: Long
    ): R<List<TestCaseExecuteHistory>> {
        return R.success(data = testCaseExecuteService.list(testCaseId))
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    @GetMapping("/export/excel/{executeHistoryId}")
    @Operation(summary = "导出测试案例执行详情")
    fun export(
        @Parameter(description = "测试案例执行记录 ID") @PathVariable executeHistoryId: String,
        response: HttpServletResponse
    ) {

    }
}
