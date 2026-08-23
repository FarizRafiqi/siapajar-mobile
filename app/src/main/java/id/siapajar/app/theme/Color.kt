package id.siapajar.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Brand Primary Colors (Consistent Emerald Theme)
val EmeraldPrimary = Color(0xFF059669)
val EmeraldDark = Color(0xFF047857)
val EmeraldLight = Color(0xFF10B981)

val AmberAccent = Color(0xFFF59E0B)
val AmberLight = Color(0xFFFEF3C7)

// Dynamic Theme Tokens (Automatically React to System Light & Dark Mode)
val CanvasBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF0B1120) else Color(0xFFF8FAFC)

val CardSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF131D31) else Color(0xFFFFFFFF)

val BorderSlate: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color(0xFFE2E8F0)

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFFF8FAFC) else Color(0xFF0F172A)

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFFCBD5E1) else Color(0xFF334155)

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF94A3B8) else Color(0xFF64748B)

val MintSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF064E3B).copy(alpha = 0.45f) else Color(0xFFECFDF5)

val MintContainer: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) Color(0xFF064E3B) else Color(0xFFD1FAE5)

// Slate Color Palette
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate600 = Color(0xFF475569)
val Slate500 = Color(0xFF64748B)
val Slate400 = Color(0xFF94A3B8)
val Slate300 = Color(0xFFCBD5E1)
val Slate200 = Color(0xFFE2E8F0)
val Slate100 = Color(0xFFF1F5F9)
val Slate50 = Color(0xFFF8FAFC)

// Status Colors
val StatusHadir = Color(0xFF059669)
val StatusIzin = Color(0xFF2563EB)
val StatusSakit = Color(0xFFD97706)
val StatusAlpa = Color(0xFFE11D48)
