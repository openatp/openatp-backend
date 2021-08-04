package com.github.xiaosongfu.atp.controller.testcase

import com.github.xiaosongfu.atp.domain.dto.testcase.TestCaseInsertRequest
import com.github.xiaosongfu.atp.entity.testcase.TestCase
import com.github.xiaosongfu.atp.service.testcase.TestCaseService
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
import javax.validation.Valid

@RestController
@RequestMapping("/testcase/v1", produces = ["application/json;charset=utf-8"])
@Tag(name = "2.1 测试案例管理")
class TestCaseController {
    @Autowired
    private lateinit var testCaseService: TestCaseService

    @Operation(summary = "新建测试案例")
    @PostMapping("/{projectId}")
    fun insert(
        @Parameter(description = "项目 ID") @PathVariable projectId: Long,
        @RequestBody @Valid req: TestCaseInsertRequest
    ): R<Unit> {
        testCaseService.insert(projectId, req)
        return R.success()
    }

    @DeleteMapping("/{testCaseId}")
    @Operation(summary = "删除测试案例")
    fun delete(
        @Parameter(description = "测试案例 ID") @PathVariable testCaseId: Long
    ): R<Unit> {
        testCaseService.delete(testCaseId)
        return R.success()
    }

    @PutMapping("/{testCaseId}")
    @Operation(summary = "更新测试案例")
    fun update(
        @Parameter(description = "测试案例 ID") @PathVariable testCaseId: Long,
        @RequestBody @Valid req: TestCaseInsertRequest
    ): R<Unit> {
        testCaseService.update(testCaseId, req)
        return R.success()
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    @GetMapping("/list/{projectId}")
    @Operation(summary = "测试案例列表--分页")
    fun list(
        @Parameter(description = "项目 ID") @PathVariable projectId: Long
    ): R<List<TestCase>> {
        return R.success(data = testCaseService.list(projectId))
    }
}
