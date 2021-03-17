# Semantic Checking

Listed here are the key design decision that impacted our semantic checking process.

## Key assumptions

We omitted a few checks that are possible to check, but are not required by the Decaf spec.

- While we check that individual integer literals are not out of bounds, mathematical expressions that could potentially be statically evaluated to out-of-bounds values are not checked.
    ```
    x = 9223372036854775807 + 1; // no error
    ```
- We ensure that a return statement matches the type of the enclosing function, but we do not verify that functions with non-void returns types always return that type. Thus the following does not throw any semantic error.
    ```
    int bad_function(int x) {
        if (x > 0) {
            return 1;
        }
        // no return if x <= 0
    }
    ```

## Immutability of AST

Though the AST was close to the structure of an IR tree, we wanted it to remain immutable, not augmenting it with IR information like symbol tables. During codegen, we will leverage a new tree derived from the AST to assist in codegen.

## Visitors

We used three visitors to perform semantic checks:

- [ProgramChecker][prog]: performs the vast majority of semantic checks, collects errors, and constructs symbol tables.
- [ExpressionChecker][expr]: used by ProgramChecker to get the return type of a full expression.
- [ArgumentChecker][arg]: *to be written*

[prog]: ../src/edu/mit/compilers/ir/ProgramChecker.java
[expr]: ../src/edu/mit/compilers/ir/ExpressionChecker.java
[arg]:  ../src/edu/mit/compilers/ir/ArgumentChecker.java

## Symbol table ADT

*to be written*