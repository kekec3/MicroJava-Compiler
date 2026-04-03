// generated with ast extension for cup
// version 0.8
// 10/2/2026 19:58:50


package rs.ac.bg.etf.pp1.ast;

public class MethodCallFactor extends Factor {

    private Designator Designator;
    private MethodCallMarker MethodCallMarker;
    private CallParams CallParams;

    public MethodCallFactor (Designator Designator, MethodCallMarker MethodCallMarker, CallParams CallParams) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.MethodCallMarker=MethodCallMarker;
        if(MethodCallMarker!=null) MethodCallMarker.setParent(this);
        this.CallParams=CallParams;
        if(CallParams!=null) CallParams.setParent(this);
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public MethodCallMarker getMethodCallMarker() {
        return MethodCallMarker;
    }

    public void setMethodCallMarker(MethodCallMarker MethodCallMarker) {
        this.MethodCallMarker=MethodCallMarker;
    }

    public CallParams getCallParams() {
        return CallParams;
    }

    public void setCallParams(CallParams CallParams) {
        this.CallParams=CallParams;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
        if(MethodCallMarker!=null) MethodCallMarker.accept(visitor);
        if(CallParams!=null) CallParams.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
        if(MethodCallMarker!=null) MethodCallMarker.traverseTopDown(visitor);
        if(CallParams!=null) CallParams.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        if(MethodCallMarker!=null) MethodCallMarker.traverseBottomUp(visitor);
        if(CallParams!=null) CallParams.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodCallFactor(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethodCallMarker!=null)
            buffer.append(MethodCallMarker.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(CallParams!=null)
            buffer.append(CallParams.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodCallFactor]");
        return buffer.toString();
    }
}
