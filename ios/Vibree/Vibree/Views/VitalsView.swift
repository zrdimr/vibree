import SwiftUI

struct VitalsView: View {
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
                Text("Vitals")
                    .font(.headline)
                    .foregroundColor(.white)
                Spacer()
                Spacer().frame(width: 40)
            }
            .background(Color.black)
            
            Spacer()
            Text("Detailed Vitals Charts User Interface")
                .foregroundColor(.gray)
            Spacer()
        }
        .background(Color.black)
    }
}
