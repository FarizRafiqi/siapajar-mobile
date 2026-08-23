package id.siapajar.app.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.siapajar.app.data.local.TokenManager
import id.siapajar.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val pass: String = "",
    val baseUrl: String = TokenManager.DEFAULT_BASE_URL,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager.getInstance(application)
    private val authRepository = AuthRepository(application, tokenManager)

    private val _uiState = MutableStateFlow(
        AuthUiState(
            baseUrl = tokenManager.getBaseUrl(),
            isSuccess = tokenManager.isLoggedIn()
        )
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail, errorMessage = null)
    }

    fun onPasswordChanged(newPass: String) {
        _uiState.value = _uiState.value.copy(pass = newPass, errorMessage = null)
    }

    fun onBaseUrlChanged(newUrl: String) {
        _uiState.value = _uiState.value.copy(baseUrl = newUrl)
        authRepository.updateBaseUrl(newUrl)
    }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank() || state.pass.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Email dan kata sandi wajib diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.login(state.email, state.pass)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Terjadi kesalahan saat login"
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = _uiState.value.copy(isSuccess = false)
        }
    }
}
