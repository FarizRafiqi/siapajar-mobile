package id.siapajar.app.data.remote

import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(val token: String, val teacherName: String, val email: String)

@Serializable
data class StudentDto(val id: String, val name: String, val nis: String, val photoUrl: String?, val classId: String, val className: String)

@Serializable
data class TodayAgendaDto(
    val weekNumber: Int,
    val semesterNumber: Int,
    val topicTitle: String,
    val todayActivity: String,
    val targetedTpCode: String,
    val targetedTpTitle: String
)

@Serializable
data class AttendanceSubmitRequest(
    val date: String,
    val classId: String,
    val items: List<AttendanceItemDto>
)

@Serializable
data class AttendanceItemDto(val studentId: String, val status: String, val notes: String?)

interface SiapAjarApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @GET("api/v1/classes/{id}/students")
    suspend fun getStudents(@Path("id") classId: String): Response<List<StudentDto>>

    @GET("api/v1/classes/{id}/today-agenda")
    suspend fun getTodayAgenda(@Path("id") classId: String): Response<TodayAgendaDto>

    @Multipart
    @POST("api/v1/assessments/quick-capture")
    suspend fun uploadAssessment(
        @Part photo: MultipartBody.Part?,
        @Part("studentIds") studentIds: RequestBody,
        @Part("instrumentType") instrumentType: RequestBody,
        @Part("notes") notes: RequestBody
    ): Response<Unit>

    @POST("api/v1/attendances/quick-submit")
    suspend fun submitAttendance(@Body req: AttendanceSubmitRequest): Response<Unit>
}
