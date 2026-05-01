package com.universidad.parchate.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.universidad.parchate.R
import com.universidad.parchate.ui.theme.IconColor
import com.universidad.parchate.ui.theme.RosadoNeon
import com.universidad.parchate.ui.theme.TextoSecundario
import java.util.Calendar

@Composable
fun TimePickerCaja(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val initialHour = value
        .takeIf { Regex("^\\d{2}:\\d{2}$").matches(it) }
        ?.substringBefore(":")
        ?.toIntOrNull()
        ?: calendar.get(Calendar.HOUR_OF_DAY)
    val initialMinute = value
        .takeIf { Regex("^\\d{2}:\\d{2}$").matches(it) }
        ?.substringAfter(":")
        ?.toIntOrNull()
        ?: calendar.get(Calendar.MINUTE)

    val timePickerDialog = remember(context, value) {
        TimePickerDialog(
            context,
            { _, selectedHour: Int, selectedMinute: Int ->
                onValueChange("${"%02d".format(selectedHour)}:${"%02d".format(selectedMinute)}")
            },
            initialHour,
            initialMinute,
            true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = label,
            color = TextoSecundario,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Surface(
            onClick = { timePickerDialog.show() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.Transparent,
            border = BorderStroke(1.dp, RosadoNeon.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = IconColor
                    )

                    Text(
                        text = if (value.isBlank()) {
                            stringResource(R.string.create_hora_helper)
                        } else {
                            value
                        },
                        color = if (value.isBlank()) TextoSecundario else Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = RosadoNeon
                )
            }
        }
    }
}
