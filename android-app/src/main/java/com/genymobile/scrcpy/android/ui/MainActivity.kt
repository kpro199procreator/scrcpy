package com.genymobile.scrcpy.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.*
import com.genymobile.scrcpy.android.core.ScrcpyClient
import com.genymobile.scrcpy.android.core.ScrcpyProtocol
import kotlinx.coroutines.*

class MainActivity : Activity(), SurfaceHolder.Callback {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var surface: SurfaceView
    private lateinit var host: EditText
    private var client: ScrcpyClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val bar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        host = EditText(this).apply { hint = "ADB/scrcpy host"; setText("127.0.0.1"); layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
        bar.addView(host)
        bar.addView(Button(this).apply { text = "Connect"; setOnClickListener { connect() } })
        bar.addView(Button(this).apply { text = "Gamepad"; setOnClickListener { startActivity(Intent(this@MainActivity, GamepadActivity::class.java)) } })
        surface = SurfaceView(this).apply { holder.addCallback(this@MainActivity); layoutParams = LinearLayout.LayoutParams(-1, 0, 1f) }
        root.addView(bar); root.addView(surface); setContentView(root)
        surface.setOnTouchListener { v, e ->
            val c = client ?: return@setOnTouchListener true
            val r = ScrcpyProtocol.displayRect(v.width, v.height, c.videoWidth, c.videoHeight)
            val x = ((e.x - r.left) * c.videoWidth / r.width().coerceAtLeast(1)).toInt().coerceIn(0, c.videoWidth)
            val y = ((e.y - r.top) * c.videoHeight / r.height().coerceAtLeast(1)).toInt().coerceIn(0, c.videoHeight)
            c.sendTouch(e.actionMasked, e.getPointerId(e.actionIndex).toLong(), x, y, e.pressure, MotionEvent.BUTTON_PRIMARY)
            true
        }
    }

    private fun connect() { if (surface.holder.surface.isValid) surfaceCreated(surface.holder) }
    override fun surfaceCreated(holder: SurfaceHolder) { scope.launch { client?.close(); client = ScrcpyClient(host.text.toString()); client!!.connect(holder.surface) } }
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit
    override fun surfaceDestroyed(holder: SurfaceHolder) { client?.close() }
    override fun onDestroy() { super.onDestroy(); scope.cancel(); client?.close() }
}
