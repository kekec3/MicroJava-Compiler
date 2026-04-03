// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class Ternary extends Expr {

    private ExprOr ExprOr;
    private TernaryStart TernaryStart;
    private ExprAdd ExprAdd;
    private TernaryElse TernaryElse;
    private ExprAdd ExprAdd1;

    public Ternary (ExprOr ExprOr, TernaryStart TernaryStart, ExprAdd ExprAdd, TernaryElse TernaryElse, ExprAdd ExprAdd1) {
        this.ExprOr=ExprOr;
        if(ExprOr!=null) ExprOr.setParent(this);
        this.TernaryStart=TernaryStart;
        if(TernaryStart!=null) TernaryStart.setParent(this);
        this.ExprAdd=ExprAdd;
        if(ExprAdd!=null) ExprAdd.setParent(this);
        this.TernaryElse=TernaryElse;
        if(TernaryElse!=null) TernaryElse.setParent(this);
        this.ExprAdd1=ExprAdd1;
        if(ExprAdd1!=null) ExprAdd1.setParent(this);
    }

    public ExprOr getExprOr() {
        return ExprOr;
    }

    public void setExprOr(ExprOr ExprOr) {
        this.ExprOr=ExprOr;
    }

    public TernaryStart getTernaryStart() {
        return TernaryStart;
    }

    public void setTernaryStart(TernaryStart TernaryStart) {
        this.TernaryStart=TernaryStart;
    }

    public ExprAdd getExprAdd() {
        return ExprAdd;
    }

    public void setExprAdd(ExprAdd ExprAdd) {
        this.ExprAdd=ExprAdd;
    }

    public TernaryElse getTernaryElse() {
        return TernaryElse;
    }

    public void setTernaryElse(TernaryElse TernaryElse) {
        this.TernaryElse=TernaryElse;
    }

    public ExprAdd getExprAdd1() {
        return ExprAdd1;
    }

    public void setExprAdd1(ExprAdd ExprAdd1) {
        this.ExprAdd1=ExprAdd1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprOr!=null) ExprOr.accept(visitor);
        if(TernaryStart!=null) TernaryStart.accept(visitor);
        if(ExprAdd!=null) ExprAdd.accept(visitor);
        if(TernaryElse!=null) TernaryElse.accept(visitor);
        if(ExprAdd1!=null) ExprAdd1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprOr!=null) ExprOr.traverseTopDown(visitor);
        if(TernaryStart!=null) TernaryStart.traverseTopDown(visitor);
        if(ExprAdd!=null) ExprAdd.traverseTopDown(visitor);
        if(TernaryElse!=null) TernaryElse.traverseTopDown(visitor);
        if(ExprAdd1!=null) ExprAdd1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprOr!=null) ExprOr.traverseBottomUp(visitor);
        if(TernaryStart!=null) TernaryStart.traverseBottomUp(visitor);
        if(ExprAdd!=null) ExprAdd.traverseBottomUp(visitor);
        if(TernaryElse!=null) TernaryElse.traverseBottomUp(visitor);
        if(ExprAdd1!=null) ExprAdd1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Ternary(\n");

        if(ExprOr!=null)
            buffer.append(ExprOr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernaryStart!=null)
            buffer.append(TernaryStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExprAdd!=null)
            buffer.append(ExprAdd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernaryElse!=null)
            buffer.append(TernaryElse.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExprAdd1!=null)
            buffer.append(ExprAdd1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Ternary]");
        return buffer.toString();
    }
}
