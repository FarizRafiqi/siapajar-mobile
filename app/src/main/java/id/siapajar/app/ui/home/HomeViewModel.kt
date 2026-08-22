package id.siapajar.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.siapajar.app.data.local.SiapAjarDatabase
import id.siapajar.app.data.local.TokenManager
import id.siapajar.app.data.repository.AssessmentRepository
import id.siapajar.app.data.repository.AuthRepository
import id.siapajar.app.data.repository.StudentRepository
import id.siapajar.app.domain.model.Assessment
import id.siapajar.app.domain.model.TodayAgenda
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val teacherName: String = "Guru SiapAjar",
    val schoolName: String = "TK Pembina",
    val activeClassName: String = "TK B1",
    val todayAgenda: TodayAgenda = TodayAgenda(
        topicTitle = "Mengenal Tanaman Obat & Apotek Hidup",
        todayActivity = "Eksplorasi Daun Mint & Menggambar Bentuk Daun",
        targetedTpCode = "TP 1.3",
        targetedTpTitle = "Menjaga Kebersihan & Rasa Ingin Tahu"
    ),
    val recentAssessments: List<Assessment> = emptyList(),
    val totalRecordedToday: Int = 18,
    val totalStudents: Int = 24,
    val isSyncing: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager.getInstance(application)
    private val db = SiapAjarDatabase.getDatabase(application)
    private val studentRepo = StudentRepository(db.studentDao(), application)
    private val assessmentRepo = AssessmentRepository(db.assessmentDao(), application)
    private val authRepo = AuthRepository(application, tokenManager)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            // Load user session profile
            val session = tokenManager.sessionState.value
            _uiState.value = _uiState.value.copy(
                teacherName = session.fullName ?: "Guru SiapAjar",
                schoolName = session.schoolName ?: "TK / RA SiapAjar"
            )

            // Fetch active classes & today's agenda from API
            try {
                val agendaDto = studentRepo.fetchTodayAgenda("1")
                if (agendaDto != null) {
                    _uiState.value = _uiState.value.copy(
                        todayAgenda = TodayAgenda(
                            topicTitle = agendaDto.topicTitle,
                            todayActivity = agendaDto.todayActivity,
                            targetedTpCode = agendaDto.targetedTpCode,
                            targetedTpTitle = agendaDto.targetedTpTitle
                        )
                    )
                }
            } catch (_: Exception) {}
        }

        // Collect assessments from Room DB
        viewModelScope.launch {
            assessmentRepo.getAllAssessments().collect { list ->
                _uiState.value = _uiState.value.copy(
                    recentAssessments = list.take(5),
                    totalRecordedToday = if (list.isNotEmpty()) list.size else 18
                )
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepo.logout()
            onLoggedOut()
        }
    }
}
