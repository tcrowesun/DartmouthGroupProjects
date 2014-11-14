
#include <stdlib.h>
#include <stdio.h>


int * read_seeds(FILE *fp, int how_many_ints);

int main(int argc, char *argv[]) {

    if(argc != 5) {
        fprintf(stderr, "%s requires exactly 4 arguments: a file to read in random numbers to, and the number of ints you have in the file, a file to write the extended bits to, and the number of random bytes you want written to your file.", argv[0]);
        return EXIT_FAILURE;
    }
    char * infile = argv[1];
    int how_many_ints = atoi(argv[2]);
    char * outfile = argv[3];
    int how_many_bytes = atoi(argv[4]);

    int bytes_per_seed = how_many_bytes/how_many_ints;

    FILE * fp = fopen(infile, "r");
    int * list =  read_seeds(fp, how_many_ints);
    fp = fopen(outfile, "w");
    unsigned char bits;

    for(int i = 0; i<how_many_ints; i++) {

        srand(list[i]);
        for(int j = 0; j < bytes_per_seed; j++) {
            
            bits = rand();
            fwrite(&bits, 1, 1, fp);
        
        }
    }

}


int * read_seeds(FILE *fp, int how_many_ints){

    int * list = (int * ) malloc(how_many_ints * sizeof(int));
    
    if(fp != NULL){
        int index = 0;
        char line[100];

        while(fgets(line, sizeof(line), fp) != NULL){

            list[index] = atoi(line);
            index++;
        }
    }
    else{
        fprintf(stdout, "Error reading numbers\n");
    }
    return list;
}
