package com.internalexam.ui.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internalexam.data.SessionManager
import com.internalexam.data.auth.toAppRole
import com.internalexam.data.network.ApiClient
import com.internalexam.data.network.LoginRequest
import com.internalexam.model.mock.Role
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppSurface
import com.internalexam.ui.theme.ButtonGradient
import com.internalexam.ui.theme.HeroGradient
import com.internalexam.ui.theme.SplashGradient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun SplashScreen(onContinue: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
        delay(2200)
        val authorization = SessionManager.authorizationHeader()
        if (authorization != null && SessionManager.hasActiveSession()) {
            runCatching { ApiClient.getCurrentUser(authorization) }
                .onFailure { exception ->
                    if (exception is HttpException && exception.code() in listOf(401, 403)) {
                        SessionManager.clear()
                    }
                }
        }
        onContinue()
    }

    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "logoScale"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 400),
        label = "textAlpha"
    )
    val pulse = rememberInfiniteTransition(label = "pulse")
    val ringScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "ring"
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(SplashGradient),
        contentAlignment = Alignment.Center
    ) {
        // Decorative circles
        Box(
            Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-200).dp)
                .background(Color.White.copy(alpha = 0.04f), CircleShape)
        )
        Box(
            Modifier
                .size(200.dp)
                .offset(x = 120.dp, y = 250.dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .size(110.dp)
                        .scale(ringScale)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                )
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.scale(logoScale)
                ) {
                    Icon(
                        Icons.Default.School, null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(24.dp)
                            .size(56.dp)
                    )
                }
            }
            Spacer(Modifier.height(28.dp))
            Text(
                "Internal Exam",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                modifier = Modifier.alpha(textAlpha)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Secure Mobile Testing",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.alpha(textAlpha)
            )
            Spacer(Modifier.height(40.dp))
            CircularProgressIndicator(
                color = Color.White.copy(alpha = 0.7f),
                strokeWidth = 2.dp,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun LoginScreen(onLogin: (Role) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun login() {
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
                val role = response.data?.roles.toAppRole()
                if (response.success && !token.isNullOrBlank() && role != null) {
                    SessionManager.saveSession(token, role)
                    onLogin(role)
                } else if (response.success && !token.isNullOrBlank()) {
                    error = "Your account has no app role assigned"
                } else {
                    error = response.message.ifBlank { "Sign in failed" }
                }
            } catch (exception: HttpException) {
                error = when (exception.code()) {
                    401, 403 -> "Invalid username or password"
                    else -> "Sign in failed. Please try again."
                }
            } catch (exception: Exception) {
                error = "Cannot sign in right now. Please try again later."
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F4FF))
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(HeroGradient)
        ) {
            Box(
                Modifier
                    .size(150.dp)
                    .offset(x = 280.dp, y = (-30).dp)
                    .background(Color.White.copy(alpha = 0.06f), CircleShape)
            )
            Column(
                Modifier
                    .padding(24.dp)
                    .padding(top = 40.dp)
            ) {
                Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.15f)) {
                    Icon(
                        Icons.Default.School, null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(28.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text("Welcome back", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyLarge)
                Text("Internal Exam", color = Color.White, style = MaterialTheme.typography.headlineLarge)
            }
        }

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = 200.dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("Sign In", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text("Enter your credentials to continue", color = AppMuted, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; error = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Username / Student ID") },
                    singleLine = true,
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; error = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    singleLine = true,
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.medium,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password"
                            )
                        }
                    }
                )

                if (error != null) {
                    Spacer(Modifier.height(10.dp))
                    Surface(color = AppRed.copy(alpha = 0.08f), shape = MaterialTheme.shapes.small) {
                        Text(error.orEmpty(), color = AppRed, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = { if (!isLoading) login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                    contentPadding = PaddingValues(),
                    enabled = !isLoading
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                if (isLoading) Brush.horizontalGradient(listOf(AppMuted, AppMuted)) else ButtonGradient,
                                MaterialTheme.shapes.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Signing in...", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}
