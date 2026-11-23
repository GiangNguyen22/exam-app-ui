package com.internalexam.model

enum class QuestionType {
    SINGLE,
    MULTI,
    TRUE_FALSE,
    ESSAY
}

data class Option(
    val id: String,
    val text: String
)

data class Question(
    val questionId: Long,
    val content: String,
    val type: QuestionType,
    val options: List<Option> = emptyList(),
    val correctAnswers: List<String> = emptyList(),
    val explanation: String? = null
)

data class StudentAnswer(
    val questionId: Long,
    val selectedAnswers: List<String> = emptyList(),
    val isCorrect: Boolean,
    val score: Double,
    val essayText: String? = null
)

data class SubmissionQuestionItem(
    val question: Question,
    val studentAnswer: StudentAnswer
)

data class SubmissionDetailHeader(
    val submissionId: Long,
    val studentName: String,
    val studentId: String,
    val avatarUrl: String?,
    val score: Double,
    val totalScore: Double = 10.0,
    val timeSpent: Int,
    val submittedAt: String
)
