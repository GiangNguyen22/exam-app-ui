package com.internalexam.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.internalexam.data.SessionManager
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.LoginRequest
import com.internalexam.model.mock.Role
import com.internalexam.ui.components.AppBackground
import com.internalexam.ui.components.GradientHero
import com.internalexam.ui.components.PrimaryAction
import com.internalexam.ui.theme.AppRed
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun SplashScreen(onContinue: () -> Unit) {
    AppBackground {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.primary) {
                    Icon(Icons.Default.School, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(22.dp).size(56.dp))
                }
                Spacer(Modifier.height(18.dp))
                Text("Internal Exam", style = MaterialTheme.typography.headlineLarge)
                Text("Secure mobile testing", color = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.height(28.dp))
                CircularProgressIndicator()
                Spacer(Modifier.height(24.dp))
                PrimaryAction("Continue", Modifier.width(220.dp), onContinue)
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (Role) -> Unit) {
    var username by remember { mutableStateOf("student1") }
    var password by remember { mutableStateOf("student123") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loginAs(role: Role) {
        if (username.isBlank() || password.isBlank()) {
            error = "Please enter your username and password"
            return
        }

        scope.launch {
            isLoading = true
            error = null
            try {
                val response = ApiClient.login(LoginRequest(username.trim(), password))
                val token = response.data?.accessToken
                if (response.success && !token.isNullOrBlank()) {
                    SessionManager.saveToken(token)
                    onLogin(role)
                } else {
                    error = response.message.ifBlank { "Sign in failed" }
                }
            } catch (exception: HttpException) {
                error = when (exception.code()) {
                    401, 403 -> "Invalid username or password"
                    else -> "Backend returned error ${exception.code()}"
                }
            } catch (exception: Exception) {
                error = "Cannot connect to the backend. Check that the server is running."
            } finally {
                isLoading = false
            }
        }
    }

    AppBackground {
        Spacer(Modifier.height(32.dp))
        GradientHero("Internal Exam", "Sign in with your student ID, employee ID, or username")
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(username, { username = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Username / student ID / employee ID") }, singleLine = true, enabled = !isLoading)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(password, { password = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), enabled = !isLoading)
        if (error != null) Text(error.orEmpty(), color = AppRed, modifier = Modifier.padding(top = 10.dp))
        Spacer(Modifier.height(18.dp))
        PrimaryAction(if (isLoading) "Signing in..." else "Sign in as Student") {
            if (!isLoading) loginAs(Role.STUDENT)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { loginAs(Role.TEACHER) }, modifier = Modifier.weight(1f), enabled = !isLoading) { Text("Teacher") }
            OutlinedButton(onClick = { loginAs(Role.ADMIN) }, modifier = Modifier.weight(1f), enabled = !isLoading) { Text("Admin") }
        }
    }
}
