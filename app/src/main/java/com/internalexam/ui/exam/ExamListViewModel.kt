package com.internalexam.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internalexam.data.repository.ExamRepository
import com.internalexam.data.repository.ExamRepositoryImpl
import com.internalexam.model.Exam
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExamListViewModel(
    private val repository: ExamRepository = ExamRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Exam>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Exam>>> = _uiState.asStateFlow()

    private var allExams: List<Exam> = emptyList()
    private var currentFilter: String = "ALL"

    init {
        getExams()
    }

    fun getExams() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                allExams = repository.getExams()
                applyFilter(currentFilter)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Có lỗi xảy ra khi tải danh sách bài thi")
            }
        }
    }

    fun filterByStatus(status: String) {
        currentFilter = status
        applyFilter(status)
    }

    private fun applyFilter(status: String) {
        val filtered = when (status) {
            "UPCOMING" -> allExams.filter { it.status == "UPCOMING" }
            "ONGOING" -> allExams.filter { it.status == "ONGOING" }
            "FINISHED" -> allExams.filter { it.status == "FINISHED" }
            else -> allExams
        }
        _uiState.value = UiState.Success(filtered)
    }
}
