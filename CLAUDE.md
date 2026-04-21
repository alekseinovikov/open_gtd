# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Open GTD is a Kotlin Multiplatform (KMP) application targeting **Android** and **Desktop (JVM)**, built with Compose Multiplatform. It is currently in its initial scaffold state.

- Package: `me.alekseinovikov.open_gtd`
- Kotlin: 2.3.20, Compose Multiplatform: 1.10.3
- Android: minSdk 24, compileSdk/targetSdk 36, JVM target 11

## Build & Run Commands

```shell
# Run desktop (JVM) app
./gradlew :composeApp:run

# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Run all tests
./gradlew :composeApp:allTests

# Run JVM tests only
./gradlew :composeApp:jvmTest

# Package desktop distributions (Dmg/Msi/Deb)
./gradlew :composeApp:packageDistributionForCurrentOS
```

## Source Set Structure

All application code lives under `composeApp/src/`:

| Source set | Purpose |
|---|---|
| `commonMain` | Shared UI and business logic (Compose, `expect` declarations) |
| `commonTest` | Shared tests using `kotlin.test` |
| `jvmMain` | Desktop entry point (`main.kt`) and `actual` platform implementations |
| `androidMain` | Android `MainActivity` and `actual` platform implementations |

## Key Architectural Patterns

**Platform abstraction**: Uses Kotlin's `expect`/`actual` mechanism. The `Platform` interface and `getPlatform()` expect function are declared in `commonMain/Platform.kt`; each target provides its `actual` implementation (`Platform.jvm.kt`, `Platform.android.kt`).

**Shared UI entry point**: `commonMain/App.kt` contains the root `@Composable fun App()` used by both targets. The desktop target wraps it in a `Window` in `jvmMain/main.kt`; the Android target hosts it in `androidMain/MainActivity.kt`.

**Dependency versions**: All library and plugin versions are centrally managed in `gradle/libs.versions.toml` (version catalog). Always use catalog aliases (`libs.*`) rather than hardcoded version strings.
