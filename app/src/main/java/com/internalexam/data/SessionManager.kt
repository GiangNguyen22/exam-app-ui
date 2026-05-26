package com.internalexam.data

object SessionManager {
    var accessToken: String? = null
        private set

    fun saveToken(token: String) {
        accessToken = token
    }

    fun authorizationHeader(): String? {
        return accessToken?.let { "Bearer $it" }
    }

    fun clear() {
        accessToken = null
    }
}
