import Foundation

/**
 * Edge AI Agent for local stress analysis on iOS.
 * Implements sliding window feature extraction and heuristic stress classification.
 */
class StressAnalysisAgent {
    static let shared = StressAnalysisAgent()
    
    private var rrBuffer: [Int] = []
    private let BUFFER_SIZE = 60 // 60 beats sliding window
    
    enum StressLabel: String {
        case relaxed = "Relaxed"
        case lowStress = "Low Stress"
        case mediumStress = "Medium Stress"
        case highStress = "High Stress"
        case unknown = "Unknown"
    }
    
    struct Prediction {
        let stressScore: Int // 0-100
        let label: StressLabel
        let confidence: Float // 0.0 - 1.0
        let rmssd: Double
        let sdnn: Double
    }
    
    func process(rrIntervalMs: Int) -> Prediction {
        if rrIntervalMs <= 0 || rrIntervalMs > 2000 {
            return emptyPrediction()
        }
        
        updateBuffer(rr: rrIntervalMs)
        
        if rrBuffer.count < 10 {
            return emptyPrediction()
        }
        
        let rmssd = calculateRMSSD(data: rrBuffer)
        let sdnn = calculateSDNN(data: rrBuffer)
        
        return inferStress(rmssd: rmssd, sdnn: sdnn)
    }
    
    private func updateBuffer(rr: Int) {
        rrBuffer.append(rr)
        if rrBuffer.count > BUFFER_SIZE {
            rrBuffer.removeFirst()
        }
    }
    
    private func calculateRMSSD(data: [Int]) -> Double {
        var sumSquares = 0.0
        for i in 0..<(data.count - 1) {
            let diff = Double(data[i + 1] - data[i])
            sumSquares += pow(diff, 2.0)
        }
        return sqrt(sumSquares / Double(data.count - 1))
    }
    
    private func calculateSDNN(data: [Int]) -> Double {
        let total = data.reduce(0, +)
        let mean = Double(total) / Double(data.count)
        
        var sumSquaredDiff = 0.0
        for x in data {
            sumSquaredDiff += pow(Double(x) - mean, 2.0)
        }
        return sqrt(sumSquaredDiff / Double(data.count))
    }
    
    private func inferStress(rmssd: Double, sdnn: Double) -> Prediction {
        // HRV Norms (Approximate):
        // RMSSD < 20ms -> High Stress
        // RMSSD 20-50ms -> Moderate
        // RMSSD > 50ms -> Relaxed
        
        var score = 0
        var label = StressLabel.unknown
        var confidence: Float = 0.8
        
        if rmssd < 20 {
            // High Stress (80-100)
            let diff = 20.0 - rmssd
            let normalized = min(diff / 20.0, 1.0)
            score = 80 + Int(normalized * 20.0)
            label = .highStress
        } else if rmssd < 40 {
            // Medium Stress (50-80)
            let diff = 40.0 - rmssd
            let normalized = min(diff / 20.0, 1.0)
            score = 50 + Int(normalized * 30.0)
            label = .mediumStress
        } else if rmssd < 70 {
            // Low Stress (20-50)
            let diff = 70.0 - rmssd
            let normalized = min(diff / 30.0, 1.0)
            score = 20 + Int(normalized * 30.0)
            label = .lowStress
        } else {
            // Relaxed (0-20)
            let diff = rmssd - 70.0
            let normalized = min(diff / 100.0, 1.0)
            score = max(0, 20 - Int(normalized * 20.0))
            label = .relaxed
        }
        
        // Adjust confidence based on sample size
        let sizeFactor = Float(rrBuffer.count) / Float(BUFFER_SIZE)
        confidence *= min(sizeFactor, 1.0)
        
        return Prediction(stressScore: score, label: label, confidence: confidence, rmssd: rmssd, sdnn: sdnn)
    }
    
    private func emptyPrediction() -> Prediction {
        return Prediction(stressScore: 0, label: .unknown, confidence: 0.0, rmssd: 0.0, sdnn: 0.0)
    }
    
    func reset() {
        rrBuffer.removeAll()
    }
}
