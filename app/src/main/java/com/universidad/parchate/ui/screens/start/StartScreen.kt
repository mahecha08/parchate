package com.universidad.parchate.ui.screens.start

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.universidad.parchate.viewmodel.StartViewModel

@Composable
fun StartScreen(navigationToLogin:() -> Unit, viewModel: StartViewModel = viewModel()) {
    val fondoPrincipal = Color(0xFF1A1A2E)
    val rosadoBotones = Color(0xFFE94560)

    Surface(modifier = Modifier.fillMaxSize(), color = fondoPrincipal) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Espacio para el Logo
            Box(modifier = Modifier.size(160.dp).background(Color.Gray.copy(0.3f), CircleShape))

            Spacer(modifier = Modifier.height(20.dp))

            Text("PARCHATE", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = { navigationToLogin() },
                colors = ButtonDefaults.buttonColors(containerColor = rosadoBotones),
                modifier = Modifier.fillMaxWidth(0.8f).height(55.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("PARCHATE", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun StartScreenPreview(){
    StartScreen( navigationToLogin = {}, viewModel= viewModel() )
}