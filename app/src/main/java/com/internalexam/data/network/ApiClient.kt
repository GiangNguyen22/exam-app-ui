package com.internalexam.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {
    private val backendUrls = listOf(
        "http://127.0.0.1:8080/",
        "http://10.0.2.2:8080/",
        "http://10.0.3.2:8080/",
        "http://192.168.0.102:8080/"
    )

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    suspend fun login(request: LoginRequest): ApiResponse<LoginResponse> {
        return callBackend { it.login(request) }
    }

    suspend fun createQuestion(authorization: String, request: QuestionCreateRequest): ApiResponse<QuestionResponse> {
        return callBackend { it.createQuestion(authorization, request) }
    }

    suspend fun createExam(authorization: String, request: ExamCreateRequest): ApiResponse<ExamResponse> {
        return callBackend { it.createExam(authorization, request) }
    }

    suspend fun generateExam(authorization: String, request: ExamGenerateRequest): ApiResponse<ExamResponse> {
        return callBackend { it.generateExam(authorization, request) }
    }

    suspend fun submitExam(authorization: String, examId: Long, request: ExamSubmitRequest): ApiResponse<String> {
        return callBackend { it.submitExam(authorization, examId, request) }
    }

    suspend fun getResult(authorization: String, examId: Long): ApiResponse<ExamResultResponse> {
        return callBackend { it.getResult(authorization, examId) }
    }

    private suspend fun <T> callBackend(block: suspend (AuthApi) -> T): T {
        var lastException: IOException? = null

        for (baseUrl in backendUrls) {
            try {
                return block(createAuthApi(baseUrl))
            } catch (exception: IOException) {
                lastException = exception
            }
        }

        throw lastException ?: IOException("Cannot connect to backend")
    }

    private fun createAuthApi(baseUrl: String): AuthApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}
