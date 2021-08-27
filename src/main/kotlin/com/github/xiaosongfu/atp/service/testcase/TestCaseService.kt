package com.github.xiaosongfu.atp.service.testcase

import com.github.xiaosongfu.atp.domain.dto.testcase.TestCaseInsertRequest
import com.github.xiaosongfu.atp.entity.testcase.TestCase
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TestCaseService {
    @Autowired
    private lateinit var testCaseRepository: TestCaseRepository

    fun insert(projectId: Long, req: TestCaseInsertRequest) {
        testCaseRepository.save(
            TestCase(
                projectId = projectId,
                name = req.name,
                type = req.type,
                projectRequestId =  req.projectRequestId
            )
        )
    }

    fun delete(testCaseId: Long) {
        testCaseRepository.deleteById(testCaseId)
    }

    fun update(testCaseId: Long, req: TestCaseInsertRequest) {
        testCaseRepository.findByIdOrNull(testCaseId)?.apply {
            name = req.name
            type = req.type
            projectRequestId = projectRequestId
            // 更新测试案例的时不可以更新 projectRequestId 属性，因为不允许修改测试案例关联的请求
        }?.let {
            testCaseRepository.save(it)
        }
    }

//    fun find(testCaseId: Long): TestCase? {
//        return testCaseRepository.findByIdOrNull(testCaseId)
//    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    fun list(projectId: Long): List<TestCase>? {
        return testCaseRepository.findAllByProjectId(projectId)
    }
}