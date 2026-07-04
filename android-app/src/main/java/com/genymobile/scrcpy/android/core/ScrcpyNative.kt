package com.genymobile.scrcpy.android.core

object ScrcpyNative {
    private var loaded = false
    fun load() { if (!loaded) { System.loadLibrary("scrcpy_android"); loaded = true } }
    external fun serializeTouch(action: Int, pointerId: Long, x: Int, y: Int, w: Int, h: Int, pressure: Float, buttons: Int): ByteArray
}
