package com.internalexam.ui.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.ExamReportResponse
import com.internalexam.data.network.ExamResponse
import com.internalexam.data.network.ProctoringEventResponse
import com.internalexam.data.network.ProctoringSummaryResponse
import com.internalexam.data.network.QuestionResponse
import com.internalexam.data.network.StudentGroupResponse
import com.internalexam.data.network.SubjectResponse
import com.internalexam.data.network.TopicResponse
import com.internalexam.ui.exam.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeacherExamViewModel : ViewModel() {

    private val _examsState = MutableStateFlow<UiState<List<ExamResponse>>>(UiState.Loading)
    val examsState: StateFlow<UiState<List<ExamResponse>>> = _examsState.asStateFlow()

    private val _subjectsState = MutableStateFlow<List<SubjectResponse>>(emptyList())
    val subjectsState: StateFlow<List<SubjectResponse>> = _subjectsState.asStateFlow()

    private val _groupsState = MutableStateFlow<List<StudentGroupResponse>>(emptyList())
    val groupsState: StateFlow<List<StudentGroupResponse>> = _groupsState.asStateFlow()

    fun loadExams() {
        viewModelScope.launch {
            _examsState.value = UiState.Loading
            val auth = SessionManager.authorizationHeader()
            if (auth == null) {
                _examsState.value = UiState.Error("Vui lòng đăng nhập lại.")
                return@launch
            }
            try {
                val exams = ApiClient.getExams(auth).data.orEmpty()
                _examsState.value = UiState.Success(exams)
            } catch (e: Exception) {
                _examsState.value = UiState.Error("Không tải được danh sách đề thi.")
            }
        }
    }

    fun loadCatalogs() {
        viewModelScope.launch {
            val auth = SessionManager.authorizationHeader() ?: return@launch
            runCatching {
                _subjectsState.value = ApiClient.getSubjects(auth).data.orEmpty()
                _groupsState.value = ApiClient.getGroups(auth).data.orEmpty()
            }
        }
    }
}

class QuestionBankViewModel : ViewModel() {

    private val _questionsState = MutableStateFlow<UiState<List<QuestionResponse>>>(UiState.Loading)
    val questionsState: StateFlow<UiState<List<QuestionResponse>>> = _questionsState.asStateFlow()

    private val _subjectsState = MutableStateFlow<List<SubjectResponse>>(emptyList())
    val subjectsState: StateFlow<List<SubjectResponse>> = _subjectsState.asStateFlow()

    private val _topicsState = MutableStateFlow<List<TopicResponse>>(emptyList())
    val topicsState: StateFlow<List<TopicResponse>> = _topicsState.asStateFlow()

    fun loadQuestions() {
        viewModelScope.launch {
            _questionsState.value = UiState.Loading
            val auth = SessionManager.authorizationHeader()
            if (auth == null) {
                _questionsState.value = UiState.Error("Vui lòng đăng nhập lại.")
                return@launch
            }
            try {
                val questions = ApiClient.getQuestions(auth).data.orEmpty()
                _questionsState.value = UiState.Success(questions)
            } catch (e: Exception) {
                _questionsState.value = UiState.Error("Không tải được danh sách câu hỏi.")
            }
        }
    }

    fun loadSubjects() {
        viewModelScope.launch {
            val auth = SessionManager.authorizationHeader() ?: return@launch
            runCatching {
                _subjectsState.value = ApiClient.getSubjects(auth).data.orEmpty()
            }
        }
    }

    fun loadTopicsForSubject(subjectId: Long) {
        viewModelScope.launch {
            val auth = SessionManager.authorizationHeader() ?: return@launch
            runCatching {
                _topicsState.value = ApiClient.getTopics(auth, subjectId).data.orEmpty()
            }
        }
    }
}

class LiveMonitoringViewModel : ViewModel() {

    private val _eventsState = MutableStateFlow<List<ProctoringEventResponse>>(emptyList())
    val eventsState: StateFlow<List<ProctoringEventResponse>> = _eventsState.asStateFlow()

    private val _summaryState = MutableStateFlow<ProctoringSummaryResponse?>(null)
    val summaryState: StateFlow<ProctoringSummaryResponse?> = _summaryState.asStateFlow()

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

    fun startMonitoring(examId: Long) {
        _isMonitoring.value = true
        viewModelScope.launch {
            while (_isMonitoring.value) {
                val auth = SessionManager.authorizationHeader() ?: break
                runCatching {
                    val events = ApiClient.getProctoringEvents(auth, examId).data.orEmpty()
                    val summary = ApiClient.getProctoringSummary(auth, examId).data
                    _eventsState.value = events.sortedByDescending { it.timestamp }
                    _summaryState.value = summary
                }
                delay(3000L)
            }
        }
    }

    fun stopMonitoring() {
        _isMonitoring.value = false
    }
}

class TeacherReportViewModel : ViewModel() {

    private val _reportState = MutableStateFlow<UiState<ExamReportResponse>>(UiState.Loading)
    val reportState: StateFlow<UiState<ExamReportResponse>> = _reportState.asStateFlow()

    fun loadReport(examId: Long) {
        viewModelScope.launch {
            _reportState.value = UiState.Loading
            val auth = SessionManager.authorizationHeader()
            if (auth == null) {
                _reportState.value = UiState.Error("Vui lòng đăng nhập lại.")
                return@launch
            }
            try {
                val response = ApiClient.getExamReport(auth, examId)
                val report = response.data
                if (report != null) {
                    _reportState.value = UiState.Success(report)
                } else {
                    _reportState.value = UiState.Error("Chưa có báo cáo cho đề thi này.")
                }
            } catch (e: Exception) {
                _reportState.value = UiState.Error("Không tải được báo cáo đề thi.")
            }
        }
    }
}
