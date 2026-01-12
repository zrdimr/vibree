package com.example.wearablecollector.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.wearablecollector.ui.components.HeartbeatWaveform
import com.example.wearablecollector.ui.theme.VibreeNeonPink

@Composable
fun DashboardScreen(
    onNavigateToMatch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        // Heartbeat Visualization Circle
        Box(
            modifier = Modifier
                .size(300.dp)
                // In a real app we might use a glassmorphic circle background here
                .background(Color.White.copy(alpha = 0.05f), shape = MaterialTheme.shapes.extraLarge),
            contentAlignment = Alignment.Center
        ) {
            HeartbeatWaveform(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                vibe = "Resonant"
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Your beats\nare telling us",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Current Vibe: Resonant",
            style = MaterialTheme.typography.bodyLarge,
            color = VibreeNeonPink
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNavigateToMatch,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Find Connections")
        }
    }
}
