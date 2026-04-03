// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class RelTerm extends ExprRel {

    private ExprAdd ExprAdd;

    public RelTerm (ExprAdd ExprAdd) {
        this.ExprAdd=ExprAdd;
        if(ExprAdd!=null) ExprAdd.setParent(this);
    }

    public ExprAdd getExprAdd() {
        return ExprAdd;
    }

    public void setExprAdd(ExprAdd ExprAdd) {
        this.ExprAdd=ExprAdd;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprAdd!=null) ExprAdd.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprAdd!=null) ExprAdd.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprAdd!=null) ExprAdd.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("RelTerm(\n");

        if(ExprAdd!=null)
            buffer.append(ExprAdd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [RelTerm]");
        return buffer.toString();
    }
}
