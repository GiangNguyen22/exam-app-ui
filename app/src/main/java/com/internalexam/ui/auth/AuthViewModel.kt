package com.internalexam.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internalexam.data.SessionManager
import com.internalexam.data.auth.toAppRole
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.LoginRequest
import com.internalexam.model.mock.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userRole: Role? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkActiveSession(onResult: () -> Unit) {
        viewModelScope.launch {
            val authorization = SessionManager.authorizationHeader()
            if (authorization != null && SessionManager.hasActiveSession()) {
                runCatching { ApiClient.getCurrentUser(authorization) }
                    .onFailure { exception ->
                        if (exception is HttpException && exception.code() in listOf(401, 403)) {
                            SessionManager.clear()
                        }
                    }
            }
            onResult()
        }
    }

    fun login(username: String, password: String, onLoginSuccess: (Role) -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vui lòng nhập tên đăng nhập và mật khẩu")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = ApiClient.login(LoginRequest(username.trim(), password))
                val token = response.data?.accessToken
                val role = response.data?.roles.toAppRole()

                if (response.success && !token.isNullOrBlank() && role != null) {
                    SessionManager.saveSession(token, role)
                    _uiState.value = _uiState.value.copy(isLoading = false, userRole = role)
                    onLoginSuccess(role)
                } else if (response.success && !token.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Tài khoản của bạn chưa được gán vai trò")
                } else {
                    val msg = response.message.ifBlank { "Đăng nhập thất bại" }
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = msg)
                }
            } catch (exception: HttpException) {
                val msg = when (exception.code()) {
                    401, 403 -> "Tên đăng nhập hoặc mật khẩu không chính xác"
                    else -> "Đăng nhập thất bại. Vui lòng thử lại sau."
                }
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = msg)
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Không thể đăng nhập lúc này. Vui lòng kiểm tra kết nối."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
