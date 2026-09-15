package com.pistolwhip.wheredafuqjamie

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2

class ColorWheelView(context: Context) : View(context) {
    var selectedColor: Int = Color.GREEN
        set(value) { field = value; invalidate() }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shader = SweepGradient(0f, 0f, intArrayOf(
        Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED
    ), null)

    override fun onDraw(canvas: Canvas) {
        val r = (width.coerceAtMost(height) / 2f) - 12f
        canvas.translate(width / 2f, height / 2f)
        paint.shader = shader
        canvas.drawCircle(0f, 0f, r, paint)
        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        paint.color = Color.WHITE
        canvas.drawCircle(0f, 0f, r, paint)
        paint.style = Paint.Style.FILL
        paint.color = selectedColor
        canvas.drawCircle(0f, 0f, r * .18f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_UP) {
            val x = event.x - width / 2f
            val y = event.y - height / 2f
            val r = (width.coerceAtMost(height) / 2f) - 12f
            if (x * x + y * y <= r * r) {
                var hue = Math.toDegrees(atan2(y.toDouble(), x.toDouble())).toFloat() + 90f
                if (hue < 0) hue += 360f
                selectedColor = Color.HSVToColor(floatArrayOf(hue, 0.85f, 0.85f))
            }
            return true
        }
        return super.onTouchEvent(event)
    }
}
