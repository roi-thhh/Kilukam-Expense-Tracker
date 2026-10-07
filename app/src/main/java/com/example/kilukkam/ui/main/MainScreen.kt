package com.example.kilukkam.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.kilukkam.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kilukkam.data.DataRepository
import com.example.kilukkam.data.Expense
import com.example.kilukkam.theme.PrimaryOrange
import com.example.kilukkam.theme.TextMuted
import com.example.kilukkam.ui.claymorphism
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(
    repository: DataRepository,
    initialAmount: Double? = null,
    showCategorizeDialog: Boolean = false,
    modifier: Modifier = Modifier
) {
    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainScreenViewModel(repository) as T
        }
    }
    
    val viewModel: MainScreenViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(showCategorizeDialog) }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.kilukkam_logo),
                contentDescription = "Kilukkam Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 16.dp),
                contentScale = ContentScale.Fit
            )
            
            val totalExpense = state.expenses.sumOf { it.amount }
            
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
                    Text("Total Expenses", color = TextMuted, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹${"%.2f".format(totalExpense)}",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Text(
                text = "Recent Transactions",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.expenses) { expense ->
                    ExpenseItem(expense)
                }
            }
        }

        if (showDialog && initialAmount != null) {
            CategorizeDialog(
                amount = initialAmount,
                categories = state.categories,
                onDismiss = { showDialog = false },
                onSave = { amount, category ->
                    viewModel.addExpense(amount, category)
                    showDialog = false
                },
                onAddCategory = {
                    viewModel.addCategory(it)
                }
            )
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(expense.timestamp))
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .claymorphism(cornerRadius = 16.dp, blurRadius = 6.dp, offsetX = 4.dp, offsetY = 4.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryOrange.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = expense.category.take(1).uppercase(),
                color = PrimaryOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(text = expense.category, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = dateString, color = TextMuted, fontSize = 12.sp)
        }
        
        Text(
            text = "-₹${"%.2f".format(expense.amount)}",
            fontWeight = FontWeight.Bold,
            color = PrimaryOrange,
            fontSize = 16.sp
        )
    }
}

@Composable
fun CategorizeDialog(
    amount: Double,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (Double, String) -> Unit,
    onAddCategory: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }
    var newCategoryText by remember { mutableStateOf("") }
    var isAddingNew by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        modifier = Modifier.claymorphism(cornerRadius = 24.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp)),
        title = { 
            Text(
                "Categorize Expense", 
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                Text(
                    text = "Amount: ₹$amount",
                    fontSize = 20.sp,
                    color = PrimaryOrange,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
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
                onClick = { onSave(amount, selectedCategory) },
                enabled = selectedCategory.isNotBlank(),
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
