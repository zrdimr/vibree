#!/bin/bash
# Helper script to build the APK locally if Android Studio/Gradle is installed.

echo "Checking for Gradle..."
if ! command -v gradle &> /dev/null; then
    echo "Gradle not found. Please install Gradle or open this project in Android Studio."
    echo "In Android Studio, the Gradle Wrapper (gradlew) will be generated automatically."
    exit 1
fi

echo "Building Debug APK..."
gradle :app:assembleDebug

if [ $? -eq 0 ]; then
    echo "Build Success!"
    echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "Build Failed. Please check the logs above."
fi
