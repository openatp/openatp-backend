package com.github.xiaosongfu.atp.controller.testcase

import com.alibaba.excel.EasyExcel
import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteDetail
import com.github.xiaosongfu.atp.entity.testcase.TestCaseExecuteHistory
import com.github.xiaosongfu.atp.entity.testcase.execCheckResultName
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
        // 设置响应头
        response.contentType = "application/vnd.ms-excel"
        response.characterEncoding = "utf-8"
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${executeHistoryId}.xlsx")
        // excel writer
        val easyExcelWriter = EasyExcel.write(response.outputStream).build()

        // 测试案例执行概览 表头
        val overviewTitle =
            listOf("执行时间", "服务器名称", "执行状态", "执行状态详情", "总的请求数量", "请求成功的请求数量", "请求成功率", "请求验证正确的请求数量", "请求验证正确率")
        // 测试案例执行概览 表格数据
        val overviewExcelData = testCaseExecuteService.find(executeHistoryId)?.let {
            listOf(
                it.executeDatetime,
                it.projectServerName,
                it.executeStatus,
                it.executeStatusDetail ?: "",
                it.requestTotalCount,
                it.requestSuccessCount,
                it.requestSuccessRate,
                it.requestCheckCorrectCount,
                it.requestCheckCorrectRate
            )
        }
        // |-----------|-------------|------------|-------------|-----------------|-----------|-----------------------|-------------|
        // |  服务器名称 |   执行状态   | 执行状态详情 | 总的请求数量  | 请求成功的请求数量 |  请求成功率 |  请求验证正确的请求数量  | 请求验证正确率 |
        // |-----------|-------------|------------|-------------|-----------------|-----------|-----------------------|-------------|
        // |   dev     |    成功     |      {}    |     100      |        100      |   100%    |      100              |   100%     ｜
        // |-----------|-------------|------------|-------------|-----------------|-----------|-----------------------|-------------|
        // 写 Excel
        val overviewSheet = EasyExcel.writerSheet(0, "测试案例执行概览").build()
        easyExcelWriter.write(listOf(overviewTitle, overviewExcelData), overviewSheet) // 表头+表格

        // ---  ---  ---  ---  ---  ---  ---  ---  ---  ---  ---  ---  ---  ---

        // 测试案例执行明细 表头
        val detailData = mutableListOf<List<String>>()
        detailData.add(
            listOf(
                "编号",
                "请求名称",
                "HTTP 请求",
                "HTTP 请求耗时(单位:ms)",
                "HTTP 响应码",
                "HTTP 响应体",
                "保存环境变量",
                "响应验证信息",
                "响应验证结果"
            )
        )
        // 测试案例执行明细 表格数据
        testCaseExecuteService.detail(executeHistoryId)?.forEach {
            detailData.add(
                listOf(
                    it.id.toString(),
                    it.testCaseRequestName,
                    it.httpRequest,
                    it.httpRequestTime.toString(),
                    it.httpResponseCode.toString(),
                    it.httpResponseBody,
                    it.saveEnvVariableInfo ?: "",
                    it.execCheckInfo ?: "",
                    it.execCheckResult.execCheckResultName()
                )
            )
        }
        // |---------|-------------|--------------|--------------|--------------|--------------|---------------|-------------|--------------|
        // |   编号   |   请求名称   | HTTP 请求     | HTTP 请求耗时 | HTTP 响应码   | HTTP 响应体   | 保存环境变量信息 |  响应验证信息 |  响应验证结果  |
        // |---------|-------------|--------------|--------------|--------------|--------------|---------------|--------------|--------------|
        // |  1      |    张三      |      {}      |    6         |    {}        |     {}       |        {}     |     {}       |      1       |
        // |---------|-------------|--------------|--------------|--------------|--------------|---------------|---------------|-------------|
        // |  2      |    李四      |      {}      |    {}        |    {}        |    {}        |      {}       |       {}      |     0       |
        // |---------|-------------|--------------|--------------|--------------|--------------|---------------|----------------|------- -----|
        // 写 Excel
        val detailSheet = EasyExcel.writerSheet(1, "测试案例执行明细").build()
        easyExcelWriter.write(detailData, detailSheet) // 表头+表格

        easyExcelWriter.finish()
    }
}
