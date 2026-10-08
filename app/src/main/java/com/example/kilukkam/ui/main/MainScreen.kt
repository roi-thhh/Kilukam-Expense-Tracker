package com.example.kilukkam.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kilukkam.R
import com.example.kilukkam.data.Expense
import com.example.kilukkam.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    initialAmount: Double? = null,
    showCategorizeDialog: Boolean = false,
    onDialogDismissed: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(showCategorizeDialog) }

    LaunchedEffect(showCategorizeDialog) {
        if (showCategorizeDialog) {
            showDialog = true
        }
    }

    Box(modifier = modifier.fillMaxSize().background(BackgroundPrimary)) {
        // Aurora Mesh Glow Background
        val infiniteTransition = rememberInfiniteTransition()
        val gradientOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentPurple.copy(alpha = 0.1f), AccentCyan.copy(alpha = 0.05f), Color.Transparent),
                        center = Offset(gradientOffset, gradientOffset),
                        radius = 1200f
                    )
                )
        )

        val totalExpense = state.expenses.sumOf { it.amount }
        val totalIncome = state.incomes.sumOf { it.amount }
        val netBalance = totalIncome - totalExpense
        var showReceiveDialog by remember { mutableStateOf(false) }
        var showSavingsDialog by remember { mutableStateOf(false) }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Good morning,", color = TextSecondary, fontSize = 14.sp)
                        Text(state.userName, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SurfacePrimary)
                            .border(1.dp, BorderSubtle, CircleShape)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimary)
                        // Notification dot
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(BrandLime)
                        )
                    }
                }
            }


            // Hero Balance Section
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Text("TOTAL NET BALANCE", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${"%.2f".format(netBalance)}",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = (-1.5).sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(BrandLime.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("+14.8%", color = BrandLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Quick Actions
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickAction(icon = Icons.Default.Send, label = "Send", isPrimary = true) {
                            com.example.kilukkam.utils.ShareUtils.shareAnalyticsAsImage(context, totalExpense)
                        }
                        QuickAction(icon = Icons.Default.CallMade, label = "Receive") { showReceiveDialog = true }
                        QuickAction(icon = Icons.Default.SwapHoriz, label = "Swap") {
                            // Dummy action for now
                        }
                        QuickAction(icon = Icons.Default.Add, label = "Add") {
                            showSavingsDialog = true
                        }
                    }
                }
            }

            // Quick Summary Metrics
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard(title = "Income", amount = "₹${"%.2f".format(totalIncome)}", color = AccentCyan, modifier = Modifier.weight(1f))
                    SummaryCard(title = "Expenses", amount = "₹${"%.2f".format(totalExpense)}", color = AccentPink, modifier = Modifier.weight(1f))
                    SummaryCard(title = "Savings", amount = "₹${"%.2f".format(state.savings)}", color = BrandLime, modifier = Modifier.weight(1f))
                }
            }

            // Spending Graph Card
            item {
                var selectedTime by remember { mutableStateOf("1M") }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(SurfacePrimary)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(32.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Spending Flow", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(BackgroundElevated).padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("1W", "1M", "1Y").forEach { time ->
                                    val isSelected = selectedTime == time
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) BrandLime else Color.Transparent)
                                            .clickable { selectedTime = time }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            time, 
                                            color = if (isSelected) PureBlack else TextSecondary, 
                                            fontSize = 12.sp, 
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Dynamic Reactive Graph with Neon Fill
                        Canvas(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                            val path = Path()
                            val fillPath = Path()
                            val width = size.width
                            val height = size.height

                            when (selectedTime) {
                                "1W" -> {
                                    // 7 day sharp fluctuation
                                    path.moveTo(0f, height * 0.7f)
                                    path.lineTo(width * 0.16f, height * 0.45f)
                                    path.lineTo(width * 0.33f, height * 0.85f)
                                    path.lineTo(width * 0.50f, height * 0.25f)
                                    path.lineTo(width * 0.66f, height * 0.60f)
                                    path.lineTo(width * 0.83f, height * 0.35f)
                                    path.lineTo(width, height * 0.15f)
                                }
                                "1M" -> {
                                    // Smooth monthly curve
                                    path.moveTo(0f, height * 0.8f)
                                    path.cubicTo(width * 0.2f, height * 0.8f, width * 0.3f, height * 0.2f, width * 0.5f, height * 0.4f)
                                    path.cubicTo(width * 0.7f, height * 0.6f, width * 0.8f, height * 0.1f, width, height * 0.3f)
                                }
                                else -> {
                                    // 1Y macro trend
                                    path.moveTo(0f, height * 0.9f)
                                    path.cubicTo(width * 0.25f, height * 0.75f, width * 0.4f, height * 0.55f, width * 0.6f, height * 0.35f)
                                    path.cubicTo(width * 0.75f, height * 0.25f, width * 0.85f, height * 0.45f, width, height * 0.1f)
                                }
                            }

                            fillPath.addPath(path)
                            fillPath.lineTo(width, height)
                            fillPath.lineTo(0f, height)
                            fillPath.close()

                            drawPath(
                                path = fillPath,
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(BrandLime.copy(alpha = 0.22f), Color.Transparent)
                                )
                            )

                            drawPath(
                                path = path,
                                color = BrandLime,
                                style = Stroke(width = 3.5.dp.toPx())
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        val insightText = when (selectedTime) {
                            "1W" -> "Weekly trend • ₹320/day average spend"
                            "1M" -> "You spent 12% less than last month"
                            else -> "Annual flow • Spending stabilized within budget"
                        }
                        Text(insightText, color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }

            // Recent Expenses Header
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Recent Expenses", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("See all", fontSize = 14.sp, color = AccentCyan)
                }
            }

            // Folder-like cards (we use negative offset conceptually, but simple spacedBy works too)
            itemsIndexed(state.expenses.reversed()) { index, expense ->
                ExpenseItem(expense, index)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (showDialog && initialAmount != null) {
            CategorizeDialog(
                amount = initialAmount,
                categories = state.categories,
                onDismiss = { 
                    showDialog = false 
                    onDialogDismissed()
                },
                onSave = { amount, category ->
                    viewModel.addExpense(amount, category)
                    showDialog = false
                    onDialogDismissed()
                },
                onAddCategory = {
                    viewModel.addCategory(it)
                }
            )
        }

        if (showReceiveDialog) {
            ManualEntryDialog(
                isIncome = true,
                categories = emptyList(),
                onDismiss = { showReceiveDialog = false },
                onSave = { amount, category ->
                    viewModel.addIncome(amount, category)
                    showReceiveDialog = false
                },
                onAddCategory = {}
            )
        }

        if (showSavingsDialog) {
            AddSavingsDialog(
                onDismiss = { showSavingsDialog = false },
                onSave = { amount ->
                    viewModel.addSavings(amount)
                    showSavingsDialog = false
                }
            )
        }
    }
}

@Composable
fun QuickAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isPrimary: Boolean = false, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isPrimary) BrandLime else SurfacePrimary)
                .border(1.dp, if (isPrimary) BrandLime else BorderSubtle, RoundedCornerShape(20.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = if (isPrimary) PureBlack else TextPrimary, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SummaryCard(title: String, amount: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SurfacePrimary)
            .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(title, color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(amount, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense, index: Int) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(expense.timestamp))
    
    val catLower = expense.category.lowercase()
    val (categoryIcon, categoryColor) = when {
        catLower.contains("food") || catLower.contains("dining") || catLower.contains("burger") || catLower.contains("pizza") || catLower.contains("cafe") || catLower.contains("tea") || catLower.contains("coffee") -> 
            Pair(Icons.Default.Restaurant, AccentOrange)
        catLower.contains("transport") || catLower.contains("fuel") || catLower.contains("travel") || catLower.contains("cab") || catLower.contains("uber") || catLower.contains("auto") -> 
            Pair(Icons.Default.DirectionsCar, AccentCyan)
        catLower.contains("shop") || catLower.contains("grocer") || catLower.contains("mart") || catLower.contains("cloth") || catLower.contains("amazon") || catLower.contains("flipkart") -> 
            Pair(Icons.Default.ShoppingCart, AccentPink)
        catLower.contains("bill") || catLower.contains("electric") || catLower.contains("wifi") || catLower.contains("recharge") || catLower.contains("rent") -> 
            Pair(Icons.Default.ReceiptLong, AccentPurple)
        catLower.contains("tech") || catLower.contains("laptop") || catLower.contains("phone") || catLower.contains("gadget") -> 
            Pair(Icons.Default.Devices, Color(0xFF00E5FF))
        catLower.contains("movie") || catLower.contains("game") || catLower.contains("entertain") || catLower.contains("netflix") -> 
            Pair(Icons.Default.Movie, Color(0xFFFF4081))
        catLower.contains("health") || catLower.contains("medic") || catLower.contains("pharma") || catLower.contains("gym") || catLower.contains("hospital") -> 
            Pair(Icons.Default.LocalHospital, Color(0xFF00E676))
        catLower.contains("salary") || catLower.contains("income") || catLower.contains("bank") -> 
            Pair(Icons.Default.AccountBalance, BrandLime)
        else -> 
            Pair(Icons.Default.Payments, listOf(AccentCyan, AccentPink, AccentOrange, BrandLime, AccentPurple)[index % 5])
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
        // High-contrast themed glassmorphic icon badge with radial ambient glow
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            categoryColor.copy(alpha = 0.28f),
                            categoryColor.copy(alpha = 0.08f),
                            BackgroundElevated
                        ),
                        radius = 90f
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(categoryColor.copy(alpha = 0.7f), categoryColor.copy(alpha = 0.2f))
                    ),
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon,
                contentDescription = expense.category,
                tint = categoryColor,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(text = expense.category, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = dateString, color = TextSecondary, fontSize = 13.sp)
        }
        
        Text(
            text = "-₹${"%.0f".format(expense.amount)}",
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            fontSize = 18.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSavingsDialog(
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val quickAmounts = listOf(500, 1000, 2000, 5000)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfacePrimary,
        shape = RoundedCornerShape(32.dp),
        title = {
            Text(
                "Add to Savings 💰",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Deposit funds into your personal savings vault.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp).align(Alignment.Start)
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() || it == '.' }) amountText = input
                    },
                    placeholder = { Text("0.00", color = TextSecondary, fontSize = 26.sp) },
                    prefix = { Text("₹ ", color = BrandLime, fontSize = 26.sp, fontWeight = FontWeight.Black) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    ),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandLime,
                        unfocusedBorderColor = BorderSubtle,
                        cursorColor = BrandLime
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick amount chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickAmounts.forEach { amt ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BackgroundElevated)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                .clickable {
                                    val curr = amountText.toDoubleOrNull() ?: 0.0
                                    amountText = (curr + amt).toInt().toString()
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "+$amt",
                                color = BrandLime,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            val validAmount = amountText.toDoubleOrNull() ?: 0.0
            Button(
                onClick = {
                    if (validAmount > 0) {
                        onSave(validAmount)
                    }
                },
                enabled = validAmount > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandLime,
                    disabledContainerColor = BackgroundElevated
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(
                    "Deposit to Savings",
                    color = if (validAmount > 0) PureBlack else TextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorizeDialog(
    amount: Double,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (Double, String) -> Unit,
    onAddCategory: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("") }
    val defaultRecommendations = listOf("Food & Dining", "Transport", "Shopping", "Tech & Gear")
    val displayCategories = if (categories.isEmpty()) defaultRecommendations else categories
    
    var newCategoryText by remember { mutableStateOf("") }
    var showNewCategoryInput by remember { mutableStateOf(false) }

    // Neon Aurora Modal
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfacePrimary,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(32.dp)),
        title = { 
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    "Add Expense", 
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                ) 
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // Large Hero Amount
                Text(
                    text = "₹${"%.2f".format(amount)}",
                    fontSize = 48.sp,
                    color = BrandLime,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 24.dp)) {
                    Box(modifier = Modifier.clip(CircleShape).background(BackgroundElevated).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("+₹10", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.clip(CircleShape).background(BackgroundElevated).padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("+₹50", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text("Select Category", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(bottom = 12.dp).align(Alignment.Start))
                
                // Categories
                FlowRowCustom(items = displayCategories, selectedItem = selectedCategory) { category ->
                    selectedCategory = category
                    showNewCategoryInput = false
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Add new category chip
                Surface(
                    onClick = { 
                        showNewCategoryInput = true
                        selectedCategory = ""
                    },
                    modifier = Modifier.align(Alignment.Start),
                    shape = CircleShape,
                    color = if (showNewCategoryInput) AccentCyan.copy(alpha = 0.2f) else BackgroundElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (showNewCategoryInput) AccentCyan else Color.Transparent)
                ) {
                    Text(
                        "+ Create New", 
                        color = if (showNewCategoryInput) AccentCyan else TextPrimary, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }

                if (showNewCategoryInput) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newCategoryText,
                        onValueChange = { newCategoryText = it },
                        placeholder = { Text("Category Name", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = AccentCyan
                        ),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                val trimmed = newCategoryText.trim()
                                if (trimmed.isNotBlank()) {
                                    onAddCategory(trimmed)
                                    selectedCategory = trimmed
                                    newCategoryText = ""
                                    showNewCategoryInput = false
                                }
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Save", tint = AccentCyan)
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (categories.isEmpty() || !categories.contains(selectedCategory)) {
                        onAddCategory(selectedCategory)
                    }
                    onSave(amount, selectedCategory) 
                },
                enabled = selectedCategory.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandLime,
                    disabledContainerColor = BackgroundElevated
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Add Expense • ₹${"%.0f".format(amount)}", color = if (selectedCategory.isNotBlank()) PureBlack else TextSecondary, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            }
        }
    )
}

@Composable
fun FlowRowCustom(items: List<String>, selectedItem: String, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items) { category ->
            val isSelected = selectedItem == category
            val colors = listOf(AccentOrange, AccentCyan, AccentPink, BrandLime, AccentPurple)
            val accentColor = colors[kotlin.math.abs(category.hashCode()) % colors.size]
            
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) accentColor.copy(alpha=0.15f) else BackgroundElevated)
                    .border(1.dp, if (isSelected) accentColor else Color.Transparent, CircleShape)
                    .clickable { onSelect(category) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(category, color = if (isSelected) accentColor else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
