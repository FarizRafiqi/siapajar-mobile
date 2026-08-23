package id.siapajar.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.siapajar.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiapAjarBottomBar(
    currentRoute: String,
    onNavigateHome: () -> Unit,
    onNavigateAssessment: (mode: String) -> Unit,
    onNavigateAttendance: () -> Unit,
    onNavigateStudents: () -> Unit
) {
    var showQuickActions by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. Bottom Bar Surface (Docked Flat Bar 72.dp height - Full Width Edge to Edge)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = androidx.compose.ui.graphics.RectangleShape,
            color = CardSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Beranda Tab (Left Slot - 1f symmetrical)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    BottomNavItem(
                        label = "Beranda",
                        icon = if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home,
                        isActive = currentRoute == "home",
                        onClick = onNavigateHome
                    )
                }

                // 2. Empty Center Slot (Placeholder for 64dp FAB + padding)
                Spacer(modifier = Modifier.width(72.dp))

                // 3. Siswa Tab (Right Slot - 1f symmetrical)
                val isStudentActive = currentRoute == "student_list" || currentRoute.startsWith("student_detail")
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    BottomNavItem(
                        label = "Siswa",
                        icon = if (isStudentActive) Icons.Filled.Group else Icons.Outlined.Group,
                        isActive = isStudentActive,
                        onClick = onNavigateStudents
                    )
                }
            }
        }

        // 2. Floating Elevated Center '+' Action Button (Prominent 64.dp FAB)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-22).dp)
                .size(64.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = Color(0x33000000)
                )
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(EmeraldLight, EmeraldPrimary, EmeraldDark)
                    )
                )
                .border(4.dp, CardSurface, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showQuickActions = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Buat Catatan Cepat",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }

    // 3. Quick Action Modal Bottom Sheet
    if (showQuickActions) {
        ModalBottomSheet(
            onDismissRequest = { showQuickActions = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = CardSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = {
                Surface(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 44.dp, height = 4.dp),
                    shape = RoundedCornerShape(2.dp),
                    color = Slate300
                ) {}
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 36.dp)
            ) {
                Text(
                    text = "Aksi & Pencatatan Cepat",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Pilih jenis asesmen atau administrasi kelas",
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
                )

                // Action Option 1: Ambil Foto Langsung (Hasil Karya / Foto Berseri)
                QuickActionItem(
                    icon = Icons.Default.CameraAlt,
                    iconBg = Color(0xFFECFDF5),
                    iconTint = EmeraldPrimary,
                    title = "Ambil Foto Langsung",
                    subtitle = "Jepret hasil karya atau dokumentasi aktivitas siswa",
                    onClick = {
                        showQuickActions = false
                        onNavigateAssessment("camera")
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Option 2: Catatan Anekdot (Teks Cepat)
                QuickActionItem(
                    icon = Icons.Default.EditNote,
                    iconBg = Color(0xFFFEF3C7),
                    iconTint = Color(0xFFD97706),
                    title = "Tulis Catatan Anekdot",
                    subtitle = "Catat peristiwa penting / capaian tujuan belajar anak",
                    onClick = {
                        showQuickActions = false
                        onNavigateAssessment("anecdote")
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Option 3: Unggah dari Galeri
                QuickActionItem(
                    icon = Icons.Default.PhotoLibrary,
                    iconBg = Color(0xFFEFF6FF),
                    iconTint = Color(0xFF2563EB),
                    title = "Unggah dari Galeri",
                    subtitle = "Pilih foto aktivitas yang tersimpan di HP",
                    onClick = {
                        showQuickActions = false
                        onNavigateAssessment("gallery")
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Option 4: Presensi Kelas
                QuickActionItem(
                    icon = Icons.Default.FactCheck,
                    iconBg = Color(0xFFF0FDF4),
                    iconTint = Color(0xFF16A34A),
                    title = "Presensi Harian Kelas",
                    subtitle = "Ceklis kehadiran siswa hari ini",
                    onClick = {
                        showQuickActions = false
                        onNavigateAttendance()
                    }
                )
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = CanvasBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val tintColor by animateColorAsState(
        targetValue = if (isActive) EmeraldPrimary else TextMuted,
        label = "tab_tint"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        // Active indicator pill container (Standard 54x32dp)
        Box(
            modifier = Modifier
                .width(54.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isActive) MintSurface else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tintColor,
                modifier = Modifier.size(25.dp) // Standard Material 3 Navigation Icon size
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 12.sp, // Standard readable caption/label size
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
            color = tintColor
        )
    }
}
