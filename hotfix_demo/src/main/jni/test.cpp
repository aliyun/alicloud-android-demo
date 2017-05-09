#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <stddef.h>
#include <string.h>
#include "common.h"

#define JNIREG_CLASS "com/alibaba/sophix/demo/SOFixDemo"
const int g_nDataSize = 3000;

extern "C" JNIEXPORT void JNICALL Java_com_taobao_patch_demo_SOFixDemo_print(JNIEnv *env, jclass clazz) {
    LOGD("old native print %d", g_nDataSize);
}

jstring test1(JNIEnv *env, jclass clazz, jstring j_str) {
    LOGD("start native method: test1");

    const char *c_str = NULL;
    char buff[128] = {0};
    jboolean isCopy;
    c_str = env->GetStringUTFChars(j_str, &isCopy);
    if (c_str == NULL) {
        return NULL;
    }
    sprintf(buff, "old %s", c_str);
//    sprintf(buff, "new %s", c_str);
    env->ReleaseStringUTFChars(j_str, c_str);
    return env->NewStringUTF(buff);
}

void test2(JNIEnv *env, jclass clazz) {
    LOGD("start native method: test2");
}

JNINativeMethod nativeMethods[] = {
        {"test1", "(Ljava/lang/String;)Ljava/lang/String;", (void *) test1},
        {"test2", "()V",                                    (void *) test2}
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGD("JNI_OnLoad");
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    jclass clz = env->FindClass(JNIREG_CLASS);
    if (env->RegisterNatives(clz, nativeMethods,
                             sizeof(nativeMethods) / sizeof(nativeMethods[0])) != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_4;
}

