package com.universidad.parchate.ui.screens.Registration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
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
fun RegistrationScreen(
    NavigationToLogin:()-> Unit,
    NavigationToPolicy: ()-> Unit){
    var scrollState = rememberScrollState()
    var email by remember { mutableStateOf("") }
    var name by remember {mutableStateOf("")}
    var dob by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrincipal)
            .verticalScroll(scrollState)
            .padding(vertical = 25.dp)

    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally

        ){
            Box(
                modifier = Modifier.fillMaxWidth()
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
            }
            Row(
                verticalAlignment = Alignment.CenterVertically){
                Box(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        .background(color=RosadoNeon)
                        .height(6.dp)
                        .width(100.dp)
                )
                Box(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        .background(color=ProgressMissing)
                        .height(6.dp)
                        .width(100.dp)
                )
                Box(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        .background(color=ProgressMissing)
                        .height(6.dp)
                        .width(100.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier= Modifier.weight(1f))

                Text(
                    text = "REGISTRATE EN PARCHATE",
                    color = RosadoNeon,
                    fontSize = 40.sp,
                    lineHeight = 50.sp,
                    modifier = Modifier.padding(vertical = 35.dp, horizontal = 20.dp)
                )
                cajasTexto(
                    modifier = Modifier.padding(vertical = 10.dp),
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                cajasTexto(
                    modifier = Modifier.padding(vertical = 10.dp),
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                cajasTexto(
                    modifier = Modifier.padding(vertical = 10.dp),
                    value = dob,
                    onValueChange = { dob = it },
                    label = "Fecha de nacimiento",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                ReusableButton(
                    "CONTINUAR",
                    NavigationToPolicy,
                    modifier = Modifier.padding(vertical = 20.dp))

                Row(modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically){
                    Text(
                        text = "¿Ya tienes una cuenta?",
                        color = TextoSecundario
                    )
                    TextButton(
                        onClick = NavigationToLogin,
                        contentPadding = PaddingValues(0.dp)
                    ){
                        Text(
                            text = " Inicia sesion",
                            color = RosadoNeon
                        )
                    }
                }

            }
        }
    }

}

