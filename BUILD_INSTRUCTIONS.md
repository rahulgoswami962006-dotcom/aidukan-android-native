# 🔨 Build Instructions for Aidukan Android App

## 📋 Prerequisites

### Required Software
1. **Android Studio** (Latest version)
   - Download: https://developer.android.com/studio
   - Minimum: Arctic Fox (2020.3.1) or later

2. **Java Development Kit (JDK)**
   - JDK 17 or later
   - Usually bundled with Android Studio

3. **Android SDK**
   - API Level 24 (Android 7.0) minimum
   - API Level 34 (Android 14) target
   - Installed via Android Studio SDK Manager

### Optional
- Physical Android device (recommended for testing voice)
- Android Emulator (for testing without device)

---

## 🚀 Quick Start (5 Minutes)

### Method 1: Android Studio (Recommended)

1. **Clone Repository**
```bash
git clone https://github.com/rahulgoswami962006-dotcom/aidukan-android-native.git
cd aidukan-android-native
```

2. **Open in Android Studio**
   - Launch Android Studio
   - File → Open
   - Select `aidukan-android-native` folder
   - Wait for Gradle sync (2-3 minutes)

3. **Run on Device/Emulator**
   - Connect Android phone via USB (enable USB debugging)
   - OR start Android Emulator
   - Click Run button (▶️) or press Shift+F10
   - Select device
   - App installs and launches automatically

### Method 2: Command Line

```bash
# Clone repository
git clone https://github.com/rahulgoswami962006-dotcom/aidukan-android-native.git
cd aidukan-android-native

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or build and install in one command
./gradlew installDebug
```

---

## 📦 Building APK Files

### Debug APK (For Testing)

**Using Android Studio:**
1. Build → Build Bundle(s) / APK(s) → Build APK(s)
2. Wait for build to complete
3. Click "locate" in notification
4. APK location: `app/build/outputs/apk/debug/app-debug.apk`

**Using Command Line:**
```bash
./gradlew assembleDebug
```

**Output:** `app/build/outputs/apk/debug/app-debug.apk`

### Release APK (For Distribution)

**Step 1: Create Keystore (First time only)**
```bash
keytool -genkey -v -keystore aidukan-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias aidukan-key
```

**Step 2: Configure Signing**

Create `keystore.properties` in project root:
```properties
storePassword=YOUR_KEYSTORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=aidukan-key
storeFile=../aidukan-release-key.jks
```

**Step 3: Build Release APK**
```bash
./gradlew assembleRelease
```

**Output:** `app/build/outputs/apk/release/app-release.apk`

---

## 🔧 Troubleshooting

### Gradle Sync Failed

**Problem:** Gradle sync fails with dependency errors

**Solution:**
```bash
# Clean project
./gradlew clean

# Invalidate caches (Android Studio)
File → Invalidate Caches / Restart

# Update Gradle wrapper
./gradlew wrapper --gradle-version=8.1
```

### Build Failed - SDK Not Found

**Problem:** Android SDK not found

**Solution:**
1. Open Android Studio
2. Tools → SDK Manager
3. Install:
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android SDK Platform-Tools

### App Crashes on Launch

**Problem:** App crashes immediately after launch

**Solution:**
1. Check logcat in Android Studio
2. Grant permissions manually:
   - Settings → Apps → Aidukan → Permissions
   - Enable Microphone and Camera
3. Ensure Android version is 7.0+

### Voice Recognition Not Working

**Problem:** Voice commands don't work

**Solution:**
1. Check microphone permission granted
2. Test device microphone in other apps
3. Ensure Google app is installed (for speech recognition)
4. Check internet connection (first-time setup)

---

## 📱 Testing on Physical Device

### Enable Developer Options

1. **Settings → About Phone**
2. Tap "Build Number" 7 times
3. Go back to Settings
4. **Developer Options** now visible

### Enable USB Debugging

1. Settings → Developer Options
2. Enable "USB Debugging"
3. Connect phone to computer
4. Accept "Allow USB Debugging" prompt

### Install APK Manually

```bash
# Using ADB
adb install app/build/outputs/apk/debug/app-debug.apk

# Or drag APK to phone and install
```

---

## 🌐 Building for Different Architectures

### Build for Specific CPU Architecture

```bash
# ARM 64-bit (most modern phones)
./gradlew assembleDebug -Pandroid.injected.build.abi=arm64-v8a

# ARM 32-bit (older phones)
./gradlew assembleDebug -Pandroid.injected.build.abi=armeabi-v7a

# x86 (emulators)
./gradlew assembleDebug -Pandroid.injected.build.abi=x86
```

### Build Universal APK (All Architectures)

```bash
./gradlew assembleDebug
# Default builds universal APK
```

---

## 📊 Build Variants

### Available Build Types

1. **Debug**
   - Debuggable
   - No optimization
   - Larger APK size
   - Fast build time

2. **Release**
   - Optimized
   - Minified (R8)
   - Smaller APK size
   - Requires signing

### Switch Build Variant (Android Studio)

1. View → Tool Windows → Build Variants
2. Select variant:
   - `debug`
   - `release`

---

## 🎯 Build Optimization

### Reduce APK Size

Add to `app/build.gradle`:
```gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt')
        }
    }
}
```

### Enable R8 Full Mode

Already enabled in `gradle.properties`:
```properties
android.enableR8.fullMode=true
```

---

## 📈 Build Performance Tips

### Speed Up Builds

1. **Enable Gradle Daemon**
   - Already enabled in `gradle.properties`

2. **Increase Heap Size**
   ```properties
   org.gradle.jvmargs=-Xmx4096m
   ```

3. **Enable Parallel Execution**
   ```properties
   org.gradle.parallel=true
   ```

4. **Use Build Cache**
   ```properties
   org.gradle.caching=true
   ```

---

## 🚢 Publishing to Play Store

### Step 1: Prepare Release Build

1. Build signed release APK (see above)
2. Test thoroughly on multiple devices
3. Prepare store listing assets:
   - App icon (512x512 PNG)
   - Feature graphic (1024x500 PNG)
   - Screenshots (at least 2)
   - App description

### Step 2: Create Play Console Account

1. Go to https://play.google.com/console
2. Pay $25 one-time registration fee
3. Complete account setup

### Step 3: Create App Listing

1. Create new app
2. Fill in details:
   - App name: Aidukan
   - Category: Business
   - Content rating: Everyone
3. Upload APK
4. Set pricing: Free
5. Submit for review

### Step 4: Wait for Approval

- Review time: 1-7 days
- Check for policy violations
- Respond to review feedback if needed

---

## 🔐 Security Best Practices

### Keystore Management

1. **Never commit keystore to Git**
   - Already in `.gitignore`

2. **Backup keystore securely**
   - Store in password manager
   - Keep offline backup

3. **Use strong passwords**
   - Minimum 12 characters
   - Mix of letters, numbers, symbols

### ProGuard Rules

Keep sensitive code obfuscated:
```proguard
-keep class com.aidukan.kirana.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
```

---

## 📞 Need Help?

- **Build Issues**: Open GitHub Issue
- **Documentation**: Check README.md
- **Community**: Join Discord (coming soon)

---

**Happy Building! 🚀**

*Built with ❤️ by Bhindi AI Team*
