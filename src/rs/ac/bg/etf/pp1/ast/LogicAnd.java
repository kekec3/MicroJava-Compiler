// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class LogicAnd extends ExprAnd {

    private ExprAnd ExprAnd;
    private AndMarker AndMarker;
    private ExprRel ExprRel;

    public LogicAnd (ExprAnd ExprAnd, AndMarker AndMarker, ExprRel ExprRel) {
        this.ExprAnd=ExprAnd;
        if(ExprAnd!=null) ExprAnd.setParent(this);
        this.AndMarker=AndMarker;
        if(AndMarker!=null) AndMarker.setParent(this);
        this.ExprRel=ExprRel;
        if(ExprRel!=null) ExprRel.setParent(this);
    }

    public ExprAnd getExprAnd() {
        return ExprAnd;
    }

    public void setExprAnd(ExprAnd ExprAnd) {
        this.ExprAnd=ExprAnd;
    }

    public AndMarker getAndMarker() {
        return AndMarker;
    }

    public void setAndMarker(AndMarker AndMarker) {
        this.AndMarker=AndMarker;
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
        if(ExprAnd!=null) ExprAnd.accept(visitor);
        if(AndMarker!=null) AndMarker.accept(visitor);
        if(ExprRel!=null) ExprRel.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprAnd!=null) ExprAnd.traverseTopDown(visitor);
        if(AndMarker!=null) AndMarker.traverseTopDown(visitor);
        if(ExprRel!=null) ExprRel.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprAnd!=null) ExprAnd.traverseBottomUp(visitor);
        if(AndMarker!=null) AndMarker.traverseBottomUp(visitor);
        if(ExprRel!=null) ExprRel.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("LogicAnd(\n");

        if(ExprAnd!=null)
            buffer.append(ExprAnd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AndMarker!=null)
            buffer.append(AndMarker.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExprRel!=null)
            buffer.append(ExprRel.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [LogicAnd]");
        return buffer.toString();
    }
}
