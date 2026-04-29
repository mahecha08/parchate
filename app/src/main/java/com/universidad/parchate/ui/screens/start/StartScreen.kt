package com.universidad.parchate.ui.screens.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universidad.parchate.R
import com.universidad.parchate.ui.theme.BackgroundPrincipal
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.viewmodel.StartViewModel
import kotlinx.coroutines.delay

@Composable
fun StartScreen(
    navigationToLogin: () -> Unit,
    viewModel: StartViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        delay(2000)
        navigationToLogin()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.logo_descripcion),
            modifier = Modifier.size(220.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.start_title),
            color = RosadoNeon,
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}
