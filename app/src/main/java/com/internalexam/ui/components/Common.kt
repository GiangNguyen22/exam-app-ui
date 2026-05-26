package com.internalexam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internalexam.model.mock.NetworkState
import com.internalexam.ui.theme.*

@Composable
fun AppBackground(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(horizontal = 18.dp),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamTopBar(title: String, onBack: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(Modifier.padding(top = 16.dp, bottom = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (subtitle != null) Text(subtitle, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun GradientHero(title: String, subtitle: String, trailing: @Composable (() -> Unit)? = null) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppIndigo),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, color = Color.White, style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(6.dp))
                Text(subtitle, color = Color.White.copy(alpha = .82f))
            }
            trailing?.invoke()
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, label: String, color: Color = AppBlue, icon: ImageVector = Icons.Default.CheckCircle) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(color.copy(alpha = .12f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = AppMuted)
                Text(value, style = MaterialTheme.typography.titleLarge)
                Text(label, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun StatusPill(state: NetworkState) {
    val (text, color, icon) = when (state) {
        NetworkState.ONLINE -> Triple("Online", AppMint, Icons.Default.Wifi)
        NetworkState.OFFLINE -> Triple("Offline", AppRed, Icons.Default.CloudOff)
        NetworkState.SYNCING -> Triple("Syncing", AppAmber, Icons.Default.Sync)
        NetworkState.SYNCED -> Triple("Synced", AppMint, Icons.Default.CloudDone)
    }
    Surface(color = color.copy(alpha = .12f), shape = CircleShape) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = color)
            Spacer(Modifier.width(6.dp))
            Text(text, color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PrimaryAction(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = AppIndigo, contentColor = Color.White)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ChipText(text: String, color: Color = AppBlue) {
    Surface(color = color.copy(alpha = .1f), shape = CircleShape) {
        Text(text, color = color, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun ScreenList(content: @Composable () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(bottom = 96.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { content() } }
}
