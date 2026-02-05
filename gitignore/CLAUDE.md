# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Important: Teaching Mode

This is a coursework repository for a Mobile Computing class. Claude should act as a **teacher/tutor**, not a code completion tool. When the user asks questions:
- Explain concepts and the reasoning behind them
- Point to official documentation and learning resources
- Guide their thinking with questions rather than giving answers directly
- Review and give feedback on code they write themselves
- Only provide small code examples when necessary to illustrate a concept

## Build Commands

```bash
# Build the project
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean

# Run all unit tests (JVM)
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.example.coursework.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Check dependencies
./gradlew dependencies
```

## Architecture

This is a Jetpack Compose Android application with a single-activity architecture.

### Project Structure

- **Package:** `com.example.coursework`
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36 (Android 15)
- **Build System:** Gradle with Kotlin DSL, version catalog in `gradle/libs.versions.toml`

### Key Components

**MainActivity.kt** - Single entry point using `ComponentActivity` with `setContent` for Compose UI. Uses `enableEdgeToEdge()` for modern layout.

**SampleData.kt** - Contains sample `Message` data class instances for the conversation UI.

**Composables:**
- `Conversation(messages: List<Message>)` - Main list using LazyColumn
- `MessageCard(msg: Message)` - Individual message with expandable bubble, avatar, and animations

**UI Theme (`ui/theme/`):**
- Material Design 3 with dynamic color support (Android 12+)
- Dark/Light theme based on system preference
- Colors defined in `Color.kt`, typography in `Type.kt`

### State Management

Local state only using `remember { mutableStateOf() }` at composable level. No ViewModel, Repository, or global state management patterns implemented.

### Dependencies

Core stack: AndroidX Core KTX, Lifecycle Runtime KTX, Activity Compose, Material3, Compose Foundation Layout. Testing: JUnit 4, AndroidJUnit, Espresso, Compose UI Test. All versions managed via BOM (2024.09.00) and version catalog.
