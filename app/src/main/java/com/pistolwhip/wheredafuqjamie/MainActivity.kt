package com.pistolwhip.wheredafuqjamie

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = FrameLayout(this).apply {
            setBackgroundColor(0xFF000000.toInt())
        }

        val saveButton = Button(this).apply {
            text = "Save"
            setOnClickListener { finish() }
        }

        root.addView(
            saveButton,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            ).apply {
                bottomMargin = (24 * resources.displayMetrics.density).toInt()
            }
        )

        setContentView(root)
    }
}
