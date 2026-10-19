package com.internalexam.ui.teacher

import androidx.compose.ui.graphics.Color
import com.internalexam.data.questionimport.QuestionType
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppBlue
import com.internalexam.ui.theme.AppMint
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ExamDateTimeComponents(
    val dateMillis: Long,
    val hour: Int,
    val minute: Int
)

fun calculatePointsEach(count: Int): String {
    if (count <= 0) return "--"
    val points = 10.0 / count
    return "%.2f".format(Locale.US, points)
}

fun questionTypeLabel(type: QuestionType): String {
    return when (type) {
        QuestionType.SINGLE -> "Trắc nghiệm 1 đáp án"
        QuestionType.MULTI -> "Trắc nghiệm nhiều đáp án"
        QuestionType.FILL_BLANK -> "Điền từ"
    }
}

fun normalizedQuestionSignature(
    subjectId: Long?,
    topicId: Long?,
    content: String,
    type: String?,
    imageUrl: String?
): String {
    val normContent = content.trim().replace("\\s+".toRegex(), " ").lowercase()
    val normImage = imageUrl?.trim()?.lowercase().orEmpty()
    return "${subjectId ?: -1}|${topicId ?: -1}|$normContent|${type.orEmpty().trim().lowercase()}|$normImage"
}

fun String?.parseExamDateTime(): ExamDateTimeComponents? {
    val raw = this?.trim()?.takeIf { it.isNotBlank() } ?: return null
    val cal = Calendar.getInstance()

    val dateFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm"
    )

    for (pattern in dateFormats) {
        runCatching {
            val date = SimpleDateFormat(pattern, Locale.getDefault()).parse(raw)
            if (date != null) {
                cal.time = date
                val dateOnlyCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, cal.get(Calendar.YEAR))
                    set(Calendar.MONTH, cal.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                return ExamDateTimeComponents(
                    dateMillis = dateOnlyCal.timeInMillis,
                    hour = cal.get(Calendar.HOUR_OF_DAY),
                    minute = cal.get(Calendar.MINUTE)
                )
            }
        }
    }
    return null
}

fun formatDateTime(dateMillis: Long?, hour: Int, minute: Int): String? {
    if (dateMillis == null) return null
    val cal = Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }
    val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return fmt.format(cal.time)
}

fun reportScore(value: String?): String {
    return value?.takeIf { it.isNotBlank() } ?: "--"
}

fun reportDate(value: String?): String {
    return value?.takeIf { it.isNotBlank() }?.replace("T", " ")?.take(16) ?: "--"
}

fun reportStatusLabel(status: String): String {
    return when (status) {
        "SUBMITTED" -> "Đã nộp bài"
        "DOING" -> "Đang làm bài"
        "CANCELLED" -> "Đã hủy"
        else -> status
    }
}

fun reportStatusColor(status: String): Color {
    return when (status) {
        "SUBMITTED" -> AppMint
        "DOING" -> AppBlue
        "CANCELLED" -> AppRed
        else -> AppAmber
    }
}
