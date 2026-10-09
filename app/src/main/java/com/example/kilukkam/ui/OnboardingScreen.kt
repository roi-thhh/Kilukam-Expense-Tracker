package com.example.kilukkam.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kilukkam.R
import com.example.kilukkam.theme.*

@Composable
fun OnboardingScreen(onFinish: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas)
    ) {
        // Warm pale yellow ambient decorative orb
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-80).dp)
                .clip(CircleShape)
                .background(BrandYellowSoft.copy(alpha = 0.5f))
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo Card
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .sunnyCardShadow(cornerRadius = 28.dp, blurRadius = 14.dp, offsetY = 4.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(BrandYellowPrimary)
                    .border(1.5.dp, BorderSubtle, RoundedCornerShape(28.dp))
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
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "Welcome to\nKilukkam",
                color = TextDark,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 42.sp,
                letterSpacing = (-0.5).sp
            )
            
            Spacer(modifier = Modifier.height(14.dp))
            
            Text(
                text = "Let's personalize your finance hub. What should we call you?",
                color = TextSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Your Name", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite,
                    focusedBorderColor = BrandYellowPrimary,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark,
                    cursorColor = TextDark
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { if (name.isNotBlank()) onFinish(name.trim()) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandYellowPrimary,
                    disabledContainerColor = BackgroundMuted
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .sunnyCardShadow(cornerRadius = 24.dp, blurRadius = 10.dp, offsetY = 3.dp)
            ) {
                Text(
                    "Get Started",
                    color = if (name.isNotBlank()) TextOnYellow else TextMuted,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp
                )
            }
        }
    }
}
