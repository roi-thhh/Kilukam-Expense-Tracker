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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
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
import com.example.kilukkam.data.Account
import com.example.kilukkam.data.Expense
import com.example.kilukkam.data.TargetVault
import com.example.kilukkam.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    initialAmount: Double? = null,
    initialMerchant: String? = null,
    initialAccount: String? = null,
    initialSuggestedCategory: String? = null,
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

    var showReceiveDialog by remember { mutableStateOf(false) }
    var showSavingsDialog by remember { mutableStateOf(false) }
    var showCreateVaultDialog by remember { mutableStateOf(false) }
    var showDepositVaultDialog by remember { mutableStateOf(false) }
    var selectedVaultForDeposit by remember { mutableStateOf<TargetVault?>(null) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(BackgroundPrimary)) {
        // Continuous Ambient Aurora Glow
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

        // Filter expenses if an account is selected
        val displayedExpenses = if (state.selectedAccountFilter != null) {
            state.expenses.filter { it.account.equals(state.selectedAccountFilter, ignoreCase = true) }
        } else {
            state.expenses
        }

        val totalExpense = state.expenses.sumOf { it.amount }
        val totalIncome = state.incomes.sumOf { it.amount }
        val netBalance = totalIncome - totalExpense

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
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
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
                    Text("TOTAL NET BALANCE", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${"%.2f".format(netBalance)}",
                            fontSize = 44.sp,
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
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Quick Actions
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        QuickAction(icon = Icons.AutoMirrored.Filled.Send, label = "Send", isPrimary = true) {
                            com.example.kilukkam.utils.ShareUtils.shareAnalyticsAsImage(context, totalExpense)
                        }
                        QuickAction(icon = Icons.AutoMirrored.Filled.CallMade, label = "Receive") { showReceiveDialog = true }
                        QuickAction(icon = Icons.Default.Tune, label = "Budgets") { showBudgetDialog = true }
                        QuickAction(icon = Icons.Default.Add, label = "Savings") { showSavingsDialog = true }
                    }
                }
            }

            // Quick Summary Metrics
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard(title = "Income", amount = "₹${"%.2f".format(totalIncome)}", color = AccentCyan, modifier = Modifier.weight(1f))
                    SummaryCard(title = "Expenses", amount = "₹${"%.2f".format(totalExpense)}", color = AccentPink, modifier = Modifier.weight(1f))
                    SummaryCard(title = "Savings", amount = "₹${"%.2f".format(state.savings)}", color = BrandLime, modifier = Modifier.weight(1f))
                }
            }

            // Feature 2: Multi-Account & Credit Card Management Strip
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Accounts & Cards", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("💳", fontSize = 14.sp)
                        }
                        if (state.selectedAccountFilter != null) {
                            Text(
                                "Clear Filter", 
                                color = AccentCyan, 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { viewModel.selectAccountFilter(null) }
                            )
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // All Accounts Filter Card
                        item {
                            AccountCard(
                                title = "All Accounts",
                                subtitle = "₹${"%.0f".format(netBalance)}",
                                icon = Icons.Default.AccountBalanceWallet,
                                isSelected = state.selectedAccountFilter == null,
                                onClick = { viewModel.selectAccountFilter(null) }
                            )
                        }

                        // Specific Accounts
                        items(state.accounts) { acc ->
                            val accExpenses = state.expenses.filter { it.account.equals(acc.name, ignoreCase = true) }
                            val accSpent = accExpenses.sumOf { it.amount }
                            val icon = when (acc.type) {
                                "CREDIT_CARD" -> Icons.Default.CreditCard
                                "CASH" -> Icons.Default.Payments
                                else -> Icons.Default.AccountBalance
                            }
                            AccountCard(
                                title = acc.name,
                                subtitle = "Spent: ₹${"%.0f".format(accSpent)}",
                                icon = icon,
                                isSelected = state.selectedAccountFilter.equals(acc.name, ignoreCase = true),
                                onClick = {
                                    if (state.selectedAccountFilter.equals(acc.name, ignoreCase = true)) {
                                        viewModel.selectAccountFilter(null)
                                    } else {
                                        viewModel.selectAccountFilter(acc.name)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Feature 3: Target Vaults (Goal-Based Savings)
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Target Vaults", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🎯", fontSize = 14.sp)
                        }
                        Text(
                            "+ New Goal",
                            color = BrandLime,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showCreateVaultDialog = true }
                        )
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.targetVaults) { vault ->
                            TargetVaultItem(
                                vault = vault,
                                onDeposit = {
                                    selectedVaultForDeposit = vault
                                    showDepositVaultDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // Spending Graph Card
            item {
                var selectedTime by remember { mutableStateOf("1M") }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(SurfacePrimary)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(32.dp))
                        .padding(20.dp)
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
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Dynamic Reactive Graph with Neon Fill
                        Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                            val path = Path()
                            val fillPath = Path()
                            val width = size.width
                            val height = size.height

                            when (selectedTime) {
                                "1W" -> {
                                    path.moveTo(0f, height * 0.7f)
                                    path.lineTo(width * 0.16f, height * 0.45f)
                                    path.lineTo(width * 0.33f, height * 0.85f)
                                    path.lineTo(width * 0.50f, height * 0.25f)
                                    path.lineTo(width * 0.66f, height * 0.60f)
                                    path.lineTo(width * 0.83f, height * 0.35f)
                                    path.lineTo(width, height * 0.15f)
                                }
                                "1M" -> {
                                    path.moveTo(0f, height * 0.8f)
                                    path.cubicTo(width * 0.2f, height * 0.8f, width * 0.3f, height * 0.2f, width * 0.5f, height * 0.4f)
                                    path.cubicTo(width * 0.7f, height * 0.6f, width * 0.8f, height * 0.1f, width, height * 0.3f)
                                }
                                else -> {
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
                                brush = Brush.verticalGradient(
                                    colors = listOf(BrandLime.copy(alpha = 0.22f), Color.Transparent)
                                )
                            )

                            drawPath(
                                path = path,
                                color = BrandLime,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        val insightText = when (selectedTime) {
                            "1W" -> "Weekly flow • Real-time bank SMS monitoring active"
                            "1M" -> "Monthly trend • Spending within stable bounds"
                            else -> "Annual perspective • Steady financial health"
                        }
                        Text(insightText, color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            // Feature 3 Part B: Live Category Budgets Overview
            item {
                if (state.categoryBudgets.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(SurfacePrimary)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(28.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Category Budgets", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("Edit", color = BrandLime, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { showBudgetDialog = true })
                            }

                            state.categoryBudgets.entries.take(3).forEach { (cat, budget) ->
                                val spent = state.expenses.filter { it.category.equals(cat, ignoreCase = true) }.sumOf { it.amount }
                                val ratio = if (budget > 0) (spent / budget).toFloat().coerceIn(0f, 1.2f) else 0f
                                val barColor = when {
                                    ratio >= 1.0f -> AccentPink
                                    ratio >= 0.75f -> AccentOrange
                                    else -> BrandLime
                                }

                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(cat, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        Text(
                                            "₹${"%.0f".format(spent)} / ₹${"%.0f".format(budget)}", 
                                            color = barColor, 
                                            fontSize = 12.sp, 
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { ratio.coerceAtMost(1f) },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = barColor,
                                        trackColor = BackgroundElevated
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Expenses Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.selectedAccountFilter != null) "Expenses (${state.selectedAccountFilter})" else "Recent Expenses",
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = TextPrimary
                    )
                    Text("${displayedExpenses.size} items", fontSize = 13.sp, color = TextSecondary)
                }
            }

            // Expense Items
            if (displayedExpenses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions found", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                itemsIndexed(displayedExpenses) { index, expense ->
                    ExpenseItem(expense, index)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // Intelligent Categorization Dialog (Triggered automatically on SMS or manually)
        if (showDialog && initialAmount != null) {
            val learnedCategory = remember(initialMerchant) {
                if (initialMerchant != null) viewModel.getLearnedCategory(initialMerchant) else null
            }
            val effectiveCategory = learnedCategory ?: initialSuggestedCategory ?: ""

            CategorizeDialog(
                amount = initialAmount,
                merchant = initialMerchant,
                accountName = initialAccount ?: "Primary Bank",
                suggestedCategory = effectiveCategory,
                categories = state.categories,
                onDismiss = { 
                    showDialog = false 
                    onDialogDismissed()
                },
                onSave = { amount, category, merchant, account ->
                    viewModel.addExpense(amount, category, merchant, account)
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
                accounts = state.accounts,
                onDismiss = { showReceiveDialog = false },
                onSave = { amount, category, _, account, _ ->
                    viewModel.addIncome(amount, category, account)
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

        if (showCreateVaultDialog) {
            CreateVaultDialog(
                onDismiss = { showCreateVaultDialog = false },
                onSave = { title, target ->
                    viewModel.addTargetVault(title, target)
                    showCreateVaultDialog = false
                }
            )
        }

        if (showDepositVaultDialog && selectedVaultForDeposit != null) {
            val vault = selectedVaultForDeposit!!
            DepositVaultDialog(
                vault = vault,
                onDismiss = { showDepositVaultDialog = false },
                onDeposit = { amount ->
                    viewModel.depositToTargetVault(vault.id, amount)
                    showDepositVaultDialog = false
                }
            )
        }

        if (showBudgetDialog) {
            CategoryBudgetDialog(
                currentBudgets = state.categoryBudgets,
                categories = state.categories,
                onDismiss = { showBudgetDialog = false },
                onSave = { cat, amt ->
                    viewModel.setCategoryBudget(cat, amt)
                }
            )
        }
    }
}

@Composable
fun AccountCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) AccentCyan.copy(alpha = 0.18f) else SurfacePrimary)
            .border(1.dp, if (isSelected) AccentCyan else BorderSubtle, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AccentCyan.copy(alpha = 0.25f) else BackgroundElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = if (isSelected) AccentCyan else TextPrimary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = if (isSelected) AccentCyan else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun TargetVaultItem(
    vault: TargetVault,
    onDeposit: () -> Unit
) {
    val progress = if (vault.targetAmount > 0) (vault.savedAmount / vault.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
    val isComplete = progress >= 1f

    Box(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(SurfacePrimary)
            .border(1.dp, if (isComplete) BrandLime else BorderSubtle, RoundedCornerShape(22.dp))
            .clickable { onDeposit() }
            .padding(14.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(vault.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(if (isComplete) "✅" else "🎯", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (isComplete) BrandLime else AccentCyan,
                trackColor = BackgroundElevated
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("₹${"%.0f".format(vault.savedAmount)}", color = if (isComplete) BrandLime else AccentCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("of ₹${"%.0f".format(vault.targetAmount)}", color = TextSecondary, fontSize = 10.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandLime.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text("+ Add", color = BrandLime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun QuickAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isPrimary: Boolean = false, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isPrimary) BrandLime else SurfacePrimary)
                .border(1.dp, if (isPrimary) BrandLime else BorderSubtle, RoundedCornerShape(20.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = if (isPrimary) PureBlack else TextPrimary, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SummaryCard(title: String, amount: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SurfacePrimary)
            .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(title, color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(amount, color = color, fontSize = 15.sp, fontWeight = FontWeight.Bold)
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
            Pair(Icons.AutoMirrored.Filled.ReceiptLong, AccentPurple)
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
            .clip(RoundedCornerShape(22.dp))
            .background(SurfacePrimary)
            .border(1.dp, BorderSubtle, RoundedCornerShape(22.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // High-contrast themed glassmorphic icon badge
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            categoryColor.copy(alpha = 0.28f),
                            categoryColor.copy(alpha = 0.08f),
                            BackgroundElevated
                        ),
                        radius = 80f
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(categoryColor.copy(alpha = 0.7f), categoryColor.copy(alpha = 0.2f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon,
                contentDescription = expense.category,
                tint = categoryColor,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(14.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            // Intelligent display: Show merchant name as primary title if available!
            val primaryTitle = expense.merchant ?: expense.category
            Text(text = primaryTitle, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (expense.merchant != null) "${expense.category} • " else "", 
                    color = TextSecondary, 
                    fontSize = 12.sp
                )
                Text(text = dateString, color = TextSecondary, fontSize = 12.sp)
            }
            if (expense.account.isNotBlank() && expense.account != "Primary Account") {
                Text(text = "via ${expense.account}", color = AccentCyan.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
        
        Text(
            text = "-₹${"%.0f".format(expense.amount)}",
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            fontSize = 17.sp
        )
    }
}

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
            Text("Add to Savings 💰", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
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
                    textStyle = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Black),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandLime,
                        unfocusedBorderColor = BorderSubtle,
                        cursorColor = BrandLime
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            Text("+$amt", color = BrandLime, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            val validAmount = amountText.toDoubleOrNull() ?: 0.0
            Button(
                onClick = { if (validAmount > 0) onSave(validAmount) },
                enabled = validAmount > 0,
                colors = ButtonDefaults.buttonColors(containerColor = BrandLime, disabledContainerColor = BackgroundElevated),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Deposit to Savings", color = if (validAmount > 0) PureBlack else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// Feature 1: Intelligent Categorization Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorizeDialog(
    amount: Double,
    merchant: String? = null,
    accountName: String = "Primary Bank",
    suggestedCategory: String? = null,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (amount: Double, category: String, merchant: String?, account: String) -> Unit,
    onAddCategory: (String) -> Unit
) {
    val defaultRecommendations = listOf("Food & Dining", "Transport", "Shopping", "Tech & Gear", "Bills & Utilities")
    val displayCategories = if (categories.isEmpty()) defaultRecommendations else categories
    
    var selectedCategory by remember { mutableStateOf(suggestedCategory.takeIf { !it.isNullOrBlank() } ?: displayCategories.first()) }
    var newCategoryText by remember { mutableStateOf("") }
    var showNewCategoryInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfacePrimary,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(32.dp)),
        title = { 
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Payment Detected", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) 
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // Large Hero Amount
                Text(
                    text = "₹${"%.2f".format(amount)}",
                    fontSize = 44.sp,
                    color = BrandLime,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                // Merchant Tag & Account Tag
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    if (merchant != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentCyan.copy(alpha = 0.18f))
                                .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("📍 $merchant", color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundElevated)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(accountName, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Text("Select Category", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(bottom = 10.dp).align(Alignment.Start))
                
                // Categories
                FlowRowCustom(items = displayCategories, selectedItem = selectedCategory) { category ->
                    selectedCategory = category
                    showNewCategoryInput = false
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
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
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                if (showNewCategoryInput) {
                    Spacer(modifier = Modifier.height(12.dp))
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
                    onSave(amount, selectedCategory, merchant, accountName) 
                },
                enabled = selectedCategory.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BrandLime, disabledContainerColor = BackgroundElevated),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(
                    "Confirm Expense • ₹${"%.0f".format(amount)}", 
                    color = if (selectedCategory.isNotBlank()) PureBlack else TextSecondary, 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 16.sp
                )
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
                    .background(if (isSelected) accentColor.copy(alpha=0.18f) else BackgroundElevated)
                    .border(1.dp, if (isSelected) accentColor else Color.Transparent, CircleShape)
                    .clickable { onSelect(category) }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(category, color = if (isSelected) accentColor else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

// Dialog: Create Target Vault
@Composable
fun CreateVaultDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, targetAmount: Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfacePrimary,
        shape = RoundedCornerShape(32.dp),
        title = { Text("New Target Vault 🎯", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title", color = TextSecondary) },
                    placeholder = { Text("e.g. MacBook Pro, Goa Trip", color = TextSecondary.copy(alpha = 0.5f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandLime, unfocusedBorderColor = BorderSubtle, cursorColor = BrandLime),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) targetText = it },
                    label = { Text("Target Amount (₹)", color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandLime, unfocusedBorderColor = BorderSubtle, cursorColor = BrandLime),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        confirmButton = {
            val amt = targetText.toDoubleOrNull() ?: 0.0
            Button(
                onClick = { if (title.isNotBlank() && amt > 0) onSave(title.trim(), amt) },
                enabled = title.isNotBlank() && amt > 0,
                colors = ButtonDefaults.buttonColors(containerColor = BrandLime, disabledContainerColor = BackgroundElevated),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Create Goal", color = if (title.isNotBlank() && amt > 0) PureBlack else TextSecondary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// Dialog: Deposit to specific Target Vault
@Composable
fun DepositVaultDialog(
    vault: TargetVault,
    onDismiss: () -> Unit,
    onDeposit: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val quickAmounts = listOf(500, 1000, 2000, 5000)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfacePrimary,
        shape = RoundedCornerShape(32.dp),
        title = { Text("Deposit to ${vault.title}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                Text(
                    "Current: ₹${"%.0f".format(vault.savedAmount)} / ₹${"%.0f".format(vault.targetAmount)}", 
                    color = AccentCyan, 
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amountText = it },
                    placeholder = { Text("Deposit Amount (₹)", color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandLime, unfocusedBorderColor = BorderSubtle, cursorColor = BrandLime),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickAmounts.forEach { q ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BackgroundElevated)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                .clickable {
                                    val curr = amountText.toDoubleOrNull() ?: 0.0
                                    amountText = (curr + q).toInt().toString()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+$q", color = BrandLime, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            val amt = amountText.toDoubleOrNull() ?: 0.0
            Button(
                onClick = { if (amt > 0) onDeposit(amt) },
                enabled = amt > 0,
                colors = ButtonDefaults.buttonColors(containerColor = BrandLime, disabledContainerColor = BackgroundElevated),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Confirm Deposit", color = if (amt > 0) PureBlack else TextSecondary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// Dialog: Category Budget Editor
@Composable
fun CategoryBudgetDialog(
    currentBudgets: Map<String, Double>,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (category: String, amount: Double) -> Unit
) {
    val displayCategories = if (categories.isEmpty()) listOf("Food & Dining", "Shopping", "Transport", "Bills & Utilities") else categories
    var selectedCat by remember { mutableStateOf(displayCategories.first()) }
    var amountText by remember { mutableStateOf(currentBudgets[selectedCat]?.toInt()?.toString() ?: "5000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfacePrimary,
        shape = RoundedCornerShape(32.dp),
        title = { Text("Set Category Budget", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                Text("Select Category", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp))
                LazyRow(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(displayCategories) { cat ->
                        val isSelected = selectedCat == cat
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) BrandLime else BackgroundElevated)
                                .clickable {
                                    selectedCat = cat
                                    amountText = currentBudgets[cat]?.toInt()?.toString() ?: "5000"
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(cat, color = if (isSelected) PureBlack else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amountText = it },
                    label = { Text("Monthly Budget for $selectedCat (₹)", color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandLime, unfocusedBorderColor = BorderSubtle, cursorColor = BrandLime),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        confirmButton = {
            val amt = amountText.toDoubleOrNull() ?: 0.0
            Button(
                onClick = {
                    if (amt > 0) {
                        onSave(selectedCat, amt)
                        onDismiss()
                    }
                },
                enabled = amt > 0,
                colors = ButtonDefaults.buttonColors(containerColor = BrandLime),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Save Budget", color = PureBlack, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}
