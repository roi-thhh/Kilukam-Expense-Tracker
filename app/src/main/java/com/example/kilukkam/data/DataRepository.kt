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
            listOf("EV Charging", "Food", "Petrol", "Household")
        }
    }
    
    fun addCategory(category: String) {
        val categories = getCategories().toMutableList()
        if (!categories.contains(category)) {
            categories.add(category)
            prefs.edit().putString("categories", Json.encodeToString(categories)).apply()
        }
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
}
