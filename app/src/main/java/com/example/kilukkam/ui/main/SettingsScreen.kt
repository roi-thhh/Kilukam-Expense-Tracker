package com.example.kilukkam.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kilukkam.theme.*
import com.example.kilukkam.utils.ShareUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainScreenViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundPrimary)) {
        // Continuous Ambient Neon Aurora Mesh
        val infiniteTransition = rememberInfiniteTransition()
        val gradientOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 900f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentCyan.copy(alpha = 0.08f), AccentPurple.copy(alpha = 0.06f), Color.Transparent),
                        center = Offset(gradientOffset, gradientOffset),
                        radius = 1100f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfacePrimary)
                        .border(1.dp, BorderSubtle, CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Profile & Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = (-0.5).sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // User Profile Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurfacePrimary)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(28.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Glowing Initial Avatar
                        val initialLetter = state.userName.takeIf { it.isNotBlank() }?.first()?.uppercase() ?: "K"
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(BrandLime.copy(alpha = 0.15f))
                                .border(2.dp, BrandLime, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initialLetter,
                                color = BrandLime,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.userName,
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(BrandLime)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Private Offline Storage",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Edit Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BackgroundElevated)
                                .border(1.dp, BorderSubtle, CircleShape)
                                .clickable { showEditProfileDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = AccentCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Key Stats in Profile
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Monthly Target", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "₹${"%.0f".format(state.budgetGoal)}",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("Total Records", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${state.expenses.size + state.incomes.size} entries",
                                color = AccentCyan,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("Currency", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "INR (₹)",
                                color = BrandLime,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Preferences & Automation
            Text(
                text = "AUTOMATION & PERMISSIONS",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfacePrimary)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SMS Auto-Detection", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Parses bank debits & credits in background", color = TextSecondary, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandLime.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("ACTIVE", color = BrandLime, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Android 13+ Restricted Settings Info:\nIf SMS permissions show 'Restricted Setting' or disabled, tap below to open App Info, tap the 3 dots (⋮) in the top right, and choose 'Allow restricted settings'.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open system settings", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BackgroundElevated),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open App System Settings", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section: Data & Backup
            Text(
                text = "DATA MANAGEMENT",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfacePrimary)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    // Export CSV
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (state.expenses.isEmpty() && state.incomes.isEmpty()) {
                                    Toast.makeText(context, "No transactions to export", Toast.LENGTH_SHORT).show()
                                } else {
                                    ShareUtils.exportAndShareCsv(context, state.expenses, state.incomes)
                                }
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BrandLime.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = BrandLime, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Export Transactions (CSV)", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Open in Excel, Google Sheets, or share via WhatsApp", color = TextSecondary, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Reset Data Action
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetConfirmDialog = true }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentPink.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = AccentPink, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Clear All Data", color = AccentPink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Permanently wipe all recorded incomes and expenses", color = TextSecondary, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AccentPink)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Version & About Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "KILUKKAM • NEON AURORA",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 2.4 (Build 2026.10)",
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "100% Offline • Kinetic Animations • Private & Secure",
                        color = BrandLime.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Edit Profile Dialog
        if (showEditProfileDialog) {
            var tempName by remember { mutableStateOf(state.userName) }
            var tempBudget by remember { mutableStateOf(state.budgetGoal.toInt().toString()) }

            AlertDialog(
                onDismissRequest = { showEditProfileDialog = false },
                containerColor = SurfacePrimary,
                title = {
                    Text("Edit Profile", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                text = {
                    Column {
                        Text("Display Name", color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandLime,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = BrandLime
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Monthly Budget Target (₹)", color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = tempBudget,
                            onValueChange = { input ->
                                if (input.all { it.isDigit() }) tempBudget = input
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = AccentCyan
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                viewModel.updateUserName(tempName.trim())
                            }
                            val budgetVal = tempBudget.toDoubleOrNull() ?: 25000.0
                            viewModel.updateBudgetGoal(budgetVal)
                            showEditProfileDialog = false
                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandLime),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save", color = PureBlack, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditProfileDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }

        // Reset Data Confirmation Dialog
        if (showResetConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showResetConfirmDialog = false },
                containerColor = SurfacePrimary,
                title = {
                    Text("Clear All Data?", color = AccentPink, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                text = {
                    Text(
                        "This will permanently delete all expenses and incomes from your phone. This action cannot be reversed.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAllData()
                            showResetConfirmDialog = false
                            Toast.makeText(context, "All data has been cleared", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Delete Everything", color = PureBlack, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirmDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}
