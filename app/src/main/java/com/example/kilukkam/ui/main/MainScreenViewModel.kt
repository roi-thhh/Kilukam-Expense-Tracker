package com.example.kilukkam.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kilukkam.data.Account
import com.example.kilukkam.data.DataRepository
import com.example.kilukkam.data.DebtRecord
import com.example.kilukkam.data.DebtType
import com.example.kilukkam.data.Expense
import com.example.kilukkam.data.TargetVault
import com.example.kilukkam.utils.MonthYear
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
            val debts = repository.getDebts().sortedByDescending { it.timestamp }

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
                    targetVaults = targetVaults,
                    debts = debts
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
    
    fun updateExpense(expense: Expense) {
        repository.ensureAccountExists(expense.account, expense.accountType)
        repository.updateExpense(expense)
        loadData()
    }

    fun deleteExpense(expenseId: String) {
        repository.deleteExpense(expenseId)
        loadData()
    }

    fun updateIncome(income: Expense) {
        repository.updateIncome(income)
        loadData()
    }

    fun deleteIncome(incomeId: String) {
        repository.deleteIncome(incomeId)
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

    fun addCategory(category: String, defaultBudget: Double? = null) {
        repository.addCategory(category, defaultBudget)
        loadData()
    }

    fun deleteCategory(category: String) {
        repository.deleteCategory(category)
        loadData()
    }

    fun deleteCategoryBudget(category: String) {
        repository.deleteCategoryBudget(category)
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

    // --- Debt / Loan Methods ---
    fun addDebt(
        personName: String,
        amount: Double,
        type: DebtType,
        note: String? = null,
        dueDate: Long? = null,
        phoneNumber: String? = null
    ) {
        repository.addDebt(personName, amount, type, note, dueDate, phoneNumber)
        loadData()
    }

    fun recordDebtRepayment(debtId: String, amount: Double, note: String? = null) {
        repository.recordDebtRepayment(debtId, amount, note)
        loadData()
    }

    fun settleDebt(debtId: String) {
        repository.settleDebt(debtId)
        loadData()
    }

    fun deleteDebt(debtId: String) {
        repository.deleteDebt(debtId)
        loadData()
    }

    fun updateDebt(debt: DebtRecord) {
        repository.updateDebt(debt)
        loadData()
    }

    // --- Month & Horizon Selection ---
    fun selectMonth(monthYear: MonthYear) {
        _uiState.update { it.copy(selectedMonth = monthYear) }
    }

    fun selectPreviousMonth() {
        _uiState.update { it.copy(selectedMonth = it.selectedMonth.previous()) }
    }

    fun selectNextMonth() {
        _uiState.update { it.copy(selectedMonth = it.selectedMonth.next()) }
    }

    fun resetToCurrentMonth() {
        _uiState.update { it.copy(selectedMonth = MonthYear.now()) }
    }

    fun selectAnalyticsYear(year: Int) {
        _uiState.update { it.copy(selectedAnalyticsYear = year) }
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
    val targetVaults: List<TargetVault> = emptyList(),
    val debts: List<DebtRecord> = emptyList(),
    val selectedMonth: MonthYear = MonthYear.now(),
    val selectedAnalyticsYear: Int = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
) {
    // Current Dashboard monthly-filtered expenses
    val currentMonthExpenses: List<Expense>
        get() {
            val start = selectedMonth.startTimestamp()
            val end = selectedMonth.endTimestamp()
            return expenses.filter { it.timestamp in start..end }
        }

    // Current Dashboard monthly-filtered incomes
    val currentMonthIncomes: List<Expense>
        get() {
            val start = selectedMonth.startTimestamp()
            val end = selectedMonth.endTimestamp()
            return incomes.filter { it.timestamp in start..end }
        }

    val currentMonthTotalExpense: Double
        get() = currentMonthExpenses.sumOf { it.amount }

    val currentMonthTotalIncome: Double
        get() = currentMonthIncomes.sumOf { it.amount }

    val currentMonthNetBalance: Double
        get() = currentMonthTotalIncome - currentMonthTotalExpense

    // Overall Lifetime metrics
    val lifetimeTotalExpense: Double
        get() = expenses.sumOf { it.amount }

    val lifetimeTotalIncome: Double
        get() = incomes.sumOf { it.amount }

    val lifetimeNetBalance: Double
        get() = lifetimeTotalIncome - lifetimeTotalExpense
}
