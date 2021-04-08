# High-Level Intermediate Representation Specification

```
<HLNode>
```

## Declarations

```
HLProgram <- <HLNode> {
  importDeclarations: [HLImportDeclaration],
  scalarFieldDeclarations: [HLGlobalScalarFieldDeclaration],
  arrayFieldDeclarations: [HLGlobalArrayFieldDeclaration],
  stringLiteralDeclarations: [HLStringLiteralDeclaration],
  methodDeclarations: [HLMethodDeclaration],
}

HLImportDeclaration <- <HLNode> {
  identifier: String,
}

<HLScalarFieldDeclaration> <- <HLNode>

HLGlobalScalarFieldDeclaration <- <HLScalarFieldDeclaration> {
  identifier: String,
}

HLArgumentDeclaration <- <HLScalarFieldDeclaration> {
  index: int,
}

HLLocalScalarFieldDeclaration <- <HLScalarFieldDeclaration> {
  index: int,
}

<HLArrayFieldDeclaration> <- <HLNode>

HLGlobalArrayFieldDeclaration <- <HLArrayFieldDeclaration> {
  identifier: String,
  length: long,
}

HLLocalArrayFieldDeclaration <- <HLArrayFieldDeclaration> {
  index: int,
  length: long,
}

HLStringLiteralDeclaration <- <HLNode> {
  index: int,
  value: String,
}

HLMethodDeclaration <- <HLNode> {
  identifier: String,
  body: HLBlock,
}

HLBlock <- <HLNode> {
  argumentDeclarations: [HLArgumentDeclaration],
  scalarFieldDeclarations: [HLLocalScalarFieldDeclaration],
  arrayFieldDeclarations: [HLLocalArrayFieldDeclaration],
  statements: [<HLStatement>],
}
```

## Statements

```
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

HLStoreArrayCompoundStatement <- <HLStoreStatement> {
  declaration: <HLArrayFieldDeclaration>,
  index: <HLExpression>,
  type: ADD | SUBTRACT,
  expression: <HLExpression>,
}

HLCallStatement <- <HLStatement> {
  call: HLCallExpression,
}

HLIfStatement <- <HLStatement> {
  condition: <HLExpression>,
  body: HLBlock,
  other: HLBlock?,
}

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

HLReturnStatement <- <HLStatement> {
  expression: <HLExpression>?,
}

HLBreakStatement <- <HLStatement> { }

HLContinueStatement <- <HLStatement> { }
```

## Expressions

```
<HLArgument> <- <HLNode>

<HLExpression> <- <HLArgument>

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
  type: NOT | NEGATE | INCREMENT | DECREMENT,
  expression: <HLExpression>,
}

HLLoadScalarExpression <- <HLExpression> {
  declaration: <HLScalarFieldDeclaration>,
}

HLLoadArrayExpression <- <HLExpression> {
  declaration: <HLArrayFieldDeclaration>,
  index: <HLExpression>,
}

<HLCallExpression> <- <HLExpression>

HLInternalCallExpression <- <HLCallExpression> {
  declaration: HLMethodDeclaration,
  arguments: [<HLExpression>],
}

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
HLIntegerLiteral <- <HLExpression> {
  value: long,
}

HLStringLiteral <- <HLArgument> {
  declaration: HLStringLiteralDeclaration,
}
```

## Symbol Table

```
HLSymbolTable {
  parent: HLSymbolTable?,
  importDeclarations: { String => HLImportDeclaration },
  scalarFieldDeclarations: { String => <HLScalarFieldDeclaration> },
  arrayFieldDeclarations: { String => <HLArrayFieldDeclaration> },
  stringLiteralDeclarations: { String => HLStringLiteralDeclaration },
  methodDeclarations: { String => HLMethodDeclaration },
}
```
