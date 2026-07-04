package com.genymobile.scrcpy.android.core

import android.graphics.Rect
import java.io.DataOutputStream

/** Protocol constants and serializers compatible with the desktop C client. */
object ScrcpyProtocol {
    const val DEFAULT_VIDEO_PORT = 27183
    const val CONTROL_MSG_TYPE_INJECT_KEYCODE = 0
    const val CONTROL_MSG_TYPE_INJECT_TEXT = 1
    const val CONTROL_MSG_TYPE_INJECT_TOUCH_EVENT = 2
    const val CONTROL_MSG_TYPE_INJECT_SCROLL_EVENT = 3
    const val CONTROL_MSG_TYPE_BACK_OR_SCREEN_ON = 4
    const val CONTROL_MSG_TYPE_EXPAND_NOTIFICATION_PANEL = 5
    const val CONTROL_MSG_TYPE_EXPAND_SETTINGS_PANEL = 6
    const val CONTROL_MSG_TYPE_COLLAPSE_PANELS = 7
    const val CONTROL_MSG_TYPE_GET_CLIPBOARD = 8
    const val CONTROL_MSG_TYPE_SET_CLIPBOARD = 9
    const val CONTROL_MSG_TYPE_SET_SCREEN_POWER_MODE = 10
    const val CONTROL_MSG_TYPE_ROTATE_DEVICE = 11
    const val CONTROL_MSG_TYPE_UHID_CREATE = 12
    const val CONTROL_MSG_TYPE_UHID_INPUT = 13
    const val CONTROL_MSG_TYPE_UHID_DESTROY = 14
    const val CONTROL_MSG_TYPE_OPEN_HARD_KEYBOARD_SETTINGS = 15
    const val CONTROL_MSG_TYPE_START_APP = 16
    const val CONTROL_MSG_TYPE_RESET_VIDEO = 17

    const val AMOTION_EVENT_ACTION_DOWN = 0
    const val AMOTION_EVENT_ACTION_UP = 1
    const val AMOTION_EVENT_ACTION_MOVE = 2
    const val AMOTION_EVENT_BUTTON_PRIMARY = 1

    init { ScrcpyNative.load() }

    fun writeBackOrScreenOn(out: DataOutputStream, action: Int) {
        out.writeByte(CONTROL_MSG_TYPE_BACK_OR_SCREEN_ON)
        out.writeByte(action)
        out.flush()
    }

    fun writeTouch(out: DataOutputStream, action: Int, pointerId: Long, x: Int, y: Int, videoWidth: Int, videoHeight: Int, pressure: Float, buttons: Int) {
        val bytes = ScrcpyNative.serializeTouch(action, pointerId, x, y, videoWidth, videoHeight, pressure, buttons)
        out.write(bytes)
        out.flush()
    }

    fun displayRect(surfaceWidth: Int, surfaceHeight: Int, videoWidth: Int, videoHeight: Int): Rect {
        if (videoWidth <= 0 || videoHeight <= 0 || surfaceWidth <= 0 || surfaceHeight <= 0) return Rect()
        val scale = minOf(surfaceWidth.toFloat() / videoWidth, surfaceHeight.toFloat() / videoHeight)
        val w = (videoWidth * scale).toInt()
        val h = (videoHeight * scale).toInt()
        val l = (surfaceWidth - w) / 2
        val t = (surfaceHeight - h) / 2
        return Rect(l, t, l + w, t + h)
    }
}
