package id.siapajar.app.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object QuickAssessment : Screen("quick_assessment")
    object Attendance : Screen("attendance")
    object StudentList : Screen("student_list")
    object StudentDetail : Screen("student_detail/{studentId}") {
        fun createRoute(studentId: String) = "student_detail/$studentId"
    }
}
