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
                if (dto != null) {
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
                    val agenda = if (dto.topicTitle.isNotBlank()) {
                        TodayAgenda(
                            weekNumber = dto.weekNumber,
                            semesterNumber = dto.semesterNumber,
                            topicTitle = dto.topicTitle,
                            subTopic = dto.subTopic,
                            todayActivity = dto.todayActivity,
                            targetedTpCode = dto.targetedTpCode,
                            targetedTpTitle = dto.targetedTpTitle,
                            stage = dto.stage,
                            openingActivities = dto.openingActivities.ifEmpty { getDefaultOpeningActivities() },
                            openingQuestions = dto.openingQuestions.ifEmpty { getDefaultOpeningQuestions() },
                            coreActivities = coreList.ifEmpty { getDefaultCoreActivities() },
                            closingActivities = dto.closingActivities.ifEmpty { getDefaultClosingActivities() }
                        )
                    } else {
                        getDefaultSampleAgenda()
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        agenda = agenda
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        agenda = getDefaultSampleAgenda()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    agenda = getDefaultSampleAgenda()
                )
            }
        }
    }

    private fun getDefaultSampleAgenda(): TodayAgenda {
        return TodayAgenda(
            weekNumber = 2,
            semesterNumber = 1,
            topicTitle = "Aku Cinta Indonesia: Negeri Seribu Pulau",
            subTopic = "Tanah Air / Indonesia (Fase Fondasi - TK B 5-6 Tahun)",
            todayActivity = "Membuat Lukisan Burung Garuda dengan Teknik Percikan",
            targetedTpCode = "TP 1.3",
            targetedTpTitle = "Menjaga Kebersihan & Mengenal Simbol Negara",
            stage = "MEMAHAMI (BERKESADARAN, BERMAKNA)",
            openingActivities = getDefaultOpeningActivities(),
            openingQuestions = getDefaultOpeningQuestions(),
            coreActivities = getDefaultCoreActivities(),
            closingActivities = getDefaultClosingActivities()
        )
    }

    private fun getDefaultOpeningActivities() = listOf(
        "Salam & Doa Pembuka",
        "Lagu \"1234 Pergi Sekolah\"",
        "Pertanyaan Pemantik & Presensi Pagi"
    )

    private fun getDefaultOpeningQuestions() = listOf(
        "Siapa yang tahu apa lambang negara kita Indonesia?",
        "Warna apa saja yang ada pada bendera Indonesia?"
    )

    private fun getDefaultCoreActivities() = listOf(
        CoreActivity(
            id = 1,
            name = "Membuat Lukisan Burung Garuda dengan Teknik Percikan",
            focus = "Pilihan Utama: Motorik Halus & Kreativitas Seni",
            materials = "Mangkuk, Pewarna makanan, Sikat gigi, Sisir, Air, Kertas HVS, Gunting, Printable garuda",
            instructions = "1. Siapkan pola burung Garuda di atas kertas HVS.\n2. Campurkan pewarna makanan dengan sedikit air di mangkuk.\n3. Celupkan sikat gigi ke dalam pewarna.\n4. Gesekkan sikat gigi pada sisir di atas kertas untuk membuat efek percikan.\n5. Biarkan mengering dan angkat pola Garuda.",
            benefits = "Melatih kekuatan jari, koordinasi bilateral tangan, serta eksplorasi tekstur warna.",
            isPrimary = true
        ),
        CoreActivity(
            id = 2,
            name = "Peta Indonesia dari Biji-bijian",
            focus = "Kolase & Kognitif Tekstur",
            materials = "Biji kacang hijau, Lem, Pola peta Indonesia, Kertas tebal",
            instructions = "1. Oleskan lem pada permukaan peta pulau.\n2. Tempelkan biji kacang hijau secara rapi mengikuti garis pulau.\n3. Tekan perlahan agar menempel kuat.",
            benefits = "Melatih kesabaran, presisi motorik, dan pengenalan geografi dasar.",
            isPrimary = false
        ),
        CoreActivity(
            id = 3,
            name = "Garuda Pancasila dari Bahan Alam",
            focus = "Loose Parts & Rekayasa Kreatif",
            materials = "Daun kering, Ranting, Kardus bekas, Lem kayu",
            instructions = "1. Kumpulkan daun kering aneka bentuk.\n2. Susun daun menjadi bentuk sayap dan ekor burung garuda.\n3. Tempelkan ranting sebagai cengkeraman pita Bhinneka Tunggal Ika.",
            benefits = "Mendorong cinta lingkungan dan pemanfaatan bahan alam.",
            isPrimary = false
        )
    )

    private fun getDefaultClosingActivities() = listOf(
        "Parade mini karya & refleksi: Anak-anak memamerkan hasil karya lukisan percikan/kolase dan menceritakan perasaan mereka selama kegiatan hari ini.",
        "Merapikan alat main bersama & Doa penutup"
    )

    fun selectActivity(index: Int) {
        _uiState.value = _uiState.value.copy(selectedActivityIndex = index)
    }
}
