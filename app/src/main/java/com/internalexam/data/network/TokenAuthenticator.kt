package com.internalexam.data.network

import com.google.gson.Gson
import com.internalexam.data.SessionManager
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.TimeUnit

class TokenAuthenticator : Authenticator {

    private val refreshHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 3) {
            return null
        }

        synchronized(this) {
            val currentToken = SessionManager.accessToken ?: return null
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()

            if (requestToken != null && requestToken != currentToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val currentRefreshToken = SessionManager.refreshToken ?: run {
                SessionManager.clearSession()
                return null
            }

            val refreshUrl = response.request.url.newBuilder()
                .encodedPath("/api/auth/refresh")
                .query(null)
                .build()

            val requestBody = "{\"refreshToken\":\"$currentRefreshToken\"}"
                .toRequestBody("application/json; charset=utf-8".toMediaType())

            val refreshRequest = Request.Builder()
                .url(refreshUrl)
                .post(requestBody)
                .build()

            try {
                refreshHttpClient.newCall(refreshRequest).execute().use { refreshResponse ->
                    if (refreshResponse.isSuccessful) {
                        val bodyString = refreshResponse.body?.string()
                        if (bodyString != null) {
                            val apiResponse = gson.fromJson(bodyString, ApiResponse::class.java)
                            if (apiResponse != null && apiResponse.success && apiResponse.data != null) {
                                val dataMap = apiResponse.data as? Map<*, *>
                                val newAccessToken = dataMap?.get("accessToken") as? String
                                val newRefreshToken = dataMap?.get("refreshToken") as? String

                                if (!newAccessToken.isNullOrBlank() && !newRefreshToken.isNullOrBlank()) {
                                    SessionManager.updateTokens(newAccessToken, newRefreshToken)
                                    return response.request.newBuilder()
                                        .header("Authorization", "Bearer $newAccessToken")
                                        .build()
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Network or parsing failure
            }

            SessionManager.clearSession()
            return null
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
