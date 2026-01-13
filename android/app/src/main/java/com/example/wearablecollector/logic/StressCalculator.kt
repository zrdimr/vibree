package com.example.wearablecollector.logic

import kotlin.math.pow
import kotlin.math.sqrt

object StressCalculator {
    private val rrIntervals = ArrayDeque<Int>()
    private const val WINDOW_SIZE = 30 // Use last 30 intervals for calculation (~20-30 seconds)

    data class Result(
        val hrv: Int,
        val stressLevel: Int
    )

    fun processRR(rrMs: Int): Result {
        if (rrMs <= 0 || rrMs > 2000) return Result(0, 0) // Filter invalid data

        rrIntervals.addLast(rrMs)
        if (rrIntervals.size > WINDOW_SIZE) {
            rrIntervals.removeFirst()
        }

        if (rrIntervals.size < 2) {
            return Result(0, 0)
        }

        val rmssd = calculateRMSSD(rrIntervals.toList())
        val stress = calculateStress(rmssd)

        return Result(rmssd.toInt(), stress)
    }

    private fun calculateRMSSD(intervals: List<Int>): Double {
        var sumSquares = 0.0
        for (i in 0 until intervals.size - 1) {
            val diff = intervals[i + 1] - intervals[i]
            sumSquares += diff.toDouble().pow(2.0)
        }
        return sqrt(sumSquares / (intervals.size - 1))
    }

    private fun calculateStress(hrv: Double): Int {
        // Simple mapping: High HRV (e.g. 100ms) -> Low Stress (0)
        // Low HRV (e.g. 10ms) -> High Stress (100)
        // This is a simplified approximation.
        
        val minHrv = 10.0
        val maxHrv = 100.0
        
        if (hrv >= maxHrv) return 1 // Minimum stress
        if (hrv <= minHrv) return 100 // Maximum stress
        
        // Invert scale: higher HRV = lower stress
        val normalized = (hrv - minHrv) / (maxHrv - minHrv)
        return ((1.0 - normalized) * 100).toInt()
    }
    
    fun reset() {
        rrIntervals.clear()
    }
}
