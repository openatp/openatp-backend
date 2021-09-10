package com.github.xiaosongfu.atp.repository.project

import com.github.xiaosongfu.atp.entity.project.ProjectRequestArgument
import org.springframework.data.repository.PagingAndSortingRepository

interface ProjectRequestArgumentRepository : PagingAndSortingRepository<ProjectRequestArgument, Long> {
    fun findAllByRequestId(requestId: Long): List<ProjectRequestArgument>?
    fun deleteAllByRequestId(requestId: Long)
}
