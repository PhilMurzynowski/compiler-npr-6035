# High-Level Intermediate Representation Specification

## Program

```
HLProgram {
  scalarFieldDeclarations: [HLGlobalScalarFieldDeclaration],
  arrayFieldDeclarations: [HLGlobalArrayFieldDeclaration],
  stringLiteralDeclarations: [HLStringLiteralDeclaration],
  methodDeclarations: [HLMethodDeclaration],
}
```

## Declarations

```
<HLScalarFieldDeclaration>
<HLArrayFieldDeclaration>

HLGlobalScalarFieldDeclaration <- <HLScalarFieldDeclaration> {
  identifier: String,
}

HLGlobalArrayFieldDeclaration <- <HLArrayFieldDeclaration> {
  identifier: String,
  length: long,
}

HLStringLiteralDeclaration {
  index: long,
  value: String,
}

HLMethodDeclaration {
  identifier: String,
  body: HLBlock,
}

HLArgumentDeclaration <- <HLScalarFieldDeclaration> {
  index: long,
}

HLLocalScalarFieldDeclaration <- <HLScalarFieldDeclaration> {
  index: long,
}

HLLocalArrayFieldDeclaration <- <HLArrayFieldDeclaration> {
  index: long,
  length: long,
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
<HLNode>

<HLStatement> <- <HLNode>

<HLStoreStatement> <- <HLStatement>

HLStoreScalarStatement <- <HLStoreStatement> {
  declaration: <HLScalarFieldDeclaration>,
  expression: <HLExpression>,
}

HLStoreArrayStatement <- <HLStoreStatement> {
  declaration: <HLArrayFieldDeclaration>,
  index: <HLExpression>,
  expression: <HLExpression>,
}

HLCallStatement <- <HLStatement> {
  call: HLCallExprression,
}

HLIfStatement <- <HLStatement> {
  condition: <HLExpression>,
  body: HLBlock,
  other: HLBlock?,
}

HLForStatement <- <HLStatement> {
  initial: <HLStoreStatement>,
  condition: <HLExpression>,
  update: <HLStoreStatement>,
  body: HLBlock,
}

HLWhileStatement <- <HLStatement> {
  condition: <HLExpression>,
  body: HLBlock,
}

HLReturnStatement <- <HLStatement> {
  expression: <HLExpression>?,
}

HLBreakStatement <- <HLStatement> { }

HLContinueStatement <- <HLStatement> { }
```

## Expressions

```
<HLArgument> <- <HLNode>
<HLExpression> <- <HLArgumen>

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

HLLoadScalarExpression <- <HLExpression> {
  declaration: <HLScalarFieldDeclaration>,
}

HLLoadArrayExpression <- <HLExpression> {
  declaration: <HLArrayFieldDeclaration>,
  index: <HLExpression>,
}

HLCallExpression <- <HLExpression> {
  declaration: HLMethodDeclaration,
  arguments: [<HLArgument>],
}

HLLengthExpression <- <HLExpression> {
  declaration: <HLArrayDeclaration>,
}
```

## Literals

```
HLIntegerLiteral <- <HLExpression> {
  value: long,
}

HLStringLiteral <- <HLArgument> {
  declaration: HLStringLiteralDeclaration,
}
```
