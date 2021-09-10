package com.github.xiaosongfu.atp.service.testcase.boom.box

import com.github.xiaosongfu.atp.domain.vo.boom.BoomVO
import org.springframework.stereotype.Service

@Service
class ReplaceParamBox {

    // 替换Arg参数和Env参数
    //
    // http.url http.header http.param
    //
    // ArgParam : $[签名]
    // EnvParam : ${姓名}
    //
    fun replaceArgAndEnvParams(
        fetchApi: BoomVO.FetchApi,
        args: HashMap<String, String>,
        envs: HashMap<String, String>
    ) {
        // http.url
        val urlTemp = replaceArgParams(fetchApi.url, args)
        fetchApi.url = replaceEnvParams(urlTemp, envs)

        // http.header
        fetchApi.header?.let {
            val headerTemp = replaceArgParams(it, args)
            fetchApi.header = replaceEnvParams(headerTemp, envs)
        }

        // http.param
        fetchApi.param?.let {
            val paramTemp = replaceArgParams(it, args)
            fetchApi.param = replaceEnvParams(paramTemp, envs)
        }
    }

    // 替换Arg参数
    private fun replaceArgParams(source: String, args: HashMap<String, String>): String {
        var result = source

        args.forEach { arg ->
            result = result.replace("\$[${arg.key}]", arg.value) // PositionParam : $[签名]
        }

        return result
    }

    // 替换Env参数
    private fun replaceEnvParams(source: String, envs: HashMap<String, String>): String {
        var result = source

        envs.forEach { env ->
            result = result.replace("\${${env.key}}", env.value) // EnvParam : ${姓名}
        }

        return result
    }
}
