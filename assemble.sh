#!/usr/bin/env bash

# Usage:
# ./assemble.sh  file.dcf  --opt=opts

IFS='.';
FILE_ARRAY=($1);
unset IFS;

EXT=${FILE_ARRAY[-1]};

if [[ "$EXT" != "dcf" ]]
then
  printf "%s is not a .dcf file\n" "$1";
  exit 1;
fi

INDEX=${#1};
FULL_FILE=${1:0:$INDEX-4};

printf "\nDecaf file with path %s\n" "$FULL_FILE";

printf "\nCompiler Output:\n";
printf "%s\n" "--------------------------------------------------";
# generate assembly
./run.sh --target=assembly "$2" "$FULL_FILE".dcf -o "$FULL_FILE".s;
printf "%s\n" "--------------------------------------------------";

# generate executable
gcc -no-pie -O0 "$FULL_FILE".s -o "$FULL_FILE".out;

printf "\nExecutable Output:\n";
printf "%s\n" "--------------------------------------------------";
# run executable
./"$FULL_FILE".out;
printf "%s\n" "--------------------------------------------------";