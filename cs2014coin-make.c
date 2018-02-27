/*!
 * @file cs2014coin-make.c
 * @brief This is the implementation of the cs2014 coin maker
 *
 * It should go without saying that these coins are for play:-)
 * 
 * This is part of CS2014
 *    https://down.dsg.cs.tcd.ie/cs2014/examples/c-progs-2/README.html
 */

/* 
 * Copyright (c) 2017 stephen.farrell@cs.tcd.ie
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

#include "mbedtls/config.h"
#include "mbedtls/platform.h"
#include "mbedtls/error.h"
#include "mbedtls/pk.h"
#include "mbedtls/ecdsa.h"
#include "mbedtls/rsa.h"
#include "mbedtls/error.h"
#include "mbedtls/entropy.h"
#include "mbedtls/ctr_drbg.h"

#include <stdbool.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "cs2014coin.h"
#include "cs2014coin-int.h"
#include "mbedtls/sha256.h"
mbedtls_md_context_t sha_ctx;
mbedtls_sha256_context ctx;

#define DFL_EC_CURVE            mbedtls_ecp_curve_list()->grp_id
#define DFL_TYPE                MBEDTLS_PK_RSA
unsigned char output_buf[16000];
unsigned char *c = output_buf;

int write_public( mbedtls_pk_context *key, const char *output_file);

FILE *f;

/*!
 * @brief make a coin
 * @param bits specifies how many bits need to be zero in the hash-output
 * @param buf is an allocated buffer for the coid
 * @param buflen is an in/out parameter reflecting the buffer-size/actual-coin-size 
 * @return the random byte
 *
 * Make me a coin of the required quality/strength
 *
 */
int cs2014coin_make(int bits, unsigned char *buf, int *buflen)
{
int rv = 0;
char cipherSuite [4]= {0x00,0x00,0x00,0x00};
char  bit [4] =  {0x00,0x00,0x00,bits};
char keyLength [4] = {0x00,0x00,0x00,0x9e};
char nonceLength [4] = {0x00,0x00,0x00,0x20};
int ec_curve = DFL_EC_CURVE;
mbedtls_ecp_point P;
mbedtls_ecp_point_init (&P);
mbedtls_mpi d;
mbedtls_mpi_init (&d);


mbedtls_pk_context key;
mbedtls_ctr_drbg_context ctr_drbg;
mbedtls_entropy_context entropy;
 mbedtls_pk_context pk;

mbedtls_pk_init(&key);
mbedtls_entropy_init(&entropy);
mbedtls_ctr_drbg_init( &ctr_drbg );

const unsigned char *pers = "gen_key";
 
 if((rv = mbedtls_pk_setup(&key,mbedtls_pk_info_from_type(MBEDTLS_PK_ECKEY)))== 0){

    if((rv = mbedtls_ctr_drbg_seed(&ctr_drbg,mbedtls_entropy_func,&entropy,pers,strlen(pers))) == 0){

        if((rv = mbedtls_ecp_gen_key(ec_curve,mbedtls_pk_ec(key),mbedtls_ctr_drbg_random, &ctr_drbg))== 0){

            if((rv = write_public(&key, "dump")) == 0){
            }
        }       
    }
}
char nonce [32];
srand(0);

unsigned char sha256 [242];
unsigned char sha256out [32];
unsigned char shaLen[4] = {0x00,0x00,0x00,0x20};



const unsigned char * in = sha256;
unsigned char hash[32];

bool isHit = false;
while(isHit == false){

    for (int i = 0; i < 32;i++){
    nonce[i] = (char) rand();
    }

    int i = 0;
    int j = 0;

    for(; i < 4; i++){
        sha256[i] = cipherSuite[i];
    }
    for(j = 0; j < 4; j++){
      sha256[i] = bit[j];
        i++;
    }
    for(j = 0; j < 4;j++){
        sha256[i] = keyLength[j];
        i++;
    }
    for(j = 0; j < 158; j++){
        sha256[i] = c[j];
        i++;
    }
    for(j = 0; j < 4;j++){
        sha256[i] = nonceLength[j];
        i++;
    }
    for(j = 0; j < 32;j++){
         sha256[i] = nonce[j];
        i++;
    }
    for(j = 0; j < 4;j++){
        sha256[i] = shaLen[j];
        i++;
    }
    for(j = 0; j < 32;j++){
        hash[j] = 0x00;
        sha256[i] = hash[j];
        i++;
}

    mbedtls_md_context_t sha_ctx;
    mbedtls_md_init( &sha_ctx );
    rv = mbedtls_md_setup( &sha_ctx, mbedtls_md_info_from_type( MBEDTLS_MD_SHA256 ), 1 );
    mbedtls_md_starts( &sha_ctx );
    mbedtls_md_update( &sha_ctx, (unsigned char *) sha256, 242 );
    mbedtls_md_finish( &sha_ctx, sha256out );
    

    if (rv = zero_bits(bits,sha256out,32)!=0){
         break;
         printf("\nhit me now\n");
        isHit = true;
    }
    else{

        for (int i = 174; i < 206;i++ ){
            sha256[i] = (char) rand();
        }     
    }
}

unsigned char sig[242];

 int k = 0;
 int l = 0;
for(; k < 4; k++){
    sig[k] = cipherSuite[k];
}

for(l= 0; l < 4; l++){
    sig[k] = bit[l];
    k++;
}
for(l = 0; l < 4;l++){
    sig[k] = keyLength[l];
    k++;
}
for(l = 0; l < 158; l++){
    sig[k] = c[l];
    k++;
}
for(l = 0; l < 4;l++){
    sig[k] = nonceLength[l];
    k++;
}
for(l= 0; l < 32;l++){
    sig[k] = nonce[l];
    k++;
}
for(l = 0; l < 4;l++){
    sig[k] = shaLen[l];
    k++;
}
for(l = 0; l< 32;l++){
    sig[k] = sha256out[l];
    k++;
}

 size_t olen = 0;

mbedtls_entropy_init( &entropy );

 unsigned char hash2[32];

 unsigned char buff [MBEDTLS_MPI_MAX_SIZE];

 mbedtls_md_context_t sha_ctx;
 mbedtls_md_init(&sha_ctx);
 mbedtls_md_setup(&sha_ctx,mbedtls_md_info_from_type( MBEDTLS_MD_SHA256 ),1);



    mbedtls_md_starts( &sha_ctx );
    mbedtls_md_update( &sha_ctx, (unsigned char *) sig, sizeof(sig));
    mbedtls_md_finish( &sha_ctx, hash2);

     if( ( rv = mbedtls_pk_sign( &key, MBEDTLS_MD_SHA256, hash2, 0, buff, &olen,mbedtls_ctr_drbg_random, &ctr_drbg ) ) != 0 )
    {
    }

    unsigned char out[olen];
    for(int i = 0; i < olen; i++){
        out[i] = buff[i];
    }
    char x = (char)olen;
    int q = (int)olen;
    char olenLen [4] = {0x00,0x00,0x00,x};

   for(int i = 0; i < *buflen;i++){
        buf[i] = 0;
   }
   int xl = q + 246; 
   *buflen = xl;
   char temp [xl];
  int i = 0;
  int j = 0;

for(; i < 242;i++){
    buf[i] = sig[i];
}
for(j = 0; j < 4;j++){
    buf[i] = olenLen[j];
    i++;
}
for(j = 0; j < q;j++){
    buf[i] = out[j];
    i++;
}

	printf("I'm a stub!\n");
	return(0);
}


int write_public( mbedtls_pk_context *key, const char *output_file)
{
     int ret;
    size_t len = 0;

    memset(output_buf, 0, 16000);

        if( ( ret = mbedtls_pk_write_pubkey_der( key, output_buf, 16000 ) ) < 0 ){
             printf("broke here11");
            return( ret );
        }
        len = ret;
        c = output_buf + sizeof(output_buf) - len;
    
    return( 0 );
}
