# Repository Guidelines

## Project Structure & Module Organization
Each product sample lives in its own Android module: `apm_android_demo/`, `httpdns_android_demo/`, `mpush_android_demo/`, `feedback_android_demo/`, `hotfix_android_demo/`, and `devops_android_demo/`. Modules follow the standard layout with app code in `src/main/java` (Kotlin and Java) and resources in `src/main/res`. Shared Gradle configuration sits in `build.gradle` and `settings.gradle`; the Gradle wrapper and scripts live in `gradle/` and `./gradlew`. Generated output goes to `build/`—leave it untracked.

## Build, Test, and Development Commands
Always call the wrapper so we share Gradle versions.
- `./gradlew assembleDebug` builds every demo in debug mode for quick smoke checks.
- `./gradlew :mpush_android_demo:assembleRelease` (swap the module) creates a signed APK using the module’s demo keystore.
- `./gradlew :httpdns_android_demo:lint` runs Android Lint; keep the tree warning-free.
- `./gradlew clean` clears root build artifacts when caches become stale.

## Coding Style & Naming Conventions
Use four-space indentation and idiomatic Kotlin/Java: camelCase methods, PascalCase classes, constants in ALL_CAPS. Android resources stay lowercase_with_underscores (for example, `activity_main.xml`). Prefer the shipped view binding/data binding over manual `findViewById`. Declare shared dependency versions beside the existing `gradle.ext` entries in `settings.gradle` to avoid drift.

## Testing Guidelines
The repo ships minimal automation, so new work should add unit tests under `src/test/java` and instrumentation tests in `src/androidTest/java`. Name tests after the component under test (for example, `HttpDnsServiceHolderTest`). Run them with `./gradlew :<module>:testDebugUnitTest` and `./gradlew :<module>:connectedDebugAndroidTest`. Document manual device checks in the PR when a feature relies on push, hotfix, or other cloud callbacks.

## Commit & Pull Request Guidelines
Follow the conventional prefix seen in history: `<type>: <summary>` such as `feat: 添加动态配置AppKey功能` or `docs: 更新 README`. Use全中文描述 for every commit message, stick to the existing `feat`/`fix`/`docs`…类型前缀, and keep summaries concise (72 characters or less). For pull requests, explain the change, link EMAS tickets or GitHub issues, list verification steps (commands, device builds), and attach screenshots or logs whenever UI or messaging flows change.

## Configuration & Security Tips
Store product credentials locally. Populate `local.properties` or a developer-only `.env` referenced from Gradle, and never commit real AppKeys or keystores. The bundled `Test.jks` is for demo signing only; replace it with a private keystore for production. Review any new Maven repositories with the team before adding them to the shared blocks in `settings.gradle`.
