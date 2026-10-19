package com.internalexam.ui.submissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internalexam.data.repository.SubmissionDetailRepository
import com.internalexam.data.repository.SubmissionDetailRepositoryImpl
import com.internalexam.model.SubmissionDetailHeader
import com.internalexam.model.SubmissionQuestionItem
import com.internalexam.ui.exam.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubmissionDetailViewModel(
    private val repository: SubmissionDetailRepository = SubmissionDetailRepositoryImpl()
) : ViewModel() {

    private val _headerState = MutableStateFlow<SubmissionDetailHeader?>(null)
    val headerState: StateFlow<SubmissionDetailHeader?> = _headerState.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<List<SubmissionQuestionItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<SubmissionQuestionItem>>> = _uiState.asStateFlow()

    private var allQuestions: List<SubmissionQuestionItem> = emptyList()
    private var currentTabPosition: Int = 0

    fun loadSubmissionDetail(submissionId: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                _headerState.value = repository.getHeader(submissionId)
                allQuestions = repository.getQuestionItems(submissionId)
                applyTabFilter(currentTabPosition)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    e.localizedMessage ?: "Không thể tải chi tiết bài nộp"
                )
            }
        }
    }

    fun filterByTab(tabPosition: Int) {
        currentTabPosition = tabPosition
        applyTabFilter(tabPosition)
    }

    private fun applyTabFilter(position: Int) {
        val filtered = if (position == 1) {
            allQuestions.filter { !it.studentAnswer.isCorrect }
        } else {
            allQuestions
        }
        _uiState.value = UiState.Success(filtered)
    }
}
