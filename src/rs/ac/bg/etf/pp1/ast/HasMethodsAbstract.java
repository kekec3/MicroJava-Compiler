// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class HasMethodsAbstract extends AbstractClassMethods {

    private AbstractClassMethodList AbstractClassMethodList;

    public HasMethodsAbstract (AbstractClassMethodList AbstractClassMethodList) {
        this.AbstractClassMethodList=AbstractClassMethodList;
        if(AbstractClassMethodList!=null) AbstractClassMethodList.setParent(this);
    }

    public AbstractClassMethodList getAbstractClassMethodList() {
        return AbstractClassMethodList;
    }

    public void setAbstractClassMethodList(AbstractClassMethodList AbstractClassMethodList) {
        this.AbstractClassMethodList=AbstractClassMethodList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(AbstractClassMethodList!=null) AbstractClassMethodList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(AbstractClassMethodList!=null) AbstractClassMethodList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(AbstractClassMethodList!=null) AbstractClassMethodList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("HasMethodsAbstract(\n");

        if(AbstractClassMethodList!=null)
            buffer.append(AbstractClassMethodList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [HasMethodsAbstract]");
        return buffer.toString();
    }
}
