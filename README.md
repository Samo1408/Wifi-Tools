# Wifi Tools (WiFi Address Changer & Zygisk Hooking Module)

An advanced Android application and Magisk Zygisk module to dynamically spoof and change WiFi network parameters and widevine system identities for high anonymity and security.

## Core Features
1. **WiFi Network Name (SSID) Modification**: Customize the broadcast SSID or generate global random SSIDs.
2. **MAC / BSSID Address Spoofing**: Support both 6-octet and custom 8-octet formatting.
3. **Bluetooth MAC Address Spoofing**: Spoof Bluetooth identities.
4. **Widevine DRM ID & System ID Spoofing**: Dynamically bypass Widevine checks by hooking native libraries with Dobby inline hooks.
5. **Foreground Notification Service**: Control and randomize configurations on-the-fly via interactive notifications.
6. **Magisk Zygisk Native Module**: High-stealth spoofing at zygote initialization using native Dobby Hook library.

## Project Structure
- `app/`: Source code of the Android application.
- `zygisk_module/`: Sources for the Magisk/Zygisk Module utilizing Dobby inline hook.
- `.github/workflows/`: Automation script for compiling and packaging releases.

## Compilation & Installation
This project supports automated building using GitHub Actions. Upon pushing to `main`, the binaries (APK & Zygisk ZIP) are automatically compiled and attached as **artifacts** to the workflow run.

To build manually:
1. Compile zygisk module: `ndk-build` inside `zygisk_module/zygisk/jni`
2. Compile Android App: `./gradlew assembleRelease`
