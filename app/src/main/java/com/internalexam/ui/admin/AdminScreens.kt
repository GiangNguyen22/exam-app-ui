package com.internalexam.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.NetworkState
import com.internalexam.model.mock.Role
import com.internalexam.model.mock.UserStatus
import com.internalexam.ui.components.*
import com.internalexam.ui.theme.*

@Composable
fun AdminDashboardScreen(openUsers: () -> Unit) {
    AppBackground {
        Spacer(Modifier.height(18.dp))
        GradientHero("Admin Dashboard", "Quan ly tai khoan, vai tro, quyen va audit logs") { StatusPill(NetworkState.SYNCED) }
        SectionTitle("Thong ke he thong")
        MetricCard("Nguoi dung", MockData.users.size.toString(), "Admin, Teacher, Student", AppBlue, Icons.Default.Groups)
        Spacer(Modifier.height(10.dp))
        MetricCard("Phan quyen", "3 role", "Quan ly quyen truy cap", AppViolet, Icons.Default.Security)
        Spacer(Modifier.height(10.dp))
        MetricCard("Audit logs", MockData.auditLogs.size.toString(), "Realtime security events", AppAmber, Icons.Default.History)
        SectionTitle("Quan tri")
        listOf("Quan ly tai khoan" to openUsers, "Gan vai tro" to {}, "Quan ly quyen" to {}, "Audit" to {}, "Cau hinh" to {}).forEach { (label, action) ->
            OutlinedButton(onClick = action, modifier = Modifier.fillMaxWidth()) { Text(label) }
        }
    }
}

@Composable
fun UserManagementScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("Quan ly user", onBack)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            Role.entries.forEach { ChipText(it.name, when (it) { Role.ADMIN -> AppRed; Role.TEACHER -> AppViolet; Role.STUDENT -> AppBlue }) }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 96.dp)) {
            items(MockData.users) { user ->
                val color = when (user.status) { UserStatus.ACTIVE -> AppMint; UserStatus.LOCKED -> AppRed; UserStatus.PENDING -> AppAmber }
                Card(shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(user.name, fontWeight = FontWeight.Bold)
                            ChipText(user.status.name, color)
                        }
                        Text("${user.code} • ${user.role}", color = AppMuted)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedButton(onClick = {}) { Text(if (user.status == UserStatus.LOCKED) "Mo khoa" else "Khoa") }
                            OutlinedButton(onClick = {}) { Text("Gan role") }
                        }
                    }
                }
            }
        }
        Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(52.dp)) { Icon(Icons.Default.AdminPanelSettings, null); Text("Tao user") }
    }
}
