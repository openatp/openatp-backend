package com.github.xiaosongfu.atp.repository.project

import com.github.xiaosongfu.atp.entity.project.ProjectServer
import org.springframework.data.repository.PagingAndSortingRepository

interface ProjectServerRepository : PagingAndSortingRepository<ProjectServer, Long> {
    fun findAllByProjectId(projectId: Long): List<ProjectServer>?

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
    fun findByProjectIdAndId(projectId: Long, id: Long): ProjectServer?
}
