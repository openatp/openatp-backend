package com.github.xiaosongfu.atp.service.project

import com.github.xiaosongfu.atp.domain.dto.project.ProjectRequestFindResponse
import com.github.xiaosongfu.atp.domain.dto.project.ProjectRequestInsertRequest
import com.github.xiaosongfu.atp.domain.vo.project.ProjectRequestResponseVO
import com.github.xiaosongfu.atp.domain.vo.project.ProjectRequestVO
import com.github.xiaosongfu.atp.entity.project.ProjectRequest
import com.github.xiaosongfu.atp.entity.project.ProjectRequestResponse
import com.github.xiaosongfu.atp.repository.project.ProjectRequestRepository
import com.github.xiaosongfu.atp.repository.project.ProjectRequestResponseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectRequestService {
    @Autowired
    private lateinit var projectRequestRepository: ProjectRequestRepository

    @Autowired
    private lateinit var projectRequestResponseRepository: ProjectRequestResponseRepository

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
                responseFieldValidate = responseFieldValidate
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
                responseFieldValidate = responseFieldValidate
            )
        }
    }
}
