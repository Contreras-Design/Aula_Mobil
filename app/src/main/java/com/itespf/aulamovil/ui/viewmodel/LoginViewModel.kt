package com.itespf.aulamovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itespf.aulamovil.data.model.User
import com.itespf.aulamovil.data.repository.AuthRepository
import com.itespf.aulamovil.data.repository.AuthResult
import com.itespf.aulamovil.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState<User>>(UiState.Idle)
    val state: StateFlow<UiState<User>> = _state.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = UiState.Error("Ingresa tu matrícula y contraseña.")
            return
        }
        _state.value = UiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.login(username.trim(), password)) {
                is AuthResult.Success -> _state.value = UiState.Success(result.user)
                is AuthResult.Error -> _state.value = UiState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _state.value = UiState.Idle
    }
}
