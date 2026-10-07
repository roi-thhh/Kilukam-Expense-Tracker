package com.example.kilukkam

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionParserTest {

    @Test
    fun testIsUpiTransaction() {
        // Valid transactions
        assertTrue(TransactionParser.isUpiTransaction("Rs.500.00 debited from a/c for UPI/Zomato", "AD-HDFCBK"))
        assertTrue(TransactionParser.isUpiTransaction("Sent Rs 200 via UPI to John", "VK-ICICIB"))
        assertTrue(TransactionParser.isUpiTransaction("Paid INR 1,500.50 via VPA xyz@upi", "AX-SBIBNK"))

        // Invalid: missing transaction/upi keywords
        assertFalse(TransactionParser.isUpiTransaction("Your OTP for transaction is 123456", "AD-HDFCBK"))
        
        // Invalid: Scam messages
        assertFalse(TransactionParser.isUpiTransaction("You won Rs 50,000! Click here http://scam.link via UPI", "BX-SPAMMR"))
        assertFalse(TransactionParser.isUpiTransaction("Free money deposited to your a/c. Check http://fake.com", "1234567890"))
        
        // Invalid: Suspicious sender (regular 10 digit number)
        assertFalse(TransactionParser.isUpiTransaction("Rs 500 debited via UPI", "+919876543210"))
    }

    @Test
    fun testExtractAmount() {
        assertEquals(500.0, TransactionParser.extractAmount("Rs.500.00 debited from a/c for UPI/Zomato"))
        assertEquals(200.0, TransactionParser.extractAmount("Sent Rs 200 via UPI to John"))
        assertEquals(1500.5, TransactionParser.extractAmount("Paid INR 1,500.50 via VPA xyz@upi"))
        assertEquals(10.0, TransactionParser.extractAmount("Paid Rs10.0 via upi"))
        
        // No amount found
        assertEquals(null, TransactionParser.extractAmount("Paid some amount via UPI"))
    }
}
