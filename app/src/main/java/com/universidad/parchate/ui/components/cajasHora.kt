package com.universidad.parchate.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar

@Composable
fun TimePickerCaja(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)


    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour: Int, selectedMinute: Int ->
            onValueChange("${"%02d".format(selectedHour)}:${"%02d".format(selectedMinute)}")
        },
        hour,
        minute,
        true
    )


    Box(modifier = Modifier.clickable { timePickerDialog.show() }) {
        cajasTexto(
            value = value,
            onValueChange = {},
            label = label,
            leadingIcon = Icons.Default.Schedule,
            modifier = Modifier
        )
    }
}