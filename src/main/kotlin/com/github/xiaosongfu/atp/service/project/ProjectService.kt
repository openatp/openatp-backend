package com.github.xiaosongfu.atp.service.project

import com.github.xiaosongfu.atp.domain.dto.project.ProjectInsertRequest
import com.github.xiaosongfu.atp.entity.project.Project
import com.github.xiaosongfu.atp.repository.project.ProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProjectService {
    @Autowired
    private lateinit var projectRepository: ProjectRepository

    fun insert(req: ProjectInsertRequest) {
        projectRepository.save(
            Project(
                name = req.name,
                desc = req.desc
            )
        )
    }

    fun delete(projectId: Long) {
        projectRepository.deleteById(projectId)
    }

    fun update(projectId: Long, req: ProjectInsertRequest) {
        projectRepository.findByIdOrNull(projectId)?.apply {
            name = req.name
            desc = req.desc
        }?.let {
            projectRepository.save(it)
        }
    }

    fun find(projectId: Long): Project? {
        return projectRepository.findByIdOrNull(projectId)
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    // 分页
    fun list(): List<Project>? {
        return projectRepository.findAll().toList()
    }
}
