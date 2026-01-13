import SwiftUI

@main
struct VibreeApp: App {
    @StateObject private var repository = SensorDataRepository.shared
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(repository)
        }
    }
}
