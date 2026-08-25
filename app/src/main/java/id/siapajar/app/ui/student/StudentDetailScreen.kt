package id.siapajar.app.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import id.siapajar.app.theme.*
import id.siapajar.app.ui.components.StudentAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    studentId: String,
    onNavigateBack: () -> Unit,
    onNavigateAddAssessment: () -> Unit,
    viewModel: StudentViewModel = viewModel()
) {
    val uiState by viewModel.detailState.collectAsState()
    val isDark = isSystemInDarkTheme()
    val filterOptions = listOf("Semua", "Catatan Anekdot", "Hasil Karya", "Foto Berseri")

    LaunchedEffect(studentId) {
        viewModel.loadStudentDetail(studentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Profil Siswa",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardSurface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateAddAssessment,
                containerColor = EmeraldPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tambah Catatan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(CanvasBackground)
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))

                // 1. Student Hero Profile (Centered Large Avatar)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StudentAvatar(
                        name = uiState.studentName,
                        photoUrl = uiState.avatarUrl,
                        size = 92.dp,
                        borderWidth = 3.dp,
                        borderColor = EmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = uiState.studentName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = CardSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "${uiState.classText}  •  ${uiState.ageText}  •  NIS ${uiState.nis}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Two-Tier Filter System
                // Level 1: Week Selector (Semua Minggu, Minggu 1..18)
                Text(
                    text = "Filter Minggu Belajar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                val weekOptions = listOf(0) + (1..18).toList()
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(weekOptions) { weekNum ->
                        val isSelected = weekNum == uiState.selectedWeek
                        val label = if (weekNum == 0) "Semua Minggu" else "Minggu $weekNum"
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.setWeekFilter(weekNum) }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) EmeraldPrimary else BorderSlate,
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            color = if (isSelected) EmeraldPrimary else CardSurface
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextPrimary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Level 2: Instrument Type (Semua, Catatan Anekdot, Hasil Karya, Foto Berseri)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterOptions) { option ->
                        val isSelected = option == uiState.selectedFilter
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { viewModel.setFilter(option) }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) EmeraldPrimary else BorderSlate,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            color = if (isSelected) MintSurface else CardSurface
                        ) {
                            Text(
                                text = option,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) EmeraldDark else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Section Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Linimasa Asesmen & Capaian",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${uiState.filteredTimeline.size} Catatan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Timeline items or Strict Empty State (Zero Dummy Rule)
            if (uiState.filteredTimeline.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = CardSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AssignmentLate,
                                contentDescription = null,
                                tint = TextSecondary.copy(alpha = 0.6f),
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (uiState.selectedWeek > 0) {
                                    "Belum ada catatan untuk Minggu ${uiState.selectedWeek}"
                                } else {
                                    "Belum ada catatan asesmen tersimpan"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Gunakan tombol Tambah Catatan di bawah untuk mulai mendokumentasikan observasi perkembangan siswa.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            } else {
                items(uiState.filteredTimeline) { item ->
                    val isWorkSample = item.instrumentType.contains("work", ignoreCase = true) || item.instrumentType.contains("karya", ignoreCase = true)
                    val isPhotoSeries = item.instrumentType.contains("photo", ignoreCase = true) || item.instrumentType.contains("foto", ignoreCase = true)
                    val badgeColor = when {
                        isWorkSample -> EmeraldPrimary
                        isPhotoSeries -> Color(0xFF8B5CF6)
                        else -> Color(0xFF2563EB)
                    }

                    TimelineItemCard(
                        type = item.instrumentTitle,
                        badgeColor = badgeColor,
                        dotColor = badgeColor,
                        weekText = "Minggu ${item.weekNumber} • Semester ${item.semesterNumber}",
                        dateText = item.dateText,
                        photoUrl = item.attachments.firstOrNull()?.url,
                        notes = item.notes ?: item.activity ?: "Observasi perkembangan anak.",
                        tpText = item.tpCode ?: "TP 1.3 - Capaian Pembelajaran"
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun TimelineItemCard(
    type: String,
    badgeColor: Color,
    dotColor: Color,
    weekText: String,
    dateText: String,
    photoUrl: String?,
    notes: String,
    tpText: String
) {
    val isDark = isSystemInDarkTheme()
    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline indicator (Dot + vertical line)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(if (photoUrl != null) 250.dp else 130.dp)
                    .background(BorderSlate)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Card Content
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, BorderSlate, RoundedCornerShape(14.dp)),
            color = CardSurface
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = badgeColor,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = type,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = weekText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateText,
                    fontSize = 11.sp,
                    color = TextSecondary.copy(alpha = 0.8f)
                )

                if (photoUrl != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CanvasBackground)
                    ) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = type,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = notes,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = MintSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = tpText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFF6EE7B7) else EmeraldDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
