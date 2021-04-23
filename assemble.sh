#!/usr/bin/env bash

# usage: ./assemble.sh <.dcf file path> <optimizations>

# example for tests/program.dcf:
#   $ ./assemble.sh tests/program all

# generate assembly
./run.sh --target=assembly --opt=$2 $1.dcf -o $1.s
# generate executable
gcc -no-pie -O0 $1.s -o $1.out
# run executable
printf "\nExecutable Output:\n";
printf "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
./$1.out
printf "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"