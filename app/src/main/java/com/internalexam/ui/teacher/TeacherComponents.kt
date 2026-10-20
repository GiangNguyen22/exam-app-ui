package com.internalexam.ui.teacher

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.ExamReportItemResponse
import com.internalexam.data.network.ExamResponse
import com.internalexam.data.network.QuestionResponse
import com.internalexam.data.network.StudentGroupResponse
import com.internalexam.data.network.SubjectResponse
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppSurface
import com.internalexam.ui.theme.AppText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConfirmActionDialog(
    title: String,
    message: String,
    confirmText: String = "Xác nhận",
    dismissText: String = "Hủy",
    isDestructive: Boolean = false,
    loading: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text(confirmText, color = if (isDestructive) AppRed else AppIndigo, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !loading
            ) {
                Text(dismissText)
            }
        }
    )
}

@Composable
fun AnswerOptionInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isCorrect: Boolean,
    isMultiChoice: Boolean,
    canRemove: Boolean,
    onToggleCorrect: () -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        color = AppSurface,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                1.dp,
                if (isCorrect) AppIndigo else AppCardBorder,
                MaterialTheme.shapes.medium
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isMultiChoice) {
                Checkbox(
                    checked = isCorrect,
                    onCheckedChange = { onToggleCorrect() },
                    colors = CheckboxDefaults.colors(checkedColor = AppIndigo)
                )
            } else {
                RadioButton(
                    selected = isCorrect,
                    onClick = onToggleCorrect,
                    colors = RadioButtonDefaults.colors(selectedColor = AppIndigo)
                )
            }
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.small
            )
            if (canRemove) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Close, contentDescription = "Xóa", tint = AppRed)
                }
            }
        }
    }
}

@Composable
fun <T> CatalogDropdown(
    label: String,
    placeholder: String,
    loading: Boolean,
    items: List<T>,
    itemLabel: (T) -> String,
    onSelect: (T) -> Unit,
    selectedLabel: String?,
    onQuickAdd: (() -> Unit)? = null,
    quickAddLabel: String? = null,
    onManageClick: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row {
                if (onQuickAdd != null && quickAddLabel != null) {
                    TextButton(onClick = onQuickAdd) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(quickAddLabel, style = MaterialTheme.typography.labelSmall)
                    }
                }
                if (onManageClick != null) {
                    TextButton(onClick = onManageClick) {
                        Text("Quản lý", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedLabel ?: placeholder,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (!loading) expanded = true },
                trailingIcon = {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                },
                shape = MaterialTheme.shapes.medium
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (items.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("Chưa có dữ liệu", color = AppMuted) },
                        onClick = { expanded = false }
                    )
                } else {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(itemLabel(item)) },
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
}

@Composable
fun QuickAddDialog(
    title: String,
    label: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    loading: Boolean = false,
    isError: Boolean = false
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (text.isNotBlank()) onConfirm(text) },
                enabled = text.isNotBlank() && !loading
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Text("Tạo mới", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !loading) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun GroupSelector(
    groups: List<StudentGroupResponse>,
    loading: Boolean,
    selectedGroupIds: Set<Long>,
    onSelectionChanged: (Set<Long>) -> Unit,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        Text("Áp dụng cho nhóm/lớp", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = if (selectedGroupIds.isEmpty()) "Tất cả học sinh" else "Đã chọn ${selectedGroupIds.size} nhóm/lớp",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (!loading) expanded = true },
                trailingIcon = {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                },
                shape = MaterialTheme.shapes.medium
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedGroupIds.isEmpty(),
                                onCheckedChange = {
                                    onSelectionChanged(emptySet())
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Tất cả học sinh", fontWeight = FontWeight.Bold)
                        }
                    },
                    onClick = {
                        onSelectionChanged(emptySet())
                    }
                )
                groups.forEach { group ->
                    val isChecked = group.id in selectedGroupIds
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        val newSet = selectedGroupIds.toMutableSet()
                                        if (checked) newSet.add(group.id) else newSet.remove(group.id)
                                        onSelectionChanged(newSet)
                                    }
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(group.name)
                            }
                        },
                        onClick = {
                            val newSet = selectedGroupIds.toMutableSet()
                            if (isChecked) newSet.remove(group.id) else newSet.add(group.id)
                            onSelectionChanged(newSet)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DateTimeField(
    dateMillis: Long?,
    hour: Int,
    minute: Int,
    label: String,
    modifier: Modifier = Modifier,
    onPickDate: () -> Unit,
    onPickTime: () -> Unit
) {
    val dateText = dateMillis?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "Chọn ngày"
    val timeText = "%02d:%02d".format(hour, minute)

    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onPickDate,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(dateText, style = MaterialTheme.typography.bodySmall)
            }
            OutlinedButton(
                onClick = onPickTime,
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(timeText, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ReportResultRow(item: ExamReportItemResponse) {
    val scoreColor = reportStatusColor(item.status)
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
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
                    reportScore(item.score),
                    fontWeight = FontWeight.Bold,
                    color = scoreColor,
                    fontSize = 15.sp
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(item.studentName ?: "Học sinh", fontWeight = FontWeight.SemiBold, color = AppText)
                Text("Mã HS: ${item.username ?: item.studentCode ?: "--"}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
            }
            Text(reportDate(item.submittedAt), color = AppMuted, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun SelectBankQuestionsDialog(
    exam: ExamResponse,
    onDismiss: () -> Unit,
    onAdded: () -> Unit
) {
    val context = LocalContext.current
    var bankQuestions by remember { mutableStateOf<List<QuestionResponse>>(emptyList()) }
    var existingQuestionContents by remember { mutableStateOf<Set<String>>(emptySet()) }
    var subjects by remember { mutableStateOf<List<SubjectResponse>>(emptyList()) }
    var selectedSubjectId by remember { mutableLongStateOf(-1L) }
    var searchQuery by remember { mutableStateOf("") }
    val selectedIds = remember { mutableStateListOf<Long>() }
    var isLoading by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(exam.id) {
        val auth = SessionManager.authorizationHeader()
        if (auth == null) {
            errorMessage = "Vui lòng đăng nhập lại."
            isLoading = false
            return@LaunchedEffect
        }
        isLoading = true
        try {
            val qRes = ApiClient.getQuestions(auth)
            bankQuestions = qRes.data.orEmpty()

            val existingRes = ApiClient.getExamQuestions(auth, exam.id)
            existingQuestionContents = existingRes.data.orEmpty()
                .map { it.content.trim().lowercase() }
                .toSet()

            val sRes = ApiClient.getSubjects(auth)
            subjects = sRes.data.orEmpty()

            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Không thể tải danh sách câu hỏi từ kho."
        } finally {
            isLoading = false
        }
    }

    val scope = rememberCoroutineScope()

    fun submitAddQuestions() {
        if (selectedIds.isEmpty()) return
        isSubmitting = true
        scope.launch {
            val auth = SessionManager.authorizationHeader()
            if (auth == null) {
                Toast.makeText(context, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
                isSubmitting = false
                return@launch
            }
            val questionsToAdd = bankQuestions.filter { it.id in selectedIds }
            var successCount = 0
            var failCount = 0

            questionsToAdd.forEach { q ->
                val req = com.internalexam.data.network.ExamQuestionCreateRequest(
                    subjectId = q.subjectId ?: 1L,
                    topicId = q.topicId,
                    content = q.content,
                    type = q.type ?: "SINGLE",
                    difficulty = q.difficulty ?: "EASY",
                    imageUrl = q.imageUrl,
                    orderIndex = null,
                    score = exam.scorePerQuestion,
                    answers = q.answers.orEmpty().map { a ->
                        com.internalexam.data.network.AnswerCreateRequest(
                            content = a.content,
                            correct = a.correct == true,
                            explanation = a.explanation
                        )
                    }
                )
                try {
                    val res = ApiClient.createQuestionForExam(auth, exam.id, req)
                    if (res.success) successCount++ else failCount++
                } catch (_: Exception) {
                    failCount++
                }
            }

            isSubmitting = false
            if (successCount > 0) {
                Toast.makeText(context, "Đã thêm $successCount câu hỏi vào đề thi!", Toast.LENGTH_SHORT).show()
                onAdded()
            } else {
                Toast.makeText(context, "Lỗi khi thêm câu hỏi vào đề thi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = {
            Column {
                Text("Chọn câu hỏi từ kho", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Đề thi: ${exam.title}", color = AppMuted, style = MaterialTheme.typography.bodySmall)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tìm kiếm nội dung câu hỏi...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )

                if (subjects.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Lọc theo môn:", style = MaterialTheme.typography.labelMedium)
                        var subjectExpanded by remember { mutableStateOf(false) }
                        Box {
                            TextButton(onClick = { subjectExpanded = true }) {
                                Text(
                                    subjects.find { it.id == selectedSubjectId }?.name ?: "Tất cả môn",
                                    color = AppIndigo,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(Icons.Default.ArrowDropDown, null, tint = AppIndigo)
                            }
                            DropdownMenu(
                                expanded = subjectExpanded,
                                onDismissRequest = { subjectExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Tất cả môn học") },
                                    onClick = {
                                        selectedSubjectId = -1L
                                        subjectExpanded = false
                                    }
                                )
                                subjects.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text(s.name) },
                                        onClick = {
                                            selectedSubjectId = s.id
                                            subjectExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (isLoading) {
                    Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AppIndigo)
                    }
                } else if (errorMessage != null) {
                    Text(errorMessage!!, color = AppRed, style = MaterialTheme.typography.bodySmall)
                } else {
                    val filteredList = bankQuestions.filter { q ->
                        val matchesSubject = selectedSubjectId <= 0 || q.subjectId == selectedSubjectId
                        val matchesSearch = searchQuery.isBlank() || q.content.contains(searchQuery, ignoreCase = true)
                        matchesSubject && matchesSearch
                    }

                    if (filteredList.isEmpty()) {
                        Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            Text("Không tìm thấy câu hỏi phù hợp.", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Đã chọn: ${selectedIds.size}/${filteredList.size}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            TextButton(onClick = {
                                if (selectedIds.size == filteredList.size) {
                                    selectedIds.clear()
                                } else {
                                    selectedIds.clear()
                                    selectedIds.addAll(filteredList.map { it.id })
                                }
                            }) {
                                Text(if (selectedIds.size == filteredList.size) "Bỏ chọn" else "Chọn tất cả", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredList) { q ->
                                val isAlreadyInExam = existingQuestionContents.contains(q.content.trim().lowercase())
                                val isChecked = selectedIds.contains(q.id)

                                Card(
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isChecked) AppIndigo.copy(alpha = 0.08f) else AppSurface
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.dp,
                                            if (isChecked) AppIndigo else AppCardBorder,
                                            MaterialTheme.shapes.medium
                                        )
                                        .clickable {
                                            if (isChecked) selectedIds.remove(q.id) else selectedIds.add(q.id)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                if (checked) selectedIds.add(q.id) else selectedIds.remove(q.id)
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = AppIndigo)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                q.content,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (isAlreadyInExam) {
                                                Spacer(Modifier.height(2.dp))
                                                Text(
                                                    "• Đã có trong đề thi này",
                                                    color = AppAmber,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { submitAddQuestions() },
                enabled = selectedIds.isNotEmpty() && !isSubmitting && !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text("Thêm (${selectedIds.size}) câu hỏi")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                Text("Hủy")
            }
        }
    )
}
