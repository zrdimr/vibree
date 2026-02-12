import Foundation

struct StressResult {
    let stressScore: Float
    let sentiment: String
}

class StressAnalyzer {
    static let shared = StressAnalyzer()
    
    private init() {}
    
    func analyze(text: String) -> StressResult {
        // MOCK IMPLEMENTATION
        // TODO: Integrate TFLite later
        
        let lowerText = text.lowercased()
        if lowerText.contains("stress") || lowerText.contains("anxious") || lowerText.contains("worried") {
            return StressResult(stressScore: 85.0, sentiment: "Negative")
        } else if lowerText.contains("happy") || lowerText.contains("good") || lowerText.contains("calm") {
            return StressResult(stressScore: 20.0, sentiment: "Positive")
        } else {
            return StressResult(stressScore: 50.0, sentiment: "Neutral")
        }
    }
}
