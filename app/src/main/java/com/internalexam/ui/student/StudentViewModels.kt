package com.internalexam.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.ExamResponse
import com.internalexam.ui.exam.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentHomeState(
    val studentName: String = "Học sinh",
    val exams: List<ExamResponse> = emptyList(),
    val examMetadata: Map<Long, StudentExamMetadata> = emptyMap(),
    val recentResults: List<StudentRecentResult> = emptyList(),
    val submittedExamIds: Set<Long> = emptySet(),
    val errorMessage: String? = null
)

class StudentHomeViewModel : ViewModel() {

    private val _state = MutableStateFlow<UiState<StudentHomeState>>(UiState.Loading)
    val state: StateFlow<UiState<StudentHomeState>> = _state.asStateFlow()

    fun loadHomeData() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val authorization = SessionManager.authorizationHeader()
            if (authorization == null) {
                _state.value = UiState.Error("Vui lòng đăng nhập lại.")
                return@launch
            }

            var studentName = "Học sinh"
            runCatching { ApiClient.getCurrentUser(authorization).data }.getOrNull()?.let { profile ->
                studentName = profile.fullName.takeIf { it.isNotBlank() } ?: profile.username
            }

            try {
                val response = ApiClient.getExams(authorization)
                val loadedExams = response.data.orEmpty()
                val visibleExams = loadedExams.filterNot { it.isExpired() }

                val subjectNamesById = runCatching {
                    ApiClient.getSubjects(authorization).data.orEmpty().associate { it.id to it.name }
                }.getOrDefault(emptyMap())

                val (metadata, recentResults) = coroutineScope {
                    val metadataDeferred = visibleExams.map { exam ->
                        async {
                            val countFromExam = exam.questionCount ?: exam.totalQuestions
                            val subjectFromExam = exam.subjectName ?: exam.subject
                            if (countFromExam != null && !subjectFromExam.isNullOrBlank()) {
                                exam.id to StudentExamMetadata(
                                    questionCount = countFromExam,
                                    subjectName = subjectFromExam
                                )
                            } else {
                                val questions = runCatching {
                                    ApiClient.getExamQuestions(authorization, exam.id).data.orEmpty()
                                }.getOrDefault(emptyList())
                                val subjectIds = questions.mapNotNull { it.subjectId }.distinct()
                                val subjectName = when {
                                    subjectIds.size == 1 -> subjectNamesById[subjectIds.first()]
                                    subjectIds.size > 1 -> "Nhiều môn"
                                    else -> subjectFromExam
                                }
                                exam.id to StudentExamMetadata(
                                    questionCount = questions.size.takeIf { it > 0 } ?: countFromExam,
                                    subjectName = subjectName
                                )
                            }
                        }
                    }

                    val resultsDeferred = loadedExams.map { exam ->
                        async {
                            runCatching {
                                ApiClient.getResult(authorization, exam.id).data?.let { result ->
                                    StudentRecentResult(exam, result)
                                }
                            }.getOrNull()
                        }
                    }

                    metadataDeferred.awaitAll().toMap() to resultsDeferred.awaitAll().filterNotNull()
                        .sortedByDescending { it.result.submittedAt.orEmpty() }
                }

                val submittedIds = recentResults.filter { it.result.status == "SUBMITTED" }.map { it.exam.id }.toSet()

                _state.value = UiState.Success(
                    StudentHomeState(
                        studentName = studentName,
                        exams = visibleExams,
                        examMetadata = metadata,
                        recentResults = recentResults,
                        submittedExamIds = submittedIds,
                        errorMessage = null
                    )
                )
            } catch (e: Exception) {
                _state.value = UiState.Error("Không tải được đề thi lúc này.")
            }
        }
    }
}
