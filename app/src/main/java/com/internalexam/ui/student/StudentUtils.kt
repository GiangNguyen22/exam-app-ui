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

data class StudentRecentResult(
    val exam: ExamResponse,
    val result: com.internalexam.data.network.ExamResultResponse
)

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

fun String?.parseBackendTimeMillis(): Long? {
    val raw = this?.trim()?.takeIf { it.isNotBlank() } ?: return null
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm"
    )
    return patterns.firstNotNullOfOrNull { pattern ->
        runCatching {
            SimpleDateFormat(pattern, Locale.US).apply {
                isLenient = false
                if (pattern.endsWith("'Z'")) {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
            }.parse(raw)?.time
        }.getOrNull()
    }
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
    val startMillis = startTime.parseBackendTimeMillis() ?: return false
    val endMillis = endTime.parseBackendTimeMillis() ?: startMillis

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
