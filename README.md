<h1 align="center">Kilukkam Expense Tracker</h1>

<p align="center">
  <strong>A modern, automated UPI expense tracker for Android that effortlessly categorizes your spending.</strong>
</p>

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png" alt="Kilukkam Logo" width="150" height="150">
</p>

## 🚀 Overview

Kilukkam is a smart, fully-automated Android expense tracking application designed to take the manual work out of budgeting. Built with a beautiful, state-of-the-art **Dark Claymorphism UI**, Kilukkam silently works in the background by listening to bank SMS notifications. Whenever a valid UPI transaction occurs, it instantly pops up a sleek heads-up notification prompting you to categorize the expense.

## ✨ Features

- **Real-Time Automated Tracking**: Instantly detects and parses incoming SMS messages from banks to extract transaction amounts automatically.
- **Advanced Scam & Spam Filtering**: Built-in intelligent RegEx parsing that filters out promotional messages, fake lottery scams, and non-transactional messages to keep your data clean.
- **Instant Categorization**: A frictionless workflow that triggers a High-Priority Android notification the moment you pay, allowing you to seamlessly assign the expense to a category.
- **Dynamic Categories**: Choose from pre-existing categories or dynamically create your own custom categories on the fly.
- **Stunning Modern UI**: Crafted entirely with **Jetpack Compose**, featuring a custom "Claymorphism" design system. The UI utilizes a premium dark theme accented with vibrant `#FF6331` (Orange) and `#372B3B` (Deep Purple).
- **Privacy First & 100% Offline**: Your financial data never leaves your device. Everything is securely persisted locally using SharedPreferences and Kotlin Serialization.

## 🛠 Tech Stack

- **Platform**: Android (Minimum SDK 24, Target SDK 34+)
- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose
- **Architecture**: MVI / MVVM Pattern
- **Data Persistence**: `SharedPreferences` + `kotlinx.serialization`
- **Background Processing**: `BroadcastReceiver` + High Priority Notifications / Full-Screen Intents

## 📥 Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/roi-thhh/Kilukam-Expense-Tracker.git
   ```
2. **Open the project**:
   Open the cloned folder in **Android Studio**.
3. **Build & Run**:
   Sync the Gradle files and hit the **Run** button (`Shift + F10`) to deploy the app to your emulator or physical Android device.
4. **Permissions**:
   Upon first launch, ensure you grant the requested **SMS** and **Notification** permissions so the app can detect incoming transactions.

## 🎨 UI / UX Design

Kilukkam employs a custom-built Jetpack Compose modifier to achieve the 3D, extruded "Claymorphism" look. It utilizes dual inner and outer shadows to give depth to the cards, buttons, and popups, making the interface feel tactile and alive.

## 🛡 Testing

The core transaction parsing engine is fully unit-tested to ensure accurate extraction of currency and aggressive filtering of spam.
To run the tests locally:
```bash
./gradlew test
```

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
