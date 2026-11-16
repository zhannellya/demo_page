# ADOPTME — Приюты животных

Android application for a demonstration animal-adoption flow. The interface is Russian. Code, package names, and this document are English.

The Figma file linked in the task was not opened from this environment. Visual direction follows the requested palette (`#F5C9F2`, `#6F4F84`, `#311E49`, `#43145D`), the onboarding illustrations already in the project, and remote animal photos used as placeholders.

Package id stays `com.adoptme.app` because that is the existing application id. Logic is split across `data`, `repository`, `viewmodel`, `ui`, `navigation`, and `utils`.

## Open in Android Studio

1. Install Android Studio (Ladybug or newer) with Android SDK 35 and JDK 17.
2. Open the `AdoptMe` directory (the folder that contains `settings.gradle.kts`).
3. Let Gradle sync. The project uses AGP 8.7.3, Kotlin 2.0.21, and Gradle 8.9.
4. If sync asks for `local.properties`, copy `local.properties.example` and set `sdk.dir`.
5. Run the `app` configuration on a device or emulator (minSdk 26).

## Maps key

Google Maps is optional. Put a key in `local.properties`:

```properties
MAPS_API_KEY=your_key_here
```

The value is passed to the manifest through a Gradle placeholder and is not hard-coded. If the key is missing, or Google Play services are unavailable, the shelter list still opens and the map area explains why it is hidden. Demonstration shelters have no street address and no map pin. Do not treat their names as real organizations.

## Demonstration sign-in

Accounts live in Room on the device. Passwords are stored as PBKDF2-HMAC-SHA256 hashes with a random salt, never as plain text.

There is no server. A seeded local administrator exists only so the review flow can be tried:

- Email: `admin@adoptme.local`
- Password: `Admin12345`

The login screen states that this role is not server-side authorization. Self-registration cannot become an administrator. Firebase Authentication and Firestore are prepared as an `AuthRemoteDataSource` seam and are not on the classpath: the Firebase init provider crashes the process when `google-services.json` is absent.

## What is implemented

- Three onboarding pages, login, and registration
- Animal catalog with search by name and filters for type, age, size, gender, and city
- Eight seeded animals with distinct Russian descriptions and stories: Шарик, Тоша, Белла, Барсик, Бобик, Дружок, Рекс, Макс
- Animal details and an adoption form with validation
- Unique numbers `ADOPT-000001`, saved in Room, protected from a second pending application and from a double tap
- My applications, with cancel for status «На рассмотрении»
- Administrator list, approve, and reject; add and edit animals; availability status
- Favorites stored per user in Room
- Shelter list by city, with a guarded Google Map
- Local chat, news, help, notifications, and settings
- DataStore for onboarding, session id, and the notification preference

Statuses shown to people: «На рассмотрении», «Одобрено», «Отклонено», «Отменено».

## Checks

Unit tests cover form validation, animal filtering, password hashing, application numbers, duplicate submits, and reading a saved application from the same store after a new service instance (the stand-in for a process restart). Room itself is exercised by the debug build.

```bash
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

The debug APK is `app/build/outputs/apk/debug/app-debug.apk`.

## Limitations

- No production backend, push notifications, or payment provider. Donation copy tells the user not to enter a card.
- Shelter cards and lost-pet notices are labeled as demonstration data and do not include real coordinates or street addresses.
- Animal photos are loaded from Unsplash when the network is available. Without a network the paw placeholder is shown and the rest of the app keeps working.
- The local administrator password is visible in the client because reviewers need it. It is not a secret and must not be reused for a real service.
- Approving an application marks the animal reserved in a follow-up write. A crash between those two writes can leave the status and the availability one step apart until the card is edited.
