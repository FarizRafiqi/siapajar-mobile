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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.siapajar.app.domain.model.AttendanceStatus
import id.siapajar.app.theme.*

data class StudentAttendanceState(
    val id: String,
    val name: String,
    val nis: String,
    var status: AttendanceStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val students = remember {
        mutableStateListOf(
            StudentAttendanceState("1", "Ahmad Rayhan", "202601", AttendanceStatus.HADIR),
            StudentAttendanceState("2", "Aisyah Putri", "202602", AttendanceStatus.HADIR),
            StudentAttendanceState("3", "Kenzo Alvaro", "202603", AttendanceStatus.HADIR),
            StudentAttendanceState("4", "Bilqis Humaira", "202604", AttendanceStatus.IZIN),
            StudentAttendanceState("5", "Fathir Rahman", "202605", AttendanceStatus.SAKIT)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Presensi Harian", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            students.forEachIndexed { index, s ->
                                students[index] = s.copy(status = AttendanceStatus.HADIR)
                            }
                        }
                    ) {
                        Text("Semua Hadir", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CanvasBackground)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = { onSaveSuccess() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Simpan Presensi", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            // Summary Stats Pill Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val hadirCount = students.count { it.status == AttendanceStatus.HADIR }
                val izinCount = students.count { it.status == AttendanceStatus.IZIN }
                val sakitCount = students.count { it.status == AttendanceStatus.SAKIT }
                val alpaCount = students.count { it.status == AttendanceStatus.ALPA }

                StatBadge("Hadir", hadirCount, StatusHadir)
                StatBadge("Izin", izinCount, StatusIzin)
                StatBadge("Sakit", sakitCount, StatusSakit)
                StatBadge("Alpa", alpaCount, StatusAlpa)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Student Attendance List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(students) { student ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, BorderSlate, RoundedCornerShape(14.dp)),
                        color = CardSurface
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = student.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "NIS: ${student.nis}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            // Segmented Controls (H, I, S, A)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                AttendanceStatus.values().forEach { statusOption ->
                                    val isSelected = student.status == statusOption
                                    val color = when (statusOption) {
                                        AttendanceStatus.HADIR -> StatusHadir
                                        AttendanceStatus.IZIN -> StatusIzin
                                        AttendanceStatus.SAKIT -> StatusSakit
                                        AttendanceStatus.ALPA -> StatusAlpa
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) color else CanvasBackground)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) color else BorderSlate,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                val index = students.indexOf(student)
                                                if (index != -1) {
                                                    students[index] = student.copy(status = statusOption)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = statusOption.code,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else TextSecondary
                                        )
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

@Composable
private fun RowScope.StatBadge(label: String, count: Int, color: Color) {
    Surface(
        modifier = Modifier.weight(1f),
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = color)
        }
    }
}
