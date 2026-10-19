package com.internalexam.ui.admin

import androidx.compose.ui.graphics.vector.ImageVector
import com.internalexam.data.network.RoleResponse
import com.internalexam.data.network.StudentGroupResponse
import com.internalexam.data.network.UserProfileResponse
import com.internalexam.model.mock.Role

data class AdminAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

fun Role.label(): String {
    return when (this) {
        Role.ADMIN -> "Quản trị viên"
        Role.TEACHER -> "Giáo viên"
        Role.STUDENT -> "Học sinh"
    }
}

fun RoleResponse.displayRoleName(): String {
    return when (code) {
        "ROLE_ADMIN" -> "Quản trị viên"
        "ROLE_TEACHER" -> "Giáo viên"
        "ROLE_STUDENT" -> "Học sinh"
        else -> name
    }
}

fun String.permissionGroup(): String {
    return this.substringBefore(":").uppercase()
}

fun String.permissionGroupLabel(): String {
    return when (this.permissionGroup()) {
        "USER" -> "Quản lý người dùng"
        "EXAM" -> "Quản lý đề thi"
        "QUESTION" -> "Quản lý câu hỏi"
        "REPORT" -> "Thống kê báo cáo"
        "AUDIT" -> "Nhật ký hệ thống"
        else -> this.permissionGroup()
    }
}

fun UserProfileResponse.displayName(): String {
    return fullName.takeIf { it.isNotBlank() } ?: username
}

fun UserProfileResponse.identityCode(): String {
    val roleStr = roles.joinToString()
    return if (roleStr.contains("TEACHER")) teacherCode.orEmpty()
    else if (roleStr.contains("STUDENT")) studentCode.orEmpty()
    else ""
}

fun UserProfileResponse.statusLabel(): String {
    return if (locked) "Đã khóa" else "Đang hoạt động"
}

fun formatAuditTime(value: String?): String {
    return value?.takeIf { it.isNotBlank() }?.replace("T", " ")?.take(19) ?: "--"
}

fun String.isValidEmailLike(): Boolean {
    val trimmed = this.trim()
    return trimmed.contains("@") && trimmed.indexOf("@") > 0 && trimmed.indexOf("@") < trimmed.length - 1
}

fun String.blankToNull(): String? {
    return this.trim().ifBlank { null }
}

fun StudentGroupResponse.displayInfo(): String {
    return "$name - ${code ?: "NoCode"} (${studentCount ?: 0} HS)"
}
