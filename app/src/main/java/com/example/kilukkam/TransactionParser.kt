package com.example.kilukkam

object TransactionParser {
    
    fun isUpiTransaction(body: String, sender: String?): Boolean {
        if (sender == null) return false
        
        val lowerBody = body.lowercase()
        
        // 1. Basic Scam Filters
        if (lowerBody.contains("won ") || 
            lowerBody.contains("lottery") || 
            lowerBody.contains("click here") || 
            lowerBody.contains("http://") || 
            lowerBody.contains("https://") ||
            lowerBody.contains("free ")) {
            return false
        }
        
        // 2. Sender Validation (Bank SMS usually don't have standard phone numbers)
        // Usually, sender looks like AD-HDFCBK, VK-ICICIB, etc.
        val isLikelyBankSender = !sender.matches(Regex("^\\+?\\d{10,13}$"))
        if (!isLikelyBankSender) {
            return false
        }
        
        // 3. Transaction Keywords
        val hasTransactionKeywords = lowerBody.contains("debited") || 
                                     lowerBody.contains("sent") ||
                                     lowerBody.contains("paid") ||
                                     lowerBody.contains("spent")
                                     
        val hasUpiKeywords = lowerBody.contains("upi") || 
                             lowerBody.contains("vpa")
        
        return hasTransactionKeywords && hasUpiKeywords
    }

    fun extractAmount(body: String): Double? {
        // Look for Rs., INR, Rs, INR followed by amount
        val regex = Regex("(?i)(rs\\.?|inr)\\s*(\\d+(?:,\\d+)*(?:\\.\\d+)?)")
        val matchResult = regex.find(body)
        
        return if (matchResult != null) {
            val amountStr = matchResult.groupValues[2].replace(",", "")
            amountStr.toDoubleOrNull()
        } else {
            null
        }
    }
}
