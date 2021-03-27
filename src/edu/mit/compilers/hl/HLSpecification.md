# High-Level Intermediate Representation Specification

## Program

```
HLProgram {
  importDeclarations: [HLImportDeclaration],
  scalarFieldDeclarations: [HLGlobalScalarFieldDeclaration],
  arrayFieldDeclarations: [HLGlobalArrayFieldDeclaration],
  stringLiteralDeclarations: [HLStringLiteralDeclaration],
  methodDeclarations: [HLMethodDeclaration],
}
```

## Declarations

```
HLImportDeclaration {
  identifier: String,
}

<HLScalarFieldDeclaration> {
  getType(): INTEGER | BOOLEAN,
}

<HLArrayFieldDeclaration> {
  getType(): INTEGER | BOOLEAN,
}

HLGlobalScalarFieldDeclaration <- <HLScalarFieldDeclaration> {
  type: INTEGER | BOOLEAN,
  identifier: String,
}

HLGlobalArrayFieldDeclaration <- <HLArrayFieldDeclaration> {
  type: INTEGER | BOOLEAN,
  identifier: String,
  length: long,
}

Noah
HLStringLiteralDeclaration {
  index: int,
  value: String,
}

Phil
HLMethodDeclaration {
  identifier: String,
  body: HLBlock,
}

HLArgumentDeclaration <- <HLScalarFieldDeclaration> {
  type: INTEGER | BOOLEAN,
  index: int,
}

Noah
HLLocalScalarFieldDeclaration <- <HLScalarFieldDeclaration> {
  type: INTEGER | BOOLEAN,
  index: int, // location(): -(<NUM_ARGS> + <INDEX>)(%rbp)
}

Phil
HLLocalArrayFieldDeclaration <- <HLArrayFieldDeclaration> {
  index: int,
  length: int, // location(): -(<NUM_ARGS> + <NUM_SCALARS> + <SUM_OF_PREVIOUS>)(%rbp)
}
```

## Block

```
HLBlock {
  argumentDeclarations: [HLArgumentDeclaration],
  scalarFieldDeclarations: [HLLocalScalarFieldDeclaration],
  arrayFieldDeclarations: [HLLocalArrayFieldDeclaration],
  statements: [<HLStatement>],
}
```

## Statements

```
Noah
<HLNode>

Phil
<HLStatement> <- <HLNode>

<HLStoreStatement> <- <HLStatement>

Noah
HLStoreScalarStatement <- <HLStoreStatement> {
  declaration: <HLScalarFieldDeclaration>,
  expression: <HLExpression>,
}

Phil
HLStoreArrayStatement <- <HLStoreStatement> {
  declaration: <HLArrayFieldDeclaration>,
  index: <HLExpression>,
  expression: <HLExpression>,
}

HLCallStatement <- <HLStatement> {
  call: HLCallExprression,
}

Noah
HLIfStatement <- <HLStatement> {
  condition: <HLExpression>,
  body: HLBlock,
  other: HLBlock?,
}

Phil
HLForStatement <- <HLStatement> {
  initial: HLStoreScalarStatement,
  condition: <HLExpression>,
  update: <HLStoreStatement>,
  body: HLBlock,
}

HLWhileStatement <- <HLStatement> {
  condition: <HLExpression>,
  body: HLBlock,
}

Noah
HLReturnStatement <- <HLStatement> {
  expression: <HLExpression>?,
}

Phil
HLBreakStatement <- <HLStatement> { }

HLContinueStatement <- <HLStatement> { }
```

## Expressions

```
Noah
<HLArgument> <- <HLNode>
<HLExpression> <- <HLArgumen>

Phil
HLBinaryExpression <- <HLExpression> {
  left: <HLExpression>,
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
  right: <HLExpression>,
}

HLUnaryExpression <- <HLExpression> {
  type: NOT | NEGATE,
  expression: <HLExpression>,
}

Noah
HLLoadScalarExpression <- <HLExpression> {
  declaration: <HLScalarFieldDeclaration>,
}

Phil
HLLoadArrayExpression <- <HLExpression> {
  declaration: <HLArrayFieldDeclaration>,
  index: <HLExpression>,
}

<HLCallExpression> <- <HLExpression>

Noah
HLInternalCallExpression <- <HLCallExpression> {
  declaration: HLMethodDeclaration,
  arguments: [<HLExpression>],
}

Phil
HLExternalCallExpression <- <HLCallExpression> {
  declaration: HLImportDeclaration,
  arguments: [<HLArgument>],
}

HLLengthExpression <- <HLExpression> {
  declaration: <HLArrayDeclaration>,
}
```

## Literals

```
Noah
HLIntegerLiteral <- <HLExpression> {
  value: long,
}

Phil
HLStringLiteral <- <HLArgument> {
  declaration: HLStringLiteralDeclaration,
}
```
