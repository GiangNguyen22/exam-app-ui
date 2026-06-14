package com.internalexam.data.questionimport

data class QuestionImportRow(
    val rowNumber: Int,
    val subjectId: Long?,
    val subjectName: String?,
    val topicId: Long?,
    val topicName: String?,
    val content: String,
    val type: String,
    val difficulty: String
)

data class QuestionImportFailure(
    val rowNumber: Int,
    val message: String
)

data class QuestionImportParseResult(
    val rows: List<QuestionImportRow>,
    val failures: List<QuestionImportFailure>
)

data class QuestionImportResult(
    val createdCount: Int,
    val failures: List<QuestionImportFailure>
) {
    val totalCount: Int
        get() = createdCount + failures.size
}
