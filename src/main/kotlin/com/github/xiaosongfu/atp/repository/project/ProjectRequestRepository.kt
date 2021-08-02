package com.github.xiaosongfu.atp.repository.project

import com.github.xiaosongfu.atp.entity.project.ProjectRequest
import org.springframework.data.repository.PagingAndSortingRepository

interface ProjectRequestRepository : PagingAndSortingRepository<ProjectRequest, Long> {
    fun findAllByProjectId(projectId: Long): List<ProjectRequest>?
}
