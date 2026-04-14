package com.universidad.parchate

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.universidad.parchate.ui.screens.start.StartScreen
import androidx.compose.material3.MaterialTheme // Importación genérica por si falla tu tema
import com.universidad.parchate.Navigation.NavigationPantallas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        setContent {
            // Usamos el tema básico de Material para probar si arranca
            MaterialTheme {

                NavigationPantallas()
            }
        }
    }
}