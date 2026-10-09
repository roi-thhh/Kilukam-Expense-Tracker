package com.example.kilukkam.ui.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kilukkam.data.Expense
import com.example.kilukkam.theme.*
import com.example.kilukkam.ui.sunnyCardShadow
import com.example.kilukkam.utils.ShareUtils

@Composable
fun AnalyticsScreen(expenses: List<Expense>) {
    val context = LocalContext.current
    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }
        
    val totalExpense = expenses.sumOf { it.amount }
    
    // Sunny Fintech Curated Color Spectrum
    val sunnyColors = listOf(
        BrandYellowPrimary,
        AccentIncome,
        AccentExpense,
        AccentBlue,
        AccentSky,
        BrandYellowWarm,
        Color(0xFF8B5CF6),
        Color(0xFFEC4899)
    )

    Box(modifier = Modifier.fillMaxSize().background(BackgroundCanvas)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("SPENDING INSIGHTS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    Text("Analytics", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextDark, letterSpacing = (-0.5).sp)
                }
                
                if (expenses.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrandYellowPrimary)
                            .clickable { ShareUtils.shareAnalyticsAsImage(context, expenses, totalExpense) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Share", tint = TextOnYellow, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", color = TextOnYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            if (expenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No spending data yet", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Transactions will automatically appear here.", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                // Donut Chart Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .sunnyCardShadow(cornerRadius = 28.dp, blurRadius = 14.dp, offsetY = 4.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(28.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(contentAlignment = Alignment.Center) {
                            SunnyDonutChart(
                                modifier = Modifier.size(190.dp),
                                data = categoryTotals.map { it.second.toFloat() },
                                colors = sunnyColors
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("TOTAL SPENT", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text(
                                    text = "₹${"%.0f".format(totalExpense)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextDark,
                                    letterSpacing = (-0.5).sp
                                )
                            }
                        }
                    }
                }
                
                Text(
                    text = "Category Breakdown",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 12.dp).align(Alignment.Start)
                )

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categoryTotals.indices.toList()) { index ->
                        val item = categoryTotals[index]
                        val color = sunnyColors[index % sunnyColors.size]
                        val percentage = if (totalExpense > 0) (item.second / totalExpense * 100) else 0.0
                        
                        val catLower = item.first.lowercase()
                        val categoryIcon = when {
                            catLower.contains("food") || catLower.contains("dining") || catLower.contains("burger") || catLower.contains("pizza") || catLower.contains("cafe") || catLower.contains("tea") || catLower.contains("coffee") -> Icons.Default.Restaurant
                            catLower.contains("transport") || catLower.contains("fuel") || catLower.contains("travel") || catLower.contains("cab") || catLower.contains("uber") || catLower.contains("auto") -> Icons.Default.DirectionsCar
                            catLower.contains("shop") || catLower.contains("grocer") || catLower.contains("mart") || catLower.contains("cloth") || catLower.contains("amazon") || catLower.contains("flipkart") -> Icons.Default.ShoppingCart
                            catLower.contains("bill") || catLower.contains("electric") || catLower.contains("wifi") || catLower.contains("recharge") || catLower.contains("rent") -> Icons.AutoMirrored.Filled.ReceiptLong
                            catLower.contains("tech") || catLower.contains("laptop") || catLower.contains("phone") || catLower.contains("gadget") -> Icons.Default.Devices
                            catLower.contains("movie") || catLower.contains("game") || catLower.contains("entertain") || catLower.contains("netflix") -> Icons.Default.Movie
                            catLower.contains("health") || catLower.contains("medic") || catLower.contains("pharma") || catLower.contains("gym") || catLower.contains("hospital") -> Icons.Default.LocalHospital
                            catLower.contains("salary") || catLower.contains("income") || catLower.contains("bank") -> Icons.Default.AccountBalance
                            else -> Icons.Default.Payments
                        }
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .sunnyCardShadow(cornerRadius = 20.dp, blurRadius = 8.dp, offsetY = 2.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = categoryIcon,
                                            contentDescription = item.first,
                                            tint = color,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.first,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = TextDark
                                        )
                                        Text(
                                            text = "${"%.1f".format(percentage)}% of total",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Text(
                                        text = "₹${"%.2f".format(item.second)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextDark,
                                        fontSize = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Progress Line
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .background(BackgroundMuted)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth((percentage.toFloat() / 100f).coerceIn(0f, 1f))
                                            .height(6.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SunnyDonutChart(
    modifier: Modifier = Modifier,
    data: List<Float>,
    colors: List<Color>
) {
    val total = data.sum()
    var startAngle = -90f
    
    val transition = updateTransition(targetState = true, label = "SunnyDonut")
    val sweepProgress by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 1200, easing = FastOutSlowInEasing) },
        label = "Sweep"
    ) { if (it) 1f else 0f }
    
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.14f
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        // Base track
        drawArc(
            color = BackgroundMuted,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(center.x - radius, center.y - radius)
        )

        data.forEachIndexed { index, value ->
            val sweepAngle = (value / total) * 360f * sweepProgress
            val color = colors[index % colors.size]
            
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
            
            startAngle += sweepAngle
        }
    }
}
