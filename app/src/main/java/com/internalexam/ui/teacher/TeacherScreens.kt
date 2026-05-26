package com.internalexam.ui.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.ExamCreateRequest
import com.internalexam.data.network.ExamGenerateRequest
import com.internalexam.data.network.QuestionCreateRequest
import com.internalexam.model.mock.CandidateStatus
import com.internalexam.model.mock.Difficulty
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.NetworkState
import com.internalexam.model.mock.QuestionType
import com.internalexam.ui.components.AppBackground
import com.internalexam.ui.components.ChipText
import com.internalexam.ui.components.ExamTopBar
import com.internalexam.ui.components.GradientHero
import com.internalexam.ui.components.MetricCard
import com.internalexam.ui.components.PrimaryAction
import com.internalexam.ui.components.SectionTitle
import com.internalexam.ui.components.StatusPill
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppBlue
import com.internalexam.ui.theme.AppMint
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppViolet
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun TeacherDashboardScreen(openQuestions: () -> Unit, openCreateExam: () -> Unit, openGenerate: () -> Unit, openMonitor: () -> Unit, openReports: () -> Unit) {
    AppBackground {
        Spacer(Modifier.height(18.dp))
        GradientHero("Teacher Dashboard", "Manage exams, question banks, and live integrity events") { StatusPill(NetworkState.ONLINE) }
        SectionTitle("Overview")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Exams", "12", "3 active", AppBlue, Icons.Default.Assessment)
                MetricCard("Alerts", "4", "Realtime", AppRed, Icons.Default.AutoAwesome)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Questions", "428", "Classified", AppViolet, Icons.Default.QuestionAnswer)
                MetricCard("Sync", "Synced", "10:35", AppMint)
            }
        }
        SectionTitle("Shortcuts")
        listOf(
            "Manage Questions" to openQuestions,
            "Create Exam" to openCreateExam,
            "Auto Generate Exam" to openGenerate,
            "Live Monitoring" to openMonitor,
            "Reports" to openReports
        ).forEach { (label, action) -> OutlinedButton(onClick = action, modifier = Modifier.fillMaxWidth()) { Text(label) } }
    }
}

@Composable
fun QuestionBankScreen(onCreate: () -> Unit, onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Question Bank", onBack)
        OutlinedTextField("", {}, modifier = Modifier.fillMaxWidth(), label = { Text("Search questions") })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 12.dp)) {
            ChipText("Android")
            ChipText("Compose")
            ChipText("Medium")
            ChipText("Single")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 90.dp)) {
            items(MockData.questions) { question ->
                Card(shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(16.dp)) {
                        Text(question.content, fontWeight = FontWeight.Bold)
                        Text("${question.subject} - ${question.topic} - ${question.difficulty} - ${question.type}", color = AppMuted)
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onCreate, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Add", modifier = Modifier.padding(start = 8.dp))
            }
            OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Import Excel") }
        }
    }
}

@Composable
fun CreateQuestionScreen(onBack: () -> Unit) {
    var type by remember { mutableStateOf(QuestionType.SINGLE) }
    var content by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Android") }
    var topic by remember { mutableStateOf("Jetpack Compose") }
    var correctAnswer by remember { mutableStateOf("A") }
    var explanation by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun saveQuestion() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Please sign in again."
            return
        }
        if (content.isBlank()) {
            message = "Question content is required."
            return
        }
        scope.launch {
            loading = true
            message = null
            try {
                val response = ApiClient.createQuestion(
                    authorization,
                    QuestionCreateRequest(
                        subjectId = 1,
                        topicId = 1,
                        content = content.trim(),
                        type = type.name,
                        difficulty = Difficulty.MEDIUM.name
                    )
                )
                message = if (response.success) "Question saved: #${response.data?.id}" else response.message
            } catch (exception: HttpException) {
                message = "Backend error ${exception.code()}. Check teacher permissions."
            } catch (exception: Exception) {
                message = "Cannot connect to backend."
            } finally {
                loading = false
            }
        }
    }

    AppBackground {
        ExamTopBar("Create Question", onBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                OutlinedTextField(content, { content = it }, modifier = Modifier.fillMaxWidth().height(120.dp), label = { Text("Question content") }, enabled = !loading)
                OutlinedTextField(subject, { subject = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Subject") }, enabled = !loading)
                OutlinedTextField(topic, { topic = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Topic") }, enabled = !loading)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Difficulty.entries.forEach { ChipText(it.name, if (it == Difficulty.MEDIUM) AppBlue else AppMuted) } }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { QuestionType.entries.forEach { FilterChip(selected = type == it, onClick = { type = it }, label = { Text(it.name) }) } }
                repeat(4) { index -> OutlinedTextField("", {}, modifier = Modifier.fillMaxWidth(), label = { Text("Answer ${'A' + index}") }) }
                OutlinedTextField(correctAnswer, { correctAnswer = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Correct answer") }, enabled = !loading)
                OutlinedTextField(explanation, { explanation = it }, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Explanation") }, enabled = !loading)
                if (message != null) Text(message.orEmpty(), color = if (message.orEmpty().startsWith("Question saved")) AppMint else AppRed)
                Spacer(Modifier.height(12.dp))
                PrimaryAction(if (loading) "Saving..." else "Save Question") { if (!loading) saveQuestion() }
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
        if (authorization == null) {
            message = "Please sign in again."
            return
        }
        val durationMinutes = duration.toIntOrNull()
        if (title.isBlank() || durationMinutes == null || durationMinutes < 1) {
            message = "Enter a title and valid duration."
            return
        }
        scope.launch {
            loading = true
            message = null
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
                message = if (response.success) "Exam created: ${response.data?.code}" else response.message
            } catch (exception: HttpException) {
                message = "Backend error ${exception.code()}. Check teacher permissions."
            } catch (exception: Exception) {
                message = "Cannot connect to backend."
            } finally {
                loading = false
            }
        }
    }

    AppBackground {
        ExamTopBar("Create Exam", onBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                OutlinedTextField(title, { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Exam title") }, enabled = !loading)
                OutlinedTextField(subject, { subject = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Subject") }, enabled = !loading)
                OutlinedTextField(duration, { duration = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Duration") }, enabled = !loading)
                OutlinedTextField(openTime, { openTime = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Open time") }, enabled = !loading)
                OutlinedTextField(closeTime, { closeTime = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Close time") }, enabled = !loading)
                OutlinedTextField(questionCount, { questionCount = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Question count") }, enabled = !loading)
                OutlinedTextField(points, { points = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Points per question") }, enabled = !loading)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Randomize questions"); Switch(randomQuestion, { randomQuestion = it }) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Randomize answers"); Switch(randomAnswer, { randomAnswer = it }) }
                if (message != null) Text(message.orEmpty(), color = if (message.orEmpty().startsWith("Exam created")) AppMint else AppRed)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { saveExam() }, modifier = Modifier.weight(1f), enabled = !loading) { Text(if (loading) "Saving..." else "Save") }
                    Button(onClick = onGenerate, modifier = Modifier.weight(1f)) { Text("Auto") }
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
        if (authorization == null) {
            message = "Please sign in again."
            return
        }
        scope.launch {
            loading = true
            message = null
            try {
                val response = ApiClient.generateExam(
                    authorization,
                    ExamGenerateRequest(title = title, durationMinutes = 45, scorePerQuestion = "0.33")
                )
                message = if (response.success) "Exam generated: ${response.data?.code}" else response.message
            } catch (exception: HttpException) {
                message = "Backend error ${exception.code()}. Check teacher permissions."
            } catch (exception: Exception) {
                message = "Cannot connect to backend."
            } finally {
                loading = false
            }
        }
    }

    AppBackground {
        ExamTopBar("Auto Generate Exam", onBack)
        OutlinedTextField(title, { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Exam title") }, enabled = !loading)
        OutlinedTextField(subject, { subject = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Subject") }, enabled = !loading)
        OutlinedTextField(topic, { topic = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Topic") }, enabled = !loading)
        SectionTitle("Questions by Difficulty")
        OutlinedTextField(easy, { easy = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Easy") }, enabled = !loading)
        OutlinedTextField(medium, { medium = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Medium") }, enabled = !loading)
        OutlinedTextField(hard, { hard = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Hard") }, enabled = !loading)
        Card(shape = MaterialTheme.shapes.large) {
            Text("Preview: 30 questions - 45 minutes - randomized questions and answers - 0.33 points each", modifier = Modifier.padding(16.dp))
        }
        if (message != null) Text(message.orEmpty(), color = if (message.orEmpty().startsWith("Exam generated")) AppMint else AppRed)
        PrimaryAction(if (loading) "Generating..." else "Generate Exam") { if (!loading) generateExam() }
    }
}

@Composable
fun LiveMonitoringScreen(onBack: () -> Unit) {
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
                Card(shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(candidate.name, fontWeight = FontWeight.Bold)
                            ChipText(candidate.status.name, color)
                        }
                        Text("${candidate.progress}% - ${candidate.device}", color = AppMuted)
                    }
                }
            }
            item { SectionTitle("Realtime Log") }
            items(MockData.auditLogs) { log -> ListItem(headlineContent = { Text(log.action) }, supportingContent = { Text(log.actor) }, trailingContent = { Text(log.time) }) }
        }
    }
}

@Composable
fun ReportDashboardScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Reports", onBack)
        GradientHero("Average Score 7.6", "Top scores, per-question accuracy, and candidate outcomes")
        SectionTitle("Mock Chart")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(120.dp)) {
            listOf(.45f, .72f, .88f, .61f, .78f).forEach { height ->
                Card(Modifier.weight(1f).fillMaxHeight(height), colors = CardDefaults.cardColors(AppBlue.copy(alpha = .75f))) {}
            }
        }
        SectionTitle("Top Scores")
        MockData.results.forEach {
            ListItem(headlineContent = { Text(it.exam) }, supportingContent = { Text("${it.correct} correct, ${it.wrong} wrong, ${it.blank} blank") }, trailingContent = { Text(it.score.toString()) })
        }
        PrimaryAction("Export Excel/PDF")
    }
}
