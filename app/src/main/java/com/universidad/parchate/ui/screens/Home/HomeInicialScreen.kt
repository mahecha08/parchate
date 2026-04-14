package com.universidad.parchate.ui.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.universidad.parchate.R
import com.universidad.parchate.ui.theme.BackgroundPrincipal


@Composable
fun HomeScreen(){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column() {
            Text(stringResource(R.string.home_saludo))
            TextAlign.Center


        }
    }
}
