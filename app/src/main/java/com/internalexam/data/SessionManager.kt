package com.internalexam.data

import android.content.Context
import com.internalexam.model.mock.Role

object SessionManager {
    private const val PREFS_NAME = "internal_exam_session"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_CURRENT_ROLE = "current_role"

    private var prefsInitialized = false
    private lateinit var prefs: android.content.SharedPreferences

    var accessToken: String? = null
        private set

    var currentRole: Role? = null
        private set

    fun initialize(context: Context) {
        if (prefsInitialized) return
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        accessToken = prefs.getString(KEY_ACCESS_TOKEN, null)
        currentRole = prefs.getString(KEY_CURRENT_ROLE, null)?.toRole()
        prefsInitialized = true
    }

    fun saveSession(token: String, role: Role) {
        accessToken = token
        currentRole = role
        ensureInitialized()
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .putString(KEY_CURRENT_ROLE, role.name)
            .apply()
    }

    fun saveToken(token: String) {
        accessToken = token
        ensureInitialized()
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .apply()
    }

    fun authorizationHeader(): String? {
        return accessToken?.let { "Bearer $it" }
    }

    fun hasActiveSession(): Boolean {
        return !accessToken.isNullOrBlank() && currentRole != null
    }

    fun clear() {
        accessToken = null
        currentRole = null
        if (prefsInitialized) {
            prefs.edit().clear().apply()
        }
    }

    private fun ensureInitialized() {
        check(prefsInitialized) {
            "SessionManager must be initialized before use"
        }
    }

    private fun String.toRole(): Role? {
        return runCatching { Role.valueOf(trim().uppercase()) }.getOrNull()
    }
}
