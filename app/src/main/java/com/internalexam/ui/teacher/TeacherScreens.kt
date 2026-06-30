package com.internalexam.ui.teacher

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.internalexam.data.SessionManager
import com.internalexam.data.ExamAttemptStore
import com.internalexam.data.examimport.ExamExcelImportService
import com.internalexam.data.examimport.ExamExcelParser
import com.internalexam.data.examimport.ExamExcelQuestionRow
import com.internalexam.data.examimport.ExamExcelTemplate
import com.internalexam.data.questionimport.QuestionExcelTemplate
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.AuditLogResponse
import com.internalexam.data.network.AnswerCreateRequest
import com.internalexam.data.network.StudentGroupResponse
import com.internalexam.data.network.ExamCreateRequest
import com.internalexam.data.network.ExamGenerateRequest
import com.internalexam.data.network.ExamReportItemResponse
import com.internalexam.data.network.ExamReportResponse
import com.internalexam.data.network.ExamQuestionCreateRequest
import com.internalexam.data.network.ExamQuestionResponse
import com.internalexam.data.network.ExamResponse
import com.internalexam.data.network.ExamUpdateRequest
import com.internalexam.data.network.QuestionCreateRequest
import com.internalexam.data.network.QuestionResponse
import com.internalexam.data.network.SubjectCreateRequest
import com.internalexam.data.network.SubjectResponse
import com.internalexam.data.network.TopicCreateRequest
import com.internalexam.data.network.TopicResponse
import com.internalexam.model.mock.CandidateStatus
import com.internalexam.model.mock.Difficulty
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.NetworkState
import com.internalexam.model.mock.QuestionType
import com.internalexam.ui.components.AppBackground
import com.internalexam.ui.components.AvatarCircle
import com.internalexam.ui.components.ChipText
import com.internalexam.ui.components.ExamTopBar
import com.internalexam.ui.components.GradientHero
import com.internalexam.ui.components.InfoBanner
import com.internalexam.ui.components.LoadingStateCard
import com.internalexam.ui.components.MetricCard
import com.internalexam.ui.components.PrimaryAction
import com.internalexam.ui.components.ScreenBottomPadding
import com.internalexam.ui.components.SectionTitle
import com.internalexam.ui.components.StatusPill
import com.internalexam.ui.components.StyledProgress
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppBlue
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppLilac
import com.internalexam.ui.theme.AppMint
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppSurface
import com.internalexam.ui.theme.AppText
import com.internalexam.ui.theme.AppViolet
import com.internalexam.ui.theme.BgGradient
import com.google.gson.Gson
import com.internalexam.data.network.ApiResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@Composable
fun TeacherExamListScreen(
    onCreateExam: () -> Unit,
    onAddQuestion: (ExamResponse) -> Unit,
    onViewQuestions: (ExamResponse) -> Unit,
    onEditExam: (ExamResponse) -> Unit,
    onBack: () -> Unit
) {
    var exams by remember { mutableStateOf<List<ExamResponse>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var pendingDeleteExam by remember { mutableStateOf<ExamResponse?>(null) }
    val selectedExam = ExamAttemptStore.selectedExam.value
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }

    fun deleteExam(exam: ExamResponse) {
        scope.launch {
            val authorization = SessionManager.authorizationHeader()
            if (authorization == null) {
                message = "Vui lòng đăng nhập lại."
                return@launch
            }
            try {
                val response = ApiClient.deleteExam(authorization, exam.id)
                if (response.success) {
                    exams = exams.filterNot { it.id == exam.id }
                    message = if (exams.isEmpty()) "Đã xóa đề thi. Không còn đề thi nào." else "Đã xóa đề thi."
                } else {
                    message = response.message.ifBlank { "Không thể xóa đề thi lúc này." }
                }
            } catch (exception: HttpException) {
                val backendMessage = runCatching {
                    val body = exception.response()?.errorBody()?.string().orEmpty()
                    gson.fromJson(body, ApiResponse::class.java)?.message
                }.getOrNull().orEmpty()
                message = backendMessage.ifBlank { "Không thể xóa đề thi lúc này." }
            } catch (exception: Exception) {
                message = "Không thể xóa đề thi lúc này."
            }
        }
    }

    LaunchedEffect(selectedExam) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val response = ApiClient.getExams(authorization)
            exams = response.data.orEmpty()
            message = if (exams.isEmpty()) "Chưa có đề thi. Hãy tạo đề thi mới." else null
        } catch (exception: Exception) {
            message = "Không thể tải đề thi lúc này."
        } finally {
            isLoading = false
        }
    }

    AppBackground {
        pendingDeleteExam?.let { exam ->
            ConfirmActionDialog(
                title = "Xóa đề thi",
                message = "Xóa đề thi ${exam.code}? Hành động này không thể hoàn tác.",
                confirmLabel = if (isLoading) "Đang xóa..." else "Xóa",
                destructive = true,
                processing = isLoading,
                onConfirm = {
                    pendingDeleteExam = null
                    deleteExam(exam)
                },
                onDismiss = { pendingDeleteExam = null }
            )
        }

        ExamTopBar("Đề thi", onBack)
        SectionTitle("Danh sách đề thi", "Chọn đề thi để thêm câu hỏi")
        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(12.dp))
        }
        if (isLoading) {
            LoadingStateCard("Đang tải đề thi...")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(exams) { exam ->
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = AppSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(exam.title, style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(4.dp))
                            Text("Mã ${exam.code} - ID ${exam.id}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(onClick = { onViewQuestions(exam) }, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Câu hỏi")
                                }
                                OutlinedButton(onClick = { onAddQuestion(exam) }, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Thêm")
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(onClick = { onEditExam(exam) }, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Sửa")
                                }
                                OutlinedButton(onClick = { pendingDeleteExam = exam }, modifier = Modifier.weight(1f)) {
                                    Text("Xóa")
                                }
                            }
                        }
                    }
                }
            }
        }
        PrimaryAction("Tạo đề thi", onClick = onCreateExam)
    }
}

@Composable
fun ExamQuestionListScreen(
    onAddQuestion: () -> Unit,
    onEditQuestion: (ExamQuestionResponse) -> Unit,
    onBack: () -> Unit
) {
    val exam = ExamAttemptStore.selectedExam.value
    var questions by remember { mutableStateOf<List<ExamQuestionResponse>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(exam?.id) {
        val selectedExam = exam
        if (selectedExam == null) {
            message = "Vui lòng chọn đề thi."
            isLoading = false
            return@LaunchedEffect
        }
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val response = ApiClient.getExamQuestions(authorization, selectedExam.id)
            questions = response.data.orEmpty()
            ExamAttemptStore.setBackendQuestions(questions)
            message = if (questions.isEmpty()) "Chưa có câu hỏi. Thêm câu hỏi đầu tiên." else null
        } catch (exception: Exception) {
            message = "Không thể tải câu hỏi lúc này."
        } finally {
            isLoading = false
        }
    }

    AppBackground {
        ExamTopBar("Câu hỏi của đề thi", onBack)
        SectionTitle(exam?.title ?: "Câu hỏi", exam?.let { "Mã đề ${it.code}" })
        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(12.dp))
        }
        if (isLoading) {
            LoadingStateCard("Đang tải câu hỏi...")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
            itemsIndexed(questions) { index, question ->
                val displayOrder = question.orderIndex?.takeIf { it > 0 } ?: (index + 1)
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ChipText("Câu $displayOrder", AppBlue)
                            Spacer(Modifier.width(8.dp))
                            ChipText(question.difficulty ?: "--", when (question.difficulty) {
                                Difficulty.EASY.name -> AppMint
                                Difficulty.HARD.name -> AppRed
                                else -> AppAmber
                            })
                            Spacer(Modifier.width(8.dp))
                            ChipText(question.type ?: "--", AppIndigo)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(question.content, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        question.answers.orEmpty().forEachIndexed { index, answer ->
                            val label = ('A' + index).toString()
                            val marker = if (answer.correct == true) "Đúng: " else ""
                            Text("$label. $marker${answer.content}", color = if (answer.correct == true) AppMint else AppMuted, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { onEditQuestion(question) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Sửa câu hỏi")
                        }
                    }
                }
            }
        }
        }
        PrimaryAction("Thêm câu hỏi", onClick = onAddQuestion)
    }
}

@Composable
fun TeacherDashboardScreen(
    openQuestions: () -> Unit,
    openCreateExam: () -> Unit,
    openGenerate: () -> Unit,
    openMonitor: () -> Unit,
    openReports: () -> Unit
) {
    var examCount by remember { mutableStateOf<Int?>(null) }
    var questionCount by remember { mutableStateOf<Int?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            return@LaunchedEffect
        }
        try {
            examCount = ApiClient.getExams(authorization).data.orEmpty().size
            questionCount = ApiClient.getQuestions(authorization).data.orEmpty().size
        } catch (exception: Exception) {
            message = "Không thể tải dữ liệu lúc này."
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(BgGradient)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        Spacer(Modifier.height(18.dp))
        GradientHero("Bảng điều khiển", "Quản lý đề thi, câu hỏi và giám sát") {
            StatusPill(NetworkState.ONLINE)
        }

        SectionTitle("Tổng quan")
        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(10.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Đề thi", examCount?.toString() ?: "--", "Đề thi hiện có", AppBlue, Icons.Default.Assessment, modifier = Modifier.height(108.dp))
                MetricCard("Cảnh báo", "--", "Không có cảnh báo", AppMuted, Icons.Default.Warning, modifier = Modifier.height(108.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Câu hỏi", questionCount?.toString() ?: "--", "Ngân hàng câu hỏi", AppViolet, Icons.Default.QuestionAnswer, modifier = Modifier.height(108.dp))
                MetricCard("Đồng bộ", "Trực tiếp", "Sẵn sàng", AppMint, Icons.Default.CloudDone, modifier = Modifier.height(108.dp))
            }
        }

        SectionTitle("Thao tác nhanh")
        val actions = listOf(
            Triple("Câu hỏi", Icons.Default.QuestionAnswer, openQuestions),
            Triple("Tạo đề thi", Icons.Default.Add, openCreateExam),
            Triple("Tự động tạo", Icons.Default.AutoAwesome, openGenerate),
            Triple("Giám sát", Icons.Default.Visibility, openMonitor),
            Triple("Báo cáo", Icons.Default.Assessment, openReports),
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            actions.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { (label, icon, action) ->
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = AppSurface),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                                .clickable { action() }
                        ) {
                            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    Modifier
                                        .size(44.dp)
                                        .background(AppIndigo.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, null, tint = AppIndigo, modifier = Modifier.size(22.dp))
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = AppText)
                            }
                        }
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun calculatePointsEach(totalQuestions: Int): String {
    if (totalQuestions <= 0) {
        return "0.00"
    }
    return "%.2f".format(java.util.Locale.US, 10.0 / totalQuestions.toDouble())
}

private fun questionTypeLabel(type: QuestionType): String = when (type) {
    QuestionType.SINGLE -> "Một đáp án"
    QuestionType.MULTI -> "Nhiều đáp án"
    QuestionType.TRUE_FALSE -> "Đúng / Sai"
    QuestionType.FILL_BLANK -> "Điền khuyết"
}

private fun normalizedQuestionSignature(
    subjectId: Long?,
    topicId: Long?,
    content: String,
    type: String?,
    difficulty: String?
): String {
    return listOf(
        subjectId?.toString().orEmpty(),
        topicId?.toString().orEmpty(),
        content.trim().lowercase(),
        type.orEmpty().trim().uppercase(),
        difficulty.orEmpty().trim().uppercase()
    ).joinToString("|")
}

@Composable
private fun ConfirmActionDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String = "Hủy",
    destructive: Boolean = false,
    processing: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!processing) {
                onDismiss()
            }
        },
        title = { Text(title) },
        text = { Text(message, color = AppText, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !processing) {
                Text(confirmLabel, color = if (destructive) AppRed else AppIndigo)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !processing) {
                Text(dismissLabel)
            }
        }
    )
}

@Composable
fun QuestionBankScreen(
    onCreate: () -> Unit,
    onEdit: (QuestionResponse) -> Unit,
    onBack: () -> Unit
) {
    var questions by remember { mutableStateOf<List<QuestionResponse>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var debouncedSearchQuery by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var importLoading by remember { mutableStateOf(false) }
    var templateLoading by remember { mutableStateOf(false) }
    var pendingDeleteQuestion by remember { mutableStateOf<QuestionResponse?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun loadQuestions() {
        scope.launch {
            val authorization = SessionManager.authorizationHeader()
            if (authorization == null) {
                message = "Vui lòng đăng nhập lại."
                return@launch
            }
            isLoading = true
            try {
                val response = ApiClient.getQuestions(authorization)
                questions = response.data.orEmpty()
                message = if (questions.isEmpty()) "Chưa có câu hỏi nào." else null
            } catch (exception: Exception) {
                message = "Không thể tải câu hỏi lúc này."
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteQuestion(question: QuestionResponse) {
        scope.launch {
            val authorization = SessionManager.authorizationHeader()
            if (authorization == null) {
                message = "Vui lòng đăng nhập lại."
                return@launch
            }
            try {
                val response = ApiClient.deleteQuestion(authorization, question.id)
                if (response.success) {
                    questions = questions.filterNot { it.id == question.id }
                    message = "Đã xóa câu hỏi."
                    if (questions.isEmpty()) {
                        message = "Đã xóa câu hỏi. Không còn câu hỏi nào."
                    }
                } else {
                    message = response.message.ifBlank { "Không thể xóa câu hỏi lúc này." }
                }
            } catch (exception: HttpException) {
                message = "Không thể xóa câu hỏi. Vui lòng kiểm tra quyền."
            } catch (exception: Exception) {
                message = "Không thể xóa câu hỏi lúc này."
            }
        }
    }

    fun importQuestionsFromExcel(uri: android.net.Uri) {
        scope.launch {
            val authorization = SessionManager.authorizationHeader()
            if (authorization == null) {
                message = "Vui lòng đăng nhập lại."
                return@launch
            }

            importLoading = true
            message = null
            try {
                val fileBytes = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                } ?: run {
                    message = "Không thể mở file Excel đã chọn."
                    return@launch
                }

                val response = ApiClient.importQuestions(authorization, "questions.xlsx", fileBytes)
                val result = response.data
                if (response.success && result != null && result.success) {
                    val skippedCount = result.errors.orEmpty().size
                    message = if (skippedCount > 0) {
                        "Đã nhập ${result.importedQuestions} câu hỏi. Bỏ qua $skippedCount hàng trùng lặp hoặc không hợp lệ."
                    } else {
                        "Đã nhập ${result.importedQuestions} câu hỏi từ Excel."
                    }
                    loadQuestions()
                } else if (result != null) {
                    val sampleFailures = result.errors.orEmpty().take(3).joinToString("; ") {
                        "hàng ${it.rowNumber}: ${it.message}"
                    }
                    val moreFailures = if ((result.errors?.size ?: 0) > 3) " +${(result.errors?.size ?: 0) - 3} nữa" else ""
                    message = if (sampleFailures.isBlank()) {
                        "Nhập thất bại. Kiểm tra mẫu Excel."
                    } else {
                        "Nhập thất bại: $sampleFailures$moreFailures"
                    }
                } else {
                    message = response.message.ifBlank { "Nhập thất bại." }
                }
            } catch (exception: Exception) {
                message = "Không thể nhập file Excel đã chọn."
            } finally {
                importLoading = false
            }
        }
    }

    fun saveQuestionTemplate(uri: android.net.Uri) {
        scope.launch {
            templateLoading = true
            message = null
            try {
                val subjectNames = mutableListOf<String>()
                val topicNames = mutableListOf<String>()
                val authorization = SessionManager.authorizationHeader()
                if (authorization != null) {
                    runCatching {
                        val subjects = ApiClient.getSubjects(authorization).data.orEmpty()
                        subjectNames += subjects.map { it.name }
                        subjects.forEach { subject ->
                            val topicsForSubject = runCatching {
                                ApiClient.getTopics(authorization, subject.id).data.orEmpty()
                            }.getOrDefault(emptyList())
                            topicNames += topicsForSubject.map { it.name }
                        }
                    }
                }

                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        output.write(
                            QuestionExcelTemplate.createBytes(
                                subjectNames = subjectNames,
                                topicNames = topicNames
                            )
                        )
                    } ?: error("Cannot open output stream")
                }
                message = "Đã lưu mẫu nhập Excel."
            } catch (exception: Exception) {
                message = "Không thể lưu mẫu nhập Excel."
            } finally {
                templateLoading = false
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) importQuestionsFromExcel(uri)
    }

    val templateLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri ->
        if (uri != null) saveQuestionTemplate(uri)
    }

    LaunchedEffect(Unit) {
        loadQuestions()
    }

    LaunchedEffect(searchQuery) {
        delay(250)
        debouncedSearchQuery = searchQuery
    }

    val filteredQuestions = remember(questions, debouncedSearchQuery) {
        val query = debouncedSearchQuery.trim()
        if (query.isBlank()) {
            questions
        } else {
            questions.filter { question ->
                question.content.contains(query, ignoreCase = true) ||
                    question.id.toString().contains(query) ||
                    question.subjectName.orEmpty().contains(query, ignoreCase = true) ||
                    question.topicName.orEmpty().contains(query, ignoreCase = true) ||
                    question.type.orEmpty().contains(query, ignoreCase = true) ||
                    question.difficulty.orEmpty().contains(query, ignoreCase = true) ||
                    question.answers.orEmpty().any { it.content.contains(query, ignoreCase = true) }
            }
        }
    }

    AppBackground {
        pendingDeleteQuestion?.let { question ->
            ConfirmActionDialog(
                title = "Xóa câu hỏi",
                message = "Xóa câu hỏi ID ${question.id}? Hành động này không thể hoàn tác.",
                confirmLabel = if (isLoading) "Đang xóa..." else "Xóa",
                destructive = true,
                processing = isLoading,
                onConfirm = {
                    pendingDeleteQuestion = null
                    deleteQuestion(question)
                },
                onDismiss = { pendingDeleteQuestion = null }
            )
        }

        ExamTopBar("Ngân hàng câu hỏi", onBack)

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tìm câu hỏi...") },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = AppMuted) },
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = AppSurface,
                focusedContainerColor = AppSurface,
                unfocusedBorderColor = AppCardBorder
            )
        )

        if (message != null) {
            val currentMessage = message.orEmpty()
            val bannerColor = when {
                currentMessage.startsWith("Đã nhập") -> AppMint
                currentMessage.startsWith("Import completed") -> AppAmber
                currentMessage.startsWith("Chưa có câu hỏi") -> AppAmber
                else -> AppRed
            }
            val bannerIcon = if (bannerColor == AppRed) Icons.Default.ErrorOutline else Icons.Default.Info
            InfoBanner(currentMessage, bannerColor, bannerIcon)
            Spacer(Modifier.height(12.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            if (isLoading) {
                item {
            LoadingStateCard("Đang tải câu hỏi...")
                }
            }
            if (filteredQuestions.isEmpty() && debouncedSearchQuery.isNotBlank()) {
                item {
                    InfoBanner("Không có câu hỏi phù hợp.", AppAmber, Icons.Default.Info)
                }
            }
            items(filteredQuestions) { question ->
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(question.content, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            ChipText("ID ${question.id}", AppIndigo)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            question.subjectName?.takeIf { it.isNotBlank() }?.let { ChipText(it, AppBlue) }
                            question.topicName?.takeIf { it.isNotBlank() }?.let { ChipText(it, AppLilac) }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            question.type?.let { ChipText(it, AppAmber) }
                            question.difficulty?.let {
                                ChipText(
                                    it,
                                    when (it) {
                                        Difficulty.EASY.name -> AppMint
                                        Difficulty.HARD.name -> AppRed
                                        else -> AppAmber
                                    }
                                )
                            }
                        }
                        val answers = question.answers.orEmpty()
                        if (answers.isNotEmpty()) {
                            Spacer(Modifier.height(10.dp))
                            answers.forEachIndexed { index, answer ->
                                val label = ('A' + index).toString()
                                val prefix = if (answer.correct == true) "Đúng: " else ""
                                Text(
                                    "$label. $prefix${answer.content}",
                                    color = if (answer.correct == true) AppMint else AppMuted,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { onEdit(question) },
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Sửa")
                            }
                            OutlinedButton(
                                onClick = { pendingDeleteQuestion = question },
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text("Xóa")
                            }
                        }
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onCreate,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Thêm câu hỏi")
            }
            OutlinedButton(
                onClick = { importLauncher.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) },
                enabled = !importLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (importLoading) "Đang nhập..." else "Nhập Excel")
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = { templateLauncher.launch("question-import-template.xlsx") },
            enabled = !templateLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(if (templateLoading) "Đang lưu..." else "Tải mẫu Excel")
        }
    }
}

@Composable
fun CreateQuestionScreen(onBack: () -> Unit) {
    val selectedExam = ExamAttemptStore.selectedExam.value
    val isBankMode = selectedExam == null
    var type by remember { mutableStateOf(QuestionType.SINGLE) }
    var difficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var content by remember { mutableStateOf("") }
    var subjects by remember { mutableStateOf<List<SubjectResponse>>(emptyList()) }
    var topics by remember { mutableStateOf<List<TopicResponse>>(emptyList()) }
    var selectedSubject by remember { mutableStateOf<SubjectResponse?>(null) }
    var selectedTopic by remember { mutableStateOf<TopicResponse?>(null) }
    var answerA by remember { mutableStateOf("") }
    var answerB by remember { mutableStateOf("") }
    var answerC by remember { mutableStateOf("") }
    var answerD by remember { mutableStateOf("") }
    var correctAnswers by remember { mutableStateOf(setOf("A")) }
    var explanation by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var catalogLoading by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var uploadingImage by remember { mutableStateOf(false) }
    var imagePreviewUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var addSubjectDialog by remember { mutableStateOf(false) }
    var addTopicDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }
    var newTopicName by remember { mutableStateOf("") }
    var addSubjectLoading by remember { mutableStateOf(false) }
    var addTopicLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imagePreviewUri = uri
            scope.launch {
                uploadingImage = true
                try {
                    val bytes = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    } ?: return@launch
                    val auth = SessionManager.authorizationHeader() ?: return@launch
                    val response = ApiClient.uploadFile(auth, "question_image", bytes, "image/jpeg")
                    if (response.success) {
                        imageUrl = response.data?.url
                    } else {
                        message = "Tải ảnh lên thất bại."
                    }
                } catch (e: Exception) {
                    message = "Tải ảnh lên thất bại."
                } finally {
                    uploadingImage = false
                }
            }
        }
    }
    val gson = remember { Gson() }

    LaunchedEffect(type) {
        when (type) {
            QuestionType.TRUE_FALSE -> {
                answerA = "True"
                answerB = "False"
                answerC = ""
                answerD = ""
                correctAnswers = correctAnswers.firstOrNull { it == "A" || it == "B" }?.let { setOf(it) } ?: setOf("A")
            }
            QuestionType.FILL_BLANK -> {
                answerB = ""
                answerC = ""
                answerD = ""
                correctAnswers = setOf("A")
            }
            QuestionType.SINGLE -> {
                correctAnswers = setOf(correctAnswers.firstOrNull() ?: "A")
            }
            QuestionType.MULTI -> {
                if (correctAnswers.isEmpty()) correctAnswers = setOf("A")
            }
        }
    }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            return@LaunchedEffect
        }

        catalogLoading = true
        try {
            val response = ApiClient.getSubjects(authorization)
            if (response.success) {
                subjects = response.data.orEmpty()
                selectedSubject = response.data.orEmpty().firstOrNull()
            } else {
                message = response.message
            }
        } catch (exception: Exception) {
            message = "Không thể tải môn học lúc này."
        } finally {
            catalogLoading = false
        }
    }

    LaunchedEffect(selectedSubject?.id) {
        val authorization = SessionManager.authorizationHeader() ?: return@LaunchedEffect
        val subject = selectedSubject ?: run {
            topics = emptyList()
            selectedTopic = null
            return@LaunchedEffect
        }

        try {
            val response = ApiClient.getTopics(authorization, subject.id)
            if (response.success) {
                topics = response.data.orEmpty()
                selectedTopic = selectedTopic?.takeIf { current ->
                    response.data.orEmpty().any { it.id == current.id }
                }
            } else {
                topics = emptyList()
                selectedTopic = null
                message = response.message
            }
        } catch (exception: Exception) {
            topics = emptyList()
            selectedTopic = null
            message = "Không thể tải chủ đề lúc này."
        }
    }

    fun saveQuestion() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Vui lòng đăng nhập lại."; return }
        if (content.isBlank()) { message = "Vui lòng nhập nội dung câu hỏi."; return }
        val subject = selectedSubject
        if (subject == null) { message = "Vui lòng chọn môn học."; return }
        val answerEntries = listOf(
            "A" to answerA.trim(),
            "B" to answerB.trim(),
            "C" to answerC.trim(),
            "D" to answerD.trim()
        ).filter { it.second.isNotBlank() }
        val minAnswers = if (type == QuestionType.FILL_BLANK) 1 else 2
        if (answerEntries.size < minAnswers) {
            message = if (type == QuestionType.FILL_BLANK) "Nhập đáp án đúng." else "Cần ít nhất hai đáp án."
            return
        }
        val validCorrectLabels = answerEntries.map { it.first }.toSet()
        val selectedCorrect = correctAnswers.intersect(validCorrectLabels)
        if (selectedCorrect.isEmpty()) { message = "Chọn đáp án đúng."; return }
        if (type != QuestionType.MULTI && selectedCorrect.size != 1) { message = "Chọn một đáp án đúng."; return }
        scope.launch {
            loading = true; message = null
            try {
                if (isBankMode) {
                    val response = ApiClient.createQuestion(
                        authorization,
                        QuestionCreateRequest(
                            subjectId = subject.id,
                            topicId = selectedTopic?.id,
                            content = content.trim(),
                            type = type.name,
                            difficulty = difficulty.name,
                            imageUrl = imageUrl,
                            answers = answerEntries.map { (label, answer) ->
                                AnswerCreateRequest(
                                    content = answer.trim(),
                                    correct = label in selectedCorrect,
                                    explanation = if (label in selectedCorrect) explanation.ifBlank { null } else null
                                )
                            }
                        )
                    )
                    if (response.success) {
                        type = QuestionType.SINGLE
                        difficulty = Difficulty.MEDIUM
                        content = ""
                        answerA = ""
                        answerB = ""
                        answerC = ""
                        answerD = ""
                        correctAnswers = setOf("A")
                        explanation = ""
                        message = "Đã tạo câu hỏi trong ngân hàng."
                    } else {
                        message = response.message
                    }
                } else {
                    val exam = selectedExam ?: run {
                        message = "Vui lòng chọn đề thi trước khi tạo câu hỏi."
                        return@launch
                    }
                    val response = ApiClient.createQuestionForExam(
                        authorization,
                        exam.id,
                        ExamQuestionCreateRequest(
                            subjectId = subject.id,
                            topicId = selectedTopic?.id,
                            content = content.trim(),
                            type = type.name,
                            difficulty = difficulty.name,
                            imageUrl = imageUrl,
                            orderIndex = null,
                            score = null,
                            answers = answerEntries.map { (label, answer) ->
                                AnswerCreateRequest(
                                    content = answer.trim(),
                                    correct = label in selectedCorrect,
                                    explanation = if (label in selectedCorrect) explanation.ifBlank { null } else null
                                )
                            }
                        )
                    )
                    if (response.success) {
                        type = QuestionType.SINGLE
                        difficulty = Difficulty.MEDIUM
                        content = ""
                        answerA = ""
                        answerB = ""
                        answerC = ""
                        answerD = ""
                        correctAnswers = setOf("A")
                        explanation = ""
                        message = "Đã thêm câu hỏi vào ${exam.title}"
                    } else {
                        message = response.message
                    }
                }
            } catch (exception: HttpException) { message = "Không thể lưu câu hỏi. Vui lòng kiểm tra quyền." }
            catch (exception: Exception) { message = "Không thể lưu câu hỏi lúc này." }
            finally { loading = false }
        }
    }

    AppBackground {
        ExamTopBar("Tạo câu hỏi", onBack)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                InfoBanner(
                    if (isBankMode) {
                        "Chế độ ngân hàng. Câu hỏi này có thể dùng lại cho nhiều đề thi."
                    } else {
                        "Đang thêm câu hỏi vào đề thi: ${selectedExam?.title}"
                    },
                    if (isBankMode) AppBlue else AppMint,
                    Icons.Default.Info
                )

                SectionTitle("Nội dung câu hỏi")
                OutlinedTextField(content, { content = it }, modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), label = { Text("Nhập nội dung câu hỏi") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        enabled = !loading && !uploadingImage,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (uploadingImage) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(6.dp))
                        }
                        Icon(Icons.Default.Image, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (imageUrl != null) "Đổi hình ảnh" else "Thêm hình ảnh")
                    }
                    if (imageUrl != null) {
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = { imageUrl = null; imagePreviewUri = null }) {
                            Text("Xóa", color = AppRed)
                        }
                    }
                }
                if (imagePreviewUri != null && imageUrl != null) {
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = imagePreviewUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Fit
                    )
                }

                SectionTitle("Phân loại")
                CatalogDropdown(
                    label = "Môn học",
                    value = selectedSubject?.name ?: if (catalogLoading) "Đang tải môn học..." else "Chọn môn học",
                    enabled = !loading && !catalogLoading && subjects.isNotEmpty(),
                    items = subjects,
                    itemText = { it.name },
                    onSelect = {
                        selectedSubject = it
                        selectedTopic = null
                    },
                    addLabel = "+ Thêm môn học",
                    onAdd = { addSubjectDialog = true }
                )
                Spacer(Modifier.height(8.dp))
                CatalogDropdown(
                    label = "Chủ đề (không bắt buộc)",
                    value = selectedTopic?.name ?: if (topics.isEmpty()) "Không có chủ đề" else "Không có chủ đề",
                    enabled = !loading && topics.isNotEmpty(),
                    items = topics,
                    itemText = { it.name },
                    onSelect = { selectedTopic = it },
                    leadingClearItem = "Không có chủ đề",
                    onClear = { selectedTopic = null },
                    addLabel = if (selectedSubject != null) "+ Thêm chủ đề" else null,
                    onAdd = if (selectedSubject != null) {{ addTopicDialog = true }} else null
                )

                Spacer(Modifier.height(8.dp))
                Text("Độ khó", color = AppMuted, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.entries.forEach { d ->
                        val c = when (d) { Difficulty.EASY -> AppMint; Difficulty.MEDIUM -> AppAmber; Difficulty.HARD -> AppRed }
                        FilterChip(
                            selected = difficulty == d,
                            onClick = { difficulty = d },
                            label = { Text(d.name) },
                            shape = MaterialTheme.shapes.small,
                            colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                                selectedContainerColor = c.copy(alpha = 0.18f),
                                selectedLabelColor = c,
                                containerColor = AppSurface,
                                labelColor = AppText
                            ),
                            border = androidx.compose.material3.FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = difficulty == d,
                                borderColor = AppCardBorder,
                                selectedBorderColor = c
                            )
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Loại câu hỏi", color = AppMuted, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuestionType.entries.chunked(2).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            rowItems.forEach { item ->
                                FilterChip(
                                    selected = type == item,
                                    onClick = { type = item },
                                    label = { Text(questionTypeLabel(item)) },
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(2 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }

                SectionTitle("Đáp án")
                if (type == QuestionType.FILL_BLANK) {
                    OutlinedTextField(
                        answerA,
                        {
                            answerA = it
                            correctAnswers = setOf("A")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Đáp án đúng") },
                        enabled = !loading,
                        shape = MaterialTheme.shapes.medium
                    )
                } else {
                    Text(
                        if (type == QuestionType.MULTI) "Chọn tất cả đáp án đúng" else "Chọn đáp án đúng",
                        color = AppMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    AnswerOptionInput(
                        label = "A",
                        value = answerA,
                        onValueChange = { answerA = it },
                        selected = "A" in correctAnswers,
                        multiple = type == QuestionType.MULTI,
                        enabled = !loading && type != QuestionType.TRUE_FALSE,
                        onSelect = {
                            correctAnswers = if (type == QuestionType.MULTI) {
                                if ("A" in correctAnswers) correctAnswers - "A" else correctAnswers + "A"
                            } else setOf("A")
                        }
                    )
                    Spacer(Modifier.height(6.dp))
                    AnswerOptionInput(
                        label = "B",
                        value = answerB,
                        onValueChange = { answerB = it },
                        selected = "B" in correctAnswers,
                        multiple = type == QuestionType.MULTI,
                        enabled = !loading && type != QuestionType.TRUE_FALSE,
                        onSelect = {
                            correctAnswers = if (type == QuestionType.MULTI) {
                                if ("B" in correctAnswers) correctAnswers - "B" else correctAnswers + "B"
                            } else setOf("B")
                        }
                    )
                    if (type != QuestionType.TRUE_FALSE) {
                        Spacer(Modifier.height(6.dp))
                        AnswerOptionInput(
                            label = "C",
                            value = answerC,
                            onValueChange = { answerC = it },
                            selected = "C" in correctAnswers,
                            multiple = type == QuestionType.MULTI,
                            enabled = !loading,
                            onSelect = {
                                correctAnswers = if (type == QuestionType.MULTI) {
                                    if ("C" in correctAnswers) correctAnswers - "C" else correctAnswers + "C"
                                } else setOf("C")
                            }
                        )
                        Spacer(Modifier.height(6.dp))
                        AnswerOptionInput(
                            label = "D",
                            value = answerD,
                            onValueChange = { answerD = it },
                            selected = "D" in correctAnswers,
                            multiple = type == QuestionType.MULTI,
                            enabled = !loading,
                            onSelect = {
                                correctAnswers = if (type == QuestionType.MULTI) {
                                    if ("D" in correctAnswers) correctAnswers - "D" else correctAnswers + "D"
                                } else setOf("D")
                            }
                        )
                    }
                }

                SectionTitle("Giải thích")
                OutlinedTextField(explanation, { explanation = it }, modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp), label = { Text("Giải thích tại sao đáp án này đúng") }, enabled = !loading, shape = MaterialTheme.shapes.medium)

                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    InfoBanner(message.orEmpty(), if (message.orEmpty().startsWith("Đã thêm câu hỏi") || message.orEmpty().startsWith("Đã tạo câu hỏi")) AppMint else AppRed, if (message.orEmpty().startsWith("Đã thêm câu hỏi") || message.orEmpty().startsWith("Đã tạo câu hỏi")) Icons.Default.CheckCircle else Icons.Default.ErrorOutline)
                }
                Spacer(Modifier.height(14.dp))
                PrimaryAction(if (loading) "Đang lưu..." else "Lưu câu hỏi") { if (!loading) saveQuestion() }
            }
        }

        if (addSubjectDialog) {
            QuickAddDialog(
                title = "Thêm môn học",
                name = newSubjectName,
                onNameChange = { newSubjectName = it },
                onDismiss = { addSubjectDialog = false; newSubjectName = "" },
                onConfirm = {
                    val auth = SessionManager.authorizationHeader() ?: return@QuickAddDialog
                    addSubjectLoading = true
                    scope.launch {
                        try {
                            val response = ApiClient.createSubject(auth, SubjectCreateRequest(name = newSubjectName.trim()))
                            if (response.success) {
                                response.data?.let { subjects = subjects + it; selectedSubject = it; selectedTopic = null }
                                message = "Đã thêm môn học."
                            } else {
                                message = response.message
                            }
                        } catch (e: Exception) {
                            message = "Không thể thêm môn học."
                        } finally {
                            addSubjectLoading = false
                            addSubjectDialog = false
                            newSubjectName = ""
                        }
                    }
                },
                loading = addSubjectLoading
            )
        }

        if (addTopicDialog) {
            val subject = selectedSubject
            if (subject == null) {
                addTopicDialog = false
            } else {
                QuickAddDialog(
                    title = "Thêm chủ đề cho ${subject.name}",
                    name = newTopicName,
                    onNameChange = { newTopicName = it },
                    onDismiss = { addTopicDialog = false; newTopicName = "" },
                    onConfirm = {
                        val auth = SessionManager.authorizationHeader() ?: return@QuickAddDialog
                        addTopicLoading = true
                        scope.launch {
                            try {
                                val response = ApiClient.createTopic(auth, subject.id, TopicCreateRequest(name = newTopicName.trim()))
                                if (response.success) {
                                    response.data?.let { topics = topics + it; selectedTopic = it }
                                    message = "Đã thêm chủ đề."
                                } else {
                                    message = response.message
                                }
                            } catch (e: Exception) {
                                message = "Không thể thêm chủ đề."
                            } finally {
                                addTopicLoading = false
                                addTopicDialog = false
                                newTopicName = ""
                            }
                        }
                    },
                    loading = addTopicLoading
                )
            }
        }
    }
}

@Composable
private fun AnswerOptionInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    selected: Boolean,
    multiple: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (multiple) {
            Checkbox(
                checked = selected,
                onCheckedChange = { onSelect() },
                colors = CheckboxDefaults.colors(checkedColor = AppIndigo)
            )
        } else {
            RadioButton(
                selected = selected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = AppIndigo)
            )
        }
        Spacer(Modifier.width(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            label = { Text("Đáp án $label") },
            enabled = enabled,
            shape = MaterialTheme.shapes.medium
        )
    }
}

@Composable
fun EditQuestionScreen(onBack: () -> Unit) {
    val exam = ExamAttemptStore.selectedExam.value
    val examQuestion = ExamAttemptStore.selectedQuestion.value
    val bankQuestion = ExamAttemptStore.selectedBankQuestion.value
    val isBankMode = bankQuestion != null && examQuestion == null
    val stateKey = examQuestion?.questionId ?: bankQuestion?.id
    val initialType = examQuestion?.type ?: bankQuestion?.type
    val initialDifficulty = examQuestion?.difficulty ?: bankQuestion?.difficulty
    val initialContent = examQuestion?.content ?: bankQuestion?.content.orEmpty()
    val initialSubjectId = examQuestion?.subjectId ?: bankQuestion?.subjectId
    val initialTopicId = examQuestion?.topicId ?: bankQuestion?.topicId
    val existingAnswers = examQuestion?.answers ?: bankQuestion?.answers.orEmpty()
    var type by remember(stateKey) { mutableStateOf(initialType?.let { runCatching { QuestionType.valueOf(it) }.getOrNull() } ?: QuestionType.SINGLE) }
    var difficulty by remember(stateKey) { mutableStateOf(initialDifficulty?.let { runCatching { Difficulty.valueOf(it) }.getOrNull() } ?: Difficulty.MEDIUM) }
    var content by remember(stateKey) { mutableStateOf(initialContent) }
    var subjects by remember { mutableStateOf<List<SubjectResponse>>(emptyList()) }
    var topics by remember { mutableStateOf<List<TopicResponse>>(emptyList()) }
    var selectedSubject by remember { mutableStateOf<SubjectResponse?>(null) }
    var selectedTopic by remember { mutableStateOf<TopicResponse?>(null) }
    var answerA by remember(stateKey) { mutableStateOf(existingAnswers.getOrNull(0)?.content.orEmpty()) }
    var answerB by remember(stateKey) { mutableStateOf(existingAnswers.getOrNull(1)?.content.orEmpty()) }
    var answerC by remember(stateKey) { mutableStateOf(existingAnswers.getOrNull(2)?.content.orEmpty()) }
    var answerD by remember(stateKey) { mutableStateOf(existingAnswers.getOrNull(3)?.content.orEmpty()) }
    var correctAnswers by remember(stateKey) {
        mutableStateOf(existingAnswers.mapIndexedNotNull { index, answer -> if (answer.correct == true) ('A' + index).toString() else null }.toSet().ifEmpty { setOf("A") })
    }
    var explanation by remember(stateKey) { mutableStateOf(existingAnswers.firstOrNull { it.correct == true }?.explanation.orEmpty()) }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var catalogLoading by remember { mutableStateOf(false) }
    var showUpdateConfirm by remember { mutableStateOf(false) }
    val initialImageUrl = examQuestion?.imageUrl ?: bankQuestion?.imageUrl
    var imageUrl by remember(stateKey) { mutableStateOf(initialImageUrl) }
    var uploadingImage by remember { mutableStateOf(false) }
    var imagePreviewUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var addSubjectDialog by remember { mutableStateOf(false) }
    var addTopicDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }
    var newTopicName by remember { mutableStateOf("") }
    var addSubjectLoading by remember { mutableStateOf(false) }
    var addTopicLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imagePreviewUri = uri
            scope.launch {
                uploadingImage = true
                try {
                    val bytes = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    } ?: return@launch
                    val auth = SessionManager.authorizationHeader() ?: return@launch
                    val response = ApiClient.uploadFile(auth, "question_image", bytes, "image/jpeg")
                    if (response.success) {
                        imageUrl = response.data?.url
                    } else {
                        message = "Tải ảnh lên thất bại."
                    }
                } catch (e: Exception) {
                    message = "Tải ảnh lên thất bại."
                } finally {
                    uploadingImage = false
                }
            }
        }
    }

    LaunchedEffect(type) {
        when (type) {
            QuestionType.TRUE_FALSE -> {
                answerA = "True"
                answerB = "False"
                answerC = ""
                answerD = ""
                correctAnswers = correctAnswers.firstOrNull { it == "A" || it == "B" }?.let { setOf(it) } ?: setOf("A")
            }
            QuestionType.FILL_BLANK -> {
                answerB = ""
                answerC = ""
                answerD = ""
                correctAnswers = setOf("A")
            }
            QuestionType.SINGLE -> {
                correctAnswers = setOf(correctAnswers.firstOrNull() ?: "A")
            }
            QuestionType.MULTI -> {
                if (correctAnswers.isEmpty()) correctAnswers = setOf("A")
            }
        }
    }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            return@LaunchedEffect
        }
        catalogLoading = true
        try {
            val response = ApiClient.getSubjects(authorization)
            subjects = response.data.orEmpty()
            selectedSubject = subjects.firstOrNull { it.id == initialSubjectId } ?: subjects.firstOrNull()
        } catch (exception: Exception) {
            message = "Không thể tải môn học lúc này."
        } finally {
            catalogLoading = false
        }
    }

    LaunchedEffect(selectedSubject?.id) {
        val authorization = SessionManager.authorizationHeader() ?: return@LaunchedEffect
        val subject = selectedSubject ?: return@LaunchedEffect
        try {
            val response = ApiClient.getTopics(authorization, subject.id)
            topics = response.data.orEmpty()
            selectedTopic = selectedTopic?.takeIf { current ->
                topics.any { it.id == current.id }
            } ?: topics.firstOrNull { it.id == initialTopicId }
        } catch (exception: Exception) {
            topics = emptyList()
            selectedTopic = null
            message = "Không thể tải chủ đề lúc này."
        }
    }

    fun buildAnswerEntries(): List<Pair<String, String>> {
        return listOf("A" to answerA.trim(), "B" to answerB.trim(), "C" to answerC.trim(), "D" to answerD.trim())
            .filter { it.second.isNotBlank() }
    }

    fun updateQuestion() {
        val subject = selectedSubject
        val authorization = SessionManager.authorizationHeader()
        if (examQuestion == null && bankQuestion == null) { message = "Vui lòng chọn câu hỏi trước khi sửa."; return }
        if (authorization == null) { message = "Vui lòng đăng nhập lại."; return }
        if (subject == null) { message = "Vui lòng chọn môn học."; return }
        if (content.isBlank()) { message = "Vui lòng nhập nội dung câu hỏi."; return }
        val answerEntries = buildAnswerEntries()
        val minAnswers = if (type == QuestionType.FILL_BLANK) 1 else 2
        if (answerEntries.size < minAnswers) {
            message = if (type == QuestionType.FILL_BLANK) "Nhập đáp án đúng." else "Cần ít nhất hai đáp án."
            return
        }
        val validCorrectLabels = answerEntries.map { it.first }.toSet()
        val selectedCorrect = correctAnswers.intersect(validCorrectLabels)
        if (selectedCorrect.isEmpty()) { message = "Chọn đáp án đúng."; return }
        if (type != QuestionType.MULTI && selectedCorrect.size != 1) { message = "Chọn một đáp án đúng."; return }

        scope.launch {
            loading = true; message = null
            try {
                if (isBankMode) {
                    val selectedQuestion = bankQuestion ?: run {
                        message = "Vui lòng chọn câu hỏi trước khi sửa."
                        return@launch
                    }
                    val response = ApiClient.updateQuestion(
                        authorization,
                        selectedQuestion.id,
                        QuestionCreateRequest(
                            subjectId = subject.id,
                            topicId = selectedTopic?.id,
                            content = content.trim(),
                            type = type.name,
                            difficulty = difficulty.name,
                            imageUrl = imageUrl,
                            answers = answerEntries.map { (label, answer) ->
                                AnswerCreateRequest(
                                    content = answer,
                                    correct = label in selectedCorrect,
                                    explanation = if (label in selectedCorrect) explanation.ifBlank { null } else null
                                )
                            }
                        )
                    )
                    if (response.success) {
                        response.data?.let { ExamAttemptStore.setSelectedBankQuestion(it) }
                        message = "Đã cập nhật câu hỏi."
                    } else {
                        message = response.message
                    }
                } else {
                    val selectedExam = exam ?: run {
                        message = "Vui lòng chọn đề thi trước khi sửa."
                        return@launch
                    }
                    val selectedQuestion = examQuestion ?: run {
                        message = "Vui lòng chọn câu hỏi trước khi sửa."
                        return@launch
                    }
                    val response = ApiClient.updateQuestionForExam(
                        authorization,
                        selectedExam.id,
                        selectedQuestion.questionId,
                        ExamQuestionCreateRequest(
                            subjectId = subject.id,
                            topicId = selectedTopic?.id,
                            content = content.trim(),
                            type = type.name,
                            difficulty = difficulty.name,
                            imageUrl = imageUrl,
                            orderIndex = selectedQuestion.orderIndex,
                            score = selectedQuestion.score,
                            answers = answerEntries.map { (label, answer) ->
                                AnswerCreateRequest(
                                    content = answer,
                                    correct = label in selectedCorrect,
                                    explanation = if (label in selectedCorrect) explanation.ifBlank { null } else null
                                )
                            }
                        )
                    )
                    if (response.success) {
                        response.data?.let { ExamAttemptStore.setSelectedQuestion(it) }
                        message = "Đã cập nhật câu hỏi."
                    } else {
                        message = response.message
                    }
                }
            } catch (exception: HttpException) { message = "Không thể cập nhật câu hỏi. Vui lòng kiểm tra quyền." }
            catch (exception: Exception) { message = "Không thể cập nhật câu hỏi lúc này." }
            finally { loading = false }
        }
    }

    AppBackground {
        if (showUpdateConfirm) {
            ConfirmActionDialog(
                title = "Cập nhật câu hỏi",
                message = "Lưu thay đổi cho câu hỏi này?",
                confirmLabel = if (loading) "Đang lưu..." else "Lưu",
                processing = loading,
                onConfirm = {
                    showUpdateConfirm = false
                    updateQuestion()
                },
                onDismiss = { showUpdateConfirm = false }
            )
        }

        ExamTopBar("Sửa câu hỏi", onBack)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                InfoBanner(
                    if (isBankMode) {
                        "Chế độ ngân hàng. Đang cập nhật câu hỏi dùng chung."
                    } else {
                        exam?.let { "Đề thi: ${it.title}" } ?: "Chưa chọn đề thi."
                    },
                    if (isBankMode) AppBlue else if (exam != null) AppMint else AppAmber,
                    Icons.Default.Info
                )

                SectionTitle("Nội dung câu hỏi")
                OutlinedTextField(content, { content = it }, modifier = Modifier.fillMaxWidth().height(120.dp), label = { Text("Nhập nội dung câu hỏi") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        enabled = !loading && !uploadingImage,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (uploadingImage) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(6.dp))
                        }
                        Icon(Icons.Default.Image, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (imageUrl != null) "Đổi hình ảnh" else "Thêm hình ảnh")
                    }
                    if (imageUrl != null) {
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = { imageUrl = null; imagePreviewUri = null }) {
                            Text("Xóa", color = AppRed)
                        }
                    }
                }
                if (imagePreviewUri != null && imageUrl != null) {
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = imagePreviewUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Fit
                    )
                } else if (imageUrl != null && imagePreviewUri == null) {
                    Spacer(Modifier.height(8.dp))
                    AsyncImage(
                        model = "${"http://10.0.2.2:8080"}$imageUrl",
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Fit
                    )
                }

                SectionTitle("Phân loại")
                CatalogDropdown(
                    label = "Môn học",
                    value = selectedSubject?.name ?: if (catalogLoading) "Đang tải môn học..." else "Chọn môn học",
                    enabled = !loading && !catalogLoading && subjects.isNotEmpty(),
                    items = subjects,
                    itemText = { it.name },
                    onSelect = {
                        selectedSubject = it
                        selectedTopic = null
                    },
                    addLabel = "+ Thêm môn học",
                    onAdd = { addSubjectDialog = true }
                )
                Spacer(Modifier.height(8.dp))
                CatalogDropdown(
                    label = "Chủ đề",
                    value = selectedTopic?.name ?: "Không có chủ đề",
                    enabled = !loading && topics.isNotEmpty(),
                    items = topics,
                    itemText = { it.name },
                    onSelect = { selectedTopic = it },
                    leadingClearItem = "Không có chủ đề",
                    onClear = { selectedTopic = null },
                    addLabel = if (selectedSubject != null) "+ Thêm chủ đề" else null,
                    onAdd = if (selectedSubject != null) {{ addTopicDialog = true }} else null
                )

                Spacer(Modifier.height(8.dp))
                Text("Độ khó", color = AppMuted, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.entries.forEach { d ->
                        FilterChip(selected = difficulty == d, onClick = { difficulty = d }, label = { Text(d.name) }, shape = MaterialTheme.shapes.small)
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Loại câu hỏi", color = AppMuted, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuestionType.entries.chunked(2).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            rowItems.forEach { qType ->
                                FilterChip(
                                    selected = type == qType,
                                    onClick = { type = qType },
                                    label = { Text(questionTypeLabel(qType)) },
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(2 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }

                SectionTitle("Đáp án")
                if (type == QuestionType.FILL_BLANK) {
                    OutlinedTextField(answerA, { answerA = it; correctAnswers = setOf("A") }, modifier = Modifier.fillMaxWidth(), label = { Text("Đáp án đúng") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                } else {
                    Text(if (type == QuestionType.MULTI) "Chọn tất cả đáp án đúng" else "Chọn đáp án đúng", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(6.dp))
                    AnswerOptionInput("A", answerA, { answerA = it }, "A" in correctAnswers, type == QuestionType.MULTI, !loading && type != QuestionType.TRUE_FALSE) { correctAnswers = if (type == QuestionType.MULTI) { if ("A" in correctAnswers) correctAnswers - "A" else correctAnswers + "A" } else setOf("A") }
                    Spacer(Modifier.height(6.dp))
                    AnswerOptionInput("B", answerB, { answerB = it }, "B" in correctAnswers, type == QuestionType.MULTI, !loading && type != QuestionType.TRUE_FALSE) { correctAnswers = if (type == QuestionType.MULTI) { if ("B" in correctAnswers) correctAnswers - "B" else correctAnswers + "B" } else setOf("B") }
                    if (type != QuestionType.TRUE_FALSE) {
                        Spacer(Modifier.height(6.dp))
                        AnswerOptionInput("C", answerC, { answerC = it }, "C" in correctAnswers, type == QuestionType.MULTI, !loading) { correctAnswers = if (type == QuestionType.MULTI) { if ("C" in correctAnswers) correctAnswers - "C" else correctAnswers + "C" } else setOf("C") }
                        Spacer(Modifier.height(6.dp))
                        AnswerOptionInput("D", answerD, { answerD = it }, "D" in correctAnswers, type == QuestionType.MULTI, !loading) { correctAnswers = if (type == QuestionType.MULTI) { if ("D" in correctAnswers) correctAnswers - "D" else correctAnswers + "D" } else setOf("D") }
                    }
                }

                SectionTitle("Giải thích")
                OutlinedTextField(explanation, { explanation = it }, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Giải thích tại sao đáp án này đúng") }, enabled = !loading, shape = MaterialTheme.shapes.medium)

                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    InfoBanner(message.orEmpty(), if (message.orEmpty().startsWith("Đã cập nhật câu hỏi")) AppMint else AppRed, if (message.orEmpty().startsWith("Đã cập nhật câu hỏi")) Icons.Default.CheckCircle else Icons.Default.ErrorOutline)
                }
                Spacer(Modifier.height(14.dp))
                PrimaryAction(if (loading) "Đang lưu..." else "Lưu thay đổi") { if (!loading) showUpdateConfirm = true }
            }
        }

        if (addSubjectDialog) {
            QuickAddDialog(
                title = "Thêm môn học",
                name = newSubjectName,
                onNameChange = { newSubjectName = it },
                onDismiss = { addSubjectDialog = false; newSubjectName = "" },
                onConfirm = {
                    val auth = SessionManager.authorizationHeader() ?: return@QuickAddDialog
                    addSubjectLoading = true
                    scope.launch {
                        try {
                            val response = ApiClient.createSubject(auth, SubjectCreateRequest(name = newSubjectName.trim()))
                            if (response.success) {
                                response.data?.let { subjects = subjects + it; selectedSubject = it; selectedTopic = null }
                                message = "Đã thêm môn học."
                            } else {
                                message = response.message
                            }
                        } catch (e: Exception) {
                            message = "Không thể thêm môn học."
                        } finally {
                            addSubjectLoading = false
                            addSubjectDialog = false
                            newSubjectName = ""
                        }
                    }
                },
                loading = addSubjectLoading
            )
        }

        if (addTopicDialog) {
            val subject = selectedSubject
            if (subject == null) {
                addTopicDialog = false
            } else {
                QuickAddDialog(
                    title = "Thêm chủ đề cho ${subject.name}",
                    name = newTopicName,
                    onNameChange = { newTopicName = it },
                    onDismiss = { addTopicDialog = false; newTopicName = "" },
                    onConfirm = {
                        val auth = SessionManager.authorizationHeader() ?: return@QuickAddDialog
                        addTopicLoading = true
                        scope.launch {
                            try {
                                val response = ApiClient.createTopic(auth, subject.id, TopicCreateRequest(name = newTopicName.trim()))
                                if (response.success) {
                                    response.data?.let { topics = topics + it; selectedTopic = it }
                                    message = "Đã thêm chủ đề."
                                } else {
                                    message = response.message
                                }
                            } catch (e: Exception) {
                                message = "Không thể thêm chủ đề."
                            } finally {
                                addTopicLoading = false
                                addTopicDialog = false
                                newTopicName = ""
                            }
                        }
                    },
                    loading = addTopicLoading
                )
            }
        }
    }
}

@Composable
private fun <T> CatalogDropdown(
    label: String,
    value: String,
    enabled: Boolean,
    items: List<T>,
    itemText: (T) -> String,
    onSelect: (T) -> Unit,
    leadingClearItem: String? = null,
    onClear: (() -> Unit)? = null,
    addLabel: String? = null,
    onAdd: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        Text(label, color = AppMuted, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(value, modifier = Modifier.weight(1f))
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (leadingClearItem != null && onClear != null) {
                    DropdownMenuItem(
                        text = { Text(leadingClearItem) },
                        onClick = {
                            onClear()
                            expanded = false
                        }
                    )
                }
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(itemText(item)) },
                        onClick = {
                            onSelect(item)
                            expanded = false
                        }
                    )
                }
                if (addLabel != null && onAdd != null) {
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(addLabel, color = AppBlue) },
                        onClick = {
                            expanded = false
                            onAdd()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickAddDialog(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    loading: Boolean
) {
    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Tên") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = !loading
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !loading && name.isNotBlank()) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(6.dp))
                }
                Text("Thêm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !loading) { Text("Hủy") }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CreateExamScreen(onGenerate: () -> Unit, onBack: () -> Unit) {
    var randomQuestion by remember { mutableStateOf(true) }
    var randomAnswer by remember { mutableStateOf(true) }
    var title by remember { mutableStateOf("Đề thi thực hành Android") }
    var duration by remember { mutableStateOf("45") }
    var openDateMillis by remember { mutableStateOf<Long?>(null) }
    var openHour by remember { mutableIntStateOf(8) }
    var openMinute by remember { mutableIntStateOf(0) }
    var closeDateMillis by remember { mutableStateOf<Long?>(null) }
    var closeHour by remember { mutableIntStateOf(10) }
    var closeMinute by remember { mutableIntStateOf(0) }
    var showDatePickerFor by remember { mutableStateOf<Boolean?>(null) }
    var showTimePickerFor by remember { mutableStateOf<Boolean?>(null) }
    var questionCount by remember { mutableStateOf("30") }
    var points by remember { mutableStateOf(calculatePointsEach(30)) }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var excelRows by remember { mutableStateOf<List<ExamExcelQuestionRow>>(emptyList()) }
    var excelFileLabel by remember { mutableStateOf<String?>(null) }
    var excelParseErrors by remember { mutableStateOf(emptyList<String>()) }
    var templateLoading by remember { mutableStateOf(false) }
    var excelLoading by remember { mutableStateOf(false) }
    var showExcelConfirm by remember { mutableStateOf(false) }
    var groups by remember { mutableStateOf<List<StudentGroupResponse>>(emptyList()) }
    var selectedGroupIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var groupsLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader() ?: return@LaunchedEffect
        groupsLoading = true
        try {
            val response = ApiClient.getGroups(authorization)
            groups = response.data.orEmpty()
        } catch (_: Exception) {}
        finally { groupsLoading = false }
    }
    val excelMimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

    fun importExamExcel(uri: android.net.Uri) {
        scope.launch {
            excelLoading = true
            message = null
            excelParseErrors = emptyList()
            try {
                val result = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { ExamExcelParser.parse(it) }
                } ?: run {
                    message = "Không thể mở file Excel đã chọn."
                    return@launch
                }

                excelRows = result.rows
                excelFileLabel = if (result.rows.isNotEmpty()) {
                    "Đã chọn Excel đề thi: ${result.rows.size} câu hỏi hợp lệ"
                } else {
                    null
                }
                questionCount = result.rows.size.takeIf { it > 0 }?.toString() ?: questionCount
                excelParseErrors = result.failures.take(5).map { "hàng ${it.rowNumber}: ${it.message}" }
                message = when {
                    result.rows.isNotEmpty() && result.failures.isEmpty() -> {
                        showExcelConfirm = true
                        "File Excel đề thi hợp lệ. Kiểm tra và xác nhận tạo đề thi."
                    }
                    result.rows.isNotEmpty() -> "Đã tải ${result.rows.size} câu hỏi; ${result.failures.size} hàng cần xem lại."
                    else -> result.failures.firstOrNull()?.message ?: "File đã chọn không phải file Excel đề thi hợp lệ."
                }
            } catch (exception: Exception) {
                excelRows = emptyList()
                excelFileLabel = null
                excelParseErrors = emptyList()
                showExcelConfirm = false
                message = "Không thể đọc file Excel đã chọn."
            } finally {
                excelLoading = false
            }
        }
    }

    fun saveTemplate(uri: android.net.Uri) {
        scope.launch {
            templateLoading = true
            message = null
            try {
                val subjectNames = mutableListOf<String>()
                val topicNames = mutableListOf<String>()
                val authorization = SessionManager.authorizationHeader()
                if (authorization != null) {
                    runCatching {
                        val subjects = ApiClient.getSubjects(authorization).data.orEmpty()
                        subjectNames += subjects.map { it.name }
                        subjects.forEach { subjectItem ->
                            val topicsForSubject = runCatching {
                                ApiClient.getTopics(authorization, subjectItem.id).data.orEmpty()
                            }.getOrDefault(emptyList())
                            topicNames += topicsForSubject.map { it.name }
                        }
                    }
                }

                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        output.write(
                            ExamExcelTemplate.createBytes(
                                subjectNames = subjectNames,
                                topicNames = topicNames
                            )
                        )
                    } ?: error("Cannot open output stream")
                }
                message = if (subjectNames.isEmpty()) {
                    "Đã lưu mẫu Excel. Danh sách môn học và chủ đề chưa được tải."
                } else {
                    "Đã lưu mẫu Excel với danh mục môn học và chủ đề."
                }
            } catch (exception: Exception) {
                message = "Không thể lưu mẫu Excel."
            } finally {
                templateLoading = false
            }
        }
    }

    val excelLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) importExamExcel(uri)
    }

    val templateLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(excelMimeType)) { uri ->
        if (uri != null) saveTemplate(uri)
    }

    LaunchedEffect(questionCount) {
        val totalQuestions = questionCount.toIntOrNull() ?: 0
        points = calculatePointsEach(totalQuestions)
    }

    fun saveExam() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Vui lòng đăng nhập lại."; return }
        val durationMinutes = duration.toIntOrNull()
        if (title.isBlank() || durationMinutes == null || durationMinutes < 1) { message = "Nhập tiêu đề và thời gian hợp lệ."; return }
        val questionCountVal = questionCount.toIntOrNull()
        if (questionCountVal == null || questionCountVal < 1) { message = "Nhập số lượng câu hỏi hợp lệ."; return }
        if (questionCountVal > 500) { message = "Tối đa 500 câu hỏi mỗi đề thi."; return }
        if (excelRows.isNotEmpty() && excelRows.size > 500) { message = "File Excel có ${excelRows.size} câu hỏi, tối đa là 500."; return }
        if (openDateMillis == null) { message = "Chọn thời gian mở."; return }
        if (closeDateMillis == null) { message = "Chọn thời gian đóng."; return }
        val openDt = formatDateTime(openDateMillis, openHour, openMinute) ?: return
        val closeDt = formatDateTime(closeDateMillis, closeHour, closeMinute) ?: return
        if (openDt >= closeDt) { message = "Thời gian mở phải trước thời gian đóng."; return }
        scope.launch {
            loading = true; message = null
            try {
                val response = ApiClient.createExam(
                    authorization,
                    ExamCreateRequest(
                        title = title.trim(),
                        durationMinutes = durationMinutes,
                        scorePerQuestion = points.ifBlank { "1.0" },
                        startTime = formatDateTime(openDateMillis, openHour, openMinute),
                        endTime = formatDateTime(closeDateMillis, closeHour, closeMinute),
                        shuffleQuestions = randomQuestion,
                        shuffleAnswers = randomAnswer,
                        groupIds = selectedGroupIds.toList().ifEmpty { null }
                    )
                )
                val createdExam = response.data
                if (response.success && createdExam != null && excelRows.isNotEmpty()) {
                    val importResult = ExamExcelImportService.importQuestions(
                        authorization = authorization,
                        examId = createdExam.id,
                        rows = excelRows,
                        defaultScore = points.ifBlank { null }
                    )
                    message = if (importResult.failures.isEmpty()) {
                        "Đã tạo đề thi: ${createdExam.code}. Đã nhập ${importResult.createdCount} câu hỏi."
                    } else if (importResult.createdCount == 0) {
                        runCatching { ApiClient.deleteExam(authorization, createdExam.id) }
                        val sampleFailures = importResult.failures.take(3).joinToString("; ") {
                            "hàng ${it.rowNumber}: ${it.message}"
                        }
                        val moreFailures = if (importResult.failures.size > 3) " +${importResult.failures.size - 3} nữa" else ""
                        "Không tạo được đề thi vì không thể nhập câu hỏi. Kiểm tra giá trị MÔN HỌC/CHỦ ĐỀ trong file Excel. $sampleFailures$moreFailures"
                    } else {
                        val sampleFailures = importResult.failures.take(3).joinToString("; ") {
                            "hàng ${it.rowNumber}: ${it.message}"
                        }
                        val moreFailures = if (importResult.failures.size > 3) " +${importResult.failures.size - 3} nữa" else ""
                        "Đã tạo đề thi: ${createdExam.code}. Đã nhập ${importResult.createdCount}/${importResult.totalCount} câu hỏi. $sampleFailures$moreFailures"
                    }
                } else {
                    message = if (response.success) "Đã tạo đề thi: ${createdExam?.code}" else response.message
                }
            } catch (exception: HttpException) { message = "Không thể lưu đề thi. Vui lòng kiểm tra quyền." }
            catch (exception: Exception) { message = "Không thể lưu đề thi lúc này." }
            finally { loading = false }
        }
    }

    AppBackground {
        if (showExcelConfirm) {
            ConfirmActionDialog(
                title = "Tạo đề thi từ Excel",
                message = "File Excel hợp lệ với ${excelRows.size} câu hỏi. Tạo đề thi ngay?",
                confirmLabel = if (loading) "Đang tạo..." else "Tạo đề thi",
                processing = loading,
                onConfirm = {
                    showExcelConfirm = false
                    saveExam()
                },
                onDismiss = { showExcelConfirm = false }
            )
        }

        ExamTopBar("Tạo đề thi", onBack)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                SectionTitle("Thông tin đề thi")
                OutlinedTextField(title, { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Tiêu đề đề thi") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(8.dp))
                InfoBanner("Câu hỏi nhập từ Excel sử dụng giá trị MÔN HỌC và CHỦ ĐỀ trong file. Đề thi không sử dụng trường môn học riêng.", AppBlue, Icons.Default.Info)

                SectionTitle("Thời gian")
                OutlinedTextField(duration, { duration = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Thời gian (phút)") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DateTimeField(
                        value = openDateMillis,
                        hour = openHour,
                        minute = openMinute,
                        label = "Giờ mở",
                        modifier = Modifier.weight(1f),
                        onDateClick = { showDatePickerFor = true },
                        onTimeClick = { showTimePickerFor = true }
                    )
                    DateTimeField(
                        value = closeDateMillis,
                        hour = closeHour,
                        minute = closeMinute,
                        label = "Giờ đóng",
                        modifier = Modifier.weight(1f),
                        onDateClick = { showDatePickerFor = false },
                        onTimeClick = { showTimePickerFor = false }
                    )
                }
                if (showDatePickerFor != null) {
                    val initial = if (showDatePickerFor == true) openDateMillis else closeDateMillis
                    val state = rememberDatePickerState(initialSelectedDateMillis = initial ?: System.currentTimeMillis())
                    DatePickerDialog(
                        onDismissRequest = { showDatePickerFor = null },
                        confirmButton = {
                            TextButton(onClick = {
                                val millis = state.selectedDateMillis
                                if (millis != null) {
                                    if (showDatePickerFor == true) {
                                        openDateMillis = millis
                                    } else {
                                        closeDateMillis = millis
                                    }
                                }
                                showDatePickerFor = null
                                showTimePickerFor = if (showDatePickerFor == true) true else false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePickerFor = null }) { Text("Hủy") }
                        }
                    ) { DatePicker(state = state) }
                }
                if (showTimePickerFor != null) {
                    val targetHour = if (showTimePickerFor == true) openHour else closeHour
                    val targetMinute = if (showTimePickerFor == true) openMinute else closeMinute
                    val timeState = rememberTimePickerState(initialHour = targetHour, initialMinute = targetMinute, is24Hour = true)
                    AlertDialog(
                        onDismissRequest = { showTimePickerFor = null },
                        title = { Text(if (showTimePickerFor == true) "Giờ mở" else "Giờ đóng") },
                        text = { TimePicker(state = timeState, colors = TimePickerDefaults.colors()) },
                        confirmButton = {
                            TextButton(onClick = {
                                if (showTimePickerFor == true) {
                                    openHour = timeState.hour; openMinute = timeState.minute
                                } else {
                                    closeHour = timeState.hour; closeMinute = timeState.minute
                                }
                                showTimePickerFor = null
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePickerFor = null }) { Text("Hủy") }
                        }
                    )
                }

                SectionTitle("Tính điểm")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(questionCount, { questionCount = it }, modifier = Modifier.weight(1f), label = { Text("Câu hỏi") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                    OutlinedTextField(
                        value = points,
                        onValueChange = {},
                        modifier = Modifier.weight(1f),
                        label = { Text("Điểm mỗi câu") },
                        enabled = false,
                        readOnly = true,
                        shape = MaterialTheme.shapes.medium
                    )
                }
                Spacer(Modifier.height(8.dp))
                InfoBanner("Tổng điểm đề thi cố định là 10.0. Điểm mỗi câu được tính tự động.", AppBlue, Icons.Default.Info)

                SectionTitle("Nhập Excel")
                InfoBanner(
                    "Màn hình này sử dụng định dạng Excel đề thi. File Excel ngân hàng câu hỏi sẽ không hoạt động ở đây.",
                    AppBlue,
                    Icons.Default.Info
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { templateLauncher.launch("exam-template.xlsx") },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = MaterialTheme.shapes.medium,
                        enabled = !loading && !templateLoading
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (templateLoading) "Đang lưu..." else "Mẫu")
                    }
                    OutlinedButton(
                        onClick = { excelLauncher.launch(arrayOf(excelMimeType)) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = MaterialTheme.shapes.medium,
                        enabled = !loading && !excelLoading
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (excelLoading) "Đang đọc..." else "Chọn Excel đề thi")
                    }
                }
                excelFileLabel?.let {
                    Spacer(Modifier.height(8.dp))
                    InfoBanner(it, if (excelRows.isNotEmpty()) AppMint else AppAmber, Icons.Default.Info)
                }
                if (excelParseErrors.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    InfoBanner(excelParseErrors.joinToString("; "), AppAmber, Icons.Default.Warning)
                }

                SectionTitle("Tùy chọn")
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column { Text("Xáo trộn câu hỏi", fontWeight = FontWeight.Medium); Text("Xáo trộn thứ tự câu hỏi", color = AppMuted, style = MaterialTheme.typography.bodyMedium) }
                            Switch(randomQuestion, { randomQuestion = it }, colors = SwitchDefaults.colors(checkedTrackColor = AppIndigo))
                        }
                        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = AppCardBorder)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column { Text("Xáo trộn đáp án", fontWeight = FontWeight.Medium); Text("Xáo trộn thứ tự đáp án", color = AppMuted, style = MaterialTheme.typography.bodyMedium) }
                            Switch(randomAnswer, { randomAnswer = it }, colors = SwitchDefaults.colors(checkedTrackColor = AppIndigo))
                        }
                    }
                }

                SectionTitle("Lớp học")
                GroupSelector(
                    groups = groups,
                    loading = groupsLoading,
                    selectedIds = selectedGroupIds,
                    onSelectionChanged = { selectedGroupIds = it },
                    enabled = !loading
                )

                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    val currentMessage = message.orEmpty()
                    val successMessage = currentMessage.startsWith("Đã tạo đề thi") ||
                        currentMessage.startsWith("Đã tải") ||
                        currentMessage.startsWith("Đã lưu mẫu Excel")
                    val warningMessage = currentMessage.contains("cần xem lại") ||
                        currentMessage.startsWith("File đã chọn không phải")
                    InfoBanner(
                        currentMessage,
                        when {
                            successMessage -> AppMint
                            warningMessage -> AppAmber
                            else -> AppRed
                        },
                        when {
                            successMessage -> Icons.Default.CheckCircle
                            warningMessage -> Icons.Default.Warning
                            else -> Icons.Default.ErrorOutline
                        }
                    )
                }

                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { saveExam() }, modifier = Modifier
                        .weight(1f)
                        .height(48.dp), shape = MaterialTheme.shapes.medium, enabled = !loading) { Text(if (loading) "Đang lưu..." else "Lưu đề thi") }
                    Button(onClick = onGenerate, modifier = Modifier
                        .weight(1f)
                        .height(48.dp), shape = MaterialTheme.shapes.medium, colors = ButtonDefaults.buttonColors(containerColor = AppViolet)) {
                        Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Tự động tạo")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExamScreen(onBack: () -> Unit) {
    val exam = ExamAttemptStore.selectedExam.value
    var randomQuestion by remember(exam?.id) { mutableStateOf(exam?.shuffleQuestions ?: true) }
    var randomAnswer by remember(exam?.id) { mutableStateOf(exam?.shuffleAnswers ?: true) }
    var title by remember(exam?.id) { mutableStateOf(exam?.title.orEmpty()) }
    var duration by remember(exam?.id) { mutableStateOf(exam?.durationMinutes?.toString() ?: "45") }
    var questionCount by remember(exam?.id) {
        val raw = exam?.totalQuestions
        val count = if (raw != null && raw > 0) raw.toString()
        else exam?.scorePerQuestion?.toDoubleOrNull()?.let { score ->
            if (score > 0) "%.0f".format(10.0 / score) else null
        } ?: "30"
        mutableStateOf(count)
    }
    var points by remember(exam?.id) { mutableStateOf(exam?.scorePerQuestion ?: "1.0") }
    var openDateMillis by remember(exam?.id) { mutableStateOf<Long?>(null) }
    var openHour by remember(exam?.id) { mutableIntStateOf(8) }
    var openMinute by remember(exam?.id) { mutableIntStateOf(0) }
    var closeDateMillis by remember(exam?.id) { mutableStateOf<Long?>(null) }
    var closeHour by remember(exam?.id) { mutableIntStateOf(10) }
    var closeMinute by remember(exam?.id) { mutableIntStateOf(0) }
    var showDatePickerFor by remember { mutableStateOf<Boolean?>(null) }
    var showTimePickerFor by remember { mutableStateOf<Boolean?>(null) }
    var groups by remember { mutableStateOf<List<StudentGroupResponse>>(emptyList()) }
    var selectedGroupIds by remember(exam?.id) { mutableStateOf(exam?.groupIds?.toSet() ?: emptySet()) }
    var groupsLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var showUpdateConfirm by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }

    LaunchedEffect(exam?.id) {
        val authorization = SessionManager.authorizationHeader() ?: return@LaunchedEffect
        groupsLoading = true
        try {
            val response = ApiClient.getGroups(authorization)
            groups = response.data.orEmpty()
        } catch (_: Exception) {}
        finally { groupsLoading = false }
    }

    LaunchedEffect(exam?.id) {
        if (exam != null) {
            val parsedStart = exam.startTime.parseExamDateTime()
            if (parsedStart != null) {
                openDateMillis = parsedStart.dateMillis
                openHour = parsedStart.hour
                openMinute = parsedStart.minute
            }
            val parsedEnd = exam.endTime.parseExamDateTime()
            if (parsedEnd != null) {
                closeDateMillis = parsedEnd.dateMillis
                closeHour = parsedEnd.hour
                closeMinute = parsedEnd.minute
            }
        }
    }

    LaunchedEffect(questionCount) {
        val total = questionCount.toIntOrNull() ?: 0
        if (total > 0) {
            points = calculatePointsEach(total)
        }
    }

    fun updateExam() {
        val selectedExam = exam
        if (selectedExam == null) { message = "Vui lòng chọn đề thi trước khi sửa."; return }
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Vui lòng đăng nhập lại."; return }
        val durationMinutes = duration.toIntOrNull()
        if (title.isBlank() || durationMinutes == null || durationMinutes < 1) { message = "Nhập tiêu đề và thời gian hợp lệ."; return }
        val questionCountVal = questionCount.toIntOrNull()
        if (questionCountVal == null || questionCountVal < 1) { message = "Nhập số lượng câu hỏi hợp lệ."; return }
        if (questionCountVal > 500) { message = "Tối đa 500 câu hỏi mỗi đề thi."; return }
        val openDt = formatDateTime(openDateMillis, openHour, openMinute)
        val closeDt = formatDateTime(closeDateMillis, closeHour, closeMinute)

        scope.launch {
            loading = true; message = null
            try {
                val response = ApiClient.updateExam(
                    authorization,
                    selectedExam.id,
                    ExamUpdateRequest(
                        title = title.trim(),
                        durationMinutes = durationMinutes,
                        scorePerQuestion = points.ifBlank { "1.0" },
                        startTime = openDt,
                        endTime = closeDt,
                        shuffleQuestions = randomQuestion,
                        shuffleAnswers = randomAnswer,
                        groupIds = selectedGroupIds.toList().ifEmpty { null }
                    )
                )
                if (response.success) {
                    response.data?.let { ExamAttemptStore.selectExam(it.id, it) }
                    message = "Đã cập nhật đề thi."
                } else {
                    message = response.message
                }
            } catch (exception: HttpException) { message = "Không thể cập nhật đề thi. Vui lòng kiểm tra quyền." }
            catch (exception: Exception) { message = "Không thể cập nhật đề thi lúc này." }
            finally { loading = false }
        }
    }

    AppBackground {
        if (showUpdateConfirm) {
            ConfirmActionDialog(
                title = "Cập nhật đề thi",
                message = "Lưu thay đổi cho đề thi này?",
                confirmLabel = if (loading) "Đang lưu..." else "Lưu",
                processing = loading,
                onConfirm = {
                    showUpdateConfirm = false
                    updateExam()
                },
                onDismiss = { showUpdateConfirm = false }
            )
        }

        ExamTopBar("Sửa đề thi", onBack)
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                InfoBanner(
                    exam?.let { "Đang sửa đề thi: ${it.code}" } ?: "Chưa chọn đề thi.",
                    if (exam != null) AppMint else AppAmber,
                    Icons.Default.Info
                )

                SectionTitle("Thông tin đề thi")
                OutlinedTextField(title, { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Tiêu đề đề thi") }, enabled = !loading && exam != null, shape = MaterialTheme.shapes.medium)

                SectionTitle("Thời gian")
                OutlinedTextField(duration, { duration = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Thời gian (phút)") }, enabled = !loading && exam != null, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DateTimeField(
                        value = openDateMillis,
                        hour = openHour,
                        minute = openMinute,
                        label = "Giờ mở",
                        modifier = Modifier.weight(1f),
                        onDateClick = { showDatePickerFor = true },
                        onTimeClick = { showTimePickerFor = true }
                    )
                    DateTimeField(
                        value = closeDateMillis,
                        hour = closeHour,
                        minute = closeMinute,
                        label = "Giờ đóng",
                        modifier = Modifier.weight(1f),
                        onDateClick = { showDatePickerFor = false },
                        onTimeClick = { showTimePickerFor = false }
                    )
                }
                if (showDatePickerFor != null) {
                    val initial = if (showDatePickerFor == true) openDateMillis else closeDateMillis
                    val state = rememberDatePickerState(initialSelectedDateMillis = initial ?: System.currentTimeMillis())
                    DatePickerDialog(
                        onDismissRequest = { showDatePickerFor = null },
                        confirmButton = {
                            TextButton(onClick = {
                                val millis = state.selectedDateMillis
                                if (millis != null) {
                                    if (showDatePickerFor == true) {
                                        openDateMillis = millis
                                    } else {
                                        closeDateMillis = millis
                                    }
                                }
                                showDatePickerFor = null
                                showTimePickerFor = if (showDatePickerFor == true) true else false
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePickerFor = null }) { Text("Hủy") }
                        }
                    ) { DatePicker(state = state) }
                }
                if (showTimePickerFor != null) {
                    val targetHour = if (showTimePickerFor == true) openHour else closeHour
                    val targetMinute = if (showTimePickerFor == true) openMinute else closeMinute
                    val timeState = rememberTimePickerState(initialHour = targetHour, initialMinute = targetMinute, is24Hour = true)
                    AlertDialog(
                        onDismissRequest = { showTimePickerFor = null },
                        title = { Text(if (showTimePickerFor == true) "Giờ mở" else "Giờ đóng") },
                        text = { TimePicker(state = timeState, colors = TimePickerDefaults.colors()) },
                        confirmButton = {
                            TextButton(onClick = {
                                if (showTimePickerFor == true) {
                                    openHour = timeState.hour; openMinute = timeState.minute
                                } else {
                                    closeHour = timeState.hour; closeMinute = timeState.minute
                                }
                                showTimePickerFor = null
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showTimePickerFor = null }) { Text("Hủy") }
                        }
                    )
                }

                SectionTitle("Tính điểm")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = questionCount, { questionCount = it }, modifier = Modifier.weight(1f), label = { Text("Câu hỏi") }, enabled = !loading && exam != null, shape = MaterialTheme.shapes.medium)
                    OutlinedTextField(
                        value = points,
                        onValueChange = {},
                        modifier = Modifier.weight(1f),
                        label = { Text("Điểm mỗi câu") },
                        enabled = false,
                        readOnly = true,
                        shape = MaterialTheme.shapes.medium
                    )
                }
                Spacer(Modifier.height(8.dp))
                InfoBanner("Tổng điểm đề thi cố định là 10.0. Điểm mỗi câu được tính tự động.", AppBlue, Icons.Default.Info)

                SectionTitle("Tùy chọn")
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column { Text("Xáo trộn câu hỏi", fontWeight = FontWeight.Medium); Text("Xáo trộn thứ tự câu hỏi", color = AppMuted, style = MaterialTheme.typography.bodyMedium) }
                            Switch(randomQuestion, { randomQuestion = it }, enabled = !loading && exam != null, colors = SwitchDefaults.colors(checkedTrackColor = AppIndigo))
                        }
                        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = AppCardBorder)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column { Text("Xáo trộn đáp án", fontWeight = FontWeight.Medium); Text("Xáo trộn thứ tự đáp án", color = AppMuted, style = MaterialTheme.typography.bodyMedium) }
                            Switch(randomAnswer, { randomAnswer = it }, enabled = !loading && exam != null, colors = SwitchDefaults.colors(checkedTrackColor = AppIndigo))
                        }
                    }
                }

                SectionTitle("Lớp học")
                GroupSelector(
                    groups = groups,
                    loading = groupsLoading,
                    selectedIds = selectedGroupIds,
                    onSelectionChanged = { selectedGroupIds = it },
                    enabled = !loading && exam != null
                )

                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    InfoBanner(message.orEmpty(), if (message.orEmpty().startsWith("Đã cập nhật đề thi")) AppMint else AppRed, if (message.orEmpty().startsWith("Đã cập nhật đề thi")) Icons.Default.CheckCircle else Icons.Default.ErrorOutline)
                }

                Spacer(Modifier.height(14.dp))
                PrimaryAction(if (loading) "Đang lưu..." else "Lưu thay đổi") { if (!loading) showUpdateConfirm = true }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimeField(
    value: Long?,
    hour: Int,
    minute: Int,
    label: String,
    modifier: Modifier = Modifier,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    val displayText = if (value != null) {
        val cal = Calendar.getInstance().apply { timeInMillis = value }
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        "${fmt.format(cal.time)} ${"%02d:%02d".format(hour, minute)}"
    } else {
        "Chưa đặt"
    }
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = modifier.border(1.dp, AppCardBorder, MaterialTheme.shapes.medium)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, color = AppMuted, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(displayText, fontWeight = FontWeight.SemiBold, color = AppText, modifier = Modifier.weight(1f))
                Icon(Icons.Default.DateRange, null, tint = AppIndigo, modifier = Modifier.size(18.dp).clickable { onDateClick() })
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.Schedule, null, tint = AppIndigo, modifier = Modifier.size(18.dp).clickable { onTimeClick() })
            }
        }
    }
}

private data class ExamDateTimeComponents(val dateMillis: Long, val hour: Int, val minute: Int)

private fun String?.parseExamDateTime(): ExamDateTimeComponents? {
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
            val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                isLenient = false
                if (pattern.endsWith("'Z'")) {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
            }
            val date = sdf.parse(raw) ?: return@firstNotNullOfOrNull null
            val cal = Calendar.getInstance().apply { time = date }
            ExamDateTimeComponents(date.time, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        }.getOrNull()
    }
}

private fun formatDateTime(dateMillis: Long?, hour: Int, minute: Int): String? {
    if (dateMillis == null) return null
    val cal = Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }
    val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    return fmt.format(cal.time)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GroupSelector(
    groups: List<StudentGroupResponse>,
    loading: Boolean,
    selectedIds: Set<Long>,
    onSelectionChanged: (Set<Long>) -> Unit,
    enabled: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Chọn lớp học", fontWeight = FontWeight.Medium)
                    Text(
                        if (selectedIds.isEmpty()) "Tất cả học sinh đều có thể xem"
                        else "${selectedIds.size} lớp được chọn",
                        color = AppMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                OutlinedButton(
                    onClick = { showDialog = true },
                    enabled = enabled
                ) {
                    Text(if (selectedIds.isEmpty()) "Chọn lớp" else "Thay đổi")
                }
            }
            if (selectedIds.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    groups.filter { it.id in selectedIds }.forEach { group ->
                        AssistChip(
                            onClick = { onSelectionChanged(selectedIds - group.id) },
                            label = { Text(group.name, style = MaterialTheme.typography.bodySmall) },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Chọn lớp học") },
            text = {
                if (loading) {
                    Text("Đang tải danh sách lớp...")
                } else if (groups.isEmpty()) {
                    Column {
                        Text("Chưa có lớp học nào.")
                        Spacer(Modifier.height(8.dp))
                        Text("Tạo lớp học trong mục Quản trị để giới hạn đề thi theo lớp.", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    Column {
                        groups.forEach { group ->
                            Row(
                                Modifier.fillMaxWidth().clickable {
                                    onSelectionChanged(
                                        if (group.id in selectedIds) selectedIds - group.id
                                        else selectedIds + group.id
                                    )
                                }.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = group.id in selectedIds,
                                    onCheckedChange = { checked ->
                                        onSelectionChanged(
                                            if (checked) selectedIds + group.id
                                            else selectedIds - group.id
                                        )
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = AppIndigo)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(group.name, fontWeight = FontWeight.Medium)
                                    group.description?.takeIf { it.isNotBlank() }?.let {
                                        Text(it, color = AppMuted, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("Xong") }
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AutoGenerateExamScreen(onBack: () -> Unit) {
    var title by remember { mutableStateOf("Đề thi Android tự động") }
    var subjects by remember { mutableStateOf<List<SubjectResponse>>(emptyList()) }
    var topics by remember { mutableStateOf<List<TopicResponse>>(emptyList()) }
    var selectedSubject by remember { mutableStateOf<SubjectResponse?>(null) }
    var selectedTopic by remember { mutableStateOf<TopicResponse?>(null) }
    var easy by remember { mutableStateOf("10") }
    var medium by remember { mutableStateOf("15") }
    var hard by remember { mutableStateOf("5") }
    var duration by remember { mutableStateOf("45") }
    var points by remember { mutableStateOf(calculatePointsEach(30)) }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var catalogLoading by remember { mutableStateOf(false) }
    var openDateMillis by remember { mutableStateOf<Long?>(null) }
    var openHour by remember { mutableIntStateOf(8) }
    var openMinute by remember { mutableIntStateOf(0) }
    var closeDateMillis by remember { mutableStateOf<Long?>(null) }
    var closeHour by remember { mutableIntStateOf(10) }
    var closeMinute by remember { mutableIntStateOf(0) }
    var showDatePickerFor by remember { mutableStateOf<Boolean?>(null) }
    var showTimePickerFor by remember { mutableStateOf<Boolean?>(null) }
    var addSubjectDialog by remember { mutableStateOf(false) }
    var addTopicDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }
    var newTopicName by remember { mutableStateOf("") }
    var addSubjectLoading by remember { mutableStateOf(false) }
    var addTopicLoading by remember { mutableStateOf(false) }
    var groups by remember { mutableStateOf<List<StudentGroupResponse>>(emptyList()) }
    var selectedGroupIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var groupsLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader() ?: return@LaunchedEffect
        groupsLoading = true
        try {
            val response = ApiClient.getGroups(authorization)
            groups = response.data.orEmpty()
        } catch (_: Exception) {}
        finally { groupsLoading = false }
    }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            return@LaunchedEffect
        }
        catalogLoading = true
        try {
            val response = ApiClient.getSubjects(authorization)
            subjects = response.data.orEmpty()
            selectedSubject = subjects.firstOrNull()
            message = if (subjects.isEmpty()) "Không có môn học." else null
        } catch (exception: Exception) {
            message = "Không thể tải môn học lúc này."
        } finally {
            catalogLoading = false
        }
    }

    LaunchedEffect(selectedSubject?.id) {
        val authorization = SessionManager.authorizationHeader() ?: return@LaunchedEffect
        val subject = selectedSubject ?: run {
            topics = emptyList()
            selectedTopic = null
            return@LaunchedEffect
        }
        try {
            val response = ApiClient.getTopics(authorization, subject.id)
            topics = response.data.orEmpty()
            selectedTopic = selectedTopic?.takeIf { current ->
                topics.any { it.id == current.id }
            }
        } catch (exception: Exception) {
            topics = emptyList()
            selectedTopic = null
            message = "Không thể tải chủ đề lúc này."
        }
    }

    LaunchedEffect(easy, medium, hard) {
        val totalQuestions = (easy.toIntOrNull() ?: 0) + (medium.toIntOrNull() ?: 0) + (hard.toIntOrNull() ?: 0)
        points = calculatePointsEach(totalQuestions)
    }

    fun generateExam() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Vui lòng đăng nhập lại."; return }
        val subject = selectedSubject
        if (subject == null) { message = "Vui lòng chọn môn học."; return }
        val durationMinutes = duration.toIntOrNull()
        val easyCount = easy.toIntOrNull()
        val mediumCount = medium.toIntOrNull()
        val hardCount = hard.toIntOrNull()
        if (title.isBlank() || durationMinutes == null || durationMinutes < 1) { message = "Nhập tiêu đề và thời gian hợp lệ."; return }
        if (easyCount == null || mediumCount == null || hardCount == null || easyCount < 0 || mediumCount < 0 || hardCount < 0) {
            message = "Số lượng câu hỏi phải là số hợp lệ."
            return
        }
        if (easyCount + mediumCount + hardCount == 0) { message = "Chọn ít nhất một câu hỏi."; return }
        if (openDateMillis == null) { message = "Chọn thời gian mở."; return }
        if (closeDateMillis == null) { message = "Chọn thời gian đóng."; return }
        val openDt = formatDateTime(openDateMillis, openHour, openMinute) ?: return
        val closeDt = formatDateTime(closeDateMillis, closeHour, closeMinute) ?: return
        if (openDt >= closeDt) { message = "Thời gian mở phải trước thời gian đóng."; return }
        scope.launch {
            loading = true; message = null
            try {
                val response = ApiClient.generateExam(
                    authorization,
                    ExamGenerateRequest(
                        title = title.trim(),
                        durationMinutes = durationMinutes,
                        scorePerQuestion = points.ifBlank { "1.0" },
                        subjectId = subject.id,
                        topicId = selectedTopic?.id,
                        easyCount = easyCount,
                        mediumCount = mediumCount,
                        hardCount = hardCount,
                        startTime = openDt,
                        endTime = closeDt,
                        groupIds = selectedGroupIds.toList().ifEmpty { null }
                    )
                )
                message = if (response.success) "Đã tạo đề thi: ${response.data?.code}" else response.message
            } catch (exception: HttpException) {
                val backendMessage = runCatching {
                    val body = exception.response()?.errorBody()?.string().orEmpty()
                    gson.fromJson(body, ApiResponse::class.java)?.message
                }.getOrNull().orEmpty()
                message = backendMessage.ifBlank {
                    if (exception.code() == 403) "Bạn không có quyền tạo đề thi." else "Không thể tạo đề thi lúc này."
                }
            }
            catch (exception: Exception) { message = "Không thể tạo đề thi lúc này." }
            finally { loading = false }
        }
    }

    AppBackground {
        ExamTopBar("Tự động tạo đề thi", onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            SectionTitle("Thông tin đề thi")
            OutlinedTextField(title, { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Tiêu đề đề thi") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(duration, { duration = it }, modifier = Modifier.weight(1f), label = { Text("Thời gian") }, enabled = !loading, shape = MaterialTheme.shapes.medium, singleLine = true)
                OutlinedTextField(
                    value = points,
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    label = { Text("Điểm mỗi câu") },
                    enabled = false,
                    readOnly = true,
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
            }
            Spacer(Modifier.height(8.dp))
            InfoBanner("Tổng điểm đề thi cố định là 10.0. Điểm mỗi câu được tính tự động.", AppBlue, Icons.Default.Info)

            SectionTitle("Thời gian")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DateTimeField(
                        value = openDateMillis,
                        hour = openHour,
                        minute = openMinute,
                        label = "Giờ mở",
                        modifier = Modifier.weight(1f),
                        onDateClick = { showDatePickerFor = true },
                        onTimeClick = { showTimePickerFor = true }
                    )
                    DateTimeField(
                        value = closeDateMillis,
                        hour = closeHour,
                        minute = closeMinute,
                        label = "Giờ đóng",
                        modifier = Modifier.weight(1f),
                        onDateClick = { showDatePickerFor = false },
                        onTimeClick = { showTimePickerFor = false }
                    )
            }
            if (showDatePickerFor != null) {
                val initial = if (showDatePickerFor == true) openDateMillis else closeDateMillis
                val state = rememberDatePickerState(initialSelectedDateMillis = initial ?: System.currentTimeMillis())
                DatePickerDialog(
                    onDismissRequest = { showDatePickerFor = null },
                    confirmButton = {
                        TextButton(onClick = {
                            val millis = state.selectedDateMillis
                            if (millis != null) {
                                if (showDatePickerFor == true) {
                                    openDateMillis = millis
                                } else {
                                    closeDateMillis = millis
                                }
                            }
                            showDatePickerFor = null
                            showTimePickerFor = if (showDatePickerFor == true) true else false
                        }) { Text("OK") }
                    },
                        dismissButton = {
                            TextButton(onClick = { showDatePickerFor = null }) { Text("Hủy") }
                        }
                    ) { DatePicker(state = state) }
                }
                if (showTimePickerFor != null) {
                    val targetHour = if (showTimePickerFor == true) openHour else closeHour
                    val targetMinute = if (showTimePickerFor == true) openMinute else closeMinute
                    val timeState = rememberTimePickerState(initialHour = targetHour, initialMinute = targetMinute, is24Hour = true)
                    AlertDialog(
                        onDismissRequest = { showTimePickerFor = null },
                        title = { Text(if (showTimePickerFor == true) "Giờ mở" else "Giờ đóng") },
                    text = { TimePicker(state = timeState, colors = TimePickerDefaults.colors()) },
                    confirmButton = {
                        TextButton(onClick = {
                            if (showTimePickerFor == true) {
                                openHour = timeState.hour; openMinute = timeState.minute
                            } else {
                                closeHour = timeState.hour; closeMinute = timeState.minute
                            }
                            showTimePickerFor = null
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showTimePickerFor = null }) { Text("Hủy") }
                    }
                )
            }

                SectionTitle("Phân loại")
                CatalogDropdown(
                    label = "Môn học",
                    value = selectedSubject?.name ?: if (catalogLoading) "Đang tải môn học..." else "Chọn môn học",
                    enabled = !loading && !catalogLoading && subjects.isNotEmpty(),
                    items = subjects,
                    itemText = { it.name },
                    onSelect = {
                        selectedSubject = it
                        selectedTopic = null
                    },
                    addLabel = "+ Thêm môn học",
                onAdd = { addSubjectDialog = true }
            )
            Spacer(Modifier.height(8.dp))
            CatalogDropdown(
                label = "Chủ đề",
                value = selectedTopic?.name ?: "Tất cả chủ đề",
                enabled = !loading && topics.isNotEmpty(),
                items = topics,
                itemText = { it.name },
                onSelect = { selectedTopic = it },
                leadingClearItem = "Tất cả chủ đề",
                onClear = { selectedTopic = null },
                addLabel = if (selectedSubject != null) "+ Thêm chủ đề" else null,
                onAdd = if (selectedSubject != null) {{ addTopicDialog = true }} else null
            )

            SectionTitle("Câu hỏi theo độ khó")
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ChipText("Dễ", AppMint); Spacer(Modifier.width(12.dp))
                        OutlinedTextField(easy, { easy = it }, modifier = Modifier.weight(1f), enabled = !loading, shape = MaterialTheme.shapes.medium, singleLine = true)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ChipText("Trung bình", AppAmber); Spacer(Modifier.width(12.dp))
                        OutlinedTextField(medium, { medium = it }, modifier = Modifier.weight(1f), enabled = !loading, shape = MaterialTheme.shapes.medium, singleLine = true)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ChipText("Khó", AppRed); Spacer(Modifier.width(12.dp))
                        OutlinedTextField(hard, { hard = it }, modifier = Modifier.weight(1f), enabled = !loading, shape = MaterialTheme.shapes.medium, singleLine = true)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .background(AppLilac)
                        .fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Xem trước cấu hình", fontWeight = FontWeight.Bold, color = AppIndigo)
                        Spacer(Modifier.height(8.dp))
                        val total = (easy.toIntOrNull() ?: 0) + (medium.toIntOrNull() ?: 0) + (hard.toIntOrNull() ?: 0)
                        Text("$total câu hỏi - ${duration.ifBlank { "--" }} phút - Đã xáo trộn - ${points.ifBlank { "--" }} điểm mỗi câu", color = AppMuted)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            SectionTitle("Lớp học")
            GroupSelector(
                groups = groups,
                loading = groupsLoading,
                selectedIds = selectedGroupIds,
                onSelectionChanged = { selectedGroupIds = it },
                enabled = !loading
            )

            if (message != null) {
                Spacer(Modifier.height(10.dp))
                InfoBanner(message.orEmpty(), if (message.orEmpty().startsWith("Đã tạo đề thi")) AppMint else AppRed, if (message.orEmpty().startsWith("Đã tạo đề thi")) Icons.Default.CheckCircle else Icons.Default.ErrorOutline)
            }

            Spacer(Modifier.height(14.dp))
            PrimaryAction(if (loading) "Đang tạo..." else "Tạo đề thi") { if (!loading) generateExam() }
            Spacer(Modifier.height(24.dp))
        }

        if (addSubjectDialog) {
            QuickAddDialog(
                title = "Thêm môn học",
                name = newSubjectName,
                onNameChange = { newSubjectName = it },
                onDismiss = { addSubjectDialog = false; newSubjectName = "" },
                onConfirm = {
                    val auth = SessionManager.authorizationHeader() ?: return@QuickAddDialog
                    addSubjectLoading = true
                    scope.launch {
                        try {
                            val response = ApiClient.createSubject(auth, SubjectCreateRequest(name = newSubjectName.trim()))
                            if (response.success) {
                                response.data?.let { subjects = subjects + it; selectedSubject = it; selectedTopic = null }
                                message = "Đã thêm môn học."
                            } else {
                                message = response.message
                            }
                        } catch (e: Exception) {
                            message = "Không thể thêm môn học."
                        } finally {
                            addSubjectLoading = false
                            addSubjectDialog = false
                            newSubjectName = ""
                        }
                    }
                },
                loading = addSubjectLoading
            )
        }

        if (addTopicDialog) {
            val subject = selectedSubject
            if (subject == null) {
                addTopicDialog = false
            } else {
                QuickAddDialog(
                    title = "Thêm chủ đề cho ${subject.name}",
                    name = newTopicName,
                    onNameChange = { newTopicName = it },
                    onDismiss = { addTopicDialog = false; newTopicName = "" },
                    onConfirm = {
                        val auth = SessionManager.authorizationHeader() ?: return@QuickAddDialog
                        addTopicLoading = true
                        scope.launch {
                            try {
                                val response = ApiClient.createTopic(auth, subject.id, TopicCreateRequest(name = newTopicName.trim()))
                                if (response.success) {
                                    response.data?.let { topics = topics + it; selectedTopic = it }
                                    message = "Đã thêm chủ đề."
                                } else {
                                    message = response.message
                                }
                            } catch (e: Exception) {
                                message = "Không thể thêm chủ đề."
                            } finally {
                                addTopicLoading = false
                                addTopicDialog = false
                                newTopicName = ""
                            }
                        }
                    },
                    loading = addTopicLoading
                )
            }
        }
    }
}

@Composable
fun LiveMonitoringScreen(onBack: () -> Unit) {
    var exams by remember { mutableStateOf<List<ExamResponse>>(emptyList()) }
    var selectedExam by remember { mutableStateOf<ExamResponse?>(null) }
    var report by remember { mutableStateOf<ExamReportResponse?>(null) }
    var auditLogs by remember { mutableStateOf<List<AuditLogResponse>>(emptyList()) }
    var loadingExams by remember { mutableStateOf(true) }
    var loadingReport by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun loadAuditLogs() {
        scope.launch {
            val auth = SessionManager.authorizationHeader() ?: return@launch
            runCatching {
                val response = ApiClient.getAuditLogs(auth)
                if (response.success) {
                    auditLogs = response.data.orEmpty()
                        .filter { log ->
                            selectedExam == null || log.resourceId == selectedExam?.id
                        }
                        .sortedByDescending { it.createdAt.orEmpty() }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val auth = SessionManager.authorizationHeader()
        if (auth == null) {
            message = "Vui lòng đăng nhập lại."
            loadingExams = false
            return@LaunchedEffect
        }
        loadingExams = true
        try {
            val response = ApiClient.getExams(auth)
            exams = response.data.orEmpty()
            selectedExam = exams.firstOrNull()
            message = if (exams.isEmpty()) "Không tìm thấy đề thi." else null
        } catch (e: Exception) {
            message = "Không thể tải đề thi."
        } finally {
            loadingExams = false
        }
    }

    LaunchedEffect(selectedExam?.id) {
        val exam = selectedExam ?: return@LaunchedEffect
        val auth = SessionManager.authorizationHeader() ?: return@LaunchedEffect
        loadingReport = true
        try {
            val response = ApiClient.getExamReport(auth, exam.id)
            report = response.data
        } catch (e: Exception) {
            message = "Không thể tải báo cáo."
        } finally {
            loadingReport = false
        }
        loadAuditLogs()
    }

    LaunchedEffect(selectedExam?.id) {
        while (true) {
            delay(10_000L)
            loadAuditLogs()
            val exam = selectedExam ?: continue
            val auth = SessionManager.authorizationHeader() ?: continue
            runCatching {
                val response = ApiClient.getExamReport(auth, exam.id)
                if (response.success) report = response.data
            }
        }
    }

    AppBackground {
        ExamTopBar("Giám sát trực tiếp", onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            SectionTitle("Chọn đề thi")
            CatalogDropdown(
                label = "Đề thi",
                value = selectedExam?.let { "${it.code} - ${it.title}" }
                    ?: if (loadingExams) "Đang tải..." else "Chưa chọn đề thi",
                enabled = !loadingExams && exams.isNotEmpty(),
                items = exams,
                itemText = { "${it.code} - ${it.title}" },
                onSelect = { selectedExam = it; message = null }
            )

            message?.let {
                Spacer(Modifier.height(8.dp))
                InfoBanner(it, AppAmber, Icons.Default.Info)
            }

            if (loadingReport) {
                Spacer(Modifier.height(8.dp))
                LoadingStateCard("Đang tải thí sinh...")
            }

            report?.let { data ->
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricCard("Tổng", data.totalResults.toString(), "thí sinh", AppBlue, Icons.Default.Person, modifier = Modifier.weight(1f).height(90.dp))
                    MetricCard("Đã nộp", data.submittedCount.toString(), "xong", AppMint, Icons.Default.CheckCircle, modifier = Modifier.weight(1f).height(90.dp))
                    MetricCard("Đang làm", (data.totalResults - data.submittedCount).toString(), "đang tiến hành", AppAmber, Icons.Default.Info, modifier = Modifier.weight(1f).height(90.dp))
                }
                Spacer(Modifier.height(8.dp))
                SectionTitle("Thí sinh")
                val results = data.results.orEmpty()
                if (results.isEmpty()) {
                    InfoBanner("Chưa có thí sinh.", AppAmber, Icons.Default.Info)
                } else {
                    results.forEach { result ->
                        val statusColor = reportStatusColor(result.status)
                        val studentName = result.studentName?.takeIf { it.isNotBlank() }
                            ?: result.username?.takeIf { it.isNotBlank() }
                            ?: result.studentCode?.takeIf { it.isNotBlank() }
                            ?: "Thí sinh #${result.studentId}"
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = AppSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(40.dp)
                                        .background(statusColor.copy(alpha = 0.12f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        studentName.first().toString().uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(studentName, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        reportStatusLabel(result.status),
                                        color = statusColor,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    result.score?.takeIf { it.isNotBlank() }?.let { "$it / 10" } ?: "--",
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            SectionTitle("Nhật ký hoạt động")
            if (auditLogs.isEmpty()) {
                InfoBanner("Chưa có hoạt động nào.", AppAmber, Icons.Default.Info)
            } else {
                auditLogs.forEach { log ->
                    val logColor = when (log.action) {
                        "SCREENSHOT", "APP_EXIT" -> AppRed
                        "FOCUS_LOST" -> AppAmber
                        "FOCUS_RESTORED" -> AppMint
                        else -> AppMuted
                    }
                    val logIcon = when (log.action) {
                        "SCREENSHOT" -> Icons.Default.Warning
                        "APP_EXIT" -> Icons.AutoMirrored.Filled.ExitToApp
                        "FOCUS_LOST" -> Icons.Default.VisibilityOff
                        "FOCUS_RESTORED" -> Icons.Default.Visibility
                        else -> Icons.Default.Info
                    }
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = AppSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(34.dp)
                                    .background(logColor.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(logIcon, null, tint = logColor, modifier = Modifier.size(16.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(log.username ?: "Không xác định", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    log.action + log.reason?.let { " - $it" }.orEmpty(),
                                    color = logColor,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Text(
                                log.createdAt?.replace("T", " ")?.take(16) ?: "",
                                color = AppMuted,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(ScreenBottomPadding))
        }
    }
}

@Composable
private fun LiveMonitoringScreenLegacy(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Giám sát trực tiếp", onBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item { SectionTitle("Thí sinh đang hoạt động") }
            items(MockData.candidates) { candidate ->
                val color = when (candidate.status) {
                    CandidateStatus.DOING -> AppBlue
                    CandidateStatus.LOST_CONNECTION -> AppAmber
                    CandidateStatus.SUBMITTED -> AppMint
                    CandidateStatus.FLAGGED -> AppRed
                }
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AvatarCircle(candidate.name, 38, color)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(candidate.name, fontWeight = FontWeight.Bold)
                                    Text(candidate.device, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            ChipText(candidate.status.name, color)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StyledProgress(candidate.progress / 100f, color, Modifier.weight(1f))
                            Spacer(Modifier.width(10.dp))
                            Text("${candidate.progress}%", color = color, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            item { SectionTitle("Realtime Log") }
            items(MockData.auditLogs) { log ->
                val logColor = when (log.action) { "SCREENSHOT", "APP_EXIT" -> AppRed; "LOST_CONNECTION", "FOCUS_LOST" -> AppAmber; else -> AppMuted }
                val logIcon = when (log.action) { "SCREENSHOT" -> Icons.Default.Warning; "APP_EXIT" -> Icons.AutoMirrored.Filled.ExitToApp; "LOST_CONNECTION" -> Icons.Default.WifiOff; "FOCUS_LOST" -> Icons.Default.VisibilityOff; else -> Icons.Default.Info }
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(36.dp)
                                .background(logColor.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(logIcon, null, tint = logColor, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(log.action, fontWeight = FontWeight.SemiBold, color = logColor)
                            Text(log.actor, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(log.time, color = AppMuted, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportDashboardScreen(onBack: () -> Unit) {
    var exams by remember { mutableStateOf<List<ExamResponse>>(emptyList()) }
    var selectedExam by remember { mutableStateOf<ExamResponse?>(null) }
    var report by remember { mutableStateOf<ExamReportResponse?>(null) }
    var loadingExams by remember { mutableStateOf(false) }
    var loadingReport by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            return@LaunchedEffect
        }

        loadingExams = true
        try {
            val response = ApiClient.getExams(authorization)
            exams = response.data.orEmpty()
            selectedExam = exams.firstOrNull()
            message = if (exams.isEmpty()) "Tạo đề thi trước để xem báo cáo." else null
        } catch (exception: HttpException) {
            val backendMessage = runCatching {
                val body = exception.response()?.errorBody()?.string().orEmpty()
                gson.fromJson(body, ApiResponse::class.java)?.message
            }.getOrNull().orEmpty()
            message = backendMessage.ifBlank { "Không thể tải đề thi lúc này." }
        } catch (exception: Exception) {
            message = "Không thể tải đề thi lúc này."
        } finally {
            loadingExams = false
        }
    }

    LaunchedEffect(selectedExam?.id) {
        val exam = selectedExam ?: return@LaunchedEffect
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            return@LaunchedEffect
        }

        loadingReport = true
        report = null
        try {
            val response = ApiClient.getExamReport(authorization, exam.id)
            report = response.data
            message = if (response.success) null else response.message
        } catch (exception: HttpException) {
            val backendMessage = runCatching {
                val body = exception.response()?.errorBody()?.string().orEmpty()
                gson.fromJson(body, ApiResponse::class.java)?.message
            }.getOrNull().orEmpty()
            message = backendMessage.ifBlank {
                if (exception.code() == 403) "Bạn không có quyền xem báo cáo." else "Không thể tải báo cáo lúc này."
            }
        } catch (exception: Exception) {
            message = "Không thể tải báo cáo lúc này."
        } finally {
            loadingReport = false
        }
    }

    AppBackground {
        ExamTopBar("Báo cáo", onBack)
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
                    Text("Báo cáo đề thi", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        selectedExam?.title ?: "Chọn đề thi để xem kết quả.",
                        color = AppMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            SectionTitle("Đề thi")
            CatalogDropdown(
                label = "Đề thi",
                value = selectedExam?.title ?: if (loadingExams) "Đang tải đề thi..." else "Chưa chọn đề thi",
                enabled = !loadingExams && exams.isNotEmpty(),
                items = exams,
                itemText = { "${it.code} - ${it.title}" },
                onSelect = {
                    selectedExam = it
                    message = null
                }
            )

            message?.let {
                Spacer(Modifier.height(12.dp))
                InfoBanner(it, AppAmber, Icons.Default.Info)
            }

            if (loadingReport) {
                Spacer(Modifier.height(12.dp))
                InfoBanner("Đang tải báo cáo...", AppBlue, Icons.Default.Info)
            }

            report?.let { data ->
        SectionTitle("Tổng quan")
                MetricCard("Kết quả", data.totalResults.toString(), "bản ghi", AppBlue, Icons.Default.Assessment)
                Spacer(Modifier.height(8.dp))
                MetricCard("Đã nộp", data.submittedCount.toString(), "đã nộp", AppMint, Icons.Default.CheckCircle)
                Spacer(Modifier.height(8.dp))
                MetricCard("Đang làm", data.doingCount.toString(), "đang tiến hành", AppAmber, Icons.Default.Info)
                Spacer(Modifier.height(8.dp))
                MetricCard("Trung bình", reportScore(data.averageScore), "điểm", AppViolet, Icons.Default.Assessment)
                Spacer(Modifier.height(8.dp))
                MetricCard("Cao nhất", reportScore(data.highestScore), "điểm", AppMint, Icons.Default.Assessment)
                Spacer(Modifier.height(8.dp))
                MetricCard("Thấp nhất", reportScore(data.lowestScore), "điểm", AppRed, Icons.Default.Assessment)

                SectionTitle("Kết quả thí sinh")
                val results = data.results.orEmpty()
                if (results.isEmpty()) {
                    InfoBanner("Chưa có kết quả.", AppAmber, Icons.Default.Info)
                } else {
                    results.forEach { result ->
                        ReportResultRow(result)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun ReportResultRow(result: ExamReportItemResponse) {
    val statusColor = reportStatusColor(result.status)
    val studentName = result.studentName?.takeIf { it.isNotBlank() }
        ?: result.username?.takeIf { it.isNotBlank() }
        ?: result.studentCode?.takeIf { it.isNotBlank() }
        ?: "Thí sinh #${result.studentId}"
    val studentIdentity = result.studentCode?.takeIf { it.isNotBlank() }
        ?: result.username?.takeIf { it.isNotBlank() }
        ?: "Người dùng #${result.studentId}"

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle(studentName, size = 42, color = statusColor)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(studentName, fontWeight = FontWeight.SemiBold, color = AppText)
                    Text(studentIdentity, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                }
                ChipText(reportStatusLabel(result.status), statusColor)
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = AppCardBorder)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("Điểm", color = AppMuted, style = MaterialTheme.typography.labelMedium)
                    Text(reportScore(result.score), fontWeight = FontWeight.Bold, color = AppText)
                }
                Column(Modifier.weight(1f)) {
                    Text("Đã nộp", color = AppMuted, style = MaterialTheme.typography.labelMedium)
                    Text(reportDate(result.submittedAt), color = AppText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

private fun reportScore(score: String?): String {
    return score?.takeIf { it.isNotBlank() } ?: "--"
}

private fun reportDate(value: String?): String {
    return value?.takeIf { it.isNotBlank() }?.replace("T", " ")?.take(16) ?: "Chưa nộp"
}

private fun reportStatusLabel(status: String): String {
    return when (status) {
        "SUBMITTED" -> "Đã nộp"
        "DOING" -> "Đang làm"
        "CANCELLED" -> "Đã hủy"
        else -> status
    }
}

private fun reportStatusColor(status: String) = when (status) {
    "SUBMITTED" -> AppMint
    "DOING" -> AppAmber
    "CANCELLED" -> AppRed
    else -> AppBlue
}

@Composable
private fun ReportDashboardScreenLegacy(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Báo cáo", onBack)
        GradientHero("Điểm trung bình 7.6", "Tổng quan hiệu suất qua tất cả đề thi")

        SectionTitle("Phân bố điểm")
        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    listOf("0-2" to 0.08f, "2-4" to 0.15f, "4-6" to 0.35f, "6-8" to 0.72f, "8-10" to 0.55f).forEach { (label, height) ->
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(height)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(Brush.verticalGradient(listOf(AppIndigo, AppBlue)))
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(label, style = MaterialTheme.typography.labelSmall, color = AppMuted)
                        }
                    }
                }
            }
        }

        SectionTitle("Kết quả cao nhất")
        MockData.results.forEach { result ->
            val scoreColor = when { result.score >= 8 -> AppMint; result.score >= 5 -> AppAmber; else -> AppRed }
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(44.dp)
                            .background(scoreColor.copy(alpha = 0.12f), CircleShape), contentAlignment = Alignment.Center) {
                        Text(result.score.toString(), fontWeight = FontWeight.Bold, color = scoreColor)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(result.exam, fontWeight = FontWeight.SemiBold)
                        Text("${result.correct} đúng · ${result.wrong} sai · ${result.blank} trống", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        PrimaryAction("Xuất Excel / PDF")
    }
}
