package id.siapajar.app.ui.assessment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.siapajar.app.data.local.SiapAjarDatabase
import id.siapajar.app.data.repository.AssessmentRepository
import id.siapajar.app.data.repository.StudentRepository
import id.siapajar.app.domain.model.Assessment
import id.siapajar.app.domain.model.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class StudentProgressItem(
    val student: Student,
    val isAssessed: Boolean,
    val assessmentCount: Int,
    val lastAssessmentType: String = "Catatan Anekdot & Foto",
    val lastAssessmentTime: String = "Hari ini, 09:30",
    val tpCode: String = "TP 1.3"
)

data class AssessmentProgressUiState(
    val totalStudents: Int = 20,
    val assessedCount: Int = 18,
    val unassessedCount: Int = 2,
    val progressPercentage: Float = 0.90f,
    val targetedTpCode: String = "TP 1.3",
    val targetedTpTitle: String = "Menjaga Kebersihan & Mengenal Simbol Negara",
    val weekNumber: Int = 2,
    val semesterNumber: Int = 1,
    val selectedFilter: String = "Semua", // "Semua", "Belum Dinilai", "Sudah Dinilai"
    val allItems: List<StudentProgressItem> = emptyList(),
    val filteredItems: List<StudentProgressItem> = emptyList(),
    val isLoading: Boolean = false
)

class AssessmentProgressViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SiapAjarDatabase.getDatabase(application)
    private val studentRepo = StudentRepository(db.studentDao(), application)
    private val assessmentRepo = AssessmentRepository(db.assessmentDao(), application)

    private val _uiState = MutableStateFlow(AssessmentProgressUiState())
    val uiState: StateFlow<AssessmentProgressUiState> = _uiState.asStateFlow()

    init {
        loadProgressData()
    }

    fun loadProgressData(classId: String = "1") {
        viewModelScope.launch {
            // Immediately combine Room DB students and assessments
            combine(
                studentRepo.getStudentsByClass(classId),
                assessmentRepo.getAllAssessments()
            ) { students, assessments ->
                val items = if (students.isNotEmpty()) {
                    students.map { student ->
                        val matchingAssessments = assessments.filter { it.studentIds.contains(student.id) }
                        val isAssessed = matchingAssessments.isNotEmpty()
                        val lastType = matchingAssessments.firstOrNull()?.instrumentType?.displayName ?: "Catatan Anekdot & Foto Berseri"
                        StudentProgressItem(
                            student = student,
                            isAssessed = isAssessed,
                            assessmentCount = matchingAssessments.size,
                            lastAssessmentType = lastType,
                            lastAssessmentTime = if (isAssessed) "Tercatat Minggu Ini" else "Belum Ada Nilai",
                            tpCode = matchingAssessments.firstOrNull()?.tpCode ?: "TP 1.3"
                        )
                    }
                } else {
                    emptyList()
                }

                val assessed = items.count { it.isAssessed }
                val unassessed = items.count { !it.isAssessed }
                val total = items.size
                val percent = if (total > 0) assessed.toFloat() / total.toFloat() else 0f

                _uiState.value = _uiState.value.copy(
                    totalStudents = total,
                    assessedCount = assessed,
                    unassessedCount = unassessed,
                    progressPercentage = percent,
                    allItems = items,
                    filteredItems = filterList(items, _uiState.value.selectedFilter),
                    isLoading = false
                )
            }.collect {}
        }

        // Fetch agenda in parallel to get active week & TP
        viewModelScope.launch {
            try {
                val agenda = studentRepo.fetchTodayAgenda(classId)
                if (agenda != null) {
                    _uiState.value = _uiState.value.copy(
                        weekNumber = agenda.weekNumber,
                        semesterNumber = agenda.semesterNumber,
                        targetedTpCode = agenda.targetedTpCode.ifBlank { "TP 1.3" },
                        targetedTpTitle = agenda.targetedTpTitle.ifBlank { "Menjaga Kebersihan & Mengenal Simbol Negara" }
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun setFilter(filter: String) {
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            filteredItems = filterList(_uiState.value.allItems, filter)
        )
    }

    private fun filterList(items: List<StudentProgressItem>, filter: String): List<StudentProgressItem> {
        return when (filter) {
            "Belum Dinilai" -> items.filter { !it.isAssessed }
            "Sudah Dinilai" -> items.filter { it.isAssessed }
            else -> items
        }
    }
}
