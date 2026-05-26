package com.internalexam.model.mock

enum class Role { ADMIN, TEACHER, STUDENT }
enum class NetworkState { ONLINE, OFFLINE, SYNCING, SYNCED }
enum class QuestionType { SINGLE, MULTI, TRUE_FALSE, FILL_BLANK }
enum class Difficulty { EASY, MEDIUM, HARD }
enum class UserStatus { ACTIVE, LOCKED, PENDING }
enum class CandidateStatus { DOING, LOST_CONNECTION, SUBMITTED, FLAGGED }

data class User(val id: String, val name: String, val role: Role, val status: UserStatus, val code: String)
data class Exam(val id: String, val title: String, val subject: String, val duration: Int, val questions: Int, val opens: String, val closes: String, val downloaded: Boolean)
data class Answer(val id: String, val text: String, val correct: Boolean = false)
data class Question(val id: Int, val content: String, val subject: String, val topic: String, val difficulty: Difficulty, val type: QuestionType, val answers: List<Answer>, val explanation: String)
data class Result(val exam: String, val score: Double, val status: String, val correct: Int, val wrong: Int, val blank: Int)
data class Candidate(val name: String, val status: CandidateStatus, val progress: Int, val device: String)
data class AuditLog(val time: String, val actor: String, val action: String)

object MockData {
    val currentStudent = User("u1", "Nguyen Minh Anh", Role.STUDENT, UserStatus.ACTIVE, "SV2026001")
    val exams = listOf(
        Exam("e1", "Kotlin Fundamentals Test", "Android Programming", 45, 30, "19/05 08:00", "19/05 10:00", true),
        Exam("e2", "Internal Security Assessment", "Security", 60, 40, "20/05 13:30", "20/05 15:00", false)
    )
    val questions = listOf(
        Question(1, "In Kotlin, which keyword declares a read-only variable?", "Android", "Kotlin", Difficulty.EASY, QuestionType.SINGLE, listOf(
            Answer("A", "var"), Answer("B", "val", true), Answer("C", "let"), Answer("D", "const")
        ), "val creates a reference that can only be assigned once."),
        Question(2, "Which concepts are used to manage UI from state in Jetpack Compose?", "Android", "Compose", Difficulty.MEDIUM, QuestionType.MULTI, listOf(
            Answer("A", "Composable functions", true), Answer("B", "State hoisting", true), Answer("C", "XML inflater"), Answer("D", "RecyclerView adapter")
        ), "Compose renders UI from composable functions and observable state."),
        Question(3, "Material 3 supports dynamic color on Android 12 and later.", "Android", "UI", Difficulty.EASY, QuestionType.TRUE_FALSE, listOf(
            Answer("A", "True", true), Answer("B", "False")
        ), "Material You introduced dynamic color for supported Android versions.")
    )
    val results = listOf(
        Result("Git Assessment", 8.5, "Graded", 17, 2, 1),
        Result("Android Basics", 7.0, "Graded", 14, 4, 2),
        Result("Application Security", 0.0, "Pending review", 0, 0, 30)
    )
    val users = listOf(
        User("a1", "System Administrator", Role.ADMIN, UserStatus.ACTIVE, "AD001"),
        User("t1", "Teacher One", Role.TEACHER, UserStatus.ACTIVE, "GV010"),
        User("s1", "Pham Bao Long", Role.STUDENT, UserStatus.LOCKED, "SV021"),
        User("s2", "Do Gia Han", Role.STUDENT, UserStatus.PENDING, "SV022")
    )
    val candidates = listOf(
        Candidate("Nguyen Minh Anh", CandidateStatus.DOING, 68, "Pixel 7"),
        Candidate("Do Gia Han", CandidateStatus.LOST_CONNECTION, 42, "Samsung A52"),
        Candidate("Pham Bao Long", CandidateStatus.FLAGGED, 55, "Oppo Reno"),
        Candidate("Tran Duc", CandidateStatus.SUBMITTED, 100, "Xiaomi 13")
    )
    val auditLogs = listOf(
        AuditLog("10:31", "SV2026001", "APP_EXIT"),
        AuditLog("10:32", "SV2026002", "LOST_CONNECTION"),
        AuditLog("10:33", "SV2026003", "SCREENSHOT"),
        AuditLog("10:35", "SV2026001", "FOCUS_LOST")
    )
}
