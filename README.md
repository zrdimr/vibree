# Vibree - Wearable Data Collector

Vibree is a modern Android and iOS application designed to collect, visualize, and analyze physiological and psychological data from wearable devices and user inputs. It features a mindful, aesthetically pleasing interface with advanced on-device AI for real-time stress detection.

## 🎯 Project Goals

*   **Real-time Monitoring**: Connect to standard BLE Heart Rate monitors to view live data.
*   **Edge AI Inference**: Uses on-device PyTorch Mobile (LibTorch-Lite) to run complex models locally without an internet connection, ensuring absolute privacy.
*   **Multimodal Stress Analysis**:
    *   **Physiological (CNN-LSTM)**: Analyzes Heart Rate Variability (HRV) patterns to calculate physical stress levels.
    *   **Psychological (MobileBERT)**: Features a built-in NLP agent that reads user journal entries ("Tempat Curhat") and identifies mental stress.
*   **Mindful Design**: Focus on calming, glassmorphic UI to reduce user anxiety while monitoring vitals.
*   **Privacy First**: All data is stored locally on the device using Room Database (Android) / CoreData (iOS).

## ✨ Features

*   **Bluetooth Low Energy (BLE)**: Seamless scanning and connection to HR sensors.
*   **Local Social Feed**: Private journaling area where the "Vibree AI" agent analyzes your thoughts for stress.
*   **Live Dashboard**: Interactive heartbeat visualization and status indicators.
*   **Vitals Tracking**: Detailed view of Stress and HRV trends.
*   **Machine Learning Models**:
    *   **CNN+LSTM**: `hrv_model.ptl` for temporal physiological patterns.
    *   **MobileBERT**: `nlp_model.ptl` for natural language understanding.

## 🚀 Getting Started

### Prerequisites

*   **Android**: Android Studio Hedgehog or later, JDK 17.
*   **iOS**: Xcode 15+, CocoaPods (`pod install`), macOS.
*   **Models**: Download the PyTorch Lite models `.ptl` into the respective assets/resources directories before building.

### Installation (Android)

1.  Clone the repository:
    ```bash
    git clone https://github.com/zrdimr/vibree.git
    cd vibree
    ```
2.  Open the `android/` project in Android Studio.
3.  Download ML models (done automatically via GitHub Actions, or manually via `gdown`).
4.  Sync Gradle and run on a physical Android device (min SDK 24).

### Building

To build the Android APK from the command line:

```bash
cd android
./gradlew assembleDebug
```

For release:
```bash
./gradlew assembleRelease
```

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to submit pull requests, report issues, and suggest improvements.

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
