# High-Level Intermediate Representation Specification

## Program

```
Rob
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
Rob
HLImportDeclaration {
  identifier: String,
}

Rob
<HLScalarFieldDeclaration> {
  getType(): INTEGER | BOOLEAN,
}

Rob
<HLArrayFieldDeclaration> {
  getType(): INTEGER | BOOLEAN,
}

Rob
HLGlobalScalarFieldDeclaration <- <HLScalarFieldDeclaration> {
  type: INTEGER | BOOLEAN,
  identifier: String,
}

Rob
HLGlobalArrayFieldDeclaration <- <HLArrayFieldDeclaration> {
  type: INTEGER | BOOLEAN,
  identifier: String,
  length: long,
}

Noah
HLStringLiteralDeclaration {
  index: long,
  value: String,
}

Phil
HLMethodDeclaration {
  identifier: String,
  body: HLBlock,
}

Rob
HLArgumentDeclaration <- <HLScalarFieldDeclaration> {
  type: INTEGER | BOOLEAN,
  index: long,
}

Noah
HLLocalScalarFieldDeclaration <- <HLScalarFieldDeclaration> {
  type: INTEGER | BOOLEAN,
  index: long, // location(): -(<NUM_ARGS> + <INDEX>)(%rbp)
}

Phil
HLLocalArrayFieldDeclaration <- <HLArrayFieldDeclaration> {
  index: long,
  length: long, // location(): -(<NUM_ARGS> + <NUM_SCALARS> + <SUM_OF_PREVIOUS>)(%rbp)
}
```

## Block

```
Rob
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

Rob
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

Rob
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

Rob
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

Rob
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

Rob
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

Rob
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

Rob
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
