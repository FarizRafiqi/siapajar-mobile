package id.siapajar.app.ui.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PeopleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.siapajar.app.domain.model.AttendanceStatus
import id.siapajar.app.theme.*
import id.siapajar.app.ui.components.StudentAvatar

data class StudentAttendanceState(
    val id: String,
    val name: String,
    val nis: String,
    val status: AttendanceStatus
)

@Composable
fun StatBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
    }
}

@Composable
fun AttendanceChip(
    code: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (isSelected) activeColor else CanvasBackground)
            .border(1.dp, if (isSelected) activeColor else BorderSlate, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = code,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else TextSecondary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AttendanceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val hadirCount = uiState.students.count { it.status == AttendanceStatus.HADIR }
    val sakitCount = uiState.students.count { it.status == AttendanceStatus.SAKIT }
    val izinCount = uiState.students.count { it.status == AttendanceStatus.IZIN }
    val alpaCount = uiState.students.count { it.status == AttendanceStatus.ALPA }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Presensi Harian", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
                    }
                },
                actions = {
                    if (uiState.students.isNotEmpty()) {
                        TextButton(onClick = { viewModel.markAllPresent() }) {
                            Text("Semua Hadir", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CanvasBackground)
            )
        },
        bottomBar = {
            if (uiState.students.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Transparent
                ) {
                    Button(
                        onClick = { viewModel.submitAttendance(onSaveSuccess) },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Simpan Presensi", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CanvasBackground)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Date Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(uiState.currentDateText, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
            }

            // Summary Stats Pill Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSurface)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatBadge("Hadir", hadirCount.toString(), EmeraldDark)
                StatBadge("Sakit", sakitCount.toString(), Color(0xFF2563EB))
                StatBadge("Izin", izinCount.toString(), AmberAccent)
                StatBadge("Alpa", alpaCount.toString(), Color(0xFFDC2626))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Student Attendance List / Empty State
            if (uiState.students.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
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
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(MintSurface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PeopleOutline,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Belum Ada Data Siswa",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Data siswa untuk presensi kelas ini belum tersedia atau belum ditambahkan.",
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(uiState.students) { student ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, BorderSlate, RoundedCornerShape(14.dp)),
                            color = CardSurface
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StudentAvatar(
                                        name = student.name,
                                        size = 38.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = student.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "NIS: ${student.nis}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                // Status Options: H, S, I, A
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    AttendanceChip("H", student.status == AttendanceStatus.HADIR, EmeraldPrimary) {
                                        viewModel.updateStatus(student.id, AttendanceStatus.HADIR)
                                    }
                                    AttendanceChip("S", student.status == AttendanceStatus.SAKIT, Color(0xFF2563EB)) {
                                        viewModel.updateStatus(student.id, AttendanceStatus.SAKIT)
                                    }
                                    AttendanceChip("I", student.status == AttendanceStatus.IZIN, AmberAccent) {
                                        viewModel.updateStatus(student.id, AttendanceStatus.IZIN)
                                    }
                                    AttendanceChip("A", student.status == AttendanceStatus.ALPA, Color(0xFFDC2626)) {
                                        viewModel.updateStatus(student.id, AttendanceStatus.ALPA)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
