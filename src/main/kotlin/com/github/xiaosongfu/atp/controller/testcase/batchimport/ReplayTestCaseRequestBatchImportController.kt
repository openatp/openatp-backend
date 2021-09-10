package com.github.xiaosongfu.atp.controller.testcase.batchimport

import com.alibaba.excel.EasyExcel
import com.github.xiaosongfu.atp.service.testcase.batchimport.ReplayTestCaseRequestBatchImportService
import com.github.xiaosongfu.jakarta.dto.R
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/testcase/replay/request/batch_import/v1", produces = ["application/json;charset=utf-8"])
@Tag(name = "2.10 Replay类型测试案例请求批量导入")
class ReplayTestCaseRequestBatchImportController {
    @Autowired
    private lateinit var batchImportService: ReplayTestCaseRequestBatchImportService

    @Operation(summary = "Replay类型测试案例请求批量导入excel模板下载")
    @GetMapping("/download_template/excel/{testCaseId}")
    fun downloadTemplateExcel(
        @Parameter(description = "测试案例 ID") @PathVariable testCaseId: Long,
        response: HttpServletResponse
    ) {
        // 准备 sheet 的 title
        val excelData = batchImportService.downloadTemplateExcel(testCaseId)

        // 设置头
        response.contentType = "application/vnd.ms-excel"
        response.characterEncoding = "utf-8"
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${testCaseId}.xlsx")

        // |-------------|-------------|-------------|------------|
        // | 请求参数-内容 | 请求参数-类型 | 响应验证-姓名 | 响应验证-年龄|
        // |-------------|-------------|-------------|------------|
        // |    你好啊    |     单纯     |    张三     |      19    |
        // |-------------|-------------|-------------|------------|
        // |    天气好    |     单车     |    李四     |      17    |
        // |-------------|-------------|-------------|------------|
        // 写 Excel
        EasyExcel.write(response.outputStream).sheet("sheet1").doWrite(listOf(excelData))
    }

    @Operation(summary = "Replay类型测试案例请求批量导入接口")
    @PostMapping("/upload_file/excel/{testCaseId}")
    fun batchImport(
        @Parameter(description = "测试案例 ID") @PathVariable testCaseId: Long,
        @Parameter(description = "excel 文件") @RequestParam("file") file: MultipartFile
    ): R<Unit> {
        // 读 Excel
        val excelData = EasyExcel.read(file.inputStream).sheet().doReadSync<Map<Int, String>>()

        // 开始导入
        batchImportService.batchImport(testCaseId, excelData)

        return R.success()
    }
}
