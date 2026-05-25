package com.internalexam.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.NetworkState
import com.internalexam.ui.components.*
import com.internalexam.ui.theme.*

@Composable
fun StudentHomeScreen(onLobby: () -> Unit, onResult: () -> Unit) {
    AppBackground {
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text("Xin chao thi sinh", color = AppMuted); Text(MockData.currentStudent.name, style = MaterialTheme.typography.headlineMedium) }
            StatusPill(NetworkState.ONLINE)
        }
        SectionTitle("Phong thi hom nay", "De da tai ve may co the tiep tuc khi mat mang")
        MockData.exams.forEach { exam ->
            Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(exam.title, style = MaterialTheme.typography.titleLarge)
                    Text("${exam.subject} • ${exam.duration} phut • ${exam.questions} cau", color = AppMuted)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ChipText("Mo ${exam.opens}")
                        ChipText(if (exam.downloaded) "Da tai de" else "Chua tai de", if (exam.downloaded) AppMint else AppAmber)
                    }
                    Spacer(Modifier.height(12.dp))
                    PrimaryAction("Vao phong thi", onClick = onLobby)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        SectionTitle("Ket qua gan day")
        MockData.results.forEach { r ->
            ListItem(headlineContent = { Text(r.exam) }, supportingContent = { Text(r.status) }, trailingContent = { Text(if (r.score > 0) r.score.toString() else "--", fontWeight = FontWeight.Bold) })
        }
        TextButton(onClick = onResult) { Text("Xem man hinh ket qua mau") }
    }
}

@Composable
fun ExamLobbyScreen(onStart: () -> Unit, onBack: () -> Unit) {
    val exam = MockData.exams.first()
    AppBackground {
        ExamTopBar("Phong thi", onBack)
        GradientHero(exam.title, "${exam.subject} • ${exam.duration} phut • ${exam.questions} cau") { StatusPill(NetworkState.SYNCED) }
        SectionTitle("Thoi gian", "Mo ${exam.opens} - dong ${exam.closes}")
        MetricCard("Trang thai de", if (exam.downloaded) "Da tai ve may" else "Chua tai", "Co the lam bai offline sau khi tai", AppMint)
        Card(shape = MaterialTheme.shapes.large) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Quy dinh thi", style = MaterialTheme.typography.titleMedium)
                Text("Khong thoat app, khong chup man hinh, moi thiet bi chi dang nhap mot phien.", color = AppMuted)
                Text("He thong se ghi log APP_EXIT, SCREENSHOT, LOST_CONNECTION, FOCUS_LOST.", color = AppMuted)
            }
        }
        Spacer(Modifier.weight(1f))
        PrimaryAction("Bat dau lam bai", onClick = onStart)
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
fun ExamTakingScreen(onSubmit: () -> Unit, onBack: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    val question = MockData.questions[index]
    val answered = remember { mutableStateListOf(1, 2) }
    AppBackground {
        ExamTopBar("Kiem tra Kotlin", onBack)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            ChipText("Con 32:18", AppRed)
            StatusPill(NetworkState.SYNCING)
        }
        Spacer(Modifier.height(10.dp))
        Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Cau ${question.id}/${MockData.questions.size}", color = AppMuted)
                    Text("Da luu luc 10:32", color = AppMint)
                }
                Spacer(Modifier.height(10.dp))
                Text(question.content, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(14.dp))
                question.answers.forEach { answer ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp).border(1.dp, Color(0xFFE2E8F0), MaterialTheme.shapes.medium).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (question.type.name == "MULTI") Checkbox(answer.correct, {}) else RadioButton(answer.correct, {})
                        Text("${answer.id}. ${answer.text}", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
        Surface(color = AppRed.copy(alpha = .1f), shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(top = 12.dp)) { Text("Canh bao mock: neu mat mang, bai lam van duoc luu cuc bo va se dong bo lai.", color = AppRed, modifier = Modifier.padding(12.dp)) }
        SectionTitle("Chuyen cau nhanh")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MockData.questions.forEachIndexed { i, q ->
                val color = when { i == index -> AppBlue; answered.contains(q.id) -> AppMint; else -> AppMuted }
                Box(Modifier.size(40.dp).background(color.copy(alpha = .14f), CircleShape).border(1.dp, color, CircleShape), contentAlignment = Alignment.Center) { Text(q.id.toString(), color = color, fontWeight = FontWeight.Bold) }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { if (index > 0) index-- }, modifier = Modifier.weight(1f)) { Text("Cau truoc") }
            Button(onClick = { if (index < MockData.questions.lastIndex) index++ }, modifier = Modifier.weight(1f)) { Text("Cau sau") }
        }
        Spacer(Modifier.weight(1f))
        PrimaryAction("Nop bai", onClick = onSubmit)
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
fun SubmitConfirmationScreen(onConfirm: () -> Unit, onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Xac nhan nop bai", onBack)
        MetricCard("Da lam", "24", "Tong so cau da tra loi", AppMint, Icons.Default.Flag)
        Spacer(Modifier.height(12.dp))
        MetricCard("Chua lam", "6", "Nen kiem tra lai truoc khi nop", AppAmber, Icons.Default.Timer)
        Spacer(Modifier.height(12.dp))
        Card(shape = MaterialTheme.shapes.large) { Text("Sau khi nop bai, thi sinh khong the sua cau tra loi. He thong se dong bo bai lam khi co internet.", modifier = Modifier.padding(16.dp), color = AppMuted) }
        Spacer(Modifier.weight(1f))
        PrimaryAction("Nop bai", onClick = onConfirm)
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Text("Quay lai kiem tra") }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
fun ResultScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Ket qua", onBack)
        GradientHero("8.5 / 10", "Da cham • 17 dung • 2 sai • 1 bo trong")
        SectionTitle("Chi tiet dap an")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
            items(MockData.questions) { q ->
                Card(shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Cau ${q.id}: ${q.content}", fontWeight = FontWeight.Bold)
                        Text("Dap an dung: ${q.answers.filter { it.correct }.joinToString { it.id }}", color = AppMint)
                        Text(q.explanation, color = AppMuted)
                    }
                }
            }
        }
    }
}
