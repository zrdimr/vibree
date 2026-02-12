import SwiftUI

@main
struct VibreeApp: App {
    @StateObject private var repository = SensorDataRepository.shared
    
    let persistenceController = PersistenceController.shared

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(repository)
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
        }
    }
}
