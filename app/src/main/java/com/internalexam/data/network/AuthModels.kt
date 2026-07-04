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
    val difficulty: String,
    val imageUrl: String?,
    val answers: List<AnswerCreateRequest>
)

data class QuestionResponse(
    val id: Long,
    val subjectId: Long?,
    val subjectName: String?,
    val topicId: Long?,
    val topicName: String?,
    val content: String,
    val imageUrl: String?,
    val type: String?,
    val difficulty: String?,
    val answers: List<AnswerOptionResponse>?
)

data class SubjectCreateRequest(
    val name: String,
    val description: String? = null
)

data class SubjectResponse(
    val id: Long,
    val name: String,
    val description: String?
)

data class TopicCreateRequest(
    val name: String,
    val description: String? = null
)

data class TopicResponse(
    val id: Long,
    val subjectId: Long,
    val name: String,
    val description: String?
)

data class StudentGroupResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val memberCount: Int? = null
)

data class AddGroupMemberRequest(
    val userIds: List<Long>
)

data class StudentGroupMemberResponse(
    val id: Long,
    val groupId: Long,
    val userId: Long,
    val username: String?,
    val fullName: String?
)

data class GroupCreateRequest(
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
    val shuffleAnswers: Boolean,
    val groupIds: List<Long>? = null
)

data class ExamUpdateRequest(
    val title: String,
    val durationMinutes: Int,
    val scorePerQuestion: String,
    val startTime: String?,
    val endTime: String?,
    val shuffleQuestions: Boolean,
    val shuffleAnswers: Boolean,
    val groupIds: List<Long>? = null
)

data class ExamGenerateRequest(
    val title: String,
    val durationMinutes: Int,
    val scorePerQuestion: String,
    val subjectId: Long,
    val topicId: Long?,
    val easyCount: Int,
    val mediumCount: Int,
    val hardCount: Int,
    val startTime: String?,
    val endTime: String?,
    val groupIds: List<Long>? = null
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
    val totalQuestions: Int? = null,
    val groupIds: List<Long>? = null
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

data class ExamResultDetailResponse(
    val resultId: Long,
    val examId: Long,
    val examTitle: String?,
    val score: String?,
    val status: String,
    val submittedAt: String?,
    val totalQuestions: Int?,
    val correctCount: Int?,
    val wrongCount: Int?,
    val blankCount: Int?,
    val questions: List<ResultQuestionResponse>?
)

data class ResultQuestionResponse(
    val questionId: Long,
    val orderIndex: Int?,
    val content: String,
    val type: String?,
    val correct: Boolean?,
    val blank: Boolean?,
    val selectedAnswerIds: List<Long>?,
    val fillContent: String?,
    val answers: List<AnswerOptionResponse>?
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
    val studentCode: String?,
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
    val imageUrl: String?,
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
    val imageUrl: String?,
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

data class UserCreateRequest(
    val username: String,
    val password: String,
    val fullName: String,
    val email: String? = null,
    val phone: String? = null,
    val studentId: String? = null,
    val employeeCode: String? = null,
    val status: String = "ACTIVE",
    val roles: List<String> = emptyList()
)

data class UserRolesRequest(
    val roles: List<String>
)

data class PermissionResponse(
    val id: Long,
    val name: String,
    val description: String?
)

data class RoleResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val permissions: List<String>?
)

data class RolePermissionsUpdateRequest(
    val permissions: List<String>
)

data class AuditLogCreateRequest(
    val action: String,
    val resourceType: String?,
    val resourceId: Long?,
    val reason: String?
)

data class AuditLogResponse(
    val id: Long,
    val userId: Long?,
    val username: String?,
    val action: String,
    val resourceType: String?,
    val resourceId: Long?,
    val result: String?,
    val reason: String?,
    val createdAt: String?
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

data class FileUploadResponse(
    val fileName: String,
    val url: String
)

data class ProctoringEventRequest(
    val eventType: String,
    val details: String?
)

data class ProctoringEventResponse(
    val id: Long,
    val examId: Long,
    val studentId: Long,
    val studentName: String?,
    val username: String?,
    val eventType: String,
    val details: String?,
    val createdAt: String?
)

data class ProctoringSummaryResponse(
    val students: List<StudentProctoringStatus>?
)

data class StudentProctoringStatus(
    val studentId: Long,
    val studentName: String?,
    val username: String?,
    val latestEventType: String?,
    val latestDetails: String?,
    val latestEventAt: String?,
    val hasAlert: Boolean?,
    val alertLabel: String?
)
