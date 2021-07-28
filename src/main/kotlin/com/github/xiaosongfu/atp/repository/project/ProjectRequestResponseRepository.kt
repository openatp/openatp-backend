package com.github.xiaosongfu.atp.repository.project

import com.github.xiaosongfu.atp.entity.project.ProjectRequestResponse
import org.springframework.data.repository.PagingAndSortingRepository

interface ProjectRequestResponseRepository : PagingAndSortingRepository<ProjectRequestResponse, Long> {
    fun findAllByRequestId(requestId: Long): List<ProjectRequestResponse>?
    fun deleteAllByRequestId(requestId: Long)
}
