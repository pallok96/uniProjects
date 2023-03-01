#define BYTES_IN_WORD   4
#define WORDS_IN_KEY    4
#define NR_ROUNDS      10
#define BLOCK_SIZE     16


uint8_t xtime(uint8_t x);
uint8_t multiplyF_256(uint8_t a, uint8_t b);
void addRoundKey(uint8_t r, uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD], uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD]);
void subBytes(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]);
void invSubBytes(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]);
void mixColumns(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]);
void invMixColumns(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]);
void shiftRows(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]);
void invShiftRows(uint8_t state[WORDS_IN_KEY][BYTES_IN_WORD]);
void rotWord(uint8_t word[], int i);
void subWord(uint8_t word[]);
void invSubWord(uint8_t word[]);
void rcon(uint8_t word[], int j);
void roundKeyGen(uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD], uint8_t Key[WORDS_IN_KEY][BYTES_IN_WORD]);
void encryptAES(uint8_t buf[], uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD]);
void decryptAES(uint8_t buf[], uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD]);
void CBC(uint8_t buf[BLOCK_SIZE], uint8_t vec[BLOCK_SIZE]);
void encryptCBC(uint8_t buf[], uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD], uint8_t iv[BLOCK_SIZE], long size);
void decryptCBC(uint8_t buf[], uint8_t roundKey[NR_ROUNDS + 1][WORDS_IN_KEY][BYTES_IN_WORD], uint8_t iv[BLOCK_SIZE], long size);
uint8_t* padding(uint8_t buf[], size_t size);
uint8_t* depadding(uint8_t BUF[], size_t size);
void ENCRYPT(uint8_t plainText[], long PTsize, uint8_t Key[WORDS_IN_KEY*BYTES_IN_WORD], uint8_t iv[BLOCK_SIZE], uint8_t cipherText[], long CTsize);
void DECRYPT(uint8_t cipherText[], long CTsize, uint8_t Key[WORDS_IN_KEY*BYTES_IN_WORD], uint8_t iv[BLOCK_SIZE], uint8_t plainText[], long PTsize);
uint8_t findRes(uint8_t cipherText[], uint8_t Key[WORDS_IN_KEY*BYTES_IN_WORD], uint8_t iv[BLOCK_SIZE]);
