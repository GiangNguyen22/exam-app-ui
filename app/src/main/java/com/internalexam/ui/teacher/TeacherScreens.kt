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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionAnswer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.internalexam.data.SessionManager
import com.internalexam.data.ExamAttemptStore
import com.internalexam.data.examimport.ExamExcelImportService
import com.internalexam.data.examimport.ExamExcelParser
import com.internalexam.data.examimport.ExamExcelQuestionRow
import com.internalexam.data.examimport.ExamExcelTemplate
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.AnswerCreateRequest
import com.internalexam.data.network.ExamCreateRequest
import com.internalexam.data.network.ExamGenerateRequest
import com.internalexam.data.network.ExamReportItemResponse
import com.internalexam.data.network.ExamReportResponse
import com.internalexam.data.network.ExamQuestionCreateRequest
import com.internalexam.data.network.ExamQuestionResponse
import com.internalexam.data.network.ExamResponse
import com.internalexam.data.network.ExamUpdateRequest
import com.internalexam.data.network.QuestionResponse
import com.internalexam.data.network.SubjectResponse
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
import kotlinx.coroutines.Dispatchers
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
    val selectedExam = ExamAttemptStore.selectedExam.value

    LaunchedEffect(selectedExam) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Please sign in again."
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val response = ApiClient.getExams(authorization)
            exams = response.data.orEmpty()
            message = if (exams.isEmpty()) "No exams found. Create an exam first." else null
        } catch (exception: Exception) {
            message = "Cannot load exams right now."
        } finally {
            isLoading = false
        }
    }

    AppBackground {
        ExamTopBar("Exams", onBack)
        SectionTitle("Exam List", "Choose an exam to add questions")
        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(12.dp))
        }
        if (isLoading) {
            LoadingStateCard("Loading exams...")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 110.dp)) {
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
                            Text("Code ${exam.code} - ID ${exam.id}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(onClick = { onViewQuestions(exam) }, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Questions")
                                }
                                OutlinedButton(onClick = { onAddQuestion(exam) }, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Add")
                                }
                                OutlinedButton(onClick = { onEditExam(exam) }, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Edit")
                                }
                            }
                        }
                    }
                }
            }
        }
        PrimaryAction("Create Exam", onClick = onCreateExam)
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
            message = "Select an exam first."
            isLoading = false
            return@LaunchedEffect
        }
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Please sign in again."
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val response = ApiClient.getExamQuestions(authorization, selectedExam.id)
            questions = response.data.orEmpty()
            ExamAttemptStore.setBackendQuestions(questions)
            message = if (questions.isEmpty()) "No questions yet. Add the first question." else null
        } catch (exception: Exception) {
            message = "Cannot load questions right now."
        } finally {
            isLoading = false
        }
    }

    AppBackground {
        ExamTopBar("Exam Questions", onBack)
        SectionTitle(exam?.title ?: "Questions", exam?.let { "Code ${it.code}" })
        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(12.dp))
        }
        if (isLoading) {
            LoadingStateCard("Loading questions...")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 110.dp)) {
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
                            val marker = if (answer.correct == true) "Correct: " else ""
                            Text("$label. $marker${answer.content}", color = if (answer.correct == true) AppMint else AppMuted, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { onEditQuestion(question) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Edit Question")
                        }
                    }
                }
            }
        }
        }
        PrimaryAction("Add Question", onClick = onAddQuestion)
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
            message = "Please sign in again."
            return@LaunchedEffect
        }
        try {
            examCount = ApiClient.getExams(authorization).data.orEmpty().size
            questionCount = ApiClient.getQuestions(authorization).data.orEmpty().size
        } catch (exception: Exception) {
            message = "Cannot load dashboard data right now."
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
        GradientHero("Teacher Dashboard", "Manage exams, questions & monitor integrity") {
            StatusPill(NetworkState.ONLINE)
        }

        SectionTitle("Overview")
        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(10.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Exams", examCount?.toString() ?: "--", "Available exams", AppBlue, Icons.Default.Assessment)
                MetricCard("Alerts", "--", "No alerts", AppMuted, Icons.Default.Warning)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Questions", questionCount?.toString() ?: "--", "Question bank", AppViolet, Icons.Default.QuestionAnswer)
                MetricCard("Sync", "Live", "Ready", AppMint, Icons.Default.CloudDone)
            }
        }

        SectionTitle("Quick Actions")
        val actions = listOf(
            Triple("Questions", Icons.Default.QuestionAnswer, openQuestions),
            Triple("Create Exam", Icons.Default.Add, openCreateExam),
            Triple("Auto Generate", Icons.Default.AutoAwesome, openGenerate),
            Triple("Monitoring", Icons.Default.Visibility, openMonitor),
            Triple("Reports", Icons.Default.Assessment, openReports),
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

@Composable
fun QuestionBankScreen(onCreate: () -> Unit, onBack: () -> Unit) {
    var questions by remember { mutableStateOf<List<QuestionResponse>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var importLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }

    fun loadQuestions() {
        scope.launch {
            val authorization = SessionManager.authorizationHeader()
            if (authorization == null) {
                message = "Please sign in again."
                return@launch
            }
            try {
                val response = ApiClient.getQuestions(authorization)
                questions = response.data.orEmpty()
                message = if (questions.isEmpty()) "No questions yet." else null
            } catch (exception: Exception) {
                message = "Cannot load questions right now."
            }
        }
    }

    fun importQuestionsFromExcel(uri: android.net.Uri) {
        scope.launch {
            val authorization = SessionManager.authorizationHeader()
            if (authorization == null) {
                message = "Please sign in again."
                return@launch
            }

            importLoading = true
            message = null
            try {
                val fileBytes = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                } ?: run {
                    message = "Cannot open the selected Excel file."
                    return@launch
                }

                val response = ApiClient.importQuestions(authorization, "questions.xlsx", fileBytes)
                val result = response.data
                if (response.success && result != null && result.success) {
                    message = "Imported ${result.importedQuestions} question(s) from Excel."
                    loadQuestions()
                } else if (result != null) {
                    val sampleFailures = result.errors.orEmpty().take(3).joinToString("; ") {
                        "row ${it.rowNumber}: ${it.message}"
                    }
                    val moreFailures = if ((result.errors?.size ?: 0) > 3) " +${(result.errors?.size ?: 0) - 3} more" else ""
                    message = if (sampleFailures.isBlank()) {
                        "Import failed. Check the Excel template."
                    } else {
                        "Import failed: $sampleFailures$moreFailures"
                    }
                } else {
                    message = response.message.ifBlank { "Import failed" }
                }
            } catch (exception: Exception) {
                message = "Cannot import the selected Excel file."
            } finally {
                importLoading = false
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) importQuestionsFromExcel(uri)
    }

    LaunchedEffect(Unit) {
        loadQuestions()
    }

    val filteredQuestions = remember(questions, searchQuery) {
        val query = searchQuery.trim()
        if (query.isBlank()) {
            questions
        } else {
            questions.filter { question ->
                question.content.contains(query, ignoreCase = true) ||
                    question.id.toString().contains(query)
            }
        }
    }

    AppBackground {
        ExamTopBar("Question Bank", onBack)

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search questions...") },
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
                currentMessage.startsWith("Imported") -> AppMint
                currentMessage.startsWith("Import completed") -> AppAmber
                currentMessage.startsWith("No questions") -> AppAmber
                else -> AppRed
            }
            val bannerIcon = if (bannerColor == AppRed) Icons.Default.ErrorOutline else Icons.Default.Info
            InfoBanner(currentMessage, bannerColor, bannerIcon)
            Spacer(Modifier.height(12.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 90.dp)) {
            if (filteredQuestions.isEmpty() && searchQuery.isNotBlank()) {
                item {
                    InfoBanner("No matching questions.", AppAmber, Icons.Default.Info)
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
                Text("Add Question")
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
                Text(if (importLoading) "Importing..." else "Import")
            }
        }
    }
}

@Composable
fun CreateQuestionScreen(onBack: () -> Unit) {
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
    val scope = rememberCoroutineScope()
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
            message = "Please sign in again."
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
            message = "Cannot load subjects right now."
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
                selectedTopic = response.data.orEmpty().firstOrNull()
            } else {
                topics = emptyList()
                selectedTopic = null
                message = response.message
            }
        } catch (exception: Exception) {
            topics = emptyList()
            selectedTopic = null
            message = "Cannot load topics right now."
        }
    }

    fun saveQuestion() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Please sign in again."; return }
        val selectedExam = ExamAttemptStore.selectedExam.value
        if (selectedExam == null) { message = "Select an exam before creating questions."; return }
        if (content.isBlank()) { message = "Question content is required."; return }
        val subject = selectedSubject
        if (subject == null) { message = "Select a subject before saving."; return }
        val answerEntries = listOf(
            "A" to answerA.trim(),
            "B" to answerB.trim(),
            "C" to answerC.trim(),
            "D" to answerD.trim()
        ).filter { it.second.isNotBlank() }
        val minAnswers = if (type == QuestionType.FILL_BLANK) 1 else 2
        if (answerEntries.size < minAnswers) {
            message = if (type == QuestionType.FILL_BLANK) "Enter the expected answer." else "At least two answer options are required."
            return
        }
        val validCorrectLabels = answerEntries.map { it.first }.toSet()
        val selectedCorrect = correctAnswers.intersect(validCorrectLabels)
        if (selectedCorrect.isEmpty()) { message = "Select the correct answer."; return }
        if (type != QuestionType.MULTI && selectedCorrect.size != 1) { message = "Select one correct answer."; return }
        scope.launch {
            loading = true; message = null
            try {
                val response = ApiClient.createQuestionForExam(
                    authorization,
                    selectedExam.id,
                    ExamQuestionCreateRequest(
                        subjectId = subject.id,
                        topicId = selectedTopic?.id,
                        content = content.trim(),
                        type = type.name,
                        difficulty = difficulty.name,
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
                    message = "Question added to ${selectedExam.title}"
                } else {
                    message = response.message
                }
            } catch (exception: HttpException) { message = "Cannot save question. Please check your account permission." }
            catch (exception: Exception) { message = "Cannot save question right now." }
            finally { loading = false }
        }
    }

    AppBackground {
        ExamTopBar("Create Question", onBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                val selectedExam = ExamAttemptStore.selectedExam.value
                InfoBanner(
                    selectedExam?.let { "Adding question to exam: ${it.title}" } ?: "No exam selected. Open an exam before adding questions.",
                    if (selectedExam != null) AppMint else AppAmber,
                    Icons.Default.Info
                )

                SectionTitle("Question Content")
                OutlinedTextField(content, { content = it }, modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), label = { Text("Enter the question text") }, enabled = !loading, shape = MaterialTheme.shapes.medium)

                SectionTitle("Classification")
                CatalogDropdown(
                    label = "Subject",
                    value = selectedSubject?.name ?: if (catalogLoading) "Loading subjects..." else "Select subject",
                    enabled = !loading && !catalogLoading && subjects.isNotEmpty(),
                    items = subjects,
                    itemText = { it.name },
                    onSelect = {
                        selectedSubject = it
                        selectedTopic = null
                    }
                )
                Spacer(Modifier.height(8.dp))
                CatalogDropdown(
                    label = "Topic (optional)",
                    value = selectedTopic?.name ?: if (topics.isEmpty()) "No topic" else "No topic selected",
                    enabled = !loading && topics.isNotEmpty(),
                    items = topics,
                    itemText = { it.name },
                    onSelect = { selectedTopic = it },
                    leadingClearItem = "No topic",
                    onClear = { selectedTopic = null }
                )

                Spacer(Modifier.height(8.dp))
                Text("Difficulty", color = AppMuted, style = MaterialTheme.typography.labelMedium)
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
                Text("Question Type", color = AppMuted, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuestionType.entries.forEach { FilterChip(selected = type == it, onClick = { type = it }, label = { Text(it.name) }, shape = MaterialTheme.shapes.small) }
                }

                SectionTitle("Answer Options")
                if (type == QuestionType.FILL_BLANK) {
                    OutlinedTextField(
                        answerA,
                        {
                            answerA = it
                            correctAnswers = setOf("A")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Expected answer") },
                        enabled = !loading,
                        shape = MaterialTheme.shapes.medium
                    )
                } else {
                    Text(
                        if (type == QuestionType.MULTI) "Tick all correct answers" else "Select the correct answer",
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

                SectionTitle("Explanation")
                OutlinedTextField(explanation, { explanation = it }, modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp), label = { Text("Explain why this is correct") }, enabled = !loading, shape = MaterialTheme.shapes.medium)

                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    InfoBanner(message.orEmpty(), if (message.orEmpty().startsWith("Question added")) AppMint else AppRed, if (message.orEmpty().startsWith("Question added")) Icons.Default.CheckCircle else Icons.Default.ErrorOutline)
                }
                Spacer(Modifier.height(14.dp))
                PrimaryAction(if (loading) "Saving..." else "Save Question") { if (!loading) saveQuestion() }
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
            label = { Text("Answer $label") },
            enabled = enabled,
            shape = MaterialTheme.shapes.medium
        )
    }
}

@Composable
fun EditQuestionScreen(onBack: () -> Unit) {
    val exam = ExamAttemptStore.selectedExam.value
    val question = ExamAttemptStore.selectedQuestion.value
    var type by remember(question?.questionId) { mutableStateOf(question?.type?.let { runCatching { QuestionType.valueOf(it) }.getOrNull() } ?: QuestionType.SINGLE) }
    var difficulty by remember(question?.questionId) { mutableStateOf(question?.difficulty?.let { runCatching { Difficulty.valueOf(it) }.getOrNull() } ?: Difficulty.MEDIUM) }
    var content by remember(question?.questionId) { mutableStateOf(question?.content.orEmpty()) }
    var subjects by remember { mutableStateOf<List<SubjectResponse>>(emptyList()) }
    var topics by remember { mutableStateOf<List<TopicResponse>>(emptyList()) }
    var selectedSubject by remember { mutableStateOf<SubjectResponse?>(null) }
    var selectedTopic by remember { mutableStateOf<TopicResponse?>(null) }
    val existingAnswers = question?.answers.orEmpty()
    var answerA by remember(question?.questionId) { mutableStateOf(existingAnswers.getOrNull(0)?.content.orEmpty()) }
    var answerB by remember(question?.questionId) { mutableStateOf(existingAnswers.getOrNull(1)?.content.orEmpty()) }
    var answerC by remember(question?.questionId) { mutableStateOf(existingAnswers.getOrNull(2)?.content.orEmpty()) }
    var answerD by remember(question?.questionId) { mutableStateOf(existingAnswers.getOrNull(3)?.content.orEmpty()) }
    var correctAnswers by remember(question?.questionId) {
        mutableStateOf(existingAnswers.mapIndexedNotNull { index, answer -> if (answer.correct == true) ('A' + index).toString() else null }.toSet().ifEmpty { setOf("A") })
    }
    var explanation by remember(question?.questionId) { mutableStateOf(existingAnswers.firstOrNull { it.correct == true }?.explanation.orEmpty()) }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var catalogLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
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
            message = "Please sign in again."
            return@LaunchedEffect
        }
        catalogLoading = true
        try {
            val response = ApiClient.getSubjects(authorization)
            subjects = response.data.orEmpty()
            selectedSubject = subjects.firstOrNull { it.id == question?.subjectId } ?: subjects.firstOrNull()
        } catch (exception: Exception) {
            message = "Cannot load subjects right now."
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
            selectedTopic = topics.firstOrNull { it.id == question?.topicId }
        } catch (exception: Exception) {
            topics = emptyList()
            selectedTopic = null
            message = "Cannot load topics right now."
        }
    }

    fun buildAnswerEntries(): List<Pair<String, String>> {
        return listOf("A" to answerA.trim(), "B" to answerB.trim(), "C" to answerC.trim(), "D" to answerD.trim())
            .filter { it.second.isNotBlank() }
    }

    fun updateQuestion() {
        val selectedExam = exam
        val selectedQuestion = question
        val subject = selectedSubject
        val authorization = SessionManager.authorizationHeader()
        if (selectedExam == null || selectedQuestion == null) { message = "Select a question before editing."; return }
        if (authorization == null) { message = "Please sign in again."; return }
        if (subject == null) { message = "Select a subject before saving."; return }
        if (content.isBlank()) { message = "Question content is required."; return }
        val answerEntries = buildAnswerEntries()
        val minAnswers = if (type == QuestionType.FILL_BLANK) 1 else 2
        if (answerEntries.size < minAnswers) {
            message = if (type == QuestionType.FILL_BLANK) "Enter the expected answer." else "At least two answer options are required."
            return
        }
        val validCorrectLabels = answerEntries.map { it.first }.toSet()
        val selectedCorrect = correctAnswers.intersect(validCorrectLabels)
        if (selectedCorrect.isEmpty()) { message = "Select the correct answer."; return }
        if (type != QuestionType.MULTI && selectedCorrect.size != 1) { message = "Select one correct answer."; return }

        scope.launch {
            loading = true; message = null
            try {
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
                    message = "Question updated."
                } else {
                    message = response.message
                }
            } catch (exception: HttpException) { message = "Cannot update question. Please check your account permission." }
            catch (exception: Exception) { message = "Cannot update question right now." }
            finally { loading = false }
        }
    }

    AppBackground {
        ExamTopBar("Edit Question", onBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                InfoBanner(exam?.let { "Exam: ${it.title}" } ?: "No exam selected.", if (exam != null) AppMint else AppAmber, Icons.Default.Info)

                SectionTitle("Question Content")
                OutlinedTextField(content, { content = it }, modifier = Modifier.fillMaxWidth().height(120.dp), label = { Text("Enter the question text") }, enabled = !loading, shape = MaterialTheme.shapes.medium)

                SectionTitle("Classification")
                CatalogDropdown(
                    label = "Subject",
                    value = selectedSubject?.name ?: if (catalogLoading) "Loading subjects..." else "Select subject",
                    enabled = !loading && !catalogLoading && subjects.isNotEmpty(),
                    items = subjects,
                    itemText = { it.name },
                    onSelect = {
                        selectedSubject = it
                        selectedTopic = null
                    }
                )
                Spacer(Modifier.height(8.dp))
                CatalogDropdown(
                    label = "Topic",
                    value = selectedTopic?.name ?: "No topic",
                    enabled = !loading && topics.isNotEmpty(),
                    items = topics,
                    itemText = { it.name },
                    onSelect = { selectedTopic = it },
                    leadingClearItem = "No topic",
                    onClear = { selectedTopic = null }
                )

                Spacer(Modifier.height(8.dp))
                Text("Difficulty", color = AppMuted, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.entries.forEach { d ->
                        FilterChip(selected = difficulty == d, onClick = { difficulty = d }, label = { Text(d.name) }, shape = MaterialTheme.shapes.small)
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Question Type", color = AppMuted, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuestionType.entries.forEach { qType -> FilterChip(selected = type == qType, onClick = { type = qType }, label = { Text(qType.name) }, shape = MaterialTheme.shapes.small) }
                }

                SectionTitle("Answer Options")
                if (type == QuestionType.FILL_BLANK) {
                    OutlinedTextField(answerA, { answerA = it; correctAnswers = setOf("A") }, modifier = Modifier.fillMaxWidth(), label = { Text("Expected answer") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                } else {
                    Text(if (type == QuestionType.MULTI) "Tick all correct answers" else "Select the correct answer", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
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

                SectionTitle("Explanation")
                OutlinedTextField(explanation, { explanation = it }, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Explain why this is correct") }, enabled = !loading, shape = MaterialTheme.shapes.medium)

                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    InfoBanner(message.orEmpty(), if (message.orEmpty().startsWith("Question updated")) AppMint else AppRed, if (message.orEmpty().startsWith("Question updated")) Icons.Default.CheckCircle else Icons.Default.ErrorOutline)
                }
                Spacer(Modifier.height(14.dp))
                PrimaryAction(if (loading) "Saving..." else "Save Changes") { if (!loading) updateQuestion() }
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
    onClear: (() -> Unit)? = null
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
            }
        }
    }
}

@Composable
fun CreateExamScreen(onGenerate: () -> Unit, onBack: () -> Unit) {
    var randomQuestion by remember { mutableStateOf(true) }
    var randomAnswer by remember { mutableStateOf(true) }
    var title by remember { mutableStateOf("Android Practice Exam") }
    var subject by remember { mutableStateOf("Android") }
    var duration by remember { mutableStateOf("45") }
    var openTime by remember { mutableStateOf("") }
    var closeTime by remember { mutableStateOf("") }
    var questionCount by remember { mutableStateOf("30") }
    var points by remember { mutableStateOf("0.33") }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var excelRows by remember { mutableStateOf<List<ExamExcelQuestionRow>>(emptyList()) }
    var excelFileLabel by remember { mutableStateOf<String?>(null) }
    var excelParseErrors by remember { mutableStateOf(emptyList<String>()) }
    var templateLoading by remember { mutableStateOf(false) }
    var excelLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }
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
                    message = "Cannot open the selected Excel file."
                    return@launch
                }

                excelRows = result.rows
                excelFileLabel = "Selected Excel: ${result.rows.size} valid question(s)"
                questionCount = result.rows.size.takeIf { it > 0 }?.toString() ?: questionCount
                excelParseErrors = result.failures.take(5).map { "row ${it.rowNumber}: ${it.message}" }
                message = when {
                    result.rows.isNotEmpty() && result.failures.isEmpty() -> "Loaded ${result.rows.size} question(s) from Excel."
                    result.rows.isNotEmpty() -> "Loaded ${result.rows.size} question(s); ${result.failures.size} row(s) need review."
                    else -> "No valid questions found in the selected Excel file."
                }
            } catch (exception: Exception) {
                excelRows = emptyList()
                excelFileLabel = null
                excelParseErrors = emptyList()
                message = "Cannot read the selected Excel file."
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
                    "Excel template saved. Subject and topic lists were not loaded."
                } else {
                    "Excel template saved with catalog dropdowns."
                }
            } catch (exception: Exception) {
                message = "Cannot save Excel template."
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

    fun saveExam() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Please sign in again."; return }
        val durationMinutes = duration.toIntOrNull()
        if (title.isBlank() || durationMinutes == null || durationMinutes < 1) { message = "Enter a title and valid duration."; return }
        scope.launch {
            loading = true; message = null
            try {
                val response = ApiClient.createExam(
                    authorization,
                    ExamCreateRequest(
                        title = title.trim(),
                        durationMinutes = durationMinutes,
                        scorePerQuestion = points.ifBlank { "1.0" },
                        startTime = null,
                        endTime = null,
                        shuffleQuestions = randomQuestion,
                        shuffleAnswers = randomAnswer
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
                        "Exam created: ${createdExam.code}. Imported ${importResult.createdCount} question(s)."
                    } else {
                        val sampleFailures = importResult.failures.take(3).joinToString("; ") {
                            "row ${it.rowNumber}: ${it.message}"
                        }
                        val moreFailures = if (importResult.failures.size > 3) " +${importResult.failures.size - 3} more" else ""
                        "Exam created: ${createdExam.code}. Imported ${importResult.createdCount}/${importResult.totalCount} question(s). $sampleFailures$moreFailures"
                    }
                } else {
                    message = if (response.success) "Exam created: ${createdExam?.code}" else response.message
                }
            } catch (exception: HttpException) { message = "Cannot save exam. Please check your account permission." }
            catch (exception: Exception) { message = "Cannot save exam right now." }
            finally { loading = false }
        }
    }

    AppBackground {
        ExamTopBar("Create Exam", onBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                SectionTitle("Exam Details")
                OutlinedTextField(title, { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Exam title") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(subject, { subject = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Subject") }, enabled = !loading, shape = MaterialTheme.shapes.medium)

                SectionTitle("Timing")
                OutlinedTextField(duration, { duration = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Duration (minutes)") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(openTime, { openTime = it }, modifier = Modifier.weight(1f), label = { Text("Open time") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                    OutlinedTextField(closeTime, { closeTime = it }, modifier = Modifier.weight(1f), label = { Text("Close time") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                }

                SectionTitle("Scoring")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(questionCount, { questionCount = it }, modifier = Modifier.weight(1f), label = { Text("Questions") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                    OutlinedTextField(points, { points = it }, modifier = Modifier.weight(1f), label = { Text("Points each") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                }

                SectionTitle("Excel Import")
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
                        Text(if (templateLoading) "Saving..." else "Template")
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
                        Text(if (excelLoading) "Reading..." else "Choose Excel")
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

                SectionTitle("Options")
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column { Text("Randomize questions", fontWeight = FontWeight.Medium); Text("Shuffle question order", color = AppMuted, style = MaterialTheme.typography.bodyMedium) }
                            Switch(randomQuestion, { randomQuestion = it }, colors = SwitchDefaults.colors(checkedTrackColor = AppIndigo))
                        }
                        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = AppCardBorder)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column { Text("Randomize answers", fontWeight = FontWeight.Medium); Text("Shuffle answer options", color = AppMuted, style = MaterialTheme.typography.bodyMedium) }
                            Switch(randomAnswer, { randomAnswer = it }, colors = SwitchDefaults.colors(checkedTrackColor = AppIndigo))
                        }
                    }
                }

                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    val currentMessage = message.orEmpty()
                    val successMessage = currentMessage.startsWith("Exam created") ||
                        currentMessage.startsWith("Loaded") ||
                        currentMessage.startsWith("Excel template saved")
                    val warningMessage = currentMessage.contains("need review") ||
                        currentMessage.startsWith("No valid questions")
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
                        .height(48.dp), shape = MaterialTheme.shapes.medium, enabled = !loading) { Text(if (loading) "Saving..." else "Save Exam") }
                    Button(onClick = onGenerate, modifier = Modifier
                        .weight(1f)
                        .height(48.dp), shape = MaterialTheme.shapes.medium, colors = ButtonDefaults.buttonColors(containerColor = AppViolet)) {
                        Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Auto Generate")
                    }
                }
            }
        }
    }
}

@Composable
fun EditExamScreen(onBack: () -> Unit) {
    val exam = ExamAttemptStore.selectedExam.value
    var randomQuestion by remember(exam?.id) { mutableStateOf(exam?.shuffleQuestions ?: true) }
    var randomAnswer by remember(exam?.id) { mutableStateOf(exam?.shuffleAnswers ?: true) }
    var title by remember(exam?.id) { mutableStateOf(exam?.title.orEmpty()) }
    var duration by remember(exam?.id) { mutableStateOf(exam?.durationMinutes?.toString() ?: "45") }
    var points by remember(exam?.id) { mutableStateOf(exam?.scorePerQuestion ?: "1.0") }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }

    fun updateExam() {
        val selectedExam = exam
        if (selectedExam == null) { message = "Select an exam before editing."; return }
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Please sign in again."; return }
        val durationMinutes = duration.toIntOrNull()
        if (title.isBlank() || durationMinutes == null || durationMinutes < 1) { message = "Enter a title and valid duration."; return }

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
                        startTime = selectedExam.startTime,
                        endTime = selectedExam.endTime,
                        shuffleQuestions = randomQuestion,
                        shuffleAnswers = randomAnswer
                    )
                )
                if (response.success) {
                    response.data?.let { ExamAttemptStore.setBackendExamId(it.id, it) }
                    message = "Exam updated."
                } else {
                    message = response.message
                }
            } catch (exception: HttpException) { message = "Cannot update exam. Please check your account permission." }
            catch (exception: Exception) { message = "Cannot update exam right now." }
            finally { loading = false }
        }
    }

    AppBackground {
        ExamTopBar("Edit Exam", onBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                InfoBanner(
                    exam?.let { "Editing exam: ${it.code}" } ?: "No exam selected.",
                    if (exam != null) AppMint else AppAmber,
                    Icons.Default.Info
                )

                SectionTitle("Exam Details")
                OutlinedTextField(title, { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Exam title") }, enabled = !loading && exam != null, shape = MaterialTheme.shapes.medium)

                SectionTitle("Timing")
                OutlinedTextField(duration, { duration = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Duration (minutes)") }, enabled = !loading && exam != null, shape = MaterialTheme.shapes.medium)

                SectionTitle("Scoring")
                OutlinedTextField(points, { points = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Points each") }, enabled = !loading && exam != null, shape = MaterialTheme.shapes.medium)

                SectionTitle("Options")
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column { Text("Randomize questions", fontWeight = FontWeight.Medium); Text("Shuffle question order", color = AppMuted, style = MaterialTheme.typography.bodyMedium) }
                            Switch(randomQuestion, { randomQuestion = it }, enabled = !loading && exam != null, colors = SwitchDefaults.colors(checkedTrackColor = AppIndigo))
                        }
                        HorizontalDivider(Modifier.padding(vertical = 10.dp), color = AppCardBorder)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column { Text("Randomize answers", fontWeight = FontWeight.Medium); Text("Shuffle answer options", color = AppMuted, style = MaterialTheme.typography.bodyMedium) }
                            Switch(randomAnswer, { randomAnswer = it }, enabled = !loading && exam != null, colors = SwitchDefaults.colors(checkedTrackColor = AppIndigo))
                        }
                    }
                }

                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    InfoBanner(message.orEmpty(), if (message.orEmpty().startsWith("Exam updated")) AppMint else AppRed, if (message.orEmpty().startsWith("Exam updated")) Icons.Default.CheckCircle else Icons.Default.ErrorOutline)
                }

                Spacer(Modifier.height(14.dp))
                PrimaryAction(if (loading) "Saving..." else "Save Changes") { if (!loading) updateExam() }
            }
        }
    }
}

@Composable
fun AutoGenerateExamScreen(onBack: () -> Unit) {
    var title by remember { mutableStateOf("Generated Android Exam") }
    var subjects by remember { mutableStateOf<List<SubjectResponse>>(emptyList()) }
    var topics by remember { mutableStateOf<List<TopicResponse>>(emptyList()) }
    var selectedSubject by remember { mutableStateOf<SubjectResponse?>(null) }
    var selectedTopic by remember { mutableStateOf<TopicResponse?>(null) }
    var easy by remember { mutableStateOf("10") }
    var medium by remember { mutableStateOf("15") }
    var hard by remember { mutableStateOf("5") }
    var duration by remember { mutableStateOf("45") }
    var points by remember { mutableStateOf("0.33") }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var catalogLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Please sign in again."
            return@LaunchedEffect
        }
        catalogLoading = true
        try {
            val response = ApiClient.getSubjects(authorization)
            subjects = response.data.orEmpty()
            selectedSubject = subjects.firstOrNull()
            message = if (subjects.isEmpty()) "No subjects available." else null
        } catch (exception: Exception) {
            message = "Cannot load subjects right now."
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
            selectedTopic = topics.firstOrNull()
        } catch (exception: Exception) {
            topics = emptyList()
            selectedTopic = null
            message = "Cannot load topics right now."
        }
    }

    fun generateExam() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Please sign in again."; return }
        val subject = selectedSubject
        if (subject == null) { message = "Select a subject."; return }
        val durationMinutes = duration.toIntOrNull()
        val easyCount = easy.toIntOrNull()
        val mediumCount = medium.toIntOrNull()
        val hardCount = hard.toIntOrNull()
        if (title.isBlank() || durationMinutes == null || durationMinutes < 1) { message = "Enter a title and valid duration."; return }
        if (easyCount == null || mediumCount == null || hardCount == null || easyCount < 0 || mediumCount < 0 || hardCount < 0) {
            message = "Question counts must be valid numbers."
            return
        }
        if (easyCount + mediumCount + hardCount == 0) { message = "Select at least one question."; return }
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
                        hardCount = hardCount
                    )
                )
                message = if (response.success) "Exam generated: ${response.data?.code}" else response.message
            } catch (exception: HttpException) {
                val backendMessage = runCatching {
                    val body = exception.response()?.errorBody()?.string().orEmpty()
                    gson.fromJson(body, ApiResponse::class.java)?.message
                }.getOrNull().orEmpty()
                message = backendMessage.ifBlank {
                    if (exception.code() == 403) "You do not have permission to generate exams." else "Cannot generate exam right now."
                }
            }
            catch (exception: Exception) { message = "Cannot generate exam right now." }
            finally { loading = false }
        }
    }

    AppBackground {
        ExamTopBar("Auto Generate Exam", onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            SectionTitle("Exam Info")
            OutlinedTextField(title, { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Exam title") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(duration, { duration = it }, modifier = Modifier.weight(1f), label = { Text("Duration") }, enabled = !loading, shape = MaterialTheme.shapes.medium, singleLine = true)
                OutlinedTextField(points, { points = it }, modifier = Modifier.weight(1f), label = { Text("Points each") }, enabled = !loading, shape = MaterialTheme.shapes.medium, singleLine = true)
            }

            SectionTitle("Classification")
            CatalogDropdown(
                label = "Subject",
                value = selectedSubject?.name ?: if (catalogLoading) "Loading subjects..." else "Select subject",
                enabled = !loading && !catalogLoading && subjects.isNotEmpty(),
                items = subjects,
                itemText = { it.name },
                onSelect = {
                    selectedSubject = it
                    selectedTopic = null
                }
            )
            Spacer(Modifier.height(8.dp))
            CatalogDropdown(
                label = "Topic",
                value = selectedTopic?.name ?: "All topics",
                enabled = !loading && topics.isNotEmpty(),
                items = topics,
                itemText = { it.name },
                onSelect = { selectedTopic = it },
                leadingClearItem = "All topics",
                onClear = { selectedTopic = null }
            )

            SectionTitle("Questions by Difficulty")
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ChipText("Easy", AppMint); Spacer(Modifier.width(12.dp))
                        OutlinedTextField(easy, { easy = it }, modifier = Modifier.weight(1f), enabled = !loading, shape = MaterialTheme.shapes.medium, singleLine = true)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ChipText("Medium", AppAmber); Spacer(Modifier.width(12.dp))
                        OutlinedTextField(medium, { medium = it }, modifier = Modifier.weight(1f), enabled = !loading, shape = MaterialTheme.shapes.medium, singleLine = true)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ChipText("Hard", AppRed); Spacer(Modifier.width(12.dp))
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
                        Text("Preview Configuration", fontWeight = FontWeight.Bold, color = AppIndigo)
                        Spacer(Modifier.height(8.dp))
                        val total = (easy.toIntOrNull() ?: 0) + (medium.toIntOrNull() ?: 0) + (hard.toIntOrNull() ?: 0)
                        Text("$total questions - ${duration.ifBlank { "--" }} minutes - Randomized - ${points.ifBlank { "--" }} pts each", color = AppMuted)
                    }
                }
            }

            if (message != null) {
                Spacer(Modifier.height(10.dp))
                InfoBanner(message.orEmpty(), if (message.orEmpty().startsWith("Exam generated")) AppMint else AppRed, if (message.orEmpty().startsWith("Exam generated")) Icons.Default.CheckCircle else Icons.Default.ErrorOutline)
            }

            Spacer(Modifier.height(14.dp))
            PrimaryAction(if (loading) "Generating..." else "Generate Exam") { if (!loading) generateExam() }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun LiveMonitoringScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Live Monitoring", onBack)
        SectionTitle("Active Candidates")
        InfoBanner("Live monitoring is not available yet.", AppAmber, Icons.Default.Info)
        Spacer(Modifier.height(12.dp))
        SectionTitle("Realtime Log")
        InfoBanner("Activity history is not available yet.", AppAmber, Icons.Default.Info)
    }
}

@Composable
private fun LiveMonitoringScreenLegacy(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Live Monitoring", onBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item { SectionTitle("Active Candidates") }
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
                val logIcon = when (log.action) { "SCREENSHOT" -> Icons.Default.Warning; "APP_EXIT" -> Icons.Default.ExitToApp; "LOST_CONNECTION" -> Icons.Default.WifiOff; "FOCUS_LOST" -> Icons.Default.VisibilityOff; else -> Icons.Default.Info }
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
            message = "Please sign in again."
            return@LaunchedEffect
        }

        loadingExams = true
        try {
            val response = ApiClient.getExams(authorization)
            exams = response.data.orEmpty()
            selectedExam = exams.firstOrNull()
            message = if (exams.isEmpty()) "Create an exam first to view reports." else null
        } catch (exception: HttpException) {
            val backendMessage = runCatching {
                val body = exception.response()?.errorBody()?.string().orEmpty()
                gson.fromJson(body, ApiResponse::class.java)?.message
            }.getOrNull().orEmpty()
            message = backendMessage.ifBlank { "Cannot load exams right now." }
        } catch (exception: Exception) {
            message = "Cannot load exams right now."
        } finally {
            loadingExams = false
        }
    }

    LaunchedEffect(selectedExam?.id) {
        val exam = selectedExam ?: return@LaunchedEffect
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Please sign in again."
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
                if (exception.code() == 403) "You do not have permission to view reports." else "Cannot load report right now."
            }
        } catch (exception: Exception) {
            message = "Cannot load report right now."
        } finally {
            loadingReport = false
        }
    }

    AppBackground {
        ExamTopBar("Reports", onBack)
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
                    Text("Exam Reports", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        selectedExam?.title ?: "Select an exam to view results.",
                        color = AppMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            SectionTitle("Exam")
            CatalogDropdown(
                label = "Exam",
                value = selectedExam?.title ?: if (loadingExams) "Loading exams..." else "No exam selected",
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
                InfoBanner("Loading report...", AppBlue, Icons.Default.Info)
            }

            report?.let { data ->
                SectionTitle("Overview")
                MetricCard("Results", data.totalResults.toString(), "records", AppBlue, Icons.Default.Assessment)
                Spacer(Modifier.height(8.dp))
                MetricCard("Submitted", data.submittedCount.toString(), "submitted", AppMint, Icons.Default.CheckCircle)
                Spacer(Modifier.height(8.dp))
                MetricCard("Doing", data.doingCount.toString(), "in progress", AppAmber, Icons.Default.Info)
                Spacer(Modifier.height(8.dp))
                MetricCard("Average", reportScore(data.averageScore), "score", AppViolet, Icons.Default.Assessment)
                Spacer(Modifier.height(8.dp))
                MetricCard("Highest", reportScore(data.highestScore), "score", AppMint, Icons.Default.Assessment)
                Spacer(Modifier.height(8.dp))
                MetricCard("Lowest", reportScore(data.lowestScore), "score", AppRed, Icons.Default.Assessment)

                SectionTitle("Student Results")
                val results = data.results.orEmpty()
                if (results.isEmpty()) {
                    InfoBanner("No results yet.", AppAmber, Icons.Default.Info)
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
        ?: "Student ${result.studentId}"

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
                    Text(result.username ?: "Student ID ${result.studentId}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                }
                ChipText(reportStatusLabel(result.status), statusColor)
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = AppCardBorder)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("Score", color = AppMuted, style = MaterialTheme.typography.labelMedium)
                    Text(reportScore(result.score), fontWeight = FontWeight.Bold, color = AppText)
                }
                Column(Modifier.weight(1f)) {
                    Text("Submitted", color = AppMuted, style = MaterialTheme.typography.labelMedium)
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
    return value?.takeIf { it.isNotBlank() }?.replace("T", " ")?.take(16) ?: "Not submitted"
}

private fun reportStatusLabel(status: String): String {
    return when (status) {
        "SUBMITTED" -> "Submitted"
        "DOING" -> "Doing"
        "CANCELLED" -> "Cancelled"
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
        ExamTopBar("Reports", onBack)
        GradientHero("Average Score 7.6", "Performance overview across all exams")

        SectionTitle("Score Distribution")
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

        SectionTitle("Top Results")
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
                        Text("${result.correct} correct · ${result.wrong} wrong · ${result.blank} blank", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        PrimaryAction("Export Excel / PDF")
    }
}




