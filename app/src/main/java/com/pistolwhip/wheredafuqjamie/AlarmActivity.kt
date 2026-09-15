package com.pistolwhip.wheredafuqjamie

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView

class AlarmActivity : Activity() {
    private var downX = 0f
    private var downY = 0f
    private var service: AlarmService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        val text = TextView(this).apply {
            text = "SWIPE TO DEACTIVATE"
            textSize = 34f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setBackgroundColor(Prefs.popup(this@AlarmActivity))
        }
        setContentView(text)
        text.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> { downX = event.x; downY = event.y; true }
                MotionEvent.ACTION_UP -> {
                    val distance = kotlin.math.hypot((event.x - downX).toDouble(), (event.y - downY).toDouble())
                    if (Prefs.deactSwipe(this) && distance > 180) deactivate()
                    true
                }
                else -> true
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP && Prefs.deactVolUp(this)) { deactivate(); return true }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && Prefs.deactVolDown(this)) { deactivate(); return true }
        return super.onKeyDown(keyCode, event)
    }

    private fun deactivate() {
        startService(android.content.Intent(this, AlarmService::class.java).setAction(AlarmService.ACTION_DEACTIVATE))
        finish()
    }
}
