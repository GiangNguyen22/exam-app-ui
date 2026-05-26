package com.internalexam.data

import androidx.compose.runtime.mutableStateMapOf
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.QuestionType

object ExamAttemptStore {
    private val selectedAnswers = mutableStateMapOf<Int, Set<String>>()
    var backendExamId: Long = 1L
        private set

    fun setBackendExamId(examId: Long) {
        backendExamId = examId
    }

    val answeredCount: Int
        get() = selectedAnswers.count { it.value.isNotEmpty() }

    val unansweredCount: Int
        get() = MockData.questions.size - answeredCount

    fun selectedAnswerIds(questionId: Int): Set<String> {
        return selectedAnswers[questionId].orEmpty()
    }

    fun isAnswered(questionId: Int): Boolean {
        return selectedAnswerIds(questionId).isNotEmpty()
    }

    fun selectAnswer(questionId: Int, answerId: String, questionType: QuestionType) {
        val current = selectedAnswerIds(questionId)
        selectedAnswers[questionId] = if (questionType == QuestionType.MULTI) {
            if (current.contains(answerId)) current - answerId else current + answerId
        } else {
            setOf(answerId)
        }
    }

    fun reset() {
        selectedAnswers.clear()
    }

    fun score(): AttemptScore {
        val correct = MockData.questions.count { question ->
            val expected = question.answers.filter { it.correct }.map { it.id }.toSet()
            selectedAnswerIds(question.id) == expected
        }
        val total = MockData.questions.size
        val blank = unansweredCount
        val wrong = total - correct - blank
        val score = if (total == 0) 0.0 else correct * 10.0 / total
        return AttemptScore(score, correct, wrong, blank)
    }
}

data class AttemptScore(
    val score: Double,
    val correct: Int,
    val wrong: Int,
    val blank: Int
)
