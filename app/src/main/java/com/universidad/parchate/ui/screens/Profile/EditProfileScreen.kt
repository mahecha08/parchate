package com.universidad.parchate.ui.screens.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universidad.parchate.R
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.components.glowButton
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario

@Composable
fun EditProfileScreen(
    OnNavigateToProfile: () -> Unit
){
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    Column(modifier = Modifier.background(BackgroundPrincipal)
        .fillMaxSize()
        .padding(vertical = 20.dp)) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            IconButton(onClick = OnNavigateToProfile) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.atras),
                    tint = RosadoNeon
                )
            }
            Text(
                text = stringResource(R.string.profile_editar_titulo),
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
        Column (modifier = Modifier.padding(horizontal = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
            cajasTexto(
                value = nombre,
                onValueChange = {nombre = it},
                label = stringResource(R.string.profile_nombre),
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.weight(1f)

            )
            cajasTexto(
                value = apellido,
                onValueChange = {apellido = it},
                label = stringResource(R.string.profile_apellido),
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.weight(1f)

            )
        }
            cajasTexto(
                value = correo,
                onValueChange = {correo = it},
                label = stringResource(R.string.profile_correo),
                leadingIcon = Icons.Default.Mail

            )
            cajasTexto(
                value = telefono,
                onValueChange = {telefono = it},
                label = stringResource(R.string.profile_telefono),
                leadingIcon = Icons.Default.Phone

            )
            cajasTexto(
                value = bio,
                onValueChange = {bio = it},
                label = stringResource(R.string.profile_bio),
                modifier = Modifier.height(150.dp)

            )
            glowButton(text = stringResource(R.string.profile_guardar), onClick = {}, modifier = Modifier.padding(vertical = 25.dp))
        }
    }
}