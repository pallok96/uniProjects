#include <stdlib.h>
#include <stdio.h>
#include <inttypes.h>
#include "RSALib.h"
#include "jniRSA.h"
#include <jni.h>

#define KEY_SIZE 16


JNIEXPORT jstring JNICALL Java_jniRSA_genKeyRSA
  (JNIEnv *env, jobject obj) {	
	char* Sk_C = GENKEYasSTRING();
	
	jstring Sk_J = (*env)->NewStringUTF(env, Sk_C);
	free(Sk_C);
	
	return Sk_J;
}


JNIEXPORT jstring JNICALL Java_jniRSA_encRSA
  (JNIEnv *env , jobject obj, jbyteArray M_J, jstring str_n_J, jstring str_e_J) {
	jbyte *M_C = (*env)->GetByteArrayElements(env, M_J, 0);
	const char *str_n_C = (*env)->GetStringUTFChars(env, str_n_J, NULL);
	const char *str_e_C = (*env)->GetStringUTFChars(env, str_e_J, NULL);
	char* C_C = (char*)malloc(strlen(str_n_C) + 1);  // essendo modulo N, il cifrato C avrà massimo un numero di cifre pari a quello di N (il +1 tiene conto del fine stringa \0 non tenuto in conto da strlen)

	ENCRYPTbasedOnSTRING(C_C, M_C, str_n_C, str_e_C); 
	
   	(*env)->ReleaseStringUTFChars(env, M_J, M_C);  
   	(*env)->ReleaseStringUTFChars(env, str_n_J, str_n_C);	
   	(*env)->ReleaseStringUTFChars(env, str_e_J, str_e_C);  
	
	jstring C_J = (*env)->NewStringUTF(env, C_C);
	free(C_C);
	
	return C_J;
}


JNIEXPORT jbyteArray JNICALL Java_jniRSA_decRSA
  (JNIEnv *env , jobject obj, jstring C_J, jstring str_n_J, jstring str_e_J, jstring str_p_J, jstring str_q_J, jstring str_d_J) {
	const char *C_C = (*env)->GetStringUTFChars(env, C_J, NULL);
	const char *str_n_C = (*env)->GetStringUTFChars(env, str_n_J, NULL);
	const char *str_e_C = (*env)->GetStringUTFChars(env, str_e_J, NULL);
	const char *str_p_C = (*env)->GetStringUTFChars(env, str_p_J, NULL);
	const char *str_q_C = (*env)->GetStringUTFChars(env, str_q_J, NULL);
	const char *str_d_C = (*env)->GetStringUTFChars(env, str_d_J, NULL);
	uint8_t* M_C = (uint8_t*)malloc(KEY_SIZE);

	DECRYPTbasedOnSTRING(C_C, M_C, str_n_C, str_e_C, str_p_C, str_q_C, str_d_C);
	
	(*env)->ReleaseStringUTFChars(env, C_J, C_C);  
   	(*env)->ReleaseStringUTFChars(env, str_n_J, str_n_C);	
   	(*env)->ReleaseStringUTFChars(env, str_e_J, str_e_C); 
	(*env)->ReleaseStringUTFChars(env, str_p_J, str_p_C);
   	(*env)->ReleaseStringUTFChars(env, str_q_J, str_q_C);	
   	(*env)->ReleaseStringUTFChars(env, str_d_J, str_d_C); 

	jbyteArray arr_decrypted = (*env)->NewByteArray(env, KEY_SIZE);
        (*env)->SetByteArrayRegion(env, arr_decrypted, 0, KEY_SIZE, M_C);
        free(M_C);
	
	return arr_decrypted;
}


