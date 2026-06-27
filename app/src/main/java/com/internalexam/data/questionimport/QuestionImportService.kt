package com.internalexam.data.questionimport

import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.AnswerCreateRequest
import com.internalexam.data.network.QuestionCreateRequest
import com.internalexam.data.network.SubjectResponse
import com.internalexam.data.network.TopicResponse
import java.util.Locale

object QuestionImportService {
    suspend fun importQuestions(
        authorization: String,
        rows: List<QuestionImportRow>
    ): QuestionImportResult {
        if (rows.isEmpty()) return QuestionImportResult(createdCount = 0, failures = emptyList())

        val failures = mutableListOf<QuestionImportFailure>()
        var createdCount = 0
        val subjects = loadSubjectsIfNeeded(authorization, rows, failures)
        val topicsBySubjectId = mutableMapOf<Long, List<TopicResponse>>()

        rows.forEach { row ->
            val subjectId = row.subjectId ?: row.subjectName?.let { name ->
                subjects.firstOrNull { it.name.sameCatalogName(name) }?.id
            }
            if (subjectId == null) {
                failures += QuestionImportFailure(row.rowNumber, "Subject was not found.")
                return@forEach
            }

            val topicId = row.topicId ?: row.topicName?.let { topicName ->
                val topics = topicsBySubjectId.getOrPut(subjectId) {
                    runCatching { ApiClient.getTopics(authorization, subjectId).data.orEmpty() }.getOrDefault(emptyList())
                }
                topics.firstOrNull { it.name.sameCatalogName(topicName) }?.id
            }
            if (row.topicName != null && topicId == null) {
                failures += QuestionImportFailure(row.rowNumber, "Topic was not found for the selected subject.")
                return@forEach
            }

            try {
                val response = ApiClient.createQuestion(
                    authorization,
                    QuestionCreateRequest(
                        subjectId = subjectId,
                        topicId = topicId,
                        content = row.content,
                        type = row.type,
                        difficulty = row.difficulty,
                        imageUrl = null,
                        answers = listOf(
                            AnswerCreateRequest(
                                content = row.content,
                                correct = true,
                                explanation = null
                            )
                        )
                    )
                )
                if (response.success) {
                    createdCount++
                } else {
                    failures += QuestionImportFailure(row.rowNumber, response.message.ifBlank { "Backend rejected this row." })
                }
            } catch (exception: Exception) {
                failures += QuestionImportFailure(row.rowNumber, "Cannot import this row right now.")
            }
        }

        return QuestionImportResult(createdCount = createdCount, failures = failures)
    }

    private suspend fun loadSubjectsIfNeeded(
        authorization: String,
        rows: List<QuestionImportRow>,
        failures: MutableList<QuestionImportFailure>
    ): List<SubjectResponse> {
        if (rows.none { it.subjectId == null && it.subjectName != null }) return emptyList()
        return try {
            ApiClient.getSubjects(authorization).data.orEmpty()
        } catch (exception: Exception) {
            failures += QuestionImportFailure(1, "Cannot load subjects to resolve subject names.")
            emptyList()
        }
    }

    private fun String.sameCatalogName(other: String): Boolean {
        return normalizeCatalogName() == other.normalizeCatalogName()
    }

    private fun String.normalizeCatalogName(): String {
        return trim().lowercase(Locale.US)
            .replace("đ", "d")
            .replace(Regex("\\s+"), " ")
    }
}
