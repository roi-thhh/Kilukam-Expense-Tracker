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
import com.example.kilukkam.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryDialog(
    isIncome: Boolean = false,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (Double, String) -> Unit,
    onAddCategory: (String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    
    // Recommendations (if categories are empty)
    val defaultRecommendations = listOf("Food \uD83C\uDF54", "Transport \uD83D\uDE97", "Bills \uD83D\uDCA1", "Tech \uD83D\uDCBB")
    val displayCategories = if (categories.isEmpty()) defaultRecommendations else categories
    
    var newCategoryText by remember { mutableStateOf("") }
    var showNewCategoryInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfacePrimary,
        shape = RoundedCornerShape(32.dp), // Folder card feel
        modifier = Modifier.border(1.dp, BorderSubtle, RoundedCornerShape(32.dp)),
        title = { 
            Text(
                if (isIncome) "Add Income" else "Add Expense", 
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            ) 
        },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (₹)", color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandLime,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = BrandLime,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = BrandLime
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    shape = RoundedCornerShape(16.dp)
                )

                Text("Category", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                
                // Horizontal scrolling chips
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
                            Text(category, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    
                    item {
                        // Add New button
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (showNewCategoryInput) AccentCyan else BackgroundElevated)
                                .clickable { 
                                    showNewCategoryInput = true
                                    selectedCategory = ""
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
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
                                focusedTextColor = AccentCyan,
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
                        // If they picked a recommended category that wasn't in their list, add it to repository
                        if (categories.isEmpty() || !categories.contains(selectedCategory)) {
                            onAddCategory(selectedCategory)
                        }
                        onSave(amount, selectedCategory) 
                    }
                },
                enabled = selectedCategory.isNotBlank() && amountText.toDoubleOrNull() != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isIncome) AccentCyan else BrandLime,
                    disabledContainerColor = BackgroundElevated
                ),
                shape = CircleShape
            ) {
                Text("Confirm", color = if (selectedCategory.isNotBlank() && amountText.toDoubleOrNull() != null) PureBlack else TextSecondary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
