package com.example.kilukkam.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
            .background(BackgroundCanvas)
            .clickable { finishOnce() },
        contentAlignment = Alignment.Center
    ) {
        // Soft warm yellow ambient ring
        Box(
            modifier = Modifier
                .size(320.dp)
                .clip(CircleShape)
                .background(BrandYellowSoft.copy(alpha = 0.45f))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(logoScale)
                .alpha(logoAlpha)
        ) {
            // Branded App Logo in crisp card
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .sunnyCardShadow(cornerRadius = 28.dp, blurRadius = 16.dp, offsetY = 6.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurfaceWhite)
                    .border(1.5.dp, BorderSubtle, RoundedCornerShape(28.dp))
                    .padding(20.dp),
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

            // App Name & Tagline
            Text(
                text = "KILUKKAM",
                color = TextDark,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "SMART EXPENSE TRACKER",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}
