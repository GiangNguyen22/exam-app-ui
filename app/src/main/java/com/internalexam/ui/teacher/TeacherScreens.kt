package com.internalexam.ui.teacher

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.internalexam.data.ExamAttemptStore
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.ExamQuestionResponse
import com.internalexam.data.network.ExamResponse
import com.internalexam.data.network.QuestionResponse
import com.internalexam.ui.student.isExpired
import com.internalexam.ui.student.isOpenNow
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

@Composable
fun TeacherDashboardScreen(
    onCreateExam: () -> Unit,
    onAutoGenerateExam: () -> Unit,
    onQuestionBank: () -> Unit,
    onLiveMonitoring: () -> Unit,
    onReportDashboard: () -> Unit
) {
    AppBackground {
        Row(
            Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle("Giáo viên", 48, AppIndigo)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Quản lý học tập", style = MaterialTheme.typography.titleLarge)
                    Text("Bảng điều khiển giáo viên", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        GradientHero("Phân hệ Giảng dạy", "Tạo đề thi, quản lý ngân hàng câu hỏi, giám sát trực tiếp và xem báo cáo thống kê.")

        SectionTitle("Lối tắt nhanh")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Tạo đề thi", "Soạn đề thi mới", "Thêm câu hỏi hoặc tạo ngẫu nhiên", AppIndigo, Icons.Default.PostAdd, Modifier.clickable { onCreateExam() })
            MetricCard("Sinh đề tự động", "Tạo đề ngẫu nhiên", "Từ ngân hàng câu hỏi theo tỷ lệ", AppMint, Icons.Default.AutoAwesome, Modifier.clickable { onAutoGenerateExam() })
            MetricCard("Ngân hàng câu hỏi", "Quản lý câu hỏi", "Danh sách, tạo câu hỏi & nhập Excel", AppBlue, Icons.Default.Quiz, Modifier.clickable { onQuestionBank() })
            MetricCard("Giám sát thi", "Theo dõi trực tiếp", "Phát hiện chuyển tab & thiết bị", AppAmber, Icons.Default.Security, Modifier.clickable { onLiveMonitoring() })
            MetricCard("Thống kê & Báo cáo", "Báo cáo kết quả", "Phổ điểm, tỷ lệ đạt & xuất file", AppBlue, Icons.Default.Analytics, Modifier.clickable { onReportDashboard() })
        }
        Spacer(Modifier.height(ScreenBottomPadding))
    }
}

@Composable
fun TeacherExamListScreen(
    onCreateExam: () -> Unit,
    onAddQuestion: (ExamResponse) -> Unit,
    onViewQuestions: (ExamResponse) -> Unit,
    onEditExam: (ExamResponse) -> Unit,
    onBack: () -> Unit,
    viewModel: TeacherExamViewModel = viewModel()
) {
    val examsUiState by viewModel.examsState.collectAsState()
    val context = LocalContext.current

    var selectedTabPosition by remember { mutableIntStateOf(1) } // Default to Tab 1: "Lịch thi"
    var examToDelete by remember { mutableStateOf<ExamResponse?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadExams()
    }

    AppBackground {
        ExamTopBar("Danh sách đề thi", onBack)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf("Tất cả", "Lịch thi", "Đã kết thúc")
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTabPosition == index
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) AppIndigo else AppSurface),
                        modifier = Modifier
                            .border(1.dp, if (isSelected) AppIndigo else AppCardBorder, MaterialTheme.shapes.medium)
                            .clickable { selectedTabPosition = index }
                    ) {
                        Box(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                title,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else AppText,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Button(onClick = onCreateExam, colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Tạo đề")
            }
        }
        Spacer(Modifier.height(12.dp))

        when (val state = examsUiState) {
            is UiState.Loading -> LoadingStateCard("Đang tải danh sách đề thi...")
            is UiState.Error -> InfoBanner(state.message, AppAmber, Icons.Default.Info)
            is UiState.Success -> {
                val filteredExams = when (selectedTabPosition) {
                    1 -> state.data.filter { !it.isExpired() } // Lịch thi (Sắp & Đang diễn ra)
                    2 -> state.data.filter { it.isExpired() }  // Đã kết thúc
                    else -> state.data                        // Tất cả
                }

                if (filteredExams.isEmpty()) {
                    val emptyMsg = when (selectedTabPosition) {
                        1 -> "Chưa có đề thi nào trong Lịch thi (chưa diễn ra hoặc đang diễn ra)."
                        2 -> "Chưa có đề thi nào đã kết thúc."
                        else -> "Chưa có đề thi nào."
                    }
                    InfoBanner(emptyMsg, AppMuted, Icons.Default.Info)
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        filteredExams.forEach { exam ->
                            val isOpenNow = exam.isOpenNow()
                            val isExpired = exam.isExpired()

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
                                        Text(
                                            exam.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        val statusText = when {
                                            isOpenNow -> "Đang diễn ra"
                                            !isExpired -> "Sắp diễn ra"
                                            else -> "Đã kết thúc"
                                        }
                                        val statusColor = when {
                                            isOpenNow -> AppMint
                                            !isExpired -> AppIndigo
                                            else -> AppMuted
                                        }
                                        ChipText(statusText, statusColor)
                                    }

                                    Spacer(Modifier.height(4.dp))
                                    Text("Mã đề: ${exam.code} • ${exam.durationMinutes ?: 45} phút", color = AppMuted, style = MaterialTheme.typography.bodySmall)

                                    if (!exam.startTime.isNullOrBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Thời gian: ${com.internalexam.ui.student.formatExamTimeRange(exam.startTime, exam.endTime, exam.durationMinutes)}",
                                            color = AppMuted,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    Spacer(Modifier.height(10.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedButton(onClick = { onViewQuestions(exam) }) { Text("Câu hỏi") }
                                        OutlinedButton(onClick = { onAddQuestion(exam) }) { Text("+ Câu hỏi") }
                                        Button(onClick = { onEditExam(exam) }, colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)) { Text("Sửa") }
                                        OutlinedButton(
                                            onClick = { examToDelete = exam },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppRed)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Xóa", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(ScreenBottomPadding))
                    }
                }
            }
        }
    }

    examToDelete?.let { exam ->
        ConfirmActionDialog(
            title = "Xóa đề thi",
            message = "Bạn có chắc chắn muốn xóa đề thi '${exam.title}'? Hành động này không thể hoàn tác.",
            confirmText = "Xóa đề thi",
            isDestructive = true,
            loading = isDeleting,
            onConfirm = {
                isDeleting = true
                viewModel.deleteExam(exam.id) { success, msg ->
                    isDeleting = false
                    if (success) {
                        Toast.makeText(context, "Đã xóa đề thi thành công!", Toast.LENGTH_SHORT).show()
                        examToDelete = null
                    } else {
                        Toast.makeText(context, msg ?: "Xóa thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDismiss = { if (!isDeleting) examToDelete = null }
        )
    }
}

@Composable
fun QuestionBankScreen(
    onCreate: () -> Unit,
    onEdit: (QuestionResponse) -> Unit,
    onBack: () -> Unit,
    viewModel: QuestionBankViewModel = viewModel()
) {
    val questionsUiState by viewModel.questionsState.collectAsState()
    val subjects by viewModel.subjectsState.collectAsState()

    var selectedSubjectId by remember { mutableLongStateOf(-1L) }

    LaunchedEffect(Unit) {
        viewModel.loadQuestions()
        viewModel.loadSubjects()
    }

    AppBackground {
        ExamTopBar("Ngân hàng câu hỏi", onBack)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val count = (questionsUiState as? UiState.Success)?.data?.size ?: 0
            ChipText("$count câu hỏi", AppIndigo)
            Button(onClick = onCreate, colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Tạo câu hỏi")
            }
        }
        Spacer(Modifier.height(12.dp))

        // Subject filter catalog
        if (subjects.isNotEmpty()) {
            CatalogDropdown(
                label = "Lọc theo môn học",
                placeholder = "Tất cả môn học",
                loading = false,
                items = subjects,
                itemLabel = { it.name },
                onSelect = { selectedSubjectId = it.id },
                selectedLabel = subjects.find { it.id == selectedSubjectId }?.name ?: "Tất cả môn học"
            )
            Spacer(Modifier.height(10.dp))
        }

        when (val state = questionsUiState) {
            is UiState.Loading -> LoadingStateCard("Đang tải ngân hàng câu hỏi...")
            is UiState.Error -> InfoBanner(state.message, AppAmber, Icons.Default.Info)
            is UiState.Success -> {
                val filteredQuestions = if (selectedSubjectId > 0) {
                    state.data.filter { it.subjectId == selectedSubjectId }
                } else {
                    state.data
                }

                if (filteredQuestions.isEmpty()) {
                    InfoBanner("Chưa có câu hỏi nào trong ngân hàng.", AppMuted, Icons.Default.Info)
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        filteredQuestions.forEachIndexed { i, q ->
                            Card(
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = AppSurface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                                    .clickable { onEdit(q) }
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    // Top Metadata Row: Subject, Difficulty & Type
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            q.subjectName?.takeIf { it.isNotBlank() }?.let { subName ->
                                                ChipText(subName, AppIndigo)
                                            }
                                            q.topicName?.takeIf { it.isNotBlank() }?.let { topName ->
                                                ChipText(topName, AppBlue)
                                            }
                                        }
                                        val diffColor = when (q.difficulty?.uppercase()) {
                                            "EASY" -> AppMint
                                            "HARD" -> AppRed
                                            else -> AppAmber
                                        }
                                        ChipText(q.type ?: "SINGLE", diffColor)
                                    }

                                    Spacer(Modifier.height(10.dp))

                                    // Question Content Text
                                    Text(
                                        text = "Câu ${i + 1}: ${q.content}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = AppText
                                    )

                                    // Image Illustration
                                    q.imageUrl?.takeIf { it.isNotBlank() }?.let { imgPath ->
                                        Spacer(Modifier.height(10.dp))
                                        AsyncImage(
                                            model = if (imgPath.startsWith("http")) imgPath else "http://192.168.119.146:8080$imgPath",
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 200.dp)
                                                .clip(MaterialTheme.shapes.medium),
                                            contentScale = ContentScale.Fit
                                        )
                                    }

                                    // Answer Options
                                    q.answers?.takeIf { it.isNotEmpty() }?.let { answersList ->
                                        Spacer(Modifier.height(12.dp))
                                        HorizontalDivider(color = AppCardBorder)
                                        Spacer(Modifier.height(8.dp))

                                        answersList.forEachIndexed { aIdx, answerOpt ->
                                            val isCorrect = answerOpt.correct == true
                                            val optionBg = if (isCorrect) AppMint.copy(alpha = 0.08f) else Color.Transparent
                                            val optionBorder = if (isCorrect) AppMint.copy(alpha = 0.3f) else Color.Transparent

                                            Surface(
                                                color = optionBg,
                                                shape = MaterialTheme.shapes.small,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 2.dp)
                                                    .border(1.dp, optionBorder, MaterialTheme.shapes.small)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "${'A' + aIdx}. ${answerOpt.content}",
                                                        color = if (isCorrect) AppMint else AppText,
                                                        fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    if (isCorrect) {
                                                        ChipText("✓ Đúng", AppMint)
                                                    }
                                                }
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
        }
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
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(exam?.id) {
        val auth = SessionManager.authorizationHeader()
        if (auth == null || exam == null) {
            errorMessage = "Không tìm thấy thông tin đề thi"
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val res = ApiClient.getExamQuestions(auth, exam.id)
            questions = res.data.orEmpty()
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Không thể tải danh sách câu hỏi của đề thi"
        } finally {
            isLoading = false
        }
    }

    AppBackground {
        ExamTopBar(exam?.title ?: "Danh sách câu hỏi đề thi", onBack)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChipText("${questions.size} câu hỏi", AppIndigo)
            Button(
                onClick = onAddQuestion,
                colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Thêm câu hỏi")
            }
        }
        Spacer(Modifier.height(12.dp))

        if (isLoading) {
            LoadingStateCard("Đang tải danh sách câu hỏi đề thi...")
        } else if (errorMessage != null) {
            InfoBanner(errorMessage.orEmpty(), AppAmber, Icons.Default.Info)
        } else if (questions.isEmpty()) {
            InfoBanner("Đề thi này chưa có câu hỏi nào. Hãy nhấn 'Thêm câu hỏi' để bổ sung.", AppMuted, Icons.Default.Info)
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                questions.forEachIndexed { i, q ->
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = AppSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                            .clickable { onEditQuestion(q) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Câu ${q.orderIndex?.takeIf { it > 0 } ?: (i + 1)}", fontWeight = FontWeight.Bold, color = AppIndigo)
                                q.score?.let { scoreVal ->
                                    ChipText("$scoreVal điểm", AppMint)
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(q.content, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)

                            q.answers?.takeIf { it.isNotEmpty() }?.let { answers ->
                                Spacer(Modifier.height(10.dp))
                                answers.forEachIndexed { aIdx, ans ->
                                    val isCorrect = ans.correct == true
                                    Text(
                                        "${'A' + aIdx}. ${ans.content} ${if (isCorrect) "✓" else ""}",
                                        color = if (isCorrect) AppMint else AppText,
                                        fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(ScreenBottomPadding))
            }
        }
    }
}

@Composable
fun CreateQuestionScreen(
    viewModel: QuestionBankViewModel = viewModel(),
    onBack: () -> Unit
) {
    val subjects by viewModel.subjectsState.collectAsState()
    val topics by viewModel.topicsState.collectAsState()

    var selectedSubjectId by remember { mutableLongStateOf(-1L) }
    var selectedTopicId by remember { mutableStateOf<Long?>(null) }
    var content by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("SINGLE") } // SINGLE, MULTI, FILL_BLANK
    var difficulty by remember { mutableStateOf("MEDIUM") } // EASY, MEDIUM, HARD
    var imageUrl by remember { mutableStateOf("") }

    // Answer options
    var answerContents by remember { mutableStateOf(listOf("Đáp án A", "Đáp án B", "Đáp án C", "Đáp án D")) }
    var correctIndices by remember { mutableStateOf(setOf(0)) }

    var showQuickAddSubject by remember { mutableStateOf(false) }
    var showQuickAddTopic by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val examContext = ExamAttemptStore.selectedExam.value

    LaunchedEffect(Unit) {
        viewModel.loadSubjects()
    }

    LaunchedEffect(selectedSubjectId) {
        if (selectedSubjectId > 0) {
            viewModel.loadTopicsForSubject(selectedSubjectId)
        }
    }

    AppBackground {
        ExamTopBar(
            if (examContext != null) "Thêm câu hỏi cho: ${examContext.title}" else "Soạn câu hỏi mới",
            onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (errorMessage != null) {
                InfoBanner(errorMessage.orEmpty(), AppRed, Icons.Default.Warning)
            }

            // Subject catalog selector
            CatalogDropdown(
                label = "Môn học *",
                placeholder = "Chọn môn học",
                loading = false,
                items = subjects,
                itemLabel = { it.name },
                onSelect = {
                    selectedSubjectId = it.id
                    selectedTopicId = null
                },
                selectedLabel = subjects.find { it.id == selectedSubjectId }?.name,
                onQuickAdd = { showQuickAddSubject = true },
                quickAddLabel = "Thêm môn"
            )

            // Topic catalog selector
            if (selectedSubjectId > 0) {
                CatalogDropdown(
                    label = "Chủ đề / Bài học",
                    placeholder = "Chọn chủ đề (tùy chọn)",
                    loading = false,
                    items = topics,
                    itemLabel = { it.name },
                    onSelect = { selectedTopicId = it.id },
                    selectedLabel = topics.find { it.id == selectedTopicId }?.name,
                    onQuickAdd = { showQuickAddTopic = true },
                    quickAddLabel = "Thêm chủ đề"
                )
            }

            // Question Content
            OutlinedTextField(
                value = content,
                onValueChange = { content = it; errorMessage = null },
                label = { Text("Nội dung câu hỏi *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                shape = MaterialTheme.shapes.medium
            )

            // Question Type
            Text("Loại câu hỏi", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("SINGLE" to "1 đáp án", "MULTI" to "Nhiều đáp án", "FILL_BLANK" to "Điền từ").forEach { (code, label) ->
                    val isSelected = type == code
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) AppIndigo else AppSurface),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, AppCardBorder, MaterialTheme.shapes.medium)
                            .clickable {
                                type = code
                                if (code == "SINGLE" && correctIndices.size > 1) {
                                    correctIndices = setOf(correctIndices.firstOrNull() ?: 0)
                                }
                            }
                    ) {
                        Box(Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                            Text(
                                label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else AppText,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Difficulty
            Text("Mức độ khó", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("EASY" to "Dễ", "MEDIUM" to "Trung bình", "HARD" to "Khó").forEach { (code, label) ->
                    val isSelected = difficulty == code
                    val color = when (code) {
                        "EASY" -> AppMint
                        "HARD" -> AppRed
                        else -> AppAmber
                    }
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) color else AppSurface),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, AppCardBorder, MaterialTheme.shapes.medium)
                            .clickable { difficulty = code }
                    ) {
                        Box(Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                            Text(
                                label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else AppText,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Image URL (optional)
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL hình ảnh minh họa (tùy chọn)") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            // Answer Options (for SINGLE and MULTI)
            if (type != "FILL_BLANK") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Danh sách đáp án", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    TextButton(onClick = {
                        answerContents = answerContents + "Đáp án ${('A' + answerContents.size)}"
                    }) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Thêm đáp án", style = MaterialTheme.typography.labelSmall)
                    }
                }

                answerContents.forEachIndexed { index, ansText ->
                    val isCorrect = index in correctIndices
                    AnswerOptionInput(
                        label = "Đáp án ${'A' + index}",
                        value = ansText,
                        onValueChange = { newText ->
                            answerContents = answerContents.toMutableList().apply { set(index, newText) }
                        },
                        isCorrect = isCorrect,
                        isMultiChoice = type == "MULTI",
                        canRemove = answerContents.size > 2,
                        onToggleCorrect = {
                            if (type == "SINGLE") {
                                correctIndices = setOf(index)
                            } else {
                                val newSet = correctIndices.toMutableSet()
                                if (isCorrect) newSet.remove(index) else newSet.add(index)
                                correctIndices = newSet
                            }
                        },
                        onRemove = {
                            if (answerContents.size > 2) {
                                answerContents = answerContents.toMutableList().apply { removeAt(index) }
                                correctIndices = correctIndices.mapNotNull {
                                    when {
                                        it < index -> it
                                        it > index -> it - 1
                                        else -> null
                                    }
                                }.toSet().ifEmpty { setOf(0) }
                            }
                        }
                    )
                }
            } else {
                OutlinedTextField(
                    value = answerContents.firstOrNull().orEmpty(),
                    onValueChange = { answerContents = listOf(it) },
                    label = { Text("Đáp án đúng chuẩn *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            }

            Spacer(Modifier.height(12.dp))
            PrimaryAction(
                text = if (isSubmitting) "Đang lưu..." else "Lưu câu hỏi",
                enabled = !isSubmitting && content.isNotBlank() && selectedSubjectId > 0,
                onClick = {
                    if (selectedSubjectId <= 0) {
                        errorMessage = "Vui lòng chọn môn học"
                        return@PrimaryAction
                    }
                    if (content.isBlank()) {
                        errorMessage = "Vui lòng nhập nội dung câu hỏi"
                        return@PrimaryAction
                    }
                    if (type != "FILL_BLANK" && correctIndices.isEmpty()) {
                        errorMessage = "Vui lòng chọn ít nhất 1 đáp án đúng"
                        return@PrimaryAction
                    }

                    isSubmitting = true
                    errorMessage = null

                    val answersList = if (type == "FILL_BLANK") {
                        listOf(com.internalexam.data.network.AnswerCreateRequest(content = answerContents.firstOrNull().orEmpty(), correct = true, explanation = null))
                    } else {
                        answerContents.mapIndexed { idx, textVal ->
                            com.internalexam.data.network.AnswerCreateRequest(
                                content = textVal,
                                correct = idx in correctIndices,
                                explanation = null
                            )
                        }
                    }

                    val req = com.internalexam.data.network.QuestionCreateRequest(
                        subjectId = selectedSubjectId,
                        topicId = selectedTopicId,
                        content = content.trim(),
                        type = type,
                        difficulty = difficulty,
                        imageUrl = imageUrl.trim().ifBlank { null },
                        answers = answersList
                    )

                    viewModel.createQuestion(req, examContext?.id) { success, msg ->
                        isSubmitting = false
                        if (success) {
                            onBack()
                        } else {
                            errorMessage = msg ?: "Tạo câu hỏi thất bại"
                        }
                    }
                }
            )
            Spacer(Modifier.height(ScreenBottomPadding))
        }
    }

    if (showQuickAddSubject) {
        QuickAddDialog(
            title = "Thêm môn học mới",
            label = "Tên môn học",
            onConfirm = { name ->
                viewModel.createSubject(name) { newSub ->
                    if (newSub != null) selectedSubjectId = newSub.id
                    showQuickAddSubject = false
                }
            },
            onDismiss = { showQuickAddSubject = false }
        )
    }

    if (showQuickAddTopic && selectedSubjectId > 0) {
        QuickAddDialog(
            title = "Thêm chủ đề mới",
            label = "Tên chủ đề",
            onConfirm = { name ->
                viewModel.createTopic(selectedSubjectId, name) { newTop ->
                    if (newTop != null) selectedTopicId = newTop.id
                    showQuickAddTopic = false
                }
            },
            onDismiss = { showQuickAddTopic = false }
        )
    }
}

@Composable
fun EditQuestionScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Chỉnh sửa câu hỏi", onBack)
        InfoBanner("Cập nhật nội dung câu hỏi hoặc đáp án.", AppIndigo, Icons.Default.Info)
        Spacer(Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)) {
            Text("Cập nhật")
        }
        Spacer(Modifier.height(ScreenBottomPadding))
    }
}

@Composable
fun CreateExamScreen(
    onGenerateExam: () -> Unit,
    onBack: () -> Unit,
    viewModel: TeacherExamViewModel = viewModel()
) {
    val groups by viewModel.groupsState.collectAsState()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("45") }
    var scorePerQuestionText by remember { mutableStateOf("1.0") }

    var startDateMillis by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var startHour by remember { mutableIntStateOf(8) }
    var startMinute by remember { mutableIntStateOf(0) }

    var endDateMillis by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var endHour by remember { mutableIntStateOf(17) }
    var endMinute by remember { mutableIntStateOf(0) }

    var selectedGroupIds by remember { mutableStateOf(setOf<Long>()) }
    var shuffleQuestions by remember { mutableStateOf(true) }
    var shuffleAnswers by remember { mutableStateOf(true) }
    var showAnswersAfterSubmit by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val excelLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Toast.makeText(context, "Đã chọn file Excel: ${uri.lastPathSegment}", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadCatalogs()
    }

    AppBackground {
        ExamTopBar("Tạo đề thi mới", onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (errorMessage != null) {
                InfoBanner(errorMessage.orEmpty(), AppRed, Icons.Default.Warning)
            }

            // Quick Auto Generate Button
            OutlinedButton(
                onClick = onGenerateExam,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.AutoAwesome, null, tint = AppMint, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sinh đề thi tự động từ ngân hàng câu hỏi")
            }

            // Exam Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; errorMessage = null },
                label = { Text("Tên / Tiêu đề đề thi *") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            // Duration & Score per question
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Thời gian (phút) *") },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )
                OutlinedTextField(
                    value = scorePerQuestionText,
                    onValueChange = { scorePerQuestionText = it },
                    label = { Text("Điểm / câu *") },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )
            }

            // Start DateTime
            DateTimeField(
                dateMillis = startDateMillis,
                hour = startHour,
                minute = startMinute,
                label = "Thời gian mở ca thi",
                onPickDate = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(context, { _, y, m, d ->
                        cal.set(y, m, d)
                        startDateMillis = cal.timeInMillis
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                },
                onPickTime = {
                    TimePickerDialog(context, { _, h, min ->
                        startHour = h
                        startMinute = min
                    }, startHour, startMinute, true).show()
                }
            )

            // End DateTime
            DateTimeField(
                dateMillis = endDateMillis,
                hour = endHour,
                minute = endMinute,
                label = "Thời gian đóng ca thi",
                onPickDate = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(context, { _, y, m, d ->
                        cal.set(y, m, d)
                        endDateMillis = cal.timeInMillis
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                },
                onPickTime = {
                    TimePickerDialog(context, { _, h, min ->
                        endHour = h
                        endMinute = min
                    }, endHour, endMinute, true).show()
                }
            )

            // Student Group Selector
            GroupSelector(
                groups = groups,
                loading = false,
                selectedGroupIds = selectedGroupIds,
                onSelectionChanged = { selectedGroupIds = it }
            )

            // Options Toggles
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.medium)
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Trộn ngẫu nhiên câu hỏi")
                        Switch(checked = shuffleQuestions, onCheckedChange = { shuffleQuestions = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Trộn ngẫu nhiên đáp án")
                        Switch(checked = shuffleAnswers, onCheckedChange = { shuffleAnswers = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Xem đáp án sau khi nộp bài")
                        Switch(checked = showAnswersAfterSubmit, onCheckedChange = { showAnswersAfterSubmit = it })
                    }
                }
            }

            // Excel Template Import Button
            OutlinedButton(
                onClick = { excelLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.UploadFile, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Nhập đề thi từ file Excel")
            }

            Spacer(Modifier.height(12.dp))
            PrimaryAction(
                text = if (isSubmitting) "Đang tạo..." else "Tạo đề thi",
                enabled = !isSubmitting && title.isNotBlank(),
                onClick = {
                    val duration = durationText.toIntOrNull() ?: 45
                    val startTimeFormatted = formatDateTime(startDateMillis, startHour, startMinute)
                    val endTimeFormatted = formatDateTime(endDateMillis, endHour, endMinute)

                    isSubmitting = true
                    errorMessage = null

                    val req = com.internalexam.data.network.ExamCreateRequest(
                        title = title.trim(),
                        durationMinutes = duration,
                        scorePerQuestion = scorePerQuestionText.trim().ifBlank { "1.0" },
                        startTime = startTimeFormatted,
                        endTime = endTimeFormatted,
                        shuffleQuestions = shuffleQuestions,
                        shuffleAnswers = shuffleAnswers,
                        showAnswersAfterSubmit = showAnswersAfterSubmit,
                        groupIds = selectedGroupIds.toList().ifEmpty { null }
                    )

                    viewModel.createExam(req) { success, msg ->
                        isSubmitting = false
                        if (success) {
                            Toast.makeText(context, "Tạo đề thi thành công!", Toast.LENGTH_SHORT).show()
                            onBack()
                        } else {
                            errorMessage = msg ?: "Tạo đề thi thất bại"
                        }
                    }
                }
            )
            Spacer(Modifier.height(ScreenBottomPadding))
        }
    }
}

@Composable
fun EditExamScreen(
    viewModel: TeacherExamViewModel = viewModel(),
    onBack: () -> Unit
) {
    val exam = ExamAttemptStore.selectedExam.value
    val groups by viewModel.groupsState.collectAsState()
    val context = LocalContext.current

    var title by remember(exam) { mutableStateOf(exam?.title.orEmpty()) }
    var durationText by remember(exam) { mutableStateOf(exam?.durationMinutes?.toString() ?: "45") }
    var scorePerQuestionText by remember(exam) { mutableStateOf(exam?.scorePerQuestion ?: "1.0") }

    val startDt = remember(exam) { exam?.startTime.parseExamDateTime() }
    var startDateMillis by remember(exam) { mutableStateOf(startDt?.dateMillis ?: System.currentTimeMillis()) }
    var startHour by remember(exam) { mutableIntStateOf(startDt?.hour ?: 8) }
    var startMinute by remember(exam) { mutableIntStateOf(startDt?.minute ?: 0) }

    val endDt = remember(exam) { exam?.endTime.parseExamDateTime() }
    var endDateMillis by remember(exam) { mutableStateOf(endDt?.dateMillis ?: System.currentTimeMillis()) }
    var endHour by remember(exam) { mutableIntStateOf(endDt?.hour ?: 17) }
    var endMinute by remember(exam) { mutableIntStateOf(endDt?.minute ?: 0) }

    var selectedGroupIds by remember(exam) { mutableStateOf(exam?.groupIds.orEmpty().toSet()) }
    var shuffleQuestions by remember(exam) { mutableStateOf(exam?.shuffleQuestions ?: true) }
    var shuffleAnswers by remember(exam) { mutableStateOf(exam?.shuffleAnswers ?: true) }
    var showAnswersAfterSubmit by remember(exam) { mutableStateOf(exam?.showAnswersAfterSubmit ?: false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCatalogs()
    }

    AppBackground {
        ExamTopBar("Chỉnh sửa đề thi", onBack)

        if (exam == null) {
            InfoBanner("Không tìm thấy thông tin đề thi cần chỉnh sửa.", AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(16.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Quay lại") }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (errorMessage != null) {
                    InfoBanner(errorMessage.orEmpty(), AppRed, Icons.Default.Warning)
                }

                // Exam Code
                ChipText("Mã đề thi: ${exam.code}", AppIndigo)

                // Exam Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorMessage = null },
                    label = { Text("Tên / Tiêu đề đề thi *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                // Duration & Score per question
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Thời gian (phút) *") },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    )
                    OutlinedTextField(
                        value = scorePerQuestionText,
                        onValueChange = { scorePerQuestionText = it },
                        label = { Text("Điểm / câu *") },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    )
                }

                // Start DateTime
                DateTimeField(
                    dateMillis = startDateMillis,
                    hour = startHour,
                    minute = startMinute,
                    label = "Thời gian mở ca thi",
                    onPickDate = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(context, { _, y, m, d ->
                            cal.set(y, m, d)
                            startDateMillis = cal.timeInMillis
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                    },
                    onPickTime = {
                        TimePickerDialog(context, { _, h, min ->
                            startHour = h
                            startMinute = min
                        }, startHour, startMinute, true).show()
                    }
                )

                // End DateTime
                DateTimeField(
                    dateMillis = endDateMillis,
                    hour = endHour,
                    minute = endMinute,
                    label = "Thời gian đóng ca thi",
                    onPickDate = {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(context, { _, y, m, d ->
                            cal.set(y, m, d)
                            endDateMillis = cal.timeInMillis
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                    },
                    onPickTime = {
                        TimePickerDialog(context, { _, h, min ->
                            endHour = h
                            endMinute = min
                        }, endHour, endMinute, true).show()
                    }
                )

                // Student Group Selector
                GroupSelector(
                    groups = groups,
                    loading = false,
                    selectedGroupIds = selectedGroupIds,
                    onSelectionChanged = { selectedGroupIds = it }
                )

                // Toggles
                Card(
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.medium)
                ) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Trộn ngẫu nhiên câu hỏi")
                            Switch(checked = shuffleQuestions, onCheckedChange = { shuffleQuestions = it })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Trộn ngẫu nhiên đáp án")
                            Switch(checked = shuffleAnswers, onCheckedChange = { shuffleAnswers = it })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Xem đáp án sau khi nộp bài")
                            Switch(checked = showAnswersAfterSubmit, onCheckedChange = { showAnswersAfterSubmit = it })
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                PrimaryAction(
                    text = if (isSubmitting) "Đang lưu..." else "Cập nhật đề thi",
                    enabled = !isSubmitting && title.isNotBlank(),
                    onClick = {
                        val duration = durationText.toIntOrNull() ?: 45
                        val startTimeFormatted = formatDateTime(startDateMillis, startHour, startMinute)
                        val endTimeFormatted = formatDateTime(endDateMillis, endHour, endMinute)

                        isSubmitting = true
                        errorMessage = null

                        val req = com.internalexam.data.network.ExamUpdateRequest(
                            title = title.trim(),
                            durationMinutes = duration,
                            scorePerQuestion = scorePerQuestionText.trim().ifBlank { "1.0" },
                            startTime = startTimeFormatted,
                            endTime = endTimeFormatted,
                            shuffleQuestions = shuffleQuestions,
                            shuffleAnswers = shuffleAnswers,
                            showAnswersAfterSubmit = showAnswersAfterSubmit,
                            groupIds = selectedGroupIds.toList().ifEmpty { null }
                        )

                        viewModel.updateExam(exam.id, req) { success, msg ->
                            isSubmitting = false
                            if (success) {
                                Toast.makeText(context, "Cập nhật đề thi thành công!", Toast.LENGTH_SHORT).show()
                                onBack()
                            } else {
                                errorMessage = msg ?: "Cập nhật đề thi thất bại"
                            }
                        }
                    }
                )
                Spacer(Modifier.height(ScreenBottomPadding))
            }
        }
    }
}

@Composable
fun AutoGenerateExamScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Sinh đề thi tự động", onBack)
        InfoBanner("Tự động chọn ngẫu nhiên câu hỏi theo môn học và cấu hình độ khó.", AppIndigo, Icons.Default.Info)
        Spacer(Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = AppMint)) {
            Text("Bắt đầu sinh đề")
        }
        Spacer(Modifier.height(ScreenBottomPadding))
    }
}

@Composable
fun LiveMonitoringScreen(
    viewModel: LiveMonitoringViewModel = viewModel(),
    onBack: () -> Unit
) {
    val events by viewModel.eventsState.collectAsState()
    val examId = ExamAttemptStore.backendExamId

    LaunchedEffect(examId) {
        if (examId > 0) {
            viewModel.startMonitoring(examId)
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopMonitoring() }
    }

    AppBackground {
        ExamTopBar("Giám sát thi trực tiếp", onBack)
        SectionTitle("Tổng quan giám sát")
        MetricCard("Số sự kiện vi phạm", "${events.size}", "Phát hiện chuyển tab/thiết bị", AppAmber, Icons.Default.Warning)

        Spacer(Modifier.height(12.dp))
        SectionTitle("Nhật ký vi phạm")
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (events.isEmpty()) {
                InfoBanner("Chưa phát hiện vi phạm nào.", AppMint, Icons.Default.Info)
            } else {
                events.forEach { evt ->
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = AppSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppCardBorder, MaterialTheme.shapes.medium)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text("Thí sinh: ${evt.studentName ?: evt.username ?: "Học sinh"}", fontWeight = FontWeight.Bold, color = AppText)
                            Text("Hành vi: ${evt.eventType}", color = AppRed, fontWeight = FontWeight.SemiBold)
                            evt.details?.let { Text(it, color = AppMuted, style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
            Spacer(Modifier.height(ScreenBottomPadding))
        }
    }
}

@Composable
fun ReportDashboardScreen(
    viewModel: TeacherReportViewModel = viewModel(),
    onBack: () -> Unit
) {
    val reportUiState by viewModel.reportState.collectAsState()
    val examId = ExamAttemptStore.backendExamId

    LaunchedEffect(examId) {
        if (examId > 0) {
            viewModel.loadReport(examId)
        }
    }

    AppBackground {
        ExamTopBar("Thống kê & Báo cáo", onBack)

        when (val state = reportUiState) {
            is UiState.Loading -> LoadingStateCard("Đang tải báo cáo...")
            is UiState.Error -> InfoBanner(state.message, AppAmber, Icons.Default.Info)
            is UiState.Success -> {
                val r = state.data
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard("Tổng bài thi", "${r.submittedCount}", "Thí sinh đã nộp", AppIndigo, Icons.Default.Group)
                    MetricCard("Điểm trung bình", r.averageScore ?: "--", "Điểm số trung bình cả lớp", AppBlue, Icons.Default.Analytics)
                    MetricCard("Điểm cao nhất", r.highestScore ?: "--", "Thành tích cao nhất", AppMint, Icons.Default.Check)

                    SectionTitle("Danh sách bài nộp")
                    r.results.orEmpty().forEach { item ->
                        ReportResultRow(item)
                    }
                    Spacer(Modifier.height(ScreenBottomPadding))
                }
            }
        }
    }
}
