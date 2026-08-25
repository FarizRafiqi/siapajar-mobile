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
    val teacherName: String = "Bapak/Ibu Guru",
    val schoolName: String = "SiapAjar",
    val activeClassName: String = "Kelompok B",
    val activeClassId: String = "1",
    val availableClasses: List<id.siapajar.app.data.remote.ClassDto> = emptyList(),
    val todayAgenda: TodayAgenda = TodayAgenda(),
    val recentAssessments: List<Assessment> = emptyList(),
    val totalRecordedToday: Int = 0,
    val totalStudents: Int = 0,
    val presentCount: Int = 0,
    val unrecordedCount: Int = 0,
    val unassessedStudentNames: List<String> = emptyList(),
    val isSyncing: Boolean = false,
    val showClassPicker: Boolean = false
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
        // 1. Load user session profile
        val session = tokenManager.sessionState.value
        _uiState.value = _uiState.value.copy(
            teacherName = session.fullName ?: "Bapak/Ibu Guru",
            schoolName = session.schoolName ?: "SiapAjar"
        )

        // 2. Fetch classes & agenda asynchronously
        viewModelScope.launch {
            try {
                val classes = studentRepo.fetchClasses()
                if (classes.isNotEmpty()) {
                    val first = classes.first()
                    _uiState.value = _uiState.value.copy(
                        availableClasses = classes,
                        activeClassId = first.id,
                        activeClassName = first.displayName ?: first.name
                    )
                }
            } catch (_: Exception) {}

            try {
                val agendaDto = studentRepo.fetchTodayAgenda(_uiState.value.activeClassId)
                if (agendaDto != null && agendaDto.topicTitle.isNotBlank()) {
                    _uiState.value = _uiState.value.copy(
                        todayAgenda = TodayAgenda(
                            weekNumber = agendaDto.weekNumber,
                            semesterNumber = agendaDto.semesterNumber,
                            topicTitle = agendaDto.topicTitle,
                            subTopic = agendaDto.subTopic,
                            todayActivity = agendaDto.todayActivity,
                            targetedTpCode = agendaDto.targetedTpCode,
                            targetedTpTitle = agendaDto.targetedTpTitle,
                            stage = agendaDto.stage,
                            openingActivities = agendaDto.openingActivities,
                            openingQuestions = agendaDto.openingQuestions,
                            coreActivities = agendaDto.coreActivities.map {
                                id.siapajar.app.domain.model.CoreActivity(
                                    id = it.id,
                                    name = it.name,
                                    focus = it.focus,
                                    materials = it.materials,
                                    instructions = it.instructions,
                                    benefits = it.benefits,
                                    isPrimary = it.isPrimary
                                )
                            },
                            closingActivities = agendaDto.closingActivities
                        )
                    )
                }
            } catch (_: Exception) {}
        }

        // 3. Collect local assessments and students
        viewModelScope.launch {
            combine(
                studentRepo.getStudentsByClass(_uiState.value.activeClassId),
                assessmentRepo.getAllAssessments()
            ) { students, assessments ->
                val total = students.size
                val assessedStudents = students.filter { s -> assessments.any { it.studentIds.contains(s.id) } }
                val unassessedList = students.filter { s -> assessments.none { it.studentIds.contains(s.id) } }.map { it.name }
                val assessedCount = assessedStudents.size

                _uiState.value = _uiState.value.copy(
                    totalStudents = total,
                    totalRecordedToday = assessedCount,
                    presentCount = assessedCount,
                    unrecordedCount = (total - assessedCount).coerceAtLeast(0),
                    unassessedStudentNames = unassessedList,
                    recentAssessments = assessments.take(5)
                )
            }.collect {}
        }
    }

    fun selectClass(classItem: id.siapajar.app.data.remote.ClassDto) {
        val displayName = classItem.displayName ?: classItem.name
        _uiState.value = _uiState.value.copy(
            activeClassId = classItem.id,
            activeClassName = displayName,
            showClassPicker = false
        )
        loadData()
    }

    fun setShowClassPicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(showClassPicker = show)
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepo.logout()
            onLoggedOut()
        }
    }
}
