# 🛒 Aidukan - Native Android App

**AI-Powered Kirana Store Management with Voice Assistant**

Aidukan is a professional Native Kotlin Android application designed specifically for kirana stores in India. It combines traditional business management features with cutting-edge AI voice assistant technology.

## ✨ Key Features

### 🎤 Voice Assistant (GWTS)
- **Wake Word Activation**: Say "GWTS" or "Hey Aidukan"
- **Natural Language Processing**: Understands Hindi + English mix
- **Voice Commands**:
  - "100 rupaye ka Fortune oil bech diya" → Records sale
  - "Stock check karo Fortune oil" → Shows inventory
  - "Aaj ka total sales batao" → Displays reports
- **Voice Responses**: AI speaks back confirmations
- **Visual Feedback**: Glowing gradient animations (Teal → Violet)
- **Waveform Visualization**: Real-time audio feedback

### 🎨 Modern UI Design
- **Center**: Large voice assistant rectangle with gradient
- **Left Sidebar**: News & AI Suggestions
- **Right Sidebar**: Complete Shop management menu
- **Color Scheme**:
  - Deep Blue (#1A237E) - Trust & stability
  - Electric Violet (#7C4DFF) - Innovation
  - Fresh Teal (#00BFA5) - Growth
  - Futuristic gradients throughout

### 🏪 Complete Shop Management
- **Bills / Invoices** - GST compliant billing
- **Balance Sheet** - Financial overview
- **Expenses** - Track daily expenses
- **Stock / Inventory** - Complete inventory management
- **Reports** - Sales, profit, GST reports
- **Customers / Udhaar** - Credit management
- **Suppliers** - Vendor management
- **Add Product** - Quick product addition
- **Sales Summary** - Daily/weekly/monthly summaries

### 📱 Technical Features
- **100% Native Kotlin** - Professional Android development
- **Material Design 3** - Modern UI components
- **Room Database** - Local data storage
- **CameraX** - Product photo capture
- **Speech Recognition** - Built-in Android speech API
- **Text-to-Speech** - Voice responses
- **MVVM Architecture** - Clean code structure
- **Offline First** - Works without internet

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0+)
- Kotlin 1.9.0+

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/rahulgoswami962006-dotcom/aidukan-android-native.git
cd aidukan-android-native
```

2. **Open in Android Studio**
- File → Open → Select project folder
- Wait for Gradle sync to complete

3. **Run the app**
- Connect Android device or start emulator
- Click Run button (▶️)
- Grant microphone and camera permissions

### Building APK

**Debug APK:**
```bash
./gradlew assembleDebug
```
APK location: `app/build/outputs/apk/debug/app-debug.apk`

**Release APK:**
```bash
./gradlew assembleRelease
```
APK location: `app/build/outputs/apk/release/app-release.apk`

## 📖 Usage Guide

### Voice Commands

**Sales:**
- "100 rupaye ka Fortune oil bech diya"
- "2 packet Maggi 28 rupaye mein becha"
- "Tata salt 1 kg 20 rupaye"

**Inventory:**
- "Stock check karo Fortune oil"
- "Kitna bacha hai Parle-G?"
- "Low stock items dikhao"

**Reports:**
- "Aaj ka total sales batao"
- "Is hafte ka profit dikhao"
- "GST report generate karo"

**Expenses:**
- "500 rupaye electricity bill add karo"
- "Transport expense 200 rupaye"

### UI Navigation

**Left Sidebar:**
- **News**: Local kirana market trends
- **Suggestions**: AI-powered business recommendations

**Center:**
- **Voice Assistant**: Tap or say "GWTS" to activate

**Right Sidebar:**
- **Shop Menu**: Access all business features

## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/aidukan/kirana/
│   │   ├── ui/
│   │   │   └── MainActivity.kt          # Main activity with voice
│   │   ├── utils/
│   │   │   └── VoiceCommandProcessor.kt # NLP logic
│   │   ├── data/
│   │   │   ├── database/                # Room database
│   │   │   ├── models/                  # Data models
│   │   │   └── repository/              # Data repository
│   │   └── viewmodel/                   # ViewModels
│   ├── res/
│   │   ├── layout/                      # XML layouts
│   │   ├── drawable/                    # Graphics & gradients
│   │   ├── values/                      # Colors, strings, themes
│   │   └── mipmap/                      # App icons
│   └── AndroidManifest.xml
├── build.gradle                         # App dependencies
└── proguard-rules.pro                   # ProGuard rules
```

## 🎨 Color Palette

```kotlin
Deep Blue:       #1A237E  // Primary background
Electric Violet: #7C4DFF  // Buttons, highlights
Fresh Teal:      #00BFA5  // Gradients, success
White:           #FFFFFF  // Text, cards
Light Gray:      #F5F5F5  // Backgrounds
```

## 🔧 Configuration

### Permissions Required
- `RECORD_AUDIO` - Voice commands
- `CAMERA` - Product photos
- `INTERNET` - Future cloud sync (optional)

### Minimum Requirements
- Android 7.0 (API 24)
- 50 MB storage
- Microphone (for voice)
- Camera (for photos)

## 📦 Dependencies

```gradle
// Core Android
androidx.core:core-ktx:1.12.0
androidx.appcompat:appcompat:1.6.1
com.google.android.material:material:1.11.0

// Lifecycle
androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0

// Room Database
androidx.room:room-runtime:2.6.1

// Coroutines
kotlinx-coroutines-android:1.7.3

// Charts
MPAndroidChart:v3.1.0

// CameraX
androidx.camera:camera-camera2:1.3.1
```

## 🚀 Deployment

### Google Play Store

1. **Generate signed APK**
   - Build → Generate Signed Bundle/APK
   - Create keystore
   - Build release APK

2. **Create Play Console account** ($25 one-time)

3. **Upload APK**
   - Create app listing
   - Add screenshots
   - Set pricing (Free)
   - Submit for review

### Direct Distribution

Share APK file directly:
```bash
adb install app-release.apk
```

## 💰 Cost Breakdown

**Development:** FREE (Open source)
**Voice Recognition:** FREE (Android built-in)
**Text-to-Speech:** FREE (Android built-in)
**Database:** FREE (Local SQLite)
**Play Store:** $25 one-time (optional)

## 🔮 Roadmap

### Phase 1 (Current)
- ✅ Voice assistant with GWTS wake word
- ✅ Basic shop management
- ✅ Modern UI with gradients
- ✅ Offline functionality

### Phase 2 (Coming Soon)
- 📸 Photo-to-product with AI
- 📊 Advanced analytics
- 🔄 Cloud sync
- 👥 Multi-user support

### Phase 3 (Future)
- 🤖 Advanced AI predictions
- 📍 Local area trends
- 💳 Payment gateway integration
- 📱 WhatsApp Business API

## 🤝 Contributing

Contributions welcome! Please:
1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push to branch
5. Open pull request

## 📄 License

MIT License - Free to use for commercial purposes

## 👨‍💻 Developer

Built with ❤️ by Bhindi AI Team

## 📞 Support

- **Issues**: GitHub Issues
- **Email**: support@aidukan.com
- **Docs**: [docs.aidukan.com](https://docs.aidukan.com)

---

**Transform your kirana store with AI! 🚀**

*Aidukan = Vyapar + AI Voice + AI Vision + Smart Insights*
