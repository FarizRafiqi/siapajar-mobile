# Project Instructions & Development Guidelines (SiapAjar Mobile)

This file is the single source of truth for AI agents (Claude, Antigravity, GitHub Copilot, Cursor, etc.) working on the **SiapAjar Mobile** (Android Kotlin / Jetpack Compose) repository.

---

## 1. Quick Start Commands

```bash
# Build & Compile
./gradlew assembleDebug
./gradlew assembleRelease

# Check & Lint
./gradlew lint
./gradlew ktlintCheck # if configured

# Run Tests
./gradlew test
./gradlew connectedAndroidTest
```

---

## 2. Architecture & Tech Stack

**SiapAjar Mobile** is an Android native application built using Kotlin and Modern Android Architecture (MVVM + Offline-First Room SQLite + Jetpack Compose Material 3).

- **UI Layer**: Jetpack Compose (Material 3), Navigation Compose, Coil 3 (image loading).
- **State & Architecture**: MVVM with Kotlin Coroutines, `StateFlow`, `SharedFlow`, and `ViewModel`.
- **Local Persistence**: Room SQLite (`SiapAjarDatabase`) for full offline functionality.
- **Networking**: Retrofit 2 + OkHttp 3 + KotlinX Serialization.
- **Background Processing**: WorkManager (`SyncWorker`) for resilient background data & photo synchronization.
- **Hardware & Utilities**: CameraX, `ImageCompressor` (smart EXIF-aware image resizing and JPEG compression).

---

## 3. UI & Design Guidelines

1. **High Text Contrast for Readability**:
   - Always use high-contrast color tokens (`TextPrimary`, `TextSecondary`, `TextMuted` from `id.siapajar.app.theme.*`). Avoid using dim, low-contrast text for important titles, descriptions, badges, or labels.

2. **No Raw Emoji Icons Rule**:
   - Never use raw text emojis (e.g. 🗺️, 💡, 🚀, 📌, 🔵, 🟢, 📄) as UI icons or in headings/buttons. Always use proper Material Icon vectors (`androidx.compose.material.icons.*`) or custom vector drawables.

3. **No Raw Underscores in UI Labels / Badges**:
   - Never render raw database enum strings or keys containing underscores (e.g. `catatan_anekdot`, `foto_berseri`, `hasil_karya`) directly in UI badges or text labels. Always format them into clean, human-readable Title Case text (e.g. `Catatan Anekdot`, `Foto Berseri`, `Hasil Karya`).

4. **Back Navigation Alignment Standard (Left Alignment Rule)**:
   - Always place 'Back' navigation triggers (e.g. `IconButton` with `ArrowBack`) on the **LEFT side** of the page header or inside `TopAppBar(navigationIcon = { ... })`.

5. **Edge-to-Edge & Status Bar Spacing**:
   - For screens with `TopAppBar`, allow Material 3 `TopAppBar` to handle status bar insets naturally.
   - For custom header screens (e.g. `HomeScreen`, `LoginScreen`), apply `.statusBarsPadding()` on the root column to maintain clean, native top spacing.

6. **Zero Dummy Data Rule (Strict Empty State Standard)**:
   - **STRICTLY PROHIBITED**: Never use fake placeholder names (e.g. "Kenzo", "Aisyah", "Budi Santoso" hardcoded) or static fallback mock data across any user-facing screens.
   - When data from the server or local database is empty or not yet created, **ALWAYS render a clean, informative, and polished Empty State component** (e.g. *"Belum ada data siswa untuk kelas ini"* or *"Belum ada modul ajar minggu ini"*).
   - Never construct mock fallback lists inside ViewModels or Composables solely to populate empty layout space.

7. **Smart Image Compression**:
   - All captured camera photos or gallery images must be processed via `ImageCompressor` respecting user quality preferences (`Kompresi Cepat`, `Standar HD`, `Kualitas Asli`) to prevent OOM errors and minimize upload payload size.
