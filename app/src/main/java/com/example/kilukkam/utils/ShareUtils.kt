package com.example.kilukkam.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.kilukkam.data.Expense
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ShareUtils {

    /**
     * Generates a high-resolution, categorized financial report card with an authentic
     * segmented donut graph and category breakdown, styled in the signature Sunny Fintech design.
     */
    fun shareAnalyticsAsImage(
        context: Context,
        expenses: List<Expense>,
        totalExpenses: Double? = null
    ) {
        if (expenses.isEmpty() && (totalExpenses == null || totalExpenses <= 0.0)) {
            Toast.makeText(context, "No spending data to share yet! Add some transactions first.", Toast.LENGTH_SHORT).show()
            return
        }

        val width = 1080
        val height = 1520
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Sunny Fintech Design Tokens
        val colorCanvasBg = Color.parseColor("#FFFDF6")       // Warm Cream Canvas
        val colorCardBg = Color.WHITE                         // Pure White Card
        val colorBorderSubtle = Color.parseColor("#E6E3D8")   // Subtle Warm Border
        val colorBrandYellow = Color.parseColor("#FFD928")    // Signature Sunny Yellow
        val colorTextDark = Color.parseColor("#171717")       // Deep Charcoal
        val colorTextSecondary = Color.parseColor("#737373")  // Neutral Gray
        val colorMutedBg = Color.parseColor("#F5F3EB")        // Cream Muted Pill
        val colorTrackBg = Color.parseColor("#ECEAE0")        // Track Background

        // Curated Category Colors Palette matching AnalyticsScreen
        val categoryColorHexes = listOf(
            "#FFD928", // Sunny Yellow Primary
            "#10B981", // Emerald Green
            "#F43F5E", // Coral Rose
            "#3B82F6", // Electric Blue
            "#0EA5E9", // Sky Blue
            "#F59E0B", // Warm Amber
            "#8B5CF6", // Royal Violet
            "#EC4899"  // Hot Pink
        )

        // 1. Draw Canvas Background
        canvas.drawColor(colorCanvasBg)

        // 2. Outer Card with Soft Shadow
        val marginX = 52f
        val marginY = 56f
        val cardRect = RectF(marginX, marginY, width - marginX, height - marginY)
        val cornerRadius = 48f

        // Soft ambient shadow
        val shadowPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        for (i in 6 downTo 1) {
            shadowPaint.color = Color.argb(8 * i, 180, 175, 160)
            val shadowRect = RectF(marginX, marginY + (i * 2.5f), width - marginX, height - marginY + (i * 2.5f))
            canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint)
        }

        // Draw Card Background
        val cardPaint = Paint().apply {
            isAntiAlias = true
            color = colorCardBg
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, cardPaint)

        // Draw Card Border
        val borderPaint = Paint().apply {
            isAntiAlias = true
            color = colorBorderSubtle
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        canvas.drawRoundRect(cardRect, cornerRadius, cornerRadius, borderPaint)

        // 3. Card Header
        val contentPaddingX = marginX + 44f
        var currentY = marginY + 44f

        // Brand Pill: Sunny Yellow badge with "KILUKKAM"
        val pillPaint = Paint().apply {
            isAntiAlias = true
            color = colorBrandYellow
            style = Paint.Style.FILL
        }
        val pillWidth = 190f
        val pillHeight = 48f
        val pillRect = RectF(contentPaddingX, currentY, contentPaddingX + pillWidth, currentY + pillHeight)
        canvas.drawRoundRect(pillRect, pillHeight / 2f, pillHeight / 2f, pillPaint)

        val brandTextPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextDark
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("KILUKKAM", contentPaddingX + pillWidth / 2f, currentY + 33f, brandTextPaint)

        // Date Tag Pill on top right
        val dateStr = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
        val datePillWidth = 220f
        val datePillRect = RectF(width - contentPaddingX - datePillWidth, currentY, width - contentPaddingX, currentY + pillHeight)
        val mutedPillPaint = Paint().apply {
            isAntiAlias = true
            color = colorMutedBg
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(datePillRect, 16f, 16f, mutedPillPaint)

        val dateTextPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextSecondary
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(dateStr, width - contentPaddingX - datePillWidth / 2f, currentY + 32f, dateTextPaint)

        // Header Subtitles
        currentY += 72f
        val subHeaderPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextSecondary
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            letterSpacing = 0.08f
        }
        canvas.drawText("SPENDING INSIGHTS", contentPaddingX, currentY, subHeaderPaint)

        currentY += 46f
        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = colorTextDark
            textSize = 42f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Financial Breakdown", contentPaddingX, currentY, titlePaint)

        // 4. Data Calculations
        val totalSpent = totalExpenses ?: expenses.sumOf { it.amount }
        val categoryTotals = expenses.groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }

        // Top 5 categories + "Other"
        val displayItems = mutableListOf<Pair<String, Double>>()
        if (categoryTotals.size <= 5) {
            displayItems.addAll(categoryTotals)
        } else {
            displayItems.addAll(categoryTotals.take(4))
            val otherSum = categoryTotals.drop(4).sumOf { it.second }
            displayItems.add("Other" to otherSum)
        }

        // 5. Categorized Donut Chart
        val chartCx = width / 2f
        val chartCy = 490f
        val chartRadius = 165f
        val chartStroke = 60f
        val chartRect = RectF(
            chartCx - chartRadius,
            chartCy - chartRadius,
            chartCx + chartRadius,
            chartCy + chartRadius
        )

        val trackArcPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = chartStroke
            color = colorTrackBg
        }
        canvas.drawArc(chartRect, 0f, 360f, false, trackArcPaint)

        if (totalSpent > 0 && displayItems.isNotEmpty()) {
            val arcPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = chartStroke
            }

            var startAngle = -90f
            val gapAngle = if (displayItems.size > 1) 2.5f else 0f

            displayItems.forEachIndexed { index, (_, amount) ->
                val sweep = ((amount / totalSpent) * 360f).toFloat()
                val effSweep = (sweep - gapAngle).coerceAtLeast(1f)
                val hex = categoryColorHexes[index % categoryColorHexes.size]
                arcPaint.color = Color.parseColor(hex)
                canvas.drawArc(chartRect, startAngle + gapAngle / 2f, effSweep, false, arcPaint)
                startAngle += sweep
            }
        }

        // Center of Donut text
        val donutLabelPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextSecondary
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.06f
        }
        canvas.drawText("TOTAL SPENT", chartCx, chartCy - 34f, donutLabelPaint)

        val donutAmountPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextDark
            textSize = 52f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(String.format(Locale.getDefault(), "₹%,.2f", totalSpent), chartCx, chartCy + 16f, donutAmountPaint)

        val donutMetaPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextSecondary
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = Paint.Align.CENTER
        }
        val metaString = if (expenses.isNotEmpty()) {
            "${categoryTotals.size} Categories • ${expenses.size} Txns"
        } else {
            "Overview"
        }
        canvas.drawText(metaString, chartCx, chartCy + 54f, donutMetaPaint)

        // 6. Category Breakdown List
        currentY = 724f
        val sectionHeaderPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextDark
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("CATEGORY BREAKDOWN", contentPaddingX, currentY, sectionHeaderPaint)

        // 100% Total Pill on the right
        val totalPillWidth = 130f
        val totalPillHeight = 36f
        val totalPillRect = RectF(
            width - contentPaddingX - totalPillWidth,
            currentY - 26f,
            width - contentPaddingX,
            currentY - 26f + totalPillHeight
        )
        canvas.drawRoundRect(totalPillRect, 10f, 10f, mutedPillPaint)
        val totalPillTextPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextSecondary
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("100% Total", width - contentPaddingX - totalPillWidth / 2f, currentY - 2f, totalPillTextPaint)

        // Render each category row
        val rowStartY = currentY + 36f
        val rowHeight = 78f
        val rowWidth = (width - contentPaddingX * 2)

        val dotPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val catNamePaint = Paint().apply {
            isAntiAlias = true
            color = colorTextDark
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val catPercentPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextSecondary
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val catAmountPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextDark
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
        }

        val trackBarPaint = Paint().apply {
            isAntiAlias = true
            color = colorMutedBg
            style = Paint.Style.FILL
        }

        val fillBarPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        displayItems.forEachIndexed { index, (catName, amount) ->
            val rowY = rowStartY + (index * rowHeight)
            val colorHex = categoryColorHexes[index % categoryColorHexes.size]
            val catColor = Color.parseColor(colorHex)
            val percentage = if (totalSpent > 0) (amount / totalSpent * 100.0) else 0.0

            // Category color dot
            dotPaint.color = catColor
            canvas.drawCircle(contentPaddingX + 12f, rowY + 12f, 10f, dotPaint)

            // Category Name
            canvas.drawText(catName, contentPaddingX + 36f, rowY + 20f, catNamePaint)

            // Percentage
            val pctText = String.format(Locale.getDefault(), "%.1f%%", percentage)
            canvas.drawText(pctText, contentPaddingX + 380f, rowY + 20f, catPercentPaint)

            // Category Amount
            val amountText = String.format(Locale.getDefault(), "₹%,.2f", amount)
            canvas.drawText(amountText, width - contentPaddingX, rowY + 20f, catAmountPaint)

            // Proportional Progress Bar
            val barY = rowY + 36f
            val barHeight = 8f
            val trackRect = RectF(contentPaddingX, barY, width - contentPaddingX, barY + barHeight)
            canvas.drawRoundRect(trackRect, 4f, 4f, trackBarPaint)

            val fillWidth = (rowWidth * (percentage / 100.0).coerceIn(0.01, 1.0)).toFloat()
            val fillRect = RectF(contentPaddingX, barY, contentPaddingX + fillWidth, barY + barHeight)
            fillBarPaint.color = catColor
            canvas.drawRoundRect(fillRect, 4f, 4f, fillBarPaint)
        }

        // 7. Bottom Metric Cards
        val statsY = rowStartY + (displayItems.size * rowHeight) + 24f
        val statCardWidth = (rowWidth - 20f) / 2f
        val statCardHeight = 90f

        // Metric Card 1: Top Category
        val statCard1Rect = RectF(contentPaddingX, statsY, contentPaddingX + statCardWidth, statsY + statCardHeight)
        canvas.drawRoundRect(statCard1Rect, 20f, 20f, mutedPillPaint)

        val statLabelPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextSecondary
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val statValuePaint = Paint().apply {
            isAntiAlias = true
            color = colorTextDark
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        canvas.drawText("TOP CATEGORY", contentPaddingX + 24f, statsY + 34f, statLabelPaint)
        val topCategoryName = displayItems.firstOrNull()?.first ?: "N/A"
        val safeTopName = if (topCategoryName.length > 14) topCategoryName.take(13) + "…" else topCategoryName
        canvas.drawText(safeTopName, contentPaddingX + 24f, statsY + 68f, statValuePaint)

        // Metric Card 2: Average per Transaction
        val statCard2Left = contentPaddingX + statCardWidth + 20f
        val statCard2Rect = RectF(statCard2Left, statsY, statCard2Left + statCardWidth, statsY + statCardHeight)
        canvas.drawRoundRect(statCard2Rect, 20f, 20f, mutedPillPaint)

        canvas.drawText("AVG / TRANSACTION", statCard2Left + 24f, statsY + 34f, statLabelPaint)
        val avgTxn = if (expenses.isNotEmpty()) totalSpent / expenses.size else 0.0
        val avgStr = String.format(Locale.getDefault(), "₹%,.0f", avgTxn)
        canvas.drawText(avgStr, statCard2Left + 24f, statsY + 68f, statValuePaint)

        // 8. Branded Card Footer
        val footerY = height - marginY - 60f
        val linePaint = Paint().apply {
            isAntiAlias = true
            color = colorBorderSubtle
            strokeWidth = 1.5f
        }
        canvas.drawLine(contentPaddingX, footerY, width - contentPaddingX, footerY, linePaint)

        // Sunny yellow accent dot
        dotPaint.color = colorBrandYellow
        canvas.drawCircle(contentPaddingX + 10f, footerY + 28f, 7f, dotPaint)

        val footerTextPaint = Paint().apply {
            isAntiAlias = true
            color = colorTextSecondary
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        canvas.drawText(
            "100% Offline • Private & Secure • Kilukkam Smart Finance",
            contentPaddingX + 28f,
            footerY + 34f,
            footerTextPaint
        )

        // 9. Save and Launch Share Chooser
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "kilukkam_analytics.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val uri = FileProvider.getUriForFile(context, "com.example.kilukkam.provider", file)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "My Spending Insights - Kilukkam")
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Share Spending Report via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to share report: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Backward-compatible overload for legacy calls.
     */
    fun shareAnalyticsAsImage(context: Context, totalExpenses: Double) {
        shareAnalyticsAsImage(context, emptyList(), totalExpenses)
    }

    fun exportAndShareCsv(context: Context, expenses: List<Expense>, incomes: List<Expense>) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val csvBuilder = StringBuilder()
        csvBuilder.append("Type,Amount,Category,Date,Transaction ID\n")

        for (income in incomes) {
            val dateStr = dateFormat.format(Date(income.timestamp))
            val safeCat = income.category.replace("\"", "\"\"")
            csvBuilder.append("\"Income\",${income.amount},\"$safeCat\",\"$dateStr\",\"${income.id}\"\n")
        }
        for (expense in expenses) {
            val dateStr = dateFormat.format(Date(expense.timestamp))
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
            Toast.makeText(context, "Failed to export CSV: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
