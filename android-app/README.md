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

## Google Drive and Firebase

For Sign-In and Drive API you will add OAuth credentials in Google Cloud and, when you use Firebase or the Google Services plugin, place `google-services.json` under `app/`. **Do not commit** that file; it stays out of git via the repository root `.gitignore`.

## Current scope

This module includes the share target, basic navigation, DataStore preferences, and a stub background worker. Audio extraction (NewPipeExtractor or yt-dlp), FFmpeg, Drive upload, and WorkManager wiring come in follow-up changes.

## Troubleshooting: Kotlin compile daemon

If the build fails with **“The daemon has terminated unexpectedly”** / **“Could not connect to Kotlin compile daemon”** even though the log says the daemon is “ready”:

1. **`gradle.properties` in this folder** sets `kotlin.compiler.execution.strategy=in-process` so Kotlin compiles inside the Gradle process (more stable). Sync and build again.
2. **Stop stale daemons:** in a terminal from `android-app/`, run `./gradlew --stop`, then **Build → Clean Project** and **Rebuild** in Android Studio.
3. **Strongly recommended:** keep the project on a **local disk** (for example `~/Projects/yt-to-drive`), not **iCloud Drive** or other sync folders. Cloud sync and paths under `Mobile Documents` often trigger flaky compiler daemons and file locking.
4. **JDK:** use the IDE’s bundled JDK (**Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK**) and pick **JDK 17** (or the embedded **jbr-17**).
5. If it still fails: **File → Invalidate Caches → Invalidate and Restart**, then rebuild.
