package com.internalexam.ui.student

import androidx.compose.ui.graphics.Color
import com.internalexam.data.network.ExamResponse
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppMint
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

data class StudentRecentResult(
    val exam: ExamResponse,
    val result: com.internalexam.data.network.ExamResultDetailResponse
)

suspend fun fetchStudentExamResultDetail(
    authorization: String,
    exam: ExamResponse
): StudentRecentResult? {
    // 1. Try detailed result endpoint first
    val detailRes = runCatching { com.internalexam.data.network.ApiClient.getResultDetail(authorization, exam.id) }.getOrNull()
    val detailData = detailRes?.data
    if (detailRes?.success == true && detailData != null && (detailData.submittedAt != null || detailData.score != null || detailData.status == "SUBMITTED")) {
        return StudentRecentResult(exam, detailData)
    }

    // 2. Fallback to summary result endpoint
    val summaryRes = runCatching { com.internalexam.data.network.ApiClient.getResult(authorization, exam.id) }.getOrNull()
    val summaryData = summaryRes?.data
    if (summaryRes?.success == true && summaryData != null && (summaryData.submittedAt != null || summaryData.score != null || summaryData.status == "SUBMITTED")) {
        val convertedDetail = com.internalexam.data.network.ExamResultDetailResponse(
            resultId = summaryData.resultId,
            examId = summaryData.examId,
            examTitle = exam.title,
            score = summaryData.score,
            status = summaryData.status,
            submittedAt = summaryData.submittedAt,
            totalQuestions = exam.questionCount ?: exam.totalQuestions,
            correctCount = null,
            wrongCount = null,
            blankCount = null,
            questions = null
        )
        return StudentRecentResult(exam, convertedDetail)
    }

    return null
}

suspend fun fetchAllStudentResults(
    authorization: String
): List<StudentRecentResult> {
    val currentUser = runCatching { com.internalexam.data.network.ApiClient.getCurrentUser(authorization).data }.getOrNull()
    val allExamsMap = runCatching {
        com.internalexam.data.network.ApiClient.getExams(authorization).data.orEmpty().associateBy { it.id }
    }.getOrDefault(emptyMap())

    if (currentUser != null) {
        val studentResultsRes = runCatching {
            com.internalexam.data.network.ApiClient.viewStudentResults(authorization, currentUser.id)
        }.getOrNull()

        val studentResultsList = studentResultsRes?.data.orEmpty()
        if (studentResultsRes?.success == true && studentResultsList.isNotEmpty()) {
            return studentResultsList.map { resItem ->
                val exam = allExamsMap[resItem.examId] ?: ExamResponse(
                    id = resItem.examId,
                    code = resItem.examCode ?: "EXAM_${resItem.examId}",
                    title = resItem.examTitle ?: "Bài thi ${resItem.examId}",
                    durationMinutes = 45,
                    scorePerQuestion = "1.0",
                    startTime = null,
                    endTime = null,
                    shuffleQuestions = false,
                    shuffleAnswers = false
                )

                val detailData = runCatching {
                    com.internalexam.data.network.ApiClient.getResultDetail(authorization, resItem.examId).data
                }.getOrNull()

                val resultDetail = detailData ?: com.internalexam.data.network.ExamResultDetailResponse(
                    resultId = resItem.resultId,
                    examId = resItem.examId,
                    examTitle = resItem.examTitle ?: exam.title,
                    score = resItem.score,
                    status = resItem.status,
                    submittedAt = resItem.submittedAt,
                    totalQuestions = resItem.totalQuestions ?: exam.questionCount ?: exam.totalQuestions,
                    correctCount = resItem.correctCount,
                    wrongCount = resItem.wrongCount,
                    blankCount = resItem.blankCount,
                    questions = null
                )

                StudentRecentResult(exam, resultDetail)
            }.sortedByDescending { it.result.submittedAt.orEmpty() }
        }
    }

    val examsList = allExamsMap.values.toList().ifEmpty {
        runCatching { com.internalexam.data.network.ApiClient.getExams(authorization).data.orEmpty() }.getOrDefault(emptyList())
    }

    return kotlinx.coroutines.coroutineScope {
        examsList.map { exam ->
            async {
                fetchStudentExamResultDetail(authorization, exam)
            }
        }.awaitAll().filterNotNull()
            .sortedByDescending { it.result.submittedAt.orEmpty() }
    }
}

data class StudentExamMetadata(
    val questionCount: Int?,
    val subjectName: String?
)

fun StudentExamMetadata?.questionCountLabel(exam: ExamResponse): String {
    val count = this?.questionCount ?: exam.questionCount ?: exam.totalQuestions
    return count?.let { "$it câu" } ?: "-- câu"
}

fun ExamResponse.durationLabel(): String {
    return durationMinutes?.let { "$it phút" } ?: "-- phút"
}

fun StudentExamMetadata?.subjectLabel(exam: ExamResponse): String {
    return this?.subjectName?.takeIf { it.isNotBlank() }
        ?: exam.subjectName?.takeIf { it.isNotBlank() }
        ?: exam.subject?.takeIf { it.isNotBlank() }
        ?: "Chưa rõ môn"
}

fun scoreColor(score: String?): Color {
    val numericScore = score?.replace(",", ".")?.toFloatOrNull()
    return when {
        numericScore == null -> AppMuted
        numericScore >= 8f -> AppMint
        numericScore >= 5f -> AppAmber
        else -> AppRed
    }
}

fun resultStatusLabel(status: String): String {
    return when (status) {
        "SUBMITTED" -> "Đã nộp bài"
        "DOING" -> "Đang làm bài"
        "CANCELLED" -> "Đã hủy"
        else -> status
    }
}

fun resultDate(value: String?): String {
    return value?.takeIf { it.isNotBlank() }?.replace("T", " ")?.take(16) ?: "--"
}

fun ExamResponse.isExpired(now: Long = System.currentTimeMillis()): Boolean {
    val endAt = endTime.parseBackendTimeMillis() ?: return false
    return endAt <= now
}

fun ExamResponse.isOpenNow(now: Long = System.currentTimeMillis()): Boolean {
    val startAt = startTime.parseBackendTimeMillis()
    val endAt = endTime.parseBackendTimeMillis()
    return (startAt == null || startAt <= now) && (endAt == null || endAt > now)
}

fun ExamResponse.isNotOpenYet(now: Long = System.currentTimeMillis()): Boolean {
    return startTime.parseBackendTimeMillis()?.let { it > now } == true
}

fun String?.parseBackendTimeMillis(): Long? {
    val raw = this?.trim()?.takeIf { it.isNotBlank() } ?: return null
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd",
        "dd/MM/yyyy HH:mm:ss",
        "dd/MM/yyyy HH:mm",
        "dd/MM/yyyy"
    )
    val normalizedRaw = raw.replace("Z", "+00:00")

    for (pattern in patterns) {
        runCatching {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            if (pattern.contains("'Z'")) {
                sdf.timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = sdf.parse(raw) ?: sdf.parse(normalizedRaw)
            if (date != null) return date.time
        }
    }
    return null
}

fun formatTimeLeft(seconds: Int): String {
    val safeSeconds = seconds.coerceAtLeast(0)
    val minutes = safeSeconds / 60
    val remainingSeconds = safeSeconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

fun savedStatusText(savedAt: Long?): String {
    return savedAt?.let {
        "Đã lưu ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))}"
    } ?: "Chưa lưu"
}

fun ExamResponse.occursOnDate(year: Int, month: Int, day: Int): Boolean {
    val rawStart = startTime?.trim()
    if (rawStart.isNullOrBlank()) return false

    // 1. Direct raw date string match (e.g. "2026-10-15")
    val targetDateStr = "%04d-%02d-%02d".format(year, month + 1, day)
    if (rawStart.startsWith(targetDateStr)) return true

    // 2. Parsed timestamp match
    val startMillis = startTime.parseBackendTimeMillis() ?: return false
    val durationMs = (durationMinutes ?: 45) * 60 * 1000L
    val endMillis = endTime.parseBackendTimeMillis() ?: (startMillis + durationMs)

    // Check same local start day
    val examCalLocal = Calendar.getInstance().apply { timeInMillis = startMillis }
    if (examCalLocal.get(Calendar.YEAR) == year &&
        examCalLocal.get(Calendar.MONTH) == month &&
        examCalLocal.get(Calendar.DAY_OF_MONTH) == day) {
        return true
    }

    // Check same UTC start day
    val examCalUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = startMillis }
    if (examCalUtc.get(Calendar.YEAR) == year &&
        examCalUtc.get(Calendar.MONTH) == month &&
        examCalUtc.get(Calendar.DAY_OF_MONTH) == day) {
        return true
    }

    // Check overlapping date range
    val calStart = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val calEnd = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }

    return startMillis <= calEnd.timeInMillis && endMillis >= calStart.timeInMillis
}

fun formatExamTimeRange(startTimeStr: String?, endTimeStr: String?, durationMinutes: Int?): String {
    val startMillis = startTimeStr.parseBackendTimeMillis()
    val endMillis = endTimeStr.parseBackendTimeMillis()

    val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())
    val startText = startMillis?.let { timeFmt.format(Date(it)) } ?: "--:--"

    val endText = if (endMillis != null) {
        timeFmt.format(Date(endMillis))
    } else if (durationMinutes != null && startMillis != null) {
        timeFmt.format(Date(startMillis + durationMinutes * 60 * 1000L))
    } else null

    return if (endText != null) "$startText - $endText" else startText
}

fun formatSelectedDateHeader(year: Int, month: Int, day: Int): String {
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, day)
    }
    val dayOfWeekStr = when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Thứ Hai"
        Calendar.TUESDAY -> "Thứ Ba"
        Calendar.WEDNESDAY -> "Thứ Tư"
        Calendar.THURSDAY -> "Thứ Năm"
        Calendar.FRIDAY -> "Thứ Sáu"
        Calendar.SATURDAY -> "Thứ Bảy"
        Calendar.SUNDAY -> "Chủ Nhật"
        else -> ""
    }
    return "$dayOfWeekStr, %02d/%02d/%04d".format(day, month + 1, year)
}
