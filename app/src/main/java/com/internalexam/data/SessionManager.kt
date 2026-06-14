package com.internalexam.data

import com.internalexam.model.mock.Role

object SessionManager {
    var accessToken: String? = null
        private set

    var currentRole: Role? = null
        private set

    fun saveSession(token: String, role: Role) {
        accessToken = token
        currentRole = role
    }

    fun saveToken(token: String) {
        accessToken = token
    }

    fun authorizationHeader(): String? {
        return accessToken?.let { "Bearer $it" }
    }

    fun clear() {
        accessToken = null
        currentRole = null
    }
}