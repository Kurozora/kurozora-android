package app.kurozora.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kurozora.core.settings.AccountManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit

class AuthViewModel(
    private val kurozoraKit: KurozoraKit,
    private val accountManager: AccountManager,
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state
    fun onUsernameChanged(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    fun onEmailChanged(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun onPasswordChanged(password: String) {
        _state.value = _state.value.copy(password = password)
    }

    fun onConfirmPasswordChanged(password: String) {
        _state.value = _state.value.copy(confirmPassword = password)
    }

    fun login(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(errorMessage = "Email and password cannot be empty")
            return
        }
        _state.value = s.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                kurozoraKit.auth().signIn(email = s.email, password = s.password)
                    .onSuccess { onSuccess() }
                    .onError { e -> _state.value = _state.value.copy(errorMessage = e.message, isLoading = false) }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = e.message ?: "Login failed", isLoading = false
                )
            }
        }
    }

    fun signup(onSuccess: () -> Unit) {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank() || s.confirmPassword.isBlank()) {
            _state.value = s.copy(errorMessage = "All fields are required")
            return
        }
        if (s.password != s.confirmPassword) {
            _state.value = s.copy(errorMessage = "Passwords do not match")
            return
        }
        _state.value = s.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                kurozoraKit.auth()
                    .signUp(username = s.username, email = s.email, password = s.password)
                    .onSuccess { onSuccess() }
                    .onError { e -> _state.value = _state.value.copy(errorMessage = e.message, isLoading = false) }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = e.message ?: "Signup failed", isLoading = false
                )
            }
        }
    }
}

