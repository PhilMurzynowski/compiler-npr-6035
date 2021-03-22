# Lexical and Syntactic Analysis

## Lexical Analysis

The compiler's first pass converts characters to tokens.

### Grammar

The following grammar is used to build tokens.

```
; -> SEMICOLON,
[ -> LEFT_SQUARE,
] -> RIGHT_SQUARE,
, -> COMMA,
( -> LEFT_ROUND,
) -> RIGHT_ROUND,
{ -> LEFT_CURLY,
} -> RIGHT_CURLY,
% -> PERCENT,
* -> STAR,
&& -> AMPERSAND_AMPERSAND,
|| -> VERTICAL_VERTICAL,
== -> EQUAL_EQUAL,
= -> EQUAL,
+= -> PLUS_EQUAL,
++ -> PLUS_PLUS,
+ -> PLUS,
-= -> MINUS_EQUAL,
-- -> MINUS_MINUS,
- -> MINUS,
!= -> BANG_EQUAL,
! -> BANG,
<= -> LESS_EQUAL,
< -> LESS,
>= -> GREATER_EQUAL,
> -> GREATER,
0x[a-zA-Z0-9]+ -> HEXADECIMAL,
[0-9]+ -> DECIMAL,
'.' -> CHARACTER,
".*" -> STRING,
/ -> SLASH,
bool -> BOOL,
break -> BREAK,
continue -> CONTINUE,
else -> ELSE,
false -> FALSE,
for -> FOR,
if -> IF,
import -> IMPORT,
int -> INT,
len -> LEN,
return -> RETURN,
true -> TRUE,
void -> VOID,
while -> WHILE,
[a-zA-Z_][a-zA-Z0-9_]* -> IDENTIFIER,
```

### Token

```
Token {
  type: SEMICOLON
    | LEFT_SQUARE
    | RIGHT_SQUARE
    | COMMA
    | LEFT_ROUND
    | RIGHT_ROUND
    | LEFT_CURLY
    | RIGHT_CURLY
    | PERCENT
    | STAR
    | AMPERSAND_AMPERSAND
    | VERTICAL_VERTICAL
    | EQUAL_EQUAL
    | EQUAL
    | PLUS_EQUAL
    | PLUS_PLUS
    | PLUS
    | MINUS_EQUAL
    | MINUS_MINUS
    | MINUS
    | BANG_EQUAL
    | BANG
    | LESS_EQUAL
    | LESS
    | GREATER_EQUAL
    | GREATER
    | HEXADECIMAL
    | DECIMAL
    | CHARACTER
    | STRING
    | SLASH
    | BOOL
    | BREAK
    | CONTINUE
    | ELSE
    | FALSE
    | FOR
    | IF
    | IMPORT
    | INT
    | LEN
    | RETURN
    | TRUE
    | VOID
    | WHILE
    | IDENTIFIER
    | EOF,
  location: (line: int, column: int),
  text: String,
}
```

The implementation can be found [here][Token].

[Token]: ../src/edu/mit/compilers/tk/Token.java

### Lexer

The lexer works like a finite state machine. Each character is consumed and transitions the state machine to a new
state. The states are represented as function references. Each function reference takes a single argument of an optional
character and returns a reference to a function representing the subsequent state. The state machine is run by
continuously passing each character of input to the current function reference and updating the function reference until
no more characters remain. At this point, one final function call is made by supplying no character which will
transition to a final state and produce an EOF token.

Several pieces of state are maintained during this execution. An accumulator holds the current text that has yet to
produce a token and a list of all previously produced tokens. There is also a line and column index into the original
text for the current position and the start of the current text accumulator.

At any point during the execution of the state machine, a token can be produced. At this point, a new token is appended
to the list of all tokens using the text accumulator and starting line/column indices. The text accumulator is consumed
and reset to empty and the starting line/column indices are set to the current line/column. The function reference
returned is to the initial state.

In addition, a lexical error can also be encountered at any point. To facilitate recovery, an error is recorded in a
list of lexical exceptions maintained during execution. When an exception is encountered, it is added to this list using
the starting line/column location. The text accumulator is discarded and the starting line/column indices are set to the
current line/column. The function reference returned is, again, to the initial state. 

Note: This mode of error recovery can lead to cascading errors as, for example, a string with an incorrect escape
character will yield an initial error for the bad escape, but possibly subsequent errors will be thrown as the state
machine resets and no longer assumes to be within a string literal. The same is likely for a character literal with a
bad escape. For simplicity and to prevent the possibility of infinite looping, this behavior is tolerated.

The implementation can be found [here][Lexer].

[Lexer]: ../src/edu/mit/compilers/tk/Lexer.java

### Lexer Exception

A lexer exception is defined as:

```
LexerException {
  location: (line: int, column: int),
  type: INVALID_CHARACTER | INVALID_ESCAPE | UNEXPECTED_EOF,
  message: String,
}
```

There are three types of lexer exceptions:
- An invalid character exception is thrown when a character is used that is not present in the language.
- An invalid escape exception is thrown when a character is not escaped properly in a string or a character literal.
- An unexpected EOF exception is thrown when the end of the file is reached before a token could be finalized, typically
  in the case of a string or character literal.

Using the location information, a pretty error message can be printed pointing directly to the mistake using a message
with helpful context.

The implementation can be found [here][LexerException].

[LexerException]: ../src/edu/mit/compilers/tk/LexerException

## Syntactic Analysis

The compiler's second pass converts tokens to a parse tree. 

### Grammar

The following grammar is used to construct the parse tree.

```
Start ::= Program EOF

Program ::= ImportDeclaration* FieldDeclaration* MethodDeclaration*

ImportDeclaration ::= IMPORT IDENTIFIER SEMICOLON
FieldDeclaration ::= (INT | BOOL) FieldIdentifierDeclaration (COMMA FieldIdentifierDeclaration)* SEMICOLON
MethodDeclaration ::= (INT | BOOL | VOID) IDENTIFIER LEFT_ROUND (ArgumentDeclaration (COMMA ArgumentDeclaration)*)? RIGHT_ROUND Block

FieldIdentifierDeclaration ::= IDENTIFIER (LEFT_SQUARE IntegerLiteral RIGHT_SQUARE)?
ArgumentDeclaration ::= (INT | BOOL) Identifier

Block ::= LEFT_CURLY FieldDeclaration* Statement* RIGHT_CURLY

Statement ::= IDAssignStatement 
  | AssignStatement 
  | CompountAssignStatement 
  | MethodCallStatement 
  | IfStatement 
  | ForStatement 
  | WhileStatement 
  | ReturnStatement 
  | BreakStatement 
  | ContinueStatement
IDAssignStatment ::= IDAssignExpression SEMICOLON
AssignStatement ::= LocationExpression EQUAL Expression SEMICOLON
CompoundAssignStatement ::= CompoundAssignExpression SEMICOLON
MethodCallStatement ::= MethodCallExpression SEMICOLON
IfStatement ::= IF LEFT_ROUND Expression RIGHT_ROUND Block (ELSE Block)?
ForStatement ::= FOR LEFT_ROUND IDAssignExpression SEMICOLON Expression SEMICOLON CompoundAssignExpression RIGHT_ROUND Block
WhileStatement ::= WHILE LEFT_ROUND Expression RIGHT_ROUND Block
ReturnStatement ::= RETURN Expression? SEMICOLON
BreakStatement ::= BREAK SEMICOLON
ContinueStatement ::= CONTINUE SEMICOLON

IDAssignExpression ::= IDENTIFIER EQUAL Expression
CompoundAssignExpression ::= LocationExpression ((PLUS_EQUAL | MINUS_EQUAL) Expression | (PLUS_PLUS | MINUS_MINUS))

Expression ::= OrExpression
OrExpression ::= AndExpression (VERTICAL_VERTICAL AndExpression)*
AndExpression ::= EqualityExpression (AMPERSAND_AMPERSAND EqualityExpression)*
EqualityExpression ::= RelationalExpression ((EQUAL_EQUAL | BANG_EQUAL) RelationalExpression)*
RelationalExpression ::= AdditiveExpression ((LESS | LESS_EQUAL | GREATER | GREATER_EQUAL) AdditiveExpression)*
AdditiveExpression ::= MultiplicativeExpression ((PLUS | MINUS) MultiplicativeExpression)*
MultiplicativeExpression ::= NotExpression ((STAR | SLASH | PERCENT) NotExpression)*
NotExpression ::= BANG* NegationExpression
NegationExpression ::= MINUS* UnitExpression
UnitExpression ::= LocationExpression
  | MethodCallExpression
  | LengthExpression
  | Literal
  | LEFT_ROUND Expression RIGHT_ROUND
LocationExpression ::= IDENTIFIER (LEFT_SQUARE Expression RIGHT_SQUARE)?
MethodCallExpression ::= IDENTIFIER LEFT_ROUND (Argument (COMMA Argument)*)? RIGHT_ROUND
LengthExpression ::= LEN LEFT_ROUND IDENTIFIER RIGHT_ROUND

Argument ::= Expression | StringLiteral

Literal ::= IntegerLiteral | CharacterLiteral | BooleanLiteral
IntegerLiteral ::= DECIMAL | HEXADECIMAL
CharacterLiteral ::= CHARACTER
BooleanLiteral ::= TRUE | FALSE

StringLiteral ::= STRING
```

Note: The grammar is carefully designed to make sure operator precedence is reflected directly in the parse tree without
any left recursion.

### Parse Tree

The parse tree is given by two variants that implement the `PTNode` interface, a terminal and a nonterminal.

The implementation can be found [here][PTNode].

[PTNode]: ../src/edu/mit/compilers/pt/PTNode

#### Terminal

```
PTTerminal <- <PTNode> {
  token: Token,
}
```

The implementation can be found [here][PTTerminal].

[PTTerminal]: ../src/edu/mit/compilers/pt/PTTerminal

#### Nonterminal

```
PTNonterminal <- <PTNode> {
  type: START
    | PROGRAM
    | IMPORT_DECLARATION
    | FIELD_DECLARATION
    | METHOD_DECLARATION
    | FIELD_IDENTIFIER_DECLARATION
    | ARGUMENT_DECLARATION
    | BLOCK
    | STATEMENT
    | ID_ASSIGN_STATEMENT
    | ASSIGN_STATEMENT
    | COMPOUND_ASSIGN_STATEMENT
    | METHOD_CALL_STATEMENT
    | IF_STATEMENT
    | FOR_STATEMENT
    | WHILE_STATEMENT
    | RETURN_STATEMENT
    | BREAK_STATEMENT
    | CONTINUE_STATEMENT
    | ID_ASSIGN_EXPRESSION
    | COMPOUND_ASSIGN_EXPRESSION
    | EXPRESSION
    | OR_EXPRESSION
    | AND_EXPRESSION
    | EQUALITY_EXPRESSION
    | RELATIONAL_EXPRESSION
    | ADDITIVE_EXPRESSION
    | MULTIPLICATIVE_EXPRESSION
    | NOT_EXPRESSION
    | NEGATION_EXPRESSION
    | UNIT_EXPRESSION
    | LOCATION_EXPRESSION
    | METHOD_CALL_EXPRESSION
    | LENGTH_EXPRESSION
    | ARGUMENT
    | LITERAL
    | INTEGER_LITERAL
    | CHARACTER_LITERAL
    | BOOLEAN_LITERAL
    | STRING_LITERAL,
  children: [<PTNode>],
}
```

The implementation can be found [here][PTNonterminal].

[PTNonterminal]: ../src/edu/mit/compilers/pt/PTNonterminal

### Parser

The parser is an LL recursive descent parser with variable lookahead and zero backtracking. Each nonterminal production
defined in the grammar above corresponds directly to a method in the parser. Each method returns a `PTNonterminal` that
corresponds to the tokens matching the respective production. The process is kicked off by calling the `parseAll` method
which begins the recursion into the `parseProgram` method. The process completes when this method returns.

The notable piece of state maintained during execution is an iterator over the tokens. This iterator forces the parser
to consume every token in sequence. However, it also allows the parser to peek any number of tokens ahead (i.e.
lookahead) without removing the token. If peeking beyond the end of the file, an EOF token is returned. This restricted
interface helps guarantee that all tokens are consumed and checked against the grammar before being added to the parse
tree.

In general, only a single token of lookahead is necessary to make all recursion decisions. However, in some
circumstances, a few additional characters are necessary (e.g. for field/method declarations and assign statements). The
decision was made to not remove this excess lookahead to keep the parse tree as simple as possible. Furthermore, the
addtional lookahead suffers no performance loss as no backtracking occurs.

At any point, an invalid token may appear. For ease of implementation, when this occurs, a parser exception is thrown.
When parsing can recover from these errors, the exception is caught. In general, there are two types of exceptions that
our parser recovers from: a statement exception and a block exception. If an exception happens within a statement (or
any construct that ends in a semicolon), the parser produces an error and discards all tokens up to and including the
next semicolon and continues. If an exception happens within a block, the parse produces an error and discards all
tokens up to and including a right curly brace. 

Note: To reduce a cascade in errors, the parser attempts to match left and right curly braces during this process. As a
result, a cascade in errors involving nested blocks should be avoided, so long as each right curly brace has a matching
left curly brace.

The implementation can be found [here][Parser].

[Parser]: ../src/edu/mit/compilers/pt/Parser

### Parser Exception

A parser exception is defined as:

```
ParserException {
  location: (line: int, column: int),
  type: INVALID_TOKEN | INCOMPLETE_PARSE | UNEXPECTED_EOF,
  message: String,
}
```

There are three types of parser exceptions:
- An invalid token exception is thrown when a token is encountered that doesn't match the grammar.
- An incomplete parse exception is thrown when the program is successfully parsed, but there are additional characters
  that were not consumed at the end of the file. This is typically rare, and should only happen in an unlucky cascade of
  errors
- An unexpected EOF exception is thrown when the end of the file is reached before an entire nonterminal production has
  been matched. This can occur, for example, with mismatched curly braces.

As with the lexer exceptions, location information can be used to produce a pretty error message pointing directly to
the mistake in the source code.

The implementation can be found [here][ParserException].

[ParserException]: ../src/edu/mit/compilers/pt/ParserException

## Syntactic Analysis (cont'd)

The compiler's third pass converts a parse tree into an abstract syntax tree.

### Abstract Syntax Tree

The abstract syntax tree is given by many variants that implement a hierarchy of interfaces. The root interface is the
`ASTNode`.

#### Program

```
ASTProgram <- <ASTNode> {
  importDeclarations: [ASTImportDeclaration],
  fieldDeclarations: [ASTFieldDeclaration],
  methodDeclarations: [ASTMethodDeclaration],
}
```

The implementation can be found [here][ASTProgram].

[ASTProgram]: ../src/edu/mit/compilers/ast/ASTProgram

#### Declarations

```
ASTImportDeclaration <- <ASTNode> {
  identifier: String,
}

ASTFieldDeclaration <- <ASTNode> {
  type: INTEGER | BOOLEAN,
  identifiers: [(identifier: String, length: ASTIntegerLiteral?)],
}

ASTMethodDeclaration <- <ASTNode> {
  type: INTEGER | BOOLEAN | VOID,
  identifier: String,
  arguments: [(type: INTEGER | BOOLEAN, identifier: String)],
  body: ASTBlock,
}
```

The respective implementations can be found [here][ASTImportDeclaration], [here][ASTFieldDeclaration], and
[here][ASTMethodDeclaration].

[ASTImportDeclaration]: ../src/edu/mit/compilers/ast/ASTImportDeclaration
[ASTFieldDeclaration]: ../src/edu/mit/compilers/ast/ASTFieldDeclaration
[ASTMethodDeclaration]: ../src/edu/mit/compilers/ast/ASTMethodDeclaration

#### Block

```
ASTBlock <- <ASTNode> {
  fieldDeclarations: [ASTFieldDeclaration],
  statements: [<ASTStatement>],
}
```

The implementation can be found [here][ASTBlock].

[ASTBlock]: ../src/edu/mit/compilers/ast/ASTBlock

#### Statements

All statements implement the `ASTStatement` interface which extends the `ASTNode` interface.

```
ASTIDAssignStatement <- <ASTStatement> {
  identifier: String,
  expression: <ASTExpression>,
}

ASTAssignStatement <- <ASTStatement> {
  location: ASTLocationExpression,
  expression: <ASTExpression>,
}

ASTCompoundAssignStatement <- <ASTStatement> {
  location: ASTLocationExpression,
  type: ADD | SUBTRACT | INCREMENT | DECREMENT,
  expression: <ASTExpression>?,
}

ASTMethodCallStatement <- <ASTStatement> {
  call: ASTMethodCallExpression,
}

ASTIfStatement <- <ASTStatement> {
  condition: <ASTExpression>,
  body: ASTBlock,
  other: ASTBlock?,
}

ASTForStatement <- <ASTStatement> {
  initial: ASTIDAssignStatement,
  condition: <ASTExpression>,
  update: ASTCompoundAssignStatement,
  body: ASTBlock,
}

ASTWhileStatement <- <ASTStatement> {
  condition: <ASTExpression>,
  body: ASTBlock,
}

ASTReturnStatement <- <ASTStatement> {
  expression: <ASTExpression>?,
}

ASTBreakStatement <- <ASTStatement> { }

ASTContinueStatement <- <ASTStatement> { }
```

The respective implementations can be found [here][ASTIDAssignStatement], [here][ASTAssignStatement],
[here][ASTCompoundAssignStatement], [here][ASTMethodCallStatement], [here][ASTIfStatement], [here][ASTForStatement],
[here][ASTWhileStatement], [here][ASTReturnStatement], [here][ASTBreakStatement], and [here][ASTContinueStatement].

[ASTIDAssignStatement]: ../src/edu/mit/compilers/ast/ASTIDAssignStatement
[ASTAssignStatement]: ../src/edu/mit/compilers/ast/ASTAssignStatement
[ASTCompoundAssignStatement]: ../src/edu/mit/compilers/ast/ASTCompoundAssignStatement
[ASTMethodCallStatement]: ../src/edu/mit/compilers/ast/ASTMethodCallStatement
[ASTIfStatement]: ../src/edu/mit/compilers/ast/ASTIfStatement
[ASTForStatement]: ../src/edu/mit/compilers/ast/ASTForStatement
[ASTWhileStatement]: ../src/edu/mit/compilers/ast/ASTWhileStatement
[ASTReturnStatement]: ../src/edu/mit/compilers/ast/ASTReturnStatement
[ASTBreakStatement]: ../src/edu/mit/compilers/ast/ASTBreakStatement
[ASTContinueStatement]: ../src/edu/mit/compilers/ast/ASTContinueStatement

#### Expressions

All expressions implement the `ASTExpression` interface. This interface extends the `ASTArgument` interface which
extends the `ASTNode` interface directly.

```
ASTBinaryExpression <- <ASTExpression> {
  left: <ASTExpression>,
  type: OR
    | AND 
    | EQUAL 
    | NOT_EQUAL 
    | LESS_THAN 
    | LESS_THAN_OR_EQUAL 
    | GREATER_THAN 
    | GREATER_THAN_OR_EQUAL 
    | ADD 
    | SUBTRACT 
    | MULTIPLY 
    | DIVIDE 
    | MODULUS,
  right: <ASTExpression>,
}

ASTUnaryExpression <- <ASTExpression> {
  type: NOT | NEGATE,
  expression: <ASTExpression>,
}

ASTLocationExpression <- <ASTExpression> {
  identifier: String,
  offset: <ASTExpression>?,
}

ASTMethodCallExpression <- <ASTExpression> {
  identifier: String,
  arguments: [<ASTArgument>],
}

ASTLengthExpression <- <ASTExpression> {
  identifier: String,
}
```

The respective implementations can be found [here][ASTBinaryExpression], [here][ASTUnaryExpression],
[here][ASTLocationExpression], [here][ASTMethodCallExpression], and [here][ASTLengthExpression].

[ASTBinaryExpression]: ../src/edu/mit/compilers/ast/ASTBinaryExpression
[ASTUnaryExpression]: ../src/edu/mit/compilers/ast/ASTUnaryExpression
[ASTLocationExpression]: ../src/edu/mit/compilers/ast/ASTLocationExpression
[ASTMethodCallExpression]: ../src/edu/mit/compilers/ast/ASTMethodCallExpression
[ASTLengthExpression]: ../src/edu/mit/compilers/ast/ASTLengthExpression

#### Literals

Most literals implement the more restrictive `ASTExpression` interface. However, the `ASTStringLiteral` implements the
more general `ASTArgument` interface which is used only in method calls.

```
ASTIntegerLiteral <- <ASTExpression> {
  value: BigInteger,
}

ASTCharacterLiteral <- <ASTExpression> {
  value: char,
}

ASTBooleanLiteral <- <ASTExpression> {
  value: boolean,
}

ASTStringLiteral <- <ASTArgument> {
  value: String,
}
```

Note: The `ASTIntegerLiteral` uses a `BigInteger` as a representation instead of a `long` directly. This is to correctly
parse negative integer literals which are initially parsed as positive (and would thus overflow).

The respective implementations can be found [here][ASTIntegerLiteral], [here][ASTCharacterLiteral],
[here][ASTBooleanLiteral], and [here][ASTStringLiteral]

[ASTIntegerLiteral]: ../src/edu/mit/compilers/ast/ASTIntegerLiteral
[ASTCharacterLiteral]: ../src/edu/mit/compilers/ast/ASTCharacterLiteral
[ASTBooleanLiteral]: ../src/edu/mit/compilers/ast/ASTBooleanLiteral
[ASTStringLiteral]: ../src/edu/mit/compilers/ast/ASTStringLiteral

### Abstracter

The abstracter works very similarly to the parser. There is a one-to-one relation between the parse tree and the
abstract syntax tree. As a result, no errors are possible when converting between the two. Nevertheless, the abstracter
takes an extremely conservative approach and consumes every token and asserts that is correct. This firms our confidence
in the resulting abstract syntax tree.

The abstracter is also responsible for simplifying expressions. The parse tree accurately reflects proper operator
precedence, but the result is often a deeply nested tree structure. The abstracter collapses this recursion when no
operator is present and reduces the n-ary tree of operators of the same precedence into a left-recursive binary tree we
are familiar with.

The implementation can be found [here][Abstracter].

[Abstracter]: ../src/edu/mit/compilers/ast/Abstracter
