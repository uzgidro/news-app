# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

UzGidro News — Android news app for `uzgidro.uz`. Being rebuilt on top of a prior clean-architecture
implementation preserved in git history (commit `5bd31dc` and neighbors): data/domain/presentation,
Retrofit, ViewModel, RecyclerView+DiffUtil, Navigation+safe-args, offline handling, HTML→blocks detail.
Current work resurrects and modernizes it. Data source: `https://uzgidro.uz/api/news`
(paged `?page=N`, 20/page, 3 languages uz/ru/eng per item).

## Build & test

Use the Gradle wrapper (`gradlew.bat` on Windows). `JAVA_HOME` is not set in the shell — export the
Android Studio JBR before building:

```
export JAVA_HOME="C:/Program Files/Android/Android Studio/jbr"   # JBR 21
./gradlew.bat :app:assembleDebug
./gradlew.bat :app:testDebugUnitTest       # JVM/Robolectric unit tests
./gradlew.bat :app:lint
./gradlew.bat :app:bundleRelease           # signed AAB (Wave 7)
```

Run a single unit test:

```
./gradlew.bat :app:testDebugUnitTest --tests "uz.uzgidro.ugenews.*NewsMapperTest"
```

## Toolchain (current)

- **AGP 9.3.1, Gradle 9.6.1, JDK 17** (JBR 21 used to run). **Built-in Kotlin 2.2.10** — AGP compiles
  Kotlin itself; there is **no** `org.jetbrains.kotlin.android` plugin, and the compiler version is
  dictated by AGP (not pinnable). Compiler plugins (serialization, parcelize) and KSP must match 2.2.10 /
  `2.2.10-2.0.2`.
- `compileSdk` via block DSL `release(37){ minorApiLevel = 1 }` = **API 37.1** (Android 17 QPR1);
  `targetSdk = 37`, `minSdk = 26`. `targetSdk` cannot take a minor level (integer only).
- Release build: R8 (`isMinifyEnabled`) + `isShrinkResources`. `android:allowBackup="false"`.
- Version catalog at `gradle/libs.versions.toml` — all versions live there; do not hardcode in modules.
- `dependencyResolutionManagement` uses `FAIL_ON_PROJECT_REPOS`: declare repos only in `settings.gradle.kts`.

## Architecture & conventions

- Single module `:app`, package `uz.uzgidro.ugenews` (note: package ≠ repo/app name; it's `ugenews`).
- Kotlin, **View-based UI** (XML layouts), **not** Jetpack Compose. viewBinding enabled.
- Layers: `data/` (net + Room + mapper + RemoteMediator), `domain/` (models, use cases), `presentation/`
  (Application `App` + `AppContainer` manual DI, fragments, viewmodels, recycler, html).
- Networking: Retrofit + OkHttp + kotlinx.serialization (suspend). Images: Coil. Offline: Room + Paging 3
  RemoteMediator. Language pref: DataStore. HTML: Jsoup → content blocks. DI: manual `AppContainer` (no Hilt).
- Multi-language: Room caches all 3 languages; selected language applied at read time (`flatMapLatest`),
  switching never refetches.
- Theme `Theme.UzGidroNews` is Material 3; `MainActivity` uses `DynamicColors`. Edge-to-edge is mandatory
  at targetSdk 37 — handle window insets on every screen.

## Data API notes

- Item fields: `id, uz/ru/eng (title), uztext/rutext/engtext (HTML body), uzsmall/rusmall/engsmall, date, img, views`.
- `img` is a BROKEN URL — prefix `https://uzgidro.uz/images/news/` is prepended to an absolute URL; the
  mapper strips it. Images can be large (up to ~4 MB) → Coil downsamples.
- `*small` == title (not a real excerpt) → derive the card excerpt from HTML `text` (strip tags, ~150 chars).
- Pagination via `_links.next` / `_meta` (totalCount, pageCount, perPage 20).

## Release

- Keystore `E:\projects\keys` (PKCS12), storePass `123456`, alias `keys`. SHA-256 must match Play Console.
- Play has versionCode 3 / targetSdk 35 → next upload versionCode ≥ 4.
