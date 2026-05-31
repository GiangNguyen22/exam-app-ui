package com.internalexam.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.internalexam.data.network.ExamResponse
import com.internalexam.data.network.ExamQuestionResponse
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.QuestionType

object ExamAttemptStore {
    private val selectedAnswers = mutableStateMapOf<Int, Set<String>>()
    var backendQuestions = mutableStateOf<List<ExamQuestionResponse>>(emptyList())
        private set
    var backendExamId: Long = 1L
        private set
    var selectedExam = mutableStateOf<ExamResponse?>(null)
        private set

    fun setBackendExamId(examId: Long, exam: ExamResponse? = null) {
        backendExamId = examId
        selectedExam.value = exam
        backendQuestions.value = emptyList()
    }

    fun setBackendQuestions(questions: List<ExamQuestionResponse>) {
        backendQuestions.value = questions
    }

    fun usingBackendQuestions(): Boolean {
        return backendQuestions.value.isNotEmpty()
    }

    val answeredCount: Int
        get() = selectedAnswers.count { it.value.isNotEmpty() }

    val unansweredCount: Int
        get() = totalQuestionCount - answeredCount

    val totalQuestionCount: Int
        get() = if (usingBackendQuestions()) backendQuestions.value.size else MockData.questions.size

    fun selectedAnswerIds(questionId: Int): Set<String> {
        return selectedAnswers[questionId].orEmpty()
    }

    fun selectedAnswerIds(questionId: Long): Set<String> {
        return selectedAnswers[questionId.toInt()].orEmpty()
    }

    fun isAnswered(questionId: Int): Boolean {
        return selectedAnswerIds(questionId).isNotEmpty()
    }

    fun isAnswered(questionId: Long): Boolean {
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

    fun selectBackendAnswer(questionId: Long, answerId: Long, type: String?) {
        val key = questionId.toInt()
        val current = selectedAnswerIds(questionId)
        selectedAnswers[key] = if (type == QuestionType.MULTI.name) {
            if (current.contains(answerId.toString())) current - answerId.toString() else current + answerId.toString()
        } else {
            setOf(answerId.toString())
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
