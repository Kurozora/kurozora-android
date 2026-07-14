package app.kurozora.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kurozorakit.core.KurozoraKit
import kurozorakit.shared.logging.KurozoraLogger

class TwoFactorViewModel(
    private val kurozoraKit: KurozoraKit,
) : ViewModel() {
    private val _state = MutableStateFlow(TwoFactorState())
    val state: StateFlow<TwoFactorState> = _state

    fun onCodeChanged(code: String) {
        _state.value = _state.value.copy(code = code, useRecoveryCode = false)
    }

    fun onRecoveryCodeChanged(code: String) {
        _state.value = _state.value.copy(code = code, useRecoveryCode = true)
    }

    fun submitChallenge(
        challengeToken: String,
        onSuccess: () -> Unit,
    ) {
        val s = _state.value
        if (s.code.isBlank()) {
            _state.value = s.copy(errorMessage = "Please enter the verification code")
            return
        }
        _state.value = s.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val result = if (s.useRecoveryCode) {
                    kurozoraKit.auth().submitTwoFactorChallenge(
                        challengeToken = challengeToken,
                        recoveryCode = s.code,
                    )
                } else {
                    kurozoraKit.auth().submitTwoFactorChallenge(
                        challengeToken = challengeToken,
                        otp = s.code,
                    )
                }
                result
                    .onSuccess { onSuccess() }
                    .onError { e ->
                        _state.value = _state.value.copy(
                            errorMessage = e.message, isLoading = false
                        )
                    }
            } catch (e: Exception) {
                KurozoraLogger.error("[TwoFactorViewModel]", "2FA challenge failed", e)
                _state.value = _state.value.copy(
                    errorMessage = e.message ?: "Verification failed", isLoading = false
                )
            }
        }
    }
}

data class TwoFactorState(
    val code: String = "",
    val useRecoveryCode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
