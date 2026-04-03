// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class SingleEnumElem extends EnumList {

    private String enumElem;
    private EnumVal EnumVal;

    public SingleEnumElem (String enumElem, EnumVal EnumVal) {
        this.enumElem=enumElem;
        this.EnumVal=EnumVal;
        if(EnumVal!=null) EnumVal.setParent(this);
    }

    public String getEnumElem() {
        return enumElem;
    }

    public void setEnumElem(String enumElem) {
        this.enumElem=enumElem;
    }

    public EnumVal getEnumVal() {
        return EnumVal;
    }

    public void setEnumVal(EnumVal EnumVal) {
        this.EnumVal=EnumVal;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(EnumVal!=null) EnumVal.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumVal!=null) EnumVal.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumVal!=null) EnumVal.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleEnumElem(\n");

        buffer.append(" "+tab+enumElem);
        buffer.append("\n");

        if(EnumVal!=null)
            buffer.append(EnumVal.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleEnumElem]");
        return buffer.toString();
    }
}
