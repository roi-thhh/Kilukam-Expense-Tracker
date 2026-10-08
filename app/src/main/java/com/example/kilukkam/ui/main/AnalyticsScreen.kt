package com.example.kilukkam.ui.main

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kilukkam.data.Expense
import com.example.kilukkam.theme.*

@Composable
fun AnalyticsScreen(expenses: List<Expense>) {
    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }
        
    val totalExpense = expenses.sumOf { it.amount }
    
    val categoryColors = listOf(
        BrandLime,
        AccentCyan,
        AccentPink,
        AccentOrange,
        Color(0xFFCCFF00), // Lemon Yellow
        Color(0xFF00FF99), // Seafoam
        Color(0xFFB000FF), // Neon Purple
        Color(0xFFFF3300)  // Vermillion
    )

    Box(modifier = Modifier.fillMaxSize().background(BackgroundPrimary)) {
        // Subtle Aurora Background
        val infiniteTransition = rememberInfiniteTransition()
        val gradientOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 800f,
            animationSpec = infiniteRepeatable(
                animation = tween(10000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentCyan.copy(alpha = 0.1f), Color.Transparent),
                        center = Offset(gradientOffset, gradientOffset),
                        radius = 1000f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Analytics",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
            )

            if (expenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data to display yet.", color = TextSecondary)
                }
            } else {
                // Pie Chart Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SurfacePrimary, PureBlack)
                            )
                        )
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        NeonDonutChart(
                            modifier = Modifier.size(200.dp),
                            data = categoryTotals.map { it.second.toFloat() },
                            colors = categoryColors
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Total Spent", color = TextSecondary, fontSize = 14.sp)
                        Text(
                            text = "₹${"%.2f".format(totalExpense)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = (-1).sp
                        )
                    }
                }
                
                Text(
                    text = "Category Breakdown",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp).align(Alignment.Start)
                )

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categoryTotals.indices.toList()) { index ->
                        val item = categoryTotals[index]
                        val color = categoryColors[index % categoryColors.size]
                        val percentage = if (totalExpense > 0) (item.second / totalExpense * 100) else 0.0
                        
                        val catLower = item.first.lowercase()
                        val categoryIcon = when {
                            catLower.contains("food") || catLower.contains("dining") || catLower.contains("burger") || catLower.contains("pizza") || catLower.contains("cafe") || catLower.contains("tea") || catLower.contains("coffee") -> Icons.Default.Restaurant
                            catLower.contains("transport") || catLower.contains("fuel") || catLower.contains("travel") || catLower.contains("cab") || catLower.contains("uber") || catLower.contains("auto") -> Icons.Default.DirectionsCar
                            catLower.contains("shop") || catLower.contains("grocer") || catLower.contains("mart") || catLower.contains("cloth") || catLower.contains("amazon") || catLower.contains("flipkart") -> Icons.Default.ShoppingCart
                            catLower.contains("bill") || catLower.contains("electric") || catLower.contains("wifi") || catLower.contains("recharge") || catLower.contains("rent") -> Icons.Default.ReceiptLong
                            catLower.contains("tech") || catLower.contains("laptop") || catLower.contains("phone") || catLower.contains("gadget") -> Icons.Default.Devices
                            catLower.contains("movie") || catLower.contains("game") || catLower.contains("entertain") || catLower.contains("netflix") -> Icons.Default.Movie
                            catLower.contains("health") || catLower.contains("medic") || catLower.contains("pharma") || catLower.contains("gym") || catLower.contains("hospital") -> Icons.Default.LocalHospital
                            catLower.contains("salary") || catLower.contains("income") || catLower.contains("bank") -> Icons.Default.AccountBalance
                            else -> Icons.Default.Payments
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(SurfacePrimary)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.radialGradient(
                                            listOf(color.copy(alpha = 0.28f), color.copy(alpha = 0.08f), BackgroundElevated),
                                            radius = 70f
                                        )
                                    )
                                    .border(1.2.dp, color.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = categoryIcon,
                                    contentDescription = item.first,
                                    tint = color,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = item.first,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${"%.2f".format(item.second)}",
                                    fontWeight = FontWeight.Black,
                                    color = color,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "${"%.1f".format(percentage)}%",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NeonDonutChart(
    modifier: Modifier = Modifier,
    data: List<Float>,
    colors: List<Color>
) {
    val total = data.sum()
    var startAngle = -90f
    
    // Animation
    val transition = updateTransition(targetState = true, label = "DonutChart")
    val sweepProgress by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 1500, easing = FastOutSlowInEasing) },
        label = "Sweep"
    ) { if (it) 1f else 0f }
    
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.15f
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        data.forEachIndexed { index, value ->
            val sweepAngle = (value / total) * 360f * sweepProgress
            val color = colors[index % colors.size]
            
            // Draw a subtle outer glow
            drawArc(
                color = color.copy(alpha = 0.2f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth * 2f, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
            
            // Draw the vibrant neon stroke
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
