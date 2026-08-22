package id.siapajar.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import id.siapajar.app.ui.assessment.QuickAssessmentScreen
import id.siapajar.app.ui.attendance.AttendanceScreen
import id.siapajar.app.ui.components.SiapAjarBottomBar
import id.siapajar.app.ui.home.HomeScreen
import id.siapajar.app.ui.student.StudentDetailScreen
import id.siapajar.app.ui.student.StudentListScreen
import kotlinx.coroutines.launch

@Composable
fun SiapAjarNavGraph(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Show bottom bar on Home, Student List, and Student Detail
            if (currentRoute == Screen.Home.route || currentRoute == Screen.StudentList.route || currentRoute.startsWith("student_detail")) {
                SiapAjarBottomBar(
                    currentRoute = currentRoute,
                    onNavigateHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateAssessment = {
                        navController.navigate(Screen.QuickAssessment.route)
                    },
                    onNavigateStudents = {
                        navController.navigate(Screen.StudentList.route) {
                            popUpTo(Screen.StudentList.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            // 1. Beranda
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateAttendance = { navController.navigate(Screen.Attendance.route) },
                    onNavigateAssessment = { navController.navigate(Screen.QuickAssessment.route) },
                    onGenerateAiSummary = {
                        scope.launch {
                            snackbarHostState.showSnackbar("⚡ Membuat Rangkuman AI dari data asesmen minggu ini...")
                        }
                    }
                )
            }

            // 2. Catat Asesmen Kegiatan
            composable(Screen.QuickAssessment.route) {
                QuickAssessmentScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccess = {
                        navController.popBackStack()
                        scope.launch {
                            snackbarHostState.showSnackbar("✅ Asesmen tersimpan secara lokal (Draft Offline)")
                        }
                    }
                )
            }

            // 3. Presensi Harian 30 Detik
            composable(Screen.Attendance.route) {
                AttendanceScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccess = {
                        navController.popBackStack()
                        scope.launch {
                            snackbarHostState.showSnackbar("✅ Presensi berhasil dicatat!")
                        }
                    }
                )
            }

            // 4. Daftar Siswa
            composable(Screen.StudentList.route) {
                StudentListScreen(
                    onSelectStudent = { studentId ->
                        navController.navigate(Screen.StudentDetail.createRoute(studentId))
                    }
                )
            }

            // 5. Profil & Portofolio Siswa (Detail)
            composable(Screen.StudentDetail.route) { backStackEntry ->
                val studentId = backStackEntry.arguments?.getString("studentId") ?: "1"
                StudentDetailScreen(
                    studentId = studentId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateAddAssessment = {
                        navController.navigate(Screen.QuickAssessment.route)
                    }
                )
            }
        }
    }
}
