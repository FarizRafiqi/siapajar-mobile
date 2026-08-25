package id.siapajar.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.siapajar.app.data.local.SiapAjarDatabase
import id.siapajar.app.data.local.TokenManager
import id.siapajar.app.data.remote.ClassDto
import id.siapajar.app.data.repository.AuthRepository
import id.siapajar.app.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import android.net.Uri
import id.siapajar.app.util.ImageCompressor

data class SettingsUiState(
    val fullName: String = "Siti Rahmawati, S.Pd",
    val email: String = "siti.guru@siapajar.id",
    val schoolName: String = "TK B1 Al-Kautsar • PAUD Terpadu Permata Hati",
    val role: String = "Guru Kelas",
    val educationLevel: String = "PAUD / TK",
    val profilePhotoUri: String? = null,
    val activeClassName: String = "TK B1 (Al-Kautsar)",
    val academicYear: String = "Semester 1 - TA 2025/2026",
    val syncStatusText: String = "Tersinkron (Semua Data Aman)",
    val offlineStorageText: String = "12.4 MB (20 Siswa, 15 Foto)",
    val serverBaseUrl: String = "https://siapajar.farizrafiqi.dev/",
    val photoCompressionQuality: String = "Kompresi Cepat",
    val isAttendanceReminderEnabled: Boolean = true,
    val appVersion: String = "v1.0.0 (Build 2026.08)",
    val availableClasses: List<ClassDto> = emptyList(),
    val isSyncing: Boolean = false,
    val showClassPicker: Boolean = false,
    val showPhotoQualityDialog: Boolean = false,
    val toastMessage: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager.getInstance(application)
    private val db = SiapAjarDatabase.getDatabase(application)
    private val authRepo = AuthRepository(application, tokenManager)
    private val studentRepo = StudentRepository(db.studentDao(), application)

    private val _uiState = MutableStateFlow(SettingsUiState(photoCompressionQuality = tokenManager.getPhotoQuality()))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        val session = tokenManager.sessionState.value
        _uiState.value = _uiState.value.copy(
            fullName = session.fullName ?: "Siti Rahmawati, S.Pd",
            email = session.email ?: "siti.guru@siapajar.id",
            schoolName = session.schoolName ?: "TK B1 Al-Kautsar • PAUD Terpadu Permata Hati",
            role = when (session.role) {
                "admin" -> "Kepala Sekolah / Admin"
                else -> "Guru Kelas"
            },
            educationLevel = session.educationLevel?.uppercase() ?: "PAUD / TK",
            serverBaseUrl = tokenManager.getBaseUrl(),
            photoCompressionQuality = tokenManager.getPhotoQuality()
        )

        viewModelScope.launch {
            try {
                val classes = studentRepo.fetchClasses()
                if (classes.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        availableClasses = classes,
                        activeClassName = classes.first().displayName ?: classes.first().name
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun selectClass(classItem: ClassDto) {
        val displayName = classItem.displayName ?: classItem.name
        _uiState.value = _uiState.value.copy(
            activeClassName = displayName,
            showClassPicker = false
        )
    }

    fun triggerManualSync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            try {
                studentRepo.fetchStudentsFromApi("1")
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncStatusText = "Tersinkronisasi Baru Saja",
                    toastMessage = "Sinkronisasi data berhasil!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    toastMessage = "Gagal terhubung ke server."
                )
            }
        }
    }

    fun toggleAttendanceReminder(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isAttendanceReminderEnabled = enabled)
    }

    fun updateProfilePhoto(uriString: String) {
        viewModelScope.launch {
            try {
                val compressed = ImageCompressor.compressUri(
                    context = getApplication(),
                    sourceUri = Uri.parse(uriString)
                )
                _uiState.value = _uiState.value.copy(
                    profilePhotoUri = compressed.absolutePath,
                    toastMessage = "Foto profil berhasil dioptimasi & diperbarui!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    profilePhotoUri = uriString,
                    toastMessage = "Foto profil diperbarui"
                )
            }
        }
    }

    fun setPhotoQuality(quality: String) {
        tokenManager.savePhotoQuality(quality)
        _uiState.value = _uiState.value.copy(
            photoCompressionQuality = quality,
            showPhotoQualityDialog = false,
            toastMessage = "Kualitas foto diubah ke $quality"
        )
    }

    fun setShowPhotoQualityDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPhotoQualityDialog = show)
    }

    fun setShowClassPicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(showClassPicker = show)
    }

    fun clearToastMessage() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            authRepo.logout()
            onLoggedOut()
        }
    }
}

