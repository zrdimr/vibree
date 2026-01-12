package com.example.wearablecollector.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wearablecollector.ui.components.HeartbeatWaveform
import com.example.wearablecollector.ui.theme.VibreeDarkPurple
import com.example.wearablecollector.ui.theme.VibreeNeonPurple

@Composable
fun MatchScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(onClick = onBack) {
                Text("< Back", color = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Connections",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Match Card (Glassmorphism style simulated)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Placeholder for User Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.Gray, shape = MaterialTheme.shapes.medium)
                )
                
                HeartbeatWaveform(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    vibe = "Sync"
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Sarah, 26",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "98% Resonance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                Button(
                    onClick = { /* TODO: Send Wave */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Brush.horizontalGradient(listOf(VibreeNeonPurple, Color.White)))
                ) {
                    Text("Send Wave")
                }
            }
        }
    }
}
