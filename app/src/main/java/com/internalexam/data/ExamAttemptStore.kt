package com.internalexam.data

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.internalexam.data.network.ExamAnswerSubmitRequest
import com.internalexam.data.network.ExamResponse
import com.internalexam.data.network.ExamQuestionResponse
import com.internalexam.data.network.QuestionResponse
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.QuestionType

object ExamAttemptStore {
    private const val PREFS_NAME = "exam_attempt_v2"
    private const val KEY_SAVED_STATE = "saved_state"

    private val gson = Gson()
    private var prefs: android.content.SharedPreferences? = null
    private var prefsInitialized = false

    private val selectedAnswers = mutableStateMapOf<Long, Set<String>>()
    private val fillAnswers = mutableStateMapOf<Long, String>()
    private val answerSavedAt = mutableStateMapOf<Long, Long>()
    var backendQuestions = mutableStateOf<List<ExamQuestionResponse>>(emptyList())
        private set
    var backendExamId: Long = 1L
        private set
    var selectedExam = mutableStateOf<ExamResponse?>(null)
        private set
    var selectedQuestion = mutableStateOf<ExamQuestionResponse?>(null)
        private set
    var selectedBankQuestion = mutableStateOf<QuestionResponse?>(null)
        private set

    /** Thời điểm kết thúc bài thi (epoch millis). Được neo cố định khi vào màn làm bài
     *  để đồng hồ không bị reset khi điều hướng qua lại. */
    var attemptEndAtMillis: Long? = null
        private set

    fun initialize(context: Context) {
        if (prefsInitialized) return
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefsInitialized = true
    }

    /** Lưu toàn bộ trạng thái bài thi vào SharedPreferences. */
    fun saveState() {
        val p = prefs ?: return
        val data = ExamSavedState(
            selectedAnswers = selectedAnswers.entries.associate { (k, v) -> k.toString() to v.toList() },
            fillAnswers = fillAnswers.entries.associate { (k, v) -> k.toString() to v },
            answerSavedAt = answerSavedAt.entries.associate { (k, v) -> k.toString() to v },
            attemptEndAtMillis = attemptEndAtMillis,
            backendExamId = backendExamId,
            selectedExam = selectedExam.value
        )
        p.edit().putString(KEY_SAVED_STATE, gson.toJson(data)).apply()
    }

    /** Khôi phục trạng thái bài thi từ SharedPreferences.
     *  Trả về true nếu khôi phục thành công. */
    fun restoreState(): Boolean {
        val p = prefs ?: return false
        val json = p.getString(KEY_SAVED_STATE, null) ?: return false
        val type = object : TypeToken<ExamSavedState>() {}.type
        val data: ExamSavedState = gson.fromJson(json, type) ?: return false

        selectedAnswers.clear()
        data.selectedAnswers.forEach { (k, v) -> selectedAnswers[k.toLong()] = v.toSet() }

        fillAnswers.clear()
        data.fillAnswers.forEach { (k, v) -> fillAnswers[k.toLong()] = v }

        answerSavedAt.clear()
        data.answerSavedAt.forEach { (k, v) -> answerSavedAt[k.toLong()] = v }

        attemptEndAtMillis = data.attemptEndAtMillis
        backendExamId = data.backendExamId
        selectedExam.value = data.selectedExam
        backendQuestions.value = emptyList()
        return true
    }

    /** Kiểm tra xem có saved attempt khớp với examId hay không. */
    fun hasSavedAttempt(examId: Long): Boolean {
        if (prefs?.contains(KEY_SAVED_STATE) != true) return false
        // Nếu đã có state trong store, kiểm tra id
        if (backendExamId != examId) return false
        if (attemptEndAtMillis == null) return false
        // Nếu deadline đã hết thì coi như không có attempt
        if (System.currentTimeMillis() >= attemptEndAtMillis!!) return false
        return true
    }

    /** Xóa trạng thái đã lưu. */
    private fun clearSavedState() {
        prefs?.edit()?.remove(KEY_SAVED_STATE)?.apply()
    }

    private const val KEY_PENDING_SUBMIT = "pending_submit"

    fun savePendingSubmission(examId: Long, note: String) {
        val p = prefs ?: return
        val data = PendingSubmission(examId = examId, note = note)
        p.edit().putString(KEY_PENDING_SUBMIT, gson.toJson(data)).apply()
    }

    fun hasPendingSubmission(): Boolean {
        return prefs?.contains(KEY_PENDING_SUBMIT) == true
    }

    fun getPendingSubmission(): PendingSubmission? {
        val p = prefs ?: return null
        val json = p.getString(KEY_PENDING_SUBMIT, null) ?: return null
        return try { gson.fromJson(json, PendingSubmission::class.java) } catch (_: Exception) { null }
    }

    fun clearPendingSubmission() {
        prefs?.edit()?.remove(KEY_PENDING_SUBMIT)?.apply()
    }

    fun selectExam(examId: Long, exam: ExamResponse? = null) {
        backendExamId = examId
        selectedExam.value = exam
        selectedBankQuestion.value = null
    }

    fun startNewAttempt(examId: Long, exam: ExamResponse? = null) {
        selectExam(examId, exam)
        backendQuestions.value = emptyList()
        // Bắt đầu một attempt mới thì mới xóa dữ liệu cũ.
        reset()
    }

    @Deprecated("Use selectExam() or startNewAttempt() depending on intent.")
    fun setBackendExamId(examId: Long, exam: ExamResponse? = null) {
        startNewAttempt(examId, exam)
    }

    /** Đặt mốc hết giờ một lần cho mỗi lần làm bài; gọi lại sẽ không làm mới mốc. */
    fun ensureAttemptDeadline(durationMinutes: Int) {
        if (attemptEndAtMillis == null) {
            attemptEndAtMillis = System.currentTimeMillis() +
                durationMinutes.coerceAtLeast(1) * 60_000L
        }
    }

    fun setBackendQuestions(questions: List<ExamQuestionResponse>) {
        backendQuestions.value = questions
    }

    fun setSelectedQuestion(question: ExamQuestionResponse?) {
        selectedQuestion.value = question
        if (question != null) {
            selectedBankQuestion.value = null
        }
    }

    fun setSelectedBankQuestion(question: QuestionResponse?) {
        selectedBankQuestion.value = question
        if (question != null) {
            selectedQuestion.value = null
        }
    }

    fun clearExamContext() {
        backendExamId = 1L
        selectedExam.value = null
        backendQuestions.value = emptyList()
        selectedQuestion.value = null
    }

    fun clearQuestionSelections() {
        selectedQuestion.value = null
        selectedBankQuestion.value = null
    }

    fun usingBackendQuestions(): Boolean {
        return backendQuestions.value.isNotEmpty()
    }

    val answeredCount: Int
        get() = if (usingBackendQuestions()) {
            backendQuestions.value.count { question ->
                if (question.type == QuestionType.FILL_BLANK.name) {
                    fillAnswer(question.questionId).isNotBlank()
                } else {
                    selectedAnswerIds(question.questionId).isNotEmpty()
                }
            }
        } else {
            selectedAnswers.count { it.value.isNotEmpty() }
        }

    val unansweredCount: Int
        get() = totalQuestionCount - answeredCount

    val totalQuestionCount: Int
        get() = if (usingBackendQuestions()) backendQuestions.value.size else MockData.questions.size

    fun selectedAnswerIds(questionId: Int): Set<String> {
        return selectedAnswers[questionId.toLong()].orEmpty()
    }

    fun selectedAnswerIds(questionId: Long): Set<String> {
        return selectedAnswers[questionId].orEmpty()
    }

    fun isAnswered(questionId: Int): Boolean {
        return selectedAnswerIds(questionId).isNotEmpty()
    }

    fun isAnswered(questionId: Long): Boolean {
        val question = backendQuestions.value.firstOrNull { it.questionId == questionId }
        return if (question?.type == QuestionType.FILL_BLANK.name) {
            fillAnswer(questionId).isNotBlank()
        } else {
            selectedAnswerIds(questionId).isNotEmpty()
        }
    }

    fun selectAnswer(questionId: Int, answerId: String, questionType: QuestionType) {
        val key = questionId.toLong()
        val current = selectedAnswerIds(questionId)
        selectedAnswers[key] = if (questionType == QuestionType.MULTI) {
            if (current.contains(answerId)) current - answerId else current + answerId
        } else {
            setOf(answerId)
        }
        answerSavedAt[key] = System.currentTimeMillis()
        saveState()
    }

    fun selectBackendAnswer(questionId: Long, answerId: Long, type: String?) {
        val key = questionId
        val current = selectedAnswerIds(questionId)
        selectedAnswers[key] = if (type == QuestionType.MULTI.name) {
            if (current.contains(answerId.toString())) current - answerId.toString() else current + answerId.toString()
        } else {
            setOf(answerId.toString())
        }
        answerSavedAt[key] = System.currentTimeMillis()
        saveState()
    }

    fun fillAnswer(questionId: Long): String {
        return fillAnswers[questionId].orEmpty()
    }

    fun setFillAnswer(questionId: Long, value: String) {
        fillAnswers[questionId] = value
        answerSavedAt[questionId] = System.currentTimeMillis()
        saveState()
    }

    fun savedAt(questionId: Int): Long? {
        return answerSavedAt[questionId.toLong()]
    }

    fun savedAt(questionId: Long): Long? {
        return answerSavedAt[questionId]
    }

    fun backendSubmitAnswers(): List<ExamAnswerSubmitRequest> {
        return backendQuestions.value.map { question ->
            ExamAnswerSubmitRequest(
                questionId = question.questionId,
                selectedAnswerIds = selectedAnswerIds(question.questionId).mapNotNull { it.toLongOrNull() },
                fillContent = if (question.type == QuestionType.FILL_BLANK.name) fillAnswer(question.questionId) else null
            )
        }
    }

    fun reset() {
        selectedAnswers.clear()
        fillAnswers.clear()
        answerSavedAt.clear()
        attemptEndAtMillis = null
        clearSavedState()
        clearPendingSubmission()
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

/** Dữ liệu trạng thái bài thi dùng để persist qua SharedPreferences. */
internal data class ExamSavedState(
    val selectedAnswers: Map<String, List<String>>,
    val fillAnswers: Map<String, String>,
    val answerSavedAt: Map<String, Long>,
    val attemptEndAtMillis: Long?,
    val backendExamId: Long,
    val selectedExam: ExamResponse?
)

data class PendingSubmission(
    val examId: Long,
    val note: String
)
