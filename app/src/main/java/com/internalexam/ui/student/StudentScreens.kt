package com.internalexam.ui.student

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.internalexam.data.ExamAttemptStore
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.ExamResponse
import com.internalexam.data.network.ExamResultDetailResponse
import com.internalexam.data.network.ExamSubmitRequest
import com.internalexam.model.mock.NetworkState
import com.internalexam.monitor.AppLifecycleMonitor
import com.internalexam.monitor.NetworkMonitor
import com.internalexam.monitor.ProctoringEventBuffer
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
import com.internalexam.ui.exam.UiState
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppBlue
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMint
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppSurface
import com.internalexam.ui.theme.AppText
import com.internalexam.ui.theme.BgGradient
import com.internalexam.ui.theme.HeroGradient
import com.internalexam.ui.theme.InternalExamTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun StudentHomeScreen(
    onLobby: (ExamResponse) -> Unit,
    onOpenResult: (ExamResponse) -> Unit,
    onSeeAllResults: () -> Unit,
    onOpenSchedule: () -> Unit,
    viewModel: StudentHomeViewModel = viewModel()
) {
    val homeUiState by viewModel.state.collectAsState()
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Chào buổi sáng"
        hour < 17 -> "Chào buổi chiều"
        else -> "Chào buổi tối"
    }

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    val state = (homeUiState as? UiState.Success)?.data
    val isLoading = homeUiState is UiState.Loading
    val studentName = state?.studentName ?: "Học sinh"
    val exams = state?.exams.orEmpty()
    val examMetadata = state?.examMetadata.orEmpty()
    val recentResults = state?.recentResults.orEmpty()
    val submittedExamIds = state?.submittedExamIds.orEmpty()
    val loadMessage = (homeUiState as? UiState.Error)?.message

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
                AvatarCircle(studentName, 50, AppIndigo)
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(greeting, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                    Text(studentName, style = MaterialTheme.typography.headlineMedium)
                }
            }
            StatusPill(if (NetworkMonitor.isOnline.value) NetworkState.ONLINE else NetworkState.OFFLINE)
        }

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                .clickable { onOpenSchedule() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(AppIndigo.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = AppIndigo, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Lịch thi theo tháng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppText)
                        Text("Xem tổng quan các ca thi đã lên lịch", style = MaterialTheme.typography.bodySmall, color = AppMuted)
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AppMuted)
            }
        }

        SectionTitle("Phòng thi")
        if (loadMessage != null) {
            InfoBanner(loadMessage, AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(12.dp))
        }

        if (isLoading) {
            LoadingStateCard("Đang tải đề thi...")
        } else if (exams.isEmpty() && loadMessage == null) {
            EmptyExamState()
        } else {
            exams.forEach { exam ->
                val isSubmitted = exam.id in submittedExamIds
                StudentExamRoomCard(
                    exam = exam,
                    metadata = examMetadata[exam.id],
                    onEnter = { onLobby(exam) },
                    onViewResult = if (isSubmitted) {{ onOpenResult(exam) }} else null
                )
            }
        }

        SectionTitle("Kết quả gần đây")
        if (isLoading) {
            LoadingStateCard("Đang tải kết quả...")
        } else if (recentResults.isEmpty()) {
            InfoBanner("Chưa có kết quả bài thi.", AppAmber, Icons.Default.Info)
        } else {
            recentResults.take(3).forEach { item ->
                StudentResultCard(item, onClick = { onOpenResult(item.exam) })
                Spacer(Modifier.height(8.dp))
            }
            TextButton(onClick = onSeeAllResults) { Text("Xem tất cả kết quả") }
        }
        Spacer(Modifier.height(ScreenBottomPadding))
    }
}

@Composable
fun StudentExamsScreen(onLobby: (ExamResponse) -> Unit, onOpenResult: (ExamResponse) -> Unit, onBack: () -> Unit) {
    var exams by remember { mutableStateOf<List<ExamResponse>>(emptyList()) }
    var examMetadata by remember { mutableStateOf<Map<Long, StudentExamMetadata>>(emptyMap()) }
    var submittedExamIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            isLoading = false
            return@LaunchedEffect
        }
        try {
            val visibleExams = ApiClient.getExams(authorization).data.orEmpty().filterNot { it.isExpired() }
            val subjectNamesById = runCatching {
                ApiClient.getSubjects(authorization).data.orEmpty().associate { it.id to it.name }
            }.getOrDefault(emptyMap())

            val metadata = coroutineScope {
                visibleExams.map { exam ->
                    async {
                        val countFromExam = exam.questionCount ?: exam.totalQuestions
                        val subjectFromExam = exam.subjectName ?: exam.subject
                        if (countFromExam != null && !subjectFromExam.isNullOrBlank()) {
                            exam.id to StudentExamMetadata(countFromExam, subjectFromExam)
                        } else {
                            val questions = runCatching {
                                ApiClient.getExamQuestions(authorization, exam.id).data.orEmpty()
                            }.getOrDefault(emptyList())
                            val subjectIds = questions.mapNotNull { it.subjectId }.distinct()
                            val subjectName = when {
                                subjectIds.size == 1 -> subjectNamesById[subjectIds.first()]
                                subjectIds.size > 1 -> "Nhiều môn"
                                else -> subjectFromExam
                            }
                            exam.id to StudentExamMetadata(
                                questionCount = questions.size.takeIf { it > 0 } ?: countFromExam,
                                subjectName = subjectName
                            )
                        }
                    }
                }.awaitAll().toMap()
            }

            exams = visibleExams
            examMetadata = metadata

            val submittedIds = coroutineScope {
                visibleExams.map { exam ->
                    async {
                        runCatching {
                            ApiClient.getResult(authorization, exam.id).data
                        }.getOrNull()?.let { result ->
                            if (result.status == "SUBMITTED") exam.id else null
                        }
                    }
                }.awaitAll().filterNotNull().toSet()
            }
            submittedExamIds = submittedIds
            message = null
        } catch (exception: Exception) {
            message = "Không tải được đề thi lúc này."
        } finally {
            isLoading = false
        }
    }

    AppBackground {
        ExamTopBar("Phòng thi", onBack)
        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(12.dp))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (isLoading) {
                LoadingStateCard("Đang tải đề thi...")
            } else if (exams.isEmpty()) {
                EmptyExamState()
            } else {
                exams.forEach { exam ->
                    StudentExamRoomCard(
                        exam = exam,
                        metadata = examMetadata[exam.id],
                        onEnter = { onLobby(exam) },
                        onViewResult = if (exam.id in submittedExamIds) {{ onOpenResult(exam) }} else null
                    )
                }
            }
            Spacer(Modifier.height(ScreenBottomPadding))
        }
    }
}

@Composable
fun StudentResultsScreen(onOpenResult: (ExamResponse) -> Unit, onBack: () -> Unit) {
    var results by remember { mutableStateOf<List<StudentRecentResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            isLoading = false
            return@LaunchedEffect
        }
        try {
            results = fetchAllStudentResults(authorization)
            message = null
        } catch (exception: Exception) {
            message = "Không tải được kết quả lúc này."
        } finally {
            isLoading = false
        }
    }

    AppBackground {
        ExamTopBar("Kết quả", onBack)
        SectionTitle("Tất cả kết quả")
        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(12.dp))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (isLoading) {
                LoadingStateCard("Đang tải kết quả...")
            } else if (results.isEmpty()) {
                EmptyResultsState()
            } else {
                results.forEach { item ->
                    StudentResultCard(item, onClick = { onOpenResult(item.exam) })
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(ScreenBottomPadding))
        }
    }
}

@Composable
fun ExamLobbyScreen(onStart: () -> Unit, onBack: () -> Unit) {
    val exam = ExamAttemptStore.selectedExam.value
    AppBackground {
        ExamTopBar("Phòng thi", onBack)
        GradientHero(
            exam?.title ?: "Chưa chọn đề thi",
            exam?.let { "Mã đề ${it.code}" } ?: "Vui lòng quay lại danh sách phòng thi để chọn đề."
        ) { StatusPill(if (NetworkMonitor.isOnline.value) NetworkState.ONLINE else NetworkState.OFFLINE) }

        SectionTitle("Trạng thái đề thi")
        MetricCard(
            "Gói đề thi",
            if (exam != null) "Sẵn sàng" else "Chưa sẵn sàng",
            if (exam != null) "Có thể bắt đầu làm bài" else "Chưa có đề để bắt đầu",
            if (exam != null) AppMint else AppAmber,
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
                RuleItem("Câu hỏi sẽ hiển thị sau khi bắt đầu làm bài", Icons.Default.Info, AppIndigo)
                Surface(
                    color = AppRed.copy(alpha = 0.05f),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RuleItem("Không rời ứng dụng trong lúc làm bài", Icons.Default.Warning, AppRed)
                }
                RuleItem("Chỉ đăng nhập và làm bài trên một thiết bị", Icons.Default.PhoneAndroid, AppAmber)
            }
        }

        Spacer(Modifier.weight(1f))
        PrimaryAction("Bắt đầu làm bài", enabled = exam != null, onClick = onStart)
        Spacer(Modifier.height(ScreenBottomPadding))
    }
}

@Composable
fun ExamTakingScreen(
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onAutoSubmitted: () -> Unit = onSubmit
) {
    var index by remember { mutableIntStateOf(0) }
    var loadMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showExitConfirm by remember { mutableStateOf(false) }
    var hasAutoSubmitted by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val backendQuestions = ExamAttemptStore.backendQuestions.value
    val totalQuestions = backendQuestions.size
    val safeIndex = index.coerceIn(0, (totalQuestions - 1).coerceAtLeast(0))
    val selectedExam = ExamAttemptStore.selectedExam.value
    val durationMinutes = (selectedExam?.durationMinutes ?: 45).coerceAtLeast(1)
    var timeLeftSeconds by remember(selectedExam?.id) { mutableIntStateOf(durationMinutes * 60) }
    val progress = if (totalQuestions > 0) ExamAttemptStore.answeredCount.toFloat() / totalQuestions else 0f

    suspend fun submitBecauseTimeExpired() {
        ProctoringEventBuffer.flush()
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            loadMessage = "Hết giờ nhưng phiên đăng nhập không còn hợp lệ."
            onSubmit()
            return
        }
        val note = "Tự động nộp bài khi hết giờ với ${ExamAttemptStore.answeredCount} câu đã trả lời."
        try {
            ApiClient.submitExam(
                authorization,
                ExamAttemptStore.backendExamId,
                ExamSubmitRequest(note = note, answers = ExamAttemptStore.backendSubmitAnswers())
            )
            ExamAttemptStore.reset()
            onAutoSubmitted()
        } catch (exception: Exception) {
            ExamAttemptStore.savePendingSubmission(ExamAttemptStore.backendExamId, note)
            loadMessage = "Hết giờ. Bài làm đã được lưu tạm thời, sẽ tự động nộp khi có mạng."
            onSubmit()
        }
    }

    LaunchedEffect(selectedExam?.id, isLoading, totalQuestions) {
        if (isLoading || totalQuestions == 0) return@LaunchedEffect
        ExamAttemptStore.ensureAttemptDeadline(durationMinutes)
        while (true) {
            val endAt = ExamAttemptStore.attemptEndAtMillis
            val remaining = if (endAt != null) {
                ((endAt - System.currentTimeMillis()) / 1000L).toInt()
            } else {
                durationMinutes * 60
            }
            timeLeftSeconds = remaining.coerceAtLeast(0)
            if (remaining <= 0) {
                if (!hasAutoSubmitted) {
                    hasAutoSubmitted = true
                    submitBecauseTimeExpired()
                }
                break
            }
            delay(1000L)
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
                    loadMessage = "Đã tải ${questions.size} câu hỏi."
                } else {
                    loadMessage = "Đề thi này chưa có câu hỏi."
                }
            } catch (exception: Exception) {
                loadMessage = "Không tải được câu hỏi lúc này."
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    LaunchedEffect(ExamAttemptStore.backendExamId) {
        AppLifecycleMonitor.start(ExamAttemptStore.backendExamId)
    }

    DisposableEffect(Unit) {
        onDispose { AppLifecycleMonitor.stop() }
    }

    BackHandler(enabled = !showExitConfirm) { showExitConfirm = true }

    AppBackground {
        ExamTopBar(selectedExam?.title ?: "Đề thi") { showExitConfirm = true }

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
            StatusPill(if (NetworkMonitor.isOnline.value) NetworkState.ONLINE else NetworkState.OFFLINE)
        }

        if (!NetworkMonitor.isOnline.value) {
            Spacer(Modifier.height(6.dp))
            InfoBanner("Mất kết nối mạng. Đáp án vẫn được lưu trên thiết bị.", AppRed, Icons.Default.WifiOff)
        }

        Spacer(Modifier.height(10.dp))
        StyledProgress(progress, AppIndigo)
        Spacer(Modifier.height(14.dp))
        if (loadMessage != null) {
            Text(loadMessage.orEmpty(), color = AppMint, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (isLoading) {
                LoadingStateCard("Đang tải câu hỏi...")
            } else if (totalQuestions == 0) {
                InfoBanner("Đề thi này chưa có câu hỏi.", AppAmber, Icons.Default.Info)
            } else {
                AnimatedContent(
                    targetState = safeIndex,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "QuestionTransition"
                ) { questionIndex ->
                    val question = backendQuestions[questionIndex]
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = AppSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                    ) {
                        Column(Modifier.padding(18.dp)) {
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
                            if (question.imageUrl != null) {
                                Spacer(Modifier.height(12.dp))
                                AsyncImage(
                                    model = if (question.imageUrl.orEmpty().startsWith("http")) question.imageUrl else "http://192.168.119.146:8080${question.imageUrl}",
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth().heightIn(max = 250.dp).clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Spacer(Modifier.height(16.dp))

                            if (question.type == "FILL_BLANK") {
                                OutlinedTextField(
                                    value = ExamAttemptStore.fillAnswer(question.questionId),
                                    onValueChange = { ExamAttemptStore.setFillAnswer(question.questionId, it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Câu trả lời của bạn") },
                                    shape = MaterialTheme.shapes.medium
                                )
                            } else {
                                question.answers.orEmpty().forEachIndexed { answerIndex, answer ->
                                    val selected = ExamAttemptStore.selectedAnswerIds(question.questionId).contains(answer.id.toString())
                                    val borderColor = if (selected) AppIndigo else AppCardBorder
                                    val bgColor = if (selected) AppIndigo.copy(alpha = 0.06f) else Color.Transparent

                                    Surface(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                        ExamAttemptStore.selectBackendAnswer(question.questionId, answer.id, question.type)
                                                    },
                                                    colors = CheckboxDefaults.colors(checkedColor = AppIndigo)
                                                )
                                            } else {
                                                RadioButton(
                                                    selected,
                                                    {
                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                InfoBanner("Vui lòng giữ ứng dụng mở trong lúc thi. Đáp án chỉ được gửi khi bạn nộp bài.", AppAmber, Icons.Default.WifiOff)
            }
        }

        if (!isLoading && totalQuestions > 0) {
            Spacer(Modifier.height(8.dp))
            Text("Chuyển nhanh", style = MaterialTheme.typography.labelMedium, color = AppMuted)
            Spacer(Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(backendQuestions) { i, q ->
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
                Text("Câu trước")
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
                Text("Câu sau")
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(18.dp))
            }
        }

        PrimaryAction("Nộp bài", onClick = { if (totalQuestions > 0) onSubmit() })
        Spacer(Modifier.height(10.dp))
    }

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text("Thoát khỏi bài thi?") },
            text = { Text("Bài thi vẫn đang tính giờ. Đáp án đã chọn được giữ lại, nhưng bạn cần quay lại nộp bài trước khi hết giờ.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitConfirm = false
                    onBack()
                }) { Text("Thoát") }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) { Text("Tiếp tục làm bài") }
            }
        )
    }
}

@Composable
fun SubmitConfirmationScreen(onConfirm: () -> Unit, onBack: () -> Unit) {
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        ProctoringEventBuffer.flush()
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Vui lòng đăng nhập lại."; return }
        scope.launch {
            loading = true; message = null
            val note = "Nộp từ ứng dụng Android với ${ExamAttemptStore.answeredCount} câu đã trả lời."
            try {
                ApiClient.submitExam(
                    authorization,
                    ExamAttemptStore.backendExamId,
                    ExamSubmitRequest(note = note, answers = ExamAttemptStore.backendSubmitAnswers())
                )
                ExamAttemptStore.reset()
                onConfirm()
            } catch (exception: HttpException) {
                message = when (exception.code()) {
                    404 -> "Không tìm thấy đề thi này."
                    401, 403 -> "Bạn không có quyền nộp bài thi này."
                    else -> "Chưa thể nộp bài lúc này. Bài làm đã được lưu tạm thời."
                }
                ExamAttemptStore.savePendingSubmission(ExamAttemptStore.backendExamId, note)
            } catch (exception: Exception) {
                ExamAttemptStore.savePendingSubmission(ExamAttemptStore.backendExamId, note)
                message = "Mất kết nối. Bài làm đã được lưu tạm thời, sẽ tự động nộp khi có mạng."
            } finally { loading = false }
        }
    }

    fun retryPending() {
        val pending = ExamAttemptStore.getPendingSubmission() ?: return
        val authorization = SessionManager.authorizationHeader() ?: return
        scope.launch {
            loading = true; message = null
            try {
                ApiClient.submitExam(
                    authorization,
                    pending.examId,
                    ExamSubmitRequest(note = pending.note, answers = ExamAttemptStore.backendSubmitAnswers())
                )
                ExamAttemptStore.clearPendingSubmission()
                ExamAttemptStore.reset()
                onConfirm()
            } catch (exception: Exception) {
                message = "Chưa thể kết nối. Bài làm vẫn được giữ, thử lại sau."
            } finally { loading = false }
        }
    }

    AppBackground {
        ExamTopBar("Xác nhận nộp bài", onBack)

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
                    Text("Tóm tắt bài làm", color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(ExamAttemptStore.answeredCount.toString(), color = Color.White, style = MaterialTheme.typography.headlineLarge)
                            Text("Đã trả lời", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                        }
                        Box(
                            Modifier
                                .width(1.dp)
                                .height(50.dp)
                                .background(Color.White.copy(alpha = 0.3f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(ExamAttemptStore.unansweredCount.toString(), color = AppAmber, style = MaterialTheme.typography.headlineLarge)
                            Text("Chưa trả lời", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        if (ExamAttemptStore.hasPendingSubmission()) {
            InfoBanner("Bài làm chưa được gửi lên hệ thống do mất kết nối.", AppRed, Icons.Default.Warning)
        } else {
            InfoBanner("Sau khi nộp bài, bạn không thể chỉnh sửa đáp án. Hãy kiểm tra lại trước khi xác nhận.", AppAmber, Icons.Default.Warning)
        }
        if (message != null) {
            Spacer(Modifier.height(10.dp))
            InfoBanner(message.orEmpty(), AppRed, Icons.Default.ErrorOutline)
        }

        Spacer(Modifier.weight(1f))
        PrimaryAction(
            if (loading) "Đang nộp bài..." else if (ExamAttemptStore.hasPendingSubmission()) "Thử lại" else "Nộp bài",
            enabled = !loading,
            onClick = {
                if (ExamAttemptStore.hasPendingSubmission()) retryPending() else showConfirm = true
            }
        )
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier
            .fillMaxWidth()
            .height(48.dp), shape = MaterialTheme.shapes.medium, enabled = !loading) { Text("Quay lại bài làm") }
        Spacer(Modifier.height(ScreenBottomPadding))
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { if (!loading) showConfirm = false },
            title = { Text("Xác nhận nộp bài") },
            text = {
                val unanswered = ExamAttemptStore.unansweredCount
                Text(
                    if (unanswered > 0) {
                        "Bạn còn $unanswered câu chưa trả lời. Xác nhận nộp bài?"
                    } else {
                        "Bạn đã trả lời hết. Xác nhận nộp bài?"
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
                    Text("Nộp bài")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirm = false },
                    enabled = !loading
                ) {
                    Text("Xem lại")
                }
            }
        )
    }
}

@Composable
fun ResultScreen(onBack: () -> Unit) {
    var detail by remember { mutableStateOf<ExamResultDetailResponse?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val fallbackTitle = ExamAttemptStore.selectedExam.value?.title
        ?: ExamAttemptStore.selectedExam.value?.code
        ?: "Kết quả thi"

    LaunchedEffect(ExamAttemptStore.backendExamId) {
        isLoading = true
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            isLoading = false
            return@LaunchedEffect
        }
        try {
            val response = ApiClient.getResultDetail(authorization, ExamAttemptStore.backendExamId)
            detail = response.data
            message = if (response.data != null) null else "Chưa có kết quả."
        } catch (exception: Exception) {
            message = "Chưa có kết quả bài thi."
        } finally {
            isLoading = false
        }
    }

    val examTitle = detail?.examTitle?.takeIf { it.isNotBlank() } ?: fallbackTitle

    AppBackground {
        ExamTopBar("Kết quả", onBack)
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
                    Text(detail?.score ?: "--", color = Color.White, style = MaterialTheme.typography.displayLarge)
                    Text(examTitle, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            if (isLoading) {
                LoadingStateCard("Đang tải kết quả...")
            } else if (message != null) {
                InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            }

            SectionTitle("Tổng quan")
            Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = AppSurface), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Đề thi: $examTitle", fontWeight = FontWeight.Bold)
                    Text("Tổng số câu: ${detail?.totalQuestions ?: "--"}", color = AppMuted)
                    Text("Trạng thái: ${detail?.status?.let { resultStatusLabel(it) } ?: "--"}", color = AppMuted)
                    Text("Nộp lúc: ${resultDate(detail?.submittedAt)}", color = AppMuted)
                }
            }

            val questions = detail?.questions
            if (questions != null) {
                SectionTitle("Chi tiết & Giải thích AI", "Nhấn để xem giải thích bằng AI cho từng câu")
                questions.forEach { question ->
                    AiExplanableQuestionCard(
                        question = question,
                        subject = detail?.examTitle
                    )
                }
            } else if (detail != null) {
                InfoBanner(
                    "Giáo viên chưa mở xem đáp án cho đề thi này.",
                    AppMuted,
                    Icons.Default.Info
                )
            }

            Spacer(Modifier.height(ScreenBottomPadding))
        }
    }
}

@Composable
fun StudentScheduleScreen(
    onLobby: (ExamResponse) -> Unit,
    onOpenResult: (ExamResponse) -> Unit,
    onBack: () -> Unit
) {
    val todayCal = remember { Calendar.getInstance() }
    var selectedYear by remember { mutableIntStateOf(todayCal.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(todayCal.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(todayCal.get(Calendar.DAY_OF_MONTH)) }

    var exams by remember { mutableStateOf<List<ExamResponse>>(emptyList()) }
    var submittedExamIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }

    fun changeMonth(delta: Int) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth + delta)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        selectedYear = cal.get(Calendar.YEAR)
        selectedMonth = cal.get(Calendar.MONTH)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        if (selectedDay > maxDays) {
            selectedDay = maxDays
        }
    }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val scheduledRes = runCatching { ApiClient.getScheduledExams(authorization) }.getOrNull()
            val scheduledExams = scheduledRes?.data.orEmpty()

            val generalRes = runCatching { ApiClient.getExams(authorization) }.getOrNull()
            val generalExams = generalRes?.data.orEmpty()

            // Combine both scheduled and general exams without duplicates
            val allExams = (scheduledExams + generalExams).distinctBy { it.id }
            exams = allExams

            // Auto-navigate calendar to the nearest upcoming scheduled exam date if current month has no exams
            val currentMonthHasExams = allExams.any { exam ->
                val startMs = exam.startTime.parseBackendTimeMillis()
                if (startMs == null) false
                else {
                    val cal = Calendar.getInstance().apply { timeInMillis = startMs }
                    cal.get(Calendar.YEAR) == selectedYear && cal.get(Calendar.MONTH) == selectedMonth
                }
            }

            if (!currentMonthHasExams) {
                val now = System.currentTimeMillis()
                val nearestFutureExam = allExams.filter { exam ->
                    val startMs = exam.startTime.parseBackendTimeMillis()
                    startMs != null && startMs >= now
                }.minByOrNull { it.startTime.parseBackendTimeMillis()!! }

                if (nearestFutureExam != null) {
                    val startMs = nearestFutureExam.startTime.parseBackendTimeMillis()!!
                    val cal = Calendar.getInstance().apply { timeInMillis = startMs }
                    selectedYear = cal.get(Calendar.YEAR)
                    selectedMonth = cal.get(Calendar.MONTH)
                    selectedDay = cal.get(Calendar.DAY_OF_MONTH)
                }
            }

            val submittedIds = mutableSetOf<Long>()
            coroutineScope {
                allExams.map { exam ->
                    async {
                        runCatching {
                            val res = ApiClient.getResult(authorization, exam.id).data
                            if (res?.status == "SUBMITTED") {
                                synchronized(submittedIds) { submittedIds.add(exam.id) }
                            }
                        }
                    }
                }.awaitAll()
            }
            submittedExamIds = submittedIds
            message = null
        } catch (e: Exception) {
            message = "Không thể tải lịch thi lúc này. Vui lòng kiểm tra kết nối."
        } finally {
            isLoading = false
        }
    }

    AppBackground {
        ExamTopBar("Lịch thi theo tháng", onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { changeMonth(-1) }) {
                                Text("‹", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }

                            val monthFmt = SimpleDateFormat("MM, yyyy", Locale("vi", "VN"))
                            val displayCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, selectedYear)
                                set(Calendar.MONTH, selectedMonth)
                                set(Calendar.DAY_OF_MONTH, 1)
                            }
                            Text(
                                text = "Tháng " + monthFmt.format(displayCal.time),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AppText,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            TextButton(onClick = { changeMonth(1) }) {
                                Text("›", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val now = Calendar.getInstance()
                                selectedYear = now.get(Calendar.YEAR)
                                selectedMonth = now.get(Calendar.MONTH)
                                selectedDay = now.get(Calendar.DAY_OF_MONTH)
                            },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Hôm nay", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    val weekdays = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        weekdays.forEach { day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AppMuted
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    val monthCal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, selectedYear)
                        set(Calendar.MONTH, selectedMonth)
                        set(Calendar.DAY_OF_MONTH, 1)
                    }
                    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val firstDayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK)
                    val leadingEmptyCount = (firstDayOfWeek + 5) % 7

                    val totalCells = leadingEmptyCount + daysInMonth
                    val rowCount = (totalCells + 6) / 7

                    val isCurrentMonthToday = todayCal.get(Calendar.YEAR) == selectedYear &&
                            todayCal.get(Calendar.MONTH) == selectedMonth
                    val todayDateNumber = if (isCurrentMonthToday) todayCal.get(Calendar.DAY_OF_MONTH) else -1

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (r in 0 until rowCount) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                for (c in 0 until 7) {
                                    val cellIndex = r * 7 + c
                                    val dayNum = cellIndex - leadingEmptyCount + 1

                                    if (dayNum in 1..daysInMonth) {
                                        val isSelected = dayNum == selectedDay
                                        val isToday = dayNum == todayDateNumber
                                        val dayExams = exams.filter { it.occursOnDate(selectedYear, selectedMonth, dayNum) }
                                        val hasExams = dayExams.isNotEmpty()

                                        val cellBg = if (isSelected) AppIndigo else AppSurface
                                        val textColor = if (isSelected) Color.White else AppText

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                                .padding(2.dp)
                                                .clip(MaterialTheme.shapes.small)
                                                .background(cellBg)
                                                .then(
                                                    if (isToday && !isSelected)
                                                        Modifier.border(1.5.dp, AppIndigo, MaterialTheme.shapes.small)
                                                    else if (!isSelected)
                                                        Modifier.border(0.5.dp, AppCardBorder, MaterialTheme.shapes.small)
                                                    else Modifier
                                                )
                                                .clickable { selectedDay = dayNum },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = dayNum.toString(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                    color = textColor
                                                )
                                                if (hasExams) {
                                                    Spacer(Modifier.height(2.dp))
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        val dotColor = if (isSelected) Color.White else AppMint
                                                        Box(
                                                            modifier = Modifier
                                                                .size(5.dp)
                                                                .background(dotColor, CircleShape)
                                                        )
                                                        if (dayExams.size > 1) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(5.dp)
                                                                    .background(dotColor, CircleShape)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            SectionTitle(
                title = formatSelectedDateHeader(selectedYear, selectedMonth, selectedDay),
                subtitle = "Danh sách ca thi trong ngày"
            )

            if (isLoading) {
                LoadingStateCard("Đang tải danh sách ca thi...")
            } else if (message != null) {
                InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            } else {
                val examsOnSelectedDay = exams.filter {
                    it.occursOnDate(selectedYear, selectedMonth, selectedDay)
                }

                val nextUpcomingExam = remember(exams) {
                    val now = System.currentTimeMillis()
                    exams.filter { exam ->
                        val startMillis = exam.startTime.parseBackendTimeMillis()
                        startMillis != null && startMillis > now
                    }.minByOrNull { it.startTime.parseBackendTimeMillis()!! }
                }

                if (examsOnSelectedDay.isEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.EventBusy, contentDescription = null, tint = AppMuted, modifier = Modifier.size(36.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Không có lịch thi trong ngày này", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        if (nextUpcomingExam != null) {
                            val nextStartMillis = nextUpcomingExam.startTime.parseBackendTimeMillis()
                            if (nextStartMillis != null) {
                                val nextCal = Calendar.getInstance().apply { timeInMillis = nextStartMillis }
                                Card(
                                    shape = MaterialTheme.shapes.large,
                                    colors = CardDefaults.cardColors(containerColor = AppIndigo.copy(alpha = 0.08f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, AppIndigo.copy(alpha = 0.25f), MaterialTheme.shapes.large)
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text("Ca thi tiếp theo:", color = AppIndigo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                        Spacer(Modifier.height(4.dp))
                                        Text(nextUpcomingExam.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppText)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Ngày: %02d/%02d/%04d • Giờ: %s".format(
                                                nextCal.get(Calendar.DAY_OF_MONTH),
                                                nextCal.get(Calendar.MONTH) + 1,
                                                nextCal.get(Calendar.YEAR),
                                                formatExamTimeRange(nextUpcomingExam.startTime, nextUpcomingExam.endTime, nextUpcomingExam.durationMinutes)
                                            ),
                                            color = AppMuted,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(Modifier.height(10.dp))
                                        Button(
                                            onClick = {
                                                selectedYear = nextCal.get(Calendar.YEAR)
                                                selectedMonth = nextCal.get(Calendar.MONTH)
                                                selectedDay = nextCal.get(Calendar.DAY_OF_MONTH)
                                            },
                                            shape = MaterialTheme.shapes.medium,
                                            colors = ButtonDefaults.buttonColors(containerColor = AppIndigo),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Chuyển đến ngày thi này")
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    examsOnSelectedDay.forEach { exam ->
                        val isSubmitted = exam.id in submittedExamIds
                        val isOpenNow = exam.isOpenNow()
                        val isExpired = exam.isExpired()

                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = AppSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        ChipText(exam.subjectName ?: exam.subject ?: "Môn học", AppBlue)
                                        ChipText("Mã: ${exam.code}", AppIndigo)
                                    }
                                    val statusText = when {
                                        isSubmitted -> "Đã nộp"
                                        isOpenNow -> "Đang diễn ra"
                                        isExpired -> "Đã kết thúc"
                                        else -> "Sắp diễn ra"
                                    }
                                    val statusColor = when {
                                        isSubmitted -> AppMint
                                        isOpenNow -> AppMint
                                        isExpired -> AppMuted
                                        else -> AppIndigo
                                    }
                                    ChipText(statusText, statusColor)
                                }

                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = exam.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AppText
                                )

                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = AppMuted, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            formatExamTimeRange(exam.startTime, exam.endTime, exam.durationMinutes),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AppMuted
                                        )
                                    }
                                    exam.durationMinutes?.let { dur ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Timer, contentDescription = null, tint = AppMuted, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("$dur phút", style = MaterialTheme.typography.bodyMedium, color = AppMuted)
                                        }
                                    }
                                }

                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = AppCardBorder)
                                Spacer(Modifier.height(12.dp))

                                if (isSubmitted) {
                                    OutlinedButton(
                                        onClick = { onOpenResult(exam) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Xem kết quả bài thi")
                                    }
                                } else if (!isExpired) {
                                    Button(
                                        onClick = { onLobby(exam) },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium,
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isOpenNow) AppMint else AppIndigo)
                                    ) {
                                        Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text(if (isOpenNow) "Vào phòng thi ngay" else "Chi tiết ca thi")
                                    }
                                } else {
                                    Text(
                                        "Đã hết thời gian làm bài thi.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AppMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(ScreenBottomPadding))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentScheduleScreenPreview() {
    InternalExamTheme {
        StudentScheduleScreen(
            onLobby = {},
            onOpenResult = {},
            onBack = {}
        )
    }
}
