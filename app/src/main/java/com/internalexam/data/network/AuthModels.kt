package com.internalexam.data.network

data class LoginRequest(
    val username: String,
    val password: String
)

data class ApiResponse<T>(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: T?
)

data class LoginResponse(
    val accessToken: String,
    val tokenType: String
)

data class QuestionCreateRequest(
    val subjectId: Long,
    val topicId: Long?,
    val content: String,
    val type: String,
    val difficulty: String
)

data class QuestionResponse(
    val id: Long,
    val content: String
)

data class ExamCreateRequest(
    val title: String,
    val durationMinutes: Int,
    val scorePerQuestion: String,
    val startTime: String?,
    val endTime: String?,
    val shuffleQuestions: Boolean,
    val shuffleAnswers: Boolean
)

data class ExamGenerateRequest(
    val title: String,
    val durationMinutes: Int,
    val scorePerQuestion: String
)

data class ExamResponse(
    val id: Long,
    val code: String,
    val title: String
)

data class ExamSubmitRequest(
    val note: String
)

data class ExamResultResponse(
    val resultId: Long,
    val examId: Long,
    val score: String?,
    val status: String,
    val submittedAt: String?
)
