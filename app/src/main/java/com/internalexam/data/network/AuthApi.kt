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

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("api/questions")
    suspend fun createQuestion(
        @Header("Authorization") authorization: String,
        @Body request: QuestionCreateRequest
    ): ApiResponse<QuestionResponse>

    @Multipart
    @POST("api/questions/import")
    suspend fun importQuestions(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<QuestionImportResponse>

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

    @GET("api/subjects/{subjectId}/topics")
    suspend fun getTopics(
        @Header("Authorization") authorization: String,
        @Path("subjectId") subjectId: Long
    ): ApiResponse<List<TopicResponse>>

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
}
