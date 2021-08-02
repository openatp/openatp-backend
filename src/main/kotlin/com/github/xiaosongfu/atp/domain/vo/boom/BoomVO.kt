package com.github.xiaosongfu.atp.domain.vo.boom

import io.swagger.v3.oas.annotations.media.Schema

data class BoomVO(
    @Schema(description = "测试案例名称")
    var name: String,

    @Schema(description = "测试案例类型")
    var type: String,

    @Schema(description = "benchmark 测试数据")
    var benchmark: Benchmark? = null,

    @Schema(description = "replay 测试数据")
    var replay: Replay? = null,

    @Schema(description = "pipeline 测试数据")
    var pipeline: Pipeline? = null
) {
    data class Benchmark(
        @Schema(description = "请求接口信息")
        var fetchApi: FetchApi,

        // --- --- --- ---

        @Schema(description = "请求参数")
        var request: Request,

        @Schema(description = "请求响应验证")
        var execCheck: List<ExecCheck>? = null,

        @Schema(description = "环境变量保存")
        var saveEnvVariable: List<SaveEnvVariable>? = null
    )

    data class Replay(
        @Schema(description = "请求接口信息")
        var fetchApi: FetchApi,

        // --- --- --- ---

        @Schema(description = "请求参数")
        var requests: List<Bundle>
    ) {
        data class Bundle(
            @Schema(description = "请求参数")
            var request: Request,

            @Schema(description = "请求响应验证")
            var execCheck: List<ExecCheck>? = null,

            @Schema(description = "环境变量保存")
            var saveEnvVariable: List<SaveEnvVariable>? = null
        )
    }

    data class Pipeline(
        @Schema(description = "请求参数")
        var requests: List<Bundle>
    ) {
        data class Bundle(
            @Schema(description = "请求接口信息")
            var fetchApi: FetchApi,

            // --- --- --- ---

            @Schema(description = "请求参数")
            var request: Request,

            @Schema(description = "请求响应验证")
            var execCheck: List<ExecCheck>? = null,

            @Schema(description = "环境变量保存")
            var saveEnvVariable: List<SaveEnvVariable>? = null
        )
    }

    // --- --- --- --- --- ---
    // --- --- --- --- --- ---

    data class FetchApi(
        @Schema(description = "请求名称")
        var name: String,

        @Schema(description = "请求完整地址,可直接请求")
        var url: String,

        @Schema(description = "http method")
        var method: String,

        @Schema(description = "content type")
        var contentType: String? = null,

        @Schema(description = "请求参数")
        var param: String? = null,

        @Schema(description = "请求头")
        var header: String? = null,

        @Schema(description = "请求超时")
        var timeout: Long
    )

    data class Request(
        @Schema(description = "测试案例请求名称")
        var name: String,

        @Schema(description = "请求参数")
        var param: String? = null
    )

    data class ExecCheck(
        @Schema(description = "字段名称")
        var fieldName: String,

        @Schema(description = "字段路径")
        var fieldPath: String,

        @Schema(description = "期望的响应字段值")
        var wantFieldValue: String
    )

    data class SaveEnvVariable(
        @Schema(description = "环境变量名称")
        var variableName: String,

        @Schema(description = "环境变量默认值")
        var defaultValue: String? = null,

        @Schema(description = "环境变量路径[jsonpath xpath]")
        var projectEnvVariableValuePath: String
    )
}
