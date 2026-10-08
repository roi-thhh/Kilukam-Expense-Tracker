package com.example.kilukkam.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
data class Expense(
    val id: String,
    val amount: Double,
    val category: String,
    val timestamp: Long,
    val merchant: String? = null,
    val account: String = "Primary Account",
    val accountType: String = "BANK" // "BANK", "CREDIT_CARD", "CASH"
)

@Serializable
data class Account(
    val id: String,
    val name: String,
    val type: String, // "BANK", "CREDIT_CARD", "CASH", "WALLET"
    val balance: Double = 0.0,
    val isDefault: Boolean = false
)

@Serializable
data class TargetVault(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val category: String = "Goal"
)

class DataRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("kilukkam_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    
    // --- Categories ---
    fun getCategories(): List<String> {
        val categoriesJson = prefs.getString("categories", null)
        return if (categoriesJson != null) {
            try {
                json.decodeFromString<List<String>>(categoriesJson)
            } catch (_: Exception) {
                defaultCategories()
            }
        } else {
            defaultCategories()
        }
    }
    
    fun defaultCategories(): List<String> {
        return listOf("Food & Dining", "Transport", "Shopping", "Bills & Utilities", "Tech & Gear", "Entertainment", "Health & Medical")
    }
    
    fun addCategory(category: String) {
        val categories = getCategories().toMutableList()
        if (!categories.contains(category)) {
            categories.add(category)
            prefs.edit().putString("categories", json.encodeToString(categories)).apply()
        }
    }
    
    // --- Incomes ---
    fun getIncomes(): List<Expense> {
        val incomesJson = prefs.getString("incomes", null)
        return if (incomesJson != null) {
            try {
                json.decodeFromString<List<Expense>>(incomesJson)
            } catch (_: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    fun addIncome(amount: Double, category: String, account: String = "Primary Account") {
        val incomes = getIncomes().toMutableList()
        incomes.add(Expense(UUID.randomUUID().toString(), amount, category, System.currentTimeMillis(), account = account))
        prefs.edit().putString("incomes", json.encodeToString(incomes)).apply()
    }

    // --- Expenses ---
    fun getExpenses(): List<Expense> {
        val expensesJson = prefs.getString("expenses", null)
        return if (expensesJson != null) {
            try {
                json.decodeFromString<List<Expense>>(expensesJson)
            } catch (_: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
    
    fun addExpense(
        amount: Double, 
        category: String, 
        merchant: String? = null,
        account: String = "Primary Account",
        accountType: String = "BANK"
    ) {
        val expenses = getExpenses().toMutableList()
        expenses.add(
            Expense(
                id = UUID.randomUUID().toString(),
                amount = amount,
                category = category,
                timestamp = System.currentTimeMillis(),
                merchant = merchant,
                account = account,
                accountType = accountType
            )
        )
        prefs.edit().putString("expenses", json.encodeToString(expenses)).apply()
        
        // Auto-learn merchant category habit
        if (merchant != null && merchant.isNotBlank()) {
            saveMerchantCategory(merchant, category)
        }
    }

    // --- Intelligent Merchant Memory (Habit Learning) ---
    fun getMerchantCategory(merchant: String): String? {
        val mapJson = prefs.getString("merchant_categories", null) ?: return null
        return try {
            val map = json.decodeFromString<Map<String, String>>(mapJson)
            map[merchant.lowercase().trim()]
        } catch (_: Exception) {
            null
        }
    }

    fun saveMerchantCategory(merchant: String, category: String) {
        val mapJson = prefs.getString("merchant_categories", null)
        val map = if (mapJson != null) {
            try {
                json.decodeFromString<Map<String, String>>(mapJson).toMutableMap()
            } catch (_: Exception) {
                mutableMapOf()
            }
        } else {
            mutableMapOf()
        }
        map[merchant.lowercase().trim()] = category
        prefs.edit().putString("merchant_categories", json.encodeToString(map)).apply()
    }

    // --- Multi-Account & Credit Card Management ---
    fun getAccounts(): List<Account> {
        val accountsJson = prefs.getString("user_accounts", null)
        return if (accountsJson != null) {
            try {
                json.decodeFromString<List<Account>>(accountsJson)
            } catch (_: Exception) {
                defaultAccounts()
            }
        } else {
            defaultAccounts()
        }
    }

    fun defaultAccounts(): List<Account> {
        return listOf(
            Account("acc_1", "Primary Bank", "BANK", balance = 0.0, isDefault = true),
            Account("acc_2", "Credit Card", "CREDIT_CARD", balance = 0.0),
            Account("acc_3", "Cash Wallet", "CASH", balance = 0.0)
        )
    }

    fun addOrUpdateAccount(account: Account) {
        val accounts = getAccounts().toMutableList()
        val index = accounts.indexOfFirst { it.id == account.id || it.name.equals(account.name, ignoreCase = true) }
        if (index != -1) {
            accounts[index] = account
        } else {
            accounts.add(account)
        }
        prefs.edit().putString("user_accounts", json.encodeToString(accounts)).apply()
    }

    fun ensureAccountExists(accountName: String, accountType: String) {
        val accounts = getAccounts().toMutableList()
        if (accounts.none { it.name.equals(accountName, ignoreCase = true) }) {
            accounts.add(Account(UUID.randomUUID().toString(), accountName, accountType))
            prefs.edit().putString("user_accounts", json.encodeToString(accounts)).apply()
        }
    }

    // --- Category-Specific Budgets ---
    fun getCategoryBudgets(): Map<String, Double> {
        val budgetsJson = prefs.getString("category_budgets", null)
        return if (budgetsJson != null) {
            try {
                json.decodeFromString<Map<String, Double>>(budgetsJson)
            } catch (_: Exception) {
                defaultCategoryBudgets()
            }
        } else {
            defaultCategoryBudgets()
        }
    }

    fun defaultCategoryBudgets(): Map<String, Double> {
        return mapOf(
            "Food & Dining" to 8000.0,
            "Shopping" to 6000.0,
            "Transport" to 3500.0,
            "Bills & Utilities" to 5000.0
        )
    }

    fun setCategoryBudget(category: String, amount: Double) {
        val map = getCategoryBudgets().toMutableMap()
        map[category] = amount
        prefs.edit().putString("category_budgets", json.encodeToString(map)).apply()
    }

    // --- Target Vaults (Goal-Based Savings) ---
    fun getTargetVaults(): List<TargetVault> {
        val vaultsJson = prefs.getString("target_vaults", null)
        return if (vaultsJson != null) {
            try {
                json.decodeFromString<List<TargetVault>>(vaultsJson)
            } catch (_: Exception) {
                defaultTargetVaults()
            }
        } else {
            defaultTargetVaults()
        }
    }

    fun defaultTargetVaults(): List<TargetVault> {
        return listOf(
            TargetVault("vault_1", "Emergency Fund", 50000.0, 0.0, "Safety"),
            TargetVault("vault_2", "New Gadget", 30000.0, 0.0, "Tech")
        )
    }

    fun addTargetVault(title: String, targetAmount: Double, category: String = "Goal") {
        val vaults = getTargetVaults().toMutableList()
        vaults.add(TargetVault(UUID.randomUUID().toString(), title, targetAmount, 0.0, category))
        prefs.edit().putString("target_vaults", json.encodeToString(vaults)).apply()
    }

    fun depositToTargetVault(vaultId: String, amount: Double) {
        val vaults = getTargetVaults().toMutableList()
        val index = vaults.indexOfFirst { it.id == vaultId }
        if (index != -1) {
            val v = vaults[index]
            vaults[index] = v.copy(savedAmount = v.savedAmount + amount)
            prefs.edit().putString("target_vaults", json.encodeToString(vaults)).apply()
            addSavings(amount) // Sync with overall total savings
        }
    }

    fun deleteTargetVault(vaultId: String) {
        val vaults = getTargetVaults().filter { it.id != vaultId }
        prefs.edit().putString("target_vaults", json.encodeToString(vaults)).apply()
    }

    // --- Savings, Profile & Overall Budget Goal ---
    fun getSavings(): Double {
        return prefs.getFloat("savings", 0f).toDouble()
    }

    fun addSavings(amount: Double) {
        val current = getSavings()
        prefs.edit().putFloat("savings", (current + amount).toFloat()).apply()
    }

    fun getUserName(): String {
        return prefs.getString("user_name", "Alex Miller") ?: "Alex Miller"
    }

    fun setUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
    }

    fun getBudgetGoal(): Double {
        return prefs.getFloat("budget_goal", 25000f).toDouble()
    }

    fun setBudgetGoal(budget: Double) {
        prefs.edit().putFloat("budget_goal", budget.toFloat()).apply()
    }

    fun clearAllData() {
        prefs.edit()
            .remove("expenses")
            .remove("incomes")
            .remove("categories")
            .remove("savings")
            .remove("user_accounts")
            .remove("category_budgets")
            .remove("target_vaults")
            .remove("merchant_categories")
            .apply()
    }
}
