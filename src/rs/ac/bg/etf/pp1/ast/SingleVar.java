// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class SingleVar extends VarList {

    private String varName;
    private Arr Arr;

    public SingleVar (String varName, Arr Arr) {
        this.varName=varName;
        this.Arr=Arr;
        if(Arr!=null) Arr.setParent(this);
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName=varName;
    }

    public Arr getArr() {
        return Arr;
    }

    public void setArr(Arr Arr) {
        this.Arr=Arr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Arr!=null) Arr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Arr!=null) Arr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Arr!=null) Arr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleVar(\n");

        buffer.append(" "+tab+varName);
        buffer.append("\n");

        if(Arr!=null)
            buffer.append(Arr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleVar]");
        return buffer.toString();
    }
}
