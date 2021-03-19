# Lexing and Parsing

## Error recovery during lexing and parsing

*to be written*

## From parse tree to AST

The parse tree generated during parsing is converted to an AST through the [abstracter][1]. The new AST is made up of AST data objects; the specification for these data objects can be found [here.][2]

[1]: ../src/edu/mit/compilers/ast/Abstracter.java
[2]: ../src/edu/mit/compilers/ast/ASTSpecification.md