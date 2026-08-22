package id.siapajar.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object QuickAssessment : Screen("quick_assessment")
    object Attendance : Screen("attendance")
    object StudentDetail : Screen("student_detail/{studentId}") {
        fun createRoute(studentId: String) = "student_detail/$studentId"
    }
}
