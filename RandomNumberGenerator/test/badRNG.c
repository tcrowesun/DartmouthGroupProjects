
#include <stdlib.h>
#include <stdio.h>


int * read_seeds(FILE *fp, int how_many_ints);
int randbad();
void srandbad(unsigned int seed);

static unsigned long int next = 1;

int main(int argc, char *argv[]) {
    if(argc != 5) {
        fprintf(stderr, "%s requires exactly 2 arguments: a file to write out random bytes and the number of random bytes you want written to your file\n", argv[0]);
        return EXIT_FAILURE;
    }
    char * outfile = argv[1];
    int how_many_bytes = atoi(argv[2]);


    FILE *fp = fopen(outfile, "w");
    unsigned char bits;


    srandbad(1);
    for(int j = 0; j < how_many_bytes; j++) {
            
            bits = randbad();
            fwrite(&bits, 1, 1, fp);
    }

}

int randbad(void) // RAND_MAX assumed to be 32767
{
    next = next * 1103515245 + 12345;
    return (unsigned int)(next/65536) % 32768;
}

void srandbad(unsigned int seed)
{
    next = seed;
}
