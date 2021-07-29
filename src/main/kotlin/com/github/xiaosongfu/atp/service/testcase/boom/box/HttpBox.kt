package com.github.xiaosongfu.atp.service.testcase.boom.box

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.xiaosongfu.atp.entity.project.ProjectRequest
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class HttpBox {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val log = LoggerFactory.getLogger(javaClass)

    // OkHttpClient
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(Duration.ofMillis(2_000))
        .readTimeout(Duration.ofMillis(2_000))
        .build()

    // 实现 HTTP 请求和结果解析
    fun doHttp(id: String, http: HttpRequest): HttpResponse {
        // 1 根据 method 封装请求
        val request = when (http.method) {
            ProjectRequest.Method.GET -> {
                doGet(http.url, http.param, http.header)
            }
            ProjectRequest.Method.POST -> {
                // POST 请求 的 ContentType 只支持 JSON 和 FORM
                if (http.contentType == ProjectRequest.ContentType.FORM) {
                    doPostForm(http.url, http.param, http.header)
                } else {
                    doPostJson(http.url, http.param, http.header)
                }
            }
            ProjectRequest.Method.DELETE -> {
                if (http.contentType == ProjectRequest.ContentType.FORM) {
                    doDeleteForm(http.url, http.param, http.header)
                } else {
                    doDeleteJson(http.url, http.param, http.header)
                }
            }
            ProjectRequest.Method.PUT -> {
                if (http.contentType == ProjectRequest.ContentType.FORM) {
                    doPutForm(http.url, http.param, http.header)
                } else {
                    doPutJson(http.url, http.param, http.header)
                }
            }
            else -> {
                throw Exception("请求 method [${http.method}] 不支持")
            }
        }

        // 2 执行请求
        okHttpClient.newCall(request).execute().use { response ->
            return HttpResponse(responseCode = response.code, responseBody = response.body?.string() ?: "")
        }
    }

    // ------ ------ ------ ------ ------ ------ ------ ------ ------
    // ------ ------ ------ ------ ------ ------ ------ ------ ------
    // ------ ------ ------ ------ ------ ------ ------ ------ ------

    // 封装 GET 请求
    private fun doGet(
        url: String,
        params: HashMap<String, Any>?,
        headers: HashMap<String, String>?
    ): Request {
        // 拼接参数
        val fullUrl = if (params == null) {
            url
        } else {
            val param = StringBuilder()
            for ((key, value) in params) {
//                  if (value !is String || value !is Int) { // TODO value 可以是哪些类型
//                      throw Exception("post request params error, ${key}'s value[${value}] is not a basic type")
//                  }
                param.append(key).append("=").append(value.toString()).append("&")
            }
            "${url}?$param"
        }

        val request = Request.Builder()
            .url(fullUrl)
            .get()

        // 处理 headers
        headers?.forEach { header ->
            request.addHeader(header.key, header.value)
        }

        return request.build()
    }

    // ------ ------ ------ ------ ------ ------ ------ ------ ------

    // 封装 POST JSON 请求
    private fun doPostJson(
        url: String,
        params: HashMap<String, Any>?,
        headers: HashMap<String, String>?
    ): Request {
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")

        // 处理 headers
        headers?.forEach { header ->
            request.addHeader(header.key, header.value)
        }

        // 处理 params
        if (params != null) {
            val json = objectMapper.writeValueAsString(params)

            request.post(json.toRequestBody())
        } else {
            request.post(ByteArray(0).toRequestBody())
        }

        return request.build()
    }

    // 封装 POST FORM 请求
    private fun doPostForm(
        url: String,
        params: HashMap<String, Any>?,
        headers: HashMap<String, String>?
    ): Request {
        val request = Request.Builder().url(url)

        // 处理 headers
        headers?.forEach { header ->
            request.addHeader(header.key, header.value)
        }

        // 处理 params
        if (params != null) {
            val formBody = FormBody.Builder()
            for ((key, value) in params) {
//                    if (value !is String) { // TODO value 可以是哪些类型
//                        throw Exception("post request params error, ${key}'s value[${value}] is not a basic type")
//                    }
                formBody.add(key, value.toString())
            }
            request.post(formBody.build())
        } else {
            request.post(ByteArray(0).toRequestBody())
        }

        return request.build()
    }

    // ------ ------ ------ ------ ------ ------ ------ ------ ------

    // 封装 DELETE JSON 请求
    private fun doDeleteJson(
        url: String,
        params: HashMap<String, Any>?,
        headers: HashMap<String, String>?
    ): Request {
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")

        // 处理 headers
        headers?.forEach { header ->
            request.addHeader(header.key, header.value)
        }

        // 处理 params
        if (params != null) {
            val json = objectMapper.writeValueAsString(params)
            request.delete(json.toRequestBody())
        } else {
            request.delete(ByteArray(0).toRequestBody())
        }

        return request.build()
    }

    // 封装 DELETE FORM 请求
    private fun doDeleteForm(
        url: String,
        params: HashMap<String, Any>?,
        headers: HashMap<String, String>?
    ): Request {
        val request = Request.Builder().url(url)

        // 处理 headers
        headers?.forEach { header ->
            request.addHeader(header.key, header.value)
        }

        // 处理 params
        if (params != null) {
            val formBody = FormBody.Builder()
            for ((key, value) in params) {
//                    if (value !is String) { // TODO value 可以是哪些类型
//                        throw Exception("post request params error, ${key}'s value[${value}] is not a basic type")
//                    }
                formBody.add(key, value.toString())
            }
            request.delete(formBody.build())
        } else {
            request.delete(ByteArray(0).toRequestBody())
        }

        return request.build()
    }

    // ------ ------ ------ ------ ------ ------ ------ ------ ------

    // 封装 PUT JSON 请求
    private fun doPutJson(
        url: String,
        params: HashMap<String, Any>?,
        headers: HashMap<String, String>?
    ): Request {
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")

        // 处理 headers
        headers?.forEach { header ->
            request.addHeader(header.key, header.value)
        }

        // 处理 params
        if (params != null) {
            val json = objectMapper.writeValueAsString(params)
            request.put(json.toRequestBody())
        } else {
            request.put(ByteArray(0).toRequestBody())
        }

        return request.build()
    }

    // 封装 PUT FORM 请求
    private fun doPutForm(
        url: String,
        params: HashMap<String, Any>?,
        headers: HashMap<String, String>?
    ): Request {
        val request = Request.Builder().url(url)

        // 处理 headers
        headers?.forEach { header ->
            request.addHeader(header.key, header.value)
        }

        // 处理 params
        if (params != null) {
            val formBody = FormBody.Builder()
            for ((key, value) in params) {
//                    if (value !is String) { // TODO value 可以是哪些类型
//                        throw Exception("post request params error, ${key}'s value[${value}] is not a basic type")
//                    }
                formBody.add(key, value.toString())
            }
            request.put(formBody.build())
        } else {
            request.put(ByteArray(0).toRequestBody())
        }

        return request.build()
    }
}

data class HttpRequest(
    val url: String,
    val method: String,
    val contentType: String,
    val header: HashMap<String, String>? = null,
    var param: HashMap<String, Any>? = null
)

data class HttpResponse(
    val responseCode: Int,
    val responseBody: String
)
