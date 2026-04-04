package com.universidad.parchate.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.universidad.parchate.ui.theme.IconColor
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario

@Composable
fun cajasTexto(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextoSecundario) },
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null, tint = IconColor) }
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Margen responsive
        shape = RoundedCornerShape(20.dp), // Bordes redondeados
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent, // Sin fondo al estar enfocado
            unfocusedContainerColor = Color.Transparent, // Sin fondo al estar desenfocado
            focusedIndicatorColor = RosadoNeon, // Borde neón enfocado
            unfocusedIndicatorColor = RosadoNeon.copy(alpha = 0.5f), // Borde suave desenfocado
            cursorColor = RosadoNeon, // Cursor rosa
            focusedTextColor = Color.White, // Texto blanco al escribir
            unfocusedTextColor = Color.White // Texto blanco al escribir
        )
    )
}