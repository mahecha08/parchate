package com.universidad.parchate.ui.screens.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universidad.parchate.R
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.viewmodel.StartViewModel
import com.universidad.parchate.ui.theme.RosadoNeon
import kotlinx.coroutines.delay

@Composable
fun StartScreen(navigationToLogin:() -> Unit, viewModel: StartViewModel = viewModel()) {
    val fondoPrincipal = Color(0xFF1A1A2E)
    val rosadoBotones = Color(0xFFE94560)

    LaunchedEffect(Unit) {
        delay(2000)
        navigationToLogin()
    }


    Column(modifier = Modifier.fillMaxSize()
        .background(BackgroundPrincipal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painterResource(R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier.size(700.dp))

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = stringResource(R.string.start_title), color = RosadoNeon, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)

        Spacer(modifier = Modifier.height(100.dp))
    }
}