package com.github.xiaosongfu.atp.repository.project

import com.github.xiaosongfu.atp.entity.project.ProjectServer
import org.springframework.data.repository.PagingAndSortingRepository

interface ProjectServerRepository : PagingAndSortingRepository<ProjectServer, Long> {
}
