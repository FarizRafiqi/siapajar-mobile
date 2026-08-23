package id.siapajar.app.ui.rppm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.siapajar.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RppmDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateAssessment: (activityName: String) -> Unit,
    viewModel: RppmViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val agenda = uiState.agenda

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Detail Rencana & RPPM",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (agenda != null) "Semester ${agenda.semesterNumber} • Minggu ke-${agenda.weekNumber}" else "Modul Ajar Harian",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    if (agenda != null) {
                        Surface(
                            color = MintSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text(
                                text = "Hari Ini",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardSurface
                )
            )
        },
        containerColor = CanvasBackground
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = EmeraldPrimary)
            }
        } else if (agenda == null || agenda.topicTitle.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MintSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "Belum Ada Modul Ajar / RPPM",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Rencana Pembelajaran Mingguan (RPM) belum dibuat atau belum diterbitkan untuk kelas ini di web SiapAjar.",
                        fontSize = 14.sp,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Kembali ke Beranda")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. HERO CARD (Topik & Target Capaian)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF047857),
                                        Color(0xFF059669),
                                        Color(0xFF10B981)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Topik Utama PAUD / RA",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Fase Fondasi (5-6 Thn)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = agenda.topicTitle,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 26.sp
                            )

                            if (agenda.subTopic.isNotBlank()) {
                                Text(
                                    text = agenda.subTopic,
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }

                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.25f),
                                thickness = 1.dp
                            )

                            // Target Capaian Pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = agenda.targetedTpCode,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = agenda.targetedTpTitle,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // 2. TAHAP PEMBELAJARAN
                Surface(
                    color = MintSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Tahapan Pembelajaran Hari Ini:",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = agenda.stage,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                        }
                    }
                }

                // 3. KEGIATAN PEMBUKA (AWAL)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AmberLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = AmberAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "1. Kegiatan Pembuka (Awal)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        // Daftar Aktivitas Pembuka
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            agenda.openingActivities.forEach { item ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Text(
                                        text = item,
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        // Pertanyaan Pemantik
                        if (agenda.openingQuestions.isNotEmpty()) {
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = Color(0xFFB45309),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Pertanyaan Pemantik Guru:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                    }
                                    agenda.openingQuestions.forEach { q ->
                                        Text(
                                            text = "• \"$q\"",
                                            fontSize = 12.sp,
                                            color = Color(0xFF78350F),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. KEGIATAN INTI HARI INI (PILIHAN SENTRA / AKTIVITAS)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MintSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "2. Kegiatan Inti (${agenda.coreActivities.size} Pilihan Aktivitas)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        // Tab / Chip Selector Kegiatan
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            agenda.coreActivities.forEachIndexed { index, activity ->
                                val isSelected = uiState.selectedActivityIndex == index
                                Surface(
                                    color = if (isSelected) EmeraldPrimary else CanvasBackground,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) EmeraldPrimary else BorderSlate
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.selectActivity(index) }
                                ) {
                                    Text(
                                        text = "Kegiatan ${index + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .wrapContentWidth(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }

                        // Rincian Kegiatan yang Dipilih
                        val selectedActivity = agenda.coreActivities.getOrNull(uiState.selectedActivityIndex)
                            ?: agenda.coreActivities.firstOrNull()

                        if (selectedActivity != null) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Nama Kegiatan & Focus
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = selectedActivity.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        lineHeight = 22.sp
                                    )
                                    Surface(
                                        color = MintSurface,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Fokus: ${selectedActivity.focus}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = EmeraldDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                // KOTAK ALAT & BAHAN
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Inventory2,
                                                contentDescription = null,
                                                tint = Color(0xFF334155),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "Alat & Bahan yang Diperlukan:",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                        Text(
                                            text = selectedActivity.materials,
                                            fontSize = 13.sp,
                                            color = TextSecondary,
                                            lineHeight = 19.sp
                                        )
                                    }
                                }

                                // LANGKAH / CARA BERMAIN
                                Surface(
                                    color = CardSurface,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FormatListNumbered,
                                                contentDescription = null,
                                                tint = EmeraldPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "Langkah Pembuatan / Cara Bermain:",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                        Text(
                                            text = selectedActivity.instructions,
                                            fontSize = 13.sp,
                                            color = TextSecondary,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }

                                // MANFAAT / BENEFIT
                                if (selectedActivity.benefits.isNotBlank() && selectedActivity.benefits != "-") {
                                    Surface(
                                        color = MintSurface,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = EmeraldDark,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = selectedActivity.benefits,
                                                fontSize = 12.sp,
                                                color = EmeraldDark,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                // TOMBOL CATAT ASESMEN
                                Button(
                                    onClick = { onNavigateAssessment(selectedActivity.name) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldPrimary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Catat Asesmen Kegiatan Ini",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. KEGIATAN PENUTUP (REFLEKSI)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0E7FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = Color(0xFF4338CA),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "3. Kegiatan Penutup & Refleksi",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            agenda.closingActivities.forEach { item ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4338CA),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Text(
                                        text = item,
                                        fontSize = 13.sp,
                                        color = TextSecondary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
