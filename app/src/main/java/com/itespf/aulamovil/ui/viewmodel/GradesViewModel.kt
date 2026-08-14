package com.itespf.aulamovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itespf.aulamovil.data.model.GradesResponse
import com.itespf.aulamovil.data.repository.AuthRepository
import com.itespf.aulamovil.data.repository.GradesRepository
import com.itespf.aulamovil.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GradesViewModel(
    private val gradesRepository: GradesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<GradesResponse>>(UiState.Idle)
    val state: StateFlow<UiState<GradesResponse>> = _state.asStateFlow()

    val userName = MutableStateFlow<String?>(null)
    val userUsername = MutableStateFlow<String?>(null)

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            userName.value = authRepository.cachedUserName()
            userUsername.value = authRepository.cachedUsername()

            val result = gradesRepository.fetchGrades()
            if (result is UiState.Error && result.expiredSession) {
                authRepository.forceLogoutLocal()
            }
            _state.value = result
        }
    }

    fun calcularPromedio(data: GradesResponse): Double? {
        val notas = mutableListOf<Double>()
        data.submissions.forEach { it.grade?.let { g -> notas.add(g) } }
        data.examResults.forEach { it.score?.let { s -> notas.add(s) } }
        data.customGrades.forEach { notas.add(it.score) }
        if (notas.isEmpty()) return null
        return notas.sum() / notas.size
    }
}
