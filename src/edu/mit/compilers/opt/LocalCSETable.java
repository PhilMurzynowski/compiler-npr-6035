package edu.mit.compilers.opt;

import edu.mit.compilers.ll.*;
import java.util.Map;
import java.util.HashMap;

public class LocalCSETable {

  private final LLMethodDeclaration methodDeclaration;
  private Map<LLDeclaration, String> mapVarToVal = new HashMap<>();
  private Map<String, String> mapExprToVal = new HashMap<>();
  private Map<String, LLDeclaration> mapExprToTmp = new HashMap<>();

  private int valCount;

  public LocalCSETable(LLMethodDeclaration methodDeclaration) {
    this.methodDeclaration = methodDeclaration;
  }

  public boolean inVarToVal(LLDeclaration llvar) {
    return mapVarToVal.containsKey(llvar);
  }

  public String getVarToVal(LLDeclaration llVar) {
    if (mapVarToVal.containsKey(llVar)) {
      return mapVarToVal.get(llVar);
    } else {
      throw new RuntimeException("value for var not set");
    }
  }

  public String addVarToVal(LLDeclaration llVar) {
    String val = "v"+valCount;
    mapVarToVal.put(llVar, val);
    this.valCount++;
    return val;
  }

  public String mutateVarToVal(LLDeclaration llVar) {
    if (!mapVarToVal.containsKey(llVar)) {
      throw new RuntimeException("value for var not set");
    } else {
      String val = "v"+valCount;
      mapVarToVal.put(llVar, val);
      this.valCount++;
      return val;
    }
  }

  // Adds var to value mapping if does not exist and return value
  public String varToVal(LLDeclaration llVar) {
    if (inVarToVal(llVar)) {
      return getVarToVal(llVar);
    } else {
      return addVarToVal(llVar);
    }
  }

  // Set Var to specific Value
  public void setVarToVal(LLDeclaration llVar, String val) {
    mapVarToVal.put(llVar, val);
  }

  public String copyVarToVal(LLDeclaration llVar1, LLDeclaration llvar2) {

    if (!inVarToVal(llVar1)) {
      throw new RuntimeException("cannot copy var as not in table");
    }

    String val = getVarToVal(llVar1);
    setVarToVal(llvar2, val);

    return val;
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
