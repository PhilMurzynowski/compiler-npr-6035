# Phase 5 Documentation

**N**oah Pauls, **P**hilip Murzynowski, **R**obert Durfee

## Design

So far, we have implemented the following optimizations:


- Global common subexpression elimination (`--opt=cse`)
- Global copy propagation (`--opt=cp`)
- Global constant folding (`--opt=cf`)
- Algebraic simplification (`--opt=as`)
- Dead code elimination (`--opt=dce`)
- Unused local elimination (`--opt=ule`)
- Unreachable code elimination (`--opt=uce`)
- Function inlining (`--opt=fi`)

You can enable all optimizations with `--opt=all`. They are applied in the following order:

1. Function inlining
2. Global copy propagation, global constant folding, algebraic simplification
3. Global common subexpression elimination
4. Global copy propagation, global constant folding, algebraic simplification
5. Dead code elimination
6. Unused local elimination
7. Unreachable code elimination

Note: [CP][CP] is called twice given that our code generation naively creates a lot of copies that make global [CSE][CSE] much less useful. However, [CSE][CSE] also exposes a lot of copies which we want to eliminate, too. Hence the [CP][CP] sandwich around [CSE][CSE].

### Global Common Subexpression Elimination

The global common subexpression elimination algorithm has two components, first, a global fixed-point working set approach computing the available expressions across basic blocks in a control flow graph, and second, a local value numbering based scheme for each basic block.

The global component is an optimistic forward data flow analysis, initially stating all blocks except for the entry block have all expressions available. To determine the availability of a given expression, a bitmap mapping a unique string describing an expression to a bit, 0 or 1, is used. In our low level intermediate representation this simplifies to binary, unary, load and store instructions

[CSE]: #global-common-subexpression-elimination

### Global Copy Propagation

The global propagation algorithm closely follows the one discussed in lecture. First, all reaching definitions are calculated by using a fixed-point working set approach. The working set initially holds all blocks. When any bitsets are changed, the successors are added to the working set.

Within each basic block, care must be taken with global variables. If we were being most conservative, we would not propagate any global variables as it is possible for a function call to happen at any point and modify a global variable. However, we found this to be too limiting as internal function calls are fairly rare and our naive code generation is pretty bad without any copy propagation. As a result, whenever an internal function call is encountered, all global definitions are reset to their identities so that globals are not propagated across function boundaries.

After all the reaching definitions are propagated, the transformation takes place. The transformation follows a fixed-point approach. However, whenever there is a change in any block, all blocks are traversed again. This accounts for the fact that a change in one block could potentially affect any of its descendants, not just its direct children.

[CP]: #global-copy-propagation

#### Global Constant Folding

Within copy propagation, if at any point a binary or unary operator is visited that has all operands as constants, that instruction is replaced with the compile-time evaluation of that expression. If the evaluation yields a divide-by-zero exception, that exception replaces the expression instead.

[CF]: #global-constant-folding

#### Algebraic Simplification

Within copy propagation, the following algebraic simplifications are made:

- `a + 0, 0 + a => a`
- `a * 1, 1 * a => a`
- `a * 0, 0 * a => 0`
- `a / 1 => a`
- `a / 0 => DivideByZeroException`
- `0 / a => 0`
- `a % 1 => 0`
- `a % 0 => DivideByZeroException`
- `0 % a => 0`
- `0 - a => -a`
- `a - 0 => a`
- `a && true, true && a => a`
- `a && false, false && a => false`
- `a || true, true || a => true`
- `a || false, false || a => a`
- `a * 2^n, 2^n * a => a << n`

Note: The boolean simplifications are less helpful due to short circuiting as these operations do not appear directly in our LLIR. However, [UCE][UCE] should take care of this instead.

[AS]: #algebraic-simplification

### Dead Code Elimination

TODO

[DCE]: #dead-code-elimination

#### Unused Local Elimination

We sought to eliminate unused local variables primarily because previous optimizations generated a huge number of new local declarations. [CSE][CSE], for instance, created a new local for every expression it attempted to eliminate, unnecessarily enlarging the stack with unused locals.

Within a single method, all uses of locals are collected. If a local present in the method’s list of locals is unused, that local is eliminated from the method and is not given space on the stack when the method is called. The exception to this is locals responsible for storing the “result” of void methods. While these are not technically used, they must be allocated for the function call to occur.

[ULE]: #unused-local-elimination

### Unreachable Code Elimination

After [CF][CF], there are a large number of conditional branches where the comparison is between two constants. When simplifying the control flow graph, these branches are reduced to unconditional jumps and the unreachable blocks are automatically garbage-collected.

[UCE]: #unreachable-code-elimination

### Function Inlining

While all other optimizations occur on the LLIR control flow graph, function inlining operates on the HLIR tree. It first passes over all methods estimating the number of instructions in each method which is considered the inline “cost”. Then, any function call with a cost below the threshold is labeled to be inlined.

When the LLIR control flow graph is being constructed, any function calls that are labeled to be inlined are copied directly into the control flow graph. The arguments are mapped to their corresponding temporaries and the local variable hoisting mechanism is easily reused. Whenever a return statement is encountered, the control flow is redirected to a single return target (like break and continue were handled previously).

[FI]: #function-inlining

## Extras

Instead of just implementing one of [CSE][CSE], [CP][CP], and [DCE][DCE], we decided to implement all of them and add [CF][CF], [AS][AS], [ULE][ULE], [UCE][UCE], and [FI][FI] as described above.


## Difficulties

The control flow graph simplification process is recursive. For very large programs, the large number of basic blocks results in stack overflow in our compiler due to the deep recursion (it is not an infinite loop). We will convert the process to be iterative to avoid this problem.

We currently do not use [CSE][CSE] to the fullest extent possible. For example, consider the following:
```
int a[100], i;
for (i = 0; i < 100; i++) {
  a[i] = 0;
}
```
In this code, the access `a[i]` can never be out of bounds because `i < 100` is available. Thus, the array out-of-bounds check `i < 100` is always true. We plan to make use of this in the future by replacing conditional jumps with unconditional ones and allowing [UCE][UCE] to take care of the rest.

We currently generate methods whether or not they are called. Now that we are inlining a lot of functions, we can clean up a lot of uncalled methods.

Both [CSE][CSE] and [CP][CP] do not currently consider array accesses. However, it should be possible to extend both to perform [CSE][CSE] and [CP][CP] on array accesses with constant indexing. This seems to be relatively common in some of the benchmarks.

We currently do not do any loop dataflow optimizations. We would like to implement some limited form of the following:


- Loop invariant code hoisting
- Loop unrolling
- Loop vectorization

Right now, [CSE][CSE] and [CP][CP] are pretty conservative with globals. While we are not simply ignoring globals, instead resetting state upon internal function calls, this is still conservative as it assumes all globals are affected by every function call, which is unreasonable. When we do more analysis to remove uncalled methods, we plan to use this information to see which globals could be affected by a function call.

## Contributions

We largely worked on different optimizations independently and debugged larger issues collaboratively (e.g. [CSE][CSE] and [CP][CP] relating to globals). Noah worked on [DCE][DCE] and [ULE][ULE], Philip worked on [CSE][CSE], and Robert worked on [CP][CP] (with [CF][CF] and [AS][AS]), [UCE][UCE], and [FI][FI].
