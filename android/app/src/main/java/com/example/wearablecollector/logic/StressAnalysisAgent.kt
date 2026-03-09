package com.example.wearablecollector.logic

import android.content.Context
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Edge AI Agent for local stress analysis.
 * Uses PyTorch Mobile for deep learning inference combined with heuristics.
 */
object StressAnalysisAgent {
    private val rrBuffer = ArrayDeque<Int>()
    private const val BUFFER_SIZE = 60 // 60 beats sliding window (~1 minute)
    
    private var hrvModel: Module? = null
    private var nlpModel: Module? = null
    private var tokenizer: BertTokenizer? = null
    
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
    
    fun initialize(context: Context) {
        try {
            val hrvPath = assetFilePath(context, "hrv_model.ptl")
            val nlpPath = assetFilePath(context, "nlp_model.ptl")
            if (hrvPath != null) {
                hrvModel = LiteModuleLoader.load(hrvPath)
            }
            if (nlpPath != null) {
                nlpModel = LiteModuleLoader.load(nlpPath)
            }
            tokenizer = BertTokenizer(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun assetFilePath(context: Context, assetName: String): String? {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        try {
            context.assets.open(assetName).use { inputStream ->
                FileOutputStream(file).use { os ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        os.write(buffer, 0, read)
                    }
                    os.flush()
                }
                return file.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun process(rrIntervalMs: Int): Prediction {
        if (rrIntervalMs <= 0 || rrIntervalMs > 2000) return emptyPrediction()

        updateBuffer(rrIntervalMs)

        if (rrBuffer.size < 10) return emptyPrediction() // Need minimum data

        val rmssd = calculateRMSSD(rrBuffer)
        val sdnn = calculateSDNN(rrBuffer)
        
        var mlScore = -1
        hrvModel?.let { model ->
            try {
                // Construct input tensor of shape (1, 1, 34) matching training HRV model
                val features = FloatArray(34) { 0f }
                features[3] = rmssd.toFloat()
                features[4] = sdnn.toFloat()
                val inputTensor = Tensor.fromBlob(features, longArrayOf(1, 1, 34))
                
                val outputTensor = model.forward(IValue.from(inputTensor)).toTensor()
                val scores = outputTensor.dataAsFloatArray
                
                if (scores.size >= 2) {
                    if (scores[1] > scores[0]) {
                        val diff = scores[1] - scores[0]
                        mlScore = 50 + (diff * 5).toInt().coerceIn(0, 50) 
                    } else {
                        val diff = scores[0] - scores[1]
                        mlScore = 50 - (diff * 5).toInt().coerceIn(0, 50)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        return inferStress(rmssd, sdnn, mlScore)
    }

    /**
     * Analyzes text for psychological stress markers using MobileBERT.
     */
    fun analyzeText(text: String): Int {
        val t = tokenizer ?: return -1
        val (ids, mask) = t.tokenize(text)
        return processNLP(ids, mask)
    }
    
    private fun processNLP(textTokens: LongArray, attentionMask: LongArray): Int {
        var nlpScore = -1
        nlpModel?.let { model ->
            try {
                val inputIdsTensor = Tensor.fromBlob(textTokens, longArrayOf(1, 128))
                val maskTensor = Tensor.fromBlob(attentionMask, longArrayOf(1, 128))
                
                val outputTensor = model.forward(IValue.from(inputIdsTensor), IValue.from(maskTensor)).toTensor()
                val scores = outputTensor.dataAsFloatArray
                
                if (scores.size >= 2) {
                    // Normalize to 0-100 scale [Non-Stress, Stress]
                    if (scores[1] > scores[0]) {
                        nlpScore = 70 + (scores[1] - scores[0]).toInt().coerceIn(0, 30)
                    } else {
                        nlpScore = 30 - (scores[0] - scores[1]).toInt().coerceIn(0, 30)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return nlpScore
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

    private fun inferStress(rmssd: Double, sdnn: Double, mlScore: Int = -1): Prediction {
        var score = 0
        var label = StressLabel.UNKNOWN
        var confidence = 0.8f 

        if (rmssd < 20) {
            score = 80 + ((20 - rmssd) / 20 * 20).toInt()
        } else if (rmssd < 40) {
            score = 50 + ((40 - rmssd) / 20 * 30).toInt()
        } else if (rmssd < 70) {
            score = 20 + ((70 - rmssd) / 30 * 30).toInt()
        } else {
            score = (20 - (rmssd - 70) / 100 * 20).toInt().coerceAtLeast(0)
        }
        
        if (mlScore != -1) {
             score = (score * 0.4 + mlScore * 0.6).toInt().coerceIn(0, 100)
             confidence = 0.95f
        }
        
        if (score >= 80) label = StressLabel.HIGH_STRESS
        else if (score >= 50) label = StressLabel.MEDIUM_STRESS
        else if (score >= 20) label = StressLabel.LOW_STRESS
        else label = StressLabel.RELAXED

        confidence *= (rrBuffer.size.toFloat() / BUFFER_SIZE).coerceAtMost(1.0f)

        return Prediction(score, label, confidence, rmssd, sdnn)
    }

    private fun emptyPrediction() = Prediction(0, StressLabel.UNKNOWN, 0f, 0.0, 0.0)

    fun reset() {
        rrBuffer.clear()
    }
}
