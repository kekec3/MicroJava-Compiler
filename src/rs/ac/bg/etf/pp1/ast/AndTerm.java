// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class AndTerm extends ExprAnd {

    private ExprRel ExprRel;

    public AndTerm (ExprRel ExprRel) {
        this.ExprRel=ExprRel;
        if(ExprRel!=null) ExprRel.setParent(this);
    }

    public ExprRel getExprRel() {
        return ExprRel;
    }

    public void setExprRel(ExprRel ExprRel) {
        this.ExprRel=ExprRel;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprRel!=null) ExprRel.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprRel!=null) ExprRel.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprRel!=null) ExprRel.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("AndTerm(\n");

        if(ExprRel!=null)
            buffer.append(ExprRel.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [AndTerm]");
        return buffer.toString();
    }
}
