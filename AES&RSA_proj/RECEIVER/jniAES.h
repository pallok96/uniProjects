/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class jniAES */

#ifndef _Included_jniAES
#define _Included_jniAES
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     jniAES
 * Method:    encAES
 * Signature: ([B[B[B[B)V
 */
JNIEXPORT void JNICALL Java_jniAES_encAES
  (JNIEnv *, jobject, jbyteArray, jbyteArray, jbyteArray, jbyteArray);

/*
 * Class:     jniAES
 * Method:    decAES
 * Signature: ([B[B[B[B)V
 */
JNIEXPORT void JNICALL Java_jniAES_decAES
  (JNIEnv *, jobject, jbyteArray, jbyteArray, jbyteArray, jbyteArray);

/*
 * Class:     jniAES
 * Method:    findRes
 * Signature: ([B[B[B)B
 */
JNIEXPORT jint JNICALL Java_jniAES_findRes
  (JNIEnv *, jobject, jbyteArray, jbyteArray, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
