package com.internalexam.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.internalexam.data.SessionManager
import com.internalexam.data.auth.backendName
import com.internalexam.data.auth.primaryRole
import com.internalexam.data.auth.roleSet
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.AuditLogResponse
import com.internalexam.data.network.UserCreateRequest
import com.internalexam.data.network.UserProfileResponse
import com.internalexam.data.network.UserRolesRequest
import com.internalexam.data.network.PermissionResponse
import com.internalexam.data.network.RolePermissionsUpdateRequest
import com.internalexam.data.network.ApiResponse
import com.internalexam.data.network.RoleResponse
import com.internalexam.data.network.StudentGroupResponse
import com.internalexam.data.network.StudentGroupMemberResponse
import com.internalexam.data.network.GroupCreateRequest
import com.google.gson.Gson
import com.internalexam.model.mock.NetworkState
import com.internalexam.model.mock.Role
import com.internalexam.ui.components.AppBackground
import com.internalexam.ui.components.AvatarCircle
import com.internalexam.ui.components.ChipText
import com.internalexam.ui.components.ExamTopBar
import com.internalexam.ui.components.GradientHero
import com.internalexam.ui.components.InfoBanner
import com.internalexam.ui.components.LoadingStateCard
import com.internalexam.ui.components.MetricCard
import com.internalexam.ui.components.PrimaryAction
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
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(
    openUsers: () -> Unit,
    openAuditLogs: () -> Unit,
    openRolePermissions: () -> Unit,
    openGroups: () -> Unit
) {
    var message by remember { mutableStateOf<String?>(null) }
    var userCount by remember { mutableStateOf<Int?>(null) }
    var auditLogCount by remember { mutableStateOf<Int?>(null) }
    var roleCount by remember { mutableStateOf<Int?>(null) }
    var permissionCount by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            return@LaunchedEffect
        }
        runCatching { ApiClient.getUsers(authorization).data.orEmpty().size }
            .onSuccess { userCount = it }
            .onFailure { message = "Không tải được số liệu tài khoản từ backend." }
        runCatching { ApiClient.getAuditLogs(authorization).data.orEmpty().size }
            .onSuccess { auditLogCount = it }
        runCatching { ApiClient.getRoles(authorization).data.orEmpty().size }
            .onSuccess { roleCount = it }
        runCatching { ApiClient.getPermissions(authorization).data.orEmpty().size }
            .onSuccess { permissionCount = it }
    }

    AppBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
        Spacer(Modifier.height(18.dp))
        GradientHero("Quản trị hệ thống", "Tài khoản, vai trò, phân quyền và nhật ký") {
            StatusPill(NetworkState.SYNCED)
        }

        if (message != null) {
            Spacer(Modifier.height(12.dp))
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Settings)
        }

        SectionTitle("Tổng quan hệ thống")
        MetricCard("Tài khoản", userCount?.toString() ?: "--", "Admin, giáo viên, học sinh", AppBlue, Icons.Default.Groups)
        Spacer(Modifier.height(10.dp))
        MetricCard("Vai trò", "3 vai trò", "Kiểm soát truy cập", AppViolet, Icons.Default.Security)
        Spacer(Modifier.height(10.dp))
        MetricCard("Nhật ký", auditLogCount?.toString() ?: "--", "Sự kiện bảo mật", AppAmber, Icons.Default.History)

        SectionTitle("Chức năng quản trị")
        val adminActions = listOf(
            AdminAction("Phân quyền theo role", Icons.Default.Security, openRolePermissions),
            AdminAction("Quản lý tài khoản", Icons.Default.Groups, openUsers),
            AdminAction("Quản lý lớp học", Icons.Default.Groups, openGroups),
            AdminAction("Nhật ký audit", Icons.Default.History, openAuditLogs)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            adminActions.forEach { actionItem ->
                Card(
                    onClick = actionItem.onClick,
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
                            Icon(actionItem.icon, null, tint = AppIndigo, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(14.dp))
                        Text(actionItem.label, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ChevronRight, null, tint = AppMuted)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun UserManagementScreen(onBack: () -> Unit) {
    var users by remember { mutableStateOf<List<UserProfileResponse>>(emptyList()) }
    var selectedRole by remember { mutableStateOf<Role?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var changingUserId by remember { mutableStateOf<Long?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var roleTarget by remember { mutableStateOf<UserProfileResponse?>(null) }
    var dialogSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val visibleUsers = users.filter { user -> selectedRole == null || user.primaryRole() == selectedRole }

    fun loadUsers() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            isLoading = false
            return
        }
        scope.launch {
            isLoading = true
            message = null
            try {
                val response = ApiClient.getUsers(authorization)
                if (response.success) {
                    users = response.data.orEmpty()
                } else {
                    message = response.message.ifBlank { "Không tải được danh sách tài khoản." }
                }
            } catch (exception: Exception) {
                message = "Không kết nối được backend quản lý tài khoản."
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleUserLock(user: UserProfileResponse) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            return
        }
        scope.launch {
            changingUserId = user.id
            message = null
            try {
                val response = if (user.status == "LOCKED") {
                    ApiClient.unlockUser(authorization, user.id)
                } else {
                    ApiClient.lockUser(authorization, user.id)
                }
                val updatedUser = response.data
                if (response.success && updatedUser != null) {
                    users = users.map { item -> if (item.id == updatedUser.id) updatedUser else item }
                    message = if (updatedUser.status == "LOCKED") {
                        "Đã khóa tài khoản ${updatedUser.displayName()}."
                    } else {
                        "Đã mở khóa tài khoản ${updatedUser.displayName()}."
                    }
                } else {
                    message = response.message.ifBlank { "Không cập nhật được trạng thái tài khoản." }
                }
            } catch (exception: Exception) {
                message = "Không cập nhật được trạng thái tài khoản."
            } finally {
                changingUserId = null
            }
        }
    }

    fun createUser(request: UserCreateRequest, onDone: () -> Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            return
        }
        scope.launch {
            dialogSaving = true
            message = null
            try {
                val response = ApiClient.createUser(authorization, request)
                val createdUser = response.data
                if (response.success && createdUser != null) {
                    users = (users + createdUser).sortedBy { it.id }
                    message = "Đã tạo tài khoản ${createdUser.displayName()}."
                    onDone()
                } else {
                    message = response.message.ifBlank { "Không tạo được tài khoản." }
                }
            } catch (exception: Exception) {
                message = "Không tạo được tài khoản."
            } finally {
                dialogSaving = false
            }
        }
    }

    fun updateRoles(user: UserProfileResponse, roles: List<String>, onDone: () -> Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            return
        }
        scope.launch {
            dialogSaving = true
            message = null
            try {
                val response = ApiClient.updateUserRoles(authorization, user.id, UserRolesRequest(roles))
                val updatedUser = response.data
                if (response.success && updatedUser != null) {
                    users = users.map { item -> if (item.id == updatedUser.id) updatedUser else item }
                    message = "Đã cập nhật vai trò cho ${updatedUser.displayName()}."
                    onDone()
                } else {
                    message = response.message.ifBlank { "Không cập nhật được vai trò." }
                }
            } catch (exception: Exception) {
                message = "Không cập nhật được vai trò."
            } finally {
                dialogSaving = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadUsers()
    }

    AppBackground {
        ExamTopBar("Quản lý tài khoản", onBack)

        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Settings)
            Spacer(Modifier.height(10.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            OutlinedButton(
                onClick = { selectedRole = null },
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(horizontal = 14.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Tất cả", style = MaterialTheme.typography.labelMedium)
            }
            Role.entries.forEach { role ->
                val color = when (role) { Role.ADMIN -> AppRed; Role.TEACHER -> AppViolet; Role.STUDENT -> AppBlue }
                Button(
                    onClick = { selectedRole = role },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRole == role) color else color.copy(alpha = 0.12f),
                        contentColor = if (selectedRole == role) AppSurface else color
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(role.label(), style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 96.dp)) {
            if (isLoading) {
                item { LoadingStateCard("Đang tải danh sách tài khoản...") }
            } else if (visibleUsers.isEmpty()) {
                item { InfoBanner("Không có tài khoản phù hợp bộ lọc.", AppAmber, Icons.Default.Groups) }
            }

            items(visibleUsers, key = { it.id }) { user ->
                val statusColor = when (user.status) { "ACTIVE" -> AppMint; "LOCKED" -> AppRed; "PENDING" -> AppAmber; else -> AppMuted }
                val role = user.primaryRole()
                val roleColor = when (role) { Role.ADMIN -> AppRed; Role.TEACHER -> AppViolet; Role.STUDENT -> AppBlue; null -> AppMuted }

                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            AvatarCircle(user.displayName(), 42, roleColor)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(user.displayName(), fontWeight = FontWeight.Bold)
                                Text("${user.identityCode()} · ${role?.label() ?: "Chưa gán vai trò"}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                            }
                            ChipText(user.statusLabel(), statusColor)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { toggleUserLock(user) },
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp),
                                enabled = changingUserId != user.id
                            ) {
                                Text(if (user.status == "LOCKED") "Mở khóa" else "Khóa", style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(
                                onClick = { roleTarget = user },
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp)
                            ) {
                                Text("Gán vai trò", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = AppIndigo)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Tạo tài khoản", fontWeight = FontWeight.Bold)
        }
    }

    if (showCreateDialog) {
        CreateUserDialog(
            saving = dialogSaving,
            onDismiss = { if (!dialogSaving) showCreateDialog = false },
            onSubmit = { request ->
                createUser(request) { showCreateDialog = false }
            }
        )
    }

    roleTarget?.let { user ->
        AssignRolesDialog(
            user = user,
            saving = dialogSaving,
            onDismiss = { if (!dialogSaving) roleTarget = null },
            onSubmit = { roles ->
                updateRoles(user, roles) { roleTarget = null }
            }
        )
    }
}

@Composable
fun AdminAuditLogScreen(onBack: () -> Unit) {
    var logs by remember { mutableStateOf<List<AuditLogResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            isLoading = false
            return@LaunchedEffect
        }
        try {
            val response = ApiClient.getAuditLogs(authorization)
            if (response.success) {
                logs = response.data.orEmpty()
                message = null
            } else {
                message = response.message.ifBlank { "Không tải được nhật ký audit." }
            }
        } catch (exception: Exception) {
            message = "Không kết nối được backend nhật ký audit."
        } finally {
            isLoading = false
        }
    }

    AppBackground {
        ExamTopBar("Nhật ký audit", onBack)

        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.History)
            Spacer(Modifier.height(10.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 96.dp)) {
            if (isLoading) {
                item { LoadingStateCard("Đang tải nhật ký audit...") }
            } else if (logs.isEmpty()) {
                item { InfoBanner("Chưa có nhật ký audit.", AppAmber, Icons.Default.History) }
            }

            items(logs, key = { it.id }) { log ->
                AuditLogCard(log)
            }
        }
    }
}

@Composable
fun RolePermissionScreen(onBack: () -> Unit) {
    var roles by remember { mutableStateOf<List<RoleResponse>>(emptyList()) }
    var permissions by remember { mutableStateOf<List<PermissionResponse>>(emptyList()) }
    var selectedRoleId by remember { mutableStateOf<Long?>(null) }
    var selectedPermissions by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val selectedRole = roles.firstOrNull { it.id == selectedRoleId }
    val savedPermissions = selectedRole?.permissions.orEmpty().toSet()
    val hasChanges = selectedRole != null && selectedPermissions != savedPermissions

    fun loadRbac() {
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            isLoading = false
            return
        }
        scope.launch {
            isLoading = true
            message = null
            try {
                val roleResponse = ApiClient.getRoles(authorization)
                val permissionResponse = ApiClient.getPermissions(authorization)
                if (roleResponse.success && permissionResponse.success) {
                    roles = roleResponse.data.orEmpty()
                    permissions = permissionResponse.data.orEmpty()
                    val nextRole = roles.firstOrNull { it.id == selectedRoleId } ?: roles.firstOrNull()
                    selectedRoleId = nextRole?.id
                    selectedPermissions = nextRole?.permissions.orEmpty().toSet()
                } else {
                    message = roleResponse.message.ifBlank {
                        permissionResponse.message.ifBlank { "Không tải được dữ liệu phân quyền." }
                    }
                }
            } catch (exception: Exception) {
                message = "Không kết nối được backend phân quyền."
            } finally {
                isLoading = false
            }
        }
    }

    fun savePermissions() {
        val role = selectedRole ?: return
        val authorization = SessionManager.authorizationHeader()
        if (authorization == null) {
            message = "Vui lòng đăng nhập lại."
            return
        }
        if (role.displayRoleName() == "ADMIN" && "rbac:manage" !in selectedPermissions) {
            message = "Role Admin cần giữ quyền rbac:manage để tiếp tục quản trị phân quyền."
            return
        }
        scope.launch {
            isSaving = true
            message = null
            try {
                val response = ApiClient.updateRolePermissions(
                    authorization,
                    role.id,
                    RolePermissionsUpdateRequest(selectedPermissions.sorted())
                )
                val updatedRole = response.data
                if (response.success && updatedRole != null) {
                    roles = roles.map { item -> if (item.id == updatedRole.id) updatedRole else item }
                    selectedPermissions = updatedRole.permissions.orEmpty().toSet()
                    message = "Đã cập nhật quyền cho role ${updatedRole.displayRoleName()}."
                } else {
                    message = response.message.ifBlank { "Không lưu được phân quyền." }
                }
            } catch (exception: Exception) {
                message = "Không lưu được phân quyền."
            } finally {
                isSaving = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadRbac()
    }

    AppBackground {
        ExamTopBar("Phân quyền theo role", onBack)

        if (message != null) {
            InfoBanner(message.orEmpty(), AppAmber, Icons.Default.Security)
            Spacer(Modifier.height(10.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 96.dp)) {
            if (isLoading) {
                item { LoadingStateCard("Đang tải phân quyền...") }
            } else if (roles.isEmpty() || permissions.isEmpty()) {
                item { InfoBanner("Chưa có vai trò hoặc quyền để cấu hình.", AppAmber, Icons.Default.Security) }
            } else {
                item {
                    SectionTitle("Vai trò")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        roles.forEach { role ->
                            val selected = role.id == selectedRoleId
                            Button(
                                onClick = {
                                    selectedRoleId = role.id
                                    selectedPermissions = role.permissions.orEmpty().toSet()
                                    message = null
                                },
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) AppIndigo else AppIndigo.copy(alpha = 0.10f),
                                    contentColor = if (selected) AppSurface else AppIndigo
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("${role.displayRoleName()} · ${role.permissions.orEmpty().size} quyền")
                            }
                        }
                    }
                }

                item {
                    selectedRole?.let { role ->
                        Card(
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = AppSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(role.displayRoleName(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    "${selectedPermissions.size}/${permissions.size} quyền đang bật",
                                    color = AppMuted,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Button(
                                    onClick = { savePermissions() },
                                    enabled = hasChanges && !isSaving,
                                    shape = MaterialTheme.shapes.medium,
                                    colors = ButtonDefaults.buttonColors(containerColor = AppIndigo),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(if (isSaving) "Đang lưu..." else if (hasChanges) "Lưu phân quyền" else "Chưa có thay đổi")
                                }
                            }
                        }
                    }
                }

                val permissionGroups = permissions
                    .groupBy { it.name.permissionGroup() }
                    .toList()
                    .sortedBy { it.first }

                items(permissionGroups, key = { it.first }) { group ->
                    val groupName = group.first
                    val groupPermissions = group.second.sortedBy { it.name }
                    val groupPermissionNames = groupPermissions.map { it.name }.toSet()
                    val allChecked = groupPermissionNames.all { it in selectedPermissions }

                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = AppSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(groupName.permissionGroupLabel(), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                TextButton(
                                    enabled = !isSaving,
                                    onClick = {
                                        selectedPermissions = if (allChecked) {
                                            selectedPermissions - groupPermissionNames
                                        } else {
                                            selectedPermissions + groupPermissionNames
                                        }
                                    }
                                ) {
                                    Text(if (allChecked) "Bỏ nhóm" else "Chọn nhóm")
                                }
                            }
                            groupPermissions.forEach { permission ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = permission.name in selectedPermissions,
                                        onCheckedChange = { checked ->
                                            selectedPermissions = if (checked) {
                                                selectedPermissions + permission.name
                                            } else {
                                                selectedPermissions - permission.name
                                            }
                                            message = null
                                        },
                                        enabled = !isSaving,
                                        colors = CheckboxDefaults.colors(checkedColor = AppIndigo)
                                    )
                                    Column(Modifier.weight(1f)) {
                                        Text(permission.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        permission.description?.takeIf { it.isNotBlank() }?.let { description ->
                                            Text(description, color = AppMuted, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditLogCard(log: AuditLogResponse) {
    val resultColor = when (log.result) {
        "allow" -> AppMint
        "deny" -> AppRed
        "error" -> AppAmber
        else -> AppMuted
    }
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(log.action, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                ChipText(log.result ?: "--", resultColor)
            }
            Text("Người dùng: ${log.username ?: "Hệ thống"}", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
            val resource = listOfNotNull(log.resourceType, log.resourceId?.toString()).joinToString(" #")
            if (resource.isNotBlank()) {
                Text("Tài nguyên: $resource", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
            }
            log.reason?.takeIf { it.isNotBlank() }?.let { reason ->
                Text(reason, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
            }
            Text(formatAuditTime(log.createdAt), color = AppMuted, style = MaterialTheme.typography.labelMedium)
        }
    }
}

private data class AdminAction(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun CreateUserDialog(
    saving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (UserCreateRequest) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var employeeCode by remember { mutableStateOf("") }
    var roles by remember { mutableStateOf(setOf(Role.STUDENT)) }
    var localError by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo tài khoản") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                localError?.let { InfoBanner(it, AppRed, Icons.Default.Settings) }
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; localError = null },
                    label = { Text("Tên đăng nhập") },
                    singleLine = true,
                    enabled = !saving,
                    supportingText = { Text("Dùng để đăng nhập hệ thống") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; localError = null },
                    label = { Text("Mật khẩu") },
                    singleLine = true,
                    enabled = !saving,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }, enabled = !saving) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showPassword) "Ẩn mật khẩu" else "Hiện mật khẩu"
                            )
                        }
                    },
                    supportingText = { Text("Tối thiểu 6 ký tự") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it; localError = null },
                    label = { Text("Họ tên") },
                    singleLine = true,
                    enabled = !saving,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; localError = null },
                    label = { Text("Email") },
                    singleLine = true,
                    enabled = !saving,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                RoleSelector(
                    selectedRoles = roles,
                    enabled = !saving,
                    onToggle = { role ->
                        roles = if (role in roles) roles - role else roles + role
                        localError = null
                    }
                )
                if (Role.STUDENT in roles) {
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it; localError = null },
                        label = { Text("Mã học sinh") },
                        singleLine = true,
                        enabled = !saving,
                        supportingText = { Text("Bỏ trống để backend tự sinh mã SV theo năm hiện tại") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (Role.TEACHER in roles || Role.ADMIN in roles) {
                    OutlinedTextField(
                        value = employeeCode,
                        onValueChange = { employeeCode = it; localError = null },
                        label = { Text("Mã nhân viên") },
                        singleLine = true,
                        enabled = !saving,
                        supportingText = { Text("Bỏ trống để backend tự sinh mã NV theo năm hiện tại") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !saving,
                onClick = {
                    val trimmedUsername = username.trim()
                    val trimmedPassword = password.trim()
                    val trimmedFullName = fullName.trim()
                    val trimmedEmail = email.trim()
                    val trimmedStudentId = studentId.trim()
                    val trimmedEmployeeCode = employeeCode.trim()
                    when {
                        trimmedUsername.isBlank() -> localError = "Vui lòng nhập tên đăng nhập."
                        trimmedPassword.length < 6 -> localError = "Mật khẩu cần ít nhất 6 ký tự."
                        trimmedFullName.isBlank() -> localError = "Vui lòng nhập họ tên."
                        roles.isEmpty() -> localError = "Vui lòng chọn ít nhất một vai trò."
                        trimmedEmail.isNotBlank() && !trimmedEmail.isValidEmailLike() -> localError = "Email chưa đúng định dạng."
                        else -> onSubmit(
                            UserCreateRequest(
                                username = trimmedUsername,
                                password = trimmedPassword,
                                fullName = trimmedFullName,
                                email = trimmedEmail.blankToNull(),
                                studentId = trimmedStudentId.blankToNull(),
                                employeeCode = trimmedEmployeeCode.blankToNull(),
                                roles = roles.map { it.backendName() }
                            )
                        )
                    }
                }
            ) {
                Text(if (saving) "Đang tạo..." else "Tạo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !saving) {
                Text("Hủy")
            }
        }
    )
}

@Composable
private fun AssignRolesDialog(
    user: UserProfileResponse,
    saving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (List<String>) -> Unit
) {
    var roles by remember(user.id) { mutableStateOf(user.roleSet()) }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gán vai trò") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(user.displayName(), style = MaterialTheme.typography.titleMedium)
                Text(user.identityCode(), color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                localError?.let { InfoBanner(it, AppRed, Icons.Default.Settings) }
                RoleSelector(
                    selectedRoles = roles,
                    enabled = !saving,
                    onToggle = { role ->
                        roles = if (role in roles) roles - role else roles + role
                        localError = null
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = !saving,
                onClick = {
                    if (roles.isEmpty()) {
                        localError = "Vui lòng chọn ít nhất một vai trò."
                    } else {
                        onSubmit(roles.map { it.backendName() })
                    }
                }
            ) {
                Text(if (saving) "Đang lưu..." else "Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !saving) {
                Text("Hủy")
            }
        }
    )
}

@Composable
private fun RoleSelector(
    selectedRoles: Set<Role>,
    enabled: Boolean,
    onToggle: (Role) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("Vai trò", color = AppMuted, style = MaterialTheme.typography.labelMedium)
        Role.entries.forEach { role ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = role in selectedRoles,
                    onCheckedChange = { onToggle(role) },
                    enabled = enabled,
                    colors = CheckboxDefaults.colors(checkedColor = AppIndigo)
                )
                Text(role.label(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private fun Role.label(): String = when (this) {
    Role.ADMIN -> "Quản trị viên"
    Role.TEACHER -> "Giáo viên"
    Role.STUDENT -> "Học sinh"
}

private fun RoleResponse.displayRoleName(): String {
    return name.removePrefix("ROLE_").uppercase()
}

private fun String.permissionGroup(): String {
    return substringBefore(":", missingDelimiterValue = "other").lowercase()
}

private fun String.permissionGroupLabel(): String = when (this) {
    "user" -> "Tài khoản"
    "audit" -> "Nhật ký audit"
    "rbac" -> "Phân quyền"
    "question" -> "Ngân hàng câu hỏi"
    "exam" -> "Đề thi"
    "result" -> "Kết quả"
    else -> replaceFirstChar { char -> char.uppercase() }
}

private fun UserProfileResponse.displayName(): String {
    return fullName.takeIf { it.isNotBlank() } ?: username
}

private fun UserProfileResponse.identityCode(): String {
    return studentId?.takeIf { it.isNotBlank() }
        ?: employeeCode?.takeIf { it.isNotBlank() }
        ?: username
}

private fun UserProfileResponse.statusLabel(): String = when (status) {
    "ACTIVE" -> "Hoạt động"
    "LOCKED" -> "Bị khóa"
    "PENDING" -> "Chờ duyệt"
    else -> status ?: "--"
}

private fun formatAuditTime(value: String?): String {
    return value?.takeIf { it.isNotBlank() }?.replace("T", " ")?.take(16) ?: "--"
}

private fun String.isValidEmailLike(): Boolean {
    return contains("@") && substringAfter("@").contains(".") && !contains(" ")
}

private fun String.blankToNull(): String? {
    return trim().takeIf { it.isNotBlank() }
}

private fun StudentGroupResponse.displayInfo(): String = "${name} (${memberCount ?: 0} học sinh)"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GroupListScreen(
    onGroupClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    var groups by remember { mutableStateOf<List<StudentGroupResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf<StudentGroupResponse?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun loadGroups() {
        val auth = SessionManager.authorizationHeader() ?: return
        loading = true
        scope.launch {
            try {
                val response = ApiClient.getGroups(auth)
                groups = response.data.orEmpty()
            } catch (e: Exception) {
                message = "Không thể tải danh sách lớp học."
            } finally { loading = false }
        }
    }

    LaunchedEffect(Unit) { loadGroups() }

    AppBackground {
        ExamTopBar("Quản lý lớp học", onBack)
        Column(
            Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${groups.size} lớp", fontWeight = FontWeight.Medium, color = AppMuted)
                OutlinedButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Thêm lớp")
                }
            }
            Spacer(Modifier.height(8.dp))

            if (loading) {
                LoadingStateCard("Đang tải danh sách lớp...")
            } else if (groups.isEmpty()) {
                Spacer(Modifier.height(40.dp))
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier.fillMaxWidth().border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Groups, null, tint = AppMuted, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Chưa có lớp học nào", color = AppMuted)
                        Spacer(Modifier.height(4.dp))
                        Text("Nhấn \"Thêm lớp\" để tạo lớp học mới.", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(groups) { group ->
                        Card(
                            onClick = { onGroupClick(group.id) },
                            shape = MaterialTheme.shapes.large,
                            colors = CardDefaults.cardColors(containerColor = AppSurface),
                            modifier = Modifier.fillMaxWidth().border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(44.dp).background(AppIndigo.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Groups, null, modifier = Modifier.size(22.dp), tint = AppIndigo)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(group.name, fontWeight = FontWeight.SemiBold)
                                    group.description?.takeIf { it.isNotBlank() }?.let {
                                        Text(it, color = AppMuted, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                    }
                                    Text("${group.memberCount ?: 0} học sinh", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { showDeleteConfirm = group }) {
                                    Icon(Icons.Default.Delete, "Xóa", tint = AppRed)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = AppMuted)
                            }
                        }
                    }
                }
            }

            if (message != null) {
                Spacer(Modifier.height(8.dp))
                InfoBanner(message.orEmpty(), AppRed, Icons.Default.ErrorOutline)
            }
        }
    }

    if (showCreateDialog) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var saving by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!saving) showCreateDialog = false },
            title = { Text("Thêm lớp học mới") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Tên lớp") }, enabled = !saving, shape = MaterialTheme.shapes.medium, singleLine = true)
                    OutlinedTextField(description, { description = it }, label = { Text("Mô tả (không bắt buộc)") }, enabled = !saving, shape = MaterialTheme.shapes.medium, minLines = 2)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (name.isBlank()) return@TextButton
                        val auth = SessionManager.authorizationHeader() ?: return@TextButton
                        saving = true
                        scope.launch {
                            try {
                                ApiClient.createGroup(auth, GroupCreateRequest(name = name.trim(), description = description.blankToNull()))
                                showCreateDialog = false
                                loadGroups()
                            } catch (e: Exception) { message = "Không thể tạo lớp." } finally { saving = false }
                        }
                    },
                    enabled = !saving && name.isNotBlank()
                ) { if (saving) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp) else Text("Tạo") }
            },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Hủy") } }
        )
    }

    showDeleteConfirm?.let { group ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Xóa lớp học") },
            text = { Text("Bạn có chắc chắn muốn xóa lớp \"${group.name}\"? Học sinh trong lớp sẽ không bị ảnh hưởng.") },
            confirmButton = {
                TextButton(onClick = {
                    val auth = SessionManager.authorizationHeader() ?: return@TextButton
                    scope.launch {
                        try {
                            ApiClient.deleteGroup(auth, group.id)
                            showDeleteConfirm = null
                            loadGroups()
                        } catch (e: Exception) { message = "Không thể xóa lớp." }
                    }
                }) { Text("Xóa", color = AppRed) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = null }) { Text("Hủy") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GroupDetailScreen(groupId: Long, onBack: () -> Unit) {
    var group by remember { mutableStateOf<StudentGroupResponse?>(null) }
    var members by remember { mutableStateOf<List<StudentGroupMemberResponse>>(emptyList()) }
    var allStudents by remember { mutableStateOf<List<UserProfileResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var editSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadData() {
        val auth = SessionManager.authorizationHeader() ?: return
        loading = true
        scope.launch {
            try {
                val gResponse = ApiClient.getGroups(auth)
                group = gResponse.data?.find { it.id == groupId }
                val mResponse = ApiClient.getGroupMembers(auth, groupId)
                members = mResponse.data.orEmpty()
            } catch (e: Exception) {
                message = "Không thể tải thông tin lớp học."
            } finally { loading = false }
        }
    }

    LaunchedEffect(groupId) { loadData() }

    AppBackground {
        ExamTopBar(group?.name ?: "Chi tiết lớp học", onBack)
        Column(
            Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (loading) {
                LoadingStateCard("Đang tải...")
                return@AppBackground
            }

            if (message != null) {
                InfoBanner(message.orEmpty(), AppRed, Icons.Default.ErrorOutline)
                Spacer(Modifier.height(8.dp))
            }

            group?.let { g ->
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier.fillMaxWidth().border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(g.name, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            OutlinedButton(onClick = {
                                if (!editing) { editName = g.name; editDescription = g.description.orEmpty() }
                                editing = !editing
                            }) { Text(if (editing) "Hủy" else "Sửa") }
                        }
                        g.description?.takeIf { it.isNotBlank() }?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(it, color = AppMuted)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("${members.size} học sinh", color = AppMuted, style = MaterialTheme.typography.bodySmall)

                        if (editing) {
                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(editName, { editName = it }, label = { Text("Tên lớp") }, singleLine = true, shape = MaterialTheme.shapes.medium)
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(editDescription, { editDescription = it }, label = { Text("Mô tả") }, shape = MaterialTheme.shapes.medium, minLines = 2)
                            Spacer(Modifier.height(8.dp))
                            PrimaryAction(if (editSaving) "Đang lưu..." else "Lưu thay đổi") {
                                if (editName.isBlank()) return@PrimaryAction
                                val auth = SessionManager.authorizationHeader() ?: return@PrimaryAction
                                editSaving = true
                                scope.launch {
                                    try {
                                        ApiClient.updateGroup(auth, groupId, GroupCreateRequest(name = editName.trim(), description = editDescription.blankToNull()))
                                        editing = false
                                        loadData()
                                    } catch (e: Exception) { message = "Không thể cập nhật lớp." } finally { editSaving = false }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Thành viên (${members.size})", fontWeight = FontWeight.Medium, color = AppMuted)
                OutlinedButton(onClick = {
                    searchQuery = ""
                    val auth = SessionManager.authorizationHeader() ?: return@OutlinedButton
                    scope.launch {
                        try {
                            allStudents = ApiClient.getUsers(auth).data.orEmpty()
                                .filter { it.roles?.contains("STUDENT") == true }
                        } catch (_: Exception) {}
                    }
                    showAddDialog = true
                }) {
                    Icon(Icons.Default.PersonAdd, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Thêm học sinh")
                }
            }
            Spacer(Modifier.height(8.dp))

            if (members.isEmpty()) {
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    modifier = Modifier.fillMaxWidth().border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
                ) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Chưa có học sinh nào trong lớp.", color = AppMuted)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(members) { member ->
                        Card(
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = AppSurface),
                            modifier = Modifier.fillMaxWidth().border(1.dp, AppCardBorder, MaterialTheme.shapes.medium)
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                AvatarCircle(member.fullName ?: member.username ?: "?", size = 36)
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(member.fullName ?: member.username ?: "--", fontWeight = FontWeight.Medium)
                                    member.username?.let { Text("@$it", color = AppMuted, style = MaterialTheme.typography.bodySmall) }
                                }
                                IconButton(onClick = {
                                    val auth = SessionManager.authorizationHeader() ?: return@IconButton
                                    scope.launch {
                                        try {
                                            ApiClient.removeGroupMember(auth, groupId, member.userId)
                                            loadData()
                                        } catch (e: Exception) { message = "Không thể xóa học sinh khỏi lớp." }
                                    }
                                }) {
                                    Icon(Icons.Default.Close, "Xóa khỏi lớp", tint = AppRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var selectedIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
        var adding by remember { mutableStateOf(false) }
        val filtered = remember(searchQuery, allStudents) {
            allStudents.filter { s ->
                (s.fullName.contains(searchQuery, ignoreCase = true) ||
                    s.username.contains(searchQuery, ignoreCase = true) ||
                    (s.studentId?.contains(searchQuery, ignoreCase = true) == true)) &&
                    members.none { it.userId == s.id }
            }
        }

        AlertDialog(
            onDismissRequest = { if (!adding) showAddDialog = false },
            title = { Text("Thêm học sinh vào lớp") },
            text = {
                Column {
                    OutlinedTextField(
                        searchQuery, { searchQuery = it },
                        label = { Text("Tìm kiếm học sinh...") },
                        singleLine = true, shape = MaterialTheme.shapes.medium
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Đã chọn ${selectedIds.size} học sinh", color = AppIndigo, style = MaterialTheme.typography.bodySmall)
                        if (filtered.isNotEmpty()) {
                            val allFilteredSelected = filtered.all { it.id in selectedIds }
                            TextButton(onClick = {
                                selectedIds = if (allFilteredSelected) selectedIds - filtered.map { it.id }.toSet()
                                else selectedIds + filtered.map { it.id }.toSet()
                            }) {
                                Text(if (allFilteredSelected) "Bỏ chọn tất cả" else "Chọn tất cả", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    if (message != null) {
                        InfoBanner(message.orEmpty(), AppRed, Icons.Default.ErrorOutline)
                        Spacer(Modifier.height(4.dp))
                        message = null
                    }
                    Spacer(Modifier.height(8.dp))
                    if (allStudents.isEmpty()) {
                        Text("Đang tải danh sách học sinh...", color = AppMuted)
                    } else if (filtered.isEmpty()) {
                        Text("Không tìm thấy học sinh phù hợp.", color = AppMuted)
                    } else {
                        LazyColumn(Modifier.heightIn(max = 300.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            items(filtered) { student ->
                                Row(
                                    Modifier.fillMaxWidth().clickable {
                                        selectedIds = if (student.id in selectedIds) selectedIds - student.id
                                        else selectedIds + student.id
                                    }.padding(vertical = 4.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = student.id in selectedIds,
                                        onCheckedChange = { checked ->
                                            selectedIds = if (checked) selectedIds + student.id
                                            else selectedIds - student.id
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = AppIndigo)
                                    )
                                    AvatarCircle(student.fullName, size = 32)
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(student.fullName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        Text("@${student.username}", color = AppMuted, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (adding) {
                    TextButton(onClick = {}, enabled = false) { CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp) }
                } else {
                    TextButton(
                        onClick = {
                            if (selectedIds.isEmpty()) return@TextButton
                            val auth = SessionManager.authorizationHeader() ?: return@TextButton
                            adding = true
                            scope.launch {
                                try {
                                    val resp = ApiClient.addGroupMember(auth, groupId, selectedIds.toList())
                                    if (resp.success) {
                                        showAddDialog = false
                                        loadData()
                                    } else {
                                        message = resp.message
                                    }
                                } catch (e: Exception) {
                                    message = try {
                                        val body = (e as? retrofit2.HttpException)?.response()?.errorBody()?.string()
                                        if (body != null) com.google.gson.Gson().fromJson(body, ApiResponse::class.java)?.message
                                        else null
                                    } catch (_: Exception) { null } ?: "Không thể thêm học sinh: ${e.message}"
                                } finally { adding = false }
                            }
                        },
                        enabled = selectedIds.isNotEmpty()
                    ) { Text("Thêm (${selectedIds.size})") }
                }
            },
            dismissButton = { TextButton(onClick = { if (!adding) showAddDialog = false }) { Text("Hủy") } }
        )
    }
}

