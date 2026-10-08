package com.example.kilukkam.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kilukkam.theme.PrimaryOrange
import com.example.kilukkam.theme.TextMuted
import com.example.kilukkam.ui.claymorphism

@Composable
fun ManualEntryDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (Double, String) -> Unit,
    onAddCategory: (String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }
    var newCategoryText by remember { mutableStateOf("") }
    var isAddingNew by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        modifier = Modifier.claymorphism(cornerRadius = 24.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp)),
        title = { 
            Text(
                "Add Expense", 
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        focusedLabelColor = PrimaryOrange
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                if (isAddingNew) {
                    OutlinedTextField(
                        value = newCategoryText,
                        onValueChange = { newCategoryText = it },
                        label = { Text("New Category") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            focusedLabelColor = PrimaryOrange
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (newCategoryText.isNotBlank()) {
                                onAddCategory(newCategoryText.trim())
                                selectedCategory = newCategoryText.trim()
                                isAddingNew = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                    ) {
                        Text("Add")
                    }
                } else {
                    Text("Select Category:", color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(categories) { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCategory = category }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = category == selectedCategory,
                                    onClick = { selectedCategory = category },
                                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryOrange)
                                )
                                Text(category, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        item {
                            TextButton(onClick = { isAddingNew = true }) {
                                Text("+ Add new category", color = PrimaryOrange)
                            }
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
                        onSave(amount, selectedCategory) 
                    }
                },
                enabled = selectedCategory.isNotBlank() && amountText.toDoubleOrNull() != null,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}
