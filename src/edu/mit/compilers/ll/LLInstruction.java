package edu.mit.compilers.ll;

import java.util.Optional;
import java.util.List;

import edu.mit.compilers.reg.Web;

public interface LLInstruction extends LLNode {

  public List<LLDeclaration> uses();
  public Optional<LLDeclaration> definition();
  public LLInstruction usesReplaced(List<LLDeclaration> uses);
  public String getUniqueExpressionString();
  public void setDefinitionWeb(Web web);
  public void addUsesWeb(LLDeclaration declaration, Web web);
  public String getDefWebLocation();
  public boolean defInRegister();
  public String getUseWebLocation(LLDeclaration use);
  public boolean useInRegister(LLDeclaration use);

}

