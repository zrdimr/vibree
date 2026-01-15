package com.example.wearablecollector.logic

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Edge AI Agent for local stress analysis.
 * Uses physiological feature extraction and heuristic classification.
 */
object StressAnalysisAgent {
    private val rrBuffer = ArrayDeque<Int>()
    private const val BUFFER_SIZE = 60 // 60 beats sliding window (~1 minute)
    
    data class Prediction(
        val stressScore: Int, // 0-100
        val label: StressLabel,
        val confidence: Float, // 0.0 - 1.0
        val rmssd: Double,
        val sdnn: Double
    )

    enum class StressLabel {
        RELAXED, LOW_STRESS, MEDIUM_STRESS, HIGH_STRESS, UNKNOWN
    }

    fun process(rrIntervalMs: Int): Prediction {
        if (rrIntervalMs <= 0 || rrIntervalMs > 2000) return emptyPrediction()

        updateBuffer(rrIntervalMs)

        if (rrBuffer.size < 10) return emptyPrediction() // Need minimum data

        val rmssd = calculateRMSSD(rrBuffer)
        val sdnn = calculateSDNN(rrBuffer)
        
        return inferStress(rmssd, sdnn)
    }

    private fun updateBuffer(rr: Int) {
        rrBuffer.addLast(rr)
        if (rrBuffer.size > BUFFER_SIZE) {
            rrBuffer.removeFirst()
        }
    }

    private fun calculateRMSSD(data: List<Int>): Double {
        var sumSquares = 0.0
        for (i in 0 until data.size - 1) {
            val diff = (data[i + 1] - data[i]).toDouble()
            sumSquares += diff.pow(2)
        }
        return sqrt(sumSquares / (data.size - 1))
    }

    private fun calculateSDNN(data: List<Int>): Double {
        val mean = data.average()
        var sumSquaredDiff = 0.0
        for (x in data) {
            sumSquaredDiff += (x - mean).pow(2)
        }
        return sqrt(sumSquaredDiff / data.size)
    }

    private fun inferStress(rmssd: Double, sdnn: Double): Prediction {
        // HRV Norms (Approximate for adults):
        // RMSSD < 20ms -> High Stress / Unhealthy
        // RMSSD 20-50ms -> Moderate
        // RMSSD > 50ms -> Healthy / Relaxed
        
        // SDNN also correlates with overall variability.

        var score = 0
        var label = StressLabel.UNKNOWN
        var confidence = 0.8f // Basline confidence

        if (rmssd < 20) {
            score = 80 + ((20 - rmssd) / 20 * 20).toInt() // 80-100
            label = StressLabel.HIGH_STRESS
        } else if (rmssd < 40) {
            score = 50 + ((40 - rmssd) / 20 * 30).toInt() // 50-80
            label = StressLabel.MEDIUM_STRESS
        } else if (rmssd < 70) {
            score = 20 + ((70 - rmssd) / 30 * 30).toInt() // 20-50
            label = StressLabel.LOW_STRESS
        } else {
            score = (20 - (rmssd - 70) / 100 * 20).toInt().coerceAtLeast(0) // 0-20
            label = StressLabel.RELAXED
        }

        // Adjust confidence based on data quantity
        confidence *= (rrBuffer.size.toFloat() / BUFFER_SIZE).coerceAtMost(1.0f)

        return Prediction(score, label, confidence, rmssd, sdnn)
    }

    private fun emptyPrediction() = Prediction(0, StressLabel.UNKNOWN, 0f, 0.0, 0.0)

    fun reset() {
        rrBuffer.clear()
    }
}
