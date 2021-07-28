package com.github.xiaosongfu.atp.controller.testcase

import com.github.xiaosongfu.atp.domain.dto.testcase.TestCaseFindResponse
import com.github.xiaosongfu.atp.domain.dto.testcase.TestCaseRequestInsertRequest
import com.github.xiaosongfu.atp.entity.testcase.TestCaseRequest
import com.github.xiaosongfu.atp.service.testcase.TestCaseRequestService
import com.github.xiaosongfu.jakarta.dto.R
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/testcase/request/v1", produces = ["application/json;charset=utf-8"])
@Tag(name = "2.2 测试案例请求管理")
class TestCaseRequestController {
    @Autowired
    private lateinit var testCaseRequestService: TestCaseRequestService

    @Operation(summary = "新建测试案例请求")
    @PostMapping("/{testCaseId}")
    fun insert(
        @Parameter(description = "项目 ID") @PathVariable testCaseId: Long,
        @RequestBody req: TestCaseRequestInsertRequest // TODO @Valid 验证
    ): R<Unit> {
        testCaseRequestService.insert(testCaseId, req)
        return R.success()
    }

    @DeleteMapping("/{testCaseRequestId}")
    @Operation(summary = "删除测试案例请求")
    fun delete(
        @Parameter(description = "测试案例请求 ID") @PathVariable testCaseRequestId: Long
    ): R<Unit> {
        testCaseRequestService.delete(testCaseRequestId)
        return R.success()
    }

    @PutMapping("/{testCaseRequestId}")
    @Operation(summary = "更新测试案例请求")
    fun update(
        @Parameter(description = "测试案例请求 ID") @PathVariable testCaseRequestId: Long,
        @RequestBody req: TestCaseRequestInsertRequest
    ): R<Unit> {
        testCaseRequestService.update(testCaseRequestId, req)
        return R.success()
    }

    @GetMapping("/detail/{testCaseRequestId}")
    @Operation(summary = "测试案例请求详情")
    fun detail(
        @Parameter(description = "测试案例请求 ID") @PathVariable testCaseRequestId: Long,
    ): R<TestCaseFindResponse> {
        return R.success(data = testCaseRequestService.detail(testCaseRequestId))
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    @GetMapping("/list")
    @Operation(summary = "测试案例请求列表--分页")
    fun list(): R<List<TestCaseRequest>> {
        return R.success(data = testCaseRequestService.list())
    }
}
