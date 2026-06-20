package com.internalexam.data.auth

import com.internalexam.data.network.UserProfileResponse
import com.internalexam.model.mock.Role

fun List<String>?.toAppRole(): Role? {
    val normalized = this.orEmpty().map { role ->
        role.removePrefix("ROLE_").uppercase()
    }.toSet()

    return when {
        "ADMIN" in normalized -> Role.ADMIN
        "TEACHER" in normalized -> Role.TEACHER
        "STUDENT" in normalized -> Role.STUDENT
        else -> null
    }
}

fun UserProfileResponse.primaryRole(): Role? {
    return roles.toAppRole()
}

fun UserProfileResponse.roleSet(): Set<Role> {
    return roles.orEmpty().mapNotNull { role ->
        when (role.removePrefix("ROLE_").uppercase()) {
            "ADMIN" -> Role.ADMIN
            "TEACHER" -> Role.TEACHER
            "STUDENT" -> Role.STUDENT
            else -> null
        }
    }.toSet()
}

fun Role.backendName(): String = when (this) {
    Role.ADMIN -> "ADMIN"
    Role.TEACHER -> "TEACHER"
    Role.STUDENT -> "STUDENT"
}
