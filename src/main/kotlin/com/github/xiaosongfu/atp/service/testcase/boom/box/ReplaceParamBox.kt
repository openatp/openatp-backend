package com.github.xiaosongfu.atp.service.testcase.boom.box

import com.github.xiaosongfu.atp.domain.vo.boom.BoomVO
import org.springframework.stereotype.Service

@Service
class ReplaceParamBox {

    // 替换位置参数和环境变量参数
    //
    // http.url http.header http.param
    //
    // PositionParam : $[0]
    // EnvParam : ${姓名}
    //
    fun replacePositionAndEnvParams(
        fetchApi: BoomVO.FetchApi,
        testCaseRequestParams: List<String>,
        envs: HashMap<String, String>
    ) {
        // http.url
        fetchApi.url = replacePositionParams(fetchApi.url, testCaseRequestParams)
        fetchApi.url = replaceEnvParams(fetchApi.url, envs)

        // http.header
        fetchApi.header?.let {
            fetchApi.header = replacePositionParams(it, testCaseRequestParams)
            fetchApi.header = replaceEnvParams(it, envs)
        }

        // http.param
        fetchApi.param?.let {
            fetchApi.param = replacePositionParams(it, testCaseRequestParams)
            fetchApi.param = replaceEnvParams(it, envs)
        }
    }

    // 替换位置参数
    private fun replacePositionParams(source: String, testCaseRequestParams: List<String>): String {
        var result = source

        testCaseRequestParams.forEachIndexed { index, param ->
            result = result.replace("\$[${index}]", param) // PositionParam : $[0]
        }

        return result
    }

    // 替换环境变量参数
    private fun replaceEnvParams(source: String, envs: HashMap<String, String>): String {
        var result = source

        envs.forEach { env ->
            result = result.replace("\${${env.key}}", env.value) // EnvParam : ${姓名}
        }

        return result
    }
}