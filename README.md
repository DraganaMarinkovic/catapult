# Catapult

Catapult is a Kotlin-based Android app that lets users explore, search, and learn about cat breeds, take quizzes, view leaderboards, and manage their accounts.  
It features smooth navigation, responsive UI design, and an intuitive search experience.

## Features
- **Breed List** with search functionality
- **Detailed Breed Pages** including gallery view
- **Interactive Quiz** for testing knowledge
- **Leaderboard** tracking top players
- **Account Management** for user sessions

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Navigation:** Jetpack Navigation Compose
- **Dependency Injection:** Hilt (Dagger-Hilt)
- **State Management:** ViewModel, Kotlin Flow
- **Async & Coroutines:** Kotlin Coroutines, Flow
- **Architecture:** MVVM (Model-View-ViewModel)
- **Other:** Coil (image loading), Scaffold-based layout, custom theming

## Requirements
- Android Studio Flamingo or newer
- Minimum SDK 24+
- Internet connection for API data

## Installation
Clone the repo and open it in Android Studio.  
Connect an Android device or emulator, then run:

```bash
./gradlew installDebug
