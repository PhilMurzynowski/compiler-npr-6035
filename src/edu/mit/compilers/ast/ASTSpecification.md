# Abstract Syntax Tree Specification

```
<Node>
```

## Program

```
Program <- <Node> {
  importDeclarations: [ImportDeclaration],
  fieldDeclarations: [FieldDeclaration],
  methodDeclarations: [MethodDeclaration],
}
```

## Declarations

```
ImportDeclaration <- <Node> {
  identifier: String,
}

FieldDeclaration <- <Node> {
  type: INTEGER | BOOLEAN,
  identifiers: [(identifier: String, length: IntegerLiteral?)],
}

MethodDeclaration <- <Node> {
  type: INTEGER | BOOLEAN | VOID,
  identifier: String,
  arguments: [(type: INTEGER | BOOLEAN, identifier: String)],
  block: Block,
}
```

## Block

```
Block <- <Node> {
  fieldDeclarations: [FieldDeclaration],
  statements: [<Statement>],
}
```

## Statements

```
<Statement> <- <Node>

IDAssignStatement <- <Statement> {
  identifier: String,
  expression: <Expression>,
}

AssignStatement <- <Statement> {
  location: LocationExpression,
  expression: <Expression>,
}

CompoundAssignStatement <- <Statement> {
  location: LocationExpression,
  type: ADD | SUBTRACT | INCREMENT | DECREMENT,
  expression: <Expression>?,
}

MethodCallStatement <- <Statement> {
  call: MethodCallExpression,
}

IfStatement <- <Statement> {
  condition: <Expression>,
  body: Block,
  other: Block?,
}

ForStatement <- <Statement> {
  initial: IDAssignStatement,
  condition: <Expression>,
  update: CompoundAssignStatement,
  body: Block,
}

WhileStatement <- <Statement> {
  condition: <Expression>,
  body: Block,
}

ReturnStatement <- <Statement> {
  expression: <Expression>?,
}

BreakStatement <- <Statement> { }

ContinueStatement <- <Statement> { }
```

## Expressions

```
<Argument> <- <Node>
<Expression> <- <Argument>

BinaryExpression <- <Expression> {
  left: <Expression>,
  type: OR | AND | EQUAL | NOT_EQUAL | LESS_THAN | LESS_THAN_OR_EQUAL | GREATER_THAN | GREATER_THAN_OR_EQUAL | ADD | SUBTRACT | MULTIPLY | DIVIDE | MODULUS,
  right: <Expression>,
}

UnaryExpression <- <Expression> {
  type: NOT | NEGATE,
  expression: <Expression>,
}

LocationExpression <- <Expression> {
  identifier: String,
  offset: <Expression>?,
}

MethodCallExpression <- <Expression> {
  identifier: String,
  arguments: [<Argument>],
}

LengthExpression <- <Expression> {
  identifier: String,
}
```

## Literals

```
IntegerLiteral <- <Expression> {
  value: long,
}

CharacterLiteral <- <Expression> {
  value: char,
}

BooleanLiteral <- <Expression> {
  value: boolean,
}

StringLiteral <- <Argument> {
  value: String,
}
```
