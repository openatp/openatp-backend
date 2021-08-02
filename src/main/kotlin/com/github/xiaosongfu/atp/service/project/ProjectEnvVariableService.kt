package com.github.xiaosongfu.atp.service.project

import com.github.xiaosongfu.atp.domain.dto.project.ProjectEnvVariableInsertRequest
import com.github.xiaosongfu.atp.entity.project.ProjectEnvVariable
import com.github.xiaosongfu.atp.repository.project.ProjectEnvVariableRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProjectEnvVariableService {
    @Autowired
    private lateinit var projectEnvVariableRepository: ProjectEnvVariableRepository

    fun insert(projectId: Long, req: ProjectEnvVariableInsertRequest) {
        projectEnvVariableRepository.save(
            ProjectEnvVariable(
                projectId = projectId,
                variableName = req.variableName,
                defaultValue = req.defaultValue
            )
        )
    }

    fun delete(projectEnvVariableId: Long) {
        projectEnvVariableRepository.deleteById(projectEnvVariableId)
    }

    fun update(projectEnvVariableId: Long, req: ProjectEnvVariableInsertRequest) {
        projectEnvVariableRepository.findByIdOrNull(projectEnvVariableId)?.apply {
            variableName = req.variableName
            defaultValue = req.defaultValue
        }?.let {
            projectEnvVariableRepository.save(it)
        }
    }

//    fun find(projectEnvVariableId: Long): ProjectEnvVariable? {
//        return projectEnvVariableRepository.findByIdOrNull(projectEnvVariableId)
//    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    fun all(projectId: Long): List<ProjectEnvVariable>? {
        return projectEnvVariableRepository.findAllByProjectId(projectId)
    }
}