# Low-Level Intermediate Representation Specification

```
<LLNode>
```

## Declarations

```
LLProgram <- <LLNode> {
  importDeclarations: [LLImportDeclaration],
  scalarFieldDeclarations: [LLGlobalScalarFieldDeclaration],
  arrayFieldDeclarations: [LLGlobalArrayFieldDeclaration],
  stringLiteralDeclarations: [LLStringLiteralDeclaration],
  methodDeclarations: [LLMethodDeclaration],
}

<LLDeclaration> <- <LLNode>

LLImportDeclaration <- <LLDeclaration> {
  identifier: String,
}

<LLScalarFieldDeclaration> <- <LLDeclaration>

LLGlobalScalarFieldDeclaration <- <LLScalarFieldDeclaration> {
  identifier: String,
}

LLLocalScalarFieldDeclaration <- <LLScalarFieldDeclaration> {
  index: int,
}

LLArgumentDeclaration <- <LLScalarFieldDeclaration> {
  index: int,
}

<LLArrayFieldDeclaration> <- <LLDeclaration>

LLGlobalArrayFieldDeclaration <- <LLArrayFieldDeclaration> {
  identifier: String,
  length: long,
}

LLLocalArrayFieldDeclaration <- <LLArrayFieldDeclaration> {
  index: int,
  length: long,
}

LLStringLiteralDeclaration <- <LLDeclaration> {
  index: int,
  value: String,
}

LLMethodDeclaration <- <LLDeclaration> {
  argumentDeclarations: [LLArgumentDeclaration],
  scalarFieldDeclarations: [LLLocalScalarFieldDeclaration],
  arrayFieldDeclarations: [LLLocalArrayFieldDeclaration],
  aliasDeclarations: [LLAliasDeclaration],
  body: LLControlFlowGraph,
}

LLAliasDeclaration <- <LLDeclaration> {
  index: int,
}
```

## Control Flow Graph

```
LLControlFlowGraph <- <LLNode> {
  entry: LLBasicBlock,
  exit: LLBasicBlock,
}
```

## Basic Block

```
LLBasicBlock <- <LLNode> {
  index: int,
  instructions: [<LLInstruction>],
  trueTarget: LLBasicBlock?,
  falseTarget: LLBasicBlock?,
}
```

## Instructions

```
<LLInstruction> <- <LLNode>

LLStoreScalar <- <LLInstruction> {
  location: <LLScalarDeclaration>,
  expression: <LLDeclaration>,
}

LLStoreArray <- <LLInstruction> {
  location: <LLArrayDeclaration>,
  index: <LLDeclaration>,
  expression: <LLDeclaration>,
}

LLReturn <- <LLInstruction> {
  expression: <LLDeclaration>?,
}

LLException <- <LLInstruction> {
  type: OUT_OF_BOUNDS | NO_RETURN_VALUE,
}

LLCompare <- <LLInstruction> {
  left: <LLDeclaration>,
  right: <LLDeclaration>,
}

LLBinary <- <LLInstruction> {
  left: <LLDeclaration>,
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
  right: <LLDeclaration>,
  result: <LLDeclaration>,
}

LLUnary <- <LLInstruction> {
  type: NOT | NEGATE | INCREMENT | DECREMENT,
  expression: <LLDeclaration>,
  result: <LLDeclaration>,
}

LLLoadScalar <- <LLInstruction> {
  location: <LLScalarDeclaration>,
  result: <LLDeclaration>,
}

LLLoadArray <- <LLInstruction> {
  location: <LLArrayDeclaration>,
  index: <LLDeclaration>,
  result: <LLDeclaration>,
}

LLInternalCall <- <LLInstruction> {
  declaration: LLMethodDeclaration,
  arguments: [<LLDeclaration>],
  result: <LLDeclaration>,
}

LLExternalCall <- <LLInstruction> {
  declaration: LLImportDeclaration,
  arguments: [<LLDeclaration>],
  result: <LLDeclaration>,
}

LLLength <- <LLInstruction> {
  declaration: <LLArrayDeclaration>,
  result: <LLDeclaration>,
}

LLIntegerLiteral <- <LLInstruction> {
  value: long,
  result: <LLDeclaration>,
}

LLStringLiteral <- <LLInstruction> {
  declaration: LLStringLiteralDeclaration,
  result: <LLDeclaration>,
}
```

## Templates

### Declarations

```
LLProgram: 
  # ImportDeclarations
  <importDeclarations[0].generate()>
  <importDeclarations[1].generate()>
  ...

  # Global Scalar Fields
  <scalarFieldDeclarations[0].generate()>
  <scalarFieldDeclarations[1].generate()>
  ...

  # Global Array Fields
  <arrayFieldDeclarations[0].generate()>
  <arrayFieldDeclarations[1].generate()>
  ...

  # String Literal Declarations
  <stringLiteralDeclarations[0].generate()>
  <stringLiteralDeclarations[1].generate()>
  ...
  out_of_bounds:
    .string "Array index access is out-of-bounds.\n"
    .align 16
  no_return_value:
    .string "Reached end of non-void method without returning a value.\n"
    .align 16

  # Methods
  <methodDeclarations[0].generate()>
  <methodDeclarations[1].generate()>
  ...

LLImportDeclaration:
  # Imported <location()>

LLGlobalScalarFieldDeclaration:
  <location()>:
    .quad 0

LLLocalScalarFieldDeclaration:
  # Local Scalar <location()>

LLArgumentDeclaration:
  # Argument <location()>

LLGlobalArrayFieldDeclaration:
  <location()>:
    .zero <length * 8>

LLLocalArrayFieldDeclaration:
  # Local Array <location()>[<length>]

LLStringLiteralDeclaration:
  <location()>:
    .string "<value>"
    .align 16

LLMethodDeclaration:
  <location()>:
    pushq  %rbp
    movq  %rsp,%rbp
    subq  $<stackSize>,%rsp
    <body.generate()>

LLAliasDeclaration:
  # Alias <location()>
```

### Control Flow Graph

```
LLControlFlowGraph: 
  <entry.generate()>
```

### Basic Block

```
LLBasicBlock (falseTarget.isPresent()):
  <instructions[0].generate()>
  <instructions[1].generate()>
  ...
  je  $<falseTarget.location()>
  jmp  $<trueTarget.location()>
  <trueTarget.generate()>
  <falseTarget.generate()>

LLBasicBlock (trueTarget.isPresent()):
  <instructions[0].generate()>
  <instructions[1].generate()>
  ...
  jmp  $<trueTarget.location()>
  <trueTarget.generate()>

LLBasicBlock:
  <instructions[0].generate()>
  <instructions[1].generate()>
  ...
```

### Instructions

```
LLStoreScalar:
  movq  <expression.location()>,%rax
  movq  %rax,<declaration.location()>

LLStoreArray:
  movq  <index.location()>,%r10
  movq  <expression.location()>,%rax
  movq  %rax,<declaration.index("%r10")>

LLReturn:
  movq  <expression.location()>,%rax
  movq  %rbp,%rsp
  popq  %rbp
  retq

LLException:
  leaq  <message>(%rip),%rdi
  callq  printf
  movq  $<returnValue>,%rdi
  callq  exit

LLCompare:
  movq  <left.location()>,%rax
  cmpq  <right.location()>,%rax

LLBinary (type == OR | AND | ADD | SUBTRACT | MULTIPLY):
  movq  <left.location()>,%rax
  <type>  <right.location()>,%rax
  movq  %rax,<result.location()>

LLBinary (type == EQUAL | NOT_EQUAL | LESS_THAN | LESS_THAN_OR_EQUAL | GREATER_THAN | GREATER_THAN_OR_EQUAL):
  movq  <left.location()>,%r10
  xorq  %rax,%rax
  cmpq  <right.location()>,%r10
  set<type>  %al
  movq  %rax,<result.location()>

LLBinary (type == DIVIDE):
  movq  <left.location()>,%rax
  cqto
  idivq  <right.location()>
  movq  %rax,<result.location()>

LLBinary (type == MODULUS):
  movq  <left.location()>,%rax
  cqto
  idivq  <right.location()>
  movq  %rdx,<result.location()>

LLUnary (type == NEGATE | INCREMENT | DECREMENT):
  movq  <expression.location()>,%rax
  <type>  %rax
  movq  %rax,<result.location()>

LLUnary (type == NOT):
  movq  <expression.location()>,%r10
  xorq  %rax,%rax
  testq  %r10,%r10
  sete  %al
  movq  %rax,<result.location()>

LLLoadScalar:
  movq  <declaration.location()>,%rax
  movq  %rax,<result.location()>

LLLoadArray:
  movq  <index.location()>,%r10
  movq  <declaration.index("%r10")>,%rax
  movq  %rax,<result.location()>

LLInternalCall:
  movq  <arguments[0].location()>,%rdi
  movq  <arguments[1].location()>,%rsi
  movq  <arguments[2].location()>,%rdx
  movq  <arguments[3].location()>,%rcx
  movq  <arguments[4].location()>,%r8
  movq  <arguments[5].location()>,%r9
  pushq  <arguments[6].location()>
  pushq  <arguments[7].location()>
  ...
  callq  <declaration.location()>
  addq  $<(arguments.size() - 6) * 8>,%rsp
  movq  %rax,<result.location()>

LLExternalCall:
  movq  <arguments[0].location()>,%rdi
  movq  <arguments[1].location()>,%rsi
  movq  <arguments[2].location()>,%rdx
  movq  <arguments[3].location()>,%rcx
  movq  <arguments[4].location()>,%r8
  movq  <arguments[5].location()>,%r9
  pushq  <arguments[6].location()>
  pushq  <arguments[7].location()>
  ...
  callq  <declaration.location()>
  addq  $<(arguments.size() - 6) * 8>,%rsp
  movq  %rax,<result.location()>

LLLength:
  movq  $<declaration.getLength()>,%rax
  movq  %rax,<result.location()>

LLIntegerLiteral:
  movq  $<value>,%rax
  movq  %rax,<result.location()>

LLStringLiteral:
  leaq  <declaration.location()>(%rip),%rax
  movq  %rax,<result.location()>
```
