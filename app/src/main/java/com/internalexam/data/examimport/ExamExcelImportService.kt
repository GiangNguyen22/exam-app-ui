package com.internalexam.data.examimport

import com.internalexam.data.network.AnswerCreateRequest
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.ExamQuestionCreateRequest
import com.internalexam.data.network.SubjectResponse
import com.internalexam.data.network.TopicResponse
import java.text.Normalizer
import java.util.Locale

object ExamExcelImportService {
    suspend fun importQuestions(
        authorization: String,
        examId: Long,
        rows: List<ExamExcelQuestionRow>,
        defaultScore: String?
    ): ExamExcelImportResult {
        if (rows.isEmpty()) return ExamExcelImportResult(createdCount = 0, failures = emptyList())

        val failures = mutableListOf<ExamExcelImportFailure>()
        var createdCount = 0
        val subjects = loadSubjectsIfNeeded(authorization, rows, failures)
        val topicsBySubjectId = mutableMapOf<Long, List<TopicResponse>>()

        rows.forEachIndexed { index, row ->
            val subjectId = row.subjectId ?: row.subjectName?.let { name ->
                subjects.firstOrNull { it.name.sameCatalogName(name) }?.id
            }
            if (subjectId == null) {
                failures += ExamExcelImportFailure(row.rowNumber, "Subject was not found.")
                return@forEachIndexed
            }

            val topicId = row.topicId ?: row.topicName?.let { topicName ->
                val topics = topicsBySubjectId.getOrPut(subjectId) {
                    runCatching { ApiClient.getTopics(authorization, subjectId).data.orEmpty() }.getOrDefault(emptyList())
                }
                topics.firstOrNull { it.name.sameCatalogName(topicName) }?.id
            }
            if (row.topicName != null && topicId == null) {
                failures += ExamExcelImportFailure(row.rowNumber, "Topic was not found for the selected subject.")
                return@forEachIndexed
            }

            try {
                val response = ApiClient.createQuestionForExam(
                    authorization,
                    examId,
                    ExamQuestionCreateRequest(
                        subjectId = subjectId,
                        topicId = topicId,
                        content = row.content,
                        type = row.type,
                        difficulty = row.difficulty,
                        orderIndex = row.orderIndex ?: (index + 1),
                        score = row.score ?: defaultScore,
                        answers = row.answers.map { answer ->
                            AnswerCreateRequest(
                                content = answer.content,
                                correct = answer.correct,
                                explanation = answer.explanation
                            )
                        }
                    )
                )
                if (response.success) {
                    createdCount++
                } else {
                    failures += ExamExcelImportFailure(row.rowNumber, response.message.ifBlank { "Backend rejected this row." })
                }
            } catch (exception: Exception) {
                failures += ExamExcelImportFailure(row.rowNumber, "Cannot add this question to the exam right now.")
            }
        }

        return ExamExcelImportResult(createdCount = createdCount, failures = failures)
    }

    private suspend fun loadSubjectsIfNeeded(
        authorization: String,
        rows: List<ExamExcelQuestionRow>,
        failures: MutableList<ExamExcelImportFailure>
    ): List<SubjectResponse> {
        if (rows.none { it.subjectId == null && it.subjectName != null }) return emptyList()
        return try {
            ApiClient.getSubjects(authorization).data.orEmpty()
        } catch (exception: Exception) {
            failures += ExamExcelImportFailure(1, "Cannot load subjects to resolve subject names.")
            emptyList()
        }
    }

    private fun String.sameCatalogName(other: String): Boolean {
        return normalizeCatalogName() == other.normalizeCatalogName()
    }

    private fun String.normalizeCatalogName(): String {
        return Normalizer.normalize(trim().lowercase(Locale.US), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .replace("\u0111", "d")
            .replace(Regex("\\s+"), " ")
    }
}
