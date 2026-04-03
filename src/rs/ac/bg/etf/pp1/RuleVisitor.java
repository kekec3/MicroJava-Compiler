package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;

public class RuleVisitor extends VisitorAdaptor{

	int printCallCount = 0;
	int varDeclCount = 0;
	
	Logger log = Logger.getLogger(getClass());

	public void visit(SingleVar sv){
		log.info("Stigo var: " + sv.getVarName());
		varDeclCount++;
	}
	
	public void visit(MultipleVars mv)
	{
		log.info("Stigo var: " + mv.getVarName());
		varDeclCount++;
	}
	
    public void visit(Print print) {
		//log.info("Stigao print: " + print.getExpr());
		printCallCount++;
	}

}
