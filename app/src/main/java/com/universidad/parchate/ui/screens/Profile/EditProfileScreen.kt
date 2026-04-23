package com.universidad.parchate.ui.screens.Profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universidad.parchate.R
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.components.glowButton
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario
import com.universidad.parchate.ui.viewmodel.ProfileViewModel

@Composable
fun EditProfileScreen(
    OnNavigateToProfile: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            OnNavigateToProfile()
            viewModel.clearMessages()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal)
            .verticalScroll(scrollState)
            .padding(bottom = 30.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = OnNavigateToProfile) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = RosadoNeon)
            }
            Text(
                text = stringResource(R.string.profile_editar_titulo),
                color = RosadoNeon,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(150.dp).clip(CircleShape).background(RosadoNeon),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = BackgroundPrincipal, modifier = Modifier.size(70.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = uiState.nombres.ifBlank { "Usuario" },
                color = TextoSecundario,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            cajasTexto(
                value = uiState.nombres,
                onValueChange = { newValue ->
                    viewModel.onFieldChange { it.copy(nombres = newValue) }
                    viewModel.clearMessages()
                },
                label = stringResource(R.string.profile_nombre),
                leadingIcon = Icons.Default.Person
            )


            Box(modifier = Modifier.fillMaxWidth()) {
                cajasTexto(
                    value = uiState.correo,
                    onValueChange = { },
                    label = stringResource(R.string.profile_correo),
                    leadingIcon = Icons.Default.Mail,
                    modifier = Modifier.alpha(0.6f)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(enabled = false) { }
                )
            }


            cajasTexto(
                value = uiState.cedula,
                onValueChange = { newValue ->
                    viewModel.onFieldChange { it.copy(cedula = newValue) }
                    viewModel.clearMessages()
                },
                label = stringResource(R.string.profile_cedula),
                leadingIcon = Icons.Default.Phone
            )


            cajasTexto(
                value = uiState.bio,
                onValueChange = { newValue ->
                    viewModel.onFieldChange { it.copy(bio = newValue) }
                },
                label = stringResource(R.string.profile_bio),
                modifier = Modifier.height(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!uiState.errorMessage.isNullOrEmpty()) {
                Text(
                    text = uiState.errorMessage!!,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFDFCB7A),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }


            if (uiState.isLoading) {
                CircularProgressIndicator(color = RosadoNeon)
            } else {
                glowButton(
                    text = stringResource(R.string.profile_guardar),
                    onClick = {
                        viewModel.updateProfile(onSuccess = { })
                    }
                )
            }
        }
    }
}