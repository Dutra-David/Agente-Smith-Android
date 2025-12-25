# AGENTE SMITH - ANDROID STUDIO DEPLOYMENT GUIDE

## COMPLETE DEPLOYMENT INSTRUCTIONS

---

## SECTION 1: ENVIRONMENT SETUP

### 1.1 System Requirements

**Minimum Requirements:**
- Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)
- 8 GB RAM (16 GB recommended)
- 10 GB free disk space
- Intel Core i5 or equivalent

**Software Requirements:**
- Android Studio Arctic Fox (2020.3.1) or Dolphin (2021.3.1)
- Java Development Kit (JDK) 11 or higher
- Gradle 6.7 or higher
- Kotlin 1.5.0 or higher
- Android SDK (API 21+)

### 1.2 Installation Steps

#### Step 1: Install Android Studio
1. Download from https://developer.android.com/studio
2. Run installer and follow prompts
3. Select "Standard" installation
4. Choose Android SDK location (default: C:\Users\[User]\AppData\Local\Android\Sdk)
5. Complete installation

#### Step 2: Install Android SDK
1. Open Android Studio
2. Go to File > Settings > Appearance & Behavior > System Settings > Android SDK
3. Under SDK Platforms, select:
   - Android 12 (API 31)
   - Android 11 (API 30)
   - Android 10 (API 29)
4. Under SDK Tools, select:
   - Android SDK Build Tools 31.0.0
   - Android Emulator
   - Android SDK Platform Tools
   - Google USB Driver (Windows only)
5. Click Apply and wait for download

#### Step 3: Configure Java JDK
1. Go to File > Settings > Build, Execution, Deployment > Build Tools > Gradle
2. Set Gradle JDK to version 11+
3. Click Apply

---

## SECTION 2: PROJECT SETUP

### 2.1 Clone Repository

```bash
# Open terminal/command prompt
cd C:\Users\[User]\Projects  # or your preferred location

# Clone repository
git clone https://github.com/Dutra-David/Agente-Smith-Android.git
cd Agente-Smith-Android

# Verify repository structure
git branch -a
git status
```

### 2.2 Open Project in Android Studio

1. Open Android Studio
2. Click File > Open
3. Navigate to Agente-Smith-Android directory
4. Click OK
5. Wait for Gradle sync to complete (5-10 minutes on first load)
6. If prompted, update Gradle: Click "Update"

### 2.3 Verify Project Structure

Ensure the following directories exist:
```
Agente-Smith-Android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/dutra/agente/
│   │   │   │   ├── domain/
│   │   │   │   ├── data/
│   │   │   │   └── presentation/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   └── androidTest/
│   ├── build.gradle
│   └── proguard-rules.pro
├── gradle/
├── build.gradle
├── gradle.properties
├── settings.gradle
└── README.md
```

---

## SECTION 3: BUILD PROCESS

### 3.1 Clean Build

```bash
# Option 1: Using Android Studio UI
# Build > Clean Project
# Build > Rebuild Project

# Option 2: Using Terminal
./gradlew clean
./gradlew build
```

### 3.2 Debug Build

```bash
./gradlew assembleDebug
# APK location: app/build/outputs/apk/debug/app-debug.apk
```

### 3.3 Release Build

```bash
./gradlew assembleRelease
# APK location: app/build/outputs/apk/release/app-release.apk

# For signed APK:
./gradlew bundleRelease
# Bundle location: app/build/outputs/bundle/release/app-release.aab
```

---

## SECTION 4: TESTING

### 4.1 Run Unit Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.dutra.agente.domain.*"

# Generate test report
./gradlew test --no-build-cache
open build/reports/tests/test/index.html
```

### 4.2 Run Instrumented Tests

```bash
# Connect Android device via USB
./gradlew connectedAndroidTest
```

---

## SECTION 5: EMULATOR SETUP

### 5.1 Create Virtual Device

1. Open Android Studio
2. Tools > Device Manager > Create Device
3. Select device type (Pixel 4 recommended)
4. Select API level (Android 12 / API 31)
5. Click Next > Finish

### 5.2 Run on Emulator

1. Start emulator from Device Manager
2. Android Studio: Run > Run 'app' or Shift+F10
3. Select emulator from device list
4. Click OK

---

## SECTION 6: PHYSICAL DEVICE DEPLOYMENT

### 6.1 Enable Developer Mode (Android Device)

1. Settings > About Phone
2. Tap "Build Number" 7 times
3. Go back to Settings > Developer Options
4. Enable "USB Debugging"
5. If prompted, tap "Allow USB Debugging"

### 6.2 Connect via USB

1. Connect device to computer via USB cable
2. Accept any trust prompts on device
3. Verify connection: `adb devices`

### 6.3 Deploy APK

```bash
# Automatic deployment (Shift+F10)
# Or manual installation:
adb install app/build/outputs/apk/debug/app-debug.apk

# Launch app
adb shell am start -n com.dutra.agente/.MainActivity
```

---

## SECTION 7: GRADLE TROUBLESHOOTING

### Issue: Gradle Sync Fails

```bash
# Solution:
./gradlew clean
./gradlew --refresh-dependencies
./gradlew build

# Or in Android Studio:
# File > Invalidate Caches > Invalidate and Restart
```

### Issue: Out of Memory During Build

```gradle
// Edit gradle.properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=512m
```

### Issue: Build Tools Not Found

```bash
# Update SDK
./gradlew --write-verification-metadata sha256 help
sudo chmod +x gradlew
```

---

## SECTION 8: IMPORTANT FILES

### 8.1 Key Configuration Files

**build.gradle (Project-level)**
- Configures Kotlin, Gradle plugins
- Sets Android SDK versions

**app/build.gradle**
- Configures app dependencies
- Sets up signing config for release builds
- Defines build variants

**gradle.properties**
- Sets JVM memory limits
- Configures Gradle behavior

**AndroidManifest.xml**
- Declares app permissions
- Registers activities, services
- Sets minimum API level

---

## SECTION 9: RUNNING THE APPLICATION

### From Android Studio
1. Click green Run button (or Shift+F10)
2. Select device (emulator or physical device)
3. App will launch automatically

### From Command Line
```bash
./gradlew installDebug
adb shell am start -n com.dutra.agente/.MainActivity
```

---

## SECTION 10: PRODUCTION DEPLOYMENT

### 10.1 Create Release APK

```bash
./gradlew assembleRelease
```

### 10.2 Create App Bundle (Google Play)

```bash
./gradlew bundleRelease
```

### 10.3 Upload to Google Play

1. Go to Google Play Console
2. Create new app
3. Upload app bundle
4. Fill store listing details
5. Submit for review

---

## SECTION 11: PERFORMANCE MONITORING

### View Logcat
```bash
adb logcat
# Filter logs:
adb logcat | grep "AgentSmith"
```

### Profile App Performance
1. Run app on device
2. Android Studio: Profiler (View > Tool Windows > Profiler)
3. Monitor CPU, Memory, Network, Energy

---

## FINAL CHECKLIST

- [x] Android Studio installed
- [x] JDK 11+ installed
- [x] Android SDK configured
- [x] Repository cloned
- [x] Project opened in Android Studio
- [x] Gradle sync completed
- [x] Build successful
- [x] Tests passing
- [x] App runs on emulator/device
- [x] Ready for production deployment

---

## SUPPORT & RESOURCES

- Android Studio Docs: https://developer.android.com/studio/intro
- Kotlin Docs: https://kotlinlang.org/docs
- Gradle Docs: https://gradle.org/guides
- GitHub Issues: https://github.com/Dutra-David/Agente-Smith-Android/issues

---

**Status: READY FOR DEPLOYMENT**

Agente Smith is now ready for production deployment on Android Studio. Follow this guide for seamless installation and deployment.
