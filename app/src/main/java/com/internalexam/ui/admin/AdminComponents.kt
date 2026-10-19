package com.internalexam.ui.admin

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internalexam.data.network.AuditLogResponse
import com.internalexam.data.network.UserCreateRequest
import com.internalexam.data.network.UserProfileResponse
import com.internalexam.model.mock.Role
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppSurface
import com.internalexam.ui.theme.AppText

@Composable
fun AuditLogCard(item: AuditLogResponse) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, null, tint = AppIndigo, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.height(0.dp))
                    Text(item.action, fontWeight = FontWeight.Bold, color = AppText)
                }
                Text(formatAuditTime(item.createdAt), color = AppMuted, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(6.dp))
            Text("Thực hiện bởi: ${item.username ?: "Hệ thống"}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
            item.details?.let {
                Spacer(Modifier.height(4.dp))
                Text("Chi tiết: $it", color = AppMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun CreateUserDialog(
    loading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (UserCreateRequest) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var employeeCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text("Tạo tài khoản mới", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Tên đăng nhập") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Họ và tên") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Mã học sinh (nếu có)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (username.isNotBlank() && password.isNotBlank() && fullName.isNotBlank()) {
                        onConfirm(
                            UserCreateRequest(
                                username = username.trim(),
                                password = password.trim(),
                                fullName = fullName.trim(),
                                email = email.blankToNull(),
                                phone = phone.blankToNull(),
                                studentId = studentId.blankToNull(),
                                employeeCode = employeeCode.blankToNull()
                            )
                        )
                    }
                },
                enabled = username.isNotBlank() && password.isNotBlank() && fullName.isNotBlank() && !loading
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
fun AssignRolesDialog(
    user: UserProfileResponse,
    loading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    val currentRoles = remember(user) { user.roles.orEmpty().toMutableSet() }
    val allRoles = listOf("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = { Text("Phân vai trò: ${user.displayName()}", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                allRoles.forEach { role ->
                    val isChecked = role in currentRoles
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) currentRoles.add(role) else currentRoles.remove(role)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = AppIndigo)
                        )
                        Text(
                            when (role) {
                                "ROLE_ADMIN" -> "Quản trị viên"
                                "ROLE_TEACHER" -> "Giáo viên"
                                "ROLE_STUDENT" -> "Học sinh"
                                else -> role
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(currentRoles.toList()) },
                enabled = !loading
            ) {
                if (loading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                else Text("Lưu thay đổi", fontWeight = FontWeight.Bold)
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
fun RoleSelector(
    selectedRoles: Set<Role>,
    loading: Boolean,
    onToggleRole: (Role) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Role.values().forEach { role ->
            val isSelected = role in selectedRoles
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = if (isSelected) AppIndigo else AppSurface),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.medium)
            ) {
                TextButton(
                    onClick = { onToggleRole(role) },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        role.label(),
                        color = if (isSelected) AppSurface else AppText,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
