package com.itespf.aulamovil

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.itespf.aulamovil.data.local.TokenManager
import com.itespf.aulamovil.data.network.RetrofitClient
import com.itespf.aulamovil.data.repository.AuthRepository
import com.itespf.aulamovil.data.repository.GradesRepository
import com.itespf.aulamovil.ui.viewmodel.GradesViewModel
import com.itespf.aulamovil.ui.viewmodel.LoginViewModel

class AppContainer(context: Context) {
    private val tokenManager = TokenManager(context.applicationContext)
    private val apiService = RetrofitClient.create(tokenManager)

    val authRepository = AuthRepository(apiService, tokenManager)
    val gradesRepository = GradesRepository(apiService)
}

class ViewModelFactory(private val appContainer: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            LoginViewModel::class.java ->
                LoginViewModel(appContainer.authRepository) as T
            GradesViewModel::class.java ->
                GradesViewModel(appContainer.gradesRepository, appContainer.authRepository) as T
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
