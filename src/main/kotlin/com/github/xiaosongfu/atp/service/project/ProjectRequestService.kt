package com.github.xiaosongfu.atp.service.project

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.xiaosongfu.atp.domain.dto.project.ProjectRequestFindResponse
import com.github.xiaosongfu.atp.domain.dto.project.ProjectRequestInsertRequest
import com.github.xiaosongfu.atp.domain.dto.project.ProjectRequestPreExecRequest
import com.github.xiaosongfu.atp.domain.dto.project.ProjectRequestPreExecResponse
import com.github.xiaosongfu.atp.domain.vo.boom.BoomVO
import com.github.xiaosongfu.atp.domain.vo.project.ProjectRequestResponseVO
import com.github.xiaosongfu.atp.domain.vo.project.ProjectRequestVO
import com.github.xiaosongfu.atp.entity.project.ProjectRequest
import com.github.xiaosongfu.atp.entity.project.ProjectRequestArgument
import com.github.xiaosongfu.atp.entity.project.ProjectRequestResponse
import com.github.xiaosongfu.atp.repository.project.ProjectRequestArgumentRepository
import com.github.xiaosongfu.atp.repository.project.ProjectRequestRepository
import com.github.xiaosongfu.atp.repository.project.ProjectRequestResponseRepository
import com.github.xiaosongfu.atp.repository.project.ProjectServerRepository
import com.github.xiaosongfu.atp.service.testcase.boom.box.HttpBox
import com.github.xiaosongfu.atp.service.testcase.boom.box.HttpRequest
import com.github.xiaosongfu.atp.service.testcase.boom.box.ReplaceParamBox
import com.github.xiaosongfu.jakarta.exception.service.ServiceException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectRequestService {
    @Autowired
    private lateinit var projectRequestRepository: ProjectRequestRepository

    @Autowired
    private lateinit var projectRequestArgumentRepository: ProjectRequestArgumentRepository

    @Autowired
    private lateinit var projectRequestResponseRepository: ProjectRequestResponseRepository

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    @Autowired
    private lateinit var projectServerRepository: ProjectServerRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var replaceParamBox: ReplaceParamBox

    @Autowired
    private lateinit var httpBox: HttpBox

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    @Transactional
    fun insert(projectId: Long, req: ProjectRequestInsertRequest) {
        val request = projectRequestRepository.save(
            ProjectRequest(
                projectId = projectId,
                name = req.request.name,
                path = req.request.path,
                method = req.request.method,
                contentType = req.request.contentType,
                param = req.request.param,
                header = req.request.header,
                timeout = req.request.timeout
            )
        )

        req.arguments?.forEach {
            projectRequestArgumentRepository.save(
                ProjectRequestArgument(
                    projectId = projectId,
                    requestId = request.id,
                    argumentName = it
                )
            )
        }

        req.responseFieldValidate?.forEach { resp ->
            projectRequestResponseRepository.save(
                ProjectRequestResponse(
                    projectId = projectId,
                    requestId = request.id,
                    fieldName = resp.fieldName,
                    fieldPath = resp.fieldPath
                )
            )
        }
    }

    @Transactional
    fun delete(projectRequestId: Long) {
        projectRequestRepository.deleteById(projectRequestId)
        projectRequestArgumentRepository.deleteAllByRequestId(projectRequestId)
        projectRequestResponseRepository.deleteAllByRequestId(projectRequestId)
    }

    @Transactional
    fun update(projectRequestId: Long, req: ProjectRequestInsertRequest) {
        TODO()
    }

    fun detail(projectRequestId: Long): ProjectRequestFindResponse? {
        return projectRequestRepository.findByIdOrNull(projectRequestId)?.let { req ->
            val responseFieldValidate = projectRequestResponseRepository.findAllByRequestId(req.id)?.map { resp ->
                ProjectRequestResponseVO(
                    id = resp.id,
                    fieldName = resp.fieldName,
                    fieldPath = resp.fieldPath
                )
            }

            val arguments = projectRequestArgumentRepository.findAllByRequestId(req.id)?.map { it.argumentName }

            ProjectRequestFindResponse(
                id = req.id,
                request = ProjectRequestVO(
                    name = req.name,
                    path = req.path,
                    method = req.method,
                    contentType = req.contentType,
                    param = req.param,
                    header = req.header,
                    timeout = req.timeout
                ),
                responseFieldValidate = responseFieldValidate,
                arguments = arguments
            )
        }
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

//    fun list(projectId: Long): List<ProjectRequest>? {
//        return projectRequestRepository.findAllByProjectId(projectId)
//    }

    fun listWithDetail(projectId: Long): List<ProjectRequestFindResponse>? {
        return projectRequestRepository.findAllByProjectId(projectId)?.map { req ->
            val responseFieldValidate = projectRequestResponseRepository.findAllByRequestId(req.id)?.map { resp ->
                ProjectRequestResponseVO(
                    id = resp.id,
                    fieldName = resp.fieldName,
                    fieldPath = resp.fieldPath
                )
            }

            val arguments = projectRequestArgumentRepository.findAllByRequestId(req.id)?.map { it.argumentName }

            ProjectRequestFindResponse(
                id = req.id,
                request = ProjectRequestVO(
                    name = req.name,
                    path = req.path,
                    method = req.method,
                    contentType = req.contentType,
                    param = req.param,
                    header = req.header,
                    timeout = req.timeout
                ),
                responseFieldValidate = responseFieldValidate,
                arguments = arguments
            )
        }
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    fun preExec(
        projectId: Long,
        projectServerId: Long,
        req: ProjectRequestPreExecRequest
    ): ProjectRequestPreExecResponse {
        // 读取 project-server
        val projectServer = projectServerRepository.findByProjectIdAndId(projectId, projectServerId)
            ?: throw ServiceException(msg = "要执行的项目服务器不存在")

        // 转换请求
        val fetchApi = BoomVO.FetchApi(
            name = req.request.name,
            url = projectServer.serverAddr + req.request.path,
            method = req.request.method,
            contentType = req.request.contentType,
            param = req.request.param,
            header = req.request.header,
            timeout = req.request.timeout
        )

        // 替换占位变量
        replaceParamBox.replaceArgAndEnvParams(
            fetchApi = fetchApi,
            args = req.arguments?.let {
                objectMapper.readValue(
                    it,
                    object : TypeReference<HashMap<String, String>>() {}
                )
            } ?: hashMapOf(),
            envs = req.env?.let {
                objectMapper.readValue(
                    it,
                    object : TypeReference<HashMap<String, String>>() {}
                )
            } ?: hashMapOf()
        )

        // 封装 http 请求
        val httpRequest = HttpRequest(
            url = fetchApi.url,
            method = fetchApi.method,
            contentType = fetchApi.contentType ?: ProjectRequest.ContentType.FORM,
            header = fetchApi.header?.let {
                objectMapper.readValue(it, object : TypeReference<HashMap<String, String>>() {})
            },
            param = fetchApi.param?.let {
                objectMapper.readValue(it, object : TypeReference<HashMap<String, Any>>() {})
            }
        )

        // 执行 http 请求
        val httpResponse = httpBox.doHttp("pre-exec-http-request", httpRequest)

        return ProjectRequestPreExecResponse(
            request = httpRequest,
            response = httpResponse
        )
    }
}
