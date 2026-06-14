package com.internalexam.ui.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.UserProfileResponse
import com.internalexam.ui.components.AvatarCircle
import com.internalexam.ui.components.ChipText
import com.internalexam.ui.components.ExamTopBar
import com.internalexam.ui.components.InfoBanner
import com.internalexam.ui.components.PrimaryAction
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppSurface

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    var profile by remember { mutableStateOf<UserProfileResponse?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }

    fun loadProfile() {
        loading = true
        error = null
    }

    LaunchedEffect(loading) {
        if (!loading) return@LaunchedEffect
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            error = "No active session"
            loading = false
            return@LaunchedEffect
        }

        try {
            val response = ApiClient.getCurrentUser(authorization)
            profile = response.data
            if (!response.success || response.data == null) {
                error = response.message.ifBlank { "Cannot load profile" }
            }
        } catch (exception: Exception) {
            error = "Cannot connect to the backend"
        } finally {
            loading = false
        }
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ExamTopBar("Profile") }

        when {
            loading -> item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Loading profile", color = AppMuted)
                }
            }

            error != null -> item {
                InfoBanner(error.orEmpty(), color = AppRed)
                Spacer(Modifier.height(8.dp))
                PrimaryAction("Try again") { loadProfile() }
            }

            profile != null -> item {
                ProfileCard(profile = profile!!)
                PrimaryAction("Sign out") { onLogout() }
            }
        }
    }
}

@Composable
private fun ProfileCard(profile: UserProfileResponse) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle(profile.fullName.ifBlank { profile.username }, size = 58, color = AppIndigo)
                Column(Modifier.padding(start = 14.dp).weight(1f)) {
                    Text(profile.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("@${profile.username}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                profile.roles.orEmpty().forEach { role -> ChipText(role.normalizeLabel(), AppIndigo) }
                profile.status?.let { ChipText(it.normalizeLabel()) }
            }

            ProfileField(Icons.Default.Person, "Username", profile.username)
            ProfileField(Icons.Default.Badge, "Student ID", profile.studentId)
            ProfileField(Icons.Default.Badge, "Employee code", profile.employeeCode)
            ProfileField(Icons.Default.Email, "Email", profile.email)
            ProfileField(Icons.Default.Phone, "Phone", profile.phone)
            ProfileField(Icons.Default.VerifiedUser, "Account status", profile.status?.normalizeLabel())
        }
    }
}

@Composable
private fun ProfileField(icon: ImageVector, label: String, value: String?) {
    if (value.isNullOrBlank()) return

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, tint = AppMuted, modifier = Modifier.size(20.dp))
        Column(Modifier.padding(start = 12.dp)) {
            Text(label, color = AppMuted, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

private fun String.normalizeLabel(): String {
    return lowercase().replaceFirstChar { it.uppercase() }.replace('_', ' ')
}