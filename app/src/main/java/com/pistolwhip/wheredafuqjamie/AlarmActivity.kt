package com.pistolwhip.wheredafuqjamie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView

class AlarmActivity : Activity() {
    private var downX = 0f
    private var downY = 0f
    private var lastVolumeKey = -1
    private var lastVolumeKeyTime = 0L
    private val doubleTapWindowMs = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        startForegroundService(Intent(this, AlarmService::class.java).setAction(AlarmService.ACTION_ACTIVATE))
        val text = TextView(this).apply { text = "SWIPE TO DEACTIVATE"; textSize = 34f; setTextColor(Color.WHITE); gravity = Gravity.CENTER; setBackgroundColor(Prefs.popup(this@AlarmActivity)) }
        setContentView(text)
        text.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> { downX = event.x; downY = event.y; true }
                MotionEvent.ACTION_UP -> { if (Prefs.deactSwipe(this) && kotlin.math.hypot((event.x - downX).toDouble(), (event.y - downY).toDouble()) > 180) deactivate(); true }
                else -> true
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) && event.repeatCount == 0) {
            val enabled = if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) Prefs.deactVolUp(this) else Prefs.deactVolDown(this)
            val now = android.os.SystemClock.elapsedRealtime()
            if (enabled && keyCode == lastVolumeKey && now - lastVolumeKeyTime <= doubleTapWindowMs) {
                lastVolumeKey = -1
                lastVolumeKeyTime = 0L
                deactivate()
                return true
            }
            lastVolumeKey = keyCode
            lastVolumeKeyTime = now
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun deactivate() { stopService(Intent(this, AlarmService::class.java)); AlarmNotification.cancel(this); finish() }
}
