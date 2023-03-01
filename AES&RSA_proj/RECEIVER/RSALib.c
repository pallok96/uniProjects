#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <gmp.h>
#include <inttypes.h>
#include <stdint.h>
#include "RSALib.h" 


void randNum(mpz_t n) { 
	static uint8_t buf[BUFFER_SIZE]; 
	
	for (int i = 0; i < BUFFER_SIZE; i++) {
			buf[i] = (uint8_t) rand(); 
	}

	buf[0] = 0xC0; 
	buf[BUFFER_SIZE-1] = 0x01; 

	mpz_import(n, BUFFER_SIZE, 1, sizeof(buf[0]), 0, 0, buf); 
} 


void RSAPrime(mpz_t p, mpz_t e) {
	randNum(p);
	
	mpz_t gcd; mpz_init(gcd);
	mpz_t phi; mpz_init(phi);
	
	do {
		mpz_nextprime(p, p);
		mpz_sub_ui(phi, p, 1);
		mpz_gcd(gcd, phi, e);
	} while (mpz_cmp_ui(gcd, 1) != 0);

	mpz_clear(gcd);
	mpz_clear(phi);		
}


void initSk(PrivateKey* Sk) {
	mpz_init(Sk->p);
	mpz_init(Sk->q);
	mpz_init(Sk->n);
	mpz_init(Sk->e);
	mpz_init(Sk->d);	
}


void initPk(PublicKey* Pk) {
	mpz_init(Pk->n);
	mpz_init(Pk->e);	
}


void genKey(PrivateKey* Sk) {
	srand(time(0));	
	
	mpz_t p; mpz_init(p);
	mpz_t q; mpz_init(q);
	mpz_t phi_p; mpz_init(phi_p);
	mpz_t phi_q; mpz_init(phi_q);
	mpz_t phi_n; mpz_init(phi_n);
	
	mpz_t e; mpz_init(e); mpz_set_ui(e, F4);
	RSAPrime(p, e);
	
	mpz_t diff; mpz_init(diff);
	mpz_t BOUND; mpz_init(BOUND); mpz_set_ui(BOUND, 2); mpz_pow_ui(BOUND, BOUND, MODULUS_SIZE/4);

	do {	
		RSAPrime(q, e);

		if (mpz_cmp(p, q) < 0) {
			mpz_sub(diff, q, p);
		} else {
			mpz_sub(diff, p, q);
		}
	} while (mpz_cmp(diff, BOUND) < 0);

	mpz_sub_ui(phi_p, p, 1);
	mpz_sub_ui(phi_q, q, 1);
	mpz_mul(phi_n, phi_p, phi_q);
	
	mpz_set(Sk->p, p);
	mpz_set(Sk->q, q);
	mpz_mul(Sk->n, p, q);
	mpz_set(Sk->e, e);
	mpz_invert(Sk->d, e, phi_n);
	
	mpz_clear(p);
	mpz_clear(q);
	mpz_clear(e);
	mpz_clear(phi_p);
	mpz_clear(phi_q);
	mpz_clear(phi_n);
	mpz_clear(diff);
	mpz_clear(BOUND);
} 


void encrypt(mpz_t C, mpz_t M, PublicKey* Pk) {
	mpz_t power; mpz_init(power); mpz_powm(power, M, Pk->e, Pk->n);
	mpz_mod(C, power, Pk->n);
	
	mpz_clear(power);
}


void decrypt(mpz_t M, mpz_t C, PrivateKey* Sk) {
	mpz_t power; mpz_init(power); mpz_powm(power, C, Sk->d, Sk->n);
	mpz_mod(M, power, Sk->n);
	
	mpz_clear(power);
}


void decryptCRT(mpz_t M, mpz_t C, PrivateKey* Sk) {
	mpz_t phi_p; mpz_init(phi_p); mpz_sub_ui(phi_p, Sk->p, 1);
	mpz_t phi_q; mpz_init(phi_q); mpz_sub_ui(phi_q, Sk->q, 1);
	
	mpz_t dp; mpz_init(dp); mpz_mod(dp, Sk->d, phi_p);
	mpz_t dq; mpz_init(dq); mpz_mod(dq, Sk->d, phi_q);
	
	mpz_t cp; mpz_init(cp); mpz_powm(cp, C, dp, Sk->p);
	mpz_t cq; mpz_init(cq); mpz_powm(cq, C, dq, Sk->q);
	
	mpz_t gcd; mpz_init(gcd);
	mpz_t u; mpz_init(u);
	mpz_t v; mpz_init(v);
	mpz_gcdext(gcd, u, v, Sk->p, Sk->q);
	
	mpz_sub(M, cq, cp);
	mpz_mul(M, M, u);
	mpz_mul(M, M, Sk->p);
	mpz_add(M, M, cp);
	mpz_mod(M, M, Sk->n);
		
	mpz_clear(dp);
	mpz_clear(dq);
	mpz_clear(cp);
	mpz_clear(cq);
	mpz_clear(phi_p);
	mpz_clear(phi_q);
	mpz_clear(gcd);
	mpz_clear(u);
	mpz_clear(v);
}


char* GENKEYasSTRING() {
	PrivateKey Sk;
	initSk(&Sk);
	genKey(&Sk);
	
	char* str_n = mpz_get_str(NULL, DEC_BASE, Sk.n);
	size_t len_str_n = strlen(str_n);
	char* str_e = mpz_get_str(NULL, DEC_BASE, Sk.e);
	size_t len_str_e = strlen(str_e);
	char* str_p = mpz_get_str(NULL, DEC_BASE, Sk.p);
	size_t len_str_p = strlen(str_p);
	char* str_q = mpz_get_str(NULL, DEC_BASE, Sk.q);
	size_t len_str_q = strlen(str_q);
	char* str_d = mpz_get_str(NULL, DEC_BASE, Sk.d);
	size_t len_str_d = strlen(str_d);
	
	size_t s = sizeof(char)*(len_str_n + 1 + len_str_e + 1 + len_str_p + 1 + len_str_q + 1 + len_str_d + 1);   // la somma dei +1 serve per tenere in conto dei simboli ':' che separano le varie informazio-
	char* strSk = (char *)malloc(s);									   // ni e del null terminator \0
	
	strcpy(strSk, str_n); strcat(strSk, ":");
	strcat(strSk, str_e); strcat(strSk, ":");  
	strcat(strSk, str_p); strcat(strSk, ":");
	strcat(strSk, str_q); strcat(strSk, ":");
	strcat(strSk, str_d); strcat(strSk, "\0");
	
	return strSk;	
}


void ENCRYPTbasedOnSTRING(char* str_C, uint8_t* arr_M, const char* str_n, const char* str_e) {
	mpz_t C; mpz_init(C);
	mpz_t M; mpz_init(M); mpz_import(M, HEX_BASE, 1, sizeof(arr_M[0]), 0, 0, arr_M);
	mpz_t n; mpz_init(n); mpz_set_str(n, str_n, DEC_BASE);
	mpz_t e; mpz_init(e); mpz_set_str(e, str_e, DEC_BASE);
	
	PublicKey Pk;
	initPk(&Pk);
	mpz_set(Pk.n, n);
	mpz_set(Pk.e, e);

	encrypt(C, M, &Pk);

	char* temp = mpz_get_str(NULL, DEC_BASE, C);
	strcpy(str_C, temp);
	
	mpz_clear(C);
	mpz_clear(M);
	mpz_clear(n);
	mpz_clear(e);
}


void DECRYPTbasedOnSTRING(const char* str_C, uint8_t* arr_M, const char* str_n, const char* str_e, const char* str_p, const char* str_q, const char* str_d) {
	mpz_t M; mpz_init(M); 
	mpz_t C; mpz_init(C); mpz_set_str(C, str_C, DEC_BASE);
	mpz_t n; mpz_init(n); mpz_set_str(n, str_n, DEC_BASE);
	mpz_t e; mpz_init(e); mpz_set_str(e, str_e, DEC_BASE);
	mpz_t p; mpz_init(p); mpz_set_str(p, str_p, DEC_BASE);
	mpz_t q; mpz_init(q); mpz_set_str(q, str_q, DEC_BASE);
	mpz_t d; mpz_init(d); mpz_set_str(d, str_d, DEC_BASE);
	
	PrivateKey Sk;
	initSk(&Sk);
	mpz_set(Sk.n, n);
	mpz_set(Sk.e, e);
	mpz_set(Sk.p, p);
	mpz_set(Sk.q, q);
	mpz_set(Sk.d, d);

	decryptCRT(M, C, &Sk);
	
	mpz_export(arr_M, NULL, 1, sizeof(uint8_t), 0, 0, M);
	
	mpz_clear(M);
	mpz_clear(C);
	mpz_clear(n);
	mpz_clear(e);
	mpz_clear(p);
	mpz_clear(q);
	mpz_clear(d);
}


