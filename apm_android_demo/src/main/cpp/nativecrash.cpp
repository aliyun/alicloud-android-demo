#include <jni.h>
#include <string>
#include <iostream>
#include <vector>
#include <stdlib.h>
#include <asm-generic/fcntl.h>
#include <fcntl.h>
#include <signal.h>
#include <unistd.h>

// SIGSEGV
extern "C"
JNIEXPORT void JNICALL
Java_com_aliyun_apm_android_demo_NativeLib_mockSigSegv(
        JNIEnv *env,
        jobject /* this */) {
    int *p = 0;
    *p = 1;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_aliyun_apm_android_demo_NativeLib_mockSigAbrt(
        JNIEnv *env,
        jobject /* this */) {
    raise(SIGABRT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_aliyun_apm_android_demo_NativeLib_mockSigBus(
        JNIEnv *env,
        jobject /* this */) {
    raise(SIGBUS);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_aliyun_apm_android_demo_NativeLib_mockSigIll(
        JNIEnv *env,
        jobject /* this */) {
    raise(SIGILL);
}
