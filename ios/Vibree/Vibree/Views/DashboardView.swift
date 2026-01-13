import SwiftUI

struct DashboardView: View {
    @EnvironmentObject var repository: SensorDataRepository
    var onNavigateToProfile: () -> Void
    var onNavigateToVitals: () -> Void
    
    var body: some View {
        VStack {
            // Header
            HStack {
                Text("Dashboard")
                    .font(.title)
                    .bold()
                    .foregroundColor(.white)
                Spacer()
                Button(action: onNavigateToProfile) {
                    Circle()
                        .fill(Color.gray)
                        .frame(width: 40, height: 40)
                        .overlay(
                            Circle().stroke(Color.pink, lineWidth: 2)
                        )
                }
            }
            .padding()
            
            ScrollView {
                VStack(spacing: 20) {
                    // Status Card
                    StatusCard(status: repository.status)
                    
                    // Main Vitals
                    HStack(spacing: 15) {
                        MetricCard(title: "Heart Rate", value: "\(repository.heartRate)", unit: "BPM", icon: "heart.fill", color: .red)
                        MetricCard(title: "HRV", value: "\(repository.hrv)", unit: "ms", icon: "waveform.path.ecg", color: .purple)
                    }
                    
                    HStack(spacing: 15) {
                        MetricCard(title: "Stress", value: "\(repository.stressLevel)", unit: "/100", icon: "bolt.fill", color: .yellow)
                        MetricCard(title: "EDA", value: String(format: "%.1f", repository.eda), unit: "µS", icon: "drop.fill", color: .blue)
                    }
                    
                    // Actions
                    Button(action: onNavigateToVitals) {
                        Text("View Detailed Vitals")
                            .font(.headline)
                            .foregroundColor(.white)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color.pink)
                            .cornerRadius(12)
                    }
                    .padding(.top)
                }
                .padding()
            }
            
            Spacer()
        }
        .background(Color.black.opacity(0.9))
    }
}

struct StatusCard: View {
    var status: String
    
    var body: some View {
        HStack {
            Text("Device Status")
                .foregroundColor(.gray)
            Spacer()
            Text(status)
                .bold()
                .foregroundColor(status == "Connected" ? .green : .red)
        }
        .padding()
        .background(Color.gray.opacity(0.2))
        .cornerRadius(12)
    }
}

struct MetricCard: View {
    var title: String
    var value: String
    var unit: String
    var icon: String
    var color: Color
    
    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                Image(systemName: icon)
                    .foregroundColor(color)
                Spacer()
            }
            Spacer()
            Text(value)
                .font(.system(size: 32, weight: .bold))
                .foregroundColor(.white)
            Text(unit)
                .font(.caption)
                .foregroundColor(.gray)
            Text(title)
                .font(.caption)
                .foregroundColor(.gray)
        }
        .padding()
        .frame(height: 120)
        .frame(maxWidth: .infinity)
        .background(Color.gray.opacity(0.2))
        .cornerRadius(16)
    }
}
