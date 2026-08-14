package com.itespf.aulamovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.itespf.aulamovil.ui.navigation.AulaMovilNavGraph
import com.itespf.aulamovil.ui.theme.AulaMovilTheme

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(applicationContext)

        setContent {
            var darkModeEnabled by remember { mutableStateOf(false) }

            AulaMovilTheme(darkTheme = darkModeEnabled) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var checkingSession by remember { mutableStateOf(true) }
                    var startLoggedIn by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        startLoggedIn = appContainer.authRepository.isLoggedIn()
                        checkingSession = false
                    }

                    if (!checkingSession) {
                        AulaMovilNavGraph(
                            appContainer = appContainer,
                            startLoggedIn = startLoggedIn,
                            darkModeEnabled = darkModeEnabled,
                            onToggleDarkMode = { darkModeEnabled = !darkModeEnabled }
                        )
                    }
                }
            }
        }
    }
}