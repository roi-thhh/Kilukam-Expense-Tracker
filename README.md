<div align="center">

<img src="app/src/main/res/drawable/kilukkam_logo.png" alt="Kilukkam Logo" width="120" height="120" style="border-radius: 28px;"/>

# Kilukkam • കിലുക്കം
### *Next-Gen Automated UPI & Bank Expense Tracker for Android*

[![Android](https://img.shields.io/badge/Platform-Android_24%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Material_3_Dark-C8FF24?style=for-the-badge&logo=google&logoColor=black)](https://m3.material.io)
[![Release](https://img.shields.io/badge/Version-v3.0.0-00E5FF?style=for-the-badge)](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-FF4081?style=for-the-badge)](LICENSE)

<p align="center">
  <strong>Effortless, intelligent expense tracking with zero manual entry, real-time banking SMS detection, and a bespoke Neon Aurora dark fintech UI.</strong>
</p>

[Download Latest APK (v3.0.0)](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v3.0.0) • [Features](#-key-features) • [Architecture](#-architecture) • [Getting Started](#-getting-started)

---

</div>

## 🌟 Overview

**Kilukkam** is a modern, privacy-first personal finance application engineered for the fast-paced UPI and digital banking era. Rather than forcing you to open the app and manually record every coffee, grocery run, or fuel receipt, Kilukkam operates silently in the background:

1. **Detects Transactions Instantly**: Intercepts official debit and credit SMS alerts from major Indian banks (HDFC, SBI, ICICI, Axis, Paytm, GPay, PhonePe, and more).
2. **Intelligent RegEx Engine**: Automatically extracts the exact amount, merchant, and transaction type while screening out promotional spam, phishing, and fake lottery alerts.
3. **Zero-Delay Heads-Up Notification**: Triggers an instant categorization prompt the moment you pay. Tap once to assign a category—done in under 2 seconds.
4. **100% Offline & Private**: Zero cloud sync, zero telemetry, zero analytics tracking. Your sensitive financial data remains strictly encrypted on your device.

---

## ✨ Key Features

| Feature | Description |
| :--- | :--- |
| ⚡ **Zero-Delay Transaction Detection** | Real-time `BroadcastReceiver` parses incoming banking SMS with sub-second latency and prompts instant categorization. |
| 🎨 **Neon Aurora Dark UI** | Premium fintech aesthetic with deep obsidian surfaces (`#080909`), high-voltage Neon Lime accents (`#C8FF24`), and dual-layer radial glassmorphism. |
| 💰 **Savings Vault & Quick Deposit** | Dedicated Savings tracking starting at ₹0.00 with fast top-up chips (`+₹500`, `+₹1000`, `+₹2000`, `+₹5000`). |
| 📈 **Interactive Spending Flow** | Switch seamlessly between **1-Week**, **1-Month**, and **1-Year** dynamic trendlines with reactive bezier fills and spending velocity metrics. |
| 🍩 **Neon Donut Analytics** | High-contrast animated segmented donut chart offering intuitive category-by-category expense distribution. |
| 🏷️ **Themed Category Badges** | Jewel-toned glassmorphic squircle badges paired semantically with categories (Dining, Transit, Shopping, Tech, Bills, Health). |
| 📤 **One-Tap Share Analytics** | Generates a sleek branded expense card directly to WhatsApp, Telegram, and social platforms. |
| 🎯 **Monthly Budget Goal** | Set custom monthly limits with live progress bar and remaining balance tracking. |

---

## 🎨 Design Philosophy: Neon Aurora

Kilukkam was designed to provide an interface that feels tactile, luxurious, and modern:

- **Obsidian Dark Foundation**: `#080909` pitch-black canvas reduces OLED battery consumption and minimizes visual fatigue.
- **Brand Neon Lime (`#C8FF24`)**: High-energy primary accent symbolizing growth, net balance, and actionable triggers.
- **Electric Cyan (`#00E5FF`)**: Signifies income, incoming transfers, and communicative elements.
- **Vibrant Accent Palette**: Warm Amber (`#FF9100`) for dining, Radiant Magenta (`#FF4081`) for shopping, and Electric Violet (`#7C4DFF`) for recurring bills.
- **Dual-Layer Glassmorphism**: Cards feature subtle 1px border glows and layered elevation that respond dynamically to interaction.

---

## 🏗 Architecture & Tech Stack

Kilukkam adheres to modern Android development best practices and Clean Architecture principles:

```
app/src/main/java/com/example/kilukkam/
├── data/               # Repository pattern, SharedPreferences storage, data models
│   └── DataRepository.kt
├── receiver/           # BroadcastReceiver for real-time banking SMS detection
│   └── SmsReceiver.kt
├── theme/              # Color tokens, Typography, Material 3 Theme setup
│   ├── Color.kt
│   └── Theme.kt
├── ui/                 # Jetpack Compose UI screens, dialogs, and components
│   ├── main/
│   │   ├── MainAppScaffold.kt    # Root scaffold with bottom navigation
│   │   ├── MainScreen.kt         # Hero balance, quick actions, spending graph, expense list
│   │   ├── AnalyticsScreen.kt    # Donut chart & category breakdown
│   │   ├── SettingsScreen.kt     # Profile, budget goals, data management
│   │   ├── ManualEntryDialog.kt  # Quick income/expense entry modal
│   │   └── MainScreenViewModel.kt# MVI state holder with StateFlow
│   ├── SplashScreen.kt          # Fast branded logo intro
│   └── OnboardingScreen.kt      # First-launch personalization
└── utils/              # Share sheet helper & file provider integrations
    └── ShareUtils.kt
```

### 🛠 Dependencies

- **Language**: Kotlin 2.0
- **UI Toolkit**: Jetpack Compose (BOM 2024.09.00) + Material 3
- **State Management**: Kotlin Coroutines + `StateFlow` + `collectAsStateWithLifecycle`
- **Serialization**: `kotlinx.serialization` (JSON)
- **Local Persistence**: Android `SharedPreferences` (Private Mode)
- **Tooling**: Gradle 9.1 with Configuration Cache enabled

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / Koala or newer
- JDK 17
- Android SDK 34+
- A physical Android device or emulator running Android 7.0 (API 24) or higher

### Building from Source

1. **Clone the repository**:
   ```bash
   git clone https://github.com/roi-thhh/Kilukam-Expense-Tracker.git
   cd Kilukam-Expense-Tracker
   ```

2. **Build the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   Output: `app/build/outputs/apk/debug/app-debug.apk`

3. **Build the Release APK**:
   ```bash
   ./gradlew assembleRelease
   ```
   Output: `app/build/outputs/apk/release/app-release.apk`

4. **Install onto Device via ADB**:
   ```bash
   adb install -r app/build/outputs/apk/release/app-release.apk
   ```

---

## 🔒 Permissions & Privacy

Kilukkam requests the following permissions purely for on-device processing:

- `RECEIVE_SMS` & `READ_SMS`: Required exclusively to intercept incoming transactional SMS messages from your financial institutions. **Messages are never uploaded or stored off-device.**
- `POST_NOTIFICATIONS`: Required on Android 13+ to display instant heads-up categorize banners when transactions occur.

---

## 📦 Releases

| Release | Version | Status | Download |
| :--- | :--- | :--- | :--- |
| **v3.0.0** | `3.0.0` (Build 3) | **Latest Stable** | [Download APK](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v3.0.0) |
| **v2.0.0** | `2.0.0` (Build 2) | Deprecated | [View Release](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v2.0.0) |

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) — free to use, modify, and distribute.

---

<div align="center">
  <sub>Built with ❤️ using Kotlin & Jetpack Compose</sub>
</div>
