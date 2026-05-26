package com.internalexam.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("api/questions")
    suspend fun createQuestion(
        @Header("Authorization") authorization: String,
        @Body request: QuestionCreateRequest
    ): ApiResponse<QuestionResponse>

    @POST("api/exams")
    suspend fun createExam(
        @Header("Authorization") authorization: String,
        @Body request: ExamCreateRequest
    ): ApiResponse<ExamResponse>

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
}
