import SwiftUI

struct LoadingView: View {
    var onTimeout: () -> Void
    
    var body: some View {
        VStack {
            Text("Vibree")
                .font(.largeTitle)
                .bold()
                .foregroundColor(.pink)
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                .scaleEffect(1.5)
                .padding()
        }
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                onTimeout()
            }
        }
    }
}

struct LoginView: View {
    var onLoginClick: () -> Void
    
    var body: some View {
        VStack {
            Spacer()
            Text("Welcome to Vibree")
                .font(.title)
                .bold()
                .foregroundColor(.white)
            
            Text("Monitor your vitals in real-time")
                .foregroundColor(.gray)
                .padding(.bottom, 50)
            
            Button(action: onLoginClick) {
                HStack {
                    Image(systemName: "g.circle.fill")
                    Text("Sign in with Google")
                }
                .font(.headline)
                .foregroundColor(.black)
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.white)
                .cornerRadius(12)
            }
            .padding(.horizontal, 40)
            
            Spacer()
        }
        .background(Color.black)
    }
}
