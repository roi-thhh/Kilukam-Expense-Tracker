package com.example.kilukkam.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kilukkam.data.Account
import com.example.kilukkam.data.Expense
import com.example.kilukkam.theme.*
import com.example.kilukkam.ui.sunnyCardShadow
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpenseActionDialog(
    expense: Expense,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(expense.timestamp))
    val primaryTitle = expense.merchant ?: expense.category

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .sunnyCardShadow(cornerRadius = 28.dp, blurRadius = 16.dp, offsetY = 6.dp)
            .border(1.dp, BorderSubtle, RoundedCornerShape(28.dp)),
        title = {
            Column {
                Text(
                    text = "Expense Options",
                    color = TextDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Manage or remove this transaction",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        text = {
            Column {
                // Transaction Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(BackgroundMuted)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = primaryTitle,
                                color = TextDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${expense.category} • ${expense.account}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = dateString,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "-₹${"%.0f".format(expense.amount)}",
                            color = TextDark,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action 1: Edit Details Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BrandYellowSoft)
                        .border(1.dp, BorderYellow.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable { onEditClick() }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BrandYellowPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = TextOnYellow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Edit Details",
                                color = TextDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Change amount, tag, merchant or category",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action 2: Delete Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AccentExpense.copy(alpha = 0.10f))
                        .border(1.dp, AccentExpense.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .clickable { onDeleteClick() }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AccentExpense),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = SurfaceWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Delete Expense",
                                color = AccentExpense,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Remove this record and adjust balance",
                                color = AccentExpense.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dismiss", color = TextSecondary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun EditExpenseDialog(
    expense: Expense,
    categories: List<String>,
    accounts: List<Account> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (updatedExpense: Expense) -> Unit,
    onDelete: (expenseId: String) -> Unit,
    onAddCategory: (String) -> Unit
) {
    val initialAmountFormatted = if (expense.amount % 1.0 == 0.0) {
        expense.amount.toLong().toString()
    } else {
        expense.amount.toString()
    }

    var amountText by remember { mutableStateOf(initialAmountFormatted) }
    var merchantText by remember { mutableStateOf(expense.merchant ?: "") }
    var selectedCategory by remember { mutableStateOf(expense.category) }

    val fallbackAccounts = listOf(
        Account("1", "Primary Bank", "BANK"),
        Account("2", "Credit Card", "CREDIT_CARD"),
        Account("3", "Cash Wallet", "CASH")
    )
    val displayAccounts = if (accounts.isEmpty()) fallbackAccounts else accounts
    var selectedAccount by remember {
        mutableStateOf(
            displayAccounts.firstOrNull { it.name.equals(expense.account, ignoreCase = true) }
                ?: displayAccounts.firstOrNull()
                ?: fallbackAccounts[0]
        )
    }

    val defaultRecommendations = listOf("Food & Dining", "Transport", "Shopping", "Bills & Utilities", "Tech & Gear")
    val displayCategories = if (categories.isEmpty()) defaultRecommendations else categories

    var newCategoryText by remember { mutableStateOf("") }
    var showNewCategoryInput by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        DeleteExpenseConfirmationDialog(
            expense = expense,
            onDismiss = { showDeleteConfirm = false },
            onConfirm = {
                showDeleteConfirm = false
                onDelete(expense.id)
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .sunnyCardShadow(cornerRadius = 28.dp, blurRadius = 16.dp, offsetY = 6.dp)
            .border(1.dp, BorderSubtle, RoundedCornerShape(28.dp)),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edit Expense",
                    color = TextDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    letterSpacing = (-0.5).sp
                )
                IconButton(
                    onClick = { showDeleteConfirm = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = AccentExpense
                    )
                }
            }
        },
        text = {
            Column {
                // Amount Field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (₹)", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = BackgroundMuted.copy(alpha = 0.5f),
                        focusedBorderColor = BrandYellowPrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        cursorColor = TextDark
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true
                )

                // Merchant / Store / Tag Field
                OutlinedTextField(
                    value = merchantText,
                    onValueChange = { merchantText = it },
                    label = { Text("Merchant / Tag / Note", color = TextSecondary) },
                    placeholder = { Text("e.g. Swiggy, Groceries, Dinner", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = BackgroundMuted.copy(alpha = 0.5f),
                        focusedBorderColor = BrandYellowPrimary,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark,
                        cursorColor = TextDark
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(18.dp),
                    singleLine = true
                )

                // Account Selector
                Text(
                    "Account / Card",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayAccounts) { acc ->
                        val isSelected = selectedAccount.id == acc.id || selectedAccount.name == acc.name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) BrandYellowPrimary else BackgroundMuted)
                                .border(1.dp, if (isSelected) BorderYellow else BorderSubtle, RoundedCornerShape(14.dp))
                                .clickable { selectedAccount = acc }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                acc.name,
                                color = TextDark,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Category Selector
                Text(
                    "Category",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayCategories) { category ->
                        val isSelected = selectedCategory.equals(category, ignoreCase = true)
                        val bgColor by animateColorAsState(if (isSelected) BrandYellowPrimary else BackgroundMuted)
                        val textColor by animateColorAsState(if (isSelected) TextOnYellow else TextDark)

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(bgColor)
                                .border(1.dp, if (isSelected) Color.Transparent else BorderSubtle, CircleShape)
                                .clickable {
                                    selectedCategory = category
                                    showNewCategoryInput = false
                                }
                                .padding(horizontal = 16.dp, vertical = 9.dp)
                        ) {
                            Text(category, color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (showNewCategoryInput) BrandYellowPrimary else BrandYellowSoft)
                                .clickable {
                                    showNewCategoryInput = true
                                    selectedCategory = ""
                                }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = TextDark)
                        }
                    }
                }

                if (showNewCategoryInput) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = newCategoryText,
                            onValueChange = { newCategoryText = it },
                            label = { Text("Custom Category", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BrandYellowPrimary,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark,
                                cursorColor = TextDark
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val trimmed = newCategoryText.trim()
                                if (trimmed.isNotBlank()) {
                                    onAddCategory(trimmed)
                                    selectedCategory = trimmed
                                    newCategoryText = ""
                                    showNewCategoryInput = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandYellowPrimary),
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Save Custom", tint = TextDark)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull()
                        if (amount != null && selectedCategory.isNotBlank()) {
                            if (categories.isEmpty() || !categories.contains(selectedCategory)) {
                                onAddCategory(selectedCategory)
                            }
                            val updated = expense.copy(
                                amount = amount,
                                category = selectedCategory,
                                merchant = merchantText.trim().takeIf { it.isNotBlank() },
                                account = selectedAccount.name,
                                accountType = selectedAccount.type
                            )
                            onSave(updated)
                        }
                    },
                    enabled = selectedCategory.isNotBlank() && amountText.toDoubleOrNull() != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandYellowPrimary,
                        disabledContainerColor = BackgroundMuted
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .sunnyCardShadow(cornerRadius = 20.dp, blurRadius = 8.dp, offsetY = 2.dp)
                ) {
                    Text(
                        text = "Save Changes",
                        color = if (selectedCategory.isNotBlank() && amountText.toDoubleOrNull() != null) TextOnYellow else TextMuted,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentExpense.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentExpense),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = AccentExpense,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete This Expense",
                        color = AccentExpense,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = TextSecondary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
fun DeleteExpenseConfirmationDialog(
    expense: Expense,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val primaryTitle = expense.merchant ?: expense.category

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceWhite,
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .sunnyCardShadow(cornerRadius = 28.dp, blurRadius = 16.dp, offsetY = 6.dp)
            .border(1.dp, BorderSubtle, RoundedCornerShape(28.dp)),
        title = {
            Text(
                text = "Delete Expense?",
                color = TextDark,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                letterSpacing = (-0.5).sp
            )
        },
        text = {
            Text(
                text = "Are you sure you want to permanently delete ₹${"%.0f".format(expense.amount)} for \"$primaryTitle\"? This action will adjust your balance and cannot be undone.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = AccentExpense),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.height(46.dp)
            ) {
                Text("Delete", color = SurfaceWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(46.dp)
            ) {
                Text("Cancel", color = TextSecondary, fontWeight = FontWeight.SemiBold)
            }
        }
    )
}
