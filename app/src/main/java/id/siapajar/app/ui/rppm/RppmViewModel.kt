package id.siapajar.app.ui.rppm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.siapajar.app.data.local.SiapAjarDatabase
import id.siapajar.app.data.repository.StudentRepository
import id.siapajar.app.domain.model.CoreActivity
import id.siapajar.app.domain.model.TodayAgenda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RppmUiState(
    val isLoading: Boolean = false,
    val agenda: TodayAgenda? = null,
    val selectedActivityIndex: Int = 0,
    val errorMessage: String? = null
)

class RppmViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SiapAjarDatabase.getDatabase(application)
    private val studentRepo = StudentRepository(db.studentDao(), application)

    private val _uiState = MutableStateFlow(RppmUiState(isLoading = true))
    val uiState: StateFlow<RppmUiState> = _uiState.asStateFlow()

    init {
        loadAgenda()
    }

    fun loadAgenda(classId: String = "1") {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val dto = studentRepo.fetchTodayAgenda(classId)
                if (dto != null && dto.topicTitle.isNotBlank()) {
                    val coreList = dto.coreActivities.map {
                        CoreActivity(
                            id = it.id,
                            name = it.name,
                            focus = it.focus,
                            materials = it.materials,
                            instructions = it.instructions,
                            benefits = it.benefits,
                            isPrimary = it.isPrimary
                        )
                    }
                    val agenda = TodayAgenda(
                        weekNumber = dto.weekNumber,
                        semesterNumber = dto.semesterNumber,
                        topicTitle = dto.topicTitle,
                        subTopic = dto.subTopic,
                        todayActivity = dto.todayActivity,
                        targetedTpCode = dto.targetedTpCode,
                        targetedTpTitle = dto.targetedTpTitle,
                        stage = dto.stage,
                        openingActivities = dto.openingActivities,
                        openingQuestions = dto.openingQuestions,
                        coreActivities = coreList,
                        closingActivities = dto.closingActivities
                    )
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        agenda = agenda
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        agenda = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    agenda = null,
                    errorMessage = e.localizedMessage
                )
            }
        }
    }

    fun selectActivity(index: Int) {
        _uiState.value = _uiState.value.copy(selectedActivityIndex = index)
    }
}

