package com.internalexam.ui.teacher

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internalexam.data.network.ExamReportItemResponse
import com.internalexam.data.network.StudentGroupResponse
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppSurface
import com.internalexam.ui.theme.AppText
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
