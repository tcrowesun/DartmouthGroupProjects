#!/bin/bash
# Script name: generaterand.sh
#
# Description: Builds and runs the various components
# of our weather-based random number generator in sequence.
# Tells users what is going on at each point in the process,
# including building each module and cleaning at the end
#
# Command line options: none
#
# Input: name of file for output, number of ints for initial/
# seed random number generation
#
# Output: a binary file containing a specified number of random
# bits
#
# Pseudocode: unnecessary here

# Check that the script has two valid parameters
if [ $# -ne 3 ]
  then
    echo "Usage: generaterand args must be [filename numints numbytes]" 1>&2
    exit 1
fi

# Parameter 1 must be file that does not exist
if [ -f $1 ]
  then
    echo "Invalid argument $1, file cannot already exist" 1>&2
    exit 1
fi

# Parameter 2 must be an integer
re='^[0-9]+$'
if ! [[ $2 =~ $re ]] || ! [[ $3 =~ $re ]]
  then
    echo "Error: numints and numbytes must be integers" 1>&2
    exit 1
fi

# Valid, so proceed!
echo "All inputs valid!" 
make

# Call weatherrandom with arguments
echo "Generating random numbers from the weather..."
weatherrandom temp $2

# Call extender
echo "Extending random numbers..."
extender temp $2 $1 $3

# We're done!
echo "Complete, output file is $1"
rm temp
make clean
