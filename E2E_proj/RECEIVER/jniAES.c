#include <stdlib.h>
#include <stdio.h>
#include <inttypes.h>
#include <jni.h>
#include "AESLib.h"
#include "jniAES.h"


JNIEXPORT void JNICALL Java_jniAES_encAES
  (JNIEnv *env, jobject obj, jbyteArray Msg_J, jbyteArray Key_J, jbyteArray IV_J, jbyteArray Cryp_J) {
	jbyte *Msg_C = (*env)->GetByteArrayElements(env, Msg_J, 0);
	jbyte *Key_C = (*env)->GetByteArrayElements(env, Key_J, 0);
	jbyte *IV_C = (*env)->GetByteArrayElements(env, IV_J, 0);
	jbyte *Cryp_C = (*env)->GetByteArrayElements(env, Cryp_J, 0);
	
	jlong Msg_len = (*env)->GetArrayLength(env, Msg_J);
	jlong Cryp_len = (*env)->GetArrayLength(env, Cryp_J);

	ENCRYPT(Msg_C, Msg_len, Key_C, IV_C, Cryp_C, Cryp_len);
	
	(*env)->ReleaseByteArrayElements(env, Cryp_J, Cryp_C, 0);
	(*env)->ReleaseByteArrayElements(env, Msg_J, Msg_C, 0);
	(*env)->ReleaseByteArrayElements(env, Key_J, Key_C, 0);
	(*env)->ReleaseByteArrayElements(env, IV_J, IV_C, 0);

	return;
}


JNIEXPORT void JNICALL Java_jniAES_decAES
   (JNIEnv *env, jobject obj, jbyteArray Cryp_J, jbyteArray Key_J, jbyteArray IV_J, jbyteArray Msg_J) {
	jbyte *Cryp_C = (*env)->GetByteArrayElements(env, Cryp_J, 0);
	jbyte *Key_C = (*env)->GetByteArrayElements(env, Key_J, 0);
	jbyte *IV_C = (*env)->GetByteArrayElements(env, IV_J, 0);
	jbyte *Msg_C = (*env)->GetByteArrayElements(env, Msg_J, 0);

	jlong Msg_len = (*env)->GetArrayLength(env, Msg_J);
	jlong Cryp_len = (*env)->GetArrayLength(env, Cryp_J);

	DECRYPT(Cryp_C, Cryp_len, Key_C, IV_C, Msg_C, Msg_len);

	(*env)->ReleaseByteArrayElements(env, Cryp_J, Cryp_C, 0);
	(*env)->ReleaseByteArrayElements(env, Msg_J, Msg_C, 0);
	(*env)->ReleaseByteArrayElements(env, Key_J, Key_C, 0);
	(*env)->ReleaseByteArrayElements(env, IV_J, IV_C, 0);
	
	return;
}


JNIEXPORT jint JNICALL Java_jniAES_findRes
  (JNIEnv *env, jobject obj, jbyteArray Cryp_J, jbyteArray Key_J, jbyteArray IV_J) {
	jbyte *Cryp_C = (*env)->GetByteArrayElements(env, Cryp_J, 0);
	jbyte *Key_C = (*env)->GetByteArrayElements(env, Key_J, 0);
	jbyte *IV_C = (*env)->GetByteArrayElements(env, IV_J, 0);
	
	jint res = findRes(Cryp_C, Key_C, IV_C);
	
	(*env)->ReleaseByteArrayElements(env, Cryp_J, Cryp_C, 0);
	(*env)->ReleaseByteArrayElements(env, Key_J, Key_C, 0);
	(*env)->ReleaseByteArrayElements(env, IV_J, IV_C, 0);
	
	return res;
}
