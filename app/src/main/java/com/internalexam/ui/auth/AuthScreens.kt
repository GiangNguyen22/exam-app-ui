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
import com.internalexam.model.mock.Role
import com.internalexam.ui.components.AppBackground
import com.internalexam.ui.components.GradientHero
import com.internalexam.ui.components.PrimaryAction
import com.internalexam.ui.theme.AppRed

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
                PrimaryAction("Tiep tuc", Modifier.width(220.dp), onContinue)
            }
        }
    }
}

@Composable
fun LoginScreen(onLogin: (Role) -> Unit) {
    var username by remember { mutableStateOf("SV2026001") }
    var password by remember { mutableStateOf("password") }
    var error by remember { mutableStateOf<String?>(null) }
    AppBackground {
        Spacer(Modifier.height(32.dp))
        GradientHero("Internal Exam", "Dang nhap bang MSSV, ma nhan vien hoac username")
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(username, { username = it }, modifier = Modifier.fillMaxWidth(), label = { Text("MSSV / ma nhan vien / username") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(password, { password = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Mat khau") }, singleLine = true, visualTransformation = PasswordVisualTransformation())
        if (error != null) Text(error.orEmpty(), color = AppRed, modifier = Modifier.padding(top = 10.dp))
        Spacer(Modifier.height(18.dp))
        PrimaryAction("Dang nhap Student") { if (password == "locked") error = "Tai khoan bi khoa" else onLogin(Role.STUDENT) }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { onLogin(Role.TEACHER) }, modifier = Modifier.weight(1f)) { Text("Teacher") }
            OutlinedButton(onClick = { onLogin(Role.ADMIN) }, modifier = Modifier.weight(1f)) { Text("Admin") }
        }
        TextButton(onClick = { error = "Sai mat khau hoac tai khoan khong ton tai" }) { Text("Mo phong loi dang nhap") }
    }
}
