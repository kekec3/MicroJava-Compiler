// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class EnumValue extends EnumVal {

    private Integer enumValue;

    public EnumValue (Integer enumValue) {
        this.enumValue=enumValue;
    }

    public Integer getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(Integer enumValue) {
        this.enumValue=enumValue;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EnumValue(\n");

        buffer.append(" "+tab+enumValue);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [EnumValue]");
        return buffer.toString();
    }
}
