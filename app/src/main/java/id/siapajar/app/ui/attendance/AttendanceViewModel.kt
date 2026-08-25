package id.siapajar.app.ui.attendance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.siapajar.app.data.local.SiapAjarDatabase
import id.siapajar.app.data.repository.AttendanceRepository
import id.siapajar.app.data.repository.StudentRepository
import id.siapajar.app.domain.model.Attendance
import id.siapajar.app.domain.model.AttendanceStatus
import id.siapajar.app.domain.model.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class AttendanceUiState(
    val students: List<StudentAttendanceState> = emptyList(),
    val currentDateText: String = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(Date()),
    val dateIso: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SiapAjarDatabase.getDatabase(application)
    private val attendanceRepo = AttendanceRepository(db.attendanceDao(), application)
    private val studentRepo = StudentRepository(db.studentDao(), application)

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    init {
        loadStudents()
    }

    private fun loadStudents() {
        viewModelScope.launch {
            studentRepo.getStudentsByClass("1").collect { list ->
                _uiState.value = _uiState.value.copy(
                    students = list.map {
                        StudentAttendanceState(it.id, it.name, it.nis, AttendanceStatus.HADIR)
                    }
                )
            }
        }
        viewModelScope.launch {
            try {
                studentRepo.fetchStudentsFromApi("1")
            } catch (_: Exception) {}
        }
    }

    fun updateStatus(studentId: String, newStatus: AttendanceStatus) {
        val updated = _uiState.value.students.map {
            if (it.id == studentId) it.copy(status = newStatus) else it
        }
        _uiState.value = _uiState.value.copy(students = updated)
    }

    fun markAllPresent() {
        val updated = _uiState.value.students.map { it.copy(status = AttendanceStatus.HADIR) }
        _uiState.value = _uiState.value.copy(students = updated)
    }

    fun submitAttendance(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val domainItems = _uiState.value.students.map {
                Attendance(
                    id = UUID.randomUUID().toString(),
                    studentId = it.id,
                    studentName = it.name,
                    date = _uiState.value.dateIso,
                    status = it.status,
                    notes = null,
                    syncStatus = SyncStatus.PENDING
                )
            }
            val result = attendanceRepo.saveAttendances(domainItems, _uiState.value.dateIso, "1")
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Gagal menyimpan presensi"
                    )
                }
            )
        }
    }
}
