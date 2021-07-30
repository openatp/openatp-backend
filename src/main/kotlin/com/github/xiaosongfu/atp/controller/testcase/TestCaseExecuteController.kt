package com.github.xiaosongfu.atp.controller.testcase

import com.alibaba.excel.EasyExcel
import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteDetail
import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteHistory
import com.github.xiaosongfu.atp.service.testcase.TestCaseExecuteService
import com.github.xiaosongfu.atp.service.testcase.boom.BoomService
import com.github.xiaosongfu.jakarta.dto.R
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
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

    @Autowired
    private lateinit var boomService: BoomService

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

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

    @GetMapping("/start/{projectId}/{projectServerId}/{testCaseId}")
    @Operation(summary = "开始执行测试案例")
    fun start(
        @Parameter(description = "项目 ID") @PathVariable projectId: Long,
        @Parameter(description = "项目服务器 ID") @PathVariable projectServerId: Long,
        @Parameter(description = "测试案例 ID") @PathVariable testCaseId: Long
    ): R<Unit> {
        boomService.boom(projectId, projectServerId, testCaseId)
        return R.success()
    }

    @GetMapping("/export/excel/{executeHistoryId}")
    @Operation(summary = "导出测试案例执行详情")
    fun export(
        @Parameter(description = "测试案例执行记录 ID") @PathVariable executeHistoryId: String,
        response: HttpServletResponse
    ) {
        // 表头
        val title = listOf("编号", "请求名称", "HTTP 请求", "HTTP 响应", "保存环境变量", "响应验证信息", "响应验证结果")

        // 表格数据
        val excelData = testCaseExecuteService.detail(executeHistoryId)?.map {
            listOf(
                it.id,
                it.testCaseRequestName,
                it.httpRequest,
                it.httpResponse,
                it.saveEnvVariableInfo,
                it.execCheckInfo,
                it.execCheckResult
            )
        }

        // 设置头
        response.contentType = "application/vnd.ms-excel"
        response.characterEncoding = "utf-8"
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${executeHistoryId}.xlsx")

        // |---------|-------------|--------------|--------------|---------------|-------------|--------------|
        // |   编号   |   请求名称   | HTTP 请求信息 | HTTP 响应信息  | 保存环境变量信息 |  响应验证信息 |  响应验证结果  |
        // |---------|-------------|--------------|--------------|---------------|--------------|--------------|
        // |  1      |    张三      |      {}      |     {}       |        {}     |     {}       |      1       |
        // |---------|-------------|--------------|--------------|---------------|---------------|-------------|
        // |  2      |    李四      |      {}      |    {}        |      {}       |       {}      |     0       |
        // |---------|-------------|--------------|--------------|---------------|----------------|------- -----|
        // 写 Excel
        val writer = EasyExcel.write(response.outputStream).sheet("sheet1")
        writer.doWrite(title) // 写表头
        writer.doWrite(excelData) // 写表格
    }
}