package com.example.wearablecollector.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.wearablecollector.ui.components.HeartbeatWaveform
import com.example.wearablecollector.ui.theme.VibreeNeonPink
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) // Show logo for 2 seconds then proceed
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HeartbeatWaveform(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                vibe = "Loading"
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Vibree", style = MaterialTheme.typography.headlineMedium, color = VibreeNeonPink)
        }
    }
}
