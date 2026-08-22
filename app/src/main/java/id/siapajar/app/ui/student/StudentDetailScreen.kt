package id.siapajar.app.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.siapajar.app.theme.*

data class StudentTimelineItem(
    val weekText: String,
    val dateText: String,
    val instrumentTitle: String,
    val observationNote: String,
    val tpCode: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    studentId: String,
    onNavigateBack: () -> Unit
) {
    val timelineItems = listOf(
        StudentTimelineItem(
            weekText = "Minggu 3 • 22 Agu",
            dateText = "22 Agustus 2026",
            instrumentTitle = "Hasil Karya: Melukis Daun Mint",
            observationNote = "Kenzo mampu mengamati tekstur daun mint dan melukis pola tulang daun dengan detail menggunakan jari.",
            tpCode = "TP 1.3"
        ),
        StudentTimelineItem(
            weekText = "Minggu 2 • 15 Agu",
            dateText = "15 Agustus 2026",
            instrumentTitle = "Catatan Anekdot: Berbagi Alat Gambar",
            observationNote = "Saat kegiatan seni, Kenzo spontan meminjamkan kuas miliknya kepada Aisyah yang sedang mencari kuas tambahan.",
            tpCode = "TP 2.1"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portofolio Siswa", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CanvasBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CanvasBackground)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Student Profile Header Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
                color = CardSurface
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(MintSurface, CircleShape)
                            .border(1.dp, EmeraldLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Kenzo Alvaro",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "NIS: 202603 • Kelas TK B1",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Surface(
                            color = MintSurface,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "8 Catatan Semester Ini",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Linimasa Observasi & Asesmen",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Timeline items
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(timelineItems) { item ->
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
                                    color = MintSurface,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.weekText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EmeraldDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.tpCode,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.instrumentTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.observationNote,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
