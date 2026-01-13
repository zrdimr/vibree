import Foundation
import Combine
import CoreBluetooth

class SensorDataRepository: ObservableObject {
    static let shared = SensorDataRepository()
    
    @Published var heartRate: Int = 0
    @Published var hrv: Int = 0
    @Published var gsr: Float = 0.0
    @Published var eda: Float = 0.0
    @Published var stressLevel: Int = 0
    @Published var status: String = "Disconnected"
    @Published var scannedDevices: [CBPeripheral] = []
    
    private let stressCalculator = StressCalculator()
    
    private init() {}
    
    func addScannedDevice(_ device: CBPeripheral) {
        if !scannedDevices.contains(where: { $0.identifier == device.identifier }) {
            scannedDevices.append(device)
        }
    }
    
    func clearScannedDevices() {
        scannedDevices.removeAll()
    }
    
    func updateHeartRate(_ hr: Int) {
        self.heartRate = hr
    }
    
    func updateHrv(rrInterval: Int) {
        let result = stressCalculator.processRR(rrMs: rrInterval)
        
        self.hrv = result.hrv
        self.stressLevel = result.stressLevel
        
        // In a real app, save to CoreData or Realm here
        if result.hrv > 0 {
            print("Logged: HR: \(heartRate), HRV: \(result.hrv), Stress: \(result.stressLevel)")
        }
    }
    
    func updateGsr(_ value: Float) {
        self.gsr = value
    }
    
    func updateEda(_ value: Float) {
        self.eda = value
    }
    
    func updateStatus(_ newStatus: String) {
        self.status = newStatus
        if newStatus == "Disconnected" {
            stressCalculator.reset()
            self.heartRate = 0
            self.hrv = 0
            self.stressLevel = 0
        }
    }
}
