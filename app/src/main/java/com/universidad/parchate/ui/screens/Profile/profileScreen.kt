package com.universidad.parchate.ui.screens.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.universidad.parchate.R
import com.universidad.parchate.ui.components.CajaBoton
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario

@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit,
    onNavitageToEdit: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToStart: () -> Unit
){
    Scaffold(
        containerColor = BackgroundPrincipal
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundPrincipal)
                .padding(horizontal = 16.dp)
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                IconButton(onClick = onNavigateToHome) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.atras),
                        tint = RosadoNeon
                    )
                }
                Text(
                    text = stringResource(R.string.profile_titulo),
                    color = RosadoNeon,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = Modifier.fillMaxWidth().padding( vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(170.dp)
                        .clip(CircleShape)
                        .background(RosadoNeon),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BackgroundPrincipal,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Text(text = stringResource(R.string.profile_nombre), color = TextoSecundario, fontSize = 25.sp)
            }


            CajaBoton(text = stringResource(R.string.profile_datos_personales),onClick = onNavitageToEdit, leadingIcon = Icons.Default.AccountCircle, modifier = Modifier.padding(vertical = 12.dp))
            CajaBoton(text = stringResource(R.string.profile_tus_eventos),onClick = {}, leadingIcon = Icons.Default.CalendarMonth, modifier = Modifier.padding(vertical = 12.dp))
            CajaBoton(text = stringResource(R.string.profile_cambiar_contrasena),onClick = {}, leadingIcon = Icons.Default.Lock, modifier = Modifier.padding(vertical = 12.dp))
            CajaBoton(text = stringResource(R.string.profile_cerrar_sesion),onClick = {
                Firebase.auth.signOut()
                onNavigateToStart()
            }, leadingIcon = Icons.Default.Undo, modifier = Modifier.padding(vertical = 12.dp))

        }
    }
}