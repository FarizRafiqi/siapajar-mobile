package id.siapajar.app.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                .background(Color(0xFFFAFAFA))
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
                        color = Color(0xFFF1F5F9),
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

                // 2. Horizontal Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterOptions) { option ->
                        val isSelected = option == uiState.selectedFilter
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { viewModel.setFilter(option) }
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) EmeraldPrimary else BorderSlate,
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            color = if (isSelected) EmeraldPrimary else Color.White
                        ) {
                            Text(
                                text = option,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Section Title
                Text(
                    text = "Linimasa Asesmen & Capaian (${uiState.totalAssessments} Catatan)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Timeline items
            if (uiState.timeline.isEmpty()) {
                // Fallback default timeline preview
                item {
                    TimelineItemCard(
                        type = "Hasil Karya",
                        badgeColor = EmeraldPrimary,
                        dotColor = EmeraldPrimary,
                        photoUrl = "https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=600&auto=format&fit=crop&q=80",
                        notes = "Mampu menggunting pola lingkaran dengan rapi dan bercerita tentang karyanya.",
                        tpText = "TP 3.1 - Motorik Halus & Kreativitas"
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    TimelineItemCard(
                        type = "Catatan Anekdot",
                        badgeColor = Color(0xFF2563EB),
                        dotColor = Color(0xFF2563EB),
                        photoUrl = null,
                        notes = "Saat waktu bermain bebas, anak aktif mengajak temannya menyusun balok bersama dan membagi peran membangun menara tinggi.",
                        tpText = "TP 2.2 - Sosial Emosional & Kolaborasi"
                    )
                }
            } else {
                items(uiState.timeline) { item ->
                    val isWorkSample = item.instrumentType.contains("work", ignoreCase = true) || item.instrumentType.contains("karya", ignoreCase = true)
                    TimelineItemCard(
                        type = item.instrumentTitle,
                        badgeColor = if (isWorkSample) EmeraldPrimary else Color(0xFF2563EB),
                        dotColor = if (isWorkSample) EmeraldPrimary else Color(0xFF2563EB),
                        photoUrl = item.attachments.firstOrNull()?.url,
                        notes = item.notes ?: item.activity ?: "Observasi pembelajaran harian.",
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
    photoUrl: String?,
    notes: String,
    tpText: String
) {
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
            color = Color.White
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

                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = TextMuted
                        )
                    }
                }

                if (photoUrl != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF8FAFC))
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
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = tpText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = EmeraldDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
