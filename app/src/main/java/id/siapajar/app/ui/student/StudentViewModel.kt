package id.siapajar.app.ui.student

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.siapajar.app.data.local.SiapAjarDatabase
import id.siapajar.app.data.remote.StudentTimelineDto
import id.siapajar.app.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentDetailUiState(
    val studentName: String = "Aisyah Putri Azzahra",
    val nis: String = "202602",
    val classText: String = "TK B1",
    val ageText: String = "5 Tahun 4 Bulan",
    val avatarUrl: String = "https://images.unsplash.com/photo-1595454223600-91fbdd77e268?w=300&auto=format&fit=crop&q=80",
    val totalAssessments: Int = 8,
    val selectedFilter: String = "Semua",
    val timeline: List<StudentTimelineDto> = emptyList(),
    val isLoading: Boolean = false
)

class StudentViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SiapAjarDatabase.getDatabase(application)
    private val studentRepo = StudentRepository(db.studentDao(), application)

    private val _detailState = MutableStateFlow(StudentDetailUiState())
    val detailState: StateFlow<StudentDetailUiState> = _detailState.asStateFlow()

    fun loadStudentDetail(studentId: String) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isLoading = true)
            val timeline = studentRepo.fetchStudentTimeline(studentId)

            val studentName = when (studentId) {
                "2" -> "Kenzo Alvaro"
                "3" -> "Ahmad Rayhan"
                "4" -> "Bilqis Humaira"
                "5" -> "Fathir Rahman"
                else -> "Aisyah Putri Azzahra"
            }
            val nis = when (studentId) {
                "2" -> "202603"
                "3" -> "202601"
                "4" -> "202604"
                "5" -> "202605"
                else -> "202602"
            }
            val avatar = when (studentId) {
                "2" -> "https://images.unsplash.com/photo-1543332164-6e82f355badc?w=300&auto=format&fit=crop&q=80"
                "3" -> "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=300&auto=format&fit=crop&q=80"
                "4" -> "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&auto=format&fit=crop&q=80"
                "5" -> "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80"
                else -> "https://images.unsplash.com/photo-1595454223600-91fbdd77e268?w=300&auto=format&fit=crop&q=80"
            }

            _detailState.value = _detailState.value.copy(
                studentName = studentName,
                nis = nis,
                avatarUrl = avatar,
                timeline = timeline,
                totalAssessments = if (timeline.isNotEmpty()) timeline.size else 8,
                isLoading = false
            )
        }
    }

    fun setFilter(filterName: String) {
        _detailState.value = _detailState.value.copy(selectedFilter = filterName)
    }
}
