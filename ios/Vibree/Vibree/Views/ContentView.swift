import SwiftUI

struct ContentView: View {
    @EnvironmentObject var repository: SensorDataRepository
    
    enum Screen {
        case loading, login, dashboard, profile, vitals, history, settings
    }
    
    @State private var currentScreen: Screen = .loading
    
    var body: some View {
        ZStack {
            Color.black.edgesIgnoringSafeArea(.all) // Background
            
            switch currentScreen {
            case .loading:
                LoadingView(onTimeout: {
                    // Simple logic: if logged in (mocked) go to dashboard, else login
                    // In real app check Auth
                    currentScreen = .login
                })
            case .login:
                LoginView(onLoginClick: {
                    currentScreen = .dashboard
                })
            case .dashboard:
                DashboardView(
                    onNavigateToProfile: { currentScreen = .profile },
                    onNavigateToVitals: { currentScreen = .vitals }
                )
            case .profile:
                ProfileView(onBack: { currentScreen = .dashboard })
            case .vitals:
                VitalsView(onBack: { currentScreen = .dashboard })
            case .history:
                 Text("History Screen (Placeholder)")
                     .foregroundColor(.white)
            case .settings:
                 Text("Settings Screen (Placeholder)")
                     .foregroundColor(.white)
            }
        }
    }
}
