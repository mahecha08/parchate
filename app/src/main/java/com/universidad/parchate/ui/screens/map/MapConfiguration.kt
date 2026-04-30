package com.universidad.parchate.ui.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.universidad.parchate.BuildConfig
import com.universidad.parchate.R
import com.universidad.parchate.ui.theme.TextoSecundario

internal val isMapsConfigured: Boolean
    get() = BuildConfig.MAPS_API_KEY.isNotBlank()

@Composable
internal fun MapConfigurationWarning() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF25233D))
        ) {
            Text(
                text = stringResource(R.string.map_config_missing_title),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp)
            )
            Text(
                text = stringResource(R.string.map_config_missing_message),
                color = TextoSecundario,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 18.dp)
            )
        }
    }
}
