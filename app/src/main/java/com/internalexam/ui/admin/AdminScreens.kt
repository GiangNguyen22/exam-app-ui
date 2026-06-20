package com.internalexam.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import com.internalexam.data.network.RoleResponse
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
    openRolePermissions: () -> Unit
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
                item { InfoBanner("Chưa có role hoặc permission để cấu hình.", AppAmber, Icons.Default.Security) }
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
    Role.ADMIN -> "Admin"
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

