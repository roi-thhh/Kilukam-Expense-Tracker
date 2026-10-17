<div align="center">

<img src="app/src/main/res/drawable/kilukkam_logo.png" alt="Kilukkam Logo" width="120" height="120" style="border-radius: 28px;"/>

# Kilukkam • കിലുക്കം
### *Next-Gen Automated UPI & Bank Expense Tracker for Android*

[![Android](https://img.shields.io/badge/Platform-Android_24%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Design-Sunny_Fintech_M3-FFD928?style=for-the-badge&logo=google&logoColor=black)](https://m3.material.io)
[![Release](https://img.shields.io/badge/Version-v4.3.0-FFD928?style=for-the-badge)](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases)
[![Security: Semgrep](https://img.shields.io/badge/Security-Semgrep_Verified-00C48C?style=for-the-badge&logo=semgrep&logoColor=white)](https://semgrep.dev)
[![License: MIT](https://img.shields.io/badge/License-MIT-27865A?style=for-the-badge)](LICENSE)

<p align="center">
  <strong>Effortless, intelligent expense tracking with zero manual entry, real-time banking SMS detection, multi-account card management, goal vaults, in-place expense editing & deletion, categorized analytics graph sharing, and bespoke Sunny Fintech UI.</strong>
</p>

[Download Latest APK (v4.3.0)](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v4.3.0) • [Key Features](#-key-features) • [Expense Editing & Deletion](#-in-place-expense-editing--deletion) • [Architecture](#-architecture--tech-stack) • [Security & Audit](#-security--production-readiness) • [Releases](#-releases)

---

</div>

## 🌟 Overview

**Kilukkam** is a modern, privacy-first personal finance application engineered for the fast-paced UPI and digital banking era. Rather than forcing you to open the app and manually record every coffee, grocery run, or fuel receipt, Kilukkam operates silently in the background:

1. **Detects Transactions Instantly**: Intercepts official debit and credit SMS alerts from major Indian banks (HDFC, SBI, ICICI, Axis, Paytm, GPay, PhonePe, and more).
2. **Intelligent Merchant & Account Parsing**: Automatically extracts the exact amount, merchant identity (Swiggy, Uber, Zomato, Amazon, etc.), and account / card number (`A/c ••4521`).
3. **Smart Habit Learning**: Remembers which categories you pair with merchants, pre-selecting them automatically next time.
4. **Target Vaults & Category Caps**: Direct savings into dedicated goal vaults and enforce category-by-category spending limits with live visual health indicators.
5. **In-Place Control**: Long-press or tap any transaction to adjust mistakes, tweak tags/notes, switch accounts, or permanently delete individual records.
6. **100% Offline & Private**: Zero cloud sync, zero telemetry, zero analytics tracking. Your sensitive financial data remains strictly on your device.

---

## ✨ Key Features

| Feature | Description |
| :--- | :--- |
| ☀️ **Sunny Fintech Aesthetic** | Radiant, friendly financial design with warm cream backgrounds (`#FFFDF6`), signature sunny yellow surfaces (`#FFD928`), crisp white cards, and high-contrast bold typography (`#171717`). |
| 👑 **Signature Yellow Hero Panel** | High-energy primary balance card displaying live net balance, growth percentages, and fast rounded pill action triggers (*Send, Receive, Budgets, Savings*). |
| ✏️ **In-Place Expense Editing & Deletion** | Long-press or tap any expense in the Recent Activity list to modify amounts, adjust or clear merchant tags, switch accounts/cards, or permanently remove mistyped transactions with real-time balance recalibration. |
| 🧠 **Intelligent Merchant Extraction** | Heuristic parsing for 30+ merchants, UPI VPA cleaning, and intelligent habit learning that pre-selects categories based on past interactions. |
| 🏦 **Multi-Account & Credit Card Hub** | Auto-detects accounts (HDFC, SBI, ICICI, Axis, etc.), differentiates Bank Accounts vs Credit Cards, and provides horizontal account filter chips. |
| 🎯 **Target Vaults & Goal Tracking** | Create custom savings vaults (e.g. *Emergency Fund*, *New Mac*) with visual progress bars and one-tap quick deposits. |
| 📊 **Category Limits & Full Management** | Set custom monthly limits per category with live health meters (*Healthy*, *Warning*, *Exceeded*), with the freedom to add custom categories and delete unwanted ones. |
| ⚡ **Zero-Delay Transaction Detection** | Real-time `BroadcastReceiver` parses incoming banking SMS with sub-second latency and prompts instant categorization. |
| 📈 **Interactive Spending Flow** | Switch seamlessly between **1-Week**, **1-Month**, and **1-Year** dynamic trendlines with reactive bezier fills and spending velocity metrics. |
| 🍩 **Sunny Donut Analytics** | Crisp segmented donut chart with centered financial summary and category breakdown meters. |
| 📤 **Categorized Graph Sharing** | Renders an authentic high-resolution segmented donut chart, category breakdown list, and key metrics into a branded Sunny Fintech report card shareable to WhatsApp, Telegram, and social media. |

---

## ✏️ In-Place Expense Editing & Deletion

We built full in-place management into the **Recent Activity** list so manual errors can be corrected effortlessly:

- **Gesture Discovery**: Tap or long-press on any transaction in the activity list. A subtle `Hold to edit` indicator reminds users this capability is always available.
- **Action Sheet**: A modal shows a formatted preview of the expense (Amount, Merchant/Tag, Category, Account, Date) with two clear options:
  - ✏️ **Edit Details**: Opens the full pre-filled editor.
  - 🗑️ **Delete Expense**: Prompts confirmation to delete immediately.
- **Complete In-Place Editor**: Pre-populates all existing data:
  - **Amount (₹)**: Fix mistyped numbers.
  - **Merchant / Tag / Note**: Add or clear specific store details.
  - **Account / Card**: Switch between Bank, Credit Card, or Cash Wallet.
  - **Category**: Select from existing categories or tap `+` to add a new custom category.
- **Real-Time Recalculation**: Saving changes or deleting immediately recalculates your total net balance, monthly category spending meters, and analytics donut charts.

---

## 🎨 Design Philosophy: Sunny Fintech

Kilukkam features the custom **Sunny Fintech** design system — engineered for optimism, tactile clarity, and financial transparency:

- **Warm Cream Canvas (`#FFFDF6` & `#F4F2EB`)**: Welcoming, paper-inspired foundation that feels natural, soft on the eyes, and editorial.
- **Signature Sunny Yellow (`#FFD928`)**: Used deliberately as a bold surface, carrying the Hero Card, primary triggers, and active navigation badges.
- **High-Contrast Bold Typography (`#171717`)**: Deep charcoal lettering with tight headline kerning and tabular numerals ritualizes financial balances as authoritative values.
- **Semantic Financial Accents**: Refined emerald green (`#27865A`) for income and savings, warm coral (`#F06C4F`) for expenses, and pastel jewel badges for categories.
- **Tactile Elevation & Soft Shadows**: 24–30dp rounded corners, hairline borders (`1.dp #E9E5DA`), and gentle organic drop shadows (`sunnyCardShadow`).

---

## 🏗 Architecture & Tech Stack

Kilukkam adheres to modern Android Clean Architecture and unidirectional data flow (MVI/MVVM):

```
app/src/main/java/com/example/kilukkam/
├── MainActivity.kt             # Application entry point & permission orchestrator
├── SmsReceiver.kt              # BroadcastReceiver with lazy notification channel setup
├── TransactionParser.kt        # 100% offline regex & merchant heuristic parser
├── data/                       # Data models & SharedPreferences repository
│   └── DataRepository.kt       # Persistence for expenses, accounts, budgets & vaults
├── theme/                      # Sunny Fintech design tokens & Material 3 setup
│   ├── Color.kt
│   └── Theme.kt
├── ui/                         # Jetpack Compose UI screens, dialogs & components
│   ├── ModifierExt.kt          # Custom soft shadow modifier (sunnyCardShadow)
│   ├── OnboardingScreen.kt     # First-launch personalization
│   ├── SplashScreen.kt         # Fast branded logo intro
│   └── main/
│       ├── AnalyticsScreen.kt      # Donut spending chart & category breakdown
│       ├── EditExpenseDialog.kt    # In-place transaction editor, action sheet & delete flow
│       ├── MainAppScaffold.kt      # Root scaffold with bottom navigation bar & FAB
│       ├── MainScreen.kt           # Hero balance, quick actions, spending flow & activity list
│       ├── MainScreenViewModel.kt  # State holder with StateFlow & reactive reload
│       ├── ManualEntryDialog.kt    # Manual income & expense entry modal
│       └── SettingsScreen.kt       # Profile, budget goals & data management
└── utils/                      # Share sheet helper & high-resolution canvas renderer
    └── ShareUtils.kt
```

### 🛠 Core Stack & Dependencies

- **Language**: Kotlin 2.0 (JVM Toolchain 17)
- **UI Toolkit**: Jetpack Compose (BOM 2024.09.00) + Material 3
- **Architecture**: Modern Android MVVM/MVI with `StateFlow` and `collectAsStateWithLifecycle`
- **Serialization**: `kotlinx.serialization` (JSON)
- **Local Persistence**: Android `SharedPreferences` (Private Mode, 100% offline)
- **Static Analysis**: Semgrep CLI v1.180 (OWASP Top 10, Secrets, Security Audit)
- **Build System**: Gradle 9.1 with Configuration Cache enabled

---

## 🛡️ Security & Production Readiness

Kilukkam is designed and audited for production deployment:

- **100% Offline Guarantee**: No network permissions are requested in `AndroidManifest.xml`. No data ever leaves your device.
- **System Broadcast Protection**: `SmsReceiver` enforces `android:permission="android.permission.BROADCAST_SMS"`, ensuring only the verified Android telephony stack can invoke it.
- **Notification Channel Resilience**: `SmsReceiver` guarantees lazy initialization of `TRANSACTION_CHANNEL` on Android 8.0+ (Oreo), preventing missed heads-up categorization notifications even when the app was closed.
- **Safe File Sharing**: `FileProvider` is configured with `android:exported="false"` and temporary URI grants (`grantUriPermissions="true"`) for exporting analytics report cards.
- **Automated Semgrep Audit**: Audited against 312+ static analysis rules covering OWASP Mobile Top 10, credentials & secret leaks, and security audits with **0 findings / 0 vulnerabilities**.

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / Koala or newer
- JDK 17
- Android SDK 34+
- Physical Android device or emulator running Android 7.0 (API 24) or higher

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

3. **Build the Production Release APK**:
   ```bash
   ./gradlew assembleRelease
   ```
   Output: `app/build/outputs/apk/release/app-release.apk`

4. **Install onto Device via ADB**:
   ```bash
   adb install -r app/build/outputs/apk/release/app-release.apk
   ```

---

## 🔒 Permissions

Kilukkam requests the following permissions purely for on-device processing:

- `RECEIVE_SMS` & `READ_SMS`: Required exclusively to intercept incoming transactional SMS messages from your financial institutions. **Messages are never uploaded or stored off-device.**
- `POST_NOTIFICATIONS`: Required on Android 13+ to display instant heads-up categorize banners when transactions occur.

---

## 📦 Releases

| Release | Version | Status | Download |
| :--- | :--- | :--- | :--- |
| **v4.3.0** | `4.3.0` (Build 7) | **Latest Stable (Sunny Fintech)** | [Download APK](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v4.3.0) |
| **v4.2.0** | `4.2.0` (Build 6) | Stable | [View Release](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v4.2.0) |
| **v4.0.0** | `4.0.0` (Build 5) | Stable | [View Release](https://github.com/roi-thhh/Kilukam-Expense-Tracker/releases/tag/v4.0.0) |
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
