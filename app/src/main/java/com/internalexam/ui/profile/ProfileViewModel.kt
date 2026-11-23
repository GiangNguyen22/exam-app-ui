package com.internalexam.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.UserProfileResponse
import com.internalexam.ui.exam.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<UserProfileResponse>>(UiState.Loading)
    val uiState: StateFlow<UiState<UserProfileResponse>> = _uiState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val authorization = SessionManager.authorizationHeader()
            if (authorization == null) {
                _uiState.value = UiState.Error("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.")
                return@launch
            }

            try {
                val response = ApiClient.getCurrentUser(authorization)
                val profile = response.data
                if (response.success && profile != null) {
                    _uiState.value = UiState.Success(profile)
                } else {
                    val msg = response.message.ifBlank { "Không thể tải thông tin cá nhân." }
                    _uiState.value = UiState.Error(msg)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Không thể kết nối đến máy chủ. Vui lòng kiểm tra mạng.")
            }
        }
    }
}
