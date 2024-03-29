package com.github.xiaosongfu.atp.repository.project

import com.github.xiaosongfu.atp.entity.project.Project
import org.springframework.data.repository.PagingAndSortingRepository

interface ProjectRepository : PagingAndSortingRepository<Project, Long> {
}
