package id.siapajar.app.data.remote

import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

@Serializable
data class ApiResponse<T>(
    val status: String,
    val message: String? = null,
    val data: T? = null
)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginData(
    val token: String,
    val user: UserProfileDto
)

@Serializable
data class UserProfileDto(
    val id: Int,
    val fullName: String?,
    val email: String,
    val schoolName: String?,
    val educationLevel: String?,
    val role: String
)

@Serializable
data class ClassDto(
    val id: String,
    val name: String,
    val displayName: String? = null,
    val gradeLevel: Int? = null,
    val groupContext: String? = null,
    val rombelNumber: String? = null,
    val studentCount: Int = 0
)

@Serializable
data class StudentDto(
    val id: String,
    val name: String,
    val nis: String,
    val nisn: String? = null,
    val classId: String,
    val className: String? = null,
    val assessmentCount: Int = 0,
    val avatarUrl: String? = null
)

@Serializable
data class CoreActivityDto(
    val id: Int = 1,
    val name: String = "",
    val focus: String = "Kreativitas & Kemandirian",
    val materials: String = "",
    val instructions: String = "",
    val benefits: String = "",
    val isPrimary: Boolean = false
)

@Serializable
data class TodayAgendaDto(
    val weekNumber: Int = 2,
    val semesterNumber: Int = 1,
    val topicTitle: String = "",
    val subTopic: String = "",
    val todayActivity: String = "",
    val targetedTpCode: String = "",
    val targetedTpTitle: String = "",
    val stage: String = "MEMAHAMI (BERKESADARAN, BERMAKNA)",
    val openingActivities: List<String> = emptyList(),
    val openingQuestions: List<String> = emptyList(),
    val coreActivities: List<CoreActivityDto> = emptyList(),
    val closingActivities: List<String> = emptyList()
)

@Serializable
data class StudentTimelineDto(
    val id: String,
    val instrumentType: String,
    val instrumentTitle: String,
    val date: String,
    val dateText: String,
    val activity: String? = null,
    val notes: String? = null,
    val achievementStatus: String? = null,
    val tpCode: String? = null,
    val weekNumber: Int = 1,
    val semesterNumber: Int = 1,
    val attachments: List<AttachmentDto> = emptyList()
)

@Serializable
data class AttachmentDto(
    val id: String,
    val fileName: String,
    val url: String
)

@Serializable
data class QuickCaptureResponse(
    val assessmentIds: List<String>
)

@Serializable
data class AttendanceSubmitRequest(
    val date: String,
    val classId: String,
    val items: List<AttendanceItemDto>
)

@Serializable
data class AttendanceItemDto(
    val studentId: String,
    val status: String,
    val notes: String? = null
)

interface SiapAjarApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<ApiResponse<LoginData>>

    @GET("api/v1/classes")
    suspend fun getClasses(): Response<ApiResponse<List<ClassDto>>>

    @GET("api/v1/classes/{id}/students")
    suspend fun getStudents(@Path("id") classId: String): Response<ApiResponse<List<StudentDto>>>

    @GET("api/v1/classes/{id}/today-agenda")
    suspend fun getTodayAgenda(@Path("id") classId: String): Response<ApiResponse<TodayAgendaDto>>

    @GET("api/v1/students/{id}/timeline")
    suspend fun getStudentTimeline(@Path("id") studentId: String): Response<ApiResponse<List<StudentTimelineDto>>>

    @Multipart
    @POST("api/v1/assessments/quick-capture")
    suspend fun uploadAssessment(
        @Part photo: MultipartBody.Part?,
        @Part("classId") classId: RequestBody,
        @Part("studentIds") studentIds: RequestBody,
        @Part("instrumentType") instrumentType: RequestBody,
        @Part("notes") notes: RequestBody
    ): Response<ApiResponse<QuickCaptureResponse>>

    @POST("api/v1/attendances/quick-submit")
    suspend fun submitAttendance(@Body req: AttendanceSubmitRequest): Response<ApiResponse<Unit>>
}
