package com.example.kilukkam.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.sunnyCardShadow(
    cornerRadius: Dp = 24.dp,
    blurRadius: Dp = 12.dp,
    offsetY: Dp = 4.dp,
    shadowColor: Color = Color(0x0C000000)
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()

        frameworkPaint.color = shadowColor.value.toLong().toInt()
        frameworkPaint.maskFilter = android.graphics.BlurMaskFilter(
            blurRadius.toPx(),
            android.graphics.BlurMaskFilter.Blur.NORMAL
        )
        canvas.drawRoundRect(
            left = 0f,
            top = offsetY.toPx(),
            right = size.width,
            bottom = size.height + offsetY.toPx(),
            radiusX = cornerRadius.toPx(),
            radiusY = cornerRadius.toPx(),
            paint = paint
        )
    }
}
