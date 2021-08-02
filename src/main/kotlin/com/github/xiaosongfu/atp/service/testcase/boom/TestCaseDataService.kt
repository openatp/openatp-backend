package com.github.xiaosongfu.atp.service.testcase.boom

import com.github.xiaosongfu.atp.domain.vo.boom.BoomVO
import com.github.xiaosongfu.atp.entity.testcase.TestCase
import com.github.xiaosongfu.atp.repository.project.ProjectEnvVariableRepository
import com.github.xiaosongfu.atp.repository.project.ProjectRequestRepository
import com.github.xiaosongfu.atp.repository.project.ProjectRequestResponseRepository
import com.github.xiaosongfu.atp.repository.project.ProjectServerRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestExecCheckRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestRepository
import com.github.xiaosongfu.atp.repository.testcase.TestCaseRequestSaveEnvVariableRepository
import com.github.xiaosongfu.jakarta.exception.service.ServiceException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TestCaseDataService {

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
    // --- --- Project Detail

    @Autowired
    private lateinit var projectServerRepository: ProjectServerRepository

    @Autowired
    private lateinit var projectEnvVariableRepository: ProjectEnvVariableRepository

    @Autowired
    private lateinit var projectRequestRepository: ProjectRequestRepository

    @Autowired
    private lateinit var projectRequestResponseRepository: ProjectRequestResponseRepository

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
    // --- --- Test Case

    @Autowired
    private lateinit var testCaseRepository: TestCaseRepository

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
    // --- --- Test Case Detail

    @Autowired
    private lateinit var testCaseRequestRepository: TestCaseRequestRepository

    @Autowired
    private lateinit var testCaseRequestExecCheckRepository: TestCaseRequestExecCheckRepository

    @Autowired
    private lateinit var testCaseRequestSaveEnvVariableRepository: TestCaseRequestSaveEnvVariableRepository

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---

    // 封装数据给 BoomService 执行用
    fun readTestCaseFullData(projectId: Long, projectServerId: Long, testCaseId: Long): BoomVO? {
        return testCaseRepository.findByIdOrNull(testCaseId)?.let { testCase ->
            // p1 读取 project-server project-env-variable 的数据
            val projectServer = projectServerRepository.findByProjectIdAndId(projectId, projectServerId)
                ?: throw ServiceException(msg = "要执行的项目服务器不存在")
            val projectEnvVariableList = projectEnvVariableRepository.findAllByProjectId(projectId)

            testCaseRequestRepository.findAllByTestCaseId(testCaseId)?.let { testCaseRequests ->
                when (testCase.type) {
                    // 1
                    TestCase.TEST_CASE_TYPE_BENCHMARK -> {
                        // benchmark 只需要一个请求
                        val testCaseRequest = testCaseRequests.first()

                        // p2 读取 project-request project-request-response 的数据
                        val projectRequest = projectRequestRepository.findByIdOrNull(testCaseRequest.projectRequestId)
                            ?: throw ServiceException(msg = "要执行的项目请求不存在")
                        val projectRequestResponseList =
                            projectRequestResponseRepository.findAllByRequestId(testCaseRequest.projectRequestId)

                        // ExecCheck
                        val execCheck =
                            testCaseRequestExecCheckRepository.findAllByTestCaseRequestId(testCaseRequest.id)
                                ?.mapNotNull { check ->
                                    projectRequestResponseList?.firstOrNull { it.id == check.projectRequestResponseId }
                                        ?.let { resp ->
                                            BoomVO.ExecCheck(
                                                fieldName = resp.fieldName,
                                                fieldPath = resp.fieldPath,
                                                wantFieldValue = check.wantResponseFieldValue
                                            )
                                        }
                                }

                        // SaveEnvVariable
                        val saveEnvVariable =
                            testCaseRequestSaveEnvVariableRepository.findAllByTestCaseRequestId(testCaseRequest.id)
                                ?.mapNotNull { saveEnvVar ->
                                    projectEnvVariableList?.firstOrNull { it.id == saveEnvVar.projectEnvVariableId }
                                        ?.let { envVar ->
                                            BoomVO.SaveEnvVariable(
                                                variableName = envVar.variableName,
                                                defaultValue = envVar.defaultValue,
                                                projectEnvVariableValuePath = saveEnvVar.projectEnvVariableValuePath
                                            )
                                        }
                                }

                        // benchmark 封装
                        val benchmark = BoomVO.Benchmark(
                            fetchApi = BoomVO.FetchApi(
                                name = projectRequest.name,
                                url = projectServer.baseUrl + projectRequest.path,
                                method = projectRequest.method,
                                contentType = projectRequest.contentType,
                                param = projectRequest.param,
                                header = projectRequest.header,
                                timeout = projectRequest.timeout
                            ),
                            request = BoomVO.Request(
                                name = testCaseRequest.name,
                                param = testCaseRequest.param
                            ),
                            execCheck = execCheck,
                            saveEnvVariable = saveEnvVariable
                        )

                        // 返回结果
                        BoomVO(
                            name = testCase.name,
                            type = testCase.type,
                            benchmark = benchmark
                        )
                    }
                    // 2
                    TestCase.TEST_CASE_TYPE_REPLAY -> {
                        // replay 所有请求都是调用同一个接口 !!!
                        val theSameTestCaseRequest = testCaseRequests.first()

                        // --1 replay 所有请求都是调用同一个接口，所以下面的数据只需要一份
                        // p2 读取 project-request project-request-response 的数据
                        val projectRequest =
                            projectRequestRepository.findByIdOrNull(theSameTestCaseRequest.projectRequestId)
                                ?: throw ServiceException(msg = "要执行的项目请求不存在")
                        val projectRequestResponseList =
                            projectRequestResponseRepository.findAllByRequestId(theSameTestCaseRequest.projectRequestId)

                        // --2 replay 所有请求都是调用同一个接口，所以 ExecCheck 只需要一份
                        val execCheck =
                            testCaseRequestExecCheckRepository.findAllByTestCaseRequestId(theSameTestCaseRequest.id)
                                ?.mapNotNull { check ->
                                    projectRequestResponseList?.firstOrNull { it.id == check.projectRequestResponseId }
                                        ?.let { resp ->
                                            BoomVO.ExecCheck(
                                                fieldName = resp.fieldName,
                                                fieldPath = resp.fieldPath,
                                                wantFieldValue = check.wantResponseFieldValue
                                            )
                                        }
                                }

                        // --3 replay 所有请求都是调用同一个接口，所以 SaveEnvVariable 只需要一份
                        val saveEnvVariable =
                            testCaseRequestSaveEnvVariableRepository.findAllByTestCaseRequestId(theSameTestCaseRequest.id)
                                ?.mapNotNull { saveEnvVar ->
                                    projectEnvVariableList?.firstOrNull { it.id == saveEnvVar.projectEnvVariableId }
                                        ?.let { envVar ->
                                            BoomVO.SaveEnvVariable(
                                                variableName = envVar.variableName,
                                                defaultValue = envVar.defaultValue,
                                                projectEnvVariableValuePath = saveEnvVar.projectEnvVariableValuePath
                                            )
                                        }
                                }

                        // 遍历所以请求
                        val requests = testCaseRequests.map { testCaseRequest ->
                            // 单个请求封装
                            BoomVO.Replay.Bundle(
                                request = BoomVO.Request(
                                    name = testCaseRequest.name,
                                    param = testCaseRequest.param
                                ),
                                execCheck = execCheck,
                                saveEnvVariable = saveEnvVariable
                            )
                        }

                        // replay 封装
                        val replay = BoomVO.Replay(
                            fetchApi = BoomVO.FetchApi(
                                name = projectRequest.name,
                                url = projectServer.baseUrl + projectRequest.path,
                                method = projectRequest.method,
                                contentType = projectRequest.contentType,
                                param = projectRequest.param,
                                header = projectRequest.header,
                                timeout = projectRequest.timeout
                            ),
                            requests = requests
                        )

                        // 返回结果
                        BoomVO(
                            name = testCase.name,
                            type = testCase.type,
                            replay = replay
                        )
                    }
                    // 3
                    TestCase.TEST_CASE_TYPE_PIPELINE -> {
                        // 遍历所以请求
                        val requests = testCaseRequests.map { testCaseRequest ->
                            // p2 读取 project-request project-request-response 的数据
                            val projectRequest =
                                projectRequestRepository.findByIdOrNull(testCaseRequest.projectRequestId)
                                    ?: throw ServiceException(msg = "要执行的项目请求不存在")
                            val projectRequestResponseList =
                                projectRequestResponseRepository.findAllByRequestId(testCaseRequest.projectRequestId)

                            // ExecCheck
                            val execCheck =
                                testCaseRequestExecCheckRepository.findAllByTestCaseRequestId(testCaseRequest.id)
                                    ?.mapNotNull { check ->
                                        projectRequestResponseList?.firstOrNull { it.id == check.projectRequestResponseId }
                                            ?.let { resp ->
                                                BoomVO.ExecCheck(
                                                    fieldName = resp.fieldName,
                                                    fieldPath = resp.fieldPath,
                                                    wantFieldValue = check.wantResponseFieldValue
                                                )
                                            }
                                    }

                            // SaveEnvVariable
                            val saveEnvVariable =
                                testCaseRequestSaveEnvVariableRepository.findAllByTestCaseRequestId(testCaseRequest.id)
                                    ?.mapNotNull { saveEnvVar ->
                                        projectEnvVariableList?.firstOrNull { it.id == saveEnvVar.projectEnvVariableId }
                                            ?.let { envVar ->
                                                BoomVO.SaveEnvVariable(
                                                    variableName = envVar.variableName,
                                                    defaultValue = envVar.defaultValue,
                                                    projectEnvVariableValuePath = saveEnvVar.projectEnvVariableValuePath
                                                )
                                            }
                                    }

                            // 单个请求封装
                            BoomVO.Pipeline.Bundle(
                                fetchApi = BoomVO.FetchApi(
                                    name = projectRequest.name,
                                    url = projectServer.baseUrl + projectRequest.path,
                                    method = projectRequest.method,
                                    contentType = projectRequest.contentType,
                                    param = projectRequest.param,
                                    header = projectRequest.header,
                                    timeout = projectRequest.timeout
                                ),
                                request = BoomVO.Request(
                                    name = testCaseRequest.name,
                                    param = testCaseRequest.param
                                ),
                                execCheck = execCheck,
                                saveEnvVariable = saveEnvVariable
                            )
                        }

                        // 返回结果
                        BoomVO(
                            name = testCase.name,
                            type = testCase.type,
                            pipeline = BoomVO.Pipeline(
                                requests = requests
                            )
                        )
                    }
                    else -> {
                        null
                    }
                }
            }
        }
    }
}
