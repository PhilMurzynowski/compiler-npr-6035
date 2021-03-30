#!/usr/bin/env bash

java -ea --enable-preview -jar "$(git rev-parse --show-toplevel)/build/jar/Compiler.jar" "$@"
