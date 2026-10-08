package com.example.kilukkam.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kilukkam.data.DataRepository
import com.example.kilukkam.theme.PrimaryOrange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

enum class Screen {
    Dashboard, Analytics, Settings
}

@Composable
fun MainAppScaffold(
    repository: DataRepository,
    initialAmount: Double? = null,
    showCategorizeDialog: Boolean = false
) {
    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainScreenViewModel(repository) as T
        }
    }
    
    val viewModel: MainScreenViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(Screen.Dashboard) }
    var showManualEntry by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = currentScreen == Screen.Dashboard,
                    onClick = { currentScreen = Screen.Dashboard },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryOrange,
                        selectedTextColor = PrimaryOrange,
                        indicatorColor = PrimaryOrange.copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.PieChart, contentDescription = "Analytics") },
                    label = { Text("Analytics") },
                    selected = currentScreen == Screen.Analytics,
                    onClick = { currentScreen = Screen.Analytics },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryOrange,
                        selectedTextColor = PrimaryOrange,
                        indicatorColor = PrimaryOrange.copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = currentScreen == Screen.Settings,
                    onClick = { currentScreen = Screen.Settings },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryOrange,
                        selectedTextColor = PrimaryOrange,
                        indicatorColor = PrimaryOrange.copy(alpha = 0.2f)
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showManualEntry = true },
                containerColor = PrimaryOrange,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Expense")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentScreen) {
                Screen.Dashboard -> {
                    MainScreen(
                        viewModel = viewModel,
                        initialAmount = initialAmount,
                        showCategorizeDialog = showCategorizeDialog
                    )
                }
                Screen.Analytics -> {
                    AnalyticsScreen(expenses = state.expenses)
                }
                Screen.Settings -> {
                    SettingsScreen()
                }
            }
            
            if (showManualEntry) {
                ManualEntryDialog(
                    categories = state.categories,
                    onDismiss = { showManualEntry = false },
                    onSave = { amount, category ->
                        viewModel.addExpense(amount, category)
                        showManualEntry = false
                    },
                    onAddCategory = { viewModel.addCategory(it) }
                )
            }
        }
    }
}
