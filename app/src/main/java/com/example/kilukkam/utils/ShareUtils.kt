package com.example.kilukkam.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.example.kilukkam.data.Expense
import java.io.File
import java.io.FileOutputStream

object ShareUtils {
    fun shareAnalyticsAsImage(context: Context, totalExpenses: Double) {
        val width = 1080
        val height = 1080
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(android.graphics.Color.parseColor("#080909"))

        val paintGlow = Paint().apply {
            color = android.graphics.Color.parseColor("#3300FFFF") 
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height / 2f, 500f, paintGlow)

        val textPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 64f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        
        canvas.drawText("KILUKKAM ANALYTICS", width / 2f, 200f, textPaint)
        
        textPaint.color = android.graphics.Color.parseColor("#A0A0A0")
        textPaint.textSize = 48f
        canvas.drawText("Total Net Balance", width / 2f, 450f, textPaint)

        textPaint.color = android.graphics.Color.parseColor("#C8FF24") 
        textPaint.textSize = 140f
        canvas.drawText(String.format("₹%.2f", totalExpenses), width / 2f, 600f, textPaint)

        textPaint.color = android.graphics.Color.WHITE
        textPaint.textSize = 40f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Powered by Kilukkam", width / 2f, height - 100f, textPaint)

        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "analytics_share.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val uri = FileProvider.getUriForFile(context, "com.example.kilukkam.provider", file)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Analytics"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun exportAndShareCsv(context: Context, expenses: List<Expense>, incomes: List<Expense>) {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        val csvBuilder = StringBuilder()
        csvBuilder.append("Type,Amount,Category,Date,Transaction ID\n")
        
        for (income in incomes) {
            val dateStr = dateFormat.format(java.util.Date(income.timestamp))
            val safeCat = income.category.replace("\"", "\"\"")
            csvBuilder.append("\"Income\",${income.amount},\"$safeCat\",\"$dateStr\",\"${income.id}\"\n")
        }
        for (expense in expenses) {
            val dateStr = dateFormat.format(java.util.Date(expense.timestamp))
            val safeCat = expense.category.replace("\"", "\"\"")
            csvBuilder.append("\"Expense\",${expense.amount},\"$safeCat\",\"$dateStr\",\"${expense.id}\"\n")
        }

        try {
            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()
            val file = File(exportDir, "kilukkam_transactions.csv")
            file.writeText(csvBuilder.toString())

            val uri = FileProvider.getUriForFile(context, "com.example.kilukkam.provider", file)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Kilukkam Financial Export")
                type = "text/csv"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Export Transactions via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
