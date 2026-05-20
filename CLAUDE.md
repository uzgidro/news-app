# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

UzGidro News is an Android app at the initial-scaffold stage. It is a fresh Android Studio "Empty Views Activity" template — a single `MainActivity` with the default layout. There is no domain logic, networking, or data layer yet; expect to build features from scratch.

## Build & test

Use the Gradle wrapper (`./gradlew` on Unix, `gradlew.bat` on Windows). The wrapper pins Gradle 8.0.

```
./gradlew assembleDebug          # build debug APK
./gradlew installDebug           # build + install on a connected device/emulator
./gradlew test                   # JVM unit tests (app/src/test)
./gradlew connectedAndroidTest   # instrumented tests (app/src/androidTest) — needs a device/emulator
./gradlew lint                   # Android Lint
./gradlew clean
```

Run a single unit test class or method:

```
./gradlew test --tests "uz.uzgidro.ugenews.ExampleUnitTest"
./gradlew test --tests "uz.uzgidro.ugenews.ExampleUnitTest.addition_isCorrect"
```

## Architecture & conventions

- Single Gradle module `:app`. Package: `uz.uzgidro.ugenews` (note the package differs from the repo/app name).
- Kotlin, View-based UI (XML layouts in `app/src/main/res/layout/`), **not** Jetpack Compose.
- `compileSdk`/`targetSdk` 33, `minSdk` 26. Kotlin 1.8.0, Android Gradle Plugin 8.1.0, JVM target 1.8.
- Theme `Theme.UzGidroNews` is a Material 3 theme; `MainActivity` opts into Material You dynamic colors via `DynamicColors.applyToActivityIfAvailable`.
- Gradle build scripts use Kotlin DSL (`.kts`). Dependency versions are declared inline in `app/build.gradle.kts` — there is no version catalog (`libs.versions.toml`) yet.
- `dependencyResolutionManagement` uses `FAIL_ON_PROJECT_REPOS`: declare repositories only in `settings.gradle.kts`, never in module build files.
