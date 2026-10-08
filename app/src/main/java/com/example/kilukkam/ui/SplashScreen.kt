package com.example.kilukkam.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kilukkam.R
import com.example.kilukkam.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var hasFinished by remember { mutableStateOf(false) }

    fun finishOnce() {
        if (!hasFinished) {
            hasFinished = true
            onSplashFinished()
        }
    }

    // Fast 850ms entrance and auto-transition to the app
    LaunchedEffect(Unit) {
        delay(850)
        finishOnce()
    }

    val transition = updateTransition(targetState = true, label = "LogoEntrance")
    
    val logoScale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 650, easing = FastOutSlowInEasing) },
        label = "LogoScale"
    ) { if (it) 1f else 0.88f }

    val logoAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 500, easing = LinearEasing) },
        label = "LogoAlpha"
    ) { if (it) 1f else 0f }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .clickable { finishOnce() },
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient neon aura glow in center
        Box(
            modifier = Modifier
                .size(280.dp)
                .alpha(0.12f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(BrandLime, AccentCyan.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(logoScale)
                .alpha(logoAlpha)
        ) {
            // Branded App Logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(SurfacePrimary)
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kilukkam_logo),
                    contentDescription = "Kilukkam Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name
            Text(
                text = "KILUKKAM",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "EXPENSE TRACKER",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}
