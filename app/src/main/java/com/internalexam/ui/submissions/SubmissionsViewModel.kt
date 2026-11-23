package com.internalexam.ui.submissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internalexam.data.repository.SubmissionRepository
import com.internalexam.data.repository.SubmissionRepositoryImpl
import com.internalexam.model.Submission
import com.internalexam.ui.exam.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SubmissionStats(
    val totalCount: Int = 0,
    val avgScore: Double = 0.0,
    val passRate: Int = 0,
    val maxScore: Double = 0.0
)

enum class SortMode {
    SCORE_DESC,
    SCORE_ASC,
    NAME_ASC,
    TIME_DESC
}

class SubmissionsViewModel(
    private val repository: SubmissionRepository = SubmissionRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Submission>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Submission>>> = _uiState.asStateFlow()

    private val _statsState = MutableStateFlow(SubmissionStats())
    val statsState: StateFlow<SubmissionStats> = _statsState.asStateFlow()

    private var allSubmissions: List<Submission> = emptyList()
    private var searchQuery: String = ""
    private var currentFilterStatus: String = "ALL"
    private var currentSortMode: SortMode = SortMode.SCORE_DESC

    fun loadSubmissions(examId: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                allSubmissions = repository.getSubmissionsByExamId(examId)
                calculateStats(allSubmissions)
                applyFiltersAndSort()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    e.localizedMessage ?: "Có lỗi xảy ra khi tải danh sách bài nộp"
                )
            }
        }
    }

    fun search(query: String) {
        searchQuery = query
        applyFiltersAndSort()
    }

    fun filterByStatus(status: String) {
        currentFilterStatus = status
        applyFiltersAndSort()
    }

    fun setSortMode(sortMode: SortMode) {
        currentSortMode = sortMode
        applyFiltersAndSort()
    }

    private fun calculateStats(submissions: List<Submission>) {
        if (submissions.isEmpty()) {
            _statsState.value = SubmissionStats()
            return
        }
        val count = submissions.size
        val avg = submissions.map { it.score }.average()
        val max = submissions.maxOfOrNull { it.score } ?: 0.0
        val passCount = submissions.count { it.score >= 5.0 }
        val passPercent = ((passCount.toDouble() / count) * 100).toInt()

        _statsState.value = SubmissionStats(
            totalCount = count,
            avgScore = (avg * 10).toInt() / 10.0,
            passRate = passPercent,
            maxScore = max
        )
    }

    private fun applyFiltersAndSort() {
        var result = allSubmissions

        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            result = result.filter {
                it.studentName.lowercase().contains(q) || it.studentId.lowercase().contains(q)
            }
        }

        if (currentFilterStatus != "ALL") {
            result = result.filter { it.status.equals(currentFilterStatus, ignoreCase = true) }
        }

        result = when (currentSortMode) {
            SortMode.SCORE_DESC -> result.sortedByDescending { it.score }
            SortMode.SCORE_ASC -> result.sortedBy { it.score }
            SortMode.NAME_ASC -> result.sortedBy { it.studentName }
            SortMode.TIME_DESC -> result.sortedByDescending { it.submittedAt }
        }

        _uiState.value = UiState.Success(result)
    }
}
