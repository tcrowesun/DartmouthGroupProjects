# RNG Makefile
CC = gcc
CFLAGS = -Wall -pedantic -std=c11 -lcurl

# Directory locations
SRC = src/
UTIL = util/
TEST = test/

all: random extender

random:
	@$(CC) $(CFLAGS) -o weatherrandom $(SRC)weatherrandom.c $(UTIL)mycurl.c -I $(UTIL)
	@echo "Building weatherrandom"

extender:
	@$(CC) $(CFLAGS) -o extender $(SRC)extender.c
	@echo "Building extender"

testing:
	@$(CC) $(CFLAGS) -o badRNG $(TEST)badRNG.c
	@echo "Building badRNG"	

weather:
	@$(CC) $(CFLAGS) -o weather $(SRC)weather.c $(UTIL)mycurl.c -I $(UTIL)
	@echo "Building weather"

clean:
	@echo "Cleaning up..."
	@rm -f *~
	@rm -f *#
	@rm -f *.o
	@rm -f ./weather
	@rm -f ./weatherrandom
	@rm -f ./extender
	@rm -f ./badRNG
