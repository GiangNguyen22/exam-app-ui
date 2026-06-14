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
    val tokenType: String,
    val roles: List<String>?
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

data class SubjectResponse(
    val id: Long,
    val name: String,
    val description: String?
)

data class TopicResponse(
    val id: Long,
    val subjectId: Long,
    val name: String,
    val description: String?
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

data class ExamUpdateRequest(
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
    val scorePerQuestion: String,
    val subjectId: Long,
    val topicId: Long?,
    val easyCount: Int,
    val mediumCount: Int,
    val hardCount: Int
)

data class ExamResponse(
    val id: Long,
    val code: String,
    val title: String,
    val durationMinutes: Int?,
    val scorePerQuestion: String?,
    val startTime: String?,
    val endTime: String?,
    val shuffleQuestions: Boolean?,
    val shuffleAnswers: Boolean?,
    val subject: String? = null,
    val subjectName: String? = null,
    val questionCount: Int? = null,
    val totalQuestions: Int? = null
)

data class ExamSubmitRequest(
    val note: String,
    val answers: List<ExamAnswerSubmitRequest> = emptyList()
)

data class ExamAnswerSubmitRequest(
    val questionId: Long,
    val selectedAnswerIds: List<Long>,
    val fillContent: String? = null
)

data class ExamResultResponse(
    val resultId: Long,
    val examId: Long,
    val score: String?,
    val status: String,
    val submittedAt: String?
)

data class ExamReportResponse(
    val examId: Long,
    val examCode: String?,
    val examTitle: String?,
    val totalResults: Int,
    val submittedCount: Int,
    val doingCount: Int,
    val averageScore: String?,
    val highestScore: String?,
    val lowestScore: String?,
    val results: List<ExamReportItemResponse>?
)

data class ExamReportItemResponse(
    val resultId: Long,
    val studentId: Long,
    val studentName: String?,
    val username: String?,
    val score: String?,
    val status: String,
    val startedAt: String?,
    val submittedAt: String?
)

data class ExamQuestionResponse(
    val examQuestionId: Long,
    val questionId: Long,
    val orderIndex: Int?,
    val score: String?,
    val subjectId: Long?,
    val topicId: Long?,
    val content: String,
    val type: String?,
    val difficulty: String?,
    val answers: List<AnswerOptionResponse>?
)

data class AnswerOptionResponse(
    val id: Long,
    val content: String,
    val correct: Boolean?,
    val explanation: String?
)

data class ExamQuestionCreateRequest(
    val subjectId: Long,
    val topicId: Long?,
    val content: String,
    val type: String,
    val difficulty: String,
    val orderIndex: Int?,
    val score: String?,
    val answers: List<AnswerCreateRequest>
)

data class AnswerCreateRequest(
    val content: String,
    val correct: Boolean,
    val explanation: String?
)

data class UserProfileResponse(
    val id: Long,
    val username: String,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val studentId: String?,
    val employeeCode: String?,
    val status: String?,
    val roles: List<String>?
)

data class QuestionImportErrorResponse(
    val rowNumber: Int,
    val questionKey: String?,
    val field: String,
    val message: String
)

data class QuestionImportResponse(
    val success: Boolean,
    val totalGroups: Int,
    val importedQuestions: Int,
    val importedAnswers: Int,
    val errors: List<QuestionImportErrorResponse>?
)
