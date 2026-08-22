package id.siapajar.app.ui.assessment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.siapajar.app.data.local.SiapAjarDatabase
import id.siapajar.app.data.repository.AssessmentRepository
import id.siapajar.app.data.repository.StudentRepository
import id.siapajar.app.domain.model.InstrumentType
import id.siapajar.app.domain.model.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AssessmentFormUiState(
    val availableStudents: List<Student> = emptyList(),
    val selectedStudentIds: Set<String> = emptySet(),
    val selectedInstrument: InstrumentType = InstrumentType.CATATAN_ANEKDOT,
    val photoPath: String? = null,
    val notes: String = "",
    val tpCode: String = "TP 1.3",
    val tpTitle: String = "Menjaga Kebersihan & Rasa Ingin Tahu",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AssessmentViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SiapAjarDatabase.getDatabase(application)
    private val assessmentRepo = AssessmentRepository(db.assessmentDao(), application)
    private val studentRepo = StudentRepository(db.studentDao(), application)

    private val _uiState = MutableStateFlow(AssessmentFormUiState())
    val uiState: StateFlow<AssessmentFormUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
    }

    private fun loadStudents() {
        viewModelScope.launch {
            // First fetch from API or Room
            studentRepo.fetchStudentsFromApi("1")
            studentRepo.getStudentsByClass("1").collect { list ->
                if (list.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        availableStudents = list,
                        selectedStudentIds = if (_uiState.value.selectedStudentIds.isEmpty()) setOf(list.first().id) else _uiState.value.selectedStudentIds
                    )
                } else {
                    // Default fallback students
                    val mockList = listOf(
                        Student("1", "Kenzo Alvaro", "10234", null, "1", "TK B1"),
                        Student("2", "Aisyah Putri", "10235", null, "1", "TK B1"),
                        Student("3", "Fathan Rasyid", "10236", null, "1", "TK B1"),
                        Student("4", "Naura Hasna", "10237", null, "1", "TK B1")
                    )
                    _uiState.value = _uiState.value.copy(
                        availableStudents = mockList,
                        selectedStudentIds = setOf("1", "2")
                    )
                }
            }
        }
    }

    fun toggleStudentSelection(studentId: String) {
        val current = _uiState.value.selectedStudentIds.toMutableSet()
        if (current.contains(studentId)) {
            if (current.size > 1) current.remove(studentId)
        } else {
            current.add(studentId)
        }
        _uiState.value = _uiState.value.copy(selectedStudentIds = current)
    }

    fun setInstrument(type: InstrumentType) {
        _uiState.value = _uiState.value.copy(selectedInstrument = type)
    }

    fun setPhotoPath(path: String?) {
        _uiState.value = _uiState.value.copy(photoPath = path)
    }

    fun setNotes(text: String) {
        _uiState.value = _uiState.value.copy(notes = text, errorMessage = null)
    }

    fun saveAssessment(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.selectedStudentIds.isEmpty()) {
            _uiState.value = state.copy(errorMessage = "Pilih minimal 1 siswa")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val studentNames = state.availableStudents
                .filter { state.selectedStudentIds.contains(it.id) }
                .map { it.name }

            val result = assessmentRepo.saveAssessment(
                studentIds = state.selectedStudentIds.toList(),
                studentNames = studentNames,
                instrumentType = state.selectedInstrument,
                photoPath = state.photoPath,
                notes = state.notes.ifBlank { "Anak menunjukkan partisipasi aktif dalam kegiatan pembelajaran hari ini." },
                tpCode = state.tpCode,
                tpTitle = state.tpTitle
            )

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Gagal menyimpan asesmen"
                    )
                }
            )
        }
    }
}
