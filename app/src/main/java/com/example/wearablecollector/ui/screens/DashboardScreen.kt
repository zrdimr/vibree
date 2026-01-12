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
    stressLevel: String = "0",
    hrv: String = "0",
    heartRate: String = "0",
    onNavigateToMatch: () -> Unit,
    onNavigateToVitals: () -> Unit
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
        Spacer(modifier = Modifier.height(20.dp))

        // Status Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), MaterialTheme.shapes.medium)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                 Text("HR", color = Color.Gray)
                 Text("$heartRate BPM", color = VibreeNeonPink, fontWeight = FontWeight.Bold)
             }
             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                 Text("HRV", color = Color.Gray)
                 Text("$hrv ms", color = Color.Cyan, fontWeight = FontWeight.Bold)
             }
             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                 Text("Stress", color = Color.Gray)
                 Text(stressLevel, color = if(stressLevel.toIntOrNull() ?: 0 > 50) Color.Red else Color.Green, fontWeight = FontWeight.Bold)
             }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
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
        
        Button(
            onClick = onNavigateToVitals,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("View Vitals Details")
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToMatch,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Find Connections")
        }
    }
}
