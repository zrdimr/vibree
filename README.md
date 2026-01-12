# Vibree - Wearable Data Collector

Vibree is a modern Android application designed to collect, visualize, and analyze physiological data from wearable devices. Use Vibree to track Heart Rate, Heart Rate Variability (HRV), and Stress levels in real-time with a mindful, aesthetically pleasing interface.

## 🎯 Project Goals

*   **Real-time Monitoring**: Connect to standard BLE Heart Rate monitors to view live data.
*   **Mindful Design**: specific focus on calming, glassmorphic UI to reduce user anxiety while monitoring vitals.
*   **Data Insight**: Calculate Stress levels based on HRV (RMSSD) to give actionable feedback.
*   **Privacy First**: All data is stored locally on the device using Room Database.

## ✨ Features

*   **Bluetooth Low Energy (BLE)**: Seamless scanning and connection to HR sensors.
*   **Live Dashboard**: Interactive heartbeat visualization and status indicators.
*   **Vitals Tracking**: Detailed view of Stress and HRV trends.
*   **History**: Review past recording sessions.
*   **Profile Management**: Personalize your experience securely.

## 🚀 Getting Started

### Prerequisites

*   Android Studio Hedgehog or later.
*   JDK 17.
*   Android Device with BLE support (Emulator support for BLE is limited).

### Installation

1.  Clone the repository:
    ```bash
    git clone https://github.com/zrdimr/vibree.git
    ```
2.  Open the project in Android Studio.
3.  Sync Gradle files.
4.  Run on a physical Android device (min SDK 24).

### Building

To build the APK from command line:

```bash
./gradlew assembleDebug
```

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to submit pull requests, report issues, and suggest improvements.

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔒 Security

For security concerns, please review our [Security Policy](SECURITY.md).

## 💖 Code of Conduct

All contributors are expected to adhere to our [Code of Conduct](CODE_OF_CONDUCT.md).
