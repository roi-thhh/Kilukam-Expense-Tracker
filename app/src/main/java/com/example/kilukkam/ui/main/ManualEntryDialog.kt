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
import com.example.kilukkam.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryDialog(
    isIncome: Boolean = false,
    categories: List<String>,
    accounts: List<Account> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (amount: Double, category: String, merchant: String?, account: String, accountType: String) -> Unit,
    onAddCategory: (String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var merchantText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    
    val fallbackAccounts = listOf(
        Account("1", "Primary Bank", "BANK"),
        Account("2", "Credit Card", "CREDIT_CARD"),
        Account("3", "Cash Wallet", "CASH")
    )
    val displayAccounts = if (accounts.isEmpty()) fallbackAccounts else accounts
    var selectedAccount by remember { mutableStateOf(displayAccounts.firstOrNull() ?: fallbackAccounts[0]) }

    val defaultRecommendations = listOf("Food & Dining", "Transport", "Shopping", "Bills & Utilities", "Tech & Gear")
    val displayCategories = if (categories.isEmpty()) defaultRecommendations else categories
    
    var newCategoryText by remember { mutableStateOf("") }
    var showNewCategoryInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfacePrimary,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(32.dp)),
        title = { 
            Text(
                if (isIncome) "Add Income" else "Add Expense", 
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            ) 
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
                        focusedBorderColor = if (isIncome) AccentCyan else BrandLime,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = BrandLime
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                // Optional Merchant / Store Field
                if (!isIncome) {
                    OutlinedTextField(
                        value = merchantText,
                        onValueChange = { merchantText = it },
                        label = { Text("Merchant / Store (Optional)", color = TextSecondary) },
                        placeholder = { Text("e.g. Swiggy, Uber, Amazon", color = TextSecondary.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandLime,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = BrandLime
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                }

                // Account Selector
                Text("Paid via Account", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayAccounts) { acc ->
                        val isSelected = selectedAccount.id == acc.id || selectedAccount.name == acc.name
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentCyan.copy(alpha = 0.2f) else BackgroundElevated)
                                .border(1.dp, if (isSelected) AccentCyan else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { selectedAccount = acc }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                acc.name, 
                                color = if (isSelected) AccentCyan else TextPrimary, 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Text("Category", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
                
                // Horizontal scrolling category chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayCategories) { category ->
                        val isSelected = selectedCategory == category
                        val bgColor by animateColorAsState(if (isSelected) (if (isIncome) AccentCyan else BrandLime) else BackgroundElevated)
                        val textColor by animateColorAsState(if (isSelected) PureBlack else TextPrimary)
                        
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(bgColor)
                                .clickable { 
                                    selectedCategory = category 
                                    showNewCategoryInput = false
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(category, color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    
                    item {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (showNewCategoryInput) AccentCyan else BackgroundElevated)
                                .clickable { 
                                    showNewCategoryInput = true
                                    selectedCategory = ""
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = if (showNewCategoryInput) PureBlack else TextPrimary)
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
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = AccentCyan
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
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            shape = CircleShape,
                            modifier = Modifier.size(50.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Save Custom", tint = PureBlack)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null && selectedCategory.isNotBlank()) {
                        if (categories.isEmpty() || !categories.contains(selectedCategory)) {
                            onAddCategory(selectedCategory)
                        }
                        onSave(
                            amount, 
                            selectedCategory, 
                            merchantText.takeIf { it.isNotBlank() },
                            selectedAccount.name,
                            selectedAccount.type
                        ) 
                    }
                },
                enabled = selectedCategory.isNotBlank() && amountText.toDoubleOrNull() != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isIncome) AccentCyan else BrandLime,
                    disabledContainerColor = BackgroundElevated
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(
                    "Confirm Entry", 
                    color = if (selectedCategory.isNotBlank() && amountText.toDoubleOrNull() != null) PureBlack else TextSecondary, 
                    fontWeight = FontWeight.ExtraBold,
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
