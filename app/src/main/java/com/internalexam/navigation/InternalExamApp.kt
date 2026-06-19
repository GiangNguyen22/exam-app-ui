package com.internalexam.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.internalexam.data.ExamAttemptStore
import com.internalexam.data.SessionManager
import com.internalexam.model.mock.Role
import com.internalexam.ui.admin.AdminAuditLogScreen
import com.internalexam.ui.admin.AdminDashboardScreen
import com.internalexam.ui.admin.UserManagementScreen
import com.internalexam.ui.auth.LoginScreen
import com.internalexam.ui.auth.SplashScreen
import com.internalexam.ui.profile.ProfileScreen
import com.internalexam.ui.student.ExamLobbyScreen
import com.internalexam.ui.student.ExamTakingScreen
import com.internalexam.ui.student.ResultScreen
import com.internalexam.ui.student.StudentExamsScreen
import com.internalexam.ui.student.StudentHomeScreen
import com.internalexam.ui.student.StudentResultsScreen
import com.internalexam.ui.student.SubmitConfirmationScreen
import com.internalexam.ui.teacher.AutoGenerateExamScreen
import com.internalexam.ui.teacher.CreateExamScreen
import com.internalexam.ui.teacher.CreateQuestionScreen
import com.internalexam.ui.teacher.EditExamScreen
import com.internalexam.ui.teacher.EditQuestionScreen
import com.internalexam.ui.teacher.ExamQuestionListScreen
import com.internalexam.ui.teacher.TeacherExamListScreen
import com.internalexam.ui.teacher.LiveMonitoringScreen
import com.internalexam.ui.teacher.QuestionBankScreen
import com.internalexam.ui.teacher.ReportDashboardScreen
import com.internalexam.ui.teacher.TeacherDashboardScreen
import com.internalexam.ui.theme.AppBg
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppSurface

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Profile = "profile"
    const val StudentHome = "student/home"
    const val StudentExams = "student/exams"
    const val Lobby = "student/lobby"
    const val Taking = "student/taking"
    const val Submit = "student/submit"
    const val Result = "student/result"
    const val Results = "student/results"
    const val TeacherDashboard = "teacher/dashboard"
    const val Questions = "teacher/questions"
    const val CreateQuestion = "teacher/questions/create"
    const val ExamQuestions = "teacher/exams/questions"
    const val EditQuestion = "teacher/questions/edit"
    const val TeacherExams = "teacher/exams"
    const val CreateExam = "teacher/exams/create"
    const val EditExam = "teacher/exams/edit"
    const val GenerateExam = "teacher/exams/generate"
    const val Monitor = "teacher/monitor"
    const val Reports = "teacher/reports"
    const val AdminDashboard = "admin/dashboard"
    const val Users = "admin/users"
    const val AuditLogs = "admin/audit-logs"
}

@Composable
fun InternalExamApp() {
    val nav = rememberNavController()
    val current = nav.currentBackStackEntryAsState().value?.destination?.route.orEmpty()
    val hideBottomBar = current == Routes.Taking || current == Routes.Submit
    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                RoleBottomBar(nav, current)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = Routes.Login,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 5 } },
            exitTransition = { fadeOut(tween(250)) },
            popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 5 } },
            popExitTransition = { fadeOut(tween(250)) + slideOutHorizontally(tween(300)) { it / 5 } }
        ) {
            composable(Routes.Splash) { SplashScreen { nav.navigate(Routes.Login) } }
            composable(Routes.Login) { LoginScreen { role -> nav.navigate(role.startRoute()) { popUpTo(Routes.Login) { inclusive = true } } } }
            composable(Routes.Profile) {
                ProfileScreen {
                    SessionManager.clear()
                    nav.navigate(Routes.Login) {
                        popUpTo(Routes.Splash) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            composable(Routes.StudentHome) {
                StudentHomeScreen(
                    onLobby = { exam ->
                        ExamAttemptStore.startNewAttempt(exam.id, exam)
                        nav.navigate(Routes.Lobby)
                    },
                    onOpenResult = { exam ->
                        ExamAttemptStore.selectExam(exam.id, exam)
                        nav.navigate(Routes.Result)
                    },
                    onSeeAllResults = { nav.navigate(Routes.Results) }
                )
            }
            composable(Routes.StudentExams) {
                StudentExamsScreen(
                    onLobby = { exam ->
                        ExamAttemptStore.startNewAttempt(exam.id, exam)
                        nav.navigate(Routes.Lobby)
                    },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Routes.Results) {
                StudentResultsScreen(
                    onOpenResult = { exam ->
                        ExamAttemptStore.selectExam(exam.id, exam)
                        nav.navigate(Routes.Result)
                    },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Routes.Lobby) {
                ExamLobbyScreen(
                    onStart = { nav.navigate(Routes.Taking) },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Routes.Taking) {
                ExamTakingScreen(
                    onSubmit = { nav.navigate(Routes.Submit) },
                    onBack = { nav.popBackStack() },
                    onAutoSubmitted = {
                        nav.navigate(Routes.Result) {
                            popUpTo(Routes.Lobby) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Routes.Submit) {
                SubmitConfirmationScreen(
                    onConfirm = {
                        nav.navigate(Routes.Result) {
                            popUpTo(Routes.Lobby) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Routes.Result) { ResultScreen { nav.popBackStack() } }
            composable(Routes.TeacherDashboard) { TeacherDashboardScreen({ nav.navigate(Routes.Questions) }, { nav.navigate(Routes.TeacherExams) }, { nav.navigate(Routes.GenerateExam) }, { nav.navigate(Routes.Monitor) }, { nav.navigate(Routes.Reports) }) }
            composable(Routes.Questions) { QuestionBankScreen({ nav.navigate(Routes.CreateQuestion) }, { nav.popBackStack() }) }
            composable(Routes.CreateQuestion) { CreateQuestionScreen { nav.popBackStack() } }
            composable(Routes.TeacherExams) {
                TeacherExamListScreen(
                    onCreateExam = { nav.navigate(Routes.CreateExam) },
                    onAddQuestion = { exam ->
                        ExamAttemptStore.setBackendExamId(exam.id, exam)
                        nav.navigate(Routes.CreateQuestion)
                    },
                    onViewQuestions = { exam ->
                        ExamAttemptStore.setBackendExamId(exam.id, exam)
                        nav.navigate(Routes.ExamQuestions)
                    },
                    onEditExam = { exam ->
                        ExamAttemptStore.setBackendExamId(exam.id, exam)
                        nav.navigate(Routes.EditExam)
                    },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Routes.ExamQuestions) {
                ExamQuestionListScreen(
                    onAddQuestion = { nav.navigate(Routes.CreateQuestion) },
                    onEditQuestion = { question ->
                        ExamAttemptStore.setSelectedQuestion(question)
                        nav.navigate(Routes.EditQuestion)
                    },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(Routes.EditQuestion) { EditQuestionScreen { nav.popBackStack() } }
            composable(Routes.CreateExam) { CreateExamScreen({ nav.navigate(Routes.GenerateExam) }, { nav.popBackStack() }) }
            composable(Routes.EditExam) { EditExamScreen { nav.popBackStack() } }
            composable(Routes.GenerateExam) { AutoGenerateExamScreen { nav.popBackStack() } }
            composable(Routes.Monitor) { LiveMonitoringScreen { nav.popBackStack() } }
            composable(Routes.Reports) { ReportDashboardScreen { nav.popBackStack() } }
            composable(Routes.AdminDashboard) {
                AdminDashboardScreen(
                    openUsers = { nav.navigate(Routes.Users) },
                    openAuditLogs = { nav.navigate(Routes.AuditLogs) }
                )
            }
            composable(Routes.Users) { UserManagementScreen { nav.popBackStack() } }
            composable(Routes.AuditLogs) { AdminAuditLogScreen { nav.popBackStack() } }
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
    val activeRole = when {
        current.startsWith("student") -> Role.STUDENT
        current.startsWith("teacher") -> Role.TEACHER
        current.startsWith("admin") -> Role.ADMIN
        current == Routes.Profile -> SessionManager.currentRole
        else -> null
    } ?: return

    val items = when (activeRole) {
        Role.STUDENT -> listOf(
            NavItem("Trang chủ", Routes.StudentHome, Icons.Default.Home),
            NavItem("Phòng thi", Routes.StudentExams, Icons.Default.Quiz),
            NavItem("Kết quả", Routes.Results, Icons.Default.Assessment),
            NavItem("Hồ sơ", Routes.Profile, Icons.Default.Person)
        )
        Role.TEACHER -> listOf(
            NavItem("Dashboard", Routes.TeacherDashboard, Icons.Default.Dashboard),
            NavItem("Questions", Routes.Questions, Icons.Default.QuestionAnswer),
            NavItem("Exams", Routes.TeacherExams, Icons.Default.Quiz),
            NavItem("Profile", Routes.Profile, Icons.Default.Person)
        )
        Role.ADMIN -> listOf(
            NavItem("Tổng quan", Routes.AdminDashboard, Icons.Default.Dashboard),
            NavItem("Tài khoản", Routes.Users, Icons.Default.Groups),
            NavItem("Hồ sơ", Routes.Profile, Icons.Default.Person)
        )
    }

    NavigationBar(
        containerColor = AppSurface,
        tonalElevation = 0.dp,
        modifier = Modifier.shadow(8.dp, spotColor = Color.Black.copy(alpha = 0.08f))
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = current == item.route,
                onClick = {
                    nav.navigate(item.route) {
                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppIndigo,
                    selectedTextColor = AppIndigo,
                    indicatorColor = AppBg,
                    unselectedIconColor = AppMuted,
                    unselectedTextColor = AppMuted
                )
            )
        }
    }
}

private data class NavItem(val label: String, val route: String, val icon: ImageVector)
