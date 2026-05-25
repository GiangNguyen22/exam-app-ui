package com.internalexam.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internalexam.model.mock.CandidateStatus
import com.internalexam.model.mock.Difficulty
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.NetworkState
import com.internalexam.model.mock.QuestionType
import com.internalexam.ui.components.*
import com.internalexam.ui.theme.*

@Composable
fun TeacherDashboardScreen(openQuestions: () -> Unit, openCreateExam: () -> Unit, openGenerate: () -> Unit, openMonitor: () -> Unit, openReports: () -> Unit) {
    AppBackground {
        Spacer(Modifier.height(18.dp))
        GradientHero("Teacher Dashboard", "Theo doi de thi, cau hoi va gian lan realtime") { StatusPill(NetworkState.ONLINE) }
        SectionTitle("Tong quan")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("De thi", "12", "3 dang dien ra", AppBlue, Icons.Default.Assessment)
                MetricCard("Canh bao", "4", "Realtime", AppRed, Icons.Default.AutoAwesome)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Cau hoi", "428", "Da phan loai", AppViolet, Icons.Default.QuestionAnswer)
                MetricCard("Dong bo", "Synced", "10:35", AppMint)
            }
        }
        SectionTitle("Shortcut")
        listOf(
            "Quan ly cau hoi" to openQuestions,
            "Tao de thi" to openCreateExam,
            "Sinh de tu dong" to openGenerate,
            "Theo doi truc tiep" to openMonitor,
            "Bao cao thong ke" to openReports
        ).forEach { (label, action) -> OutlinedButton(onClick = action, modifier = Modifier.fillMaxWidth()) { Text(label) } }
    }
}

@Composable
fun QuestionBankScreen(onCreate: () -> Unit, onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Ngan hang cau hoi", onBack)
        OutlinedTextField("", {}, modifier = Modifier.fillMaxWidth(), label = { Text("Search cau hoi") })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 12.dp)) {
            ChipText("Android"); ChipText("Compose"); ChipText("Medium"); ChipText("SINGLE")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 90.dp)) {
            items(MockData.questions) { q ->
                Card(shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(16.dp)) {
                        Text(q.content, fontWeight = FontWeight.Bold)
                        Text("${q.subject} • ${q.topic} • ${q.difficulty} • ${q.type}", color = AppMuted)
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onCreate, modifier = Modifier.weight(1f)) { Icon(Icons.Default.Add, null); Text("Them") }
            OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Import Excel") }
        }
    }
}

@Composable
fun CreateQuestionScreen(onBack: () -> Unit) {
    var type by remember { mutableStateOf(QuestionType.SINGLE) }
    AppBackground {
        ExamTopBar("Tao cau hoi", onBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                OutlinedTextField("", {}, modifier = Modifier.fillMaxWidth().height(120.dp), label = { Text("Noi dung cau hoi") })
                OutlinedTextField("Android", {}, modifier = Modifier.fillMaxWidth(), label = { Text("Mon hoc") })
                OutlinedTextField("Jetpack Compose", {}, modifier = Modifier.fillMaxWidth(), label = { Text("Chu de") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Difficulty.entries.forEach { ChipText(it.name, if (it == Difficulty.MEDIUM) AppBlue else AppMuted) } }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { QuestionType.entries.forEach { FilterChip(selected = type == it, onClick = { type = it }, label = { Text(it.name) }) } }
                repeat(4) { i -> OutlinedTextField("", {}, modifier = Modifier.fillMaxWidth(), label = { Text("Dap an ${'A' + i}") }) }
                OutlinedTextField("A", {}, modifier = Modifier.fillMaxWidth(), label = { Text("Dap an dung") })
                OutlinedTextField("", {}, modifier = Modifier.fillMaxWidth().height(100.dp), label = { Text("Giai thich") })
                Spacer(Modifier.height(12.dp)); PrimaryAction("Luu cau hoi")
            }
        }
    }
}

@Composable
fun CreateExamScreen(onGenerate: () -> Unit, onBack: () -> Unit) {
    var randomQuestion by remember { mutableStateOf(true) }
    var randomAnswer by remember { mutableStateOf(true) }
    AppBackground {
        ExamTopBar("Tao de thi", onBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                listOf("Ten de thi", "Mon hoc", "Thoi luong", "Thoi gian mo", "Thoi gian dong", "So cau", "Diem moi cau").forEach { OutlinedTextField("", {}, modifier = Modifier.fillMaxWidth(), label = { Text(it) }) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Random cau hoi"); Switch(randomQuestion, { randomQuestion = it }) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Random dap an"); Switch(randomAnswer, { randomAnswer = it }) }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Thu cong") }
                    Button(onClick = onGenerate, modifier = Modifier.weight(1f)) { Text("Tu dong") }
                }
            }
        }
    }
}

@Composable
fun AutoGenerateExamScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Sinh de tu dong", onBack)
        OutlinedTextField("Android", {}, modifier = Modifier.fillMaxWidth(), label = { Text("Mon hoc") })
        OutlinedTextField("Compose", {}, modifier = Modifier.fillMaxWidth(), label = { Text("Chu de") })
        SectionTitle("So cau theo do kho")
        listOf("De" to "10", "Trung binh" to "15", "Kho" to "5").forEach { (label, value) -> OutlinedTextField(value, {}, modifier = Modifier.fillMaxWidth(), label = { Text(label) }) }
        Card(shape = MaterialTheme.shapes.large) { Text("Preview: 30 cau • 45 phut • random cau hoi va dap an • diem moi cau 0.33", modifier = Modifier.padding(16.dp)) }
        PrimaryAction("Sinh de")
    }
}

@Composable
fun LiveMonitoringScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Theo doi truc tiep", onBack)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
            item { SectionTitle("Thi sinh dang lam bai") }
            items(MockData.candidates) { c ->
                val color = when (c.status) { CandidateStatus.DOING -> AppBlue; CandidateStatus.LOST_CONNECTION -> AppAmber; CandidateStatus.SUBMITTED -> AppMint; CandidateStatus.FLAGGED -> AppRed }
                Card(shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(c.name, fontWeight = FontWeight.Bold); ChipText(c.status.name, color) }
                        Text("${c.progress}% • ${c.device}", color = AppMuted)
                    }
                }
            }
            item { SectionTitle("Realtime log") }
            items(MockData.auditLogs) { log -> ListItem(headlineContent = { Text(log.action) }, supportingContent = { Text(log.actor) }, trailingContent = { Text(log.time) }) }
        }
    }
}

@Composable
fun ReportDashboardScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Bao cao", onBack)
        GradientHero("Diem trung binh 7.6", "Top diem cao, ty le dung tung cau va ket qua thi sinh")
        SectionTitle("Bieu do mock")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(120.dp)) {
            listOf(.45f, .72f, .88f, .61f, .78f).forEach { h -> Card(Modifier.weight(1f).fillMaxHeight(h), colors = CardDefaults.cardColors(AppBlue.copy(alpha = .75f))) {} }
        }
        SectionTitle("Top diem cao")
        MockData.results.forEach { ListItem(headlineContent = { Text(it.exam) }, supportingContent = { Text("Dung ${it.correct}, sai ${it.wrong}, trong ${it.blank}") }, trailingContent = { Text(it.score.toString()) }) }
        PrimaryAction("Xuat Excel/PDF")
    }
}
