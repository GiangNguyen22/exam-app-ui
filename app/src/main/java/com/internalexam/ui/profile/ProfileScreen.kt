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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.internalexam.data.network.UserProfileResponse
import com.internalexam.ui.components.AvatarCircle
import com.internalexam.ui.components.ChipText
import com.internalexam.ui.components.ExamTopBar
import com.internalexam.ui.components.InfoBanner
import com.internalexam.ui.components.LoadingStateCard
import com.internalexam.ui.components.PrimaryAction
import com.internalexam.ui.components.ScreenBottomPadding
import com.internalexam.ui.exam.UiState
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppSurface

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ExamTopBar("Hồ sơ cá nhân") }

        when (val uiState = state) {
            is UiState.Loading -> item {
                LoadingStateCard("Đang tải thông tin cá nhân...")
            }

            is UiState.Error -> item {
                InfoBanner(uiState.message, color = AppRed)
                Spacer(Modifier.height(12.dp))
                PrimaryAction("Thử lại") { viewModel.loadProfile() }
            }

            is UiState.Success -> item {
                ProfileCard(profile = uiState.data)
                Spacer(Modifier.height(16.dp))
                PrimaryAction("Đăng xuất") { onLogout() }
                Spacer(Modifier.height(ScreenBottomPadding))
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
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                AvatarCircle(profile.fullName.ifBlank { profile.username }, size = 58, color = AppIndigo)
                Column(Modifier.padding(start = 14.dp).weight(1f)) {
                    Text(profile.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("@${profile.username}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                profile.roles.orEmpty().forEach { role -> ChipText(role.normalizeLabel(), AppIndigo) }
                profile.status?.let { ChipText(it.normalizeLabel(), AppAmber) }
            }

            ProfileField(Icons.Default.Person, "Tên đăng nhập", profile.username)
            ProfileField(Icons.Default.Badge, "Mã học sinh", profile.studentId)
            ProfileField(Icons.Default.Badge, "Mã cán bộ / giáo viên", profile.employeeCode)
            ProfileField(Icons.Default.Email, "Email", profile.email)
            ProfileField(Icons.Default.Phone, "Số điện thoại", profile.phone)
            ProfileField(Icons.Default.VerifiedUser, "Trạng thái tài khoản", profile.status?.normalizeLabel())
        }
    }
}

@Composable
private fun ProfileField(icon: ImageVector, label: String, value: String?) {
    if (value.isNullOrBlank()) return

    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, tint = AppMuted, modifier = Modifier.size(20.dp))
        Column(Modifier.padding(start = 12.dp)) {
            Text(label, color = AppMuted, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

private fun String.normalizeLabel(): String {
    return when (this.uppercase()) {
        "ROLE_ADMIN", "ADMIN" -> "Quản trị viên"
        "ROLE_TEACHER", "TEACHER" -> "Giáo viên"
        "ROLE_STUDENT", "STUDENT" -> "Học sinh"
        "ACTIVE" -> "Đang hoạt động"
        "INACTIVE" -> "Đã khóa"
        else -> lowercase().replaceFirstChar { it.uppercase() }.replace('_', ' ')
    }
}
