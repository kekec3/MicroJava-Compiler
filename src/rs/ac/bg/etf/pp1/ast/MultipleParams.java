// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class MultipleParams extends FormParamList {

    private FormParamList FormParamList;
    private Type Type;
    private String paramName;
    private Arr Arr;

    public MultipleParams (FormParamList FormParamList, Type Type, String paramName, Arr Arr) {
        this.FormParamList=FormParamList;
        if(FormParamList!=null) FormParamList.setParent(this);
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.paramName=paramName;
        this.Arr=Arr;
        if(Arr!=null) Arr.setParent(this);
    }

    public FormParamList getFormParamList() {
        return FormParamList;
    }

    public void setFormParamList(FormParamList FormParamList) {
        this.FormParamList=FormParamList;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName=paramName;
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
        if(FormParamList!=null) FormParamList.accept(visitor);
        if(Type!=null) Type.accept(visitor);
        if(Arr!=null) Arr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FormParamList!=null) FormParamList.traverseTopDown(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(Arr!=null) Arr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FormParamList!=null) FormParamList.traverseBottomUp(visitor);
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(Arr!=null) Arr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MultipleParams(\n");

        if(FormParamList!=null)
            buffer.append(FormParamList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+paramName);
        buffer.append("\n");

        if(Arr!=null)
            buffer.append(Arr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MultipleParams]");
        return buffer.toString();
    }
}
