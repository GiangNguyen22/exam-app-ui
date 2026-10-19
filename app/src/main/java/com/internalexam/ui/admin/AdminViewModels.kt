package com.internalexam.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.AuditLogResponse
import com.internalexam.data.network.RbacMatrixResponse
import com.internalexam.data.network.StudentGroupDetailResponse
import com.internalexam.data.network.StudentGroupResponse
import com.internalexam.data.network.UserCreateRequest
import com.internalexam.data.network.UserProfileResponse
import com.internalexam.ui.exam.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserManagementViewModel : ViewModel() {

    private val _usersState = MutableStateFlow<UiState<List<UserProfileResponse>>>(UiState.Loading)
    val usersState: StateFlow<UiState<List<UserProfileResponse>>> = _usersState.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            val auth = SessionManager.authorizationHeader()
            if (auth == null) {
                _usersState.value = UiState.Error("Vui lòng đăng nhập lại.")
                return@launch
            }
            try {
                val users = ApiClient.getUsers(auth).data.orEmpty()
                _usersState.value = UiState.Success(users)
            } catch (e: Exception) {
                _usersState.value = UiState.Error("Không tải được danh sách người dùng.")
            }
        }
    }

    fun createUser(request: UserCreateRequest, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val auth = SessionManager.authorizationHeader() ?: return@launch
            try {
                ApiClient.createUser(auth, request)
                loadUsers()
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }

    fun toggleUserLock(user: UserProfileResponse) {
        viewModelScope.launch {
            val auth = SessionManager.authorizationHeader() ?: return@launch
            try {
                ApiClient.toggleLockUser(auth, user.id)
                loadUsers()
            } catch (_: Exception) {}
        }
    }
}

class AdminAuditLogViewModel : ViewModel() {

    private val _logsState = MutableStateFlow<UiState<List<AuditLogResponse>>>(UiState.Loading)
    val logsState: StateFlow<UiState<List<AuditLogResponse>>> = _logsState.asStateFlow()

    fun loadLogs() {
        viewModelScope.launch {
            _logsState.value = UiState.Loading
            val auth = SessionManager.authorizationHeader()
            if (auth == null) {
                _logsState.value = UiState.Error("Vui lòng đăng nhập lại.")
                return@launch
            }
            try {
                val logs = ApiClient.getAuditLogs(auth).data.orEmpty()
                _logsState.value = UiState.Success(logs)
            } catch (e: Exception) {
                _logsState.value = UiState.Error("Không tải được nhật ký hệ thống.")
            }
        }
    }
}

class RolePermissionViewModel : ViewModel() {

    private val _rbacState = MutableStateFlow<UiState<RbacMatrixResponse>>(UiState.Loading)
    val rbacState: StateFlow<UiState<RbacMatrixResponse>> = _rbacState.asStateFlow()

    fun loadRbac() {
        viewModelScope.launch {
            _rbacState.value = UiState.Loading
            val auth = SessionManager.authorizationHeader()
            if (auth == null) {
                _rbacState.value = UiState.Error("Vui lòng đăng nhập lại.")
                return@launch
            }
            try {
                val rbac = ApiClient.getRbacMatrix(auth).data
                if (rbac != null) {
                    _rbacState.value = UiState.Success(rbac)
                } else {
                    _rbacState.value = UiState.Error("Chưa có dữ liệu phân quyền.")
                }
            } catch (e: Exception) {
                _rbacState.value = UiState.Error("Không tải được bảng phân quyền.")
            }
        }
    }
}

class AdminGroupViewModel : ViewModel() {

    private val _groupsState = MutableStateFlow<UiState<List<StudentGroupResponse>>>(UiState.Loading)
    val groupsState: StateFlow<UiState<List<StudentGroupResponse>>> = _groupsState.asStateFlow()

    private val _groupDetailState = MutableStateFlow<UiState<StudentGroupDetailResponse>>(UiState.Loading)
    val groupDetailState: StateFlow<UiState<StudentGroupDetailResponse>> = _groupDetailState.asStateFlow()

    fun loadGroups() {
        viewModelScope.launch {
            _groupsState.value = UiState.Loading
            val auth = SessionManager.authorizationHeader()
            if (auth == null) {
                _groupsState.value = UiState.Error("Vui lòng đăng nhập lại.")
                return@launch
            }
            try {
                val groups = ApiClient.getGroups(auth).data.orEmpty()
                _groupsState.value = UiState.Success(groups)
            } catch (e: Exception) {
                _groupsState.value = UiState.Error("Không tải được danh sách nhóm/lớp.")
            }
        }
    }

    fun loadGroupDetail(groupId: Long) {
        viewModelScope.launch {
            _groupDetailState.value = UiState.Loading
            val auth = SessionManager.authorizationHeader()
            if (auth == null) {
                _groupDetailState.value = UiState.Error("Vui lòng đăng nhập lại.")
                return@launch
            }
            try {
                val detail = ApiClient.getGroupDetail(auth, groupId).data
                if (detail != null) {
                    _groupDetailState.value = UiState.Success(detail)
                } else {
                    _groupDetailState.value = UiState.Error("Không tìm thấy chi tiết nhóm/lớp.")
                }
            } catch (e: Exception) {
                _groupDetailState.value = UiState.Error("Không tải được chi tiết nhóm/lớp.")
            }
        }
    }
}
