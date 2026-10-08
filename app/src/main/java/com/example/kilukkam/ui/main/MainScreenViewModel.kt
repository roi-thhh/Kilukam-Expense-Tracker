package com.example.kilukkam.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kilukkam.data.Account
import com.example.kilukkam.data.DataRepository
import com.example.kilukkam.data.Expense
import com.example.kilukkam.data.TargetVault
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
            val accounts = repository.getAccounts()
            val categoryBudgets = repository.getCategoryBudgets()
            val targetVaults = repository.getTargetVaults()

            _uiState.update { 
                it.copy(
                    isLoading = false,
                    expenses = expenses,
                    incomes = incomes,
                    categories = categories,
                    userName = userName,
                    budgetGoal = budgetGoal,
                    savings = savings,
                    accounts = accounts,
                    categoryBudgets = categoryBudgets,
                    targetVaults = targetVaults
                )
            }
        }
    }

    fun addExpense(
        amount: Double, 
        category: String, 
        merchant: String? = null,
        account: String = "Primary Account",
        accountType: String = "BANK"
    ) {
        repository.ensureAccountExists(account, accountType)
        repository.addExpense(amount, category, merchant, account, accountType)
        loadData()
    }
    
    fun addIncome(amount: Double, category: String, account: String = "Primary Account") {
        repository.addIncome(amount, category, account)
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

    fun selectAccountFilter(accountName: String?) {
        _uiState.update { it.copy(selectedAccountFilter = accountName) }
    }

    fun addAccount(name: String, type: String, balance: Double = 0.0) {
        repository.addOrUpdateAccount(Account(java.util.UUID.randomUUID().toString(), name, type, balance))
        loadData()
    }

    fun setCategoryBudget(category: String, amount: Double) {
        repository.setCategoryBudget(category, amount)
        loadData()
    }

    fun addTargetVault(title: String, targetAmount: Double, category: String = "Goal") {
        repository.addTargetVault(title, targetAmount, category)
        loadData()
    }

    fun depositToTargetVault(vaultId: String, amount: Double) {
        repository.depositToTargetVault(vaultId, amount)
        loadData()
    }

    fun deleteTargetVault(vaultId: String) {
        repository.deleteTargetVault(vaultId)
        loadData()
    }

    fun getLearnedCategory(merchant: String): String? {
        return repository.getMerchantCategory(merchant)
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
    val savings: Double = 0.0,
    val accounts: List<Account> = emptyList(),
    val selectedAccountFilter: String? = null,
    val categoryBudgets: Map<String, Double> = emptyMap(),
    val targetVaults: List<TargetVault> = emptyList()
)
