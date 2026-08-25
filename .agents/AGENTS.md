# Project Design & Development Guidelines (SiapAjar Mobile)

## UI & Design Aesthetics Preferences

1. **High Text Contrast for Readability**:
   - Always use high-contrast text tokens (`TextPrimary` for light/dark mode). Avoid using dim or low-contrast gray text for important titles, descriptions, badges, or labels.

2. **No Raw Emoji Icons Rule**:
   - Never use raw text emojis as UI icons or in headings/buttons. Always use proper Material Icon vectors or custom SVG drawables.

3. **No Raw Underscores in UI Labels / Badges**:
   - Never render raw database enum strings or keys containing underscores (e.g. `catatan_anekdot`, `foto_berseri`, `hasil_karya`) directly in UI badges or text labels. Always format them on the Frontend into clean, human-readable Title Case text (e.g. `Catatan Anekdot`, `Foto Berseri`, `Hasil Karya`).

4. **Back Navigation Alignment Standard (Left Alignment Rule)**:
   - Always place 'Back' navigation triggers on the **LEFT side** of the page header.

5. **Zero Dummy Data Rule (Strict Empty State Standard)**:
   - **STRICTLY PROHIBITED**: Never use fake placeholder names (e.g. "Kenzo", "Aisyah", "Budi Santoso" hardcoded) or static fallback mock data across any user-facing screens.
   - When data from the server or local database is empty or not yet created, **ALWAYS render a clean, informative, and polished Empty State component** (e.g. *"Belum ada data siswa untuk kelas ini"* or *"Belum ada catatan asesmen"*).
   - Never construct mock fallback lists inside ViewModels or Composables solely to populate empty layout space.
