# YT to Drive (Android)

Kotlin app: receive a shared link (for example from YouTube), then—once implemented—sign in to Google Drive, pick a folder under your Music library, convert to MP3, and upload.

## Requirements

- **Android Studio** Koala (2024.1.1) or newer, or another environment with **JDK 17** and Android SDK **API 35** / build-tools installed.
- From this directory, Gradle uses the included wrapper (`./gradlew`). If `./gradlew` is not executable: `chmod +x gradlew`.

## Build

```bash
./gradlew :app:assembleDebug
```

Install the debug APK on a device or emulator from Android Studio (**Run**), or:

```bash
./gradlew :app:installDebug
```

## Google Drive API (one-time setup)

Sign-In and folder listing use **Google Sign-In** plus the **Drive REST API**. You do **not** need Firebase or `google-services.json` for this flow.

1. In [Google Cloud Console](https://console.cloud.google.com/), create or select a project and **enable the Google Drive API**.
2. Configure the **OAuth consent screen** (Testing is fine while you develop; add your Google account as a test user if the app stays in testing mode).
3. Create an **OAuth 2.0 Client ID** of type **Android**:
   - **Package name:** `com.yttodrive.app`
   - **SHA-1:** debug keystore (run on your Mac):

     ```bash
     keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey \
       -storepass android -keypass android
     ```

     Copy the **SHA1** fingerprint into the client configuration.

4. Install the app on a device/emulator signed with that debug key and open **Try Drive folders** or continue from **Share** → **Continue**. Sign in when prompted. You must have a top-level folder named **Music** in My Drive; under it, this app lists **subfolders** you can tap to save as the upload target.

If you later add Firebase, place `google-services.json` under `app/` — it is **gitignored** at the repo root and must not be committed.

## Current scope

Share target, folder picker with **Google Sign-In** and **Drive folder listing** under Music, DataStore for last chosen folder, and a stub **WorkManager** worker. Audio extraction (NewPipeExtractor or yt-dlp), FFmpeg encoding, Drive **file** upload, and full upload pipeline come next.

## Troubleshooting: Kotlin compile daemon

If the build fails with **“The daemon has terminated unexpectedly”** / **“Could not connect to Kotlin compile daemon”** even though the log says the daemon is “ready”:

1. **`gradle.properties` in this folder** sets `kotlin.compiler.execution.strategy=in-process` so Kotlin compiles inside the Gradle process (more stable). Sync and build again.
2. **Stop stale daemons:** in a terminal from `android-app/`, run `./gradlew --stop`, then **Build → Clean Project** and **Rebuild** in Android Studio.
3. **Strongly recommended:** keep the project on a **local disk** (for example `~/Projects/yt-to-drive`), not **iCloud Drive** or other sync folders. Cloud sync and paths under `Mobile Documents` often trigger flaky compiler daemons and file locking.
4. **JDK:** use the IDE’s bundled JDK (**Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**) and pick **JDK 17** (or the embedded **jbr-17**).
5. If it still fails: **File → Invalidate Caches → Invalidate and Restart**, then rebuild.
