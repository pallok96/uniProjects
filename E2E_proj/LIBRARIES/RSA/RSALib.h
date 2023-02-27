/**********************************************************************
 *                            RSA using GMP                           *
 **********************************************************************/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <gmp.h>

#define F4 65537	                 // 4th Fermat's number (2^2^4 + 1). Minimum requested by NIST
#define MODULUS_SIZE 1024                //Modulus Size
#define BUFFER_SIZE (MODULUS_SIZE/16)    //Prime Size;
#define BLOCK_SIZE (MODULUS_SIZE/8)      //Size of a block that gets en/decrypted at once
#define HEX_BASE 16 
#define DEC_BASE 10


struct publicKey {
	mpz_t n;  		/* Modulus */
	mpz_t e;  		/* Public Exponent */
};

typedef struct publicKey PublicKey;

struct privateKey {
	mpz_t n;  		// Modulus 
	mpz_t e;  		// Public Exponent 
	mpz_t d;  		// Private Exponent 
	mpz_t p;  		// prime p 
	mpz_t q;  		// prime q 
};

typedef struct privateKey PrivateKey;

void randNum(mpz_t n);
void RSAPrime(mpz_t p, mpz_t e);
void initSk(PrivateKey* Sk);
void initPk(PublicKey* Pk);
void genKey(PrivateKey* Sk); 
void encrypt(mpz_t C, mpz_t M, PublicKey* Pk);
void decrypt(mpz_t M, mpz_t C, PrivateKey* Sk);
void decryptCRT(mpz_t M, mpz_t C, PrivateKey* Sk);
char* GENKEYasSTRING();
void ENCRYPTbasedOnSTRING(char* str_C, uint8_t* arr_M, const char* str_n, const char* str_e);
void DECRYPTbasedOnSTRING(const char* str_C, uint8_t* arr_M, const char* str_n, const char* str_e, const char* str_p, const char* str_q, const char* str_d);
