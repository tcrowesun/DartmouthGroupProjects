# Weather Makefile
CC = gcc
CFLAGS = -Wall -pedantic -std=c11 -lcurl

# Directory locations
SRC = src/
UTIL = util/

random:
	@$(CC) $(CFLAGS) -o weatherrandom $(SRC)weatherrandom.c $(UTIL)mycurl.c -I $(UTIL)
	@echo "Building weatherrandom"

weather:
	@$(CC) $(CFLAGS) -o weather $(SRC)weather.c $(UTIL)mycurl.c -I $(UTIL)
	@echo "Building weather"

clean:
	@rm -f *~
	@rm -f *#
	@rm -f *.o
	@rm -f ./weather
	@rm -f ./weatherrandom
