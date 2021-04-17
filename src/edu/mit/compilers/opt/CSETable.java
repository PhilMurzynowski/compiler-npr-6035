package edu.mit.compilers.opt;

import edu.mit.compilers.ll.*;
import java.util.Map;
import java.util.HashMap;

public class CSETable {

  private final LLMethodDeclaration methodDeclaration;
  private Map<LLDeclaration, String> mapVarToVal = new HashMap<>();
  private Map<String, String> mapExprToVal = new HashMap<>();
  private Map<String, LLDeclaration> mapExprToTmp = new HashMap<>();

  private int valCount;

  public CSETable(LLMethodDeclaration methodDeclaration) {
    this.methodDeclaration = methodDeclaration;
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

  // Deviating from above pattern as exprToTmp more complicated

  public boolean inExprToTmp(String expr) {
    return mapExprToTmp.containsKey(expr);
  }

  public LLDeclaration addExprToTmp(String expr) {
    if (mapExprToTmp.containsKey(expr)) {
      throw new RuntimeException("Expr already in ExprToTmp");
    } else {
      LLAliasDeclaration tmp = methodDeclaration.newAlias();
      mapExprToTmp.put(expr, tmp);
      return tmp;
    }
  }

  public LLDeclaration getExprToTmp(String expr) {
    if (mapExprToTmp.containsKey(expr)) {
      return mapExprToTmp.get(expr);
    } else {
      throw new RuntimeException("Expr not in ExprToTmp");
    }
  }

}
