package com.internalexam.data.network

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {
    private val backendUrls = listOf(
        "http://10.0.2.2:8080/",
        "http://103.72.57.63:8080/",
        "http://127.0.0.1:8080/",
        "http://localhost:8080/"
    )

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val aiHttpClient = httpClient.newBuilder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .build()

    suspend fun login(request: LoginRequest): ApiResponse<LoginResponse> {
        return callBackend { it.login(request) }
    }

    suspend fun createQuestion(authorization: String, request: QuestionCreateRequest): ApiResponse<QuestionResponse> {
        return callBackend { it.createQuestion(authorization, request) }
    }

    suspend fun updateQuestion(
        authorization: String,
        questionId: Long,
        request: QuestionCreateRequest
    ): ApiResponse<QuestionResponse> {
        return callBackend { it.updateQuestion(authorization, questionId, request) }
    }

    suspend fun deleteQuestion(authorization: String, questionId: Long): ApiResponse<String> {
        return callBackend { it.deleteQuestion(authorization, questionId) }
    }

    suspend fun importQuestions(authorization: String, fileName: String, fileBytes: ByteArray): ApiResponse<QuestionImportResponse> {
        return callBackend {
            val body = fileBytes.toRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaType())
            val part = MultipartBody.Part.createFormData("file", fileName, body)
            it.importQuestions(authorization, part)
        }
    }

    suspend fun uploadFile(authorization: String, fileName: String, fileBytes: ByteArray, mimeType: String): ApiResponse<FileUploadResponse> {
        return callBackend {
            val body = fileBytes.toRequestBody(mimeType.toMediaType())
            val part = MultipartBody.Part.createFormData("file", fileName, body)
            it.uploadFile(authorization, part)
        }
    }

    suspend fun createExam(authorization: String, request: ExamCreateRequest): ApiResponse<ExamResponse> {
        return callBackend { it.createExam(authorization, request) }
    }

    suspend fun updateExam(authorization: String, examId: Long, request: ExamUpdateRequest): ApiResponse<ExamResponse> {
        return callBackend { it.updateExam(authorization, examId, request) }
    }

    suspend fun deleteExam(authorization: String, examId: Long): ApiResponse<String> {
        return callBackend { it.deleteExam(authorization, examId) }
    }

    suspend fun getExams(authorization: String): ApiResponse<List<ExamResponse>> {
        return callBackend { it.getExams(authorization) }
    }

    suspend fun getQuestions(authorization: String): ApiResponse<List<QuestionResponse>> {
        return callBackend { it.getQuestions(authorization) }
    }

    suspend fun getSubjects(authorization: String): ApiResponse<List<SubjectResponse>> {
        return callBackend { it.getSubjects(authorization) }
    }

    suspend fun createSubject(authorization: String, request: SubjectCreateRequest): ApiResponse<SubjectResponse> {
        return callBackend { it.createSubject(authorization, request) }
    }

    suspend fun getTopics(authorization: String, subjectId: Long): ApiResponse<List<TopicResponse>> {
        return callBackend { it.getTopics(authorization, subjectId) }
    }

    suspend fun createTopic(authorization: String, subjectId: Long, request: TopicCreateRequest): ApiResponse<TopicResponse> {
        return callBackend { it.createTopic(authorization, subjectId, request) }
    }

    suspend fun generateExam(authorization: String, request: ExamGenerateRequest): ApiResponse<ExamResponse> {
        return callBackend { it.generateExam(authorization, request) }
    }

    suspend fun submitExam(authorization: String, examId: Long, request: ExamSubmitRequest): ApiResponse<String> {
        return callBackend { it.submitExam(authorization, examId, request) }
    }

    suspend fun getResult(authorization: String, examId: Long): ApiResponse<ExamResultResponse> {
        return callBackend { it.getResult(authorization, examId) }
    }

    suspend fun getResultDetail(authorization: String, examId: Long): ApiResponse<ExamResultDetailResponse> {
        return callBackend { it.getResultDetail(authorization, examId) }
    }


    suspend fun getExamReport(authorization: String, examId: Long): ApiResponse<ExamReportResponse> {
        return callBackend { it.getExamReport(authorization, examId) }
    }

    suspend fun getExamQuestions(authorization: String, examId: Long): ApiResponse<List<ExamQuestionResponse>> {
        return callBackend { it.getExamQuestions(authorization, examId) }
    }

    suspend fun createQuestionForExam(
        authorization: String,
        examId: Long,
        request: ExamQuestionCreateRequest
    ): ApiResponse<ExamQuestionResponse> {
        return callBackend { it.createQuestionForExam(authorization, examId, request) }
    }

    suspend fun updateQuestionForExam(
        authorization: String,
        examId: Long,
        questionId: Long,
        request: ExamQuestionCreateRequest
    ): ApiResponse<ExamQuestionResponse> {
        return callBackend { it.updateQuestionForExam(authorization, examId, questionId, request) }
    }

    suspend fun getCurrentUser(authorization: String): ApiResponse<UserProfileResponse> {
        return callBackend { it.getCurrentUser(authorization) }
    }

    suspend fun getUsers(authorization: String): ApiResponse<List<UserProfileResponse>> {
        return callBackend { it.getUsers(authorization) }
    }

    suspend fun createUser(authorization: String, request: UserCreateRequest): ApiResponse<UserProfileResponse> {
        return callBackend { it.createUser(authorization, request) }
    }

    suspend fun lockUser(authorization: String, userId: Long): ApiResponse<UserProfileResponse> {
        return callBackend { it.lockUser(authorization, userId) }
    }

    suspend fun unlockUser(authorization: String, userId: Long): ApiResponse<UserProfileResponse> {
        return callBackend { it.unlockUser(authorization, userId) }
    }

    suspend fun updateUserRoles(authorization: String, userId: Long, request: UserRolesRequest): ApiResponse<UserProfileResponse> {
        return callBackend { it.updateUserRoles(authorization, userId, request) }
    }

    suspend fun getRoles(authorization: String): ApiResponse<List<RoleResponse>> {
        return callBackend { it.getRoles(authorization) }
    }

    suspend fun getPermissions(authorization: String): ApiResponse<List<PermissionResponse>> {
        return callBackend { it.getPermissions(authorization) }
    }

    suspend fun updateRolePermissions(
        authorization: String,
        roleId: Long,
        request: RolePermissionsUpdateRequest
    ): ApiResponse<RoleResponse> {
        return callBackend { it.updateRolePermissions(authorization, roleId, request) }
    }

    suspend fun getGroups(authorization: String): ApiResponse<List<StudentGroupResponse>> {
        return callBackend { it.getGroups(authorization) }
    }

    suspend fun createGroup(authorization: String, request: GroupCreateRequest): ApiResponse<StudentGroupResponse> {
        return callBackend { it.createGroup(authorization, request) }
    }

    suspend fun updateGroup(authorization: String, id: Long, request: GroupCreateRequest): ApiResponse<StudentGroupResponse> {
        return callBackend { it.updateGroup(authorization, id, request) }
    }

    suspend fun deleteGroup(authorization: String, id: Long): ApiResponse<String> {
        return callBackend { it.deleteGroup(authorization, id) }
    }

    suspend fun getGroupMembers(authorization: String, id: Long): ApiResponse<List<StudentGroupMemberResponse>> {
        return callBackend { it.getGroupMembers(authorization, id) }
    }

    suspend fun addGroupMember(authorization: String, groupId: Long, userIds: List<Long>): ApiResponse<List<StudentGroupMemberResponse>> {
        return callBackend { it.addGroupMember(authorization, groupId, AddGroupMemberRequest(userIds = userIds)) }
    }

    suspend fun removeGroupMember(authorization: String, groupId: Long, userId: Long): ApiResponse<String> {
        return callBackend { it.removeGroupMember(authorization, groupId, userId) }
    }

    suspend fun getAuditLogs(authorization: String): ApiResponse<List<AuditLogResponse>> {
        return callBackend { it.getAuditLogs(authorization) }
    }

    suspend fun createAuditLog(authorization: String, request: AuditLogCreateRequest): ApiResponse<AuditLogResponse> {
        return callBackend { it.createAuditLog(authorization, request) }
    }

    suspend fun createProctoringEvent(authorization: String, examId: Long, request: ProctoringEventRequest): ApiResponse<ProctoringEventResponse> {
        return callBackend { it.createProctoringEvent(authorization, examId, request) }
    }

    suspend fun getProctoringEvents(authorization: String, examId: Long): ApiResponse<List<ProctoringEventResponse>> {
        return callBackend { it.getProctoringEvents(authorization, examId) }
    }

    suspend fun getProctoringSummary(authorization: String, examId: Long): ApiResponse<ProctoringSummaryResponse> {
        return callBackend { it.getProctoringSummary(authorization, examId) }
    }

    suspend fun explainAnswer(authorization: String, request: AiExplainRequest): ApiResponse<AiExplainResponse> {
        return callBackend(aiHttpClient) { it.explainAnswer(authorization, request) }
    }
    private suspend fun <T> callBackend(client: OkHttpClient = httpClient, block: suspend (AuthApi) -> T): T {
        var lastException: IOException? = null

        for (baseUrl in backendUrls) {
            try {
                return block(createAuthApi(baseUrl, client))
            } catch (exception: IOException) {
                lastException = exception
            }
        }

        throw lastException ?: IOException("Cannot connect to backend")
    }

    private fun createAuthApi(baseUrl: String, client: OkHttpClient = httpClient): AuthApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}
