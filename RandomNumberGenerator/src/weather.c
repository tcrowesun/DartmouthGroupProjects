/*	weather.c - uses easycurl to output weather information using NOAA's webite

	Project name: CS50 Lab3
	Component name: weather.c

	This file contains code for an executable that expects a 4-digit station ID
	and retrieves weather data for that station from the NOAA's website, then prints
	the information to the user.
	
	Primary Author:	Chris Leech
	Date Created:	4/14/14

	Special considerations:  
	Must have "mycurl.c" and "mycurl.h" in the same directory to compile
	Will compile with gcc using the following tags:
	gcc -std=c11 -Wall -pedantic -lcurl -o Weather weather.c mycurl.c  
	
======================================================================*/
// //
// // ---------------- Open Issues none
//
// // ---------------- System includes:

#include <stdlib.h>	// for exit
#include <stdio.h>	// for fprintf
#include <curl/curl.h>  // for curl
#include <string.h>	// for string manipulation functions to get output
	
// // ---------------- Local includes:

#include "mycurl.h"    // to use ccp's curl functions

// // ---------------- Constant definitions none
//
// // ---------------- Macro definitions none
//
// // ---------------- Structures/Types Types found in the curl headers.
//
// // ---------------- Private variables
//
// // ---------------- Private prototypes 
//
int check_code_found(char * response, CURL *curl);		// checks if the station ID was correct
void get_output(char * response, char * tag, char * message);	// prints out a single piece of data
void print_output(char * response);				// calls getOutput for all the data

// /*====================================================================*/



int main(int argc, char *argv[]) {
	

	// if the user enters no args, print a the help message below
	if(argc == 1) {
		fprintf(stdout, "HELP: Weather takes one argument, a four-letter station code.\n");
		fprintf(stdout, "Station codes can be found at the website below:\n");
		fprintf(stdout, "http://w1.weather.gov/xm/current_obs/seek.php?state=ak&Find=Find\n" );
		return EXIT_FAILURE;
	}

	/* Kick out if user enters too many args */
	if (argc > 2) {
		fprintf(stderr, "%s needs one arg, a four-letter location code. Enter no args for help.", argv[0]);
		return EXIT_FAILURE;
	}

	/* If the code isn't four characters long, it can't be used */
	if (strlen(argv[1]) != 4) {
                fprintf(stderr, "%s must be four characters long.", argv[1]);
                return EXIT_FAILURE;
	}


	char *stationCode = argv[1]; /* grabs the code from the user */
	
	/* sets up variables for the curl */
	CURL *curl; 
    	CURLcode res;
    	struct curlResponse s;

    	/* Here is the format string for the URL we will request */	
	char *APIurl  = "http://w1.weather.gov/xml/current_obs/%s.xml";
	
	/* our new url will be 50 chars long */
	char modifiedURL[50];
	
	/* allocate and intiialize the output area */
    	init_curlResponse(&s);
	
    	/* create the URL from the format string */
	sprintf(modifiedURL, APIurl, stationCode); 
	
    	/* initialize curl */
    	curl = curl_easy_init();
	
    	if(curl) {
        	
		/* set url we want to visit */
        	curl_easy_setopt(curl, CURLOPT_URL, modifiedURL);
        	
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


		/* make sure the station exists */
		if(check_code_found(s.ptr, curl)) {
			fprintf(stderr, "Station %s not found", stationCode);
                        curl_easy_cleanup(curl);
			return EXIT_FAILURE;
		}
        	
		/* print output to user */
		print_output(s.ptr);
        
	
		/* close out curl session */
		curl_easy_cleanup(curl);
    	}
	return EXIT_SUCCESS;
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
 * get_output -  finds and prints data from a specific tag.
 * 
 * @response - the response string from the curl
 * @tag - the tag on the data you want to print - ie <station_id>
 * @message - what you want to print before the data - ie "The ID is: "
 *
 * The function uses sting.h functions to find the tag. 
 * If not found, it prints that this
 * specific piece of data was not found, but doesn't exit.
 * When found, it prints the message, followed by the data.
 */

void  get_output(char *response, char *tag, char *message) {

	char *tagPointer = strstr(response, tag);
	
	if (tagPointer == NULL) {
		fprintf(stdout, "%s Not found.\n", message);
		return;
	}

	tagPointer = strpbrk(tagPointer, ">"); /* shift to the end of the tag */
	int lengthUntilTag = strcspn(tagPointer, "<");
	tagPointer++; /* shift past the ">" in the starting tag */
	char data[lengthUntilTag];

	snprintf(data, lengthUntilTag, tagPointer); /* write in the data */
	fprintf(stdout, "%s %s\n", message, data); /* print output */
} 


/*
 * print_output -  unclutters the main function by holding all the
 * calls to get_output for each piece of data.
 * @response is the string that we will be searching through for the data
 */

void print_output(char * response) {

	get_output(response, "<credit>", "Credit: ");
	get_output(response, "<location>", "Location: ");
	get_output(response, "<station_id>", "Station ID: ");
	get_output(response, "<observation_time>", "Observation time: ");
        get_output(response, "<weather>", "Weather: ");
        get_output(response, "<temperature_string>", "Temperature: ");
	get_output(response, "<relative_humidity>", "Relative humidity: ");
        get_output(response, "<wind_string>", "Wind: ");
        get_output(response, "<visibility_mi", "Visibility (miles): ");
}



