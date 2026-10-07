package com.example.kilukkam.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kilukkam.data.DataRepository
import com.example.kilukkam.data.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(private val repository: DataRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MainScreenUiState(isLoading = true))
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val expenses = repository.getExpenses().sortedByDescending { it.timestamp }
            val categories = repository.getCategories()
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    expenses = expenses,
                    categories = categories
                )
            }
        }
    }

    fun addExpense(amount: Double, category: String) {
        repository.addExpense(amount, category)
        loadData()
    }
    
    fun addCategory(category: String) {
        repository.addCategory(category)
        loadData()
    }
}

data class MainScreenUiState(
    val isLoading: Boolean = false,
    val expenses: List<Expense> = emptyList(),
    val categories: List<String> = emptyList()
)
