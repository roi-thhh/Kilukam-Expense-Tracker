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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kilukkam.data.Account
import com.example.kilukkam.data.Expense
import com.example.kilukkam.data.TargetVault
import com.example.kilukkam.theme.*
import com.example.kilukkam.ui.sunnyCardShadow
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

    Box(modifier = modifier.fillMaxSize().background(BackgroundCanvas)) {
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
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
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
                        Text("WELCOME BACK,", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                        Text(state.userName, color = TextDark, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .sunnyCardShadow(cornerRadius = 23.dp, blurRadius = 8.dp, offsetY = 2.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, BorderSubtle, CircleShape)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextDark, modifier = Modifier.size(20.dp))
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(3.dp)
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(BrandYellowPrimary)
                        )
                    }
                }
            }

            // Hero Balance Section: Signature Sunny Yellow Surface Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .sunnyCardShadow(cornerRadius = 30.dp, blurRadius = 18.dp, offsetY = 6.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(SurfaceYellowCard)
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "TOTAL NET BALANCE", 
                                color = TextDark.copy(alpha = 0.75f), 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold, 
                                letterSpacing = 1.2.sp
                            )
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceWhite.copy(alpha = 0.85f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("+14.8%", color = TextDark, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "₹${"%.2f".format(netBalance)}",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = TextDark,
                            letterSpacing = (-1.5).sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Quick Action Buttons inside Hero Panel
                        val context = androidx.compose.ui.platform.LocalContext.current
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            HeroQuickAction(icon = Icons.AutoMirrored.Filled.Send, label = "Send") {
                                com.example.kilukkam.utils.ShareUtils.shareAnalyticsAsImage(context, state.expenses, totalExpense)
                            }
                            HeroQuickAction(icon = Icons.AutoMirrored.Filled.CallMade, label = "Receive") { 
                                showReceiveDialog = true 
                            }
                            HeroQuickAction(icon = Icons.Default.Tune, label = "Budgets") { 
                                showBudgetDialog = true 
                            }
                            HeroQuickAction(icon = Icons.Default.Add, label = "Savings") { 
                                showSavingsDialog = true 
                            }
                        }
                    }
                }
            }

            // Quick Summary Metrics Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), 
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryCard(title = "Income", amount = "₹${"%.0f".format(totalIncome)}", color = AccentIncome, modifier = Modifier.weight(1f))
                    SummaryCard(title = "Expenses", amount = "₹${"%.0f".format(totalExpense)}", color = AccentExpense, modifier = Modifier.weight(1f))
                    SummaryCard(title = "Savings", amount = "₹${"%.0f".format(state.savings)}", color = TextDark, modifier = Modifier.weight(1f))
                }
            }

            // Feature 2: Multi-Account & Credit Card Management Strip
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Accounts & Cards", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        if (state.selectedAccountFilter != null) {
                            Text(
                                "Clear Filter", 
                                color = TextDark, 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BrandYellowSoft)
                                    .clickable { viewModel.selectAccountFilter(null) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
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
                        Text("Target Vaults 🎯", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text(
                            "+ New Goal",
                            color = TextDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(BrandYellowSoft)
                                .clickable { showCreateVaultDialog = true }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
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

            // Category Budgets Live Summary Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .sunnyCardShadow(cornerRadius = 24.dp, blurRadius = 10.dp, offsetY = 3.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Monthly Category Limits", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(
                                if (state.categoryBudgets.isEmpty()) "+ Set Limits" else "Manage", 
                                color = TextDark, 
                                fontSize = 12.sp, 
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showBudgetDialog = true }
                            )
                        }
                        if (state.categoryBudgets.isEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No category limits configured yet. Tap above to add limits.", color = TextSecondary, fontSize = 13.sp)
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))

                            state.categoryBudgets.forEach { (cat, limit) ->
                                val spent = state.expenses.filter { it.category.equals(cat, ignoreCase = true) }.sumOf { it.amount }
                                val ratio = if (limit > 0) (spent / limit).toFloat().coerceIn(0f, 1.2f) else 0f
                                val isOver = spent > limit
                                val isWarning = ratio >= 0.8f && !isOver

                                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(cat, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("₹${"%.0f".format(spent)} / ₹${"%.0f".format(limit)}", fontSize = 12.sp, color = TextSecondary)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isOver) AccentExpense.copy(alpha = 0.15f) else if (isWarning) AccentWarning.copy(alpha = 0.15f) else AccentIncome.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    if (isOver) "Exceeded" else if (isWarning) "Warning" else "Healthy",
                                                    color = if (isOver) AccentExpense else if (isWarning) AccentWarning else AccentIncome,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { ratio.coerceAtMost(1f) },
                                        modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                                        color = if (isOver) AccentExpense else if (isWarning) AccentWarning else BrandYellowPrimary,
                                        trackColor = BackgroundMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Spending Flow Card (Smooth Sunny Graph)
            item {
                var selectedTime by remember { mutableStateOf("1M") }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .sunnyCardShadow(cornerRadius = 28.dp, blurRadius = 12.dp, offsetY = 4.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(28.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Spending Flow", color = TextDark, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(BackgroundMuted).padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("1W", "1M", "1Y").forEach { time ->
                                    val isSelected = selectedTime == time
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) BrandYellowPrimary else Color.Transparent)
                                            .clickable { selectedTime = time }
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(time, color = if (isSelected) TextOnYellow else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dynamic Bezier Spending Curve in Sunny Yellow
                        Canvas(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                            val path = Path()
                            val fillPath = Path()
                            
                            val rawPoints = when (selectedTime) {
                                "1W" -> listOf(0.2f, 0.45f, 0.3f, 0.75f, 0.4f, 0.85f, 0.6f)
                                "1M" -> listOf(0.15f, 0.35f, 0.25f, 0.6f, 0.45f, 0.8f, 0.55f, 0.9f, 0.7f)
                                else -> listOf(0.3f, 0.2f, 0.5f, 0.4f, 0.7f, 0.6f, 0.85f, 0.75f, 0.95f)
                            }
                            
                            val widthStep = size.width / (rawPoints.size - 1)
                            val points = rawPoints.mapIndexed { index, fl -> 
                                Offset(index * widthStep, size.height * (1f - fl))
                            }

                            if (points.isNotEmpty()) {
                                path.moveTo(points.first().x, points.first().y)
                                fillPath.moveTo(points.first().x, size.height)
                                fillPath.lineTo(points.first().x, points.first().y)

                                for (i in 0 until points.size - 1) {
                                    val p0 = points[i]
                                    val p1 = points[i + 1]
                                    val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2, p0.y)
                                    val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2, p1.y)
                                    path.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                                    fillPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                                }

                                fillPath.lineTo(points.last().x, size.height)
                                fillPath.close()

                                // Soft sunny yellow gradient fill
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(BrandYellowPrimary.copy(alpha = 0.35f), Color.Transparent)
                                    )
                                )

                                // Solid stroke
                                drawPath(
                                    path = path,
                                    color = BrandYellowWarm,
                                    style = Stroke(width = 3.5.dp.toPx())
                                )
                            }
                        }
                    }
                }
            }

            // Transactions Activity Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Activity", color = TextDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("${displayedExpenses.size} items", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            if (displayedExpenses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No expenses logged yet", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Incoming banking SMS or manual adds appear here", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                itemsIndexed(displayedExpenses.take(15)) { index, expense ->
                    ExpenseItem(expense = expense, index = index)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // Feature 1: SMS Intent Categorization Dialog
        if (showDialog && initialAmount != null) {
            CategorizeDialog(
                amount = initialAmount,
                merchant = initialMerchant,
                accountName = initialAccount ?: "Primary Bank",
                suggestedCategory = initialSuggestedCategory,
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
                onSaveBudget = { cat, amt ->
                    viewModel.setCategoryBudget(cat, amt)
                },
                onAddCategory = { cat, budget ->
                    viewModel.addCategory(cat, budget)
                },
                onDeleteCategory = { cat ->
                    viewModel.deleteCategory(cat)
                }
            )
        }
    }
}

@Composable
fun HeroQuickAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceWhite)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = TextDark, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, color = TextDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            .sunnyCardShadow(cornerRadius = 18.dp, blurRadius = 8.dp, offsetY = 2.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) BrandYellowPrimary else SurfaceWhite)
            .border(1.dp, if (isSelected) BorderYellow else BorderSubtle, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) SurfaceWhite else BackgroundMuted),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = TextDark, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = TextDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = if (isSelected) TextDark.copy(alpha = 0.75f) else TextSecondary, fontSize = 11.sp)
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
            .width(175.dp)
            .sunnyCardShadow(cornerRadius = 22.dp, blurRadius = 10.dp, offsetY = 3.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceWhite)
            .border(1.dp, if (isComplete) AccentIncome else BorderSubtle, RoundedCornerShape(22.dp))
            .clickable { onDeposit() }
            .padding(14.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(vault.title, color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(if (isComplete) "✅" else "🎯", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (isComplete) AccentIncome else BrandYellowPrimary,
                trackColor = BackgroundMuted
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("₹${"%.0f".format(vault.savedAmount)}", color = TextDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("of ₹${"%.0f".format(vault.targetAmount)}", color = TextSecondary, fontSize = 10.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandYellowSoft)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("+ Add", color = TextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, amount: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .sunnyCardShadow(cornerRadius = 20.dp, blurRadius = 8.dp, offsetY = 2.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(title, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(amount, color = color, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense, index: Int) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(expense.timestamp))
    
    val catLower = expense.category.lowercase()
    val (categoryIcon, catBg, catColor) = when {
        catLower.contains("food") || catLower.contains("dining") || catLower.contains("burger") || catLower.contains("pizza") || catLower.contains("cafe") || catLower.contains("tea") || catLower.contains("coffee") -> 
            Triple(Icons.Default.Restaurant, CatDiningBg, CatDiningIcon)
        catLower.contains("transport") || catLower.contains("fuel") || catLower.contains("travel") || catLower.contains("cab") || catLower.contains("uber") || catLower.contains("auto") -> 
            Triple(Icons.Default.DirectionsCar, CatTransitBg, CatTransitIcon)
        catLower.contains("shop") || catLower.contains("grocer") || catLower.contains("mart") || catLower.contains("cloth") || catLower.contains("amazon") || catLower.contains("flipkart") -> 
            Triple(Icons.Default.ShoppingCart, CatShoppingBg, CatShoppingIcon)
        catLower.contains("bill") || catLower.contains("electric") || catLower.contains("wifi") || catLower.contains("recharge") || catLower.contains("rent") -> 
            Triple(Icons.AutoMirrored.Filled.ReceiptLong, CatBillsBg, CatBillsIcon)
        catLower.contains("tech") || catLower.contains("laptop") || catLower.contains("phone") || catLower.contains("gadget") -> 
            Triple(Icons.Default.Devices, CatTechBg, CatTechIcon)
        catLower.contains("movie") || catLower.contains("game") || catLower.contains("entertain") || catLower.contains("netflix") -> 
            Triple(Icons.Default.Movie, CatShoppingBg, CatShoppingIcon)
        catLower.contains("health") || catLower.contains("medic") || catLower.contains("pharma") || catLower.contains("gym") || catLower.contains("hospital") -> 
            Triple(Icons.Default.LocalHospital, CatHealthBg, CatHealthIcon)
        catLower.contains("salary") || catLower.contains("income") || catLower.contains("bank") -> 
            Triple(Icons.Default.AccountBalance, BrandYellowSoft, TextDark)
        else -> 
            Triple(Icons.Default.Payments, BackgroundMuted, TextDark)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .sunnyCardShadow(cornerRadius = 20.dp, blurRadius = 8.dp, offsetY = 2.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceWhite)
            .border(1.dp, BorderSubtle, RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Squircle category icon with pastel background & distinct icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(catBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = expense.category,
                    tint = catColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                val primaryTitle = expense.merchant ?: expense.category
                Text(text = primaryTitle, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 15.sp)
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
                    Text(text = "via ${expense.account}", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
            
            Text(
                text = "-₹${"%.0f".format(expense.amount)}",
                fontWeight = FontWeight.ExtraBold,
                color = TextDark,
                fontSize = 17.sp
            )
        }
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
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text("Add to Savings 💰", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
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
                    placeholder = { Text("0.00", color = TextMuted, fontSize = 26.sp) },
                    prefix = { Text("₹ ", color = TextDark, fontSize = 26.sp, fontWeight = FontWeight.Black) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    textStyle = androidx.compose.ui.text.TextStyle(color = TextDark, fontSize = 26.sp, fontWeight = FontWeight.Black),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandYellowPrimary,
                        unfocusedBorderColor = BorderSubtle,
                        cursorColor = TextDark
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
                                .background(BackgroundMuted)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                .clickable {
                                    val curr = amountText.toDoubleOrNull() ?: 0.0
                                    amountText = (curr + amt).toInt().toString()
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+$amt", color = TextDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                colors = ButtonDefaults.buttonColors(containerColor = BrandYellowPrimary, disabledContainerColor = BackgroundMuted),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Deposit to Savings", color = if (validAmount > 0) TextOnYellow else TextMuted, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .sunnyCardShadow(cornerRadius = 28.dp, blurRadius = 16.dp, offsetY = 6.dp)
            .border(1.dp, BorderSubtle, RoundedCornerShape(28.dp)),
        title = { 
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Payment Detected", color = TextDark, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) 
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                // Large Hero Amount
                Text(
                    text = "₹${"%.2f".format(amount)}",
                    fontSize = 42.sp,
                    color = TextDark,
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
                                .background(BrandYellowSoft)
                                .border(1.dp, BorderYellow, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("📍 $merchant", color = TextDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundMuted)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(accountName, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Text("Select Category", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 10.dp).align(Alignment.Start))
                
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
                    color = if (showNewCategoryInput) BrandYellowSoft else BackgroundMuted,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (showNewCategoryInput) BorderYellow else BorderSubtle)
                ) {
                    Text(
                        "+ Create New", 
                        color = TextDark, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }

                if (showNewCategoryInput) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newCategoryText,
                        onValueChange = { newCategoryText = it },
                        placeholder = { Text("Category Name", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandYellowPrimary,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            cursorColor = TextDark
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
                                Icon(Icons.Default.Check, contentDescription = "Save", tint = TextDark)
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
                colors = ButtonDefaults.buttonColors(containerColor = BrandYellowPrimary, disabledContainerColor = BackgroundMuted),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(
                    "Confirm Expense • ₹${"%.0f".format(amount)}", 
                    color = if (selectedCategory.isNotBlank()) TextOnYellow else TextMuted, 
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
            
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) BrandYellowPrimary else BackgroundMuted)
                    .border(1.dp, if (isSelected) BorderYellow else BorderSubtle, CircleShape)
                    .clickable { onSelect(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    category, 
                    color = if (isSelected) TextOnYellow else TextDark, 
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, 
                    fontSize = 13.sp
                )
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
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(28.dp),
        title = { Text("New Target Vault 🎯", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title", color = TextSecondary) },
                    placeholder = { Text("e.g. MacBook Pro, Goa Trip", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandYellowPrimary, unfocusedBorderColor = BorderSubtle, cursorColor = TextDark),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) targetText = it },
                    label = { Text("Target Amount (₹)", color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandYellowPrimary, unfocusedBorderColor = BorderSubtle, cursorColor = TextDark),
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
                colors = ButtonDefaults.buttonColors(containerColor = BrandYellowPrimary, disabledContainerColor = BackgroundMuted),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Create Goal", color = if (title.isNotBlank() && amt > 0) TextOnYellow else TextMuted, fontWeight = FontWeight.Bold)
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
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(28.dp),
        title = { Text("Deposit to ${vault.title}", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                Text(
                    "Current: ₹${"%.0f".format(vault.savedAmount)} / ₹${"%.0f".format(vault.targetAmount)}", 
                    color = TextSecondary, 
                    fontSize = 13.sp, 
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) amountText = it },
                    placeholder = { Text("Deposit Amount (₹)", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandYellowPrimary, unfocusedBorderColor = BorderSubtle, cursorColor = TextDark),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickAmounts.forEach { q ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(BackgroundMuted)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                                .clickable {
                                    val curr = amountText.toDoubleOrNull() ?: 0.0
                                    amountText = (curr + q).toInt().toString()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+$q", color = TextDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                colors = ButtonDefaults.buttonColors(containerColor = BrandYellowPrimary, disabledContainerColor = BackgroundMuted),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Confirm Deposit", color = if (amt > 0) TextOnYellow else TextMuted, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// Dialog: Category Limits & Category Manager
@Composable
fun CategoryBudgetDialog(
    currentBudgets: Map<String, Double>,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSaveBudget: (category: String, amount: Double) -> Unit,
    onAddCategory: (category: String, budget: Double?) -> Unit,
    onDeleteCategory: (category: String) -> Unit
) {
    val allCategories = (categories + currentBudgets.keys).distinct().filter { it.isNotBlank() }
    
    var isAddingCategory by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryBudget by remember { mutableStateOf("5000") }
    var editingCategory by remember { mutableStateOf<String?>(null) }
    var editAmountText by remember { mutableStateOf("") }
    var categoryToDelete by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(28.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Category Limits", color = TextDark, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Text("Set monthly limits, add or delete categories", color = TextSecondary, fontSize = 12.sp)
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // "+ Add New Category" button or form
                if (!isAddingCategory) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrandYellowPrimary)
                            .clickable { isAddingCategory = true }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = TextOnYellow, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add New Category", color = TextOnYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                } else {
                    // Expandable Add Category Form Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(BackgroundMuted)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("Create New Category", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newCategoryName,
                                onValueChange = { newCategoryName = it },
                                label = { Text("Category Name (e.g. Subscriptions, Gym)", color = TextSecondary, fontSize = 12.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandYellowPrimary,
                                    unfocusedBorderColor = BorderSubtle,
                                    focusedTextColor = TextDark,
                                    unfocusedTextColor = TextDark,
                                    cursorColor = TextDark,
                                    focusedContainerColor = SurfaceWhite,
                                    unfocusedContainerColor = SurfaceWhite
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newCategoryBudget,
                                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) newCategoryBudget = it },
                                label = { Text("Monthly Limit (₹) - Optional", color = TextSecondary, fontSize = 12.sp) },
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BrandYellowPrimary,
                                    unfocusedBorderColor = BorderSubtle,
                                    focusedTextColor = TextDark,
                                    unfocusedTextColor = TextDark,
                                    cursorColor = TextDark,
                                    focusedContainerColor = SurfaceWhite,
                                    unfocusedContainerColor = SurfaceWhite
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { isAddingCategory = false }) {
                                    Text("Cancel", color = TextSecondary, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        val trimmed = newCategoryName.trim()
                                        if (trimmed.isNotBlank()) {
                                            val budget = newCategoryBudget.toDoubleOrNull()
                                            onAddCategory(trimmed, budget)
                                            newCategoryName = ""
                                            newCategoryBudget = "5000"
                                            isAddingCategory = false
                                        }
                                    },
                                    enabled = newCategoryName.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandYellowPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Add", color = TextOnYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Categories List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (allCategories.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No categories yet. Tap Add above to create one.", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    } else {
                        items(allCategories) { cat ->
                            val limit = currentBudgets[cat]
                            val catLower = cat.lowercase()
                            val categoryIcon = when {
                                catLower.contains("food") || catLower.contains("dining") || catLower.contains("burger") || catLower.contains("pizza") || catLower.contains("cafe") -> Icons.Default.Restaurant
                                catLower.contains("transport") || catLower.contains("fuel") || catLower.contains("travel") || catLower.contains("cab") || catLower.contains("uber") -> Icons.Default.DirectionsCar
                                catLower.contains("shop") || catLower.contains("grocer") || catLower.contains("mart") || catLower.contains("amazon") -> Icons.Default.ShoppingCart
                                catLower.contains("bill") || catLower.contains("electric") || catLower.contains("wifi") || catLower.contains("recharge") -> Icons.AutoMirrored.Filled.ReceiptLong
                                catLower.contains("tech") || catLower.contains("phone") || catLower.contains("gadget") -> Icons.Default.Devices
                                catLower.contains("movie") || catLower.contains("entertain") || catLower.contains("netflix") || catLower.contains("game") -> Icons.Default.Movie
                                catLower.contains("health") || catLower.contains("medic") || catLower.contains("gym") -> Icons.Default.LocalHospital
                                catLower.contains("salary") || catLower.contains("income") || catLower.contains("bank") -> Icons.Default.AccountBalance
                                else -> Icons.Default.Payments
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BackgroundMuted)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Category Icon Box
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(BrandYellowPrimary.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(categoryIcon, contentDescription = cat, tint = TextDark, modifier = Modifier.size(18.dp))
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        // Category Name & Current Limit
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(cat, color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(
                                                if (limit != null && limit > 0) "Limit: ₹%.0f / mo".format(limit) else "No limit set",
                                                color = if (limit != null && limit > 0) TextSecondary else TextMuted,
                                                fontSize = 11.sp
                                            )
                                        }

                                        // Edit Limit Button
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(SurfaceWhite)
                                                .border(1.dp, BorderSubtle, CircleShape)
                                                .clickable {
                                                    if (editingCategory == cat) {
                                                        editingCategory = null
                                                    } else {
                                                        editingCategory = cat
                                                        editAmountText = currentBudgets[cat]?.toInt()?.toString() ?: "5000"
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit Limit", tint = TextDark, modifier = Modifier.size(15.dp))
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        // Delete Category Button
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(AccentExpense.copy(alpha = 0.12f))
                                                .clickable { categoryToDelete = cat },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Category", tint = AccentExpense, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    // Inline Edit Field when expanding edit mode
                                    if (editingCategory == cat) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = editAmountText,
                                                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) editAmountText = it },
                                                label = { Text("Monthly Limit (₹)", fontSize = 11.sp) },
                                                singleLine = true,
                                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = BrandYellowPrimary,
                                                    unfocusedBorderColor = BorderSubtle,
                                                    focusedContainerColor = SurfaceWhite,
                                                    unfocusedContainerColor = SurfaceWhite
                                                ),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = {
                                                    val amt = editAmountText.toDoubleOrNull() ?: 0.0
                                                    if (amt > 0) {
                                                        onSaveBudget(cat, amt)
                                                        editingCategory = null
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = BrandYellowPrimary),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Save", color = TextOnYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandYellowPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Done", color = TextOnYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    )

    // Delete Confirmation Sub-Dialog
    if (categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Delete Category?", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Text(
                    "Are you sure you want to delete '$categoryToDelete'? Its monthly spending limit will also be removed.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCategory(categoryToDelete!!)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentExpense),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
