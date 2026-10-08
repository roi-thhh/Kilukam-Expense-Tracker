package com.example.kilukkam

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.core.app.NotificationCompat

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                val messageBody = sms.displayMessageBody
                val sender = sms.displayOriginatingAddress
                
                Log.d("SmsReceiver", "Received SMS from: $sender, Body: $messageBody")
                
                if (TransactionParser.isTransactionalSms(messageBody, sender)) {
                    val parsed = TransactionParser.parse(messageBody, sender)
                    if (parsed != null) {
                        Log.d("SmsReceiver", "Parsed Transaction: $parsed")
                        
                        val categoryIntent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra("transaction_amount", parsed.amount)
                            putExtra("transaction_merchant", parsed.merchant)
                            putExtra("transaction_account", parsed.accountName)
                            putExtra("transaction_account_type", parsed.accountType)
                            putExtra("suggested_category", parsed.suggestedCategory)
                            putExtra("is_income", parsed.isIncome)
                            putExtra("show_categorize_dialog", true)
                        }
                        
                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            parsed.amount.toInt() + System.currentTimeMillis().toInt(),
                            categoryIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        val title = if (parsed.merchant != null) {
                            "Payment to ${parsed.merchant}"
                        } else {
                            if (parsed.isIncome) "Income Detected" else "New Payment Detected"
                        }
                        
                        val contentText = if (parsed.merchant != null) {
                            "₹${"%.0f".format(parsed.amount)} via ${parsed.accountName}. Tap to categorize."
                        } else {
                            "₹${"%.0f".format(parsed.amount)} paid. Tap to categorize."
                        }

                        val builder = NotificationCompat.Builder(context, "TRANSACTION_CHANNEL")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(title)
                            .setContentText(contentText)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL) // Instant heads-up display
                            .setFullScreenIntent(pendingIntent, true)
                            .setAutoCancel(true)

                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
                    }
                }
            }
        }
    }
}
