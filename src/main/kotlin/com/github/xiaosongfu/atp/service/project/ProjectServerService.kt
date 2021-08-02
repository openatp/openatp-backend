package com.github.xiaosongfu.atp.service.project

import com.github.xiaosongfu.atp.domain.dto.project.ProjectServerInsertRequest
import com.github.xiaosongfu.atp.entity.project.ProjectServer
import com.github.xiaosongfu.atp.repository.project.ProjectServerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProjectServerService {
    @Autowired
    private lateinit var projectServerRepository: ProjectServerRepository

    fun insert(projectId: Long, req: ProjectServerInsertRequest) {
        projectServerRepository.save(
            ProjectServer(
                projectId = projectId,
                envName = req.envName,
                baseUrl = req.baseUrl
            )
        )
    }

    fun delete(projectServerId: Long) {
        projectServerRepository.deleteById(projectServerId)
    }

    fun update(projectServerId: Long, req: ProjectServerInsertRequest) {
        projectServerRepository.findByIdOrNull(projectServerId)?.apply {
            envName = req.envName
            baseUrl = req.baseUrl
        }?.let {
            projectServerRepository.save(it)
        }
    }

//    fun find(projectServerId: Long): ProjectServer? {
//        return projectServerRepository.findByIdOrNull(projectServerId)
//    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    fun all(projectId: Long): List<ProjectServer>? {
        return projectServerRepository.findAllByProjectId(projectId)
    }
}
