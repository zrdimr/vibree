package com.example.wearablecollector.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.wearablecollector.ui.theme.VibreeNeonPink
import com.example.wearablecollector.ui.theme.VibreeNeonPurple
import kotlin.math.sin

@Composable
fun HeartbeatWaveform(
    modifier: Modifier = Modifier,
    vibe: String = "Resonant"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveAnimation")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        val path = Path()
        path.moveTo(0f, centerY)

        for (x in 0..width.toInt()) {
            val xPos = x.toFloat()
            // Dynamic frequency and amplitude could be based on 'vibe'
            val frequency = 0.02f
            val amplitude = height * 0.3f
            
            // Basic sine wave + simulated heartbeat spike
            val sine = sin(xPos * frequency + phase) * amplitude
            
            // Add a "beat" spike in the middle
            val beatPos = (width / 2)
            val dist = Math.abs(xPos - beatPos)
            val spike = if (dist < 50) {
                 (1 - dist / 50) * amplitude * 1.5f * sin((xPos - beatPos) * 0.2f)
            } else {
                0f
            }

            val yPos = centerY + sine + spike
            
            if (x == 0) {
                path.moveTo(xPos, yPos)
            } else {
                path.lineTo(xPos, yPos)
            }
        }

        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(VibreeNeonPurple, VibreeNeonPink, VibreeNeonPurple)
            ),
            style = Stroke(width = 8f)
        )
    }
}
