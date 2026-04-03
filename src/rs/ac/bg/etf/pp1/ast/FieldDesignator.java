// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class FieldDesignator extends Designator {

    private Designator Designator;
    private Field Field;

    public FieldDesignator (Designator Designator, Field Field) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.Field=Field;
        if(Field!=null) Field.setParent(this);
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public Field getField() {
        return Field;
    }

    public void setField(Field Field) {
        this.Field=Field;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
        if(Field!=null) Field.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
        if(Field!=null) Field.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        if(Field!=null) Field.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FieldDesignator(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Field!=null)
            buffer.append(Field.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FieldDesignator]");
        return buffer.toString();
    }
}
