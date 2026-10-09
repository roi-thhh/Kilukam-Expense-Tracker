<div align="center">

<img src="app/src/main/res/drawable/kilukkam_logo.png" alt="Kilukkam Logo" width="120" height="120" style="border-radius: 28px;"/>

# Kilukkam • കിലുക്കം
### *Next-Gen Automated UPI & Bank Expense Tracker for Android*

[![Android](https://img.shields.io/badge/Platform-Android_24%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Sunny_Fintech_M3-FFD928?style=for-the-badge&logo=google&logoColor=black)](https://m3.material.io)
[![Release](https://img.shields.io/badge/Version-v4.0.0-FFD928?style=for-the-badge)](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-27865A?style=for-the-badge)](LICENSE)

<p align="center">
  <strong>Effortless, intelligent expense tracking with zero manual entry, real-time banking SMS detection, multi-account card management, goal vaults, and a bespoke Sunny Fintech warm cream & yellow UI.</strong>
</p>

[Download Latest APK (v4.0.0)](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v4.0.0) • [Features](#-key-features) • [Architecture](#-architecture) • [Getting Started](#-getting-started)

---

</div>

## 🌟 Overview

**Kilukkam** is a modern, privacy-first personal finance application engineered for the fast-paced UPI and digital banking era. Rather than forcing you to open the app and manually record every coffee, grocery run, or fuel receipt, Kilukkam operates silently in the background:

1. **Detects Transactions Instantly**: Intercepts official debit and credit SMS alerts from major Indian banks (HDFC, SBI, ICICI, Axis, Paytm, GPay, PhonePe, and more).
2. **Intelligent Merchant & Account Parsing**: Automatically extracts the exact amount, merchant identity (Swiggy, Uber, Zomato, Amazon, etc.), and account / card number (`A/c ••4521`).
3. **Smart Habit Learning**: Remembers which categories you pair with merchants, pre-selecting them automatically next time.
4. **Target Vaults & Category Caps**: Direct savings into dedicated goal vaults and enforce category-by-category spending limits with live visual health indicators.
5. **100% Offline & Private**: Zero cloud sync, zero telemetry, zero analytics tracking. Your sensitive financial data remains strictly encrypted on your device.

---

## ✨ Key Features

| Feature | Description |
| :--- | :--- |
| ☀️ **Sunny Fintech Aesthetic** | Radiant, friendly financial design with warm cream backgrounds (`#FFFDF6`), signature sunny yellow surfaces (`#FFD928`), crisp white cards, and high-contrast bold typography (`#171717`). |
| 👑 **Signature Yellow Hero Panel** | High-energy primary balance card displaying live net balance, growth percentages, and fast rounded pill action triggers (*Send, Receive, Budgets, Savings*). |
| 🧠 **Intelligent Merchant Extraction** | Heuristic parsing for 30+ merchants, UPI VPA cleaning, and intelligent habit learning that pre-selects categories based on past interactions. |
| 🏦 **Multi-Account & Credit Card Hub** | Auto-detects accounts (HDFC, SBI, ICICI, Axis, etc.), differentiates Bank Accounts vs Credit Cards, and provides horizontal account filter chips. |
| 🎯 **Target Vaults & Goal Tracking** | Create custom savings vaults (e.g. *Emergency Fund*, *New Mac*) with visual progress bars and one-tap quick deposits. |
| 📊 **Category Budgets & Live Warnings** | Set custom monthly limits per category with dynamic progress meters and status badges (*Healthy*, *Warning*, *Exceeded*). |
| ⚡ **Zero-Delay Transaction Detection** | Real-time `BroadcastReceiver` parses incoming banking SMS with sub-second latency and prompts instant categorization. |
| 📈 **Interactive Spending Flow** | Switch seamlessly between **1-Week**, **1-Month**, and **1-Year** dynamic trendlines with reactive bezier fills and spending velocity metrics. |
| 🍩 **Sunny Donut Analytics** | Crisp segmented donut chart with centered financial summary and category breakdown meters. |
| 📤 **One-Tap Share Analytics** | Generates a sleek branded expense card directly to WhatsApp, Telegram, and social platforms. |

---

## 🎨 Design Philosophy: Sunny Fintech

Kilukkam has transitioned from dark aesthetics to **Sunny Fintech** — a visual language built around optimism, tactile clarity, and financial transparency:

- **Warm Cream Canvas (`#FFFDF6` & `#F4F2EB`)**: Welcoming, paper-inspired foundation that feels natural, soft on the eyes, and editorial.
- **Signature Sunny Yellow (`#FFD928`)**: Used deliberately as a bold surface, carrying the Hero Card, primary triggers, and active navigation badges.
- **High-Contrast Bold Typography (`#171717`)**: Deep charcoal lettering with tight headline kerning and tabular numerals ritualizes financial balances as authoritative values.
- **Semantic Financial Accents**: Refined emerald green (`#27865A`) for income and savings, warm coral (`#F06C4F`) for expenses, and pastel jewel badges for categories.
- **Tactile Elevation & Soft Shadows**: Replaced flat glassmorphism with 24–30dp rounded corners, hairline borders (`1.dp #E9E5DA`), and gentle organic drop shadows.

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
| **v4.0.0** | `4.0.0` (Build 5) | **Latest Stable (Sunny Fintech)** | [Download APK](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v4.0.0) |
| **v3.1.0** | `3.1.0` (Build 4) | Stable | [View Release](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v3.1.0) |
| **v3.0.0** | `3.0.0` (Build 3) | Deprecated | [View Release](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v3.0.0) |
| **v2.0.0** | `2.0.0` (Build 2) | Deprecated | [View Release](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v2.0.0) |

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) — free to use, modify, and distribute.

---

<div align="center">
  <sub>Built with ❤️ using Kotlin & Jetpack Compose</sub>
</div>
