package com.internalexam.ui.student

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.ExamResponse
import com.internalexam.data.network.ResultQuestionResponse
import com.internalexam.ui.components.ChipText
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

@Composable
fun EmptyExamState() {
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

@Composable
fun EmptyResultsState() {
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

@Composable
fun StudentExamRoomCard(
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

@Composable
fun StudentResultCard(item: StudentRecentResult, onClick: () -> Unit) {
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

@Composable
fun AnswerDetailRow(label: String, value: String, valueColor: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(
            label,
            color = AppMuted,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(100.dp)
        )
        Text(
            value,
            color = valueColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

sealed class ExplainState {
    object Idle : ExplainState()
    object Loading : ExplainState()
    data class Success(val explanation: String) : ExplainState()
    data class Error(val message: String) : ExplainState()
}

@Composable
fun AiExplanableQuestionCard(
    question: ResultQuestionResponse,
    subject: String?
) {
    var explainState by remember { mutableStateOf<ExplainState>(ExplainState.Idle) }

    val correctAnswerText = question.answers
        ?.filter { it.correct == true }
        ?.joinToString(", ") { it.content }
        ?: ""

    val studentAnswerText = run {
        val selectedIds = question.selectedAnswerIds.orEmpty().toSet()
        if (selectedIds.isEmpty() && question.fillContent != null) {
            question.fillContent
        } else {
            question.answers
                ?.filter { it.id in selectedIds }
                ?.joinToString(", ") { it.content }
                ?: ""
        }
    }

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val isCorrect = question.correct == true
            val statusColor = if (isCorrect) AppMint else AppRed
            val statusLabel = if (question.blank == true) "Bỏ trống" else if (isCorrect) "Đúng" else "Sai"

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Câu ${question.orderIndex?.takeIf { it > 0 } ?: "?"}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = CircleShape,
                    modifier = Modifier.border(1.dp, statusColor.copy(alpha = 0.35f), CircleShape)
                ) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (statusLabel == "Đúng") Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            statusLabel,
                            color = statusColor,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                question.content,
                color = AppText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 2.dp)
            )

            if (studentAnswerText.isNotBlank()) {
                AnswerDetailRow(
                    label = "Bài làm",
                    value = studentAnswerText,
                    valueColor = if (isCorrect) AppMint else AppCoral
                )
            } else if (question.fillContent != null) {
                AnswerDetailRow("Bài làm", question.fillContent.orEmpty(), if (isCorrect) AppMint else AppCoral)
            }

            if (question.correct != true && correctAnswerText.isNotBlank()) {
                AnswerDetailRow(label = "Đáp án đúng", value = correctAnswerText, valueColor = AppMint)
            }

            when (val state = explainState) {
                is ExplainState.Idle -> {
                    OutlinedButton(
                        onClick = { explainState = ExplainState.Loading },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Giải thích bằng AI")
                    }
                }
                is ExplainState.Loading -> {
                    Surface(
                        color = AppIndigo.copy(alpha = 0.06f),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppIndigo.copy(alpha = 0.2f), MaterialTheme.shapes.medium)
                    ) {
                        Row(
                            Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.5.dp,
                                color = AppIndigo
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Đang tạo giải thích...", color = AppIndigo, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                is ExplainState.Error -> {
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = AppRed.copy(alpha = 0.06f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppRed.copy(alpha = 0.25f), MaterialTheme.shapes.medium)
                    ) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ErrorOutline, null, tint = AppRed, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Không thể giải thích", color = AppRed, fontWeight = FontWeight.Bold)
                            }
                            Text(state.message, color = AppMuted, style = MaterialTheme.typography.bodySmall)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { explainState = ExplainState.Idle }) {
                                    Text("Thử lại")
                                }
                            }
                        }
                    }
                }
                is ExplainState.Success -> {
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = AppIndigo.copy(alpha = 0.07f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppIndigo.copy(alpha = 0.22f), MaterialTheme.shapes.medium)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = AppIndigo.copy(alpha = 0.14f),
                                    shape = CircleShape,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PhoneAndroid, null, tint = AppIndigo, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(Modifier.width(10.dp))
                                Text("Giải thích AI", color = AppIndigo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            }
                            HorizontalDivider(color = AppIndigo.copy(alpha = 0.15f))
                            Text(
                                state.explanation,
                                color = AppText,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { explainState = ExplainState.Idle }) {
                                    Text("Ẩn", color = AppIndigo)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (explainState is ExplainState.Loading) {
        LaunchedEffect(question.questionId) {
            explainState = ExplainState.Loading
            val authorization = SessionManager.authorizationHeader()
            if (authorization == null) {
                explainState = ExplainState.Error("Vui lòng đăng nhập lại.")
                return@LaunchedEffect
            }
            val request = com.internalexam.data.network.AiExplainRequest(
                question = question.content,
                correctAnswer = correctAnswerText.ifBlank { null },
                studentAnswer = studentAnswerText.ifBlank { null },
                correct = question.correct == true,
                subject = subject
            )
            explainState = try {
                val response = ApiClient.explainAnswer(authorization, request)
                val text = response.data?.explanation
                if (text != null) ExplainState.Success(text)
                else ExplainState.Error("Không thể tạo giải thích lúc này.")
            } catch (e: Exception) {
                ExplainState.Error("Lỗi kết nối. Vui lòng thử lại.")
            }
        }
    }
}
