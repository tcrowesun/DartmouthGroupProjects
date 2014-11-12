// compile with gcc -std=c11 -Wall -pedantic -lcurl -o findStations findStations.c mycurl.c
/* Code to find all of the stations */

#include <stdlib.h>     // for exit
#include <stdio.h>      // for fprintf
#include <curl/curl.h>  // for curl
#include <string.h>     // for string manipulation functions to get output

#include "mycurl.h"

void writeIndex(FILE *fp, char* xmlText){
	char *stationIndicator = "<station_id>K";
	char *stationPointer = strstr(xmlText, stationIndicator);
	while(stationPointer != NULL){
		stationPointer = strpbrk(stationPointer, ">"); // Shift to the end of the string
		int nameSize = strcspn(stationPointer, "<");
		stationPointer++; // move the station pointer past >
		char stationName[nameSize];
		snprintf(stationName, nameSize, stationPointer);
		fprintf(fp, "%s\n", stationName); //write the station name into the file
		

		stationPointer = strstr(stationPointer, stationIndicator); //get next station id
	}

}


int main(int argc, char *argv[]) {


	
	/* sets up variables for the curl */
	CURL *curl;
        CURLcode res;
        struct curlResponse s;

	char indexURL[50] = "http://w1.weather.gov/xml/current_obs/index.xml";
	
	/* allocate and intiialize the output area */
	init_curlResponse(&s);

	/* initialize curl */
        curl = curl_easy_init();

	if(curl) {

                /* set url we want to visit */
                curl_easy_setopt(curl, CURLOPT_URL, indexURL);

                /* set the function curl should call with the result */
                curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, writefunc);

                /* set the buffer into which curl should place the data */
                curl_easy_setopt(curl, CURLOPT_WRITEDATA, &s);

                /* Perform the request, res will get the return code */
                res = curl_easy_perform(curl);

                if(res != CURLE_OK) {
                        fprintf(stderr, "Unsuccessful curl");
                        curl_easy_cleanup(curl);
                        return EXIT_FAILURE;
                }
		
		FILE *fp;
		fp = fopen("index.txt", "w");		
		
		writeIndex(fp, s.ptr);

		/* close out curl session */
                curl_easy_cleanup(curl);
		fclose(fp);
        }
        return EXIT_SUCCESS;	

}
