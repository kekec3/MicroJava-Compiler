package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;



%%

%{

	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn);
	}
	
	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn, value);
	}

%}

%cup
%line
%column
%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

[ \t\r\n\f]+ { }


"program"   { return new_symbol(sym.PROGRAM, yytext()); }
"break"     { return new_symbol(sym.BREAK, yytext()); }
"enum"      { return new_symbol(sym.ENUM, yytext()); }
"class"     { return new_symbol(sym.CLASS, yytext()); }
"abstract"  { return new_symbol(sym.ABSTRACT, yytext()); }
"else"      { return new_symbol(sym.ELSE, yytext()); }
"const"     { return new_symbol(sym.CONST, yytext()); }
"if"        { return new_symbol(sym.IF, yytext()); }
"new"       { return new_symbol(sym.NEW, yytext()); }
"print"     { return new_symbol(sym.PRINT, yytext()); }
"read"      { return new_symbol(sym.READ, yytext()); }
"return"    { return new_symbol(sym.RETURN, yytext()); }
"void"      { return new_symbol(sym.VOID, yytext()); }
"extends"   { return new_symbol(sym.EXTENDS, yytext()); }
"continue"  { return new_symbol(sym.CONTINUE, yytext()); }
"for"       { return new_symbol(sym.FOR, yytext()); }
"length"    { return new_symbol(sym.LENGTH, yytext()); }
"switch"    { return new_symbol(sym.SWITCH, yytext()); }
"case"      { return new_symbol(sym.CASE, yytext()); }

"+"   { return new_symbol(sym.PLUS, yytext()); }
"-"   { return new_symbol(sym.MINUS, yytext()); }
"*"   { return new_symbol(sym.MUL, yytext()); }
"/"   { return new_symbol(sym.DIV, yytext()); }
"%"   { return new_symbol(sym.MOD, yytext()); }

"=="  { return new_symbol(sym.EQ, yytext()); }
"!="  { return new_symbol(sym.NEQ, yytext()); }
">"   { return new_symbol(sym.GT, yytext()); }
">="  { return new_symbol(sym.GTE, yytext()); }
"<"   { return new_symbol(sym.LT, yytext()); }
"<="  { return new_symbol(sym.LTE, yytext()); }

"&&"  { return new_symbol(sym.AND, yytext()); }
"||"  { return new_symbol(sym.OR, yytext()); }

"="   { return new_symbol(sym.EQUAL, yytext()); }
"++"  { return new_symbol(sym.INC, yytext()); }
"--"  { return new_symbol(sym.DEC, yytext()); }

";"   { return new_symbol(sym.DOTCOMMA, yytext()); }
":"   { return new_symbol(sym.DOTDOT, yytext()); }
","   { return new_symbol(sym.COMMA, yytext()); }
"."   { return new_symbol(sym.DOT, yytext()); }
"?"   { return new_symbol(sym.QUESTION, yytext()); }

"("   { return new_symbol(sym.LPARENT, yytext()); }
")"   { return new_symbol(sym.RPARENT, yytext()); }
"["   { return new_symbol(sym.LSQUARE, yytext()); }
"]"   { return new_symbol(sym.RSQUARE, yytext()); }
"{"   { return new_symbol(sym.LVIGGLE, yytext()); }
"}"   { return new_symbol(sym.RVIGGLE, yytext()); }

[0-9]+                 				{ return new_symbol(sym.NUMBER, Integer.parseInt(yytext())); }
"'" ( [^'\\\r\n] | ("\\" .) ) "'"   { return new_symbol(sym.CHAR, new Character(yytext().charAt(1))); }
"true" | "false"       				{ return new_symbol(sym.BOOL, Boolean.parseBoolean(yytext())); }
[a-zA-Z][a-zA-Z0-9_]*  				{ return new_symbol(sym.IDENT, yytext()); }

"//"        	   { yybegin(COMMENT); }
<COMMENT> [^\r\n]+ {  }
<COMMENT> \r\n|\n|\r   { yybegin(YYINITIAL); }

. { System.err.println("Leksicka greska ("+yytext()+") u liniji "+(yyline+1)); }
