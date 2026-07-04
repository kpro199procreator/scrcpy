#include <jni.h>
#include <stdint.h>

#define TYPE_INJECT_TOUCH_EVENT 2

static void put16(uint8_t *p, uint16_t v){ p[0]=(uint8_t)(v>>8); p[1]=(uint8_t)v; }
static void put32(uint8_t *p, uint32_t v){ p[0]=(uint8_t)(v>>24); p[1]=(uint8_t)(v>>16); p[2]=(uint8_t)(v>>8); p[3]=(uint8_t)v; }
static void put64(uint8_t *p, uint64_t v){ for(int i=7;i>=0;--i){ p[7-i]=(uint8_t)(v>>(i*8)); } }

JNIEXPORT jbyteArray JNICALL
Java_com_genymobile_scrcpy_android_core_ScrcpyNative_serializeTouch(JNIEnv *env, jobject thiz, jint action, jlong pointer_id, jint x, jint y, jint w, jint h, jfloat pressure, jint buttons) {
    (void) thiz;
    uint8_t msg[32] = {0};
    msg[0] = TYPE_INJECT_TOUCH_EVENT;
    msg[1] = (uint8_t) action;
    put64(&msg[2], (uint64_t) pointer_id);
    put32(&msg[10], (uint32_t) x);
    put32(&msg[14], (uint32_t) y);
    put16(&msg[18], (uint16_t) w);
    put16(&msg[20], (uint16_t) h);
    uint16_t p = pressure <= 0 ? 0 : pressure >= 1 ? 0xffff : (uint16_t) (pressure * 0xffff);
    put16(&msg[22], p);
    put32(&msg[24], (uint32_t) buttons);
    jbyteArray out = (*env)->NewByteArray(env, sizeof msg);
    (*env)->SetByteArrayRegion(env, out, 0, sizeof msg, (const jbyte *) msg);
    return out;
}
