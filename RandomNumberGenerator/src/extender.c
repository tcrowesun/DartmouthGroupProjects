
#include <stdlib.h>
#include <stdio.h>


int * read_seeds(FILE *fp, int how_many);

int main(int argc, char *argv[]) {

    if(argc != 4) {
        fprintf(stderr, "%s requires exactly 2 arguments: a file to read in random numbers to, and the number of ints you have in the file, and a file to write the extended bits to.", argv[0]);
        return EXIT_FAILURE;
    }
    char * infile = argv[1];
    int how_many = atoi(argv[2]);
    char * outfile = argv[3];

    FILE * fp = fopen(infile, "r");
    int * list =  read_seeds(fp, how_many);

    for(int i = 0; i<10; i++) {

        printf("%d\n",list[i]);
    }

}



int * read_seeds(FILE *fp, int how_many){

    int * list = (int * ) malloc(how_many * sizeof(int));
    
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
