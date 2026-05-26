package com.internalexam.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.NetworkState
import com.internalexam.model.mock.Role
import com.internalexam.model.mock.UserStatus
import com.internalexam.ui.components.AppBackground
import com.internalexam.ui.components.ChipText
import com.internalexam.ui.components.ExamTopBar
import com.internalexam.ui.components.GradientHero
import com.internalexam.ui.components.MetricCard
import com.internalexam.ui.components.SectionTitle
import com.internalexam.ui.components.StatusPill
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppBlue
import com.internalexam.ui.theme.AppMint
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppViolet

@Composable
fun AdminDashboardScreen(openUsers: () -> Unit) {
    AppBackground {
        Spacer(Modifier.height(18.dp))
        GradientHero("Admin Dashboard", "Manage accounts, roles, permissions, and audit logs") { StatusPill(NetworkState.SYNCED) }
        SectionTitle("System Overview")
        MetricCard("Users", MockData.users.size.toString(), "Admin, Teacher, Student", AppBlue, Icons.Default.Groups)
        Spacer(Modifier.height(10.dp))
        MetricCard("Roles", "3 roles", "Access control", AppViolet, Icons.Default.Security)
        Spacer(Modifier.height(10.dp))
        MetricCard("Audit Logs", MockData.auditLogs.size.toString(), "Realtime security events", AppAmber, Icons.Default.History)
        SectionTitle("Administration")
        listOf("Manage Accounts" to openUsers, "Assign Roles" to {}, "Manage Permissions" to {}, "Audit Logs" to {}, "Settings" to {}).forEach { (label, action) ->
            OutlinedButton(onClick = action, modifier = Modifier.fillMaxWidth()) { Text(label) }
        }
    }
}

@Composable
fun UserManagementScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("User Management", onBack)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            Role.entries.forEach { role -> ChipText(role.name, when (role) { Role.ADMIN -> AppRed; Role.TEACHER -> AppViolet; Role.STUDENT -> AppBlue }) }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 96.dp)) {
            items(MockData.users) { user ->
                val color = when (user.status) {
                    UserStatus.ACTIVE -> AppMint
                    UserStatus.LOCKED -> AppRed
                    UserStatus.PENDING -> AppAmber
                }
                Card(shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(user.name, fontWeight = FontWeight.Bold)
                            ChipText(user.status.name, color)
                        }
                        Text("${user.code} - ${user.role}", color = AppMuted)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedButton(onClick = {}) { Text(if (user.status == UserStatus.LOCKED) "Unlock" else "Lock") }
                            OutlinedButton(onClick = {}) { Text("Assign Role") }
                        }
                    }
                }
            }
        }
        Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
            Text("Create User", modifier = Modifier.padding(start = 8.dp))
        }
    }
}
