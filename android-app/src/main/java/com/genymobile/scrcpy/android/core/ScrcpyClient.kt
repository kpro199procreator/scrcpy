package com.genymobile.scrcpy.android.core

import android.media.MediaCodec
import android.media.MediaFormat
import android.view.Surface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket

class ScrcpyClient(private val host: String, private val port: Int = ScrcpyProtocol.DEFAULT_VIDEO_PORT) {
    private var socket: Socket? = null
    private var control: DataOutputStream? = null
    var videoWidth = 1080; private set
    var videoHeight = 1920; private set

    suspend fun connect(surface: Surface) = withContext(Dispatchers.IO) {
        val s = Socket().apply { connect(InetSocketAddress(host, port), 5000); tcpNoDelay = true }
        socket = s
        val input = DataInputStream(BufferedInputStream(s.getInputStream()))
        control = DataOutputStream(s.getOutputStream())
        decodeAnnexB(input, surface)
    }

    fun sendTouch(action: Int, pointerId: Long, x: Int, y: Int, pressure: Float, buttons: Int) {
        control?.let { ScrcpyProtocol.writeTouch(it, action, pointerId, x, y, videoWidth, videoHeight, pressure, buttons) }
    }

    fun back() { control?.let { ScrcpyProtocol.writeBackOrScreenOn(it, 0) } }
    fun close() { socket?.close() }

    private fun decodeAnnexB(input: DataInputStream, surface: Surface) {
        val codec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        codec.configure(MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, videoWidth, videoHeight), surface, null, 0)
        codec.start()
        val info = MediaCodec.BufferInfo()
        val scratch = ByteArray(64 * 1024)
        while (!Thread.interrupted()) {
            val index = codec.dequeueInputBuffer(10_000)
            if (index >= 0) {
                val buffer = codec.getInputBuffer(index) ?: continue
                buffer.clear()
                val count = input.read(scratch)
                if (count < 0) break
                buffer.put(scratch, 0, count)
                codec.queueInputBuffer(index, 0, count, System.nanoTime() / 1000, 0)
            }
            var out = codec.dequeueOutputBuffer(info, 0)
            while (out >= 0) {
                codec.releaseOutputBuffer(out, true)
                out = codec.dequeueOutputBuffer(info, 0)
            }
        }
        codec.stop(); codec.release()
    }
}
