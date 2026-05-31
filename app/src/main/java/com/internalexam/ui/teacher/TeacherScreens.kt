package com.internalexam.ui.teacher

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internalexam.data.SessionManager
import com.internalexam.data.ExamAttemptStore
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.AnswerCreateRequest
import com.internalexam.data.network.ExamCreateRequest
import com.internalexam.data.network.ExamGenerateRequest
import com.internalexam.data.network.ExamQuestionCreateRequest
import com.internalexam.data.network.ExamResponse
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
import com.internalexam.ui.components.MetricCard
import com.internalexam.ui.components.PrimaryAction
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
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun TeacherExamListScreen(
    onCreateExam: () -> Unit,
    onAddQuestion: (ExamResponse) -> Unit,
    onBack: () -> Unit
) {
    var exams by remember { mutableStateOf<List<ExamResponse>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Please sign in again."
            return@LaunchedEffect
        }
        try {
            val response = ApiClient.getExams(authorization)
            exams = response.data.orEmpty()
            message = if (exams.isEmpty()) "No exams found. Create an exam first." else null
        } catch (exception: Exception) {
            message = "Cannot load exams from backend."
        }
    }

    AppBackground {
        ExamTopBar("Exams", onBack)
        SectionTitle("Exam List", "Choose an exam to add questions")
        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(12.dp))
        }
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
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(onClick = { onAddQuestion(exam) }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Add Question")
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
            message = "Cannot load dashboard data from backend."
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
                MetricCard("Exams", examCount?.toString() ?: "--", "From database", AppBlue, Icons.Default.Assessment)
                MetricCard("Alerts", "--", "No backend API", AppMuted, Icons.Default.Warning)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Questions", questionCount?.toString() ?: "--", "From database", AppViolet, Icons.Default.QuestionAnswer)
                MetricCard("Sync", "Live", "Backend API", AppMint, Icons.Default.CloudDone)
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
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Please sign in again."
            return@LaunchedEffect
        }
        try {
            val response = ApiClient.getQuestions(authorization)
            questions = response.data.orEmpty()
            message = if (questions.isEmpty()) "No questions found in the database." else null
        } catch (exception: Exception) {
            message = "Cannot load questions from the backend."
        }
    }

    AppBackground {
        ExamTopBar("Question Bank", onBack)

        OutlinedTextField(
            "", {},
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
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Info)
            Spacer(Modifier.height(12.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 90.dp)) {
            items(questions) { question ->
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
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Import")
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
    var correctAnswer by remember { mutableStateOf("A") }
    var explanation by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var catalogLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
            message = "Cannot load subjects from backend."
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
            message = "Cannot load topics from backend."
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
        val answerContents = listOf(answerA, answerB, answerC, answerD)
            .map { it.trim() }
            .filter { it.isNotBlank() }
        if (answerContents.size < 2) { message = "At least two answer options are required."; return }
        val correct = correctAnswer.trim().uppercase()
        val validCorrectLabels = answerContents.indices.map { ('A' + it).toString() }
        if (correct !in validCorrectLabels) { message = "Correct answer must be one of ${validCorrectLabels.joinToString()}."; return }
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
                        answers = answerContents.mapIndexed { index, answer ->
                            val label = ('A' + index).toString()
                            AnswerCreateRequest(
                                content = answer.trim(),
                                correct = label == correct,
                                explanation = if (label == correct) explanation.ifBlank { null } else null
                            )
                        }
                    )
                )
                message = if (response.success) "Question added to ${selectedExam.title}" else response.message
            } catch (exception: HttpException) { message = "Backend error ${exception.code()}. Check teacher permissions." }
            catch (exception: Exception) { message = "Cannot connect to backend." }
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
                    itemText = { "${it.name} (#${it.id})" },
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
                    itemText = { "${it.name} (#${it.id})" },
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
                OutlinedTextField(answerA, { answerA = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Answer A") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(answerB, { answerB = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Answer B") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(answerC, { answerC = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Answer C") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(answerD, { answerD = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Answer D") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(correctAnswer, { correctAnswer = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Correct answer (A, B, C, or D)") }, enabled = !loading, shape = MaterialTheme.shapes.medium)

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
    val scope = rememberCoroutineScope()

    fun saveExam() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Please sign in again."; return }
        val durationMinutes = duration.toIntOrNull()
        if (title.isBlank() || durationMinutes == null || durationMinutes < 1) { message = "Enter a title and valid duration."; return }
        scope.launch {
            loading = true; message = null
            try {
                val response = ApiClient.createExam(authorization, ExamCreateRequest(title = title.trim(), durationMinutes = durationMinutes, scorePerQuestion = points.ifBlank { "1.0" }, startTime = null, endTime = null, shuffleQuestions = randomQuestion, shuffleAnswers = randomAnswer))
                message = if (response.success) "Exam created: ${response.data?.code}" else response.message
            } catch (exception: HttpException) { message = "Backend error ${exception.code()}. Check teacher permissions." }
            catch (exception: Exception) { message = "Cannot connect to backend." }
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
                    InfoBanner(message.orEmpty(), if (message.orEmpty().startsWith("Exam created")) AppMint else AppRed, if (message.orEmpty().startsWith("Exam created")) Icons.Default.CheckCircle else Icons.Default.ErrorOutline)
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
fun AutoGenerateExamScreen(onBack: () -> Unit) {
    var title by remember { mutableStateOf("Generated Android Exam") }
    var subject by remember { mutableStateOf("Android") }
    var topic by remember { mutableStateOf("Compose") }
    var easy by remember { mutableStateOf("10") }
    var medium by remember { mutableStateOf("15") }
    var hard by remember { mutableStateOf("5") }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun generateExam() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) { message = "Please sign in again."; return }
        scope.launch {
            loading = true; message = null
            try {
                val response = ApiClient.generateExam(authorization, ExamGenerateRequest(title = title, durationMinutes = 45, scorePerQuestion = "0.33"))
                message = if (response.success) "Exam generated: ${response.data?.code}" else response.message
            } catch (exception: HttpException) { message = "Backend error ${exception.code()}. Check teacher permissions." }
            catch (exception: Exception) { message = "Cannot connect to backend." }
            finally { loading = false }
        }
    }

    AppBackground {
        ExamTopBar("Auto Generate Exam", onBack)

        SectionTitle("Exam Info")
        OutlinedTextField(title, { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Exam title") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(subject, { subject = it }, modifier = Modifier.weight(1f), label = { Text("Subject") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
            OutlinedTextField(topic, { topic = it }, modifier = Modifier.weight(1f), label = { Text("Topic") }, enabled = !loading, shape = MaterialTheme.shapes.medium)
        }

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
                    Text("$total questions · 45 minutes · Randomized · 0.33 pts each", color = AppMuted)
                }
            }
        }

        if (message != null) {
            Spacer(Modifier.height(10.dp))
            InfoBanner(message.orEmpty(), if (message.orEmpty().startsWith("Exam generated")) AppMint else AppRed, if (message.orEmpty().startsWith("Exam generated")) Icons.Default.CheckCircle else Icons.Default.ErrorOutline)
        }

        Spacer(Modifier.height(14.dp))
        PrimaryAction(if (loading) "Generating..." else "Generate Exam") { if (!loading) generateExam() }
    }
}

@Composable
fun LiveMonitoringScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Live Monitoring", onBack)
        SectionTitle("Active Candidates")
        InfoBanner("Live monitoring data is not available because the backend does not expose a monitoring API yet.", AppAmber, Icons.Default.Info)
        Spacer(Modifier.height(12.dp))
        SectionTitle("Realtime Log")
        InfoBanner("Audit log data is not available in the mobile API yet.", AppAmber, Icons.Default.Info)
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
    AppBackground {
        ExamTopBar("Reports", onBack)
        GradientHero("Reports", "Backend report data is not available yet")
        SectionTitle("Score Distribution")
        InfoBanner("Reports are hidden until the backend exposes report APIs.", AppAmber, Icons.Default.Info)
    }
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
