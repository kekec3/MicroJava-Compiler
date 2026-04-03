// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class Subtraction extends ExprAdd {

    private ExprAdd ExprAdd;
    private Term Term;

    public Subtraction (ExprAdd ExprAdd, Term Term) {
        this.ExprAdd=ExprAdd;
        if(ExprAdd!=null) ExprAdd.setParent(this);
        this.Term=Term;
        if(Term!=null) Term.setParent(this);
    }

    public ExprAdd getExprAdd() {
        return ExprAdd;
    }

    public void setExprAdd(ExprAdd ExprAdd) {
        this.ExprAdd=ExprAdd;
    }

    public Term getTerm() {
        return Term;
    }

    public void setTerm(Term Term) {
        this.Term=Term;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ExprAdd!=null) ExprAdd.accept(visitor);
        if(Term!=null) Term.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExprAdd!=null) ExprAdd.traverseTopDown(visitor);
        if(Term!=null) Term.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExprAdd!=null) ExprAdd.traverseBottomUp(visitor);
        if(Term!=null) Term.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Subtraction(\n");

        if(ExprAdd!=null)
            buffer.append(ExprAdd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Term!=null)
            buffer.append(Term.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Subtraction]");
        return buffer.toString();
    }
}
