package id.siapajar.app.ui.student

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.siapajar.app.data.local.SiapAjarDatabase
import id.siapajar.app.data.remote.StudentTimelineDto
import id.siapajar.app.data.repository.AssessmentRepository
import id.siapajar.app.data.repository.StudentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StudentListItem(
    val id: String,
    val name: String,
    val nis: String,
    val ageText: String = "5-6 Tahun",
    val avatarUrl: String? = null,
    val assessmentCount: Int = 0,
    val lastAssessmentDate: String = "Tersimpan"
)

data class StudentListUiState(
    val students: List<StudentListItem> = emptyList(),
    val className: String = "Kelompok B (TK B1)",
    val totalStudents: Int = 0,
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)

data class StudentDetailUiState(
    val id: String = "1",
    val studentName: String = "Siswa",
    val nis: String = "-",
    val classText: String = "TK B1",
    val ageText: String = "5-6 Tahun",
    val avatarUrl: String? = null,
    val totalAssessments: Int = 0,
    val selectedFilter: String = "Semua",
    val timeline: List<StudentTimelineDto> = emptyList(),
    val isLoading: Boolean = false
)

class StudentViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SiapAjarDatabase.getDatabase(application)
    private val studentRepo = StudentRepository(db.studentDao(), application)
    private val assessmentRepo = AssessmentRepository(db.assessmentDao(), application)

    private val _listState = MutableStateFlow(StudentListUiState(isLoading = true))
    val listState: StateFlow<StudentListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(StudentDetailUiState())
    val detailState: StateFlow<StudentDetailUiState> = _detailState.asStateFlow()

    init {
        loadStudents()
    }

    fun loadStudents(classId: String = "1") {
        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true)

            // 1. Fetch classes to get proper display name
            try {
                val classes = studentRepo.fetchClasses()
                val activeClass = classes.find { it.id == classId } ?: classes.firstOrNull()
                if (activeClass != null) {
                    val displayName = activeClass.displayName ?: activeClass.name
                    _listState.value = _listState.value.copy(className = displayName)
                }
            } catch (_: Exception) {}

            // 2. Sync fresh student list from API to Room
            try {
                studentRepo.fetchStudentsFromApi(classId)
            } catch (_: Exception) {}

            // 3. Observe local Room database students & local assessments
            combine(
                studentRepo.getStudentsByClass(classId),
                assessmentRepo.getAllAssessments()
            ) { studentEntities, allAssessments ->
                if (studentEntities.isNotEmpty() && _listState.value.className == "Kelompok B (TK B1)") {
                    studentEntities.firstOrNull()?.className?.let { name ->
                        _listState.value = _listState.value.copy(className = name)
                    }
                }
                val items = studentEntities.map { student ->
                    val count = allAssessments.count { it.studentIds.contains(student.id) }
                    StudentListItem(
                        id = student.id,
                        name = student.name,
                        nis = student.nis,
                        ageText = "5-6 Tahun",
                        avatarUrl = student.photoUrl,
                        assessmentCount = count,
                        lastAssessmentDate = if (count > 0) "Tercatat" else "Belum ada"
                    )
                }
                items
            }.collect { items ->
                _listState.value = _listState.value.copy(
                    students = items,
                    totalStudents = items.size,
                    isLoading = false
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _listState.value = _listState.value.copy(searchQuery = query)
    }

    fun loadStudentDetail(studentId: String) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isLoading = true)

            // 1. Query local Room database for student basic info
            val localStudent = db.studentDao().getStudentById(studentId)
            val studentName = localStudent?.name ?: "Siswa"
            val nis = localStudent?.nis ?: "-"
            val avatar = localStudent?.photoUrl
            val classText = localStudent?.className ?: "TK B1"

            // 2. Fetch timeline from API
            val apiTimeline = try {
                studentRepo.fetchStudentTimeline(studentId)
            } catch (_: Exception) {
                emptyList()
            }

            // 3. Collect local assessments for this student
            assessmentRepo.getAssessmentsForStudent(studentId).firstOrNull()

            _detailState.value = _detailState.value.copy(
                id = studentId,
                studentName = studentName,
                nis = nis,
                classText = classText,
                avatarUrl = avatar,
                timeline = apiTimeline,
                totalAssessments = if (apiTimeline.isNotEmpty()) apiTimeline.size else 0,
                isLoading = false
            )
        }
    }

    fun setFilter(filterName: String) {
        _detailState.value = _detailState.value.copy(selectedFilter = filterName)
    }
}
