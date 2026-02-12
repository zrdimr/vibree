import SwiftUI

struct ContentView: View {
    @EnvironmentObject var repository: SensorDataRepository
    
    enum Screen {
        case loading, login, dashboard, profile, vitals, history, settings, social
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
                    onNavigateToVitals: { currentScreen = .vitals },
                    onNavigateToSocial: { currentScreen = .social }
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
            case .social:
                SocialFeedView()
                    // Add a back button mechanism since SocialFeedView is NavigationView
                    // But standard NavigationView doesn't pop back to our custom coordinator easily.
                    // For MVP, we can wrap SocialFeedView or modify it to accept onBack.
                    // Let's modify SocialFeedView to have a toolbar 'Done' or 'Back' if needed.
                    // Or actually, just let it be. But we need a way to go back to Dashboard.
                    // Let's add an overlay button or use a custom header in SocialFeedView.
                    .overlay(
                        VStack {
                            HStack {
                                Button(action: { currentScreen = .dashboard }) {
                                    Image(systemName: "house.fill")
                                        .padding()
                                        .background(Color.white.opacity(0.8))
                                        .clipShape(Circle())
                                }
                                .padding()
                                Spacer()
                            }
                            Spacer()
                        }
                    )
            }
        }
    }
}
