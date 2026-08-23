package id.siapajar.app.domain.model

enum class InstrumentType(val displayName: String) {
    CATATAN_ANEKDOT("Catatan Anekdot"),
    HASIL_KARYA("Hasil Karya"),
    FOTO_BERSERI("Foto Berseri"),
    CEKLIS_CAPAIAN("Ceklis Capaian")
}

enum class AttendanceStatus(val code: String, val displayName: String) {
    HADIR("H", "Hadir"),
    IZIN("I", "Izin"),
    SAKIT("S", "Sakit"),
    ALPA("A", "Alpa")
}

enum class SyncStatus {
    PENDING,
    SYNCING,
    SYNCED,
    FAILED
}

data class Student(
    val id: String,
    val name: String,
    val nis: String,
    val photoUrl: String? = null,
    val classId: String,
    val className: String
)

data class Assessment(
    val id: String,
    val studentIds: List<String>,
    val studentNames: List<String>,
    val instrumentType: InstrumentType,
    val photoPath: String?,
    val notes: String,
    val tpCode: String? = null,
    val tpTitle: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

data class Attendance(
    val id: String,
    val studentId: String,
    val studentName: String,
    val date: String,
    val status: AttendanceStatus,
    val notes: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

data class CoreActivity(
    val id: Int = 1,
    val name: String = "",
    val focus: String = "Kreativitas & Kemandirian",
    val materials: String = "",
    val instructions: String = "",
    val benefits: String = "",
    val isPrimary: Boolean = false
)

data class TodayAgenda(
    val weekNumber: Int = 2,
    val semesterNumber: Int = 1,
    val topicTitle: String = "Aku Cinta Indonesia: Negeri Seribu Pulau",
    val subTopic: String = "Tanah Air / Indonesia (Fase Fondasi - TK B 5-6 Tahun)",
    val todayActivity: String = "Membuat Lukisan Burung Garuda dengan Teknik Percikan",
    val targetedTpCode: String = "TP 1.3",
    val targetedTpTitle: String = "Menjaga Kebersihan & Mengenal Simbol Negara",
    val stage: String = "MEMAHAMI (BERKESADARAN, BERMAKNA)",
    val openingActivities: List<String> = emptyList(),
    val openingQuestions: List<String> = emptyList(),
    val coreActivities: List<CoreActivity> = emptyList(),
    val closingActivities: List<String> = emptyList()
)
