// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class RelationExpr extends ExprRel {

    private ExprAdd ExprAdd;
    private Relation Relation;
    private ExprAdd ExprAdd1;

    public RelationExpr (ExprAdd ExprAdd, Relation Relation, ExprAdd ExprAdd1) {
        this.ExprAdd=ExprAdd;
        if(ExprAdd!=null) ExprAdd.setParent(this);
        this.Relation=Relation;
        if(Relation!=null) Relation.setParent(this);
        this.ExprAdd1=ExprAdd1;
        if(ExprAdd1!=null) ExprAdd1.setParent(this);
    }

    public ExprAdd getExprAdd() {
        return ExprAdd;
    }

    public void setExprAdd(ExprAdd ExprAdd) {
        this.ExprAdd=ExprAdd;
    }

    public Relation getRelation() {
        return Relation;
    }

    public void setRelation(Relation Relation) {
        this.Relation=Relation;
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
        if(ExprAdd!=null) ExprAdd.accept(visitor);
        if(Relation!=null) Relation.accept(visitor);
        if(ExprAdd1!=null) ExprAdd1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprAdd!=null) ExprAdd.traverseTopDown(visitor);
        if(Relation!=null) Relation.traverseTopDown(visitor);
        if(ExprAdd1!=null) ExprAdd1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprAdd!=null) ExprAdd.traverseBottomUp(visitor);
        if(Relation!=null) Relation.traverseBottomUp(visitor);
        if(ExprAdd1!=null) ExprAdd1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("RelationExpr(\n");

        if(ExprAdd!=null)
            buffer.append(ExprAdd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Relation!=null)
            buffer.append(Relation.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ExprAdd1!=null)
            buffer.append(ExprAdd1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [RelationExpr]");
        return buffer.toString();
    }
}
