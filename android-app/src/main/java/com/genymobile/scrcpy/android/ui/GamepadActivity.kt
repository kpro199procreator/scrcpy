package com.genymobile.scrcpy.android.ui

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.*

class GamepadActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(24,24,24,24) }
        root.addView(stick("Left stick"), LinearLayout.LayoutParams(0, -1, 1f))
        root.addView(buttonPad(), LinearLayout.LayoutParams(0, -1, 1f))
        root.addView(stick("Right stick"), LinearLayout.LayoutParams(0, -1, 1f))
        setContentView(root)
    }

    private fun stick(label: String): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        addView(TextView(this@GamepadActivity).apply { text = label; textSize = 20f })
        addView(TextView(this@GamepadActivity).apply {
            text = "◎"; textSize = 96f; gravity = android.view.Gravity.CENTER
            setOnTouchListener { _, e ->
                val nx = (e.x / width * 2f - 1f).coerceIn(-1f, 1f)
                val ny = (e.y / height * 2f - 1f).coerceIn(-1f, 1f)
                text = if (e.actionMasked == MotionEvent.ACTION_UP) "◎" else "${label.first()}: ${"%.2f".format(nx)}, ${"%.2f".format(ny)}"
                true
            }
        }, LinearLayout.LayoutParams(-1, 0, 1f))
    }

    private fun buttonPad(): GridLayout = GridLayout(this).apply {
        columnCount = 2; rowCount = 3
        listOf("Y", "X", "B", "A", "L/R", "Start").forEach { label ->
            addView(Button(this@GamepadActivity).apply { text = label; textSize = 24f }, ViewGroup.LayoutParams(220, 160))
        }
    }
}
