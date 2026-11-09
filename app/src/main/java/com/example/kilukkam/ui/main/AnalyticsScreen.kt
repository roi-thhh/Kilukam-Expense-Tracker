package com.example.kilukkam.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.example.kilukkam.utils.DateUtils
import com.example.kilukkam.utils.MonthYear
import com.example.kilukkam.utils.ShareUtils
import java.util.Calendar

enum class AnalyticsHorizon {
    MONTHLY, YEARLY, ALL_TIME
}

@Composable
fun AnalyticsScreen(
    expenses: List<Expense>,
    initialMonth: MonthYear = MonthYear.now(),
    onMonthChange: (MonthYear) -> Unit = {}
) {
    val context = LocalContext.current
    var horizon by remember { mutableStateOf(AnalyticsHorizon.MONTHLY) }
    var selectedMonth by remember { mutableStateOf(initialMonth) }
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    var selectedYear by remember { mutableStateOf(currentYear) }
    var showMonthPickerSheet by remember { mutableStateOf(false) }

    // Sync with external initialMonth updates
    LaunchedEffect(initialMonth) {
        selectedMonth = initialMonth
    }

    // Filter expenses according to selected horizon
    val filteredExpenses = remember(expenses, horizon, selectedMonth, selectedYear) {
        when (horizon) {
            AnalyticsHorizon.MONTHLY -> {
                val start = selectedMonth.startTimestamp()
                val end = selectedMonth.endTimestamp()
                expenses.filter { it.timestamp in start..end }
            }
            AnalyticsHorizon.YEARLY -> {
                val start = DateUtils.getStartOfYear(selectedYear)
                val end = DateUtils.getEndOfYear(selectedYear)
                expenses.filter { it.timestamp in start..end }
            }
            AnalyticsHorizon.ALL_TIME -> expenses
        }
    }

    val categoryTotals = remember(filteredExpenses) {
        filteredExpenses.groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
    }
        
    val totalExpense = remember(filteredExpenses) {
        filteredExpenses.sumOf { it.amount }
    }

    val allTimestamps = remember(expenses) { expenses.map { it.timestamp } }
    val availableYears = remember(allTimestamps) { DateUtils.getAvailableYears(allTimestamps) }
    val availableMonths = remember(allTimestamps) { DateUtils.getAvailableMonths(allTimestamps) }
    
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("SPENDING INSIGHTS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    Text("Analytics", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextDark, letterSpacing = (-0.5).sp)
                }
                
                if (filteredExpenses.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrandYellowPrimary)
                            .clickable { ShareUtils.shareAnalyticsAsImage(context, filteredExpenses, totalExpense) }
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

            // Horizon Segmented Control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .sunnyCardShadow(cornerRadius = 16.dp, blurRadius = 8.dp, offsetY = 2.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceWhite)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AnalyticsHorizon.values().forEach { tab ->
                    val isSelected = horizon == tab
                    val label = when (tab) {
                        AnalyticsHorizon.MONTHLY -> "Monthly"
                        AnalyticsHorizon.YEARLY -> "Yearly"
                        AnalyticsHorizon.ALL_TIME -> "All Time"
                    }
                    val bg by animateColorAsState(
                        targetValue = if (isSelected) BrandYellowPrimary else Color.Transparent,
                        label = "horizonTabBg"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .clickable { horizon = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) TextDark else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold
                        )
                    }
                }
            }

            // Sub-Horizon Navigation Bar (for Monthly and Yearly modes)
            when (horizon) {
                AnalyticsHorizon.MONTHLY -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        val prev = selectedMonth.previous()
                                        selectedMonth = prev
                                        onMonthChange(prev)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = TextDark, modifier = Modifier.size(18.dp))
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showMonthPickerSheet = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedMonth.getDisplayName(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        val next = selectedMonth.next()
                                        selectedMonth = next
                                        onMonthChange(next)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = TextDark, modifier = Modifier.size(18.dp))
                            }
                        }

                        if (!selectedMonth.isCurrentMonth()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BrandYellowSoft)
                                    .clickable {
                                        val now = MonthYear.now()
                                        selectedMonth = now
                                        onMonthChange(now)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("This Month", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                        }
                    }
                }
                AnalyticsHorizon.YEARLY -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .clickable { selectedYear -= 1 },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Year", tint = TextDark, modifier = Modifier.size(18.dp))
                            }

                            Text(
                                text = "Year $selectedYear",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .clickable { selectedYear += 1 },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next Year", tint = TextDark, modifier = Modifier.size(18.dp))
                            }
                        }

                        if (selectedYear != currentYear) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BrandYellowSoft)
                                    .clickable { selectedYear = currentYear }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Current Year", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            }
                        }
                    }
                }
                AnalyticsHorizon.ALL_TIME -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            "Cumulative historical summary across all logged transactions",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (filteredExpenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (horizon) {
                                AnalyticsHorizon.MONTHLY -> "No spending in ${selectedMonth.getDisplayName()}"
                                AnalyticsHorizon.YEARLY -> "No spending in Year $selectedYear"
                                AnalyticsHorizon.ALL_TIME -> "No spending logged yet"
                            },
                            color = TextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Transactions logged during this period will automatically appear here.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Donut Chart Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .sunnyCardShadow(cornerRadius = 28.dp, blurRadius = 14.dp, offsetY = 4.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(28.dp))
                        .padding(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(contentAlignment = Alignment.Center) {
                            SunnyDonutChart(
                                modifier = Modifier.size(180.dp),
                                data = categoryTotals.map { it.second.toFloat() },
                                colors = sunnyColors
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    when (horizon) {
                                        AnalyticsHorizon.MONTHLY -> "${selectedMonth.getMonthShortName().uppercase()} SPENT"
                                        AnalyticsHorizon.YEARLY -> "$selectedYear SPENT"
                                        AnalyticsHorizon.ALL_TIME -> "TOTAL SPENT"
                                    },
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
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

                // If Yearly horizon is selected: Show Monthly Breakdown Trend Bar Chart
                if (horizon == AnalyticsHorizon.YEARLY) {
                    YearlyMonthlyBarChart(
                        expenses = filteredExpenses,
                        year = selectedYear
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Category Breakdown",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "${categoryTotals.size} categories",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                .sunnyCardShadow(cornerRadius = 18.dp, blurRadius = 8.dp, offsetY = 2.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = categoryIcon,
                                            contentDescription = item.first,
                                            tint = color,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
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

        // Month Picker Bottom Sheet
        if (showMonthPickerSheet) {
            MonthPickerBottomSheet(
                selectedMonth = selectedMonth,
                availableMonths = availableMonths,
                onMonthSelected = {
                    selectedMonth = it
                    onMonthChange(it)
                    showMonthPickerSheet = false
                },
                onDismiss = { showMonthPickerSheet = false }
            )
        }
    }
}

@Composable
fun YearlyMonthlyBarChart(
    expenses: List<Expense>,
    year: Int
) {
    val monthShortNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val cal = Calendar.getInstance()
    
    // Group expenses by 0-indexed month
    val monthlyTotals = remember(expenses, year) {
        val map = IntArray(12) { 0 }
        expenses.forEach { exp ->
            cal.timeInMillis = exp.timestamp
            val m = cal.get(Calendar.MONTH)
            if (m in 0..11) {
                map[m] += exp.amount.toInt()
            }
        }
        map
    }

    val maxMonthSpend = (monthlyTotals.maxOrNull() ?: 1).coerceAtLeast(1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sunnyCardShadow(cornerRadius = 22.dp, blurRadius = 10.dp, offsetY = 3.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceWhite)
            .border(1.dp, BorderSubtle, RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Monthly Spending Trend",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    "Year $year",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                monthlyTotals.forEachIndexed { index, total ->
                    val fraction = (total.toFloat() / maxMonthSpend.toFloat()).coerceIn(0.06f, 1f)
                    val isPeak = total == maxMonthSpend && total > 0

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(fraction)
                                .width(12.dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(if (isPeak) BrandYellowPrimary else BrandYellowSoft)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = monthShortNames[index],
                            fontSize = 9.sp,
                            fontWeight = if (isPeak) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isPeak) TextDark else TextSecondary
                        )
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
