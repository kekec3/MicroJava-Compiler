// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class OrExpr extends Expr {

    private ExprOr ExprOr;

    public OrExpr (ExprOr ExprOr) {
        this.ExprOr=ExprOr;
        if(ExprOr!=null) ExprOr.setParent(this);
    }

    public ExprOr getExprOr() {
        return ExprOr;
    }

    public void setExprOr(ExprOr ExprOr) {
        this.ExprOr=ExprOr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprOr!=null) ExprOr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprOr!=null) ExprOr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprOr!=null) ExprOr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("OrExpr(\n");

        if(ExprOr!=null)
            buffer.append(ExprOr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [OrExpr]");
        return buffer.toString();
    }
}
