
# Team NPR Decaf Compiler

*Robert Durfee, Phil Murzynowski, Noah Pauls*

Compiler written by Noah, Phil, and Robert in 2021 for the Decaf language specified in 6.035. Implements parsing, semantic checking, code generation with an custom IR and assembly output, dataflow optimizations, and further optimizations such as register allocation by graph coloring.

## Requirements for running:

Our Decaf compiler requires Java 15 to compile.

## Status

Our code passes all test cases, and there are no bugs/failures that we are currently aware of.
 
## Docs Overview:

- [Phase 1: Lexing and Parsing](1-lex-parse.md)
- [Phase 2: Semantic Checking](2-semantics.md)
- [Phase 3: Code Generation](3-codegen.md)
- [Phase 4: Data Flow](4-dataflow.md)
- [Phase 5: Optimizer](5-optimizer.md)
