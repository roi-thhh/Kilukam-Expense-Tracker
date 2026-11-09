package com.example.kilukkam.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kilukkam.data.DebtRecord
import com.example.kilukkam.data.DebtStatus
import com.example.kilukkam.data.DebtType
import com.example.kilukkam.theme.*
import com.example.kilukkam.ui.sunnyCardShadow
import java.text.SimpleDateFormat
import java.util.*

enum class DueFilter {
    ALL, TO_COLLECT, TO_PAY, SETTLED
}

@Composable
fun DuesScreen(
    debts: List<DebtRecord>,
    onAddDebt: (personName: String, amount: Double, type: DebtType, note: String?, dueDate: Long?, phoneNumber: String?) -> Unit,
    onSettleDebt: (debtId: String) -> Unit,
    onPartialRepayment: (debtId: String, amount: Double, note: String?) -> Unit,
    onDeleteDebt: (debtId: String) -> Unit
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf(DueFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDebtForSettle by remember { mutableStateOf<DebtRecord?>(null) }
    var selectedDebtToDelete by remember { mutableStateOf<DebtRecord?>(null) }

    // Aggregate statistics
    val activeDebts = debts.filter { it.status != DebtStatus.SETTLED }
    val totalToCollect = activeDebts
        .filter { it.type == DebtType.LENT }
        .sumOf { it.remainingAmount }
    val totalToPay = activeDebts
        .filter { it.type == DebtType.BORROWED }
        .sumOf { it.remainingAmount }
    val netPosition = totalToCollect - totalToPay

    val countToCollect = activeDebts.count { it.type == DebtType.LENT }
    val countToPay = activeDebts.count { it.type == DebtType.BORROWED }
    val countSettled = debts.count { it.status == DebtStatus.SETTLED }

    // Filtered list
    val filteredDebts = debts.filter { debt ->
        val matchesFilter = when (selectedFilter) {
            DueFilter.ALL -> true
            DueFilter.TO_COLLECT -> debt.type == DebtType.LENT && debt.status != DebtStatus.SETTLED
            DueFilter.TO_PAY -> debt.type == DebtType.BORROWED && debt.status != DebtStatus.SETTLED
            DueFilter.SETTLED -> debt.status == DebtStatus.SETTLED
        }
        val matchesSearch = if (searchQuery.isBlank()) true else {
            debt.personName.contains(searchQuery, ignoreCase = true) ||
            (debt.note?.contains(searchQuery, ignoreCase = true) == true)
        }
        matchesFilter && matchesSearch
    }

    Scaffold(
        containerColor = BackgroundCanvas,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BrandYellowPrimary,
                contentColor = TextDark,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .sunnyCardShadow(cornerRadius = 28.dp, blurRadius = 14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Due", tint = TextDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Due", fontWeight = FontWeight.Bold, color = TextDark)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Friends & Dues",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = TextDark,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "Track money lent, borrowed & spot-pays",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Quick Add Mini Pill
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceWhite,
                                contentColor = TextDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextDark)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. Hero Summary Cards
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // "You'll Get" Card
                        HeroMetricCard(
                            modifier = Modifier.weight(1f),
                            title = "You'll Get",
                            amount = totalToCollect,
                            countText = "$countToCollect ${if (countToCollect == 1) "friend owes" else "friends owe"} you",
                            accentColor = AccentIncome,
                            icon = Icons.Filled.ArrowDownward,
                            badgeText = "Receivable"
                        )

                        // "You'll Give" Card
                        HeroMetricCard(
                            modifier = Modifier.weight(1f),
                            title = "You'll Give",
                            amount = totalToPay,
                            countText = "$countToPay ${if (countToPay == 1) "person to repay" else "people to repay"}",
                            accentColor = AccentExpense,
                            icon = Icons.Filled.ArrowUpward,
                            badgeText = "Payable"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Net Position Banner
                    NetPositionBanner(netPosition = netPosition)
                }
            }

            // 3. Search Bar
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .sunnyCardShadow(cornerRadius = 14.dp, blurRadius = 6.dp),
                        placeholder = { Text("Search by friend name or note...", fontSize = 13.sp, color = TextMuted) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear", tint = TextSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceWhite,
                            unfocusedContainerColor = SurfaceWhite,
                            focusedBorderColor = BrandYellowPrimary,
                            unfocusedBorderColor = BorderSubtle,
                            cursorColor = TextDark
                        )
                    )
                }
            }

            // 4. Filter Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            label = "All (${debts.size})",
                            isSelected = selectedFilter == DueFilter.ALL,
                            onClick = { selectedFilter = DueFilter.ALL }
                        )
                    }
                    item {
                        FilterChip(
                            label = "You'll Get ($countToCollect)",
                            isSelected = selectedFilter == DueFilter.TO_COLLECT,
                            onClick = { selectedFilter = DueFilter.TO_COLLECT },
                            highlightColor = AccentIncome
                        )
                    }
                    item {
                        FilterChip(
                            label = "You'll Give ($countToPay)",
                            isSelected = selectedFilter == DueFilter.TO_PAY,
                            onClick = { selectedFilter = DueFilter.TO_PAY },
                            highlightColor = AccentExpense
                        )
                    }
                    item {
                        FilterChip(
                            label = "Settled ($countSettled)",
                            isSelected = selectedFilter == DueFilter.SETTLED,
                            onClick = { selectedFilter = DueFilter.SETTLED }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 5. Debt List Items
            if (filteredDebts.isEmpty()) {
                item {
                    EmptyDuesView(
                        filter = selectedFilter,
                        onAddClick = { showAddDialog = true }
                    )
                }
            } else {
                items(
                    items = filteredDebts,
                    key = { it.id }
                ) { debt ->
                    DueItemCard(
                        debt = debt,
                        onSettleClick = { selectedDebtForSettle = debt },
                        onDeleteClick = { selectedDebtToDelete = debt },
                        onRemindClick = {
                            sendWhatsAppReminder(context, debt)
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    // Dialog: Add New Due
    if (showAddDialog) {
        val existingFriends = debts.map { it.personName }.distinct().take(6)
        AddDueDialog(
            existingFriends = existingFriends,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, amount, type, note, dueDate, phone ->
                onAddDebt(name, amount, type, note, dueDate, phone)
                showAddDialog = false
            }
        )
    }

    // Dialog: Settle Up / Partial Repayment
    selectedDebtForSettle?.let { debt ->
        SettleUpDialog(
            debt = debt,
            onDismiss = { selectedDebtForSettle = null },
            onFullSettle = {
                onSettleDebt(debt.id)
                selectedDebtForSettle = null
            },
            onPartialRepay = { amount, note ->
                onPartialRepayment(debt.id, amount, note)
                selectedDebtForSettle = null
            }
        )
    }

    // Dialog: Confirm Delete
    selectedDebtToDelete?.let { debt ->
        AlertDialog(
            onDismissRequest = { selectedDebtToDelete = null },
            title = { Text("Delete Record?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this record for ${debt.personName}? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteDebt(debt.id)
                        selectedDebtToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = AccentExpense)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDebtToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ==========================================
// COMPOSABLE SUBCOMPONENTS
// ==========================================

@Composable
fun HeroMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: Double,
    countText: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeText: String
) {
    Card(
        modifier = modifier.sunnyCardShadow(cornerRadius = 18.dp, blurRadius = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Text(
                text = "₹${String.format(Locale.getDefault(), "%,.0f", amount)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextDark,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = countText,
                fontSize = 11.sp,
                color = TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun NetPositionBanner(netPosition: Double) {
    val (text, bgColor, textColor, icon) = when {
        netPosition > 0 -> Quadruple(
            "Overall Net: You are owed ₹${String.format(Locale.getDefault(), "%,.0f", netPosition)} in total",
            AccentIncome.copy(alpha = 0.1f),
            AccentIncome,
            Icons.Filled.TrendingUp
        )
        netPosition < 0 -> Quadruple(
            "Overall Net: You owe ₹${String.format(Locale.getDefault(), "%,.0f", -netPosition)} in total",
            AccentExpense.copy(alpha = 0.1f),
            AccentExpense,
            Icons.Filled.TrendingDown
        )
        else -> Quadruple(
            "All squared up! You have zero net balance with friends",
            BrandYellowSoft,
            TextDark,
            Icons.Filled.CheckCircle
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    highlightColor: Color? = null
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) {
            highlightColor ?: BrandYellowPrimary
        } else {
            SurfaceWhite
        },
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) Color.Transparent else BorderSubtle
        )
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) {
                if (highlightColor != null) Color.White else TextDark
            } else {
                TextSecondary
            },
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun DueItemCard(
    debt: DebtRecord,
    onSettleClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRemindClick: () -> Unit
) {
    val isLent = debt.type == DebtType.LENT
    val isSettled = debt.status == DebtStatus.SETTLED
    val dateFormat = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }
    val formattedDate = remember(debt.timestamp) { dateFormat.format(Date(debt.timestamp)) }

    // Initials & Color seed for avatar
    val initials = remember(debt.personName) {
        debt.personName.trim().split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
            .ifEmpty { "?" }
    }
    val avatarBg = remember(debt.personName) {
        val hues = listOf(
            Color(0xFFFFEAD1),
            Color(0xFFE3F2FD),
            Color(0xFFFFE1EA),
            Color(0xFFEDE7F6),
            Color(0xFFFFF8E1),
            Color(0xFFE8F5E9)
        )
        val index = (debt.personName.hashCode().let { if (it < 0) -it else it }) % hues.size
        hues[index]
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .sunnyCardShadow(cornerRadius = 16.dp, blurRadius = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top Row: Avatar + Name + Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar Circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(avatarBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name & Note
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = debt.personName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (!debt.note.isNullOrBlank()) "${debt.note} · $formattedDate" else formattedDate,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Amount Pill
                Column(horizontalAlignment = Alignment.End) {
                    if (isSettled) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentIncome.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = AccentIncome, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Settled",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentIncome
                                )
                            }
                        }
                    } else {
                        val amountColor = if (isLent) AccentIncome else AccentExpense
                        val prefix = if (isLent) "+₹" else "-₹"
                        Text(
                            text = "$prefix${String.format(Locale.getDefault(), "%,.0f", debt.remainingAmount)}",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = amountColor
                        )
                        Text(
                            text = if (isLent) "to collect" else "to pay",
                            fontSize = 10.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Progress bar if partial settlement
            if (debt.paidAmount > 0 && !isSettled) {
                Spacer(modifier = Modifier.height(10.dp))
                val progress = (debt.paidAmount / debt.originalAmount).toFloat().coerceIn(0f, 1f)
                Column {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isLent) AccentIncome else AccentExpense,
                        trackColor = BackgroundMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Paid ₹${String.format(Locale.getDefault(), "%,.0f", debt.paidAmount)}",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Total ₹${String.format(Locale.getDefault(), "%,.0f", debt.originalAmount)}",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            // Optional Due Date chip
            debt.dueDate?.let { due ->
                Spacer(modifier = Modifier.height(6.dp))
                val now = System.currentTimeMillis()
                val isOverdue = due < now && !isSettled
                val dueFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                val dueStr = dueFormat.format(Date(due))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isOverdue) AccentExpense.copy(alpha = 0.1f) else BackgroundMuted
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = if (isOverdue) AccentExpense else TextSecondary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isOverdue) "Overdue (Due $dueStr)" else "Due $dueStr",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isOverdue) AccentExpense else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BorderSubtle, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // WhatsApp Reminder Button (only for Lent + Unsettled)
                if (isLent && !isSettled) {
                    TextButton(
                        onClick = onRemindClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF25D366)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Remind", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isSettled) {
                        Button(
                            onClick = onSettleClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandYellowPrimary,
                                contentColor = TextDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextDark)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Settle Up", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Delete Icon Button
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyDuesView(
    filter: DueFilter,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .sunnyCardShadow(cornerRadius = 18.dp, blurRadius = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BrandYellowSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountBalanceWallet,
                    contentDescription = null,
                    tint = TextDark,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = when (filter) {
                    DueFilter.ALL -> "No Dues Recorded"
                    DueFilter.TO_COLLECT -> "Nobody Owes You"
                    DueFilter.TO_PAY -> "You Owe Nothing"
                    DueFilter.SETTLED -> "No Settled Records"
                },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "When you pay for a friend, split a bill, or lend money, record it here to track repayments effortlessly.",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Normal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandYellowPrimary,
                    contentColor = TextDark
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = TextDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add First Due", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// DIALOGS: ADD DUE & SETTLE UP
// ==========================================

@Composable
fun AddDueDialog(
    existingFriends: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (personName: String, amount: Double, type: DebtType, note: String?, dueDate: Long?, phoneNumber: String?) -> Unit
) {
    var personName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var debtType by remember { mutableStateOf(DebtType.LENT) }
    var selectedDaysOffset by remember { mutableStateOf<Int?>(null) } // 3, 7, 30 days
    val isFormValid = personName.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0.0

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Due / Loan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Segmented Toggle: I Lent vs I Borrowed
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundMuted)
                        .padding(4.dp)
                ) {
                    // "I Lent" Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (debtType == DebtType.LENT) AccentIncome else Color.Transparent)
                            .clickable { debtType = DebtType.LENT }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "I Lent (They Owe)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (debtType == DebtType.LENT) Color.White else TextSecondary
                        )
                    }

                    // "I Borrowed" Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (debtType == DebtType.BORROWED) AccentExpense else Color.Transparent)
                            .clickable { debtType = DebtType.BORROWED }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "I Borrowed (I Owe)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (debtType == DebtType.BORROWED) Color.White else TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Person Name Field
                OutlinedTextField(
                    value = personName,
                    onValueChange = { personName = it },
                    label = { Text("Friend / Person Name") },
                    placeholder = { Text("e.g. Rahul, Sneha") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandYellowPrimary,
                        unfocusedBorderColor = BorderSubtle
                    )
                )

                // Quick Friend Suggestions
                if (existingFriends.isNotEmpty() && personName.isBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(existingFriends) { friend ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = BackgroundMuted,
                                modifier = Modifier.clickable { personName = friend }
                            ) {
                                Text(
                                    text = friend,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Amount Field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Amount (₹)") },
                    placeholder = { Text("0.00") },
                    leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandYellowPrimary,
                        unfocusedBorderColor = BorderSubtle
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Note / Purpose
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Reason / Note (Optional)") },
                    placeholder = { Text("e.g. Dinner split, Cab fare, Movie tickets") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandYellowPrimary,
                        unfocusedBorderColor = BorderSubtle
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone Number (Optional)
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number (Optional, for WhatsApp)") },
                    placeholder = { Text("+91 98765 43210") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandYellowPrimary,
                        unfocusedBorderColor = BorderSubtle
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Optional Due Date Shortcuts
                Text("Expected Return (Optional):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(3 to "3 Days", 7 to "1 Week", 30 to "1 Month").forEach { (days, label) ->
                        val isSelected = selectedDaysOffset == days
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) BrandYellowPrimary else BackgroundMuted,
                            modifier = Modifier.clickable {
                                selectedDaysOffset = if (isSelected) null else days
                            }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = TextDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Save Button
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        val dueDate = selectedDaysOffset?.let { System.currentTimeMillis() + (it * 86400000L) }
                        onConfirm(personName, amount, debtType, note, dueDate, phoneNumber)
                    },
                    enabled = isFormValid,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandYellowPrimary,
                        contentColor = TextDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text("Save Due Record", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SettleUpDialog(
    debt: DebtRecord,
    onDismiss: () -> Unit,
    onFullSettle: () -> Unit,
    onPartialRepay: (amount: Double, note: String?) -> Unit
) {
    var isPartialMode by remember { mutableStateOf(false) }
    var partialAmountText by remember { mutableStateOf("") }
    var settlementNote by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settle Up with ${debt.personName}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TextDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Summary Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BackgroundMuted,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (debt.type == DebtType.LENT) "Pending to collect from ${debt.personName}:" else "You owe ${debt.personName}:",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "₹${String.format(Locale.getDefault(), "%,.0f", debt.remainingAmount)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = if (debt.type == DebtType.LENT) AccentIncome else AccentExpense
                        )
                        if (!debt.note.isNullOrBlank()) {
                            Text(text = "For: ${debt.note}", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isPartialMode) {
                    // Option 1: Full Settle Button
                    Button(
                        onClick = onFullSettle,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandYellowPrimary,
                            contentColor = TextDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = TextDark)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Settle Full Amount (₹${String.format(Locale.getDefault(), "%,.0f", debt.remainingAmount)})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 2: Switch to Partial Payment
                    OutlinedButton(
                        onClick = { isPartialMode = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Text("Record Partial Repayment", color = TextDark, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                } else {
                    // Partial Payment Input Form
                    Text("Enter Partial Amount:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = partialAmountText,
                        onValueChange = { partialAmountText = it.filter { char -> char.isDigit() || char == '.' } },
                        label = { Text("Repayment Amount (₹)") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, color = TextDark) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandYellowPrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = settlementNote,
                        onValueChange = { settlementNote = it },
                        label = { Text("Note (e.g. Paid cash, UPI)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandYellowPrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val partialVal = partialAmountText.toDoubleOrNull() ?: 0.0
                    val isPartialValid = partialVal > 0.0 && partialVal <= debt.remainingAmount

                    Button(
                        onClick = { onPartialRepay(partialVal, settlementNote) },
                        enabled = isPartialValid,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandYellowPrimary,
                            contentColor = TextDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text("Confirm Repayment", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    TextButton(
                        onClick = { isPartialMode = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Full Settlement", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// WHATSAPP / SMS REMINDER HELPER
// ==========================================

fun sendWhatsAppReminder(context: Context, debt: DebtRecord) {
    val cleanName = debt.personName.trim()
    val notePart = if (!debt.note.isNullOrBlank()) " for ${debt.note}" else ""
    val amountFormatted = String.format(Locale.getDefault(), "%,.0f", debt.remainingAmount)
    val text = "Hey $cleanName! Just a friendly reminder regarding the ₹$amountFormatted$notePart on Kilukkam. Whenever you're free 😊"

    val phone = debt.phoneNumber?.replace(Regex("[^0-9]"), "")
    if (!phone.isNullOrBlank()) {
        try {
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(text)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
            return
        } catch (_: Exception) {
            // Fallback to generic share below
        }
    }

    // Generic share intent
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    try {
        context.startActivity(Intent.createChooser(shareIntent, "Send Reminder via"))
    } catch (_: Exception) {
        Toast.makeText(context, "Could not open sharing app", Toast.LENGTH_SHORT).show()
    }
}
