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
    val selectedWeek: Int = 0, // 0 = Semua Minggu, 1..18
    val selectedFilter: String = "Semua",
    val timeline: List<StudentTimelineDto> = emptyList(),
    val filteredTimeline: List<StudentTimelineDto> = emptyList(),
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
        // 1. Immediately observe local Room database (Instant 0ms render)
        viewModelScope.launch {
            combine(
                studentRepo.getStudentsByClass(classId),
                assessmentRepo.getAllAssessments()
            ) { studentEntities, allAssessments ->
                if (studentEntities.isNotEmpty() && _listState.value.className == "Kelompok B (TK B1)") {
                    studentEntities.firstOrNull()?.className?.let { name ->
                        _listState.value = _listState.value.copy(className = name)
                    }
                }
                studentEntities.map { student ->
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
            }.collect { items ->
                _listState.value = _listState.value.copy(
                    students = items,
                    totalStudents = items.size,
                    isLoading = false
                )
            }
        }

        // 2. Background sync (Parallel, non-blocking)
        viewModelScope.launch {
            try {
                val classes = studentRepo.fetchClasses()
                val activeClass = classes.find { it.id == classId } ?: classes.firstOrNull()
                if (activeClass != null) {
                    val displayName = activeClass.displayName ?: activeClass.name
                    _listState.value = _listState.value.copy(className = displayName)
                }
            } catch (_: Exception) {}

            try {
                studentRepo.fetchStudentsFromApi(classId)
            } catch (_: Exception) {}
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

            val filtered = applyFilters(apiTimeline, _detailState.value.selectedWeek, _detailState.value.selectedFilter)

            _detailState.value = _detailState.value.copy(
                id = studentId,
                studentName = studentName,
                nis = nis,
                classText = classText,
                avatarUrl = avatar,
                timeline = apiTimeline,
                filteredTimeline = filtered,
                totalAssessments = apiTimeline.size,
                isLoading = false
            )
        }
    }

    fun setWeekFilter(week: Int) {
        val currentTimeline = _detailState.value.timeline
        val currentFilter = _detailState.value.selectedFilter
        val filtered = applyFilters(currentTimeline, week, currentFilter)
        _detailState.value = _detailState.value.copy(
            selectedWeek = week,
            filteredTimeline = filtered
        )
    }

    fun setFilter(filterName: String) {
        val currentTimeline = _detailState.value.timeline
        val currentWeek = _detailState.value.selectedWeek
        val filtered = applyFilters(currentTimeline, currentWeek, filterName)
        _detailState.value = _detailState.value.copy(
            selectedFilter = filterName,
            filteredTimeline = filtered
        )
    }

    private fun applyFilters(
        items: List<StudentTimelineDto>,
        week: Int,
        instrumentFilter: String
    ): List<StudentTimelineDto> {
        return items.filter { item ->
            val matchWeek = if (week == 0) true else item.weekNumber == week
            val matchInstrument = when (instrumentFilter) {
                "Semua" -> true
                "Catatan Anekdot" -> item.instrumentType.contains("anecdot", ignoreCase = true) || item.instrumentTitle.contains("anekdot", ignoreCase = true)
                "Hasil Karya" -> item.instrumentType.contains("work", ignoreCase = true) || item.instrumentTitle.contains("karya", ignoreCase = true)
                "Foto Berseri" -> item.instrumentType.contains("photo", ignoreCase = true) || item.instrumentTitle.contains("foto", ignoreCase = true)
                else -> true
            }
            matchWeek && matchInstrument
        }
    }
}
