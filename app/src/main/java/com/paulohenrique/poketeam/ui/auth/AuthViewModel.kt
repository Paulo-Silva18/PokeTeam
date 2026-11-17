package com.paulohenrique.poketeam.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulohenrique.poketeam.data.repository.AuthRepository
import com.paulohenrique.poketeam.data.repository.friendlyAuthError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val isLoggedIn: Boolean = false
)

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState(isLoggedIn = repo.isLoggedIn()))
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        val e = email.trim()
        if (!isValidEmail(e)) return fail("Informe um e-mail válido.")
        if (password.isEmpty()) return fail("Informe a senha.")
        execute { repo.login(e, password) }
    }

    fun register(email: String, password: String, confirm: String) {
        val e = email.trim()
        if (!isValidEmail(e)) return fail("Informe um e-mail válido.")
        if (password.length < 6) return fail("A senha precisa ter pelo menos 6 caracteres.")
        if (password != confirm) return fail("As senhas não conferem.")
        execute { repo.register(e, password) }
    }

    fun resetPassword(email: String) {
        val e = email.trim()
        if (!isValidEmail(e)) return fail("Informe um e-mail válido.")
        execute(successMessage = "Enviamos um link de recuperação para $e.") {
            repo.sendPasswordReset(e)
        }
    }

    fun logout() {
        repo.logout()
        _state.value = AuthUiState(isLoggedIn = false)
    }

    fun clearFeedback() = _state.update { it.copy(error = null, message = null) }

    private fun fail(message: String) = _state.update { it.copy(error = message, message = null) }

    private fun isValidEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun execute(successMessage: String? = null, block: suspend () -> Result<Unit>) {
        _state.update { it.copy(isLoading = true, error = null, message = null) }
        viewModelScope.launch {
            block()
                .onSuccess {
                    _state.update {
                        it.copy(isLoading = false, isLoggedIn = repo.isLoggedIn(), message = successMessage)
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = friendlyAuthError(e)) }
                }
        }
    }
}
