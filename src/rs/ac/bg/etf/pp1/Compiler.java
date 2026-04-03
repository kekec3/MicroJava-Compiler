package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import java_cup.runtime.Symbol;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;

public class Compiler {

    static {
        DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
        Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
    }

    public static void main(String[] args) {
        Logger log = Logger.getLogger(Compiler.class);
        
        List<String> filesToCompile;
        
        // java rs.ac.bg.etf.pp1.Compiler test/myTest.mj test/other.mj
        if (args != null && args.length > 0) {
            filesToCompile = Arrays.asList(args);
        } else {
            filesToCompile = Arrays.asList("test/test301.mj", "test/test302.mj", "test/test303.mj");
        }

        for (String filePath : filesToCompile) {
            log.info("--------------------------------------------------");
            log.info("Kompajliranje fajla: " + filePath);
            compileFile(filePath, log);
        }
    }

    public static void compileFile(String srcPath, Logger log) {
        File sourceCode = new File(srcPath);
        if (!sourceCode.exists()) {
            log.error("NIje nadjen: " + srcPath);
            return;
        }

        try (Reader br = new BufferedReader(new FileReader(sourceCode))) {
            Yylex lexer = new Yylex(br);
            MJParser p = new MJParser(lexer);
            Symbol s = p.parse(); 

            Program prog = (Program) (s.value);
            Tab.init();

            log.info(prog.toString(""));

            SemanticAnalyzer v = new SemanticAnalyzer();
            prog.traverseBottomUp(v);

            Tab.dump();

            if (!v.errorDetected) {
                String outPath = srcPath.replace(".mj", ".obj");
                File objFile = new File(outPath);
                if (objFile.exists()) objFile.delete();

                CodeGenerator codeGenerator = new CodeGenerator();
                prog.traverseBottomUp(codeGenerator);
                
                Code.dataSize = v.nVars + codeGenerator.getDataSize();
                Code.mainPc = codeGenerator.getMain();
                Code.write(new FileOutputStream(objFile));

                log.info("Uspesna kompajliranje: " + outPath);
            } else {
                log.error("Semanticka greska " + srcPath + ". Preskace se generisanje koda.");
            }
        } catch (Exception e) {
            log.error("Greska " + srcPath + ": " + e.getMessage(), e);
        }
    }
}