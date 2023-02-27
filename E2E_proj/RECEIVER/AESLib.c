#include <stdlib.h>
#include <stdio.h>
#include <inttypes.h>
#include <math.h>
#include "AESLib.h"


uint8_t SBox[256] = { 0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
  		      0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
  		      0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
 	              0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
  		      0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
 		      0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
  	              0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
  		      0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
  		      0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
  		      0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
  		      0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
  		      0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
  		      0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
  		      0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
  		      0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
  		      0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16 };


uint8_t InvSBox[256] = { 0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb,
 			 0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb,
 			 0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e,
			 0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25,
  			 0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92,
 			 0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84,
 			 0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06,
 			 0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b,
 			 0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73,
 			 0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e,
 			 0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b,
 			 0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4,
 			 0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f,
 			 0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef,
 			 0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61,
 			 0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d };


uint8_t RCon[] = {0x00, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36};


uint8_t xtime(uint8_t x) {
	uint8_t y = (x << 1);
	
	if (x >> 7) {
		y ^= (uint8_t)27;	
	}
	
	return y;
}


uint8_t multiplyF_256(uint8_t a, uint8_t b) {
	uint8_t p = 0;
	int i = 0;
	
	while ((a != 0) && (b != 0) && (i < 8)) {
		if (b & (uint8_t)1) {
			p ^= a;
		}
		
		b >>= 1;
		a = xtime(a);
		i++;
	}
	
	return p;
}


void addRoundKey(uint8_t r, uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD], uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD]) {        
	for (int i = 0; i < WORDS_IN_KEY; i++) {
		for (int j = 0; j < BYTES_IN_WORD; j++) {
			state[i][j] = state[i][j] ^ roundKey[r][i][j];
		}
	}
}


void subBytes(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]) {                
	for (int i = 0; i < WORDS_IN_KEY; i++) {
		subWord(state[i]);
	}
}


void invSubBytes(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]) {                
	for (int i = 0; i < WORDS_IN_KEY; i++) {
		invSubWord(state[i]);
	}
}


void mixColumns(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]) {
	for (int i = 0; i < WORDS_IN_KEY; i++) {	
		uint8_t temp[BYTES_IN_WORD];
		
		for (int j = 0; j < BYTES_IN_WORD; j++) {
			temp[j] = state[i][j];
		}
	
		state[i][0] = multiplyF_256((uint8_t)2, temp[0]) ^ multiplyF_256((uint8_t)3, temp[1]) ^ temp[2] ^ temp[3];
		state[i][1] = temp[0] ^ multiplyF_256((uint8_t)2, temp[1]) ^ multiplyF_256((uint8_t)3, temp[2]) ^ temp[3];
		state[i][2] = temp[0] ^ temp[1] ^ multiplyF_256((uint8_t)2, temp[2]) ^ multiplyF_256((uint8_t)3, temp[3]);
		state[i][3] = multiplyF_256((uint8_t)3, temp[0]) ^ temp[1] ^ temp[2] ^ multiplyF_256((uint8_t)2, temp[3]);
	}
}


void invMixColumns(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]) {
	for (int i = 0; i < WORDS_IN_KEY; i++) {	
		uint8_t temp[BYTES_IN_WORD];
		
		for (int j = 0; j < BYTES_IN_WORD; j++) {
			temp[j] = state[i][j];
		}
	
		state[i][0] = multiplyF_256((uint8_t)14, temp[0]) ^ multiplyF_256((uint8_t)11, temp[1]) ^ multiplyF_256((uint8_t)13, temp[2]) ^ multiplyF_256((uint8_t) 9, temp[3]);
		state[i][1] = multiplyF_256((uint8_t) 9, temp[0]) ^ multiplyF_256((uint8_t)14, temp[1]) ^ multiplyF_256((uint8_t)11, temp[2]) ^ multiplyF_256((uint8_t)13, temp[3]);
		state[i][2] = multiplyF_256((uint8_t)13, temp[0]) ^ multiplyF_256((uint8_t) 9, temp[1]) ^ multiplyF_256((uint8_t)14, temp[2]) ^ multiplyF_256((uint8_t)11, temp[3]);
		state[i][3] = multiplyF_256((uint8_t)11, temp[0]) ^ multiplyF_256((uint8_t)13, temp[1]) ^ multiplyF_256((uint8_t) 9, temp[2]) ^ multiplyF_256((uint8_t)14, temp[3]);
	}
}


void rotWord(uint8_t word[], int i) {
	uint8_t temp[BYTES_IN_WORD];
	
	for (int k = 0; k < BYTES_IN_WORD; k++) {
		temp[k] = word[k];
	}
	
	for (int j = 0; j < BYTES_IN_WORD; j++) {
		word[j] = temp[(j + i) % BYTES_IN_WORD];
	}
}


void shiftRows(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]) {                     
	for (int i = 0; i < WORDS_IN_KEY; i++) {
		uint8_t temp[BYTES_IN_WORD];
	
		for (int j = 0; j < WORDS_IN_KEY; j++) {
			temp[j] = state[j][i];
		}
			
		rotWord(temp, i);
		
		for (int j = 0; j < WORDS_IN_KEY; j++) {
			state[j][i] = temp[j];
		}
	}
}


void invShiftRows(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]) {                     
	for (int i = 0; i < WORDS_IN_KEY; i++) {
		uint8_t temp[BYTES_IN_WORD];
	
		for (int j = 0; j < WORDS_IN_KEY; j++) {
			temp[j] = state[j][i];
		}
			
		rotWord(temp, BYTES_IN_WORD - i);  

		for (int j = 0; j < WORDS_IN_KEY; j++) {
			state[j][i] = temp[j];
		}
	}
}


void subWord(uint8_t word[]) {
	for (int i = 0; i < BYTES_IN_WORD; i++) {
		word[i] = SBox[word[i]];
	}
}


void invSubWord(uint8_t word[]) {
	for (int i = 0; i < BYTES_IN_WORD; i++) {
		word[i] = InvSBox[word[i]];
	}
}


void rcon(uint8_t word[], int j) {
	word[0] = word[0] ^ RCon[j];
}


void roundKeyGen(uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD], uint8_t Key[WORDS_IN_KEY][BYTES_IN_WORD]) {
	for (int i = 0; i < WORDS_IN_KEY; i++) {
		for (int j = 0; j < WORDS_IN_KEY; j++) {
			roundKey[0][i][j] = Key[i][j];
		}
	}
	
	for (int i = 1; i < NR_ROUNDS + 1; i++) {
		uint8_t square[WORDS_IN_KEY][BYTES_IN_WORD];

		for (int p = 0; p < WORDS_IN_KEY; p++) {
			for (int q = 0; q < WORDS_IN_KEY; q++) {
				square[p][q] = roundKey[i - 1][p][q];
			}
		}
		
		uint8_t temp[BYTES_IN_WORD];

		for (int j = 0; j < BYTES_IN_WORD; j++) {
			temp[j] = square[3][j];
		}
		
		rotWord(temp, 1);
		subWord(temp);
		rcon(temp, i);
		
		for (int j = 0; j < BYTES_IN_WORD; j++) {
			square[0][j] = square[0][j] ^ temp[j];
		}
		
		for (int k = 0; k < BYTES_IN_WORD; k++) {
			square[1][k] = square[1][k] ^ square[0][k];
		}

		for (int l = 0; l < BYTES_IN_WORD; l++) {
			square[2][l] = square[2][l] ^ square[1][l];
		}

		for (int n = 0; n < BYTES_IN_WORD; n++) {
			square[3][n] = square[3][n] ^ square[2][n];
		}
		
		for (int g = 0; g < WORDS_IN_KEY; g++) {
			for (int h = 0; h < WORDS_IN_KEY; h++) {
				roundKey[i][g][h] = square[g][h];
			}
		}
	}
}


void encryptAES(uint8_t buf[], uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD]) {
	uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD];
	
	for (int i = 0; i < BLOCK_SIZE; i++) {
		state[i/WORDS_IN_KEY][i % BYTES_IN_WORD] = buf[i];
	}
	
	addRoundKey(0, state, roundKey);
	
	for (int r = 1; r < NR_ROUNDS; r++) {
		subBytes(state);
		shiftRows(state);
		mixColumns(state);
		addRoundKey(r, state, roundKey);
	}
	
	subBytes(state);
	shiftRows(state);
	addRoundKey(NR_ROUNDS, state, roundKey);
	
	for (int i = 0; i < BLOCK_SIZE; i++) {
		buf[i] = state[i/WORDS_IN_KEY][i % BYTES_IN_WORD];
	}		
}


void decryptAES(uint8_t buf[], uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD]) {
	uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD];
	
	for (int i = 0; i < BLOCK_SIZE; i++) {
		state[i/WORDS_IN_KEY][i % BYTES_IN_WORD] = buf[i];
	}
	
	addRoundKey(NR_ROUNDS, state, roundKey);	
	
	for (int r = NR_ROUNDS - 1; r > 0; r--) {
		invShiftRows(state);		
		invSubBytes(state);
		addRoundKey(r, state, roundKey);
		invMixColumns(state);
	}
	
	invShiftRows(state);	
	invSubBytes(state);
	addRoundKey(0, state, roundKey);
	
	for (int i = 0; i < BLOCK_SIZE; i++) {
		buf[i] = state[i/WORDS_IN_KEY][i % BYTES_IN_WORD];
	}		
}


void CBC(uint8_t buf[BLOCK_SIZE], uint8_t vec[BLOCK_SIZE]) {
	for (int i = 0; i < BLOCK_SIZE; i++) {
		buf[i] ^= vec[i];	
	}
}


uint8_t* padding(uint8_t buf[], size_t size) {			// serve per far sì che l'input sia "schierato" a blocchi di 16 in modo tale da poterlo dare in pasto a encryptCBC: nel caso l'input non abbia 
	uint8_t res = size % BLOCK_SIZE;			// lunghezza  ultipla di 16, vengono aggiunti zeri fino a raggiungere il "sedicetto"
	int quo = size/BLOCK_SIZE;				
	size_t s = sizeof(uint8_t)*(BLOCK_SIZE*(quo + 1));
	uint8_t* BUF = (uint8_t *)malloc(s);
	
	BUF[0] = res;

	for (int i = 1; i <= size; i++) {
		BUF[i] = buf[i - 1];
	} 
		
	for (int i = 1; i <= BLOCK_SIZE - res - 1; i++) {
		BUF[size + i] = 0;
	}
	
	return BUF;
}


uint8_t* depadding(uint8_t BUF[], size_t size) {
	size_t s = sizeof(uint8_t)*((size - 1) - (BLOCK_SIZE - BUF[0] - 1));
	uint8_t* buf = (uint8_t *)malloc(s);
	
	for (int i = 1; i <= s; i++) {
		buf[i - 1] = BUF[i];
	} 
	
	return buf;
}


uint8_t findRes(uint8_t cipherText[], uint8_t Key[WORDS_IN_KEY*BYTES_IN_WORD], uint8_t iv[BLOCK_SIZE]) {
	uint8_t reconstructed_key[WORDS_IN_KEY][BYTES_IN_WORD];			// per comodità di interazione con le JNI, la firma di questo metodo così come quella di ENCRYPT e DECRYPT prende in input
	uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD];		// la chiave sotto forma di array di 16 byte piuttosto che sotto forma di una matrice 4x4. Essendo che però tutte le altre
										// funzioni di libreria maneggiano la chiave in quest'ultima forma, è necessario "ricostruirla" (riassemlarla) in tale 
	for (int i = 0; i < WORDS_IN_KEY; i++) {				// forma prima di invocare tali metodi
		for (int j = 0; j < BYTES_IN_WORD; j++) {
			reconstructed_key[i][j] = Key[WORDS_IN_KEY*i + j];
		}
	}

	roundKeyGen(roundKey, reconstructed_key);
	
	uint8_t res;
	uint8_t stateVect[BLOCK_SIZE];	
		
	for (int j = 0; j < BLOCK_SIZE; j++) {
		stateVect[j] = cipherText[j];		
	}

	decryptAES(stateVect, roundKey);
	CBC(stateVect, iv);
	res = stateVect[0];	
	
	return res;
}


void encryptCBC(uint8_t buf[], uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD], uint8_t iv[BLOCK_SIZE], long size) {
	uint8_t xoring[BLOCK_SIZE];

	for (int i = 0; i < BLOCK_SIZE; i++) {
		xoring[i] = iv[i];
	}
	
	uint8_t stateVect[BLOCK_SIZE];

	for (int i = 0; i < size/BLOCK_SIZE; i++) {     
		for (int j = 0; j < BLOCK_SIZE; j++) {
			stateVect[j] = buf[j + BLOCK_SIZE*i];
		}
		
		CBC(stateVect, xoring);
		encryptAES(stateVect, roundKey);
		
		for (int i = 0; i < BLOCK_SIZE; i++) {
			xoring[i] = stateVect[i];
		}
		
		for (int j = 0; j < BLOCK_SIZE; j++) {
			buf[j + BLOCK_SIZE*i] = stateVect[j];
		}		
	}	
}
	

void decryptCBC(uint8_t buf[], uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD], uint8_t iv[BLOCK_SIZE], long size) {
	uint8_t xoring1[BLOCK_SIZE];
	uint8_t xoring2[BLOCK_SIZE];
	uint8_t stateVect[BLOCK_SIZE];	
	
	for (int j = 0; j < BLOCK_SIZE; j++) {
		xoring1[j] = iv[j];
		xoring2[j] = buf[j];  
	}

	for (int i = 0; i < size/BLOCK_SIZE - 1; i++) { 			       	
		for (int j = 0; j < BLOCK_SIZE; j++) {
			stateVect[j] = buf[j + BLOCK_SIZE*i];		
		}

		decryptAES(stateVect, roundKey);
		CBC(stateVect, xoring1);	
	
		for (int j = 0; j < BLOCK_SIZE; j++) {
			buf[j + BLOCK_SIZE*i] = stateVect[j];
		}	

		for (int j = 0; j < BLOCK_SIZE; j++) {
			xoring1[j] = xoring2[j];
			xoring2[j] = buf[j + BLOCK_SIZE*(i + 1)];
		}	
	}

	for (int j = 0; j < BLOCK_SIZE; j++) {
		stateVect[j] = buf[j + BLOCK_SIZE*(size/BLOCK_SIZE - 1)];		
	}

	decryptAES(stateVect, roundKey);
	CBC(stateVect, xoring1);	
	
	for (int j = 0; j < BLOCK_SIZE; j++) {
		buf[j + BLOCK_SIZE*(size/BLOCK_SIZE - 1)] = stateVect[j]; 
	}		
}


void ENCRYPT (uint8_t plainText[], long PTsize, uint8_t Key[WORDS_IN_KEY*BYTES_IN_WORD], uint8_t iv[BLOCK_SIZE], uint8_t cipherText[], long CTsize) {
	uint8_t reconstructed_key[WORDS_IN_KEY][BYTES_IN_WORD];
	uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD];
	
	for (int i = 0; i < WORDS_IN_KEY; i++) {
		for (int j = 0; j < BYTES_IN_WORD; j++) {
			reconstructed_key[i][j] = Key[WORDS_IN_KEY*i + j];
		}
	}
	
	roundKeyGen(roundKey, reconstructed_key); 	
	
	uint8_t* padded = padding(plainText, PTsize); 
	
	for (int i = 0; i < CTsize; i++) {
		cipherText[i] = *(padded + i);	
	}
	
	free(padded);		// la funzione padding alloca memoria dinamicamente con malloc(), quindi è necessario liberarla appena il puntatore a tale zona non è più utile
	encryptCBC(cipherText, roundKey, iv, CTsize);
}


void DECRYPT (uint8_t cipherText[], long CTsize, uint8_t Key[WORDS_IN_KEY*BYTES_IN_WORD], uint8_t iv[BLOCK_SIZE], uint8_t plainText[], long PTsize) {
	uint8_t reconstructed_key[WORDS_IN_KEY][BYTES_IN_WORD];
	uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD];
	
	for (int i = 0; i < WORDS_IN_KEY; i++) {
		for (int j = 0; j < BYTES_IN_WORD; j++) {
			reconstructed_key[i][j] = Key[4*i + j];
		}
	}

	roundKeyGen(roundKey, reconstructed_key);
	decryptCBC(cipherText, roundKey, iv, CTsize);
	
	uint8_t* depadded = depadding(cipherText, CTsize);

	for(int i = 0; i < PTsize; i++) {
		plainText[i] = *(depadded + i);
	}
	
	free(depadded);		// la funzione depadding alloca memoria dinamicamente con malloc(), quindi è necessario liberarla appena il puntatore a tale zona non è più utile
}


