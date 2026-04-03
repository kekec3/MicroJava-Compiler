package rs.ac.bg.etf.pp1;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

public class SemanticAnalyzer extends VisitorAdaptor {

    public static Struct boolType = new Struct(Struct.Bool);
    boolean errorDetected = false;
	
    Struct lastType = Tab.noType;
    Obj lastMethod = Tab.noObj;
    int paramCnt = 0;
    boolean hasReturn = false;
    
    int nextEnumValue = 0;
    Obj lastClass = Tab.noObj;
    Scope classScope = null;
	
    Logger log = Logger.getLogger(getClass());
    private final Deque<List<Struct>> actualParamsStack = new ArrayDeque<>();
    
    int nVars;
    
    public void report_error(String message, SyntaxNode info) {
        errorDetected = true;
        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0: info.getLine();
        if (line != 0)
            msg.append (" na liniji ").append(line);
        log.error(msg.toString());
    }

    public void report_info(String message, SyntaxNode info) {
        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0: info.getLine();
        if (line != 0)
            msg.append (" na liniji ").append(line);
        log.info(msg.toString());
    }
	
    private static String kindName(Struct s) {
        if (s == null) return "<null>";
        switch (s.getKind()) {
            case Struct.Int:   return "int";
            case Struct.Char:  return "char";
            case Struct.Bool:  return "bool";
            case Struct.None:  return "void";
            case Struct.Array: return "array[" + kindName(s.getElemType()) + "]";
            case Struct.Class: return "class";
            case Struct.Enum:  return "enum";
            default:           return "?(kind=" + s.getKind() + ")";
        }
    }
    
    private boolean isAsignableTo(Struct dst, Struct src) {
        if (src.assignableTo(dst)) 
            return true;
        if (src.getKind() == Struct.Class && dst.getKind() == Struct.Class) {
            Struct curr = src.getElemType();
            while (curr != null && curr.getKind() == Struct.Class) {
                if (curr == dst)
                    return true;
                curr = curr.getElemType();
            }
        }
        return false;
    }
	
    @Override
    public void visit(ProgramName programName) {
        Tab.currentScope.addToLocals(new Obj(Obj.Type, "bool", boolType));
        programName.obj = Tab.insert(Obj.Prog, programName.getName(), Tab.noType);
        Tab.openScope();
    }
	
    @Override
    public void visit(Program program) {
        Obj main = Tab.find("main");
        if (main == Tab.noObj || main.getKind() != Obj.Meth || main.getType() != Tab.noType || main.getLevel() != 0) {
            report_error("GRESKA Program mora imati void main() metodu bez parametara.", null);
        } else {
            report_info("Main metoda ispravno deklarisana.", null);
        }
        nVars = Tab.currentScope.getnVars();
        Tab.chainLocalSymbols(program.getProgramName().obj);
        Tab.closeScope();
    }
	
    @Override
    public void visit(Type type) {
        Obj typeE = Tab.find(type.getTypeName());
        if (typeE == Tab.noObj) {
            report_error("GRESKA Tip nije pronadjen: " + type.getTypeName() + " Linija: " + type.getLine(), null);
            lastType = Tab.noType;
        } else {
            if (Obj.Type == typeE.getKind()) {
                lastType = typeE.getType();
            } else {
                report_error("GRESKA Identifikator ne predstavlja tip: " + type.getTypeName() + " Linija: " + type.getLine(), null);
                lastType = Tab.noType;
            }
        }
        type.struct = lastType;
    }
	
    @Override
    public void visit(ConstNumber constNumber) {
        constNumber.struct = Tab.intType;
    }
	
    @Override
    public void visit(ConstChar constChar) {
        constChar.struct = Tab.charType;
    }
	
    @Override
    public void visit(ConstBool constBool) {
        constBool.struct = boolType;
    }
	
    @Override
    public void visit(SingleConst singleConst) {
        String name = singleConst.getConstName();
        Obj constE = Tab.find(name);
        
        Const constNode = singleConst.getConst();
        Struct valType = constNode.struct;
        int val = 0;
        if (constNode instanceof ConstNumber) val = ((ConstNumber)constNode).getNum();
        else if (constNode instanceof ConstChar) val = ((ConstChar)constNode).getChr();
        else if (constNode instanceof ConstBool) val = ((ConstBool)constNode).getBool() ? 1 : 0;

        if (constE != Tab.noObj) {
            report_error("GRESKA Konstanta sa datim imenom vec definisana: " + name + " Linija: " + singleConst.getLine(), null);
        } else if (!lastType.assignableTo(valType)) {
            report_error("GRESKA Konstanta nema adekvatan tip -> Ocekivan: " + kindName(lastType) + " Dobijen: " + kindName(valType) + " Linija: " + singleConst.getLine(), null);
        } else {
            Obj constt = Tab.insert(Obj.Con, name, lastType);
            constt.setAdr(val);
            report_info("Deklarisana konstanta: " + name, singleConst);
        }
    }
	
    @Override
    public void visit(MultipleConsts multipleConsts) {
        String name = multipleConsts.getConstName();
        Obj constE = Tab.find(name);
        
        Const constNode = multipleConsts.getConst();
        Struct valType = constNode.struct;
        int val = 0;
        if (constNode instanceof ConstNumber) val = ((ConstNumber)constNode).getNum();
        else if (constNode instanceof ConstChar) val = ((ConstChar)constNode).getChr();
        else if (constNode instanceof ConstBool) val = ((ConstBool)constNode).getBool() ? 1 : 0;

        if (constE != Tab.noObj) {
            report_error("GRESKA Konstanta sa datim imenom vec definisana: " + name + " Linija: " + multipleConsts.getLine(), null);
        } else if (!lastType.assignableTo(valType)) {
            report_error("GRESKA Konstanta nema adekvatan tip -> Ocekivan: " + kindName(lastType) + " Dobijen: " + kindName(valType) + " Linija: " + multipleConsts.getLine(), null);
        } else {
            Obj constt = Tab.insert(Obj.Con, name, lastType);
            constt.setAdr(val);
            report_info("Deklarisana konstanta: " + name, null);
        }
    }
	
    @Override
    public void visit(SingleVar singleVar) {
        String name = singleVar.getVarName();
        Obj varE = Tab.currentScope.findSymbol(name);
        if (varE != null) {
            report_error("GRESKA Promenljiva sa datim imenom vec definisana: " + name + " Linija: " + singleVar.getLine(), null);
        } else {
            Struct type = lastType.getKind() == Struct.Enum ? Tab.intType : lastType;
            if (singleVar.getArr() instanceof IsArray) {
                type = new Struct(Struct.Array, type);
            }
            int kind = Obj.Var;
            if (lastClass != Tab.noObj && lastMethod == Tab.noObj) {
                kind = Obj.Fld;
            }
            Obj var = Tab.insert(kind, name, type);
            var.setLevel(lastMethod != Tab.noObj || lastClass != Tab.noObj ? 1 : 0);
            report_info("Deklarisana promenljiva: " + name, singleVar);
        }
    }
	
    @Override
    public void visit(MultipleVars multipleVars) {
        String name = multipleVars.getVarName();
        Obj varE = Tab.currentScope.findSymbol(name);
        if (varE != null) {
            report_error("GRESKA Promenljiva sa datim imenom vec definisana: " + name + " Linija: " + multipleVars.getLine(), null);
        } else {
            Struct type = lastType.getKind() == Struct.Enum ? Tab.intType : lastType;
            if (multipleVars.getArr() instanceof IsArray) {
                type = new Struct(Struct.Array, type);
            }
            int kind = Obj.Var;
            if (lastClass != Tab.noObj && lastMethod == Tab.noObj) {
                kind = Obj.Fld;
            }
            Obj var = Tab.insert(kind, name, type);
            var.setLevel(lastMethod != Tab.noObj || lastClass != Tab.noObj ? 1 : 0);
            report_info("Deklarisana promenljiva: " + name, multipleVars);
        }
    }
	
    @Override
    public void visit(EnumName enumName) {
        String name = enumName.getName();
        Obj enumE = Tab.currentScope.findSymbol(name);
        if (enumE != null) {
            report_error("GRESKA Enum vec postoji " + name + " Linija: " + enumName.getLine(), enumName);
        }
        enumName.obj = Tab.insert(Obj.Type, enumName.getName(), new Struct(Struct.Enum));
        Tab.openScope();
        nextEnumValue = 0;
        report_info("Pocinje enum: " + name, enumName);
    }
	
    @Override
    public void visit(EnumValue enumValue) {
        nextEnumValue = enumValue.getEnumValue();
    }
	
    @Override
    public void visit(SingleEnumElem singleEnumElem) {
        String name = singleEnumElem.getEnumElem();
        Obj enumE = Tab.currentScope.findSymbol(name);
        if (enumE != null) {
            report_error("GRESKA Duplikat enum elementa: '" + name + " Linija: " + singleEnumElem.getLine(), null);
        } else {
            Obj elem = Tab.insert(Obj.Con, name, Tab.intType);
            elem.setAdr(nextEnumValue);
            report_info("Enum elem: " + name, singleEnumElem);
        }
        nextEnumValue++;
    }

    @Override
    public void visit(MultipleEnumElems multipleEnumElems) {
        String name = multipleEnumElems.getEnumElem();
        Obj enumE = Tab.currentScope.findSymbol(name);
        if (enumE != null) {
            report_error("GRESKA Duplikat enum elementa: '" + name + " Linija: " + multipleEnumElems.getLine(), null);
        } else {
            Obj elem = Tab.insert(Obj.Con, name, Tab.intType);
            elem.setAdr(nextEnumValue);
            report_info("Enum elem: " + name, multipleEnumElems);
        }
        nextEnumValue++;
    }
	
    @Override
    public void visit(EnumDecl enumDecl) {
        Tab.chainLocalSymbols(enumDecl.getEnumName().obj.getType());
        Tab.closeScope();
        report_info("Deklarisan enum: " + enumDecl.getEnumName().obj.getName(), null);
        nextEnumValue = 0;
    }
	
    @Override
    public void visit(ClassName className) {
        String name = className.getName();
        Obj classE = Tab.currentScope.findSymbol(name);
        if (classE != null) {
            report_error("GRESKA Klasa vec postoji: " + name + " Linija: " + className.getLine(), className);
        }
        className.obj = Tab.insert(Obj.Type, name, new Struct(Struct.Class));
        lastClass = className.obj;
        Tab.openScope();
        classScope = Tab.currentScope;
        Obj vmt = Tab.insert(Obj.Fld, "vmt", Tab.noType);
        vmt.setAdr(0);
        report_info("Deklarisana klasa: " + name, className);
    }
	
    @Override
    public void visit(Extends ext) {
        if (lastClass == Tab.noObj) {
            return;
        }
        if (lastType.getKind() != Struct.Class) {
            report_error("GRESKA Nasledivanje moguce samo iz klase ne iz: " + lastType.getKind() + " Linija: " + ext.getLine(), ext);
        } else {
            lastClass.getType().setElementType(lastType);
            for (Obj pFld : lastType.getMembers()) {
                if (pFld.getKind() == Obj.Fld) {
                    Tab.insert(Obj.Fld, pFld.getName(), pFld.getType());
                }
            }
            report_info("Prosiruje: " + kindName(lastType), ext);
        }
    }
	
    @Override
    public void visit(ClassDecl classDecl) {
        if (lastClass != Tab.noObj) {
            Tab.chainLocalSymbols(lastClass.getType());
            Tab.closeScope();
        }
        lastClass = Tab.noObj;
        classScope = null;
    }
	
    @Override
    public void visit(AbstractClassDecl classDecl) {
        if (lastClass != Tab.noObj) {
            Tab.chainLocalSymbols(lastClass.getType());
            Tab.closeScope();
            lastClass = Tab.noObj;
            classScope = null;
        }
    }
	
    @Override
    public void visit(NoReturn NoReturn) {
        lastType = Tab.noType;
    }
	
    @Override
    public void visit(MethodTypeName methodTypeName) {
        String name = methodTypeName.getMethodName();
        Obj methE = Tab.currentScope.findSymbol(name);
        if (methE != null) {
            report_error("GRESKA Metodan sa datim imenom vec definisana: " + name + " Linija: " + methodTypeName.getLine(), null);
            lastMethod = Tab.noObj;
        } else {
            lastMethod = Tab.insert(Obj.Meth, name, lastType);
            paramCnt = 0;
            Tab.openScope();
            if (lastClass != Tab.noObj) {
                Obj thisC = Tab.insert(Obj.Var, "this", lastClass.getType());
                thisC.setAdr(0);
                paramCnt++;
            }
            report_info("Deklaracija metode: " + name, methodTypeName);
        }
        methodTypeName.obj = lastMethod;
    }
	
    @Override
    public void visit(SingularParam singularParam) {
        String name = singularParam.getParamName();
        Obj paramE = Tab.currentScope.findSymbol(name);
        if (paramE != null) {
            report_error("GRESKA Parametar sa datim imenom vec definisana: " + name + " Linija: " + singularParam.getLine(), null);
        } else {
            Struct type = lastType;
            if (singularParam.getArr() instanceof IsArray) {
                type = new Struct(Struct.Array, lastType);
            }
            Obj param = Tab.insert(Obj.Var, name, type);
            param.setLevel(1);
            paramCnt++;
        }
    }
	
    @Override
    public void visit(MultipleParams multipleParams) {
        String name = multipleParams.getParamName();
        Obj paramE = Tab.currentScope.findSymbol(name);
        if (paramE != null) {
            report_error("GRESKA Parametar sa datim imenom vec definisana: " + name + " Linija: " + multipleParams.getLine(), null);
        } else {
            Struct type = lastType;
            if (multipleParams.getArr() instanceof IsArray) {
                type = new Struct(Struct.Array, lastType);
            }
            Obj param = Tab.insert(Obj.Var, name, type);
            param.setLevel(1);
            paramCnt++;
        }
    }
	
    @Override
    public void visit(NoReturnExpr noReturnExpr) {
        hasReturn = true;
        lastType  = Tab.noType;
    }
	
    @Override
    public void visit(ReturnExpr returnExpr) {
        hasReturn = true;
        lastType = returnExpr.getExpr().struct;
    }
	
    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        if (lastMethod != Tab.noObj) {
            boolean isVoid = lastMethod.getType() == Tab.noType;
            boolean goodReturn = lastType.assignableTo(lastMethod.getType());
			
            if (isVoid && hasReturn && lastType != Tab.noType) {
                report_error("GRESKA Void metoda ne sme vracati vrednost", methodDeclaration);
            } else if (!isVoid && !hasReturn) {
                report_error("GRESKA Metoda mora imati povratnu vrednost tipa: " + kindName(lastMethod.getType()), methodDeclaration);
            } else if (!isVoid && !goodReturn) {
                report_error("GRESKA Los tip povratne vrednosti -> ocekivan: " + kindName(lastMethod.getType()) + " dobijen: " + kindName(lastType), methodDeclaration);
            } else {
                report_info("Metoda " + lastMethod.getName() + " ispravno deklarisana", methodDeclaration);
            }
            lastMethod.setLevel(paramCnt);
            Tab.chainLocalSymbols(lastMethod);
            Tab.closeScope();
        }
        lastMethod = Tab.noObj;
        hasReturn = false;
        lastType = Tab.noType;
        paramCnt = 0;
    }
	
    @Override
    public void visit(AbstractMethodDecl abstractMethodDecl) {
        if (lastMethod != Tab.noObj) {
            lastMethod.setLevel(paramCnt);
            Tab.chainLocalSymbols(lastMethod);
            Tab.closeScope();
            report_info("Deklarisana apstraktna metoda: " + lastMethod.getName(), abstractMethodDecl);
            lastMethod = Tab.noObj;
            hasReturn = false;
            lastType = Tab.noType;
        }
        paramCnt = 0;
    }
	
    @Override
    public void visit(SimpleDesignator simpleDesignator) {
        String name = simpleDesignator.getDesigName();
        Obj desig = Tab.find(name);
        if (desig == Tab.noObj && lastClass != Tab.noObj) {
            Struct curr = lastClass.getType().getElemType();
            while (curr != null && curr.getKind() == Struct.Class) {
                desig = curr.getMembersTable().searchKey(name);
                if (desig != null) {
                    break;
                }
                curr = curr.getElemType();
                desig = Tab.noObj;
            }
        }
        if (desig == Tab.noObj) {
            report_error("GRESKA Identifikator nije pronadjen: " + name + " Linija: " + simpleDesignator.getLine(), simpleDesignator);
            simpleDesignator.obj = Tab.noObj;
        } else {
            simpleDesignator.obj = desig;
        }
    }
    
    @Override
    public void visit(FieldDesignator fieldDesignator) {
        Obj desigObj = fieldDesignator.getDesignator().obj;
        if (fieldDesignator.getField() instanceof Len) {
            if (desigObj.getType().getKind() != Struct.Array) {
                report_error("GRESKA Operator .length moze se koristiti samo na nizovima Linija: " + fieldDesignator.getLine(), fieldDesignator);
                fieldDesignator.obj = Tab.noObj;
            } else {
                fieldDesignator.obj = new Obj(Obj.Fld, "length", Tab.intType);
                report_info("Pristup .length", fieldDesignator);
            }
        } else {
            String fieldName = ((FieldAcces) fieldDesignator.getField()).getFieldName();
            if (desigObj.getType().getKind() != Struct.Class && desigObj.getType().getKind() != Struct.Enum) {
                report_error("GRESKA Pristup polju moguc samo za objekte klasa i enuma Linija: " + fieldDesignator.getLine(), fieldDesignator);
                fieldDesignator.obj = Tab.noObj;
            } else {
                Struct curr = desigObj.getType();
                Obj field = null;
                if (curr.getKind() == Struct.Enum) {
                    field = curr.getMembersTable().searchKey(fieldName);
                } else {
                    while (curr != null && field == null && curr.getKind() == Struct.Class) {
                        if (lastClass != Tab.noObj && curr == lastClass.getType() && classScope != null) {
                            field = classScope.findSymbol(fieldName);
                        } else {
                            field = curr.getMembersTable().searchKey(fieldName);
                        }
                        curr = curr.getElemType();
                    }
                }
                if (field == null) {
                    report_error("GRESKA Clan " + fieldName + " nije pronadjen Linija: " + fieldDesignator.getLine(), fieldDesignator);
                    fieldDesignator.obj = Tab.noObj;
                } else {
                    fieldDesignator.obj = field;
                    report_info("Pristup clanu: " + fieldName, fieldDesignator);
                }
            }
        }
    }
    
    @Override
    public void visit(ArrayDesignator arrayDesignator) {
        Struct indexType = arrayDesignator.getExpr().struct;
        Obj arrayObj = arrayDesignator.getDesignator().obj;

        if (indexType != Tab.intType) {
            report_error("GRESKA Indeks niza mora biti tipa int Linija: " + arrayDesignator.getLine(), arrayDesignator);
        }
        if (arrayObj.getType().getKind() != Struct.Array) {
            report_error("GRESKA Pristup indeksom moguc samo na nizovima Linija: " + arrayDesignator.getLine(), arrayDesignator);
            arrayDesignator.obj = Tab.noObj;
        } else {
            arrayDesignator.obj = new Obj(Obj.Elem, "elem", arrayObj.getType().getElemType());
            report_info("Pristup elementu niza", arrayDesignator);
        }
    }
    
    @Override
    public void visit(FactorDesignator factorDesignator) {
        Obj info = factorDesignator.getDesignator().obj;
        if (info != Tab.noObj && info.getKind() == Obj.Meth) {
            report_error("GRESKA Ime metode ne moze se koristiti kao vrednost bez poziva Linija: " + factorDesignator.getLine(), factorDesignator);
            factorDesignator.struct = Tab.noType;
        } else {
            factorDesignator.struct = info.getType();
        }
    }
    
    @Override
    public void visit(MethodCallFactor methodCallFactor) {
        Obj called = methodCallFactor.getDesignator().obj;
        List<Struct> realParams = actualParamsStack.pop();
        
        if (called == Tab.noObj || called.getKind() != Obj.Meth) {
            report_error("GRESKA Identifikator nije metoda Linija: " + methodCallFactor.getLine(), methodCallFactor);
            methodCallFactor.struct = Tab.noType;
        } else {
            boolean hasThis = false;
            for (Obj o : called.getLocalSymbols()) {
                if (o.getKind() == Obj.Var && "this".equals(o.getName())) {
                    hasThis = true;
                    break;
                }
            }
            int expectCount = hasThis ? called.getLevel() - 1 : called.getLevel();
            if (realParams.size() != expectCount) {
                report_error("GRESKA Metoda '" + called.getName() + "' ocekuje " + expectCount + " parametara, dobijeno " + realParams.size() + " Linija: " + methodCallFactor.getLine(), methodCallFactor);
                methodCallFactor.struct = Tab.noType;
            } else {        		
                List<Obj> expectParams = new ArrayList<>();
                for (Obj o : called.getLocalSymbols()) {
                    if (o.getKind() == Obj.Var) {
                        expectParams.add(o);
                    }
                }
                Collections.sort(expectParams, Comparator.comparingInt(Obj::getAdr));
                int start = hasThis ? 1 : 0;
                for (int i = 0; i < realParams.size(); i++) {
                    if (!isAsignableTo(expectParams.get(start + i).getType(), realParams.get(i))) {
                        report_error("GRESKA parametar " + i + " nije odgovarajuceg tipa Linija: " + methodCallFactor.getLine(), methodCallFactor);
                    }
                }
                report_info("Poziv metode " + called.getName(), methodCallFactor);
                methodCallFactor.struct = called.getType();
            }
        }
    }
    
    @Override
    public void visit(NumberFactor numberFactor) {
        numberFactor.struct = Tab.intType;
    }

    @Override
    public void visit(CharacterFactor characterFactor) {
        characterFactor.struct = Tab.charType;
    }

    @Override
    public void visit(BooleanFactor booleanFactor) {
        booleanFactor.struct = boolType;
    }
    
    @Override
    public void visit(IsArrayExpr isArrayExpr) {
        if (isArrayExpr.getExpr().struct != Tab.intType) {
            report_error("GRESKA Velicina niza mora biti tipa int Linija: " + isArrayExpr.getLine(), isArrayExpr);
        }
    }
    
    @Override
    public void visit(Allocation allocation) {
        Struct allocType = allocation.getType().struct;
        if (allocation.getArrExpr() instanceof IsArrayExpr) {
            allocType = new Struct(Struct.Array, allocType);
        }
        allocation.struct = allocType;
        report_info("Kreiranje objekta", allocation);
    }
    
    @Override
    public void visit(Ternary ternary) {
        Struct condition = ternary.getExprOr().struct;
        Struct trueBranch = ternary.getExprAdd().struct;
        Struct falseBranch = ternary.getExprAdd1().struct;
        
        if (!condition.equals(boolType)) {
            report_error("GRESKA Uslov ternarnog operatora mora biti bool Linija: " + ternary.getLine(), ternary);
        }
        if (!trueBranch.compatibleWith(falseBranch)) {
            report_error("GRESKA Nekompatibilni tipovi u granama ternarnog operatora Linija: " + ternary.getLine(), ternary);
        }
        Struct result = (trueBranch != Tab.noType) ? trueBranch : falseBranch;
        ternary.struct = result;
    }
    
    @Override
    public void visit(Multiplication multiplication) {
        Struct left = multiplication.getTerm().struct;
        Struct right = multiplication.getFactor().struct;
        if (left != Tab.intType || right != Tab.intType) {
            report_error("GRESKA Operandi mnozenje moraju biti tipa int Linija: " + multiplication.getLine(), multiplication);
            multiplication.struct = Tab.noType;
        } else {
            multiplication.struct = Tab.intType;
            report_info("Mnozenje", multiplication);
        }
    }

    @Override
    public void visit(Division division) {
        Struct left = division.getTerm().struct;
        Struct right = division.getFactor().struct;
        if (left != Tab.intType || right != Tab.intType) {
            report_error("GRESKA Operandi deljenja moraju biti tipa int Linija: " + division.getLine(), division);
            division.struct = Tab.noType;
        } else {
            division.struct = Tab.intType;
            report_info("Deljenje", division);
        }
    }

    @Override
    public void visit(Modality modality) {
        Struct left = modality.getTerm().struct;
        Struct right = modality.getFactor().struct;
        if (left != Tab.intType || right != Tab.intType) {
            report_error("GRESKA Operandi modovanja moraju biti tipa int Linija: " + modality.getLine(), modality);
            modality.struct = Tab.noType;
        } else {
            modality.struct = Tab.intType;
            report_info("Modovanje", modality);
        }
    }
    
    @Override
    public void visit(Addition addition) {
        Struct left = addition.getExprAdd().struct;
        Struct right = addition.getTerm().struct;
        if (left != Tab.intType || right != Tab.intType) {
            report_error("GRESKA Operandi sabiranja moraju biti tipa int Linija: " + addition.getLine(), addition);
            addition.struct = Tab.noType;
        } else {
            addition.struct = Tab.intType;
            report_info("Sabiranje", addition);
        }
    }

    @Override
    public void visit(Subtraction subtraction) {
        Struct left = subtraction.getExprAdd().struct;
        Struct right = subtraction.getTerm().struct;
        if (left != Tab.intType || right != Tab.intType) {
            report_error("GRESKA Operandi oduzimanja moraju biti tipa int Linija: " + subtraction.getLine(), subtraction);
            subtraction.struct = Tab.noType;
        } else {
            subtraction.struct = Tab.intType;
            report_info("Oduzimanje", subtraction);
        }
    }
    
    @Override
    public void visit(Negative negative) {
        Struct operand = negative.getTerm().struct;
        if (operand != Tab.intType) {
            report_error("GRESKA Operand negacija mora biti tipa int Linija: " + negative.getLine(), negative);
            negative.struct = Tab.noType;
        } else {
            negative.struct = Tab.intType;
            report_info("Negacija", negative);
        }
    }
    
    @Override
    public void visit(RelationExpr relationExpr) {
        Struct left = relationExpr.getExprAdd().struct;
        Struct right = relationExpr.getExprAdd1().struct;
        if (!left.compatibleWith(right)) {
            report_error("GRESKA Nekompatibilni tipovi u relacionom izrazu Linija: " + relationExpr.getLine(), relationExpr);
        } else {
            int leftType = left.getKind();
            int rightType = right.getKind();
            Relation rel = relationExpr.getRelation();
            if ((leftType == Struct.Class || leftType == Struct.Array || rightType == Struct.Class || rightType == Struct.Array) && !(rel instanceof Equal) && !(rel instanceof NotEqual)) {
                report_error("GRESKA Uz tipove klase i niza mogu se koristiti samo == i != Linija: " + relationExpr.getLine(), relationExpr);
            }
        }
        relationExpr.struct = boolType;
        report_info("Relacija", relationExpr);
    }
    
    @Override
    public void visit(LogicAnd logicAnd) {
        Struct left = logicAnd.getExprAnd().struct;
        Struct right = logicAnd.getExprRel().struct;
        if (!left.equals(boolType) || !right.equals(boolType)) {
            report_error("GRESKA Operandi AND-a moraju biti tipa bool Linija: " + logicAnd.getLine() , logicAnd);
            logicAnd.struct = Tab.noType;
        } else {
            logicAnd.struct = boolType;
            report_info("AND", logicAnd);
        }
    }
    
    @Override
    public void visit(LogicOr logicOr) {
        Struct left = logicOr.getExprOr().struct;
        Struct right = logicOr.getExprAnd().struct;
        if (!left.equals(boolType) || !right.equals(boolType)) {
            report_error("GRESKA Operandi OR-a moraju biti tipa bool Linija: " + logicOr.getLine() , logicOr);
            logicOr.struct = Tab.noType;
        } else {
            logicOr.struct = boolType;
            report_info("OR", logicOr);
        }
    }

    @Override
    public void visit(NoCallParams noCallParams) {
        actualParamsStack.push(new ArrayList<>());
    }
    
    @Override
    public void visit(SingleParameter singleParameter) {
        List<Struct> list = new ArrayList<>();
        list.add(singleParameter.getExpr().struct);
        actualParamsStack.push(list);
    }
    
    @Override
    public void visit(MultipleParameters multipleParameters) {
        if (actualParamsStack.isEmpty()) {
            List<Struct> list = new ArrayList<>();
            list.add(multipleParameters.getExpr().struct);
            actualParamsStack.push(list);
        } else {
            actualParamsStack.peek().add(multipleParameters.getExpr().struct);
        }
    }
    
    @Override
    public void visit(Assign assign) {
        DesignatorStatement parent = (DesignatorStatement) assign.getParent();
        Obj desigObj = parent.getDesignator().obj;
        Struct valueType = assign.getExpr().struct;

        if (desigObj != Tab.noObj) {
            int kind = desigObj.getKind();
            if (kind != Obj.Var && kind != Obj.Fld && kind != Obj.Elem) {
                report_error("GRESKA Leva strana dodele mora biti l-vrednost Linija: " + assign.getLine(), assign);
                return;
            }
        }	
        if (!isAsignableTo(desigObj.getType(), valueType)) {
            report_error("GRESKA Nekompatibilni tipovi u dodeli -> levo: " + kindName(desigObj.getType()) + " desno: " + kindName(valueType) + " Linija: " + assign.getLine(), assign);
        } else {
            report_info("Dodela vrednosti", assign);
        }
    }
    
    @Override
    public void visit(MethodCall methodCall) {
        DesignatorStatement parent = (DesignatorStatement) methodCall.getParent();
        Obj called = parent.getDesignator().obj;
        List<Struct> realParams = actualParamsStack.pop();
        
        if (called == Tab.noObj || called.getKind() != Obj.Meth) {
            report_error("GRESKA Identifikator nije metoda Linija: " + methodCall.getLine(), methodCall);
        } else {
            boolean hasThis = false;
            for (Obj o : called.getLocalSymbols()) {
                if (o.getKind() == Obj.Var && "this".equals(o.getName())) {
                    hasThis = true;
                    break;
                }
            }
            int expectCount = hasThis ? called.getLevel() - 1 : called.getLevel();
            if (realParams.size() != expectCount) {
                report_error("GRESKA Metoda '" + called.getName() + "' ocekuje " + expectCount + " parametara, dobijeno " + realParams.size() + " Linija: " + methodCall.getLine(), methodCall);
            } else {
                List<Obj> expectParams = new ArrayList<>();
                for (Obj o : called.getLocalSymbols()) {
                    if (o.getKind() == Obj.Var) {
                        expectParams.add(o);
                    }
                }
                Collections.sort(expectParams, Comparator.comparingInt(Obj::getAdr));
                int start = hasThis ? 1 : 0;
                for (int i = 0; i < realParams.size(); i++) {
                    if (!isAsignableTo(expectParams.get(start + i).getType(), realParams.get(i))) {
                        report_error("GRESKA parametar " + i + " nije odgovarajuceg tipa Linija: " + methodCall.getLine(), methodCall);
                    }
                }
                report_info("Poziv metode '" + called.getName(), methodCall);
            }
        }
    }
    
    @Override
    public void visit(Inc inc) {
        DesignatorStatement parent = (DesignatorStatement) inc.getParent();
        Obj desigObj = parent.getDesignator().obj;
        if (desigObj.getType() != Tab.intType) {
            report_error("GRESKA Operator ++ moze se koristiti samo sa tipom int Linija: " + inc.getLine(), inc);
        }
    }
    
    @Override
    public void visit(Dec dec) {
        DesignatorStatement parent = (DesignatorStatement) dec.getParent();
        Obj desigObj = parent.getDesignator().obj;
        if (desigObj.getType() != Tab.intType) {
            report_error("GRESKA Operator -- moze se koristiti samo sa tipom int Linija: " + dec.getLine(), dec);
        }
    }
    
    @Override
    public void visit(If ifStmt) {
        Struct cond = ifStmt.getIfCondition().getExpr().struct;
        if (!cond.equals(boolType)) {
            report_error("GRESKA Uslov u if iskazu mora biti bool Linija: " + ifStmt.getLine(), ifStmt);
        }
        report_info("If", ifStmt);
    }

    @Override
    public void visit(IfElse ifElseStmt) {
        Struct cond = ifElseStmt.getIfCondition().getExpr().struct;
        if (!cond.equals(boolType)) {
            report_error("GRESKA Uslov u if-else iskazu mora biti bool Linija: " + ifElseStmt.getLine(), ifElseStmt);
        }
        report_info("If-Else", ifElseStmt);
    }
    
    @Override
    public void visit(Switch switchStmt) {
        Struct exprType = switchStmt.getSwitchStart().getExpr().struct;
        if (exprType != Tab.intType) {
            report_error("GRESKA Izraz u switch iskazu mora biti int Linija: " + switchStmt.getLine(), switchStmt);
        }
        report_info("Switch", switchStmt);
    }
    
    @Override
    public void visit(HasForCondition hasForCondition) {
        Struct cond = hasForCondition.getExpr().struct;
        if (!cond.equals(boolType)) {
            report_error("GRESKA Uslov for petlje mora biti bool Linija: " + hasForCondition.getLine(), hasForCondition);
        }
        report_info("For", hasForCondition);
    }

    @Override
    public void visit(Break breakStmt) {
        report_info("Break iskaz", breakStmt);
    }

    @Override
    public void visit(Continue continueStmt) {
        report_info("Continue iskaz", continueStmt);
    }
    
    @Override
    public void visit(Read read) {
        Obj desig = read.getDesignator().obj;
        if (desig != Tab.noObj) {
            int kind = desig.getKind();
            if (kind != Obj.Var && kind != Obj.Fld && kind != Obj.Elem) {
                report_error("GRESKA read zahteva l-vrednost Linija: " + read.getLine(), read);
            }
        }
        Struct t = desig.getType();
        if (t != Tab.intType && t != Tab.charType && !t.equals(boolType)) {
            report_error("GRESKA read prihvata samo int, char ili bool Linija: " + read.getLine(), read);
        }
        report_info("Read", read);
    }
    
    @Override
    public void visit(Print print) {
        Struct t = print.getExpr().struct;
        if (t != Tab.intType && t != Tab.charType && !t.equals(boolType)) {
            report_error("GRESKA print() prihvata samo int, char ili bool Liinja: " + print.getLine(), print);
        }
        report_info("Print", print);
    }
    
    @Override public void visit(OrExpr orExpr) { orExpr.struct = orExpr.getExprOr().struct; }
    @Override public void visit(OrTerm orTerm) { orTerm.struct = orTerm.getExprAnd().struct; }
    @Override public void visit(AndTerm andTerm) { andTerm.struct = andTerm.getExprRel().struct; }
    @Override public void visit(RelTerm relTerm) { relTerm.struct = relTerm.getExprAdd().struct; }
    @Override public void visit(ExprTerm exprTerm) { exprTerm.struct = exprTerm.getTerm().struct; }
    @Override public void visit(TermFactor termFactor) { termFactor.struct = termFactor.getFactor().struct; }
    @Override public void visit(Expression expression) { expression.struct = expression.getExpr().struct; }
}