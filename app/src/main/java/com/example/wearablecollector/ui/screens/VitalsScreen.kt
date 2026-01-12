package com.example.wearablecollector.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wearablecollector.ui.theme.VibreeNeonPink
import com.example.wearablecollector.ui.theme.VibreeTeal
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalsScreen(
    stressLevel: Int,
    hrv: Int,
    heartRate: Int,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vitals & Stress", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Stress Gauge
            Text("Current Stress Level", color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Box(contentAlignment = Alignment.Center) {
                StressGauge(stressLevel)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$stressLevel", style = MaterialTheme.typography.displayMedium, color = Color.White)
                    Text(text = "/100", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Detailed Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VitalCard(
                    title = "Heart Rate",
                    value = "$heartRate BPM",
                    color = VibreeNeonPink,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                VitalCard(
                    title = "HRV",
                    value = "$hrv ms",
                    color = VibreeTeal,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Understanding your Vitals:\n\n" +
                        "• HRV (Heart Rate Variability): Higher is generally better, indicating lower stress and better recovery.\n" +
                        "• Stress Level: Calculated from HRV. Lower is better. Take a deep breath if it's high!",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun StressGauge(level: Int) {
    Canvas(modifier = Modifier.size(200.dp)) {
        val sweepAngle = 240f
        val startAngle = 150f
        
        // Background Arc
        drawArc(
            color = Color.DarkGray.copy(alpha = 0.5f),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = 30f, cap = StrokeCap.Round)
        )

        // Progress Arc
        val progressSweep = (level / 100f) * sweepAngle
        val progressColor = if (level < 40) VibreeTeal else if (level < 70) Color.Yellow else VibreeNeonPink
        
        drawArc(
            color = progressColor,
            startAngle = startAngle,
            sweepAngle = progressSweep,
            useCenter = false,
            style = Stroke(width = 30f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun VitalCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = Color.Gray, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = color, style = MaterialTheme.typography.headlineSmall)
    }
}
