// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class HasCallParams extends CallParams {

    private CallParamList CallParamList;

    public HasCallParams (CallParamList CallParamList) {
        this.CallParamList=CallParamList;
        if(CallParamList!=null) CallParamList.setParent(this);
    }

    public CallParamList getCallParamList() {
        return CallParamList;
    }

    public void setCallParamList(CallParamList CallParamList) {
        this.CallParamList=CallParamList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(CallParamList!=null) CallParamList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(CallParamList!=null) CallParamList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(CallParamList!=null) CallParamList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("HasCallParams(\n");

        if(CallParamList!=null)
            buffer.append(CallParamList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [HasCallParams]");
        return buffer.toString();
    }
}
