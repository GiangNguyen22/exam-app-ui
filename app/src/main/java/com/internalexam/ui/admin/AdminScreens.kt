package com.internalexam.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internalexam.model.mock.MockData
import com.internalexam.model.mock.NetworkState
import com.internalexam.model.mock.Role
import com.internalexam.model.mock.UserStatus
import com.internalexam.ui.components.AppBackground
import com.internalexam.ui.components.AvatarCircle
import com.internalexam.ui.components.ChipText
import com.internalexam.ui.components.ExamTopBar
import com.internalexam.ui.components.GradientHero
import com.internalexam.ui.components.MetricCard
import com.internalexam.ui.components.SectionTitle
import com.internalexam.ui.components.StatusPill
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppBlue
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMint
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppSurface
import com.internalexam.ui.theme.AppViolet

@Composable
fun AdminDashboardScreen(openUsers: () -> Unit) {
    AppBackground {
        Spacer(Modifier.height(18.dp))
        GradientHero("Admin Dashboard", "Accounts, roles, permissions & audit") {
            StatusPill(NetworkState.SYNCED)
        }

        SectionTitle("System Overview")
        MetricCard("Users", MockData.users.size.toString(), "Admin, Teacher, Student", AppBlue, Icons.Default.Groups)
        Spacer(Modifier.height(10.dp))
        MetricCard("Roles", "3 roles", "Access control", AppViolet, Icons.Default.Security)
        Spacer(Modifier.height(10.dp))
        MetricCard("Audit Logs", MockData.auditLogs.size.toString(), "Security events", AppAmber, Icons.Default.History)

        SectionTitle("Administration")
        val adminActions = listOf(
            Triple("Manage Accounts", Icons.Default.Groups, openUsers),
            Triple("Assign Roles", Icons.Default.Badge, {}),
            Triple("Permissions", Icons.Default.Security, {}),
            Triple("Audit Logs", Icons.Default.History, {}),
            Triple("Settings", Icons.Default.Settings, {}),
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            adminActions.forEach { (label, icon, action) ->
                Card(
                    onClick = action,
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(40.dp)
                                .background(AppIndigo.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = AppIndigo, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(label, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, null, tint = AppMuted)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun UserManagementScreen(onBack: () -> Unit) {
    AppBackground {
        ExamTopBar("User Management", onBack)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            Role.entries.forEach { role ->
                val color = when (role) { Role.ADMIN -> AppRed; Role.TEACHER -> AppViolet; Role.STUDENT -> AppBlue }
                ChipText(role.name, color)
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 96.dp)) {
            items(MockData.users) { user ->
                val statusColor = when (user.status) { UserStatus.ACTIVE -> AppMint; UserStatus.LOCKED -> AppRed; UserStatus.PENDING -> AppAmber }
                val roleColor = when (user.role) { Role.ADMIN -> AppRed; Role.TEACHER -> AppViolet; Role.STUDENT -> AppBlue }

                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            AvatarCircle(user.name, 42, roleColor)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(user.name, fontWeight = FontWeight.Bold)
                                Text("${user.code} · ${user.role}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                            }
                            ChipText(user.status.name, statusColor)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = {}, shape = MaterialTheme.shapes.medium, modifier = Modifier.height(36.dp), contentPadding = PaddingValues(horizontal = 14.dp)) {
                                Text(if (user.status == UserStatus.LOCKED) "Unlock" else "Lock", style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(onClick = {}, shape = MaterialTheme.shapes.medium, modifier = Modifier.height(36.dp), contentPadding = PaddingValues(horizontal = 14.dp)) {
                                Text("Assign Role", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Create User", fontWeight = FontWeight.Bold)
        }
    }
}
