# Low-Level Intermediate Representation Specification

## Program

```
LLProgram {
  importDeclarations: [LLImportDeclaration],
  scalarFieldDeclarations: [LLGlobalScalarFieldDeclaration],
  arrayFieldDeclarations: [LLGlobalArrayFieldDeclaration],
  stringLiteralDeclarations: [LLStringLiteralDeclaration],
  methodDeclarations: [LLMethodDeclaration],
}
```

## Declarations

```
<LLDeclaration> <- <LLNode> {
  location(): String, // "label" or "-i(%rbp)"
}

LLImportDeclaration <- <LLDeclaration> {
  identifier: String,
}

<LLScalarFieldDeclaration> <- <LLDeclaration>

<LLArrayFieldDeclaration> <- <LLDeclaration> {
  index(register: String): String, // register: "%r10" -> "-i(%rbp,%r10,8)"
}

LLGlobalScalarFieldDeclaration <- <LLScalarFieldDeclaration> {
  identifier: String,
}

LLGlobalArrayFieldDeclaration <- <LLArrayFieldDeclaration> {
  identifier: String, // "<label>(,%r10,8)"
  length: long,
}

LLStringLiteralDeclaration <- <LLDeclaration> {
  index: long,
  value: String,
}

LLMethodDeclaration <- <LLDeclaration> {
  argumentDeclarations: [LLArgumentDeclaration],
  scalarFieldDeclarations: [LLLocalScalarFieldDeclaration],
  arrayFieldDeclarations: [LLLocalArrayFieldDeclaration],
  aliasDeclarations: [<LLAliasDeclaration>],
  body: LLControlFlowGraph,
}

LLArgumentDeclaration <- <LLScalarFieldDeclaration> {
  index: long,
}

LLLocalScalarFieldDeclaration <- <LLScalarFieldDeclaration> {
  index: long,
}

LLLocalArrayFieldDeclaration <- <LLArrayFieldDeclaration> {
  index: long,
  length: long,
}

LLAliasDeclaration <- <LLDeclaration> {
  index: long,
}

LLLabelDeclaration <- <LLDeclaration> {
  index: long,
}
```

## Control Flow Graph

```
LLControlFlowGraph {
  entry: LLBasicBlock,
  exit: LLBasicBlock,
  generate(): String,
}

LLBasicBlock {
  index: long,
  instructions: [<LLNode>], // no labels, declarations, or jumps
  trueTarget: LLBasicBlock?,
  falseTarget: LLBasicBlock?,
  generate(): String,
}
```

## Statements

```
<LLNode> {
  generate(): String,
}

LLStoreScalar <- <LLNode> {
  declaration: <LLScalarDeclaration>,
  expression: LLAliasDeclaration,
}

LLStoreArray <- <LLNode> {
  declaration: <LLArrayDeclaration>,
  index: LLAliasDeclaration,
  expression: LLAliasDeclaration,
}

LLReturn <- <LLNode> {
  expression: LLAliasDeclaration?,
}

LLBranch <- <LLNode> {
  condition: LLAliasDeclaration,
  trueTarget: LLLabelDeclaration,
  falseTarget: LLLabelDeclaration,
}

LLJump <- <LLNode> {
  target: LLLabelDeclaration,
}
```

## Expressions

```
LLBinary <- <LLNode> {
  left: LLAliasDeclaration,
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
  right: LLAliasDeclaration,
  result: LLAliasDeclaration,
}

LLUnary <- <LLNode> {
  type: NOT | NEGATE,
  expression: LLAliasDeclaration,
  result: LLAliasDeclaration,
}

LLLoadScalar <- <LLNode> {
  declaration: <LLScalarDeclaration>,
  result: LLAliasDeclaration,
}

LLLoadArray <- <LLNode> {
  declaration: <LLArrayDeclaration>,
  index: LLAliasDeclaration,
  result: LLAliasDeclaration,
}

LLInternalCall <- <LLNode> {
  declaration: LLMethodDeclaration,
  arguments: [<LLAliasDeclaration>],
  result: LLAliasDeclaration,
}

LLExternalCall <- <LLNode> {
  declaration: LLImportDeclaration,
  arguments: [<LLAliasDeclaration>],
  result: LLAliasDeclaration,
}

LLLength <- <LLNode> {
  declaration: <LLArrayDeclaration>,
  result: LLAliasDeclaration,
}

LLIntegerLiteral <- <LLNode> {
  value: long,
  result: LLAliasDeclaration,
}

LLStringLiteral <- <LLNode> {
  declaration: LLStringLiteralDeclaration,
  result: LLStringAliasDeclaration,
}
```

### Templates

```

LLGlobalScalarFieldDeclaration <- <LLScalarFieldDeclaration> {
  identifier: String,
}

  <location()>:
    .quad 0

LLGlobalArrayFieldDeclaration <- <LLArrayFieldDeclaration> {
  identifier: String,
  length: long,
}

  <location()>:
    .quad <length>
    .zero <length * 8>

LLStringLiteralDeclaration <- <LLDeclaration> {
  index: long,
  value: String,
}

  <location()>:
    .asciz "<value>"

LLMethodDeclaration <- <LLDeclaration> {
  argumentDeclarations: [LLArgumentDeclaration],
  scalarFieldDeclarations: [LLLocalScalarFieldDeclaration],
  arrayFieldDeclarations: [LLLocalArrayFieldDeclaration],
  aliasDeclarations: [<LLAliasDeclaration>],
  body: LLControlFlowGraph,
}

  <location()>:
    <scalarFieldDeclarations[0].generate()>
    ...
    <arrayFieldDeclarations[0].generate()>
    ...
    <aliasFieldDeclarations[0].generate()>
    ...
    <body.generate()>

LLArgumentDeclaration <- <LLScalarFieldDeclaration> {
  index: long,
}

  // NOTE(rbd): nothing to do (this is set up by caller)

LLLocalScalarFieldDeclaration <- <LLScalarFieldDeclaration> {
  index: long,
}

  subq $8, %rsp
  movq $0, <location()>

LLLocalArrayFieldDeclaration <- <LLArrayFieldDeclaration> {
  index: long,
  length: long,
}

  subq <(length+1)*8>, %rsp
  movq <length>, <location()>
  // TODO(rbd): set all elements to zero

LLAliasDeclaration <- <LLDeclaration> {
  index: long,
}

  subq $8, %rsp
  movq $0, <location()>

LLLabelDeclaration <- <LLDeclaration> {
  index: long,
}

  <location()>:

LLControlFlowGraph {
  entry: LLBasicBlock,
  exit: LLBasicBlock,
}

  // TODO(rbd): make template

LLBasicBlock {
  index: long,
  instructions: [<LLNode>],
  trueTarget: LLBasicBlock?,
  falseTarget: LLBasicBlock?,
}

  // TODO(rbd): make template

LLStoreScalar <- <LLNode> {
  declaration: <LLScalarDeclaration>,
  expression: LLAliasDeclaration,
}

  movq <expression.location()>, %rax
  movq %rax, <declaration.location()>

LLStoreArray <- <LLNode> {
  declaration: <LLArrayDeclaration>,
  index: LLAliasDeclaration,
  expression: LLAliasDeclaration,
}

  movq <index.location()>, %r10
  movq <expression.location()>, %rax
  addq $8, %r10
  movq %rax, <declaration.index("%r10")>

LLReturn <- <LLNode> {
  expression: LLAliasDeclaration?,
}

  movq <expression.location()>, %rax
  retq

LLBranch <- <LLNode> {
  condition: LLAliasDeclaration,
  trueTarget: LLLabelDeclaration,
  falseTarget: LLLabelDeclaration,
}

  movq <condition.location()>, %eax
  cmpq $0, %eax
  jne <trueTarget.location()>
  jmp <falseTarget.location()>

LLJump <- <LLNode> {
  target: LLLabelDeclaration,
}

  jmp <target.location()>

LLBinary <- <LLNode> {
  left: LLAliasDeclaration,
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
  right: LLAliasDeclaration,
  result: LLAliasDeclaration,
}

  movq <left.location()>, %rax
  <type>q <right.location()>, %rax,
  movq %rax, <result.location()>

LLUnary <- <LLNode> {
  type: NOT | NEGATE,
  expression: LLAliasDeclaration,
  result: LLAliasDeclaration,
}

  movq <expression.location()>, %rax
  <type> %rax
  movq %rax, <result.location()>

LLLoadScalar <- <LLNode> {
  declaration: <LLScalarDeclaration>,
  result: LLAliasDeclaration,
}

  movq <declaration.location()>, %rax // -i(%rbp) or label
  movq %rax, <result.location()>

LLLoadArray <- <LLNode> {
  declaration: <LLArrayDeclaration>,
  index: LLAliasDeclaration,
  result: LLAliasDeclaration,
}
  
  movq <index.location()>, %r10
  movq <declaration.index("%r10")>, %rax // -i(%rbp,%r10,8) or label(,%r10,8)
  addq $8, %r10
  movq %rax, <result.location()>

LLInternalCall <- <LLNode> {
  declaration: LLMethodDeclaration,
  arguments: [<LLAliasDeclaration>],
  result: LLAliasDeclaration,
}

  movq <arguments[0].location()>, %edi
  movq <arguments[1].location()>, %esi
  ...
  // TODO(rbd): complete prolog
  ...
  call <declaration.location()>
  movq %rax, <result.location()>

LLExternalCall <- <LLNode> {
  declaration: LLImportDeclaration,
  arguments: [<LLAliasDeclaration>],
  result: LLAliasDeclaration,
}

  movq <arguments[0].location()>, %edi
  movq <arguments[1].location()>, %esi
  ...
  // TODO(rbd): complete prolog
  ...
  call <declaration.location()>
  movq %rax, <result.location()>

LLLength <- <LLNode> {
  declaration: <LLArrayDeclaration>,
  result: LLAliasDeclaration,
}

  movq <declaration.location()>, %rax
  movq %rax, <result.location()>

LLIntegerLiteral <- <LLNode> {
  value: long,
  result: LLAliasDeclaration,
}

  movq <value>, %rax
  movq %rax, <result.location()>

LLStringLiteral <- <LLNode> {
  declaration: LLStringLiteralDeclaration,
  result: LLStringAliasDeclaration,
}

  leaq <declaration.location()>, %rax
  movq %rax, <result.location()>
```
