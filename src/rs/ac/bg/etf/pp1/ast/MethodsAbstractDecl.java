// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class MethodsAbstractDecl extends AbstractClassMethodList {

    private AbstractClassMethodList AbstractClassMethodList;
    private MethodOrAbstract MethodOrAbstract;

    public MethodsAbstractDecl (AbstractClassMethodList AbstractClassMethodList, MethodOrAbstract MethodOrAbstract) {
        this.AbstractClassMethodList=AbstractClassMethodList;
        if(AbstractClassMethodList!=null) AbstractClassMethodList.setParent(this);
        this.MethodOrAbstract=MethodOrAbstract;
        if(MethodOrAbstract!=null) MethodOrAbstract.setParent(this);
    }

    public AbstractClassMethodList getAbstractClassMethodList() {
        return AbstractClassMethodList;
    }

    public void setAbstractClassMethodList(AbstractClassMethodList AbstractClassMethodList) {
        this.AbstractClassMethodList=AbstractClassMethodList;
    }

    public MethodOrAbstract getMethodOrAbstract() {
        return MethodOrAbstract;
    }

    public void setMethodOrAbstract(MethodOrAbstract MethodOrAbstract) {
        this.MethodOrAbstract=MethodOrAbstract;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbstractClassMethodList!=null) AbstractClassMethodList.accept(visitor);
        if(MethodOrAbstract!=null) MethodOrAbstract.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbstractClassMethodList!=null) AbstractClassMethodList.traverseTopDown(visitor);
        if(MethodOrAbstract!=null) MethodOrAbstract.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbstractClassMethodList!=null) AbstractClassMethodList.traverseBottomUp(visitor);
        if(MethodOrAbstract!=null) MethodOrAbstract.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodsAbstractDecl(\n");

        if(AbstractClassMethodList!=null)
            buffer.append(AbstractClassMethodList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethodOrAbstract!=null)
            buffer.append(MethodOrAbstract.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodsAbstractDecl]");
        return buffer.toString();
    }
}
