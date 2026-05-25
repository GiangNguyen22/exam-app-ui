package com.internalexam.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.internalexam.model.mock.Role
import com.internalexam.ui.admin.AdminDashboardScreen
import com.internalexam.ui.admin.UserManagementScreen
import com.internalexam.ui.auth.LoginScreen
import com.internalexam.ui.auth.SplashScreen
import com.internalexam.ui.student.*
import com.internalexam.ui.teacher.*

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val StudentHome = "student/home"
    const val Lobby = "student/lobby"
    const val Taking = "student/taking"
    const val Submit = "student/submit"
    const val Result = "student/result"
    const val TeacherDashboard = "teacher/dashboard"
    const val Questions = "teacher/questions"
    const val CreateQuestion = "teacher/questions/create"
    const val CreateExam = "teacher/exams/create"
    const val GenerateExam = "teacher/exams/generate"
    const val Monitor = "teacher/monitor"
    const val Reports = "teacher/reports"
    const val AdminDashboard = "admin/dashboard"
    const val Users = "admin/users"
}

@Composable
fun InternalExamApp() {
    val nav = rememberNavController()
    val current = nav.currentBackStackEntryAsState().value?.destination?.route.orEmpty()
    Scaffold(bottomBar = { RoleBottomBar(nav, current) }) { innerPadding ->
        NavHost(navController = nav, startDestination = Routes.Splash, modifier = Modifier.padding(innerPadding)) {
            composable(Routes.Splash) { SplashScreen { nav.navigate(Routes.Login) } }
            composable(Routes.Login) { LoginScreen { role -> nav.navigate(role.startRoute()) { popUpTo(Routes.Login) { inclusive = true } } } }
            composable(Routes.StudentHome) { StudentHomeScreen({ nav.navigate(Routes.Lobby) }, { nav.navigate(Routes.Result) }) }
            composable(Routes.Lobby) { ExamLobbyScreen({ nav.navigate(Routes.Taking) }, { nav.popBackStack() }) }
            composable(Routes.Taking) { ExamTakingScreen({ nav.navigate(Routes.Submit) }, { nav.popBackStack() }) }
            composable(Routes.Submit) { SubmitConfirmationScreen({ nav.navigate(Routes.Result) }, { nav.popBackStack() }) }
            composable(Routes.Result) { ResultScreen { nav.popBackStack() } }
            composable(Routes.TeacherDashboard) { TeacherDashboardScreen({ nav.navigate(Routes.Questions) }, { nav.navigate(Routes.CreateExam) }, { nav.navigate(Routes.GenerateExam) }, { nav.navigate(Routes.Monitor) }, { nav.navigate(Routes.Reports) }) }
            composable(Routes.Questions) { QuestionBankScreen({ nav.navigate(Routes.CreateQuestion) }, { nav.popBackStack() }) }
            composable(Routes.CreateQuestion) { CreateQuestionScreen { nav.popBackStack() } }
            composable(Routes.CreateExam) { CreateExamScreen({ nav.navigate(Routes.GenerateExam) }, { nav.popBackStack() }) }
            composable(Routes.GenerateExam) { AutoGenerateExamScreen { nav.popBackStack() } }
            composable(Routes.Monitor) { LiveMonitoringScreen { nav.popBackStack() } }
            composable(Routes.Reports) { ReportDashboardScreen { nav.popBackStack() } }
            composable(Routes.AdminDashboard) { AdminDashboardScreen { nav.navigate(Routes.Users) } }
            composable(Routes.Users) { UserManagementScreen { nav.popBackStack() } }
        }
    }
}

private fun Role.startRoute(): String = when (this) {
    Role.ADMIN -> Routes.AdminDashboard
    Role.TEACHER -> Routes.TeacherDashboard
    Role.STUDENT -> Routes.StudentHome
}

@Composable
private fun RoleBottomBar(nav: NavHostController, current: String) {
    val student = current.startsWith("student")
    val teacher = current.startsWith("teacher")
    val admin = current.startsWith("admin")
    if (!student && !teacher && !admin) return
    NavigationBar {
        when {
            student -> listOf(
                NavItem("Home", Routes.StudentHome, Icons.Default.Home),
                NavItem("Bai thi", Routes.Lobby, Icons.Default.Quiz),
                NavItem("Ket qua", Routes.Result, Icons.Default.Assessment),
                NavItem("Thong bao", Routes.StudentHome, Icons.Default.Notifications),
                NavItem("Ca nhan", Routes.StudentHome, Icons.Default.Person)
            )
            teacher -> listOf(
                NavItem("Dashboard", Routes.TeacherDashboard, Icons.Default.Dashboard),
                NavItem("Cau hoi", Routes.Questions, Icons.Default.QuestionAnswer),
                NavItem("De thi", Routes.CreateExam, Icons.Default.Quiz),
                NavItem("Theo doi", Routes.Monitor, Icons.Default.MonitorHeart),
                NavItem("Bao cao", Routes.Reports, Icons.Default.Assessment)
            )
            else -> listOf(
                NavItem("Dashboard", Routes.AdminDashboard, Icons.Default.Dashboard),
                NavItem("Nguoi dung", Routes.Users, Icons.Default.Groups),
                NavItem("Vai tro", Routes.AdminDashboard, Icons.Default.Person),
                NavItem("Audit", Routes.AdminDashboard, Icons.Default.Assessment),
                NavItem("Cau hinh", Routes.AdminDashboard, Icons.Default.AdminPanelSettings)
            )
        }.forEach { item ->
            NavigationBarItem(
                selected = current == item.route,
                onClick = { nav.navigate(item.route) { popUpTo(nav.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

private data class NavItem(val label: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
