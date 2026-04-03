package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

    private int mainPC;
    private Obj currentMethod = Tab.noObj;
    private int enterPos = -1;

    private final Map<Struct, Integer> structVmtIdx = new HashMap<>();
    
    // static call patch lists
    private final List<Integer> callPatchPos = new ArrayList<>();
    private final List<Obj>    callPatchTarget = new ArrayList<>();

    // vtable support (Level C)
    private final List<Obj>       classList       = new ArrayList<>();
    private int dataWords = 0;
    private final List<Integer>   allocVmtPos     = new ArrayList<>();
    private final List<Struct>    allocVmtStructs = new ArrayList<>(); // struct of allocated class

    private Obj programObj = Tab.noObj;

    // ── if/else ──────────────────────────────────────────────────────────────
    private final Stack<Integer> ifStack   = new Stack<>();
    private final Stack<Integer> elseStack = new Stack<>();

    // ── for loop ─────────────────────────────────────────────────────────────
    // Stacks for nested for-loops (one entry per active loop)
    private final Stack<Integer> forCondLabelStack        = new Stack<>();
    private final Stack<Integer> forCondFalsePatchStack   = new Stack<>();  // -1 = no condition
    private final Stack<Integer> forJumpOverStepPatchStack = new Stack<>();
    private final Stack<Integer> forStepLabelStack        = new Stack<>();

    // ── break / continue (for + switch share break list) ─────────────────────
    private final List<List<Integer>> breakPatchStack    = new ArrayList<>();
    private final List<List<Integer>> continuePatchStack = new ArrayList<>();

    // ── switch ───────────────────────────────────────────────────────────────
    private final Stack<Integer> switchJnePatchStack = new Stack<>();

    // ── ternary ──────────────────────────────────────────────────────────────
    private final Stack<Integer> ternaryFalsePatchStack = new Stack<>();
    private final Stack<Integer> ternaryJumpOverStack   = new Stack<>();

    // ── AND / OR short-circuit ────────────────────────────────────────────────
    private final Stack<Integer> andShortCircuitPatch = new Stack<>();
    private final Stack<Integer> orShortCircuitPatch  = new Stack<>();

    // ─────────────────────────────────────────────────────────────────────────

    public int getMain()     { return mainPC;    }
    public int getDataSize() {
        if (dataWords == 0 && programObj != Tab.noObj) {
            int globalVars = 0;
            for (Obj o : programObj.getLocalSymbols()) {
                if (o.getKind() == Obj.Var) globalVars++;
            }
            return globalVars;
        }
        return dataWords;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void safeLoad(Obj o) {
        if (o == null || o == Tab.noObj) { Code.error("load of null/noObj"); return; }
        int k = o.getKind();
        if (k == Obj.Var || k == Obj.Con || k == Obj.Elem) {
            Code.load(o);
        } else if (k == Obj.Fld) {
            int adr = Math.max(o.getAdr(), 0);
            Code.put(Code.getfield); Code.put2(adr);
        } else {
            Code.error("Illegal load kind=" + k);
        }
    }

    // ── program initialisation (chr, ord, len builtins) ──────────────────────

    public void init() {
        Obj chr = Tab.find("chr");
        chr.setAdr(Code.pc);
        Code.put(Code.enter); Code.put(1); Code.put(1);
        Code.put(Code.load_n);          // load0
        Code.put(Code.exit); Code.put(Code.return_);

        Obj ord = Tab.find("ord");
        ord.setAdr(Code.pc);
        Code.put(Code.enter); Code.put(1); Code.put(1);
        Code.put(Code.load_n);          // load0
        Code.put(Code.exit); Code.put(Code.return_);

        Obj len = Tab.find("len");
        len.setAdr(Code.pc);
        Code.put(Code.enter); Code.put(1); Code.put(1);
        Code.put(Code.load_n);
        Code.put(Code.arraylength);
        Code.put(Code.exit); Code.put(Code.return_);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  PROGRAM / METHOD
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(ProgramName programName) {
        init();
        this.programObj = programName.obj;
    }

    @Override
    public void visit(MethodTypeName methodTypeName) {
        currentMethod = methodTypeName.obj;
        if (currentMethod != Tab.noObj) {
            currentMethod.setAdr(Code.pc);
            if ("main".equals(currentMethod.getName())) {
                mainPC = Code.pc;
                Code.mainPc = Code.pc;
            }
            Code.put(Code.enter);
            enterPos = Code.pc;
            Code.put(0); Code.put(0);

            // FIX: If this is main, emit the VMT initialization bytecode immediately!
            if ("main".equals(currentMethod.getName())) {
                
                // 1. Compute how many global variables exist
                int globalVars = 0;
                if (programObj != Tab.noObj) {
                    for (Obj o : programObj.getLocalSymbols()) {
                        if (o.getKind() == Obj.Var) globalVars++;
                    }
                }
                
                // 2. Start VMTs right after the global variables
                int currentVmtIdx = globalVars;

                for (Obj cl : classList) {
                    if (cl.getType() == null) continue;
                    structVmtIdx.put(cl.getType(), currentVmtIdx);
                    
                    for (Obj m : buildCompleteVtable(cl)) {
                        for (char c : m.getName().toCharArray()) {
                            Code.loadConst(c);
                            Code.put(Code.putstatic); Code.put2(currentVmtIdx++);
                        }
                        Code.loadConst(-1);
                        Code.put(Code.putstatic); Code.put2(currentVmtIdx++);
                        Code.loadConst(m.getAdr());
                        Code.put(Code.putstatic); Code.put2(currentVmtIdx++);
                    }
                    Code.loadConst(-2);
                    Code.put(Code.putstatic); Code.put2(currentVmtIdx++);
                }
                
                // 3. Set the total data size (Globals + VMTs) so getDataSize() is correct
                dataWords = currentVmtIdx; 
            }
        }
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        if (currentMethod == Tab.noObj) return;

        // Count total local symbols (params + local vars)
        int totalVars = 0;
        for (Obj o : currentMethod.getLocalSymbols())
            if (o.getKind() == Obj.Var) totalVars++;

        int nParams = currentMethod.getLevel();
        // FIX: b1=nParams, b2=totalVars (entire frame, not just locals after params)
        Code.put2(enterPos, (nParams << 8) | totalVars);

        // FIX: void -> exit/return, non-void -> trap(1) if fall-through
        if (currentMethod.getType() == Tab.noType) {
            Code.put(Code.exit);
            Code.put(Code.return_);
        } else {
            Code.put(Code.trap); Code.put(1);
        }

        currentMethod = Tab.noObj;
        enterPos = -1;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CONSTANTS / LITERALS
    // ═════════════════════════════════════════════════════════════════════════

    @Override public void visit(NumberFactor   nf) { Code.loadConst(nf.getValue());              }
    @Override public void visit(CharacterFactor cf) { Code.loadConst(cf.getValue());              }
    @Override public void visit(BooleanFactor  bf) { Code.loadConst(bf.getValue() ? 1 : 0);      }

    // ═════════════════════════════════════════════════════════════════════════
    //  ARITHMETIC
    // ═════════════════════════════════════════════════════════════════════════

    @Override public void visit(Addition      a) { Code.put(Code.add); }
    @Override public void visit(Subtraction   s) { Code.put(Code.sub); }
    @Override public void visit(Multiplication m) { Code.put(Code.mul); }
    @Override public void visit(Division      d) { Code.put(Code.div); }
    @Override public void visit(Modality      m) { Code.put(Code.rem); }
    @Override public void visit(Negative      n) { Code.put(Code.neg); }

    // ═════════════════════════════════════════════════════════════════════════
    //  RELATIONAL (produces 0 or 1 on stack)
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(RelationExpr relationExpr) {
        int op;
        Relation rel = relationExpr.getRelation();
        if      (rel instanceof Equal)        op = Code.eq;
        else if (rel instanceof NotEqual)     op = Code.ne;
        else if (rel instanceof Grater)       op = Code.gt;
        else if (rel instanceof GraterEqual)  op = Code.ge;
        else if (rel instanceof Less)         op = Code.lt;
        else                                  op = Code.le;

        int patchFalse = Code.pc;
        Code.putFalseJump(op, 0);   // jump to false-label if condition NOT met
        Code.loadConst(1);          // true path
        int patchJump = Code.pc;
        Code.putJump(0);
        Code.fixup(patchFalse + 1); // false-label = here
        Code.loadConst(0);          // false path
        Code.fixup(patchJump + 1);  // end-label   = here
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LOGICAL AND  (short-circuit)
    //  Grammar: ExprAnd AndMarker ExprRel -> LogicAnd
    //  AndMarker fires with left (0/1) on stack, BEFORE right is evaluated.
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(AndMarker andMarker) {
        // left on stack; if left==0, skip right
        Code.loadConst(0);
        int patch = Code.pc;
        Code.putFalseJump(Code.ne, 0); // jeq: jump when left==0
        andShortCircuitPatch.push(patch);
    }

    @Override
    public void visit(LogicAnd logicAnd) {
        // right on stack (left was non-zero, otherwise we jumped here)
        int jumpEnd = Code.pc;
        Code.putJump(0);                            // true path: skip the "false" constant
        Code.fixup(andShortCircuitPatch.pop() + 1); // SKIP label: left was 0
        Code.loadConst(0);                          // result = 0
        Code.fixup(jumpEnd + 1);                    // END label
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LOGICAL OR  (short-circuit)
    //  Grammar: ExprOr OrMarker ExprAnd -> LogicOr
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(OrMarker orMarker) {
        // left on stack; if left!=0, skip right
        Code.loadConst(0);
        int patch = Code.pc;
        Code.putFalseJump(Code.eq, 0); // jne: jump when left!=0
        orShortCircuitPatch.push(patch);
    }

    @Override
    public void visit(LogicOr logicOr) {
        // right on stack (left was 0, otherwise we jumped here)
        int jumpEnd = Code.pc;
        Code.putJump(0);                           // false path: skip the "true" constant
        Code.fixup(orShortCircuitPatch.pop() + 1); // SKIP label: left was non-zero
        Code.loadConst(1);                         // result = 1
        Code.fixup(jumpEnd + 1);                   // END label
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TERNARY  (condition ? trueExpr : falseExpr)
    //  Grammar: ExprOr TernaryStart ExprAdd TernaryElse ExprAdd -> Ternary
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(TernaryStart ts) {
        // condition (0/1) is on stack
        Code.loadConst(0);
        int patchFalse = Code.pc;
        Code.putFalseJump(Code.ne, 0); // jeq: jump to false-branch if cond==0
        ternaryFalsePatchStack.push(patchFalse);
    }

    @Override
    public void visit(TernaryElse te) {
        // true-branch result on stack; emit jump over false-branch
        int jumpOver = Code.pc;
        Code.putJump(0);
        ternaryJumpOverStack.push(jumpOver);
        // false-branch starts here -> patch the conditional jump
        Code.fixup(ternaryFalsePatchStack.pop() + 1);
    }

    @Override
    public void visit(Ternary ternary) {
        // false-branch result on stack; patch the over-jump to here
        Code.fixup(ternaryJumpOverStack.pop() + 1);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  IF / IF-ELSE
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(IfCondition ifCondition) {
        // bool result (0 or 1) on stack
        Code.loadConst(0);
        int patchAdr = Code.pc;
        // FIX: putFalseJump(ne,...) emits jeq which jumps when cond==0 (FALSE)
        Code.putFalseJump(Code.ne, 0);
        ifStack.push(patchAdr);
    }

    @Override
    public void visit(ElseStart elseStart) {
        // end of true-branch: jump over false-branch
        int jumpToEnd = Code.pc;
        Code.putJump(0);
        elseStack.push(jumpToEnd);
        // patch the if-condition jump to here (start of false/else branch)
        Code.fixup(ifStack.pop() + 1);
    }

    @Override
    public void visit(If ifStmt) {
        // no else: patch condition jump to here (after body)
        Code.fixup(ifStack.pop() + 1);
    }

    @Override
    public void visit(IfElse ifElseStmt) {
        // patch the over-jump to here (after else body)
        Code.fixup(elseStack.pop() + 1);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  FOR LOOP
    //
    //  Generated bytecode layout:
    //
    //    [init]
    //  condLabel:
    //    [cond code]          (if HasCondition)
    //    jeq  --> EXIT        (if HasCondition)
    //    jmp  --> BODY        (skip step on first entry)
    //  stepLabel:
    //    [step code]          (if HasStep)
    //    jmp  --> condLabel
    //  BODY:                  (patched by ForStep; continue -> stepLabel, break -> EXIT)
    //    [body code]
    //    jmp  --> stepLabel
    //  EXIT:
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(ForStart forStart) {
        // init code already generated; condition comes next
        forCondLabelStack.push(Code.pc);
    }

    @Override
    public void visit(ForCondition forCondition) {
        // condition code (if any) already generated
        int condFalsePatch = -1;
        if (forCondition.getForCond() instanceof HasForCondition) {
            // bool result (0/1) on stack
            Code.loadConst(0);
            condFalsePatch = Code.pc;
            Code.putFalseJump(Code.ne, 0); // jeq: jump to EXIT when cond==0
        }
        forCondFalsePatchStack.push(condFalsePatch);

        // Jump over step on first entry (body is after step code)
        int jumpOverStep = Code.pc;
        Code.putJump(0);
        forJumpOverStepPatchStack.push(jumpOverStep);

        // Step code starts right here
        forStepLabelStack.push(Code.pc);
    }

    @Override
    public void visit(ForStep forStep) {
        // step code already generated; emit loop-back jmp to condition
        int condLabel = forCondLabelStack.peek(); // keep on stack, popped in For
        Code.putJump(condLabel);

        // Body starts here; patch the jump-over-step to point here
        Code.fixup(forJumpOverStepPatchStack.pop() + 1);

        // Push fresh break/continue lists HERE (ForBodyStart has no production
        // name in the grammar so its visitor is never called; ForStep is always
        // called and fires just before the body is traversed, which is correct).
        breakPatchStack.add(new ArrayList<>());
        continuePatchStack.add(new ArrayList<>());
    }

    @Override
    public void visit(For forStmt) {
        int stepLabel = forStepLabelStack.pop();
        forCondLabelStack.pop();

        // End of body: jump back to step
        Code.putJump(stepLabel);

        // EXIT point is now at Code.pc
        // Patch condition-false jump to EXIT
        int condFalsePatch = forCondFalsePatchStack.pop();
        if (condFalsePatch != -1) {
            Code.fixup(condFalsePatch + 1);
        }

        // Patch break targets to EXIT (Code.pc unchanged)
        List<Integer> breaks = breakPatchStack.remove(breakPatchStack.size() - 1);
        for (int p : breaks) Code.fixup(p + 1);

        // Patch continue targets to stepLabel
        List<Integer> continues = continuePatchStack.remove(continuePatchStack.size() - 1);
        for (int p : continues) Code.put2(p + 1, stepLabel - p);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  BREAK / CONTINUE
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(Break breakStmt) {
        int patchPos = Code.pc;
        Code.putJump(0);
        if (!breakPatchStack.isEmpty())
            breakPatchStack.get(breakPatchStack.size() - 1).add(patchPos);
    }

    @Override
    public void visit(Continue continueStmt) {
        int patchPos = Code.pc;
        Code.putJump(0);
        if (!continuePatchStack.isEmpty())
            continuePatchStack.get(continuePatchStack.size() - 1).add(patchPos);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  SWITCH
    //
    //  Layout:
    //    [switch_val]
    //  CASE_1_CHECK:
    //    dup / const caseVal1 / jne --> FALLTHRU_JMP_2
    //    [stmts1]                 (break -> jmp AFTER_CASES)
    //  FALLTHRU_JMP_2:            (fall-through from case1 body skips case2 check)
    //    jmp --> CASE_2_BODY
    //  CASE_2_CHECK:              (case1's jne non-match lands here)
    //    dup / const caseVal2 / jne --> ...
    //  CASE_2_BODY:               (fall-through jmp from case1 lands here)
    //    [stmts2]
    //  AFTER_CASES:               (last jne + all breaks patched here)
    //    pop                      (discard switch_val)
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(SwitchStart switchStart) {
        // switch expression is on stack
        breakPatchStack.add(new ArrayList<>());
        switchJnePatchStack.push(-1); // no pending jne yet
    }

    @Override
    public void visit(CaseStart caseStart) {
        int prevJne = switchJnePatchStack.pop();

        // For every case after the first: emit a fall-through jmp BEFORE the check.
        // A previous case body that falls through (no break) hits this jmp and skips
        // the condition check, landing directly at this case's body.
        // The previous case's jne (no-match path) jumps HERE to run the check.
        int fallthroughJmpPos = -1;
        if (prevJne != -1) {
            fallthroughJmpPos = Code.pc;
            Code.putJump(0);          // placeholder; patched to body-start below
            Code.fixup(prevJne + 1);  // previous jne non-match → here (condition check)
        }

        // Emit this case's comparison: dup, load constant, jne
        Code.put(Code.dup);
        Code.loadConst(caseStart.getNum());
        int jnePatch = Code.pc;
        Code.putFalseJump(Code.eq, 0); // jne: jump when switch_val != caseNum
        switchJnePatchStack.push(jnePatch);

        // Patch the fall-through jmp to here (body start, right after condition check)
        if (fallthroughJmpPos != -1) {
            Code.fixup(fallthroughJmpPos + 1);
        }
    }

    @Override
    public void visit(Switch switchStmt) {
        // Patch last case's jne to AFTER_CASES (here)
        int lastJne = switchJnePatchStack.pop();
        if (lastJne != -1) Code.fixup(lastJne + 1);

        // Patch all breaks to AFTER_CASES (here)
        List<Integer> breaks = breakPatchStack.remove(breakPatchStack.size() - 1);
        for (int p : breaks) Code.fixup(p + 1);

        // Pop switch_val (after all jne/break patches, so they land before this pop)
        Code.put(Code.pop);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DESIGNATORS
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(FactorDesignator factorDesignator) {
        Obj o = factorDesignator.getDesignator().obj;
        if (o == null || o == Tab.noObj) return;

        if (o.getKind() == Obj.Fld) {
            // Special case: .length on array
            if (factorDesignator.getDesignator() instanceof FieldDesignator) {
                FieldDesignator fd = (FieldDesignator) factorDesignator.getDesignator();
                if (fd.getDesignator().obj.getType().getKind() == Struct.Array) {
                    Code.put(Code.arraylength);
                    return;
                }
            }
            Code.put(Code.getfield); Code.put2(Math.max(o.getAdr(), 0));
        } else {
            safeLoad(o);
        }
    }

    @Override
    public void visit(FieldDesignator fieldDesignator) {
        Designator inner      = fieldDesignator.getDesignator();
        Obj        currentObj = fieldDesignator.obj;
        if (inner == null || inner.obj == null || inner.obj == Tab.noObj) return;

        // Only push receiver for field/method access (not for enum constants)
        if (currentObj != null &&
            (currentObj.getKind() == Obj.Fld || currentObj.getKind() == Obj.Meth)) {
            // Push receiver if we're in an assignment, factor read, or method call context
            SyntaxNode parent = fieldDesignator.getParent();
            if (parent instanceof DesignatorStatement
                    || parent instanceof FactorDesignator
                    || parent instanceof MethodCall
                    || parent instanceof MethodCallFactor) {
                safeLoad(inner.obj);
            }
        }
    }

    @Override
    public void visit(ArrayDesignator arrayDesignator) {
        // Index (Expr) is already on stack; push array reference then swap: adr, idx
        Designator inner = arrayDesignator.getDesignator();
        if (inner != null && inner.obj != null && inner.obj != Tab.noObj) {
            safeLoad(inner.obj);       // stack: ..., idx, adr
            Code.put(Code.dup_x1);     // stack: ..., adr, idx, adr
            Code.put(Code.pop);        // stack: ..., adr, idx
        }
    }
    /*
    @Override
    public void visit(SimpleDesignator simpleDesignator) {
        // Push implicit 'this' BEFORE params when calling an instance method by simple name
        if (!(simpleDesignator.getParent() instanceof DesignatorStatement)) return;
        DesignatorStatement ds = (DesignatorStatement) simpleDesignator.getParent();
        if (!(ds.getDesignateOp() instanceof MethodCall)) return;
        Obj called = ds.getDesignator().obj;
        if (called == Tab.noObj) return;
        boolean hasThis = false;
        for (Obj o : called.getLocalSymbols())
            if (o.getKind() == Obj.Var && "this".equals(o.getName())) { hasThis = true; break; }
        if (!hasThis || currentMethod == Tab.noObj) return;
        for (Obj o : currentMethod.getLocalSymbols())
            if (o.getKind() == Obj.Var && "this".equals(o.getName())) { safeLoad(o); break; }
    }
    */
    @Override
    public void visit(SimpleDesignator simpleDesignator) {
        Obj obj = simpleDesignator.obj;
        if (obj != null && obj != Tab.noObj && obj.getKind() == Obj.Fld) {
            // Implicit 'this' field access.
            // Push 'this' (local[0]) so that the subsequent
            // Code.load(fld) → getfield  or  Code.store(fld) → putfield  works.
            Code.put(Code.load_n + 0);   // emits load_0
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ASSIGNMENT / INC / DEC
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(Assign assign) {
        DesignatorStatement parent = (DesignatorStatement) assign.getParent();
        Obj desigObj = parent.getDesignator().obj;
        if (desigObj == null || desigObj == Tab.noObj) return;
        if (desigObj.getKind() == Obj.Fld) {
            Code.put(Code.putfield); Code.put2(Math.max(desigObj.getAdr(), 0));
        } else {
            Code.store(desigObj);
        }
    }

    @Override
    public void visit(Inc inc) {
        DesignatorStatement parent = (DesignatorStatement) inc.getParent();
        Designator desig = parent.getDesignator();
        Obj obj = desig.obj;
        if (obj == Tab.noObj) return;
        switch (obj.getKind()) {
            case Obj.Var:
                safeLoad(obj);
                Code.loadConst(1); Code.put(Code.add);
                Code.store(obj);
                break;
            case Obj.Fld:
                if (desig instanceof FieldDesignator) {
                    Designator inner = ((FieldDesignator) desig).getDesignator();
                    int adr = Math.max(obj.getAdr(), 0);
                    safeLoad(inner.obj);
                    Code.put(Code.dup);
                    Code.put(Code.getfield); Code.put2(adr);
                    Code.loadConst(1); Code.put(Code.add);
                    Code.put(Code.putfield); Code.put2(adr);
                }
                break;
            case Obj.Elem:
                if (desig instanceof ArrayDesignator) {
                    safeLoad(((ArrayDesignator) desig).getDesignator().obj);
                    Code.put(Code.dup_x1); Code.put(Code.pop);
                    Code.put(Code.dup2);
                    if (obj.getType().getKind() == Struct.Char) Code.put(Code.baload);
                    else Code.put(Code.aload);
                    Code.loadConst(1); Code.put(Code.add);
                    Code.put(obj.getType().getKind() == Struct.Char ? Code.bastore : Code.astore);
                }
                break;
        }
    }

    @Override
    public void visit(Dec dec) {
        DesignatorStatement parent = (DesignatorStatement) dec.getParent();
        Designator desig = parent.getDesignator();
        Obj obj = desig.obj;
        if (obj == Tab.noObj) return;
        switch (obj.getKind()) {
            case Obj.Var:
                safeLoad(obj);
                Code.loadConst(1); Code.put(Code.sub);
                Code.store(obj);
                break;
            case Obj.Fld:
                if (desig instanceof FieldDesignator) {
                    Designator inner = ((FieldDesignator) desig).getDesignator();
                    int adr = Math.max(obj.getAdr(), 0);
                    safeLoad(inner.obj);
                    Code.put(Code.dup);
                    Code.put(Code.getfield); Code.put2(adr);
                    Code.loadConst(1); Code.put(Code.sub);
                    Code.put(Code.putfield); Code.put2(adr);
                }
                break;
            case Obj.Elem:
                if (desig instanceof ArrayDesignator) {
                    safeLoad(((ArrayDesignator) desig).getDesignator().obj);
                    Code.put(Code.dup_x1); Code.put(Code.pop);
                    Code.put(Code.dup2);
                    if (obj.getType().getKind() == Struct.Char) Code.put(Code.baload);
                    else Code.put(Code.aload);
                    Code.loadConst(1); Code.put(Code.sub);
                    Code.put(obj.getType().getKind() == Struct.Char ? Code.bastore : Code.astore);
                }
                break;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  METHOD CALLS
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(MethodCallMarker methodCallMarker) {
        SyntaxNode parent = methodCallMarker.getParent();
        Designator designator = null;
        
        if (parent instanceof MethodCallFactor) {
            designator = ((MethodCallFactor) parent).getDesignator();
        } else if (parent instanceof MethodCall) {
            // FIX: The parent is MethodCall. Its parent is the DesignatorStatement!
            DesignatorStatement ds = (DesignatorStatement) parent.getParent();
            designator = ds.getDesignator();
        }
        if (designator == null) return;

        Obj calledObj = designator.obj;
        if (calledObj == null || calledObj == Tab.noObj) return;

        // Push 'this' if it's an implicit class method call
        if (designator instanceof SimpleDesignator
                && calledObj.getKind() == Obj.Meth
                && hasThisParam(calledObj)) {
            Code.put(Code.load_n + 0); // emits load_0 (push 'this' before args)
        }
    }

    // Helper method (if you don't have it already)
    private boolean hasThisParam(Obj method) {
        for (Obj local : method.getLocalSymbols()) {
            if ("this".equals(local.getName())) return true;
        }
        return false;
    }
    
    @Override
    public void visit(MethodCall methodCall) {
        DesignatorStatement parent = (DesignatorStatement) methodCall.getParent();
        Obj called = parent.getDesignator().obj;
        if (called == Tab.noObj || called.getKind() != Obj.Meth) return;

        if (hasThisParam(called)) {
            // 1. Evaluate designator again to push object reference to the top of the stack
            Designator designator = parent.getDesignator();
            if (designator instanceof SimpleDesignator) {
                Code.put(Code.load_n + 0); // Implicit this
            } else {
                designator.traverseBottomUp(this); // Re-evaluate explicit object
            }
            
            // 2. Fetch the dynamic VMT address from the object's field 0
            Code.put(Code.getfield); Code.put2(0);
            
            // 3. Emit invokevirtual
            Code.put(Code.invokevirtual);
            for (char c : called.getName().toCharArray()) Code.put4(c);
            Code.put4(-1);
        } else {
            // Static Call
            Code.put(Code.call);
            int patchPos = Code.pc; Code.put2(0);
            callPatchPos.add(patchPos); callPatchTarget.add(called);
        }

        // FIX: If method returns a value but is called as a Statement, POP the unused result!
        if (called.getType() != Tab.noType) {
            Code.put(Code.pop);
        }
    }

    @Override
    public void visit(MethodCallFactor methodCallFactor) {
        Obj called = methodCallFactor.getDesignator().obj;
        if (called == Tab.noObj || called.getKind() != Obj.Meth) return;

        if (hasThisParam(called)) {
            // 1. Evaluate designator again to push object reference
            Designator designator = methodCallFactor.getDesignator();
            if (designator instanceof SimpleDesignator) {
                Code.put(Code.load_n + 0);
            } else {
                designator.traverseBottomUp(this);
            }
            
            // 2. Fetch dynamic VMT address
            Code.put(Code.getfield); Code.put2(0);
            
            // 3. Emit invokevirtual
            Code.put(Code.invokevirtual);
            for (char c : called.getName().toCharArray()) Code.put4(c);
            Code.put4(-1);
        } else {
            // Static Call
            Code.put(Code.call);
            int patchPos = Code.pc; Code.put2(0);
            callPatchPos.add(patchPos); callPatchTarget.add(called);
        }
        // No POP here, because a Factor's return value is supposed to be left on the stack!
    }
    

    // ═════════════════════════════════════════════════════════════════════════
    //  READ / PRINT / RETURN
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(Read read) {
        Obj desig = read.getDesignator().obj;
        if (desig == null || desig == Tab.noObj) return;
        Struct t = desig.getType();
        Code.put(t == Tab.charType ? Code.bread : Code.read);
        if (desig.getKind() == Obj.Fld) {
            Code.put(Code.putfield); Code.put2(Math.max(desig.getAdr(), 0));
        } else {
            Code.store(desig);
        }
    }

    @Override
    public void visit(Print print) {
        // FIX: use print for int/bool, bprint for char; handle HasPrintNum for any type
        Struct exprType = print.getExpr().struct;
        boolean isChar  = (exprType != null && exprType.equals(Tab.charType));

        if (print.getPrintNum() instanceof HasPrintNum) {
            Code.loadConst(((HasPrintNum) print.getPrintNum()).getPrintNum());
        } else {
            Code.loadConst(isChar ? 1 : 5);
        }
        Code.put(isChar ? Code.bprint : Code.print);
    }

    @Override
    public void visit(Return returnStmt) {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ALLOCATION (new)
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(Allocation allocation) {
        Struct allocType = allocation.getType().struct;
        if (allocation.getArrExpr() instanceof IsArrayExpr) {
            // Array: element type determines byte (char) vs word (other)
            int elemKind = (allocType.getKind() == Struct.Char) ? 0 : 1;
            Code.put(Code.newarray); Code.put(elemKind);
        } else {
            // Object: Size MUST be in bytes (1 word = 4 bytes)
            // Note: Since your ClassName visitor automatically adds the 'vmt' field, 
            // getNumberOfFields() already includes the VMT pointer!
            int nFields = allocType.getNumberOfFields();
            
            Code.put(Code.new_); 
            Code.put2(nFields * 4); // <--- FIX: Multiply by 4
            
            Code.put(Code.dup);
            
            // Push placeholder for VMT address to be patched later
            Code.put(Code.const_); 
            int pos = Code.pc; 
            Code.put4(0);
            allocVmtPos.add(pos); 
            allocVmtStructs.add(allocType);
            
            // Store VMT address into field 0
            Code.put(Code.putfield); 
            Code.put2(0);
            // NOTE: do NOT shift the pointer; getfield 0 on the raw ptr gives the VMT correctly.
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CLASS NAMES  (for vtable collection)
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(ClassName className) {
        Obj cl = className.obj;
        if (cl != null && cl != Tab.noObj) classList.add(cl);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  VTABLE HELPER  — builds complete method list for a class including
    //  all inherited methods, with overrides replacing base implementations.
    // ═════════════════════════════════════════════════════════════════════════

    private List<Obj> buildCompleteVtable(Obj cl) {
        // Walk inheritance chain from root ancestor down to cl (root first).
        // More-derived entries overwrite base entries for the same method name.
        java.util.LinkedHashMap<String, Obj> methodMap = new java.util.LinkedHashMap<>();
        List<Struct> chain = new ArrayList<>();
        Struct s = cl.getType();
        while (s != null && s.getKind() == Struct.Class) {
            chain.add(0, s); // prepend so root comes first
            Struct parent = s.getElemType();
            if (parent == null || parent == s || parent.getKind() != Struct.Class) break;
            s = parent;
        }
        for (Struct cs : chain) {
            try {
                for (Obj m : cs.getMembersTable().symbols()) {
                    if (m.getKind() == Obj.Meth && m.getAdr() > 0) // concrete only
                        methodMap.put(m.getName(), m);
                }
            } catch (Exception ignored) {}
        }
        return new ArrayList<>(methodMap.values());
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  PROGRAM END  (patch calls + emit vtables)
    // ═════════════════════════════════════════════════════════════════════════

    @Override
    public void visit(Program program) {
        // Patch static call addresses
        for (int i = 0; i < callPatchPos.size(); i++) {
            int pos    = callPatchPos.get(i);
            Obj target = callPatchTarget.get(i);
            int value  = (target != null ? target.getAdr() : 0) - pos + 1;
            Code.put2(pos, value);
        }

        // Patch VMT addresses for 'new' object allocations
        for (int i = 0; i < allocVmtPos.size(); i++) {
            int pos = allocVmtPos.get(i);
            Struct s = allocVmtStructs.get(i);
            int abs = structVmtIdx.getOrDefault(s, 0);
            Code.put2(pos, (abs >>> 16) & 0xFFFF);
            Code.put2(pos + 2, abs & 0xFFFF);
        }


    }
}