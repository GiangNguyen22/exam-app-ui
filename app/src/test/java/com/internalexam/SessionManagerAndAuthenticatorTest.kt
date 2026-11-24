package com.internalexam

import android.content.Context
import android.content.SharedPreferences
import com.internalexam.data.SessionManager
import com.internalexam.data.network.TokenAuthenticator
import com.internalexam.model.mock.Role
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

class SessionManagerAndAuthenticatorTest {

    private class FakeSharedPreferences : SharedPreferences {
        val map = ConcurrentHashMap<String, Any>()

        override fun getAll(): MutableMap<String, *> = map
        override fun getString(key: String?, defValue: String?): String? = map[key] as? String ?: defValue
        override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? = null
        override fun getInt(key: String?, defValue: Int): Int = map[key] as? Int ?: defValue
        override fun getLong(key: String?, defValue: Long): Long = map[key] as? Long ?: defValue
        override fun getFloat(key: String?, defValue: Float): Float = map[key] as? Float ?: defValue
        override fun getBoolean(key: String?, defValue: Boolean): Boolean = map[key] as? Boolean ?: defValue
        override fun contains(key: String?): Boolean = map.containsKey(key)
        override fun edit(): SharedPreferences.Editor = FakeEditor(map)
        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

        class FakeEditor(private val target: ConcurrentHashMap<String, Any>) : SharedPreferences.Editor {
            private val temp = mutableMapOf<String, Any?>()
            private var clearFlag = false

            override fun putString(key: String?, value: String?): SharedPreferences.Editor {
                if (key != null) temp[key] = value
                return this
            }
            override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor = this
            override fun putInt(key: String?, value: Int): SharedPreferences.Editor = this
            override fun putLong(key: String?, value: Long): SharedPreferences.Editor = this
            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = this
            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = this
            override fun remove(key: String?): SharedPreferences.Editor {
                if (key != null) temp[key] = null
                return this
            }
            override fun clear(): SharedPreferences.Editor {
                clearFlag = true
                return this
            }
            override fun commit(): Boolean {
                apply()
                return true
            }
            override fun apply() {
                if (clearFlag) {
                    target.clear()
                }
                for ((k, v) in temp) {
                    if (v == null) target.remove(k) else target[k] = v
                }
            }
        }
    }

    private val fakePrefs = FakeSharedPreferences()

    @Before
    fun setUp() {
        SessionManager.resetForTesting(fakePrefs)
    }

    @Test
    fun testSaveSessionWithRefreshToken() {
        val accessToken = "access-token-123"
        val refreshToken = "refresh-token-xyz"
        val role = Role.STUDENT

        SessionManager.saveSession(accessToken, refreshToken, role)

        assertEquals(accessToken, SessionManager.accessToken)
        assertEquals(refreshToken, SessionManager.refreshToken)
        assertEquals(role, SessionManager.currentRole)

        // Clear session removes all
        SessionManager.clearSession()
        assertNull(SessionManager.accessToken)
        assertNull(SessionManager.refreshToken)
        assertNull(SessionManager.currentRole)
    }

    @Test
    fun testUpdateTokens() {
        SessionManager.saveSession("old-access", "old-refresh", Role.TEACHER)
        assertEquals("old-access", SessionManager.accessToken)

        SessionManager.updateTokens("new-access-456", "new-refresh-789")
        assertEquals("new-access-456", SessionManager.accessToken)
        assertEquals("new-refresh-789", SessionManager.refreshToken)
    }

    @Test
    fun testAuthenticatorConcurrentRetryWhenTokenAlreadyUpdated() {
        SessionManager.saveSession("new-token-from-concurrent-thread", "some-refresh", Role.STUDENT)

        val authenticator = TokenAuthenticator()
        val dummyRequest = Request.Builder()
            .url("http://127.0.0.1:8080/api/questions")
            .header("Authorization", "Bearer old-stale-token")
            .build()

        val response = Response.Builder()
            .request(dummyRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()

        val retryRequest = authenticator.authenticate(null, response)

        assertNotNull(retryRequest)
        assertEquals("Bearer new-token-from-concurrent-thread", retryRequest?.header("Authorization"))
    }
}
