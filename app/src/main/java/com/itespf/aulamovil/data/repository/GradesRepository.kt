package com.itespf.aulamovil.data.repository

import com.itespf.aulamovil.data.model.GradesResponse
import com.itespf.aulamovil.data.network.ApiService
import com.itespf.aulamovil.ui.state.UiState
import java.io.IOException

class GradesRepository(private val api: ApiService) {

    suspend fun fetchGrades(): UiState<GradesResponse> {
        return try {
            val response = api.getGrades()
            when {
                response.isSuccessful -> UiState.Success(response.body() ?: GradesResponse())
                response.code() == 401 -> UiState.Error(
                    "Tu sesión expiró. Inicia sesión de nuevo.",
                    expiredSession = true
                )
                response.code() == 403 -> UiState.Error("Esta cuenta no es de estudiante.")
                else -> UiState.Error("No se pudieron cargar las calificaciones (código ${response.code()}).")
            }
        } catch (e: IOException) {
            UiState.Error("Sin conexión a internet. Verifica tu red e inténtalo de nuevo.")
        } catch (e: Exception) {
            UiState.Error("Ocurrió un error inesperado: ${e.message}")
        }
    }
}
