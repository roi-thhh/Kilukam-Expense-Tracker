package com.example.kilukkam.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
data class Expense(val id: String, val amount: Double, val category: String, val timestamp: Long)

class DataRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("kilukkam_prefs", Context.MODE_PRIVATE)
    
    fun getCategories(): List<String> {
        val categoriesJson = prefs.getString("categories", null)
        return if (categoriesJson != null) {
            Json.decodeFromString<List<String>>(categoriesJson)
        } else {
            emptyList()
        }
    }
    
    fun addCategory(category: String) {
        val categories = getCategories().toMutableList()
        if (!categories.contains(category)) {
            categories.add(category)
            prefs.edit().putString("categories", Json.encodeToString(categories)).apply()
        }
    }
    
    fun getIncomes(): List<Expense> {
        val incomesJson = prefs.getString("incomes", null)
        return if (incomesJson != null) {
            Json.decodeFromString<List<Expense>>(incomesJson)
        } else {
            emptyList()
        }
    }
    
    fun addIncome(amount: Double, category: String) {
        val incomes = getIncomes().toMutableList()
        incomes.add(Expense(UUID.randomUUID().toString(), amount, category, System.currentTimeMillis()))
        prefs.edit().putString("incomes", Json.encodeToString(incomes)).apply()
    }
    fun getExpenses(): List<Expense> {
        val expensesJson = prefs.getString("expenses", null)
        return if (expensesJson != null) {
            Json.decodeFromString<List<Expense>>(expensesJson)
        } else {
            emptyList()
        }
    }
    
    fun addExpense(amount: Double, category: String) {
        val expenses = getExpenses().toMutableList()
        expenses.add(Expense(UUID.randomUUID().toString(), amount, category, System.currentTimeMillis()))
        prefs.edit().putString("expenses", Json.encodeToString(expenses)).apply()
    }

    fun clearAllData() {
        prefs.edit()
            .remove("expenses")
            .remove("incomes")
            .remove("categories")
            .remove("savings")
            .apply()
    }

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
}
