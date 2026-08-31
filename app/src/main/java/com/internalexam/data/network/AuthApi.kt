package com.internalexam.data.network

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("api/questions")
    suspend fun createQuestion(
        @Header("Authorization") authorization: String,
        @Body request: QuestionCreateRequest
    ): ApiResponse<QuestionResponse>

    @PUT("api/questions/{questionId}")
    suspend fun updateQuestion(
        @Header("Authorization") authorization: String,
        @Path("questionId") questionId: Long,
        @Body request: QuestionCreateRequest
    ): ApiResponse<QuestionResponse>

    @DELETE("api/questions/{questionId}")
    suspend fun deleteQuestion(
        @Header("Authorization") authorization: String,
        @Path("questionId") questionId: Long
    ): ApiResponse<String>

    @Multipart
    @POST("api/questions/import")
    suspend fun importQuestions(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<QuestionImportResponse>

    @Multipart
    @POST("api/files/upload")
    suspend fun uploadFile(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<FileUploadResponse>

    @POST("api/exams")
    suspend fun createExam(
        @Header("Authorization") authorization: String,
        @Body request: ExamCreateRequest
    ): ApiResponse<ExamResponse>

    @PUT("api/exams/{examId}")
    suspend fun updateExam(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long,
        @Body request: ExamUpdateRequest
    ): ApiResponse<ExamResponse>

    @DELETE("api/exams/{examId}")
    suspend fun deleteExam(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long
    ): ApiResponse<String>

    @GET("api/exams")
    suspend fun getExams(
        @Header("Authorization") authorization: String
    ): ApiResponse<List<ExamResponse>>

    @GET("api/questions")
    suspend fun getQuestions(
        @Header("Authorization") authorization: String
    ): ApiResponse<List<QuestionResponse>>

    @GET("api/subjects")
    suspend fun getSubjects(
        @Header("Authorization") authorization: String
    ): ApiResponse<List<SubjectResponse>>

    @POST("api/subjects")
    suspend fun createSubject(
        @Header("Authorization") authorization: String,
        @Body request: SubjectCreateRequest
    ): ApiResponse<SubjectResponse>

    @GET("api/subjects/{subjectId}/topics")
    suspend fun getTopics(
        @Header("Authorization") authorization: String,
        @Path("subjectId") subjectId: Long
    ): ApiResponse<List<TopicResponse>>

    @POST("api/subjects/{subjectId}/topics")
    suspend fun createTopic(
        @Header("Authorization") authorization: String,
        @Path("subjectId") subjectId: Long,
        @Body request: TopicCreateRequest
    ): ApiResponse<TopicResponse>

    @POST("api/exams/generate")
    suspend fun generateExam(
        @Header("Authorization") authorization: String,
        @Body request: ExamGenerateRequest
    ): ApiResponse<ExamResponse>

    @POST("api/exams/{examId}/submit")
    suspend fun submitExam(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long,
        @Body request: ExamSubmitRequest
    ): ApiResponse<String>

    @GET("api/results/{examId}")
    suspend fun getResult(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long
    ): ApiResponse<ExamResultResponse>

    @GET("api/results/{examId}/detail")
    suspend fun getResultDetail(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long
    ): ApiResponse<ExamResultDetailResponse>

    @GET("api/results/exams/{examId}/report")
    suspend fun getExamReport(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long
    ): ApiResponse<ExamReportResponse>

    @GET("api/exams/{examId}/questions")
    suspend fun getExamQuestions(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long
    ): ApiResponse<List<ExamQuestionResponse>>

    @POST("api/exams/{examId}/questions")
    suspend fun createQuestionForExam(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long,
        @Body request: ExamQuestionCreateRequest
    ): ApiResponse<ExamQuestionResponse>

    @PUT("api/exams/{examId}/questions/{questionId}")
    suspend fun updateQuestionForExam(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long,
        @Path("questionId") questionId: Long,
        @Body request: ExamQuestionCreateRequest
    ): ApiResponse<ExamQuestionResponse>

    @GET("api/users/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): ApiResponse<UserProfileResponse>

    @GET("api/users")
    suspend fun getUsers(
        @Header("Authorization") authorization: String
    ): ApiResponse<List<UserProfileResponse>>

    @POST("api/users")
    suspend fun createUser(
        @Header("Authorization") authorization: String,
        @Body request: UserCreateRequest
    ): ApiResponse<UserProfileResponse>

    @PUT("api/users/{userId}/lock")
    suspend fun lockUser(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: Long
    ): ApiResponse<UserProfileResponse>

    @PUT("api/users/{userId}/unlock")
    suspend fun unlockUser(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: Long
    ): ApiResponse<UserProfileResponse>

    @PUT("api/users/{userId}/roles")
    suspend fun updateUserRoles(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: Long,
        @Body request: UserRolesRequest
    ): ApiResponse<UserProfileResponse>

    @GET("api/rbac/roles")
    suspend fun getRoles(
        @Header("Authorization") authorization: String
    ): ApiResponse<List<RoleResponse>>

    @GET("api/rbac/permissions")
    suspend fun getPermissions(
        @Header("Authorization") authorization: String
    ): ApiResponse<List<PermissionResponse>>

    @PUT("api/rbac/roles/{roleId}/permissions")
    suspend fun updateRolePermissions(
        @Header("Authorization") authorization: String,
        @Path("roleId") roleId: Long,
        @Body request: RolePermissionsUpdateRequest
    ): ApiResponse<RoleResponse>

    @GET("api/groups")
    suspend fun getGroups(
        @Header("Authorization") authorization: String
    ): ApiResponse<List<StudentGroupResponse>>

    @POST("api/groups")
    suspend fun createGroup(
        @Header("Authorization") authorization: String,
        @Body request: GroupCreateRequest
    ): ApiResponse<StudentGroupResponse>

    @PUT("api/groups/{id}")
    suspend fun updateGroup(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,
        @Body request: GroupCreateRequest
    ): ApiResponse<StudentGroupResponse>

    @DELETE("api/groups/{id}")
    suspend fun deleteGroup(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): ApiResponse<String>

    @GET("api/groups/{id}/members")
    suspend fun getGroupMembers(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): ApiResponse<List<StudentGroupMemberResponse>>

    @POST("api/groups/{id}/members")
    suspend fun addGroupMember(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,
        @Body request: AddGroupMemberRequest
    ): ApiResponse<List<StudentGroupMemberResponse>>

    @DELETE("api/groups/{id}/members/{userId}")
    suspend fun removeGroupMember(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,
        @Path("userId") userId: Long
    ): ApiResponse<String>

    @GET("api/audit-logs")
    suspend fun getAuditLogs(
        @Header("Authorization") authorization: String
    ): ApiResponse<List<AuditLogResponse>>

    @POST("api/audit-logs")
    suspend fun createAuditLog(
        @Header("Authorization") authorization: String,
        @Body request: AuditLogCreateRequest
    ): ApiResponse<AuditLogResponse>

    @POST("api/exams/{examId}/proctoring-events")
    suspend fun createProctoringEvent(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long,
        @Body request: ProctoringEventRequest
    ): ApiResponse<ProctoringEventResponse>

    @GET("api/exams/{examId}/proctoring-events")
    suspend fun getProctoringEvents(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long
    ): ApiResponse<List<ProctoringEventResponse>>

    @GET("api/exams/{examId}/proctoring-events/summary")
    suspend fun getProctoringSummary(
        @Header("Authorization") authorization: String,
        @Path("examId") examId: Long
    ): ApiResponse<ProctoringSummaryResponse>

    @POST("api/ai/explain")
    suspend fun explainAnswer(
        @Header("Authorization") authorization: String,
        @Body request: AiExplainRequest
    ): ApiResponse<AiExplainResponse>

}
