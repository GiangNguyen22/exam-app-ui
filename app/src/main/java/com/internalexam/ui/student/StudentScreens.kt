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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.internalexam.data.ExamAttemptStore
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.monitor.AppLifecycleMonitor
import com.internalexam.monitor.ExamEventBuffer
import com.internalexam.monitor.NetworkMonitor
import com.internalexam.data.network.ExamResponse
import com.internalexam.data.network.ExamQuestionResponse
import com.internalexam.data.network.ExamResultDetailResponse
import com.internalexam.data.network.ExamResultResponse
import com.internalexam.data.network.ExamSubmitRequest
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun StudentHomeScreen(
    onLobby: (ExamResponse) -> Unit,
    onOpenResult: (ExamResponse) -> Unit,
    onSeeAllResults: () -> Unit
) {
    var exams by remember { mutableStateOf<List<ExamResponse>>(emptyList()) }
    var examMetadata by remember { mutableStateOf<Map<Long, StudentExamMetadata>>(emptyMap()) }
    var recentResults by remember { mutableStateOf<List<StudentRecentResult>>(emptyList()) }
    var submittedExamIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var loadMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var studentName by remember { mutableStateOf("Học sinh") }
    var networkState by remember { mutableStateOf(NetworkState.SYNCING) }
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Chào buổi sáng"
        hour < 17 -> "Chào buổi chiều"
        else -> "Chào buổi tối"
    }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            loadMessage = "Vui lòng đăng nhập lại."
            networkState = NetworkState.OFFLINE
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        // #7b: lấy tên thật của người dùng hiện tại.
        runCatching { ApiClient.getCurrentUser(authorization).data }.getOrNull()?.let { profile ->
            studentName = profile.fullName.takeIf { it.isNotBlank() } ?: profile.username
        }
        try {
            val response = ApiClient.getExams(authorization)
            val loadedExams = response.data.orEmpty()
            val visibleExams = loadedExams.filterNot { it.isExpired() }
            exams = visibleExams
            val subjectNamesById = runCatching {
                ApiClient.getSubjects(authorization).data.orEmpty().associate { it.id to it.name }
            }.getOrDefault(emptyMap())

            // #14: tải metadata + kết quả của từng đề song song thay vì tuần tự.
            coroutineScope {
                val metadataDeferred = visibleExams.map { exam ->
                    async {
                        val countFromExam = exam.questionCount ?: exam.totalQuestions
                        val subjectFromExam = exam.subjectName ?: exam.subject
                        if (countFromExam != null && !subjectFromExam.isNullOrBlank()) {
                            exam.id to StudentExamMetadata(
                                questionCount = countFromExam,
                                subjectName = subjectFromExam
                            )
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
                }
                val resultsDeferred = loadedExams.map { exam ->
                    async {
                        runCatching {
                            ApiClient.getResult(authorization, exam.id).data?.let { result ->
                                StudentRecentResult(exam, result)
                            }
                        }.getOrNull()
                    }
                }
                examMetadata = metadataDeferred.awaitAll().toMap()
                recentResults = resultsDeferred.awaitAll().filterNotNull()
                    .sortedByDescending { it.result.submittedAt.orEmpty() }
                val submittedIds = recentResults.filter { it.result.status == "SUBMITTED" }.map { it.exam.id }.toSet()
                submittedExamIds = submittedIds
            }
            loadMessage = null
            networkState = NetworkState.ONLINE
        } catch (exception: Exception) {
            loadMessage = "Không tải được đề thi lúc này."
            networkState = NetworkState.OFFLINE
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
                AvatarCircle(studentName, 50, AppIndigo)
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(greeting, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                    Text(studentName, style = MaterialTheme.typography.headlineMedium)
                }
            }
            StatusPill(networkState)
        }

        SectionTitle("Phòng thi")
        if (loadMessage != null) {
            InfoBanner(loadMessage.orEmpty(), AppAmber, Icons.Default.Info)
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

/** Thẻ một phòng thi, dùng chung cho màn Home và màn danh sách đề thi. */
@Composable
private fun StudentExamRoomCard(
    exam: ExamResponse,
    metadata: StudentExamMetadata?,
    onEnter: () -> Unit,
    onViewResult: (() -> Unit)? = null
) {
    val canEnterExam = exam.isOpenNow() && onViewResult == null
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
                if (!canEnterExam && onViewResult == null) {
                    Spacer(Modifier.height(8.dp))
                    ChipText("Chưa mở", AppAmber)
                }
                Spacer(Modifier.height(14.dp))
                if (onViewResult != null) {
                    Button(
                        onClick = onViewResult,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = AppMint)
                    ) {
                        Text("Xem kết quả")
                    }
                } else {
                    Button(
                        onClick = onEnter,
                        enabled = canEnterExam,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)
                    ) {
                        Text(if (canEnterExam) "Vào phòng thi" else "Chưa tới giờ mở")
                    }
                }
            }
        }
    }
}

/** Tải danh sách phòng thi (đã lọc đề hết hạn) + metadata môn/số câu, song song. */
private suspend fun loadStudentExamRooms(
    authorization: String
): Pair<List<ExamResponse>, Map<Long, StudentExamMetadata>> {
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
    return visibleExams to metadata
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
            val (loadedExams, metadata) = loadStudentExamRooms(authorization)
            exams = loadedExams
            examMetadata = metadata
            coroutineScope {
                val submittedIds = loadedExams.map { exam ->
                    async {
                        runCatching {
                            ApiClient.getResult(authorization, exam.id).data
                        }.getOrNull()?.let { result ->
                            if (result.status == "SUBMITTED") exam.id else null
                        }
                    }
                }.awaitAll().filterNotNull().toSet()
                submittedExamIds = submittedIds
            }
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
private fun StudentResultCard(item: StudentRecentResult, onClick: () -> Unit) {
    val scoreColor = scoreColor(item.result.score)
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
            .clickable { onClick() }
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
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AppMuted, modifier = Modifier.size(20.dp))
        }
    }
}

/** Tải kết quả của mọi đề (song song) cho danh sách "Tất cả kết quả". */
private suspend fun loadStudentResults(authorization: String): List<StudentRecentResult> {
    val exams = ApiClient.getExams(authorization).data.orEmpty()
    return coroutineScope {
        exams.map { exam ->
            async {
                runCatching {
                    ApiClient.getResult(authorization, exam.id).data?.let { StudentRecentResult(exam, it) }
                }.getOrNull()
            }
        }.awaitAll().filterNotNull()
            .sortedByDescending { it.result.submittedAt.orEmpty() }
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
            results = loadStudentResults(authorization)
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
private fun EmptyResultsState() {
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
                Icon(Icons.Default.Assessment, contentDescription = null, tint = AppAmber, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.height(14.dp))
            Text("Chưa có kết quả bài thi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Hoàn thành một bài thi để xem kết quả ở đây.", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
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

private fun ExamResponse.isExpired(now: Long = System.currentTimeMillis()): Boolean {
    val endAt = endTime.parseBackendTimeMillis() ?: return false
    return endAt <= now
}

private fun ExamResponse.isOpenNow(now: Long = System.currentTimeMillis()): Boolean {
    val startAt = startTime.parseBackendTimeMillis()
    val endAt = endTime.parseBackendTimeMillis()
    return (startAt == null || startAt <= now) && (endAt == null || endAt > now)
}

private fun String?.parseBackendTimeMillis(): Long? {
    val raw = this?.trim()?.takeIf { it.isNotBlank() } ?: return null
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss",
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
        ExamEventBuffer.flush()
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

    // Đồng hồ neo theo mốc kết thúc tuyệt đối -> không reset khi điều hướng qua lại,
    // và tự động nộp bài khi hết giờ.
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
                // Question card
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
                                    model = question.imageUrl,
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

        // #10: thanh chuyển nhanh được ghim cố định, luôn thấy mà không cần cuộn.
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
        ExamEventBuffer.flush()
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
            message = if (response.data != null) null else (response.message ?: "Chưa có kết quả.")
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
            Spacer(Modifier.height(ScreenBottomPadding))
        }
    }
}
