# Code Generation

## Summary

### High Level IR (HLIR)

- new Symbol Table: we use a new symbol table type for the HLIR. This symbol table contains information relevant to locating different variables throughout scopes, making special distinctions between globals and locals, as well as between declared scalars and method arguments.

### Low Level IR (LLIR)
- 0 register allocation in LLIR: the LLIR is completely agnostic to the kind of location (register or memory) it is using to perform operations.
- Block labels: every basic block in the LLIR has its own label. When assembly is generated, these labels are added to the assembly code and can be referenced by conditionals where needed.
- Hoisting of variables: in the following example:
  ```
  void main() {
    int a, i;
    a = 2;
    for (i = 0; i < a; i++) {
      int a;
      a = i + 1;
    }
  }
  ```
  The variable declarations are hoisted to the top of the `main` block. The LLIR preserves the difference between the different `a` declarations because they are represented by different objects. However, since all variables are initialized to 0 at the time they are declared, those initializations are not hoisted. The code thus has the following analogue, which loosely translates to the LLIR:
  ```
  void main() {
    int a0, a1, i;
    a0 = 0;
    a0 = 2;
    for (i = 0; i < a; i++) {
      a1 = 0
      a1 = i + 1;
    }
  }
  ```
    