package edu.mit.compilers.opt;

import edu.mit.compilers.ll.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class CSETable {

  private Map<LLDeclaration, String> mapVarToVal = new HashMap<>();
  private Map<String, String> mapExpToVal = new HashMap<>();
  private Map<String, LLDeclaration> mapExpToTmp = new HashMap<>();

  private int varCount;
  private int exprCount;
  private int tmpCount;

  public CSETable() {

  }

  // Adds var to value mapping if does not exist and return value
  public String varToVal(LLDeclaration llVar) {
    if (mapVarToVal.get(llVar) != null) {
      return mapVarToVal.get(llVar);
    } else {
      String val = "v"+varCount;
      mapVarToVal.put(llVar, val);
      this.varCount++;
      return val;
    }
  }

}
