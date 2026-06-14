package com.internalexam.ui.student

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internalexam.data.ExamAttemptStore
import com.internalexam.data.AttemptScore
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.ExamResponse
import com.internalexam.data.network.ExamQuestionResponse
import com.internalexam.data.network.ExamResultResponse
import com.internalexam.data.network.ExamSubmitRequest
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.NetworkState
import com.internalexam.ui.components.AppBackground
import com.internalexam.ui.components.AvatarCircle
import com.internalexam.ui.components.ChipText
import com.internalexam.ui.components.ExamTopBar
import com.internalexam.ui.components.GradientHero
import com.internalexam.ui.components.InfoBanner
import com.internalexam.ui.components.LoadingStateCard
import com.internalexam.ui.components.MetricCard
import com.internalexam.ui.components.PrimaryAction
import com.internalexam.ui.components.RuleItem
import com.internalexam.ui.components.ScreenBottomPadding
import com.internalexam.ui.components.SectionTitle
import com.internalexam.ui.components.StatusPill
import com.internalexam.ui.components.StyledProgress
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppBlue
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppCoral
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMint
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppSurface
import com.internalexam.ui.theme.AppText
import com.internalexam.ui.theme.BgGradient
import com.internalexam.ui.theme.HeroGradient
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun StudentHomeScreen(onLobby: (ExamResponse) -> Unit, onResult: () -> Unit) {
    var exams by remember { mutableStateOf<List<ExamResponse>>(emptyList()) }
    var examMetadata by remember { mutableStateOf<Map<Long, StudentExamMetadata>>(emptyMap()) }
    var recentResults by remember { mutableStateOf<List<StudentRecentResult>>(emptyList()) }
    var loadMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            loadMessage = "Please sign in again."
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val response = ApiClient.getExams(authorization)
            exams = response.data.orEmpty()
            val subjectNamesById = runCatching {
                ApiClient.getSubjects(authorization).data.orEmpty().associate { it.id to it.name }
            }.getOrDefault(emptyMap())
            examMetadata = exams.associate { exam ->
                val questions = runCatching {
                    ApiClient.getExamQuestions(authorization, exam.id).data.orEmpty()
                }.getOrDefault(emptyList())
                val subjectIds = questions.mapNotNull { it.subjectId }.distinct()
                val subjectName = when {
                    subjectIds.size == 1 -> subjectNamesById[subjectIds.first()]
                    subjectIds.size > 1 -> "Nhiều môn"
                    else -> exam.subjectName ?: exam.subject
                }
                exam.id to StudentExamMetadata(
                    questionCount = questions.size.takeIf { it > 0 } ?: exam.questionCount ?: exam.totalQuestions,
                    subjectName = subjectName
                )
            }
            recentResults = exams.mapNotNull { exam ->
                runCatching {
                    ApiClient.getResult(authorization, exam.id).data?.let { result ->
                        StudentRecentResult(exam, result)
                    }
                }.getOrNull()
            }.sortedByDescending { it.result.submittedAt.orEmpty() }
            loadMessage = null
        } catch (exception: Exception) {
            loadMessage = "Cannot load exams right now."
        } finally {
            isLoading = false
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(BgGradient)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle("Student", 50, AppIndigo)
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(greeting, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                    Text("Student Dashboard", style = MaterialTheme.typography.headlineMedium)
                }
            }
            StatusPill(NetworkState.ONLINE)
        }

        SectionTitle("Exam Rooms")
        if (loadMessage != null) {
            InfoBanner(loadMessage.orEmpty(), AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(12.dp))
        }

        if (isLoading) {
            LoadingStateCard("Dang tai de thi...")
        } else if (exams.isEmpty() && loadMessage == null) {
            EmptyExamState()
        } else {
            exams.forEach { exam ->
                val metadata = examMetadata[exam.id]
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Row(Modifier.height(IntrinsicSize.Min)) {
                        Box(
                            Modifier
                                .width(5.dp)
                                .fillMaxHeight()
                                .clip(MaterialTheme.shapes.small)
                                .background(AppIndigo)
                        )
                        Column(
                            Modifier
                                .padding(16.dp)
                                .weight(1f)
                        ) {
                            Text(exam.title, style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                ChipText(metadata.questionCountLabel(exam), AppBlue)
                                ChipText(exam.durationLabel(), AppAmber)
                                ChipText(metadata.subjectLabel(exam), AppIndigo)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Code ${exam.code}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(14.dp))
                            PrimaryAction("Enter Exam Room") { onLobby(exam) }
                        }
                    }
                }
            }
        }

        SectionTitle("Recent Results")
        if (recentResults.isEmpty()) {
            InfoBanner("Chưa có kết quả bài thi.", AppAmber, Icons.Default.Info)
        } else {
            recentResults.take(3).forEach { item ->
                StudentResultCard(item)
                Spacer(Modifier.height(8.dp))
            }
            TextButton(onClick = onResult) { Text("Xem chi tiết kết quả") }
        }
        Spacer(Modifier.height(ScreenBottomPadding))
    }
}

private data class StudentRecentResult(
    val exam: ExamResponse,
    val result: ExamResultResponse
)

private data class StudentExamMetadata(
    val questionCount: Int?,
    val subjectName: String?
)

@Composable
private fun EmptyExamState() {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(64.dp)
                    .background(AppAmber.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.EventBusy, contentDescription = null, tint = AppAmber, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.height(14.dp))
            Text("Chưa có đề thi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Giáo viên chưa mở phòng thi nào.", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
        }
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun StudentResultCard(item: StudentRecentResult) {
    val scoreColor = scoreColor(item.result.score)
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(scoreColor.copy(alpha = 0.12f), CircleShape)
                    .border(2.dp, scoreColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    item.result.score?.takeIf { it.isNotBlank() } ?: "--",
                    fontWeight = FontWeight.Bold,
                    color = scoreColor,
                    fontSize = 15.sp
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(item.exam.title, fontWeight = FontWeight.SemiBold, color = AppText)
                Text(resultStatusLabel(item.result.status), color = AppMuted, style = MaterialTheme.typography.bodyMedium)
            }
            Text(resultDate(item.result.submittedAt), color = AppMuted, style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun StudentExamMetadata?.questionCountLabel(exam: ExamResponse): String {
    val count = this?.questionCount ?: exam.questionCount ?: exam.totalQuestions
    return count?.let { "$it câu" } ?: "-- câu"
}

private fun ExamResponse.durationLabel(): String {
    return durationMinutes?.let { "$it phút" } ?: "-- phút"
}

private fun StudentExamMetadata?.subjectLabel(exam: ExamResponse): String {
    return this?.subjectName?.takeIf { it.isNotBlank() }
        ?: exam.subjectName?.takeIf { it.isNotBlank() }
        ?: exam.subject?.takeIf { it.isNotBlank() }
        ?: "Chưa rõ môn"
}

private fun scoreColor(score: String?): Color {
    val numericScore = score?.replace(",", ".")?.toFloatOrNull()
    return when {
        numericScore == null -> AppMuted
        numericScore >= 8f -> AppMint
        numericScore >= 5f -> AppAmber
        else -> AppRed
    }
}

private fun resultStatusLabel(status: String): String {
    return when (status) {
        "SUBMITTED" -> "Đã nộp bài"
        "DOING" -> "Đang làm bài"
        "CANCELLED" -> "Đã hủy"
        else -> status
    }
}

private fun resultDate(value: String?): String {
    return value?.takeIf { it.isNotBlank() }?.replace("T", " ")?.take(16) ?: "--"
}

private fun formatTimeLeft(seconds: Int): String {
    val safeSeconds = seconds.coerceAtLeast(0)
    val minutes = safeSeconds / 60
    val remainingSeconds = safeSeconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

private fun savedStatusText(savedAt: Long?): String {
    return savedAt?.let {
        "Đã lưu ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))}"
    } ?: "Chưa lưu"
}

@Composable
private fun StudentHomeScreenLegacy(onLobby: () -> Unit, onResult: () -> Unit) {
    var exams by remember { mutableStateOf<List<ExamResponse>>(emptyList()) }
    var loadMessage by remember { mutableStateOf<String?>(null) }
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            loadMessage = "Please sign in again."
            return@LaunchedEffect
        }
        try {
            val response = ApiClient.getExams(authorization)
            exams = response.data.orEmpty()
            loadMessage = if (exams.isEmpty()) "No active exams available." else null
        } catch (exception: Exception) {
            loadMessage = "Cannot load exams right now."
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(BgGradient)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle(MockData.currentStudent.name, 50, AppIndigo)
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(greeting, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                    Text(MockData.currentStudent.name, style = MaterialTheme.typography.headlineMedium)
                }
            }
            StatusPill(NetworkState.ONLINE)
        }

        SectionTitle("Today's Exam Rooms", "Downloaded exams can continue offline")

        MockData.exams.forEach { exam ->
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
            ) {
                Row {
                    Box(
                        Modifier
                            .width(5.dp)
                            .height(170.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(HeroGradient)
                    )
                    Column(
                        Modifier
                            .padding(16.dp)
                            .weight(1f)
                    ) {
                        Text(exam.title, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${exam.subject} · ${exam.duration} min · ${exam.questions} questions",
                            color = AppMuted,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ChipText("Opens ${exam.opens}")
                            ChipText(
                                if (exam.downloaded) "✓ Downloaded" else "Not downloaded",
                                if (exam.downloaded) AppMint else AppAmber
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        PrimaryAction("Enter Exam Room", onClick = onLobby)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        SectionTitle("Recent Results")
        MockData.results.forEach { result ->
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    val scoreColor = when {
                        result.score >= 8 -> AppMint
                        result.score >= 5 -> AppAmber
                        result.score > 0 -> AppRed
                        else -> AppMuted
                    }
                    Box(
                        Modifier
                            .size(48.dp)
                            .background(scoreColor.copy(alpha = 0.12f), CircleShape)
                            .border(2.dp, scoreColor.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (result.score > 0) result.score.toString() else "—",
                            fontWeight = FontWeight.Bold,
                            color = scoreColor,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(result.exam, fontWeight = FontWeight.SemiBold)
                        Text(result.status, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AppMuted)
                }
            }
        }
        TextButton(onClick = onResult) { Text("View sample result →") }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun ExamLobbyScreen(onStart: () -> Unit, onBack: () -> Unit) {
    val exam = ExamAttemptStore.selectedExam.value
    AppBackground {
        ExamTopBar("Exam Room", onBack)
        GradientHero(
            exam?.title ?: "No exam selected",
            exam?.let { "Code ${it.code}" } ?: "Go back and select an exam."
        ) { StatusPill(NetworkState.SYNCED) }

        SectionTitle("Trạng thái đề thi")
        MetricCard(
            "Exam package",
            if (exam != null) "Ready" else "Not ready",
            "Ready to start",
            AppMint,
            Icons.Default.CloudDone
        )

        Spacer(Modifier.height(12.dp))
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
        ) {
            Column(Modifier.padding(16.dp)) {
                RuleItem("Questions will be available when you start the exam", Icons.Default.Info, AppIndigo)
                Surface(
                    color = AppRed.copy(alpha = 0.05f),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RuleItem("Do not leave the app during the exam", Icons.Default.Warning, AppRed)
                }
                RuleItem("Single device sign-in only", Icons.Default.PhoneAndroid, AppAmber)
            }
        }

        Spacer(Modifier.weight(1f))
        PrimaryAction("Start Exam", onClick = onStart)
        Spacer(Modifier.height(ScreenBottomPadding))
    }
}

@Composable
private fun ExamLobbyScreenLegacy(onStart: () -> Unit, onBack: () -> Unit) {
    val exam = MockData.exams.first()
    AppBackground {
        ExamTopBar("Exam Room", onBack)
        GradientHero(
            exam.title,
            "${exam.subject} · ${exam.duration} min · ${exam.questions} questions"
        ) { StatusPill(NetworkState.SYNCED) }

        SectionTitle("Schedule")
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(44.dp)
                        .background(AppBlue.copy(alpha = 0.1f), MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Schedule, null, tint = AppBlue)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Opens ${exam.opens}", fontWeight = FontWeight.SemiBold)
                    Text("Closes ${exam.closes}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        MetricCard("Exam Package", if (exam.downloaded) "Downloaded ✓" else "Not ready", "Available offline after download", AppMint, Icons.Default.CloudDone)

        SectionTitle("Exam Rules")
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
        ) {
            Column(Modifier.padding(16.dp)) {
                RuleItem("Do not leave the app during the exam", Icons.Default.Warning, AppRed)
                RuleItem("Screenshots are prohibited", Icons.Default.Security, AppRed)
                RuleItem("Single device sign-in only", Icons.Default.PhoneAndroid, AppAmber)
                Spacer(Modifier.height(8.dp))
                InfoBanner("Events recorded: APP_EXIT, SCREENSHOT, LOST_CONNECTION, FOCUS_LOST", AppAmber, Icons.Default.Info)
            }
        }

        Spacer(Modifier.weight(1f))
        PrimaryAction("Start Exam", onClick = onStart)
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
fun ExamTakingScreen(onSubmit: () -> Unit, onBack: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var loadMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current
    val backendQuestions = ExamAttemptStore.backendQuestions.value
    val useBackendQuestions = true
    val totalQuestions = backendQuestions.size
    val safeIndex = index.coerceIn(0, (totalQuestions - 1).coerceAtLeast(0))
    val selectedExam = ExamAttemptStore.selectedExam.value
    val initialTimeSeconds = ((selectedExam?.durationMinutes ?: 45).coerceAtLeast(1)) * 60
    var timeLeftSeconds by remember(selectedExam?.id) { mutableIntStateOf(initialTimeSeconds) }
    val progress = if (totalQuestions > 0) ExamAttemptStore.answeredCount.toFloat() / totalQuestions else 0f

    LaunchedEffect(selectedExam?.id) {
        while (timeLeftSeconds > 0) {
            delay(1000L)
            timeLeftSeconds--
        }
    }

    LaunchedEffect(ExamAttemptStore.backendExamId) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization != null) {
            isLoading = true
            try {
                val response = ApiClient.getExamQuestions(authorization, ExamAttemptStore.backendExamId)
                val questions = response.data.orEmpty()
                if (response.success && questions.isNotEmpty()) {
                    ExamAttemptStore.setBackendQuestions(questions)
                    loadMessage = "${questions.size} questions ready."
                } else {
                    loadMessage = "No questions available for this exam."
                }
            } catch (exception: Exception) {
                loadMessage = "Cannot load questions right now."
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    AppBackground {
        ExamTopBar(selectedExam?.title ?: "Exam", onBack)

        // Timer & sync bar
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = AppRed.copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.border(1.dp, AppRed.copy(alpha = 0.2f), CircleShape)
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Timer, null, tint = AppRed, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        formatTimeLeft(timeLeftSeconds),
                        color = if (timeLeftSeconds < 300) AppRed else AppText,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            StatusPill(NetworkState.SYNCING)
        }

        Spacer(Modifier.height(10.dp))
        StyledProgress(progress, AppIndigo)
        Spacer(Modifier.height(14.dp))
        if (loadMessage != null) {
            Text(loadMessage.orEmpty(), color = if (useBackendQuestions) AppMint else AppMuted, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (isLoading) {
                LoadingStateCard("Dang tai cau hoi...")
            } else if (totalQuestions == 0) {
                InfoBanner("This exam does not have questions yet.", AppAmber, Icons.Default.Info)
            } else {
            // Question card
            AnimatedContent(
                targetState = safeIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "QuestionTransition"
            ) { questionIndex ->
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
            ) {
                Column(Modifier.padding(18.dp)) {
                if (useBackendQuestions) {
                    val question = backendQuestions[questionIndex]
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        val displayOrder = question.orderIndex?.takeIf { it > 0 } ?: (questionIndex + 1)
                        ChipText("Câu $displayOrder / ${backendQuestions.size}", AppIndigo)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, null, tint = AppMint, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(savedStatusText(ExamAttemptStore.savedAt(question.questionId)), color = AppMint, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(question.content, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))

                    if (question.type == "FILL_BLANK") {
                        OutlinedTextField(
                            value = ExamAttemptStore.fillAnswer(question.questionId),
                            onValueChange = { ExamAttemptStore.setFillAnswer(question.questionId, it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Your answer") },
                            shape = MaterialTheme.shapes.medium
                        )
                    } else {
                        question.answers.orEmpty().forEachIndexed { answerIndex, answer ->
                            val selected = ExamAttemptStore.selectedAnswerIds(question.questionId).contains(answer.id.toString())
                            val borderColor = if (selected) AppIndigo else AppCardBorder
                            val bgColor = if (selected) AppIndigo.copy(alpha = 0.06f) else Color.Transparent

                            Surface(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    ExamAttemptStore.selectBackendAnswer(question.questionId, answer.id, question.type)
                                },
                                shape = MaterialTheme.shapes.medium,
                                color = bgColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(1.5.dp, borderColor, MaterialTheme.shapes.medium)
                            ) {
                                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (question.type == "MULTI") {
                                        Checkbox(
                                            selected,
                                            {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                ExamAttemptStore.selectBackendAnswer(question.questionId, answer.id, question.type)
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = AppIndigo)
                                        )
                                    } else {
                                        RadioButton(
                                            selected,
                                            {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                ExamAttemptStore.selectBackendAnswer(question.questionId, answer.id, question.type)
                                            },
                                            colors = RadioButtonDefaults.colors(selectedColor = AppIndigo)
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "${'A' + answerIndex}. ${answer.content}",
                                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (selected) AppIndigo else AppText
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val question = MockData.questions[questionIndex]
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ChipText("Câu ${question.id} / ${MockData.questions.size}", AppIndigo)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Save, null, tint = AppMint, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(savedStatusText(ExamAttemptStore.savedAt(question.id)), color = AppMint, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(question.content, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))

                    question.answers.forEach { answer ->
                        val selected = ExamAttemptStore.selectedAnswerIds(question.id).contains(answer.id)
                        val borderColor = if (selected) AppIndigo else AppCardBorder
                        val bgColor = if (selected) AppIndigo.copy(alpha = 0.06f) else Color.Transparent

                        Surface(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                ExamAttemptStore.selectAnswer(question.id, answer.id, question.type)
                            },
                            shape = MaterialTheme.shapes.medium,
                            color = bgColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.5.dp, borderColor, MaterialTheme.shapes.medium)
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                if (question.type.name == "MULTI") {
                                    Checkbox(
                                        selected,
                                        {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            ExamAttemptStore.selectAnswer(question.id, answer.id, question.type)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = AppIndigo)
                                    )
                                } else {
                                    RadioButton(
                                        selected,
                                        {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            ExamAttemptStore.selectAnswer(question.id, answer.id, question.type)
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = AppIndigo)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "${answer.id}. ${answer.text}",
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selected) AppIndigo else AppText
                                )
                            }
                        }
                    }
                }
            }
            }

            }
            }

            Spacer(Modifier.height(10.dp))
            InfoBanner("If network is lost, answers are stored locally and sync later.", AppAmber, Icons.Default.WifiOff)

            SectionTitle("Quick Navigation")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (useBackendQuestions) itemsIndexed(backendQuestions) { i, q ->
                    val color = when {
                        i == safeIndex -> AppIndigo
                        ExamAttemptStore.isAnswered(q.questionId) -> AppMint
                        else -> AppMuted
                    }
                    val isCurrent = i == safeIndex
                    Box(
                        Modifier
                            .size(42.dp)
                            .background(if (isCurrent) color else color.copy(alpha = .10f), CircleShape)
                            .then(if (!isCurrent) Modifier.border(1.dp, color.copy(alpha = 0.3f), CircleShape) else Modifier)
                            .clickable { index = i },
                        contentAlignment = Alignment.Center
                    ) {
                        Text((i + 1).toString(), color = if (isCurrent) Color.White else color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                } else itemsIndexed(MockData.questions) { i, q ->
                    val color = when {
                        i == safeIndex -> AppIndigo
                        ExamAttemptStore.isAnswered(q.id) -> AppMint
                        else -> AppMuted
                    }
                    val isCurrent = i == safeIndex
                    Box(
                        Modifier
                            .size(42.dp)
                            .background(if (isCurrent) color else color.copy(alpha = .10f), CircleShape)
                            .then(if (!isCurrent) Modifier.border(1.dp, color.copy(alpha = 0.3f), CircleShape) else Modifier)
                            .clickable { index = i },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(q.id.toString(), color = if (isCurrent) Color.White else color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { if (index > 0) index-- },
                enabled = index > 0,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Previous")
            }
            Button(
                onClick = { if (index < totalQuestions - 1) index++ },
                enabled = index < totalQuestions - 1,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)
            ) {
                Text("Next")
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(18.dp))
            }
        }

        PrimaryAction("Submit Exam", onClick = { if (totalQuestions > 0) onSubmit() })
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun SubmitConfirmationScreen(onConfirm: () -> Unit, onBack: () -> Unit) {
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Please sign in again."; return }
        scope.launch {
            loading = true; message = null
            try {
                ApiClient.submitExam(
                    authorization,
                    ExamAttemptStore.backendExamId,
                    ExamSubmitRequest(
                        note = "Submitted from Android app with ${ExamAttemptStore.answeredCount} answered questions.",
                        answers = ExamAttemptStore.backendSubmitAnswers()
                    )
                )
                onConfirm()
            } catch (exception: HttpException) {
                message = when (exception.code()) {
                    404 -> "This exam was not found."
                    401, 403 -> "You do not have permission to submit this exam."
                    else -> "Cannot submit exam right now."
                }
            } catch (exception: Exception) {
                message = "Cannot submit exam right now."
            } finally { loading = false }
        }
    }

    AppBackground {
        ExamTopBar("Confirm Submission", onBack)

        Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .background(HeroGradient)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Submission Summary", color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(ExamAttemptStore.answeredCount.toString(), color = Color.White, style = MaterialTheme.typography.headlineLarge)
                            Text("Answered", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                        }
                        Box(
                            Modifier
                                .width(1.dp)
                                .height(50.dp)
                                .background(Color.White.copy(alpha = 0.3f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(ExamAttemptStore.unansweredCount.toString(), color = AppAmber, style = MaterialTheme.typography.headlineLarge)
                            Text("Unanswered", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        InfoBanner("After submission, answers cannot be changed. The system will sync when internet is available.", AppAmber, Icons.Default.Warning)
        if (message != null) {
            Spacer(Modifier.height(10.dp))
            InfoBanner(message.orEmpty(), AppRed, Icons.Default.ErrorOutline)
        }

        Spacer(Modifier.weight(1f))
        PrimaryAction(if (loading) "Submitting..." else "Submit Exam", onClick = { if (!loading) showConfirm = true })
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier
            .fillMaxWidth()
            .height(48.dp), shape = MaterialTheme.shapes.medium, enabled = !loading) { Text("Back to Exam") }
        Spacer(Modifier.height(ScreenBottomPadding))
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { if (!loading) showConfirm = false },
            title = { Text("Xac nhan nop bai") },
            text = {
                val unanswered = ExamAttemptStore.unansweredCount
                Text(
                    if (unanswered > 0) {
                        "Ban con $unanswered cau chua tra loi. Xac nhan nop bai?"
                    } else {
                        "Ban da tra loi het. Xac nhan nop bai?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirm = false
                        submit()
                    },
                    enabled = !loading
                ) {
                    Text("Nop bai")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirm = false },
                    enabled = !loading
                ) {
                    Text("Xem lai")
                }
            }
        )
    }
}

@Composable
fun ResultScreen(onBack: () -> Unit) {
    var backendResult by remember { mutableStateOf<ExamResultResponse?>(null) }
    var backendMessage by remember { mutableStateOf<String?>(null) }
    val examTitle = ExamAttemptStore.selectedExam.value?.title
        ?: ExamAttemptStore.selectedExam.value?.code
        ?: "Ket qua thi"
    val summary = backendAttemptSummary()

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            backendMessage = "Please sign in again."
            return@LaunchedEffect
        }
        try {
            val response = ApiClient.getResult(authorization, ExamAttemptStore.backendExamId)
            backendResult = response.data
            backendMessage = if (response.success) "Result loaded." else response.message
        } catch (exception: Exception) {
            backendMessage = "Result is not available yet."
        }
    }

    AppBackground {
        ExamTopBar("Results", onBack)
        Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .background(HeroGradient)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(backendResult?.score ?: "--", color = Color.White, style = MaterialTheme.typography.displayLarge)
                    Text(examTitle, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(summary.correct.toString(), color = AppMint, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Correct", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(summary.wrong.toString(), color = AppCoral, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Wrong", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(summary.blank.toString(), color = AppAmber, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Blank", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        if (backendMessage != null) {
            InfoBanner(
                backendMessage.orEmpty(),
                if (backendResult != null) AppMint else AppAmber,
                if (backendResult != null) Icons.Default.CloudDone else Icons.Default.Info
            )
        }

        SectionTitle("Result Summary")
        Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = AppSurface), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Exam: $examTitle", fontWeight = FontWeight.Bold)
                Text("Score: ${backendResult?.score ?: "--"}", color = AppMuted)
                Text("Status: ${backendResult?.status ?: "--"}", color = AppMuted)
                Text("Submitted at: ${backendResult?.submittedAt ?: "--"}", color = AppMuted)
            }
        }

        SectionTitle("Answer Details")
        if (ExamAttemptStore.backendQuestions.value.isEmpty()) {
            InfoBanner("No answer details available.", AppAmber, Icons.Default.Info)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ExamAttemptStore.backendQuestions.value.forEach { question ->
                    BackendResultQuestionCard(question)
                }
            }
        }
    }
}

@Composable
private fun BackendResultQuestionCard(question: ExamQuestionResponse) {
    val answers = question.answers.orEmpty()
    val selectedIds = ExamAttemptStore.selectedAnswerIds(question.questionId).mapNotNull { it.toLongOrNull() }.toSet()
    val correctIds = answers.filter { it.correct == true }.map { it.id }.toSet()
    val isBlank = if (question.type == "FILL_BLANK") {
        ExamAttemptStore.fillAnswer(question.questionId).isBlank()
    } else {
        selectedIds.isEmpty()
    }
    val isCorrect = when (question.type) {
        "FILL_BLANK" -> {
            val userAnswer = ExamAttemptStore.fillAnswer(question.questionId).trim()
            if (userAnswer.isBlank()) false else answers.any { it.correct == true && it.content.trim().equals(userAnswer, ignoreCase = true) }
        }
        else -> selectedIds == correctIds
    }
    val statusColor = when {
        isBlank -> AppMuted
        isCorrect -> AppMint
        else -> AppRed
    }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, statusColor.copy(alpha = 0.3f), MaterialTheme.shapes.large)
    ) {
        Row {
            Box(
                Modifier
                    .width(4.dp)
                    .height(110.dp)
                    .background(statusColor)
            )
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        question.content,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    ChipText(
                        when {
                            isBlank -> "Blank"
                            isCorrect -> "Correct"
                            else -> "Wrong"
                        },
                        statusColor
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text("Your answer: ${backendSelectedAnswerText(question)}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                Text("Correct: ${backendCorrectAnswerText(question)}", color = AppMint, style = MaterialTheme.typography.bodyMedium)
                if (answers.any { !it.explanation.isNullOrBlank() }) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        answers.firstNotNullOfOrNull { it.explanation } ?: "",
                        color = AppMuted,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

private fun backendSelectedAnswerText(question: ExamQuestionResponse): String {
    return if (question.type == "FILL_BLANK") {
        ExamAttemptStore.fillAnswer(question.questionId).takeIf { it.isNotBlank() } ?: "—"
    } else {
        val selectedIds = ExamAttemptStore.selectedAnswerIds(question.questionId).mapNotNull { it.toLongOrNull() }.toSet()
        val selectedAnswers = question.answers.orEmpty().filter { it.id in selectedIds }.map { it.content }
        if (selectedAnswers.isEmpty()) "—" else selectedAnswers.joinToString()
    }
}

private fun backendCorrectAnswerText(question: ExamQuestionResponse): String {
    val correctAnswers = question.answers.orEmpty().filter { it.correct == true }.map { it.content }
    return if (correctAnswers.isEmpty()) "—" else correctAnswers.joinToString()
}

private fun backendAttemptSummary(): AttemptScore {
    val questions = ExamAttemptStore.backendQuestions.value
    if (questions.isEmpty()) {
        return AttemptScore(0.0, 0, 0, 0)
    }
    var correct = 0
    var blank = 0

    questions.forEach { question ->
        val selectedIds = ExamAttemptStore.selectedAnswerIds(question.questionId).mapNotNull { it.toLongOrNull() }.toSet()
        val correctIds = question.answers.orEmpty().filter { it.correct == true }.map { it.id }.toSet()
        when (question.type) {
            "FILL_BLANK" -> {
                val userAnswer = ExamAttemptStore.fillAnswer(question.questionId).trim()
                if (userAnswer.isBlank()) {
                    blank++
                } else if (question.answers.orEmpty().any { it.correct == true && it.content.trim().equals(userAnswer, ignoreCase = true) }) {
                    correct++
                }
            }
            else -> {
                if (selectedIds.isEmpty()) {
                    blank++
                } else if (selectedIds == correctIds) {
                    correct++
                }
            }
        }
    }

    val total = questions.size
    val wrong = total - correct - blank
    val score = if (total == 0) 0.0 else correct * 10.0 / total
    return AttemptScore(score, correct, wrong, blank)
}

@Composable
private fun ResultScreenLegacy(onBack: () -> Unit) {
    val score = ExamAttemptStore.score()
    var backendResult by remember { mutableStateOf<ExamResultResponse?>(null) }
    var backendMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization != null) {
            try {
                val response = ApiClient.getResult(authorization, ExamAttemptStore.backendExamId)
                backendResult = response.data
                backendMessage = if (response.success) "Result loaded." else response.message
            } catch (exception: Exception) { backendMessage = "Result details are not available yet." }
        }
    }

    AppBackground {
        ExamTopBar("Results", onBack)

        // Score hero
        Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .background(HeroGradient)
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .padding(28.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("%.1f".format(score.score), color = Color.White, style = MaterialTheme.typography.displayLarge)
                    Text("out of 10", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(score.correct.toString(), color = AppMint, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Correct", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(score.wrong.toString(), color = AppCoral, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Wrong", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(score.blank.toString(), color = AppAmber, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Blank", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        if (backendMessage != null) {
            Spacer(Modifier.height(10.dp))
            InfoBanner(
                backendMessage.orEmpty(),
                if (backendResult != null) AppMint else AppMuted,
                if (backendResult != null) Icons.Default.CloudDone else Icons.Default.Info
            )
        }
        if (backendResult != null) {
            Text("Status: ${backendResult?.status} · submitted ${backendResult?.submittedAt ?: "--"}", color = AppMuted, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 6.dp, start = 4.dp))
        }

        SectionTitle("Answer Details")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
            items(MockData.questions) { question ->
                val userAnswers = ExamAttemptStore.selectedAnswerIds(question.id)
                val correctAnswers = question.answers.filter { it.correct }.map { it.id }.toSet()
                val isCorrect = userAnswers == correctAnswers
                val isBlank = userAnswers.isEmpty()
                val statusColor = when {
                    isBlank -> AppMuted
                    isCorrect -> AppMint
                    else -> AppRed
                }

                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, statusColor.copy(alpha = 0.3f), MaterialTheme.shapes.large)
                ) {
                    Row {
                        Box(
                            Modifier
                                .width(4.dp)
                                .height(110.dp)
                                .background(statusColor)
                        )
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Q${question.id}: ${question.content}", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                ChipText(
                                    when {
                                        isBlank -> "Blank"
                                        isCorrect -> "Correct"
                                        else -> "Wrong"
                                    },
                                    statusColor
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Your answer: ${userAnswers.ifEmpty { setOf("—") }.joinToString()}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                            Text("Correct: ${correctAnswers.joinToString()}", color = AppMint, style = MaterialTheme.typography.bodyMedium)
                            if (question.explanation.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(question.explanation, color = AppMuted, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
