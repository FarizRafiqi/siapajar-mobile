# SiapAjar Mobile (Android) 📱

Companion mobile app untuk guru saat mengajar aktif di kelas (PAUD / TK / SD). Didesain dengan fokus pada **simplicity**, **kecepatan pengambilan asesmen**, dan **arsitektur Offline-First (Room SQLite)** yang tersinkronisasi otomatis dengan backend SiapAjar (AdonisJS).

---

## 🎨 Tech Stack & Architecture

- **Language**: Kotlin 2.1.0 (Coroutines, Flow, Serialization)
- **UI Framework**: Jetpack Compose + Material 3
- **Local DB (Offline-First)**: AndroidX Room (SQLite)
- **Background Sync**: AndroidX WorkManager
- **Networking**: Retrofit 2 + OkHttp + Kotlinx Serialization
- **Image Loading**: Coil 3
- **Design System**: Emerald Signature (`#059669`), Mint Surface (`#ECFDF5`), Amber Accent (`#F59E0B`), Slate Text (`#0F172A`)

---

## 📱 Struktur Layar & Fitur Utama

1. **Beranda (`HomeScreen`)**:
   - Status Sinkronisasi Realtime & Indikator Offline.
   - Hero Card: Agenda & Modul Ajar Hari Ini (Topik, Kegiatan, TP).
   - Card Presensi Cepat Kelas.
   - Status Progres Asesmen Mingguan & Tombol Buat Rangkuman AI.
   - **Central Docked FAB**: Tombol Kamera Asesmen di tengah navigasi bawah.

2. **Catat Asesmen Kegiatan (`QuickAssessmentScreen`)**:
   - Preview foto kegiatan anak.
   - Pilihan instrumen asesmen (*Catatan Anekdot*, *Hasil Karya*, *Foto Berseri*, *Ceklis Capaian*).
   - Multi-tagging nama siswa.
   - Input catatan observasi & fitur voice-to-text.
   - Penyimpanan langsung ke Room SQLite lokal (instan tanpa lag).

3. **Presensi Harian 30 Detik (`AttendanceScreen`)**:
   - Header ringkasan jumlah status (*Hadir*, *Izin*, *Sakit*, *Alpa*).
   - Tombol satu ketukan *"Semua Hadir"*.
   - Segmented buttons H/I/S/A per siswa.

4. **Profil & Portofolio Siswa (`StudentDetailScreen`)**:
   - Header profil & jumlah catatan asesmen semester ini.
   - Linimasa mingguan observasi, karya, dan ketercapaian TP.

---

## 🛠️ Menjalankan Proyek

Buka folder `/home/farizrafiqi/Projects/siapajar-mobile` langsung di **Android Studio Ladybug / Koala** atau versi terbaru. Gradle akan mengunduh dependencies secara otomatis.
