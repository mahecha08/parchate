package com.universidad.parchate

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.universidad.parchate.Navigation.NavigationPantallas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Temporal: limpiar sesión para evitar arrastrar auth vieja
        FirebaseAuth.getInstance().signOut()

        // Verificación de Firebase activo
        val options = FirebaseApp.getInstance().options
        Log.d("FIREBASE_CHECK", "projectId=${options.projectId}")
        Log.d("FIREBASE_CHECK", "storageBucket=${options.storageBucket}")
        Log.d("FIREBASE_CHECK", "applicationId=${options.applicationId}")

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        setContent {
            MaterialTheme {
                NavigationPantallas()
            }
        }
    }
}