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
            val incomes = repository.getIncomes().sortedByDescending { it.timestamp }
            val categories = repository.getCategories()
            val userName = repository.getUserName()
            val budgetGoal = repository.getBudgetGoal()
            val savings = repository.getSavings()
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    expenses = expenses,
                    incomes = incomes,
                    categories = categories,
                    userName = userName,
                    budgetGoal = budgetGoal,
                    savings = savings
                )
            }
        }
    }

    fun addExpense(amount: Double, category: String) {
        repository.addExpense(amount, category)
        loadData()
    }
    
    fun addIncome(amount: Double, category: String) {
        repository.addIncome(amount, category)
        loadData()
    }

    fun addSavings(amount: Double) {
        repository.addSavings(amount)
        loadData()
    }

    fun addCategory(category: String) {
        repository.addCategory(category)
        loadData()
    }

    fun updateUserName(name: String) {
        repository.setUserName(name)
        loadData()
    }

    fun updateBudgetGoal(goal: Double) {
        repository.setBudgetGoal(goal)
        loadData()
    }

    fun clearAllData() {
        repository.clearAllData()
        loadData()
    }

    fun refresh() {
        loadData()
    }
}

data class MainScreenUiState(
    val isLoading: Boolean = false,
    val expenses: List<Expense> = emptyList(),
    val incomes: List<Expense> = emptyList(),
    val categories: List<String> = emptyList(),
    val userName: String = "Alex Miller",
    val budgetGoal: Double = 25000.0,
    val savings: Double = 0.0
)

