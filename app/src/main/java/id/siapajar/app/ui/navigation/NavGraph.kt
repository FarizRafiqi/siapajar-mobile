package id.siapajar.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import id.siapajar.app.data.local.TokenManager
import id.siapajar.app.ui.assessment.QuickAssessmentScreen
import id.siapajar.app.ui.attendance.AttendanceScreen
import id.siapajar.app.ui.auth.LoginScreen
import id.siapajar.app.ui.components.SiapAjarBottomBar
import id.siapajar.app.ui.home.HomeScreen
import id.siapajar.app.ui.rppm.RppmDetailScreen
import id.siapajar.app.ui.student.StudentDetailScreen
import id.siapajar.app.ui.student.StudentListScreen
import kotlinx.coroutines.launch

@Composable
fun SiapAjarNavGraph(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager.getInstance(context) }
    val initialDestination = remember {
        if (tokenManager.isLoggedIn()) Screen.Home.route else Screen.Login.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: initialDestination
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Show bottom bar only on Home, Student List, and Student Detail
            if (currentRoute == Screen.Home.route || currentRoute == Screen.StudentList.route || currentRoute.startsWith("student_detail")) {
                SiapAjarBottomBar(
                    currentRoute = currentRoute,
                    onNavigateHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateAssessment = { mode ->
                        navController.navigate(Screen.QuickAssessment.createRoute(mode))
                    },
                    onNavigateAttendance = {
                        navController.navigate(Screen.Attendance.route)
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
            startDestination = initialDestination,
            modifier = Modifier.padding(padding)
        ) {
            // 0. Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // 1. Beranda
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateAttendance = { navController.navigate(Screen.Attendance.route) },
                    onNavigateAssessment = { navController.navigate(Screen.QuickAssessment.createRoute("camera")) },
                    onNavigateRppmDetail = { navController.navigate(Screen.RppmDetail.route) },
                    onGenerateAiSummary = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Membuat Rangkuman AI dari data asesmen minggu ini...")
                        }
                    },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // 1.1. Detail Rencana Kegiatan & RPPM (Modul Ajar Harian)
            composable(Screen.RppmDetail.route) {
                RppmDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateAssessment = { _ ->
                        navController.navigate(Screen.QuickAssessment.createRoute("camera"))
                    }
                )
            }

            // 2. Catat Asesmen Kegiatan
            composable(
                route = Screen.QuickAssessment.route,
                arguments = listOf(
                    androidx.navigation.navArgument("mode") {
                        type = androidx.navigation.NavType.StringType
                        defaultValue = "default"
                    }
                )
            ) { backStackEntry ->
                val mode = backStackEntry.arguments?.getString("mode") ?: "default"
                QuickAssessmentScreen(
                    initialMode = mode,
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccess = {
                        navController.popBackStack()
                        scope.launch {
                            snackbarHostState.showSnackbar("Asesmen berhasil disimpan!")
                        }
                    }
                )
            }

            // 3. Presensi Harian
            composable(Screen.Attendance.route) {
                AttendanceScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccess = {
                        navController.popBackStack()
                        scope.launch {
                            snackbarHostState.showSnackbar("Presensi harian berhasil disimpan!")
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
