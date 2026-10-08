package com.example.kilukkam.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kilukkam.theme.PrimaryOrange
import com.example.kilukkam.theme.TextMuted
import com.example.kilukkam.ui.claymorphism

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings & Help",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
        )

        // Permission Fix Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .claymorphism(cornerRadius = 24.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Fix SMS Permissions (Android 13+)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryOrange,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "If Android says 'Restricted Setting' or 'Harmful App' when trying to allow SMS permissions, this is normal for un-published apps. Follow these steps to fix it:",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "1. Go to your phone's Settings app.\n" +
                           "2. Go to Apps > Kilukkam.\n" +
                           "3. Tap the three dots (⋮) in the top right.\n" +
                           "4. Tap 'Allow restricted settings'.\n" +
                           "5. Return here and allow the SMS permission.",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }
        }
        
        // Export Data (Placeholder)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .claymorphism(cornerRadius = 24.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Data Management",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Export your expenses to a CSV file to view them in Excel or Google Sheets.",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = { /* TODO: Implement CSV Export */ },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export to CSV", color = Color.White)
                }
            }
        }
    }
}
