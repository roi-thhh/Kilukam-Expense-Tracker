package com.example.kilukkam.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.kilukkam.R
import com.example.kilukkam.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onFinish: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    
    Box(modifier = Modifier.fillMaxSize().background(BackgroundPrimary)) {
        val infiniteTransition = rememberInfiniteTransition()
        val gradientOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(6000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentCyan.copy(alpha = 0.1f), AccentPurple.copy(alpha = 0.1f), Color.Transparent),
                        center = Offset(gradientOffset, gradientOffset),
                        radius = 1200f
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfacePrimary)
                    .border(1.5.dp, BrandLime.copy(alpha=0.6f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kilukkam_logo),
                    contentDescription = "Kilukkam Logo",
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Welcome to\nKilukkam",
                color = TextPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 44.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Let's personalize your experience. What should we call you?",
                color = TextSecondary,
                fontSize = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Your Name", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandLime,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = BrandLime
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { if (name.isNotBlank()) onFinish(name.trim()) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandLime,
                    disabledContainerColor = BackgroundElevated
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Text(
                    "Get Started",
                    color = if (name.isNotBlank()) PureBlack else TextSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }
    }
}
