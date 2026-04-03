// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class Allocation extends Factor {

    private Type Type;
    private ArrExpr ArrExpr;

    public Allocation (Type Type, ArrExpr ArrExpr) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.ArrExpr=ArrExpr;
        if(ArrExpr!=null) ArrExpr.setParent(this);
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public ArrExpr getArrExpr() {
        return ArrExpr;
    }

    public void setArrExpr(ArrExpr ArrExpr) {
        this.ArrExpr=ArrExpr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Type!=null) Type.accept(visitor);
        if(ArrExpr!=null) ArrExpr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(ArrExpr!=null) ArrExpr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(ArrExpr!=null) ArrExpr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Allocation(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ArrExpr!=null)
            buffer.append(ArrExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Allocation]");
        return buffer.toString();
    }
}
