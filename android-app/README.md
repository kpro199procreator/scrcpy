# scrcpy Android native client prototype

This module is a native Android/Kotlin front-end for scrcpy. It is intentionally separate from the existing Java server code: the app talks to a scrcpy-compatible video/control socket and ports protocol-critical client serialization into JNI C.

Implemented now:

- Kotlin Android application module (`:android-app`).
- Native C/JNI library for scrcpy control-message byte serialization.
- H.264 `MediaCodec` rendering path to a `SurfaceView`.
- Touch-to-control-message forwarding.
- A separate virtual gamepad screen scaffold for future UHID/gamepad packet output.

Typical use while this is a prototype:

1. Start a scrcpy server from another environment and expose its socket over ADB/TCP.
2. Install `:android-app` on the controlling Android device.
3. Enter the host/port endpoint and connect.

Full parity with the SDL/FFmpeg desktop client still requires porting stream metadata parsing, robust packet framing, audio decoding, recording, USB/ADB bootstrap and UHID gamepad dispatch.
