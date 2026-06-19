package com.internalexam.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internalexam.model.mock.NetworkState
import com.internalexam.ui.theme.AppAmber
import com.internalexam.ui.theme.AppBlue
import com.internalexam.ui.theme.AppCardBorder
import com.internalexam.ui.theme.AppIndigo
import com.internalexam.ui.theme.AppMint
import com.internalexam.ui.theme.AppMuted
import com.internalexam.ui.theme.AppRed
import com.internalexam.ui.theme.AppSurface
import com.internalexam.ui.theme.AppText
import com.internalexam.ui.theme.BgGradient
import com.internalexam.ui.theme.ButtonGradient
import com.internalexam.ui.theme.HeroGradient

val ScreenBottomPadding = 24.dp

/* ── Background ── */
@Composable
fun AppBackground(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGradient)
            .padding(horizontal = 18.dp),
        content = content
    )
}

/* ── Top bar ── */
@Composable
fun LoadingStateCard(message: String = "Loading...") {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = AppIndigo
            )
            Spacer(Modifier.width(14.dp))
            Text(message, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamTopBar(title: String, onBack: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (onBack != null) IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

/* ── Section heading with accent bar ── */
@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(Modifier.padding(top = 18.dp, bottom = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(CircleShape)
                    .background(HeroGradient)
            )
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
        if (subtitle != null) {
            Text(
                subtitle,
                color = AppMuted,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

/* ── Hero banner with real gradient ── */
@Composable
fun GradientHero(title: String, subtitle: String, trailing: @Composable (() -> Unit)? = null) {
    Card(
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, MaterialTheme.shapes.large, spotColor = AppIndigo.copy(alpha = 0.25f))
    ) {
        Box(
            Modifier
                .background(HeroGradient)
                .fillMaxWidth()
        ) {
            Box(
                Modifier
                    .size(100.dp)
                    .offset(x = 260.dp, y = (-20).dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            )
            Row(
                Modifier.padding(22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(title, color = Color.White, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(subtitle, color = Color.White.copy(alpha = .82f), style = MaterialTheme.typography.bodyMedium)
                }
                trailing?.invoke()
            }
        }
    }
}

/* ── Metric card with border ── */
@Composable
fun MetricCard(
    title: String,
    value: String,
    label: String,
    color: Color = AppBlue,
    icon: ImageVector = Icons.Default.CheckCircle
) {
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AppCardBorder, MaterialTheme.shapes.large)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = .10f), MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = AppMuted, style = MaterialTheme.typography.labelMedium)
                Text(value, style = MaterialTheme.typography.titleLarge, color = color)
                Text(label, color = AppMuted, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/* ── Network status pill with pulse ── */
@Composable
fun StatusPill(state: NetworkState) {
    val (text, color, icon) = when (state) {
        NetworkState.ONLINE -> Triple("Trực tuyến", AppMint, Icons.Default.Wifi)
        NetworkState.OFFLINE -> Triple("Mất kết nối", AppRed, Icons.Default.CloudOff)
        NetworkState.SYNCING -> Triple("Đang tải", AppAmber, Icons.Default.Sync)
        NetworkState.SYNCED -> Triple("Sẵn sàng", AppMint, Icons.Default.CloudDone)
    }
    val alpha = if (state == NetworkState.SYNCING) {
        val transition = rememberInfiniteTransition(label = "pulse")
        transition.animateFloat(
            initialValue = 0.08f,
            targetValue = 0.22f,
            animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
            label = "pillAlpha"
        ).value
    } else 0.12f

    Surface(
        color = color.copy(alpha = alpha),
        shape = CircleShape,
        modifier = Modifier.border(1.dp, color.copy(alpha = 0.25f), CircleShape)
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = color)
            Spacer(Modifier.width(6.dp))
            Text(text, color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

/* ── Primary action button with gradient ── */
@Composable
fun PrimaryAction(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .then(
                if (enabled) {
                    Modifier.shadow(6.dp, MaterialTheme.shapes.medium, spotColor = AppIndigo.copy(alpha = 0.3f))
                } else {
                    Modifier
                }
            ),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White.copy(alpha = 0.72f)
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    if (enabled) ButtonGradient else Brush.horizontalGradient(listOf(AppMuted, AppMuted)),
                    MaterialTheme.shapes.medium
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

/* ── Chip with border ── */
@Composable
fun ChipText(text: String, color: Color = AppBlue) {
    Surface(
        color = color.copy(alpha = .08f),
        shape = CircleShape,
        modifier = Modifier.border(1.dp, color.copy(alpha = 0.18f), CircleShape)
    ) {
        Text(
            text,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/* ── Avatar circle with initials ── */
@Composable
fun AvatarCircle(name: String, size: Int = 48, color: Color = AppIndigo) {
    val initials = name.split(" ").take(2).joinToString("") {
        it.firstOrNull()?.uppercase() ?: ""
    }
    Box(
        Modifier
            .size(size.dp)
            .background(color.copy(alpha = 0.12f), CircleShape)
            .border(2.dp, color.copy(alpha = 0.3f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = color, fontWeight = FontWeight.Bold, fontSize = (size / 3).sp)
    }
}

/* ── Info / warning banner ── */
@Composable
fun InfoBanner(text: String, color: Color = AppAmber, icon: ImageVector = Icons.Default.CheckCircle) {
    Surface(
        color = color.copy(alpha = .08f),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.2f), MaterialTheme.shapes.medium)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(text, color = color, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/* ── Rule item with icon ── */
@Composable
fun RuleItem(text: String, icon: ImageVector, color: Color = AppMuted) {
    Row(
        Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, color = AppText, style = MaterialTheme.typography.bodyMedium)
    }
}

/* ── Linear progress bar ── */
@Composable
fun StyledProgress(progress: Float, color: Color = AppBlue, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(CircleShape),
        color = color,
        trackColor = color.copy(alpha = 0.12f)
    )
}

/* ── Scrollable screen wrapper ── */
@Composable
fun ScreenList(content: @Composable () -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) { item { content() } }
}
