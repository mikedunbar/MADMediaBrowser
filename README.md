![Band List Screen](docs/images/BandListScreenNew.png)


# Modern Android Media Browser
A contrived POC samples app

## Purpose
Showcase modern Android development skills, to aid in landing my next job and keeping my head in the game.

I was laid off as part of a 10% staff reduction in March. After a 2 month sabbatical I'm now rested and looking for my next Android adventure.
This project aims to keep me busy and my skills fresh until I land my next job. UI/UX polish is taking a backseat to engineering best practices to 
begin with, but I look forward to improving that as well. It will be a perpetual 

I welcome any and all feedback! I'm intentionally publishing this with some glaring warts, to serve as motivation to clean them up, before
too many prospective employers take a look. 

## What's implemented so far
### Features
- Multiple product versions: Free vs Paid
- Custom splash screen with branding message
- Material 3 App Theming
- Material 3 App Scaffolding, with top and bottom bars, plus navigation drawer
- Dark Theme Support, including option to override system setting

### Tech
- Kotlin, Coroutines and Flows 
- Jetpack Compose
- Jetpack Navigation
- Jetpack Lifecycle Components
- Jetpack ViewModel
- Jetpack Datastore, for local settings prefs storage
- Jetpack Hilt, for Dependency Injection
- Layered Architecture
- MVVM in the UI layer
- Unidirectional Data Flow
- REST API integration with OkHttp, RetroFit, and Moshi
- Behavior testing with JUnit 5

## In flight
### Training for interview coding challenges
- Gotta, gotta eat. Whatever it takes

### MVP Functionality
- Real API: Scrollable, paged Band List Screen. Click to Album List Screen, Click to Song List Screen.
- Play songs

## On Deck
- UI tests with Espresso
- ViewModel tests
- Domain and Data Layer tests
- Optimize layout in landscape mode
- Play a song
- Data survives configuration change (settings input field, e.g.)
- TOML version catalogue for dependencies
- WorkManager (for syncing)
- Offline first
- Play a video
- Ship as app bundle
- cool splash effect
- cool animations
- timber for logging
- Baseline profiles
- Biometric authentication
- Android integration with Instrumentation tests
- App Widget with Glance
- WearOS version
