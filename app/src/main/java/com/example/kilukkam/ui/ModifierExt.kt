package com.example.kilukkam.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.claymorphism(
    lightShadowColor: Color = Color(0xFF483A4E),
    darkShadowColor: Color = Color(0xFF261D29),
    cornerRadius: Dp = 24.dp,
    blurRadius: Dp = 10.dp,
    offsetX: Dp = 6.dp,
    offsetY: Dp = 6.dp
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()

        frameworkPaint.color = darkShadowColor.value.toLong().toInt()
        frameworkPaint.maskFilter = android.graphics.BlurMaskFilter(
            blurRadius.toPx(),
            android.graphics.BlurMaskFilter.Blur.NORMAL
        )
        canvas.drawRoundRect(
            left = offsetX.toPx(),
            top = offsetY.toPx(),
            right = size.width + offsetX.toPx(),
            bottom = size.height + offsetY.toPx(),
            radiusX = cornerRadius.toPx(),
            radiusY = cornerRadius.toPx(),
            paint = paint
        )

        frameworkPaint.color = lightShadowColor.value.toLong().toInt()
        frameworkPaint.maskFilter = android.graphics.BlurMaskFilter(
            blurRadius.toPx(),
            android.graphics.BlurMaskFilter.Blur.NORMAL
        )
        canvas.drawRoundRect(
            left = -offsetX.toPx(),
            top = -offsetY.toPx(),
            right = size.width - offsetX.toPx(),
            bottom = size.height - offsetY.toPx(),
            radiusX = cornerRadius.toPx(),
            radiusY = cornerRadius.toPx(),
            paint = paint
        )
    }
}
