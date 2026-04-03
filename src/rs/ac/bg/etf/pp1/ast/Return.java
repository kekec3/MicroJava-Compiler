// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class Return extends Statement {

    private RetExpr RetExpr;

    public Return (RetExpr RetExpr) {
        this.RetExpr=RetExpr;
        if(RetExpr!=null) RetExpr.setParent(this);
    }

    public RetExpr getRetExpr() {
        return RetExpr;
    }

    public void setRetExpr(RetExpr RetExpr) {
        this.RetExpr=RetExpr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(RetExpr!=null) RetExpr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(RetExpr!=null) RetExpr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(RetExpr!=null) RetExpr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Return(\n");

        if(RetExpr!=null)
            buffer.append(RetExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Return]");
        return buffer.toString();
    }
}
