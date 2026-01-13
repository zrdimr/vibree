import Foundation

class StressCalculator {
    private var rrIntervals: [Int] = []
    private let WINDOW_SIZE = 30 // Use last 30 intervals (~20-30 seconds)
    
    struct Result {
        let hrv: Int
        let stressLevel: Int
    }
    
    func processRR(rrMs: Int) -> Result {
        if rrMs <= 0 || rrMs > 2000 {
            return Result(hrv: 0, stressLevel: 0) // Filter invalid data
        }
        
        rrIntervals.append(rrMs)
        if rrIntervals.count > WINDOW_SIZE {
            rrIntervals.removeFirst()
        }
        
        if rrIntervals.count < 2 {
            return Result(hrv: 0, stressLevel: 0)
        }
        
        let rmssd = calculateRMSSD(intervals: rrIntervals)
        let stress = calculateStress(hrv: rmssd)
        
        return Result(hrv: Int(rmssd), stressLevel: stress)
    }
    
    private func calculateRMSSD(intervals: [Int]) -> Double {
        var sumSquares = 0.0
        for i in 0..<(intervals.count - 1) {
            let diff = Double(intervals[i + 1] - intervals[i])
            sumSquares += pow(diff, 2.0)
        }
        return sqrt(sumSquares / Double(intervals.count - 1))
    }
    
    private func calculateStress(hrv: Double) -> Int {
        // High HRV (e.g. 100ms) -> Low Stress (0)
        // Low HRV (e.g. 10ms) -> High Stress (100)
        
        let minHrv = 10.0
        let maxHrv = 100.0
        
        if hrv >= maxHrv { return 1 } // Minimum stress
        if hrv <= minHrv { return 100 } // Maximum stress
        
        // Invert scale: higher HRV = lower stress
        let normalized = (hrv - minHrv) / (maxHrv - minHrv)
        return Int((1.0 - normalized) * 100)
    }
    
    func reset() {
        rrIntervals.removeAll()
    }
}
