package com.itespf.aulamovil.data.repository

import com.itespf.aulamovil.data.local.TokenManager
import com.itespf.aulamovil.data.model.LoginRequest
import com.itespf.aulamovil.data.model.User
import com.itespf.aulamovil.data.network.ApiService
import com.google.gson.Gson
import com.itespf.aulamovil.data.model.ApiError
import java.io.IOException

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(username: String, password: String): AuthResult {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                tokenManager.saveSession(body.token, body.user.name, body.user.username)
                AuthResult.Success(body.user)
            } else if (response.code() == 401) {
                AuthResult.Error("Credenciales inválidas.")
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: IOException) {
            AuthResult.Error("Sin conexión a internet. Verifica tu red e inténtalo de nuevo.")
        } catch (e: Exception) {
            AuthResult.Error("Ocurrió un error inesperado: ${e.message}")
        }
    }


    suspend fun logout() {
        try {
            api.logout()
        } catch (_: Exception) {

        } finally {
            tokenManager.clearSession()
        }
    }

    suspend fun isLoggedIn(): Boolean = tokenManager.getTokenOnce() != null

    suspend fun cachedUserName(): String? = tokenManager.getUserName()
    suspend fun cachedUsername(): String? = tokenManager.getUserUsername()


    suspend fun forceLogoutLocal() = tokenManager.clearSession()

    private fun parseError(raw: String?): String {
        return try {
            if (raw.isNullOrBlank()) "Error del servidor." else Gson().fromJson(raw, ApiError::class.java).error
        } catch (_: Exception) {
            "Error del servidor. Inténtalo más tarde."
        }
    }
}
