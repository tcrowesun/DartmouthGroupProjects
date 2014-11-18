// compile with gcc -std=c11 -Wall -pedantic -lcurl -o weather weather.c mycurl.c

// ---------------- System includes:

#include <stdlib.h>     // for exit
#include <stdio.h>      // for fprintf
#include <curl/curl.h>  // for curl
#include <string.h>     // for string manipulation functions to get output
#include <time.h>       // for seeding rand

// ---------------- Local includes:
#include "mycurl.h"     // to use ccp's curl functions

// ---------------- Private prototypes
int check_code_found(char * response, CURL *curl);           // checks if the station ID was correct
void get_output(char * response, char * tag, char * data);   // prints out a single piece of data
void print_output(char * response);                          // calls get output for all the data
unsigned int write_bits(char * response);
void get_next_url(char* APIurl, char *stationIndex[], int randomStationIndex);
void get_station_list(FILE *fp, char *stationList[]);


// ---------------- Constants
#define URLSIZE 100
#define NUMSTATIONS 1934
#define ERROR 666

int main(int argc, char *argv[]) {

    /* check args and initialize everything */
    if(argc != 3) {
        fprintf(stderr, "%s requires exactly 2 arguments: a file to write random numbers to, and the number of ints you want\n", argv[0]);
        return EXIT_FAILURE;
    }

    char *outfile = argv[1];

    FILE * fp = fopen(outfile, "w");

    if(fp == NULL) {
        fprintf(stderr, "Cannot open file %s", outfile);
        return EXIT_FAILURE;
    }
    
    FILE *index = fopen("files/index.txt", "r");
    
    if(index == NULL) {
	fclose(fp);
        fprintf(stderr, "Cannot open station index");
        return EXIT_FAILURE;
    } 

    char *stationIndex[NUMSTATIONS];
    get_station_list(index, stationIndex);

 
    int numInts = atoi(argv[2]);

    char *APIurl = (char *)  malloc(URLSIZE * sizeof(char));

    if (!APIurl) {
        fclose(fp);
        fclose(index);
        fprintf(stderr, "Malloc error, could not allocate memory\n");
        return EXIT_FAILURE;
    } 
    
    srand(time(NULL));


    /* sets up variables for the curl */
    CURL *curl;
    CURLcode res;
    struct curlResponse s;


    /* allocate and intiialize the output area */
    init_curlResponse(&s);
    curl = curl_easy_init();

    if(curl) {

        int intsWritten = 0;        


        /* set the function curl should call with the result */
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writefunc);

        /* set the buffer into which curl should place the data */
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &s);

        /* to store random numbers */
        unsigned int bits;
        unsigned int accumulator = 0x0;
        int step = 0;
        

        while (intsWritten < numInts) {

	    int randomStationIndex = rand() % NUMSTATIONS;
	    
            /* if index out of bounds, cut out and exit */
	    if(randomStationIndex > NUMSTATIONS || randomStationIndex < 0){
		fprintf(stderr, "Random station index (%d) out of bounds\n", randomStationIndex);
                fclose(fp);
                fclose(index);
                free(APIurl);
                curl_easy_cleanup(curl);
		return EXIT_FAILURE;
	    }


            get_next_url(APIurl, stationIndex, randomStationIndex);


            /* set url we want to visit */
            curl_easy_setopt(curl, CURLOPT_URL, APIurl);


            /* perform the request, res will get the return code */
            res = curl_easy_perform(curl);
            
            /* if curl didn't work, cut out and exit */
            if(res != CURLE_OK) {
                fprintf(stderr, "Unsuccessful curl\n");
                fclose(index);
                fclose(index);
                free(APIurl);
                curl_easy_cleanup(curl);
                return EXIT_FAILURE;
            } 

            /* make sure the station exists */
            if(check_code_found(s.ptr, curl)) {
                init_curlResponse(&s);
                continue;
            }

            /* get the random bits from the curl reply */
            bits = write_bits(s.ptr);

            /* if any bits aren't found, we get back an 8
             * here we just want to skip the number 
             */
            if(bits == ERROR) {
                init_curlResponse(&s);
                continue;
            }

            /* shift to the appropriate slot */
            bits = bits << 4 * step;

            /* add the bits in, move slot over */
            accumulator = accumulator | bits;
            step++;

            /* if we have a full number, then write it! */
            if(step == sizeof(int)*2) {
                
                fprintf(fp, "%u\n", accumulator);


                step = 0;
                accumulator = 0;
                intsWritten ++;
            }
            

            /* allocate and intialize the output area */
            init_curlResponse(&s);

        }
        fclose(fp);
        fclose(index);
        free(APIurl);
        curl_easy_cleanup(curl);

    }
    else {
        fprintf(stderr, "Could not curl, likely no internet\n");
        fclose(fp);
        fclose(index);
        free(APIurl);
        return EXIT_FAILURE;
    }
    

}


/*
 * check_code_found - did the user pass a valid station code?
 *   
 * @response - the downloaded string curled from the web
 * @curl - the curl handle, in case we need to clean up
 *      
 * This function checks the returned string to see if
 * the station ID was valid. If the string doesn't begin
 * with "<?xml", it returns 0. Otherwise, it returns 1.
 */
int check_code_found(char *response, CURL *curl) {

    char first_five[6];

    snprintf(first_five, 6, response);

    int comparison = strcmp(first_five, "<?xml");
    return (comparison != 0);
}

/*
 * get_next_url - create a new random url!
 *
 * Note that this is NOT the source of randomness we
 * are using. We are simply using this to generate station
 * codes.
 *
 * This just gets a random station ID url, starting with K
		return stationNames;
 */
void get_next_url(char* APIurl, char *stationIndex[], int randomStationIndex){

    char* formatString = "http://w1.weather.gov/xml/current_obs/%s.xml";
    
    sprintf(APIurl, formatString, stationIndex[randomStationIndex]);

}


/*
 * write_bits - store the randomness we got from 
 * our weather!
 *
 * We use only two numbers that we believe are
 * actually random - the temp and the dew point.
 */
unsigned int write_bits(char * response) {

    char * temp = (char *) malloc(6*sizeof(char));
    char * dew = (char *) malloc(6*sizeof(char));


    get_output(response, "<temp_f>",  temp);
    get_output(response, "<dewpoint_f>", dew);


    if(!temp || !dew) {
        free(temp);
        free(dew);
        return ERROR;
    }

    unsigned int itemp = atoi(temp);
    unsigned int idew = atoi(dew);

    

    free(temp);
    free(dew);

    unsigned int toRet = itemp % 4;
    toRet = toRet << 2;
    toRet = toRet | (idew % 4);
    
    return toRet;
}

/* 
 * get_output - get various fields from the weather
 * information
 *
 * Works based on tags in curled HTML
 */
void  get_output(char *response, char *tag,  char * data) {

    char *tagPointer = strstr(response, tag);


    if (tagPointer == NULL) {

        data = NULL;
        return;
    }

    tagPointer = strpbrk(tagPointer, ">"); /* shift to the end of the tag */
    int lengthUntilTag = strcspn(tagPointer, "<");
    tagPointer++; /* shift past the ">" in the starting tag */


    snprintf(data, lengthUntilTag, tagPointer); /* write in the data */


}

/*
 * get_station_list - build an index of valid stations
 * 
 * Used to increase speed and efficiency in our random
 * number generator
 */
void get_station_list(FILE *fp, char *stationNames[]){

	if(fp != NULL){
		int index = 0;
		char line[10];

		while(fgets(line, sizeof(line), fp) != NULL){
			
			stationNames[index] = malloc(sizeof(line)+1);
			int nameSize = strcspn(line, "\n");
			strncpy(stationNames[index], line, nameSize);
			index++;
		}
	}
	else{
		fprintf(stdout, "Error reading stations\n");
	}
}


