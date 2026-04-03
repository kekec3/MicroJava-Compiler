// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class HasMethodsDeclaration extends MethodDeclList {

    private MethodList MethodList;

    public HasMethodsDeclaration (MethodList MethodList) {
        this.MethodList=MethodList;
        if(MethodList!=null) MethodList.setParent(this);
    }

    public MethodList getMethodList() {
        return MethodList;
    }

    public void setMethodList(MethodList MethodList) {
        this.MethodList=MethodList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MethodList!=null) MethodList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodList!=null) MethodList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodList!=null) MethodList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("HasMethodsDeclaration(\n");

        if(MethodList!=null)
            buffer.append(MethodList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [HasMethodsDeclaration]");
        return buffer.toString();
    }
}
