# Vibree 🌿

<p align="center">
  <img src="https://img.shields.io/badge/Platform-iOS%20%7C%20Android-green.svg" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Swift%20%7C%20Kotlin-orange.svg" alt="Language" />
  <img src="https://img.shields.io/badge/AI-LibTorch%20%7C%20PyTorch-red.svg" alt="AI Framework" />
  <img src="https://img.shields.io/badge/v1.0.0-Release-blue.svg" alt="Version 1.0.0" />
</p>

Vibree is an advanced, cross-platform wearable data collection and stress-analysis application. Designed around privacy-first edge intelligence, Vibree allows users to monitor their physiological metrics and perform cutting-edge Natural Language Processing (NLP) stress analysis directly on their device without relying on external cloud processing.

## 🔥 Key Features

- **Physiological Monitoring**: Real-time integration with wearable sensors to track metrics like Heart Rate Variability (HRV), Blood Volume Pulse (BVP), and Electrodermal Activity (EDA).
- **On-Device Edge AI**: Utilizes heavily optimized `MobileBERT` via `LibTorch-Lite` to perform sub-second text-based stress inference strictly on-device.
- **Cross-Platform Extraction**: Deployed natively to both iOS and Android platforms, ensuring high-performance execution of complex Deep Learning pipelines.
- **Social Feed**: A robust, core-data/room driven localized diary and journaling platform evaluated instantly by the offline NLP analysis module.

## 🏗️ Architecture

Vibree's inference engine operates fundamentally offline, loading custom serialized PyTorch Script modules (`.ptl`) into standard inference pipelines:

1. **HRV Pipeline**: Processes wearable signals via quantized Multi-Layer Perceptrons.
2. **NLP Pipeline**: `MobileBERT-LSTM-CRF` architecture evaluating user input sequences through custom WordPiece tokenization matching standard BERT vocabularies.

## 🚀 Getting Started

### Prerequisites
- **iOS**: macOS Sequoia (15+), Xcode 16.2+, CocoaPods, Homebrew (`brew install xcodegen`). 
- **Android**: JDK 17, Android Studio Giraffe+ (or Gradle 8.5 command line equivalent).
- **ML Models**: The PyTorch model weights are managed securely using Google Drive and initialized automatically during CI/CD.

### Building iOS (Simulator & Device)
```bash
git clone https://github.com/zrdimr/vibree.git
cd vibree/ios/Vibree
xcodegen generate
pod install
```
Open `Vibree.xcworkspace`, verify signing identities, and run on any M-series Apple Silicon simulator via integrated Rosetta abstractions.

### Building Android
```bash
cd vibree/android
./gradlew :app:assembleDebug
```

## 🔄 CI/CD Pipelines
Vibree leverages powerful GitHub action runners to perform rigorous build steps and automated binary compilations:
- Automates `.ipa` generation for sideload-ready generic unsigned testing.
- Automates standalone `app-release.apk` compilations.
- Injects `.ptl` deep-learning binaries directly into `Assets/` / `Resources/` boundaries at initialization.

## 📄 License
Copyright (c) 2026. All Rights Reserved. Not explicitly licensed for open distribution without author consent.
