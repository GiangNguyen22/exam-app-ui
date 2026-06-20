package com.internalexam.data.examimport

import com.internalexam.data.network.AnswerCreateRequest
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.ApiResponse
import com.internalexam.data.network.ExamQuestionCreateRequest
import com.internalexam.data.network.SubjectResponse
import com.internalexam.data.network.TopicResponse
import com.google.gson.Gson
import java.text.Normalizer
import java.util.Locale
import retrofit2.HttpException

object ExamExcelImportService {
    private val gson = Gson()

    suspend fun importQuestions(
        authorization: String,
        examId: Long,
        rows: List<ExamExcelQuestionRow>,
        defaultScore: String?
    ): ExamExcelImportResult {
        if (rows.isEmpty()) return ExamExcelImportResult(createdCount = 0, failures = emptyList())

        val failures = mutableListOf<ExamExcelImportFailure>()
        var createdCount = 0
        val existingQuestionKeys = runCatching {
            ApiClient.getExamQuestions(authorization, examId).data.orEmpty().map { question ->
                question.uniqueKey()
            }.toMutableSet()
        }.getOrDefault(mutableSetOf())
        val importedKeys = mutableSetOf<String>()
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

            val rowKey = row.uniqueKey(subjectId, topicId)
            if (!importedKeys.add(rowKey)) {
                failures += ExamExcelImportFailure(row.rowNumber, "Duplicate row in the import file. This question was skipped.")
                return@forEachIndexed
            }
            if (rowKey in existingQuestionKeys) {
                failures += ExamExcelImportFailure(row.rowNumber, "This question already exists in the exam. It was skipped.")
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
                    existingQuestionKeys += rowKey
                } else {
                    failures += ExamExcelImportFailure(row.rowNumber, response.message.ifBlank { "Backend rejected this row." })
                }
            } catch (exception: HttpException) {
                val backendMessage = runCatching {
                    val body = exception.response()?.errorBody()?.string().orEmpty()
                    gson.fromJson(body, ApiResponse::class.java)?.message
                }.getOrNull().orEmpty()
                failures += ExamExcelImportFailure(
                    row.rowNumber,
                    backendMessage.ifBlank { "Backend rejected this row." }
                )
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

    private fun ExamExcelQuestionRow.uniqueKey(subjectId: Long, topicId: Long?): String {
        return listOf(
            subjectId.toString(),
            topicId?.toString().orEmpty(),
            content.trim().lowercase(Locale.US),
            type.trim().uppercase(Locale.US),
            difficulty.trim().uppercase(Locale.US)
        ).joinToString("|")
    }

    private fun com.internalexam.data.network.ExamQuestionResponse.uniqueKey(): String {
        return listOf(
            subjectId?.toString().orEmpty(),
            topicId?.toString().orEmpty(),
            content.trim().lowercase(Locale.US),
            type.orEmpty().trim().uppercase(Locale.US),
            difficulty.orEmpty().trim().uppercase(Locale.US)
        ).joinToString("|")
    }
}
