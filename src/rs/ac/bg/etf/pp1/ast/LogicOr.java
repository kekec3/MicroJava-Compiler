// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class LogicOr extends ExprOr {

    private ExprOr ExprOr;
    private OrMarker OrMarker;
    private ExprAnd ExprAnd;

    public LogicOr (ExprOr ExprOr, OrMarker OrMarker, ExprAnd ExprAnd) {
        this.ExprOr=ExprOr;
        if(ExprOr!=null) ExprOr.setParent(this);
        this.OrMarker=OrMarker;
        if(OrMarker!=null) OrMarker.setParent(this);
        this.ExprAnd=ExprAnd;
        if(ExprAnd!=null) ExprAnd.setParent(this);
    }

    public ExprOr getExprOr() {
        return ExprOr;
    }

    public void setExprOr(ExprOr ExprOr) {
        this.ExprOr=ExprOr;
    }

    public OrMarker getOrMarker() {
        return OrMarker;
    }

    public void setOrMarker(OrMarker OrMarker) {
        this.OrMarker=OrMarker;
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
        if(ExprOr!=null) ExprOr.accept(visitor);
        if(OrMarker!=null) OrMarker.accept(visitor);
        if(ExprAnd!=null) ExprAnd.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprOr!=null) ExprOr.traverseTopDown(visitor);
        if(OrMarker!=null) OrMarker.traverseTopDown(visitor);
        if(ExprAnd!=null) ExprAnd.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprOr!=null) ExprOr.traverseBottomUp(visitor);
        if(OrMarker!=null) OrMarker.traverseBottomUp(visitor);
        if(ExprAnd!=null) ExprAnd.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("LogicOr(\n");

        if(ExprOr!=null)
            buffer.append(ExprOr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OrMarker!=null)
            buffer.append(OrMarker.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExprAnd!=null)
            buffer.append(ExprAnd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [LogicOr]");
        return buffer.toString();
    }
}
