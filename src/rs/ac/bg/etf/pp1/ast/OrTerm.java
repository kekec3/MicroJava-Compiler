// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class OrTerm extends ExprOr {

    private ExprAnd ExprAnd;

    public OrTerm (ExprAnd ExprAnd) {
        this.ExprAnd=ExprAnd;
        if(ExprAnd!=null) ExprAnd.setParent(this);
    }

    public ExprAnd getExprAnd() {
        return ExprAnd;
    }

    public void setExprAnd(ExprAnd ExprAnd) {
        this.ExprAnd=ExprAnd;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprAnd!=null) ExprAnd.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprAnd!=null) ExprAnd.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprAnd!=null) ExprAnd.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("OrTerm(\n");

        if(ExprAnd!=null)
            buffer.append(ExprAnd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [OrTerm]");
        return buffer.toString();
    }
}
