package id.siapajar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .shadow(elevation = 12.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Beranda Tab
            BottomNavItem(
                label = "Beranda",
                icon = Icons.Outlined.Home,
                isActive = currentRoute == "home",
                onClick = onNavigateHome
            )

            // 2. Asesmen Tab
            BottomNavItem(
                label = "Asesmen",
                icon = Icons.Outlined.Assignment,
                isActive = currentRoute == "quick_assessment",
                onClick = onNavigateAssessment
            )

            // 3. Siswa Tab
            BottomNavItem(
                label = "Siswa",
                icon = Icons.Outlined.Group,
                isActive = currentRoute == "student_list" || currentRoute.startsWith("student_detail"),
                onClick = onNavigateStudents
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
    if (isActive) {
        // Active Pill Style (as shown in Stitch Screenshot 2: Filled Emerald rounded container)
        Box(
            modifier = Modifier
                .height(42.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(EmeraldPrimary)
                .clickable { onClick() }
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    } else {
        // Inactive Icon + Text Stack
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() }
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextMuted,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted
            )
        }
    }
}
