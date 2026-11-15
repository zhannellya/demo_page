# AdoptMe

Modern Android pet adoption app for animal shelters — Kotlin, Jetpack Compose, Material 3, MVVM.

## Design

- Elegant purple & lavender palette with pink / cyan neon accents
- Soft gradient backgrounds, large rounded white cards, purple gradient CTAs
- Cute 3D onboarding illustrations
- Bottom navigation: Home · Animals · Favorites · Map · Profile

## Screens

1. Splash  
2. Onboarding (3 slides)  
3. Sign Up / Login  
4. Home (search, categories, recommendations, quick actions)  
5. Animals (search + filter)  
6. Animal Details + Adopt  
7. Favorites  
8. Adoption Application (validated form)  
9. Lost & Found  
10. Help & Donate  
11. Veterinary  
12. Map (shelter markers + list)  
13. Profile (history + logout)  
14. Notifications  

## Open in Android Studio

1. Open the `AdoptMe` folder in Android Studio (Ladybug / Koala or newer).
2. Let Gradle sync (AGP 8.7, Kotlin 2.0, Compose BOM 2024.10).
3. Set `sdk.dir` in `local.properties` if needed.
4. Run on an emulator or device (minSdk 26).

## Firebase (optional)

The app works fully offline with local DataStore + sample data.

To enable Firebase Auth:

1. Create a Firebase project and add an Android app with package `com.adoptme.app`.
2. Download `google-services.json` into `AdoptMe/app/`.
3. Sync Gradle — the Google Services plugin applies automatically when the file is present.

## Package structure

```
com.adoptme.app
├── data/model
├── data/local
├── data/repository
├── di
├── ui/theme
├── ui/components
├── ui/navigation
├── ui/screens/*
└── viewmodel
```
