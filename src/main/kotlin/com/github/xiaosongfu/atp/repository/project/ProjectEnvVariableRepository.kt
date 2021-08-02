package com.github.xiaosongfu.atp.repository.project

import com.github.xiaosongfu.atp.entity.project.ProjectEnvVariable
import org.springframework.data.repository.PagingAndSortingRepository

interface ProjectEnvVariableRepository : PagingAndSortingRepository<ProjectEnvVariable, Long> {
    fun findAllByProjectId(projectId: Long): List<ProjectEnvVariable>?

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
}
