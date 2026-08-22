package id.siapajar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.siapajar.app.theme.*

@Composable
fun SiapAjarBottomBar(
    currentRoute: String,
    onNavigateHome: () -> Unit,
    onNavigateAssessment: () -> Unit,
    onNavigateStudents: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bottom Bar Background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(elevation = 8.dp),
            color = CardSurface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Beranda
                val isHomeActive = currentRoute == "home"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onNavigateHome() }
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Beranda",
                        tint = if (isHomeActive) EmeraldPrimary else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Beranda",
                        fontSize = 12.sp,
                        fontWeight = if (isHomeActive) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isHomeActive) EmeraldPrimary else TextMuted
                    )
                }

                Spacer(modifier = Modifier.width(72.dp)) // Space for Central FAB

                // Right: Siswa
                val isStudentsActive = currentRoute.startsWith("student")
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onNavigateStudents() }
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Siswa",
                        tint = if (isStudentsActive) EmeraldPrimary else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Siswa",
                        fontSize = 12.sp,
                        fontWeight = if (isStudentsActive) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isStudentsActive) EmeraldPrimary else TextMuted
                    )
                }
            }
        }

        // Central Elevated FAB: Asesmen
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-4).dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(EmeraldPrimary)
                    .clickable { onNavigateAssessment() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Asesmen Foto",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = "Asesmen",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = EmeraldPrimary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
