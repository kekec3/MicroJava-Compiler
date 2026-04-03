// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class MultipleEnumElems extends EnumList {

    private EnumList EnumList;
    private String enumElem;
    private EnumVal EnumVal;

    public MultipleEnumElems (EnumList EnumList, String enumElem, EnumVal EnumVal) {
        this.EnumList=EnumList;
        if(EnumList!=null) EnumList.setParent(this);
        this.enumElem=enumElem;
        this.EnumVal=EnumVal;
        if(EnumVal!=null) EnumVal.setParent(this);
    }

    public EnumList getEnumList() {
        return EnumList;
    }

    public void setEnumList(EnumList EnumList) {
        this.EnumList=EnumList;
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
        if(EnumList!=null) EnumList.accept(visitor);
        if(EnumVal!=null) EnumVal.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumList!=null) EnumList.traverseTopDown(visitor);
        if(EnumVal!=null) EnumVal.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumList!=null) EnumList.traverseBottomUp(visitor);
        if(EnumVal!=null) EnumVal.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MultipleEnumElems(\n");

        if(EnumList!=null)
            buffer.append(EnumList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+enumElem);
        buffer.append("\n");

        if(EnumVal!=null)
            buffer.append(EnumVal.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MultipleEnumElems]");
        return buffer.toString();
    }
}
