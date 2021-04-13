package edu.mit.compilers.opt;

import edu.mit.compilers.ll.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class CSETable {

  private Map<LLDeclaration, String> mapVarToVal = new HashMap<>();
  private Map<String, String> mapExprToVal = new HashMap<>();
  private Map<String, LLDeclaration> mapExprToTmp = new HashMap<>();

  private int valCount;
  private int tmpCount;

  public CSETable() {

  }

  // Adds var to value mapping if does not exist and return value
  public String varToVal(LLDeclaration llVar) {
    if (mapVarToVal.containsKey(llVar)) {
      return mapVarToVal.get(llVar);
    } else {
      String val = "v"+valCount;
      mapVarToVal.put(llVar, val);
      this.valCount++;
      return val;
    }
  }

  public String exprToVal(String expr) {
    if (mapExprToVal.containsKey(expr)) {
      return mapExprToVal.get(expr);
    } else {
      String val = "v"+valCount;
      mapExprToVal.put(expr, val);
      this.valCount++;
      return val;
    }
  }

}
