package com.internalexam.data.examimport

data class ExamExcelAnswerRow(
    val label: String,
    val content: String,
    val correct: Boolean,
    val explanation: String?
)

data class ExamExcelQuestionRow(
    val rowNumber: Int,
    val subjectId: Long?,
    val subjectName: String?,
    val topicId: Long?,
    val topicName: String?,
    val content: String,
    val type: String,
    val difficulty: String,
    val orderIndex: Int?,
    val score: String?,
    val answers: List<ExamExcelAnswerRow>
)

data class ExamExcelImportFailure(
    val rowNumber: Int,
    val message: String
)

data class ExamExcelParseResult(
    val rows: List<ExamExcelQuestionRow>,
    val failures: List<ExamExcelImportFailure>
)

data class ExamExcelImportResult(
    val createdCount: Int,
    val failures: List<ExamExcelImportFailure>
) {
    val totalCount: Int
        get() = createdCount + failures.size
}
