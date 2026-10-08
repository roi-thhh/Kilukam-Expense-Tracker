package com.example.kilukkam.ui.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kilukkam.data.Expense
import com.example.kilukkam.theme.PrimaryOrange
import com.example.kilukkam.theme.TextMuted
import com.example.kilukkam.ui.claymorphism

@Composable
fun AnalyticsScreen(expenses: List<Expense>) {
    // Group expenses by category
    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }
        
    val totalExpense = expenses.sumOf { it.amount }
    
    // Assign a unique color to each category
    val categoryColors = listOf(
        PrimaryOrange,
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFF3F51B5), // Indigo
        Color(0xFF00BCD4), // Cyan
        Color(0xFF4CAF50), // Green
        Color(0xFFFFEB3B), // Yellow
        Color(0xFFFF9800)  // Orange
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Analytics",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
        )

        if (expenses.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No data to display yet.", color = TextMuted)
            }
        } else {
            // Pie Chart Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .claymorphism(cornerRadius = 24.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PieChart(
                        modifier = Modifier.size(200.dp),
                        data = categoryTotals.map { it.second.toFloat() },
                        colors = categoryColors
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Total Spent", color = TextMuted, fontSize = 14.sp)
                    Text(
                        text = "₹${"%.2f".format(totalExpense)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Text(
                text = "Category Breakdown",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp).align(Alignment.Start)
            )

            // Category Legend / List
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categoryTotals.indices.toList()) { index ->
                    val item = categoryTotals[index]
                    val color = categoryColors[index % categoryColors.size]
                    val percentage = if (totalExpense > 0) (item.second / totalExpense * 100) else 0.0
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .claymorphism(cornerRadius = 16.dp, blurRadius = 4.dp, offsetX = 2.dp, offsetY = 2.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = item.first,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${"%.2f".format(item.second)}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${"%.1f".format(percentage)}%",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: List<Float>,
    colors: List<Color>
) {
    val total = data.sum()
    var startAngle = -90f
    
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        data.forEachIndexed { index, value ->
            val sweepAngle = (value / total) * 360f
            val color = colors[index % colors.size]
            
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
            
            startAngle += sweepAngle
        }
        
        // Inner circle for donut chart look
        drawCircle(
            color = Color(0xFF1E1622), // Matching the surface color roughly
            radius = radius * 0.65f,
            center = center
        )
    }
}
