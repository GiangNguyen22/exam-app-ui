package com.internalexam.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internalexam.data.ExamAttemptStore
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.ExamResultResponse
import com.internalexam.data.network.ExamSubmitRequest
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.NetworkState
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
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun StudentHomeScreen(onLobby: () -> Unit, onResult: () -> Unit) {
    AppBackground {
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Welcome back", color = AppMuted)
                Text(MockData.currentStudent.name, style = MaterialTheme.typography.headlineMedium)
            }
            StatusPill(NetworkState.ONLINE)
        }
        SectionTitle("Today's Exam Rooms", "Downloaded exams can continue when the network is unavailable.")
        MockData.exams.forEach { exam ->
            Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(exam.title, style = MaterialTheme.typography.titleLarge)
                    Text("${exam.subject} - ${exam.duration} min - ${exam.questions} questions", color = AppMuted)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChipText("Opens ${exam.opens}")
                        ChipText(if (exam.downloaded) "Downloaded" else "Not downloaded", if (exam.downloaded) AppMint else AppAmber)
                    }
                    Spacer(Modifier.height(12.dp))
                    PrimaryAction("Enter Exam Room", onClick = onLobby)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        SectionTitle("Recent Results")
        MockData.results.forEach { result ->
            ListItem(
                headlineContent = { Text(result.exam) },
                supportingContent = { Text(result.status) },
                trailingContent = { Text(if (result.score > 0) result.score.toString() else "--", fontWeight = FontWeight.Bold) }
            )
        }
        TextButton(onClick = onResult) { Text("Open sample result screen") }
    }
}

@Composable
fun ExamLobbyScreen(onStart: () -> Unit, onBack: () -> Unit) {
    val exam = MockData.exams.first()
    AppBackground {
        ExamTopBar("Exam Room", onBack)
        GradientHero(exam.title, "${exam.subject} - ${exam.duration} min - ${exam.questions} questions") { StatusPill(NetworkState.SYNCED) }
        SectionTitle("Schedule", "Opens ${exam.opens} - closes ${exam.closes}")
        MetricCard("Exam package", if (exam.downloaded) "Downloaded" else "Not ready", "Available offline after download", AppMint)
        Card(shape = MaterialTheme.shapes.large) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Exam Rules", style = MaterialTheme.typography.titleMedium)
                Text("Do not leave the app, take screenshots, or sign in from multiple devices.", color = AppMuted)
                Text("The system records APP_EXIT, SCREENSHOT, LOST_CONNECTION, and FOCUS_LOST events.", color = AppMuted)
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
    val question = MockData.questions[index]
    AppBackground {
        ExamTopBar("Kotlin Test", onBack)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            ChipText("32:18 left", AppRed)
            StatusPill(NetworkState.SYNCING)
        }
        Spacer(Modifier.height(10.dp))
        Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Question ${question.id}/${MockData.questions.size}", color = AppMuted)
                    Text("Saved at 10:32", color = AppMint)
                }
                Spacer(Modifier.height(10.dp))
                Text(question.content, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(14.dp))
                question.answers.forEach { answer ->
                    val selected = ExamAttemptStore.selectedAnswerIds(question.id).contains(answer.id)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(1.dp, if (selected) AppBlue else Color(0xFFE2E8F0), MaterialTheme.shapes.medium)
                            .clickable { ExamAttemptStore.selectAnswer(question.id, answer.id, question.type) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (question.type.name == "MULTI") {
                            Checkbox(selected, { ExamAttemptStore.selectAnswer(question.id, answer.id, question.type) })
                        } else {
                            RadioButton(selected, { ExamAttemptStore.selectAnswer(question.id, answer.id, question.type) })
                        }
                        Text("${answer.id}. ${answer.text}", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
        Surface(color = AppRed.copy(alpha = .1f), shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(top = 12.dp)) {
            Text("Mock warning: if the network is lost, answers remain stored locally and sync later.", color = AppRed, modifier = Modifier.padding(12.dp))
        }
        SectionTitle("Quick Navigation")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MockData.questions.forEachIndexed { i, q ->
                val color = when {
                    i == index -> AppBlue
                    ExamAttemptStore.isAnswered(q.id) -> AppMint
                    else -> AppMuted
                }
                Box(
                    Modifier
                        .size(40.dp)
                        .background(color.copy(alpha = .14f), CircleShape)
                        .border(1.dp, color, CircleShape)
                        .clickable { index = i },
                    contentAlignment = Alignment.Center
                ) {
                    Text(q.id.toString(), color = color, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { if (index > 0) index-- }, modifier = Modifier.weight(1f)) { Text("Previous") }
            Button(onClick = { if (index < MockData.questions.lastIndex) index++ }, modifier = Modifier.weight(1f)) { Text("Next") }
        }
        Spacer(Modifier.weight(1f))
        PrimaryAction("Submit Exam", onClick = onSubmit)
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
fun SubmitConfirmationScreen(onConfirm: () -> Unit, onBack: () -> Unit) {
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun submit() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Please sign in again."
            return
        }
        scope.launch {
            loading = true
            message = null
            try {
                ApiClient.submitExam(
                    authorization,
                    ExamAttemptStore.backendExamId,
                    ExamSubmitRequest("Submitted from Android app with ${ExamAttemptStore.answeredCount} answered questions.")
                )
                onConfirm()
            } catch (exception: HttpException) {
                message = when (exception.code()) {
                    404 -> "Backend exam #${ExamAttemptStore.backendExamId} was not found. Showing local result only."
                    401, 403 -> "You do not have permission to submit this exam."
                    else -> "Backend error ${exception.code()}."
                }
            } catch (exception: Exception) {
                message = "Cannot connect to backend. Showing local result only."
            } finally {
                loading = false
            }
        }
    }

    AppBackground {
        ExamTopBar("Confirm Submission", onBack)
        MetricCard("Answered", ExamAttemptStore.answeredCount.toString(), "Total answered questions", AppMint, Icons.Default.Flag)
        Spacer(Modifier.height(12.dp))
        MetricCard("Unanswered", ExamAttemptStore.unansweredCount.toString(), "Review before submitting", AppAmber, Icons.Default.Timer)
        Spacer(Modifier.height(12.dp))
        Card(shape = MaterialTheme.shapes.large) {
            Text("After submission, answers cannot be changed. The system will sync the attempt when internet is available.", modifier = Modifier.padding(16.dp), color = AppMuted)
        }
        if (message != null) Text(message.orEmpty(), color = AppRed, modifier = Modifier.padding(top = 12.dp))
        Spacer(Modifier.weight(1f))
        PrimaryAction(if (loading) "Submitting..." else "Submit Exam", onClick = { if (!loading) submit() })
        OutlinedButton(onClick = onConfirm, modifier = Modifier.fillMaxWidth().padding(top = 10.dp), enabled = !loading) { Text("Show Local Result") }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 10.dp), enabled = !loading) { Text("Back to Exam") }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
fun ResultScreen(onBack: () -> Unit) {
    val score = ExamAttemptStore.score()
    var backendResult by remember { mutableStateOf<ExamResultResponse?>(null) }
    var backendMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization != null) {
            try {
                val response = ApiClient.getResult(authorization, ExamAttemptStore.backendExamId)
                backendResult = response.data
                backendMessage = if (response.success) "Backend result loaded." else response.message
            } catch (exception: Exception) {
                backendMessage = "Local result shown. Backend result is not available."
            }
        }
    }

    AppBackground {
        ExamTopBar("Results", onBack)
        GradientHero("%.1f / 10".format(score.score), "Graded - ${score.correct} correct - ${score.wrong} incorrect - ${score.blank} blank")
        if (backendMessage != null) {
            Text(backendMessage.orEmpty(), color = if (backendResult != null) AppMint else AppMuted, modifier = Modifier.padding(top = 12.dp))
        }
        if (backendResult != null) {
            Text("Backend status: ${backendResult?.status} - submitted at ${backendResult?.submittedAt ?: "--"}", color = AppMuted)
        }
        SectionTitle("Answer Details")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
            items(MockData.questions) { question ->
                Card(shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Question ${question.id}: ${question.content}", fontWeight = FontWeight.Bold)
                        Text("Your answer: ${ExamAttemptStore.selectedAnswerIds(question.id).ifEmpty { setOf("--") }.joinToString()}", color = AppMuted)
                        Text("Correct answer: ${question.answers.filter { it.correct }.joinToString { it.id }}", color = AppMint)
                        Text(question.explanation, color = AppMuted)
                    }
                }
            }
        }
    }
}
