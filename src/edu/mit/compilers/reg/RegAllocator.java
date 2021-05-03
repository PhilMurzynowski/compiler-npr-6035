package edu.mit.compilers.reg;

import java.util.*;

import edu.mit.compilers.ll.*;

public class RegAllocator {

  private static boolean update(final LLBasicBlock block, final List<Map<LLDeclaration, Set<Chain>>> intermediaries) {
    final List<LLInstruction> instructions = block.getInstructions();

    final Map<LLDeclaration, Set<Chain>> oldEntry = new HashMap<>();
    for (final Map.Entry<LLDeclaration, Set<Chain>> entry : intermediaries.get(0).entrySet()) {
      oldEntry.put(entry.getKey(), new HashSet<>(entry.getValue()));
    }

    for (int i = instructions.size() - 1; i >= 0; i--) {
      final Map<LLDeclaration, Set<Chain>> above = intermediaries.get(i);
      final Map<LLDeclaration, Set<Chain>> below = intermediaries.get(i + 1);

      above.clear();
      for (final Map.Entry<LLDeclaration, Set<Chain>> entry : below.entrySet()) {
        above.put(entry.getKey(), new HashSet<>(entry.getValue()));
      }

      final LLInstruction instruction = instructions.get(i);

      if (instruction.definition().isPresent()) {
        final LLDeclaration definition = instruction.definition().get();
        above.remove(definition);
      }

      for (final LLDeclaration use : instruction.uses()) {
        final boolean include = use instanceof LLAliasDeclaration
          || use instanceof LLLocalScalarFieldDeclaration
          || use instanceof LLArgumentDeclaration; 
        if (include) { 
          if(!above.containsKey(use)) {
            above.put(use, new HashSet<>());
          }
          above.get(use).add(new Chain());
        }
      }
    }

    final Map<LLDeclaration, Set<Chain>> newEntry = intermediaries.get(0);

    return !newEntry.keySet().equals(oldEntry.keySet());
  }

  private static void transform(final LLBasicBlock block, final List<Map<LLDeclaration, Set<Chain>>> intermediaries) {
    final List<LLInstruction> instructions = block.getInstructions();

    for (int i = instructions.size() - 1; i >= 0; i--) {
      final Map<LLDeclaration, Set<Chain>> above = intermediaries.get(i);
      final Map<LLDeclaration, Set<Chain>> below = intermediaries.get(i + 1);

      // set chains as interfering
      List<LLDeclaration> declarations = new ArrayList<>(below.keySet());
      for (int j = 0; j < declarations.size(); j++) {
        for (int k = j+1; k < declarations.size(); k++) {
          for (Chain jChain : below.get(declarations.get(j))) {
            for (Chain kChain : below.get(declarations.get(k))) {
              jChain.addInterference(kChain);
              kChain.addInterference(jChain);
            }
          }
        }
      }

      final LLInstruction instruction = instructions.get(i);

      if (instruction.definition().isPresent()) {
        final LLDeclaration definition = instruction.definition().get();

        if (below.containsKey(definition)) {
          instruction.setDefinitionWeb(below.get(definition).iterator().next().getWeb());
        }
      }

      for (final LLDeclaration use : instruction.uses()) {
        if (above.containsKey(use)) {
          instruction.addUsesWeb(use, above.get(use).iterator().next().getWeb());
        }
      }
    }
    // set chains as interfering
    final Map<LLDeclaration, Set<Chain>> above = intermediaries.get(0);
    List<LLDeclaration> declarations = new ArrayList<>(above.keySet());
    for (int i = 0; i < declarations.size(); i++) {
      for (int j = 0; j < declarations.size(); j++) {
        for (Chain iChain : above.get(declarations.get(i))) {
          for (Chain jChain : above.get(declarations.get(j))) {
            iChain.addInterference(jChain);
            jChain.addInterference(iChain);
          }
        }
      }
    }
  }

  private static void union(final Map<LLDeclaration, Set<Chain>> left, final Map<LLDeclaration, Set<Chain>> right) {
    for (final LLDeclaration declaration : right.keySet()) {
      if (!left.containsKey(declaration)) {
        left.put(declaration, new HashSet<>());
      }
      left.get(declaration).addAll(right.get(declaration));
    }
  }

  private static void unionFind(final Map<LLBasicBlock, List<Map<LLDeclaration, Set<Chain>>>> chains) {
    for (final LLBasicBlock block : chains.keySet()) {
      for (final Map<LLDeclaration, Set<Chain>> intermediary : chains.get(block)) {
        for (final LLDeclaration declaration : intermediary.keySet()) {
          final Chain first = intermediary.get(declaration).iterator().next();
          for (final Chain chain : intermediary.get(declaration)) {
            first.union(chain);
          }
        }
      }
    }
    for (final LLBasicBlock block : chains.keySet()) {
      for (final Map<LLDeclaration, Set<Chain>> intermediary : chains.get(block)) {
        for (final LLDeclaration declaration : intermediary.keySet()) {
          for (final Chain chain : intermediary.get(declaration)) {
            final Chain chainSet = chain.find();
            if (!chainSet.hasWeb()) {
              chainSet.setWeb(new Web());
            }
          }
        }
      }
    }
  }

  private static Set<Web> collectWebs(final Map<LLBasicBlock, List<Map<LLDeclaration, Set<Chain>>>> chains) {
    Set<Web> webs = new HashSet<>();
    for (final LLBasicBlock block : chains.keySet()) {
      for (final Map<LLDeclaration, Set<Chain>> intermediary : chains.get(block)) {
        for (final LLDeclaration declaration : intermediary.keySet()) {
          for (final Chain chain : intermediary.get(declaration)) {
            webs.add(chain.getWeb());
          }
        }
      }
    }
    return webs;
  }

  private static Map<Web, Set<Web>> interferenceFind(final Map<LLBasicBlock, List<Map<LLDeclaration, Set<Chain>>>> chains) {
    final Map<Web, Set<Web>> interference = new HashMap<>();

    // initialize with no interference for all
    for (Web web : collectWebs(chains)) {
      interference.put(web, new HashSet<>());
    }

    for (final LLBasicBlock block : chains.keySet()) {
      for (final Map<LLDeclaration, Set<Chain>> intermediary : chains.get(block)) {
        for (final LLDeclaration declaration : intermediary.keySet()) {
          for (final Chain chain : intermediary.get(declaration)) {
            final Web firstWeb = chain.getWeb();
            for (Chain interferingChain : chain.getInterference()) {
              final Web secondWeb = interferingChain.getWeb();
              interference.get(firstWeb).add(secondWeb);
            }
          }
        }
      }
    }

    return interference;
  }

  // Chaitin's algorithm
  private static void color(final Map<Web, Set<Web>> originalInterference, List<String> colors) {
    final Map<Web, Set<Web>> currentInterference = new HashMap<>();
    for (final Map.Entry<Web, Set<Web>> entry : originalInterference.entrySet()) {
      currentInterference.put(entry.getKey(), new HashSet<>(entry.getValue()));
    }

    final Stack<Web> stack = new Stack<>();

    while (!currentInterference.isEmpty()) {
      boolean anyRemoved = false;
      for (final Web current : Set.copyOf(currentInterference.keySet())) {
        if (currentInterference.get(current).size() < colors.size()) {
          stack.push(current);
          System.err.println("web " + current.getIndex() + " added to stack");

          currentInterference.remove(current);
          for (final Set<Web> webs : currentInterference.values()) {
            webs.remove(current);
          }

          anyRemoved = true;
        }
      }

      if (!anyRemoved) {
        int maxDegree = Integer.MIN_VALUE;
        Web maxDegreeWeb = null;

        for (final Map.Entry<Web, Set<Web>> entry : currentInterference.entrySet()) {
          final int degree = entry.getValue().size();
          if (degree > maxDegree) {
            maxDegree = degree;
            maxDegreeWeb = entry.getKey();
          }
        }

        assert maxDegreeWeb != null : "currentInterference should not be empty";

        currentInterference.remove(maxDegreeWeb);
        for (final Set<Web> webs : currentInterference.values()) {
          webs.remove(maxDegreeWeb);
        }
        maxDegreeWeb.setLocation(Web.SPILL);
      }
    }

    while (!stack.isEmpty()) {
      final Web current = stack.pop();
      System.err.println("web " + current.getIndex() + " popped from stack");

      final Set<String> neighborLocations = new HashSet<>();
      for (final Web neighbor : originalInterference.get(current)) {
        if (neighbor.hasLocation()) {
          neighborLocations.add(neighbor.getLocation());
        }
      }

      for (final String color : colors) {
        if (!neighborLocations.contains(color)) {
          current.setLocation(color);
          break;
        }
      }

      assert current.hasLocation() : "web should have been colored by now";
    }
  }

  public static void apply(final LLControlFlowGraph controlFlowGraph) {
    final Map<LLBasicBlock, List<Map<LLDeclaration, Set<Chain>>>> chains = new HashMap<>();

    final Set<LLBasicBlock> workSet = new LinkedHashSet<>();
    final Set<LLBasicBlock> visited = new LinkedHashSet<>();

    // Initialize the exit block's chains
    if (controlFlowGraph.hasExit()) {
      final LLBasicBlock exit = controlFlowGraph.expectExit();
      final int n = exit.getInstructions().size() + 1;
      chains.put(exit, new ArrayList<>(n));
      for (int i = 0; i < n; i++) {
        chains.get(exit).add(new HashMap<>());
      }

      workSet.addAll(controlFlowGraph.expectExit().getPredecessors());
      visited.add(controlFlowGraph.expectExit());
    }

    // Initialize the exception blocks' chains
    for (final LLBasicBlock exception : controlFlowGraph.getExceptions()) {
      final int n = exception.getInstructions().size() + 1;
      chains.put(exception, new ArrayList<>(n));
      for (int i = 0; i < n; i++) {
        chains.get(exception).add(new HashMap<>());
      }

      workSet.addAll(exception.getPredecessors());
      visited.add(exception);
    }

    // Initialize all blocks' chains
    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      if (!visited.contains(block)) {
        final int n = block.getInstructions().size() + 1;
        chains.put(block, new ArrayList<>(n));
        for (int i = 0; i < n; i++) {
          chains.get(block).add(new HashMap<>());
        }

        workSet.addAll(block.getPredecessors());

        visited.add(block);
      }
    }

    // Set all blocks as to-be-visited
    workSet.addAll(visited);

    // Update entry/exit chains for all basic blocks
    while (!workSet.isEmpty()) {
      final LLBasicBlock block = workSet.iterator().next();
      workSet.remove(block);

      // Only update predecessors if entry changes
      if (update(block, chains.get(block))) {
        for (final LLBasicBlock predecessor : block.getPredecessors()) {
          union(chains.get(predecessor).get(chains.get(predecessor).size() - 1), chains.get(block).get(0));

          // Add all predecessors to work set
          workSet.add(predecessor);
        }
      }
    }

    unionFind(chains);

    // Now actually add webs to instructions
    for (final LLBasicBlock block : visited) {
      transform(block, chains.get(block));
    }

    final Map<Web, Set<Web>> interference = interferenceFind(chains);

    System.err.println("Interference graph:");
    for (Web web: interference.keySet()) {
      System.err.print(web.getIndex() + " : ");
      for (Web interferes : interference.get(web)) {
        System.err.print(interferes.getIndex() + ", ");
      }
      System.err.print("\n");
    }

    final List<String> colors = List.of(
      //Registers.RAX,
      Registers.RBX,
      Registers.RCX,
      Registers.RDX,
      Registers.RDI,
      Registers.RSI,
      Registers.R8,
      Registers.R9,
      //Registers.R10,
      Registers.R11,
      Registers.R12,
      Registers.R13,
      Registers.R14,
      Registers.R15
    );
    color(interference, colors);
  }

}
