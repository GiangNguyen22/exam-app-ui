package com.internalexam.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.internalexam.data.network.UserCreateRequest
import com.internalexam.data.network.UserProfileResponse
import com.internalexam.ui.components.AppBackground
import com.internalexam.ui.components.AvatarCircle
import com.internalexam.ui.components.ChipText
import com.internalexam.ui.components.ExamTopBar
import com.internalexam.ui.components.GradientHero
import com.internalexam.ui.components.InfoBanner
import com.internalexam.ui.components.LoadingStateCard
import com.internalexam.ui.components.MetricCard
import com.internalexam.ui.components.ScreenBottomPadding
import com.internalexam.ui.components.SectionTitle
import com.internalexam.ui.exam.UiState
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppBlue
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMint
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppSurface
import com.internalexam.ui.theme.AppText

@Composable
fun AdminDashboardScreen(
    openUsers: () -> Unit,
    openAuditLogs: () -> Unit,
    openRolePermissions: () -> Unit,
    openGroups: () -> Unit
) {
    AppBackground {
        Row(
            Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle("Quản trị viên", 48, AppRed)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Quản trị hệ thống", style = MaterialTheme.typography.titleLarge)
                    Text("Bảng điều khiển Admin", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        GradientHero("Phân hệ Quản trị", "Quản lý người dùng, phân quyền vai trò, nhóm học sinh và theo dõi nhật ký hệ thống.")

        SectionTitle("Chức năng quản trị")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Quản lý tài khoản", "Người dùng & Vai trò", "Tạo tài khoản, khóa & phân vai trò", AppBlue, Icons.Default.People, Modifier.clickable { openUsers() })
            MetricCard("Phân quyền hệ thống", "Ma trận RBAC", "Cấu hình quyền chi tiết cho từng vai trò", AppIndigo, Icons.Default.Security, Modifier.clickable { openRolePermissions() })
            MetricCard("Quản lý lớp học / Nhóm", "Nhóm học sinh", "Tạo lớp, thêm học sinh vào danh sách", AppMint, Icons.Default.Groups, Modifier.clickable { openGroups() })
            MetricCard("Nhật ký audit", "Nhật ký truy cập", "Lịch sử thao tác & sự kiện hệ thống", AppAmber, Icons.Default.History, Modifier.clickable { openAuditLogs() })
        }
        Spacer(Modifier.height(ScreenBottomPadding))
    }
}

@Composable
fun UserManagementScreen(
    viewModel: UserManagementViewModel = viewModel(),
    onBack: () -> Unit
) {
    val usersUiState by viewModel.usersState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedUserForRole by remember { mutableStateOf<UserProfileResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    AppBackground {
        ExamTopBar("Quản lý người dùng", onBack)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showCreateDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Tạo tài khoản")
            }
        }
        Spacer(Modifier.height(12.dp))

        when (val state = usersUiState) {
            is UiState.Loading -> LoadingStateCard("Đang tải danh sách người dùng...")
            is UiState.Error -> InfoBanner(state.message, AppAmber, Icons.Default.Info)
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    InfoBanner("Chưa có người dùng nào.", AppMuted, Icons.Default.Info)
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        state.data.forEach { user ->
                            Card(
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = AppSurface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AvatarCircle(user.displayName(), 44, AppIndigo)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(user.displayName(), fontWeight = FontWeight.Bold, color = AppText)
                                        Text("@${user.username}", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                                        user.roles?.takeIf { it.isNotEmpty() }?.let {
                                            Spacer(Modifier.height(4.dp))
                                            ChipText(it.joinToString(), AppIndigo)
                                        }
                                    }
                                    IconButton(onClick = { viewModel.toggleUserLock(user) }) {
                                        Icon(
                                            imageVector = if (user.status == "LOCKED" || user.status == "INACTIVE") Icons.Default.Lock else Icons.Default.LockOpen,
                                            contentDescription = "Khóa",
                                            tint = if (user.status == "LOCKED" || user.status == "INACTIVE") AppRed else AppMint
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(ScreenBottomPadding))
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateUserDialog(
            loading = false,
            onDismiss = { showCreateDialog = false },
            onConfirm = { req ->
                viewModel.createUser(req) { success ->
                    if (success) showCreateDialog = false
                }
            }
        )
    }

    selectedUserForRole?.let { user ->
        AssignRolesDialog(
            user = user,
            loading = false,
            onDismiss = { selectedUserForRole = null },
            onConfirm = { selectedUserForRole = null }
        )
    }
}

@Composable
fun AdminAuditLogScreen(
    viewModel: AdminAuditLogViewModel = viewModel(),
    onBack: () -> Unit
) {
    val logsUiState by viewModel.logsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadLogs()
    }

    AppBackground {
        ExamTopBar("Nhật ký audit hệ thống", onBack)
        Spacer(Modifier.height(8.dp))

        when (val state = logsUiState) {
            is UiState.Loading -> LoadingStateCard("Đang tải nhật ký...")
            is UiState.Error -> InfoBanner(state.message, AppAmber, Icons.Default.Info)
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    InfoBanner("Chưa có nhật ký audit nào.", AppMuted, Icons.Default.Info)
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        state.data.forEach { log ->
                            AuditLogCard(log)
                        }
                        Spacer(Modifier.height(ScreenBottomPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun RolePermissionScreen(
    onBack: () -> Unit
) {
    AppBackground {
        ExamTopBar("Phân quyền vai trò (RBAC)", onBack)
        InfoBanner("Cấu hình quyền truy cập theo từng nhóm vai trò trong hệ thống.", AppIndigo, Icons.Default.Info)
        Spacer(Modifier.weight(1f))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)) {
            Text("Lưu cấu hình phân quyền")
        }
        Spacer(Modifier.height(ScreenBottomPadding))
    }
}

@Composable
fun GroupListScreen(
    onGroupClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: AdminGroupViewModel = viewModel()
) {
    val groupsUiState by viewModel.groupsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadGroups()
    }

    AppBackground {
        ExamTopBar("Danh sách nhóm / lớp", onBack)
        Spacer(Modifier.height(8.dp))

        when (val state = groupsUiState) {
            is UiState.Loading -> LoadingStateCard("Đang tải nhóm/lớp...")
            is UiState.Error -> InfoBanner(state.message, AppAmber, Icons.Default.Info)
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    InfoBanner("Chưa có nhóm/lớp học sinh nào.", AppMuted, Icons.Default.Info)
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        state.data.forEach { group ->
                            Card(
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(containerColor = AppSurface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                                    .clickable { onGroupClick(group.id) }
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(group.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(4.dp))
                                    Text("Số thành viên: ${group.memberCount ?: 0} học sinh", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                        Spacer(Modifier.height(ScreenBottomPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun GroupDetailScreen(
    groupId: Long,
    onBack: () -> Unit
) {
    AppBackground {
        ExamTopBar("Chi tiết nhóm / lớp", onBack)
        InfoBanner("Quản lý danh sách học sinh thuộc nhóm ID: $groupId", AppIndigo, Icons.Default.Info)
        Spacer(Modifier.weight(1f))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Quay lại")
        }
        Spacer(Modifier.height(ScreenBottomPadding))
    }
}
