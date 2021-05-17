# Phase 5 Documentation

**N**oah Pauls, **P**hilip Murzynowski, **R**obert Durfee

## Design

In Phase 5, we implemented the following optimizations:

- Register Allocation (`--opt=reg`)
- Peephole Assembly Optimizations
- Optimized Pushes/Pops

Register allocation can be enabled with the `reg` flag, and is performed as the last step during assembly generation. Optimized pushes and pops occur by default during register allocation, as do peephole assembly optimizations.

### Register Allocation

Register allocation was a priority in our phase 5 optimizations as direct computation on registers is far more performant than keeping all of the data allocated on the stack and loading to and from registers each time computation is needed. 

We took a graph coloring approach, as after reviewing the linear scan algorithm we estimated that both approaches would be comparable amounts of working with limited code portability between the two approaches and we likely would not have time to try both, while in the literature we found that the linear scan algorithm could be around 20% slower than an aggressive coloring algorithm.

To perform the register allocation there were 5 main components: creating def-use chains from method control flow graphs, combining chains into webs assigning instructions to webs corresponding to the definitions and uses in an instruction, determining intersections between webs, and lastly assigning registers to specific webs (the coloring). To create def-use chains from the method control flow graphs we followed a similar approach to dead-code elimination, using a backwards analysis and identifying definition declarations with each use, and each use being identified in a method declaration by a triple formed of its basic block, instruction line, and index into list of uses in an instruction. To create webs we use a union find algorithm to group chains, which upon completion labels the sets of chains as a web, and then assigns instructions to the webs corresponding to their definitions and uses. After the webs have been created their interferences are determined by finding the interferences among consisting chains, and then they can be colored after an interference graph has been created, which is done using Chaitin Graph coloring algorithm and a simple heuristic is used to spill the web with with the greatest number of interferences.

#### Peephole Assembly Optimizations

Registers introduced the possibility of uses and defs for a single instruction utilizing the same register, leading to `movq` instructions where both operands were the same register (`movq %rax, %rax`). When generating move instructions, we eliminated any moves where both operands were the same location.

Our original assembly generator assumed that all operands originated in memory. Registers allowed us to reduce the number of instructions for various operations (such as `addq`) by checking if operands were present in registers at the beginning of the instruction. If they were, we could omit any instructions responsible for transferring a value from memory into a register before operating.

Because this optimization is part of what makes the results of register allocation efficient, it is included in the register allocation optimization by default.

#### Optimized Pushes/Pops

Register allocation required pushing/popping registers from the stack when making function calls. A naive approach was to push/pop all caller/callee saved registers for every call, but this turned out to be excessive; we improved this approach using the webs generated during register allocation, optimizing register pushes/pops at function calls using two methods.

First, only callee-saved registers that a function stores a new value in are pushed/popped. For each function, we find the webs associated with definitions in the function body. If those webs are colored with a callee-saved register, we know to store that register. Otherwise, the register isn’t stored.

Second, a caller-saved register is only stored if a web containing that register includes the relevant function call. During register allocation, we determine the active range of webs on a per-instruction basis, and we leveraged this information to determine what webs were active across function calls.

One optimization we did not perform was to omit the storing of caller-saved registers that the called function did not modify. However, this optimization proves complicated due to the presence of external calls whose modified caller-saved registers are not apparent, so we did not implement this optimization.

Because this optimization is part of what makes the results of register allocation efficient, it is included in the register allocation optimization by default.

## Extras

### Void method return value elision

Previously, all function calls were given a storage location for their return value (in the stack or otherwise). This applied to void methods, despite their not having a return value. Internal calls returning void are no longer given storage for return values, slightly saving used memory.

## Difficulties

There are no correctness bugs in our code of which we are aware. However, there is still plenty of room for optimization. Our register allocator only uses spills (no splits) and the spill heuristic is simply the highest degree node that has not yet been colored. 

Furthermore, we do not use precoloring as effectively as we could. Primarily, we use precoloring to place values in the appropriate function argument positions. For things like division operators, we simply ignore the `%rdx` register in allocation. 

Also, our LLIR does not reflect our assembly generation that immediately moves all function arguments into the stack. As a result, all arguments, even with register allocation, are still accessed via the stack as it proved cumbersome to handle this in the web coloring.

When coloring webs, we do not use `%rax` or `%r10`. These registers are reserved for assembly generation. Ideally, the coloring should be aware of this and no registers should be reserved.

## Contributions

All three of us worked collectively on register allocation in group meetings. Noah further improved the excessive pushes/pops associated with caller-/callee-saved registers and function calls.
