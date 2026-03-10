import Foundation

/**
 * Edge AI Agent for local stress analysis on iOS.
 * Integrates PyTorch Mobile (CNN-LSTM and MobileBERT) with heuristic fallback.
 */
class StressAnalysisAgent {
    static let shared = StressAnalysisAgent()
    
    private var rrBuffer: [Int] = []
    private let BUFFER_SIZE = 60
    
    private var hrvModule: TorchModule?
    private var nlpModule: TorchModule?
    private let tokenizer = BertTokenizer()
    
    enum StressLabel: String {
        case relaxed = "Relaxed"
        case lowStress = "Low Stress"
        case mediumStress = "Medium Stress"
        case highStress = "High Stress"
        case unknown = "Unknown"
    }
    
    struct Prediction {
        let stressScore: Int
        let label: StressLabel
        let confidence: Float
        let rmssd: Double
        let sdnn: Double
    }
    
    private init() {
        if let hrvPath = Bundle.main.path(forResource: "hrv_model", ofType: "ptl") {
            hrvModule = TorchModule(fileAtPath: hrvPath)
        }
        if let nlpPath = Bundle.main.path(forResource: "nlp_model", ofType: "ptl") {
            nlpModule = TorchModule(fileAtPath: nlpPath)
        }
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
        
        var mlScore: Int = -1
        if let module = hrvModule {
            // Construct features: [34 features] matches CNN-LSTM training data
            var features = [Double](repeating: 0.0, count: 34)
            features[3] = rmssd
            features[4] = sdnn
            
            if let output = module.predictPayload(features as [NSNumber]) {
                if output.count >= 2 {
                    let nonStress = output[0].floatValue
                    let stress = output[1].floatValue
                    
                    if stress > nonStress {
                         let diff = stress - nonStress
                         mlScore = 50 + Int(min(diff * 5, 50))
                    } else {
                         let diff = nonStress - stress
                         mlScore = 50 - Int(min(diff * 5, 50))
                    }
                }
            }
        }
        
        return inferStress(rmssd: rmssd, sdnn: sdnn, mlScore: mlScore)
    }
    
    /**
     * Analyze context for stress markers.
     */
    func analyzeText(_ text: String) -> Int {
        let (ids, mask) = tokenizer.tokenize(text)
        
        if let module = nlpModule, let output = module.predictNLPWithIds(ids as [NSNumber], mask: mask as [NSNumber]) {
            if output.count >= 2 {
                let nonStress = output[0].floatValue
                let stress = output[1].floatValue
                if stress > nonStress {
                    return 70 + Int(min((stress - nonStress) * 10, 30))
                } else {
                    return 30 - Int(min((nonStress - stress) * 10, 30))
                }
            }
        }
        return -1
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
    
    private func inferStress(rmssd: Double, sdnn: Double, mlScore: Int = -1) -> Prediction {
        var score = 0
        var label = StressLabel.unknown
        var confidence: Float = 0.8
        
        if rmssd < 20 {
            score = 80 + Int(min((20.0 - rmssd) / 20.0 * 20.0, 20.0))
        } else if rmssd < 40 {
            score = 50 + Int(min((40.0 - rmssd) / 20.0 * 30.0, 30.0))
        } else if rmssd < 70 {
            score = 20 + Int(min((70.0 - rmssd) / 30.0 * 30.0, 30.0))
        } else {
            score = max(0, 20 - Int(min((rmssd - 70.0) / 100.0 * 20.0, 20.0)))
        }
        
        if mlScore != -1 {
            score = Int(Double(score) * 0.4 + Double(mlScore) * 0.6)
            confidence = 0.95
        }
        
        if score >= 80 { label = .highStress }
        else if score >= 50 { label = .mediumStress }
        else if score >= 20 { label = .lowStress }
        else { label = .relaxed }
        
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
