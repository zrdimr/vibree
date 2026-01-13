import SwiftUI

struct ProfileView: View {
    var onBack: () -> Void
    
    var body: some View {
        VStack {
            HStack {
                Button(action: onBack) {
                    Image(systemName: "chevron.left")
                        .foregroundColor(.white)
                        .padding()
                }
                Spacer()
                Text("Profile")
                    .font(.headline)
                    .foregroundColor(.white)
                Spacer()
                Spacer().frame(width: 40)
            }
            .background(Color.black)
            
            Spacer()
            Text("Profile Settings & History")
                .foregroundColor(.gray)
            Spacer()
        }
        .background(Color.black)
    }
}
