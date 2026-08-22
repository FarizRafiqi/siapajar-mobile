package id.siapajar.app.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSession(
    val token: String?,
    val fullName: String?,
    val email: String?,
    val schoolName: String?,
    val educationLevel: String?,
    val role: String?,
    val baseUrl: String
)

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("siapajar_auth_prefs", Context.MODE_PRIVATE)

    private val _sessionState = MutableStateFlow(loadSession())
    val sessionState: StateFlow<UserSession> = _sessionState.asStateFlow()

    fun saveSession(
        token: String,
        fullName: String?,
        email: String,
        schoolName: String?,
        educationLevel: String?,
        role: String
    ) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_EMAIL, email)
            .putString(KEY_SCHOOL_NAME, schoolName)
            .putString(KEY_EDUCATION_LEVEL, educationLevel)
            .putString(KEY_ROLE, role)
            .apply()
        _sessionState.value = loadSession()
    }

    fun saveBaseUrl(url: String) {
        var formatted = url.trim()
        if (!formatted.endsWith("/")) formatted += "/"
        prefs.edit().putString(KEY_BASE_URL, formatted).apply()
        _sessionState.value = loadSession()
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_FULL_NAME)
            .remove(KEY_EMAIL)
            .remove(KEY_SCHOOL_NAME)
            .remove(KEY_EDUCATION_LEVEL)
            .remove(KEY_ROLE)
            .apply()
        _sessionState.value = loadSession()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getBaseUrl(): String = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    private fun loadSession(): UserSession {
        return UserSession(
            token = prefs.getString(KEY_TOKEN, null),
            fullName = prefs.getString(KEY_FULL_NAME, "Guru SiapAjar"),
            email = prefs.getString(KEY_EMAIL, null),
            schoolName = prefs.getString(KEY_SCHOOL_NAME, "TK / RA SiapAjar"),
            educationLevel = prefs.getString(KEY_EDUCATION_LEVEL, "paud"),
            role = prefs.getString(KEY_ROLE, "teacher"),
            baseUrl = getBaseUrl()
        )
    }


    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_FULL_NAME = "user_full_name"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_SCHOOL_NAME = "user_school_name"
        private const val KEY_EDUCATION_LEVEL = "user_education_level"
        private const val KEY_ROLE = "user_role"
        private const val KEY_BASE_URL = "server_base_url"

        // Default to Android Emulator loopback mapping (10.0.2.2 points to host machine port 3333)
        const val DEFAULT_BASE_URL = "http://10.0.2.2:3333/"

        @Volatile
        private var INSTANCE: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return INSTANCE ?: synchronized(this) {
                val instance = TokenManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
