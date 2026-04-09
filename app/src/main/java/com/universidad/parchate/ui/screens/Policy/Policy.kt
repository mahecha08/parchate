package com.universidad.parchate.ui.screens.Policy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universidad.parchate.ui.components.cajasTexto
import com.universidad.parchate.ui.theme.*
import com.universidad.parchate.ui.components.ReusableButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

@Composable
fun PolicyScreen(
    NavigationToLogin: ()-> Unit,
    NavigationToVerification: () -> Unit
    ){
    var scrollState = rememberScrollState()
    var termsScrollState = rememberScrollState()
    var termsAccepted by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize()
            .background(BackgroundPrincipal),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 25.dp)
                .height(80.dp)
                .background(Encabezado),
            contentAlignment = Alignment.CenterStart
        ){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = RosadoNeon,

                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(28.dp)
                    .clickable { NavigationToLogin() }
            )
        };

        Box(
            modifier = Modifier
                .height(600.dp)
                .fillMaxWidth()
                .verticalScroll(termsScrollState)
                .padding(horizontal = 16.dp, vertical = 25.dp)
                .background(Color.Gray, RoundedCornerShape(10.dp)),
        ) {
            Column(){
                Text(
                    text = "Aqui van los terminos y condiciones",
                    fontSize = 30.sp
                )
                Text(
                    text = "blablablablabla",
                    fontSize = 20.sp
                )
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(2.dp, RosadoNeon, RoundedCornerShape(4.dp))
                    .clickable { termsAccepted = !termsAccepted },
                contentAlignment = Alignment.Center
            ) {
                if (termsAccepted) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Checked",
                        tint = RosadoNeon,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Acepto los términos y condiciones",
                fontSize = 14.sp,
                color = TextoSecundario
            )
        }
        ReusableButton(
            "CONTINUAR",
            NavigationToVerification,
            modifier = Modifier.padding(vertical = 20.dp))
    }
}