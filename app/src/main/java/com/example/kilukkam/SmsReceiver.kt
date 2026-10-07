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
                
                if (TransactionParser.isUpiTransaction(messageBody, sender)) {
                    val amount = TransactionParser.extractAmount(messageBody)
                    if (amount != null) {
                        Log.d("SmsReceiver", "Extracted Amount: $amount")
                        
                        val categoryIntent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            putExtra("transaction_amount", amount)
                            putExtra("show_categorize_dialog", true)
                        }
                        
                        val pendingIntent = PendingIntent.getActivity(
                            context,
                            amount.toInt(),
                            categoryIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )

                        val builder = NotificationCompat.Builder(context, "TRANSACTION_CHANNEL")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("New Payment Detected")
                            .setContentText("₹$amount paid. Tap to categorize.")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL) // Tricks Android into showing it immediately
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
