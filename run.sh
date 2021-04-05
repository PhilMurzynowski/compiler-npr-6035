#!/usr/bin/env bash

java15 -ea --enable-preview -jar "$(git rev-parse --show-toplevel)/build/jar/Compiler.jar" "$@"
