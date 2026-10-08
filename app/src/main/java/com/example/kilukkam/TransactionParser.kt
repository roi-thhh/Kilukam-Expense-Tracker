package com.example.kilukkam

data class ParsedTransaction(
    val amount: Double,
    val merchant: String? = null,
    val accountName: String = "Primary Account",
    val accountType: String = "BANK", // "BANK", "CREDIT_CARD", "CASH"
    val suggestedCategory: String = "Food & Dining",
    val isIncome: Boolean = false
)

object TransactionParser {
    
    fun isTransactionalSms(body: String, sender: String?): Boolean {
        if (sender == null) return false
        val lowerBody = body.lowercase()
        
        // 1. Basic Scam / Promo Filters
        if (lowerBody.contains("won ") || 
            lowerBody.contains("lottery") || 
            lowerBody.contains("click here") || 
            lowerBody.contains("http://") || 
            lowerBody.contains("https://") ||
            lowerBody.contains("congratulations") ||
            lowerBody.contains("free recharge")) {
            return false
        }
        
        // 2. Sender Validation (Bank SMS usually has alphanumeric headers like AD-HDFCBK, VK-SBIINB)
        val isLikelyBankSender = !sender.matches(Regex("^\\+?\\d{10,13}$"))
        if (!isLikelyBankSender) {
            return false
        }
        
        // 3. Transaction Keywords
        val hasDebit = lowerBody.contains("debited") || lowerBody.contains("sent") || lowerBody.contains("paid") || lowerBody.contains("spent")
        val hasCredit = lowerBody.contains("credited") || lowerBody.contains("received") || lowerBody.contains("deposited")
        
        return (hasDebit || hasCredit)
    }

    // Retained for backward-compatibility with existing receiver calls
    fun isUpiTransaction(body: String, sender: String?): Boolean {
        return isTransactionalSms(body, sender)
    }

    fun parse(body: String, sender: String?): ParsedTransaction? {
        val amount = extractAmount(body) ?: return null
        val lowerBody = body.lowercase()
        val isIncome = lowerBody.contains("credited") || lowerBody.contains("received") || lowerBody.contains("deposited")
        
        val accountInfo = extractAccount(body, sender)
        val merchant = extractMerchant(body)
        val suggestedCat = if (isIncome) "Income" else suggestCategory(merchant, body)
        
        return ParsedTransaction(
            amount = amount,
            merchant = merchant,
            accountName = accountInfo.first,
            accountType = accountInfo.second,
            suggestedCategory = suggestedCat,
            isIncome = isIncome
        )
    }

    fun extractAmount(body: String): Double? {
        // Look for Rs., INR, Rs followed by numerical value
        val regex = Regex("(?i)(?:rs\\.?|inr)\\s*([0-9]+(?:,[0-9]+)*(?:\\.[0-9]+)?)")
        val matchResult = regex.find(body)
        
        return if (matchResult != null) {
            val amountStr = matchResult.groupValues[1].replace(",", "")
            amountStr.toDoubleOrNull()
        } else {
            null
        }
    }

    fun extractAccount(body: String, sender: String?): Pair<String, String> {
        val lowerBody = body.lowercase()
        val bankName = detectBank(sender, body)
        
        val isCreditCard = lowerBody.contains("credit card") || lowerBody.contains("card ending")
        val accountType = if (isCreditCard) "CREDIT_CARD" else "BANK"
        
        // Match last 4 digits
        val acRegex = Regex("(?i)(?:a/c|ac|account|card)\\s*(?:no\\.?)?\\s*[*xX]*([0-9]{3,4})")
        val match = acRegex.find(body)
        val lastDigits = match?.groupValues?.get(1)
        
        val accountName = if (lastDigits != null) {
            if (isCreditCard) "$bankName Card ••$lastDigits" else "$bankName ••$lastDigits"
        } else {
            if (isCreditCard) "$bankName Credit Card" else "$bankName Account"
        }
        
        return Pair(accountName, accountType)
    }

    fun detectBank(sender: String?, body: String): String {
        val text = "${sender.orEmpty()} $body".uppercase()
        return when {
            text.contains("HDFC") -> "HDFC Bank"
            text.contains("SBI") || text.contains("STATE BANK") -> "SBI"
            text.contains("ICICI") -> "ICICI Bank"
            text.contains("AXIS") -> "Axis Bank"
            text.contains("KOTAK") -> "Kotak Bank"
            text.contains("PAYTM") -> "Paytm"
            text.contains("PNB") || text.contains("PUNJAB") -> "PNB"
            text.contains("BOB") || text.contains("BARODA") -> "Bank of Baroda"
            text.contains("INDUS") -> "IndusInd Bank"
            text.contains("CANARA") -> "Canara Bank"
            text.contains("YES") -> "Yes Bank"
            text.contains("IDFC") -> "IDFC FIRST"
            text.contains("FEDERAL") -> "Federal Bank"
            else -> "Bank"
        }
    }

    fun extractMerchant(body: String): String? {
        val lower = body.lowercase()
        
        // 1. Check known merchant dictionary first
        val knownMerchants = mapOf(
            "swiggy" to "Swiggy",
            "zomato" to "Zomato",
            "uber" to "Uber",
            "ola" to "Ola",
            "rapido" to "Rapido",
            "amazon" to "Amazon",
            "flipkart" to "Flipkart",
            "myntra" to "Myntra",
            "blinkit" to "Blinkit",
            "zepto" to "Zepto",
            "instamart" to "Instamart",
            "dmart" to "DMart",
            "starbucks" to "Starbucks",
            "mcdonald" to "McDonald's",
            "kfc" to "KFC",
            "domino" to "Domino's",
            "burger king" to "Burger King",
            "subway" to "Subway",
            "netflix" to "Netflix",
            "spotify" to "Spotify",
            "bookmyshow" to "BookMyShow",
            "pvr" to "PVR Cinemas",
            "inox" to "INOX",
            "airtel" to "Airtel",
            "jio" to "Jio",
            "vi" to "Vodafone Idea",
            "apollo" to "Apollo Pharmacy",
            "pharmeasy" to "PharmEasy",
            "1mg" to "Tata 1mg",
            "irctc" to "IRCTC",
            "fastag" to "FASTag",
            "hpcl" to "HP Petrol",
            "bpcl" to "Bharat Petroleum",
            "indian oil" to "Indian Oil"
        )
        
        for ((key, name) in knownMerchants) {
            if (lower.contains(key)) return name
        }
        
        // 2. Extract from VPA handle (e.g., "to VPA merchant@bank" or "to merchant@okaxis")
        val vpaRegex = Regex("(?i)(?:to\\s+vpa|to|at)\\s+([a-zA-Z0-9\\.\\-_]+)@[a-zA-Z0-9]+")
        val vpaMatch = vpaRegex.find(body)
        if (vpaMatch != null) {
            val rawHandle = vpaMatch.groupValues[1]
                .replace("payu", "", ignoreCase = true)
                .replace(".", " ")
                .replace("-", " ")
                .replace("_", " ")
                .trim()
            if (rawHandle.length in 3..25) {
                return rawHandle.split(" ")
                    .filter { it.isNotBlank() }
                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
            }
        }
        
        // 3. Fallback extraction for "to <Name>" or "at <Name>"
        val toRegex = Regex("(?i)(?:paid to|transferred to|spent at|to|at)\\s+([a-zA-Z0-9\\s]{3,20})(?:\\s+on|\\s+ref|\\s+via|\\s+upi|\\.|$)")
        val toMatch = toRegex.find(body)
        if (toMatch != null) {
            val candidate = toMatch.groupValues[1].trim()
            if (candidate.length in 3..25 && !candidate.equals("vpa", ignoreCase = true) && !candidate.equals("account", ignoreCase = true)) {
                return candidate.split(" ")
                    .filter { it.isNotBlank() }
                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
            }
        }
        
        return null
    }

    fun suggestCategory(merchant: String?, body: String): String {
        val text = "${merchant.orEmpty()} $body".lowercase()
        return when {
            text.contains("swiggy") || text.contains("zomato") || text.contains("starbucks") || 
            text.contains("mcdonald") || text.contains("kfc") || text.contains("domino") || 
            text.contains("pizza") || text.contains("burger") || text.contains("cafe") || 
            text.contains("restaurant") || text.contains("food") || text.contains("tea") || text.contains("chai") -> "Food & Dining"

            text.contains("uber") || text.contains("ola") || text.contains("rapido") || 
            text.contains("petrol") || text.contains("fuel") || text.contains("hpcl") || 
            text.contains("bpcl") || text.contains("indian oil") || text.contains("shell") || 
            text.contains("metro") || text.contains("irctc") || text.contains("fastag") -> "Transport"

            text.contains("amazon") || text.contains("flipkart") || text.contains("myntra") || 
            text.contains("blinkit") || text.contains("zepto") || text.contains("instamart") || 
            text.contains("dmart") || text.contains("bigbasket") || text.contains("grocery") || 
            text.contains("shopping") || text.contains("mart") || text.contains("cloth") -> "Shopping"

            text.contains("bescom") || text.contains("airtel") || text.contains("jio") || 
            text.contains("vi") || text.contains("electric") || text.contains("recharge") || 
            text.contains("broadband") || text.contains("wifi") || text.contains("water") || 
            text.contains("gas") || text.contains("rent") || text.contains("bill") -> "Bills & Utilities"

            text.contains("netflix") || text.contains("spotify") || text.contains("bookmyshow") || 
            text.contains("pvr") || text.contains("inox") || text.contains("movie") || 
            text.contains("game") || text.contains("prime") || text.contains("youtube") -> "Entertainment"

            text.contains("apollo") || text.contains("pharmeasy") || text.contains("1mg") || 
            text.contains("cult") || text.contains("gym") || text.contains("hospital") || 
            text.contains("doctor") || text.contains("medic") || text.contains("pharmacy") -> "Health & Medical"

            text.contains("apple") || text.contains("croma") || text.contains("laptop") || 
            text.contains("phone") || text.contains("gadget") || text.contains("tech") -> "Tech & Gear"

            else -> "Food & Dining" // Default intuitive recommendation
        }
    }
}
