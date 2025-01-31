/*
Credits:
Some code was taken from the 2nd-example we did in class.
Some code was also taken from the JFlex manual:
https://jflex.de/manual.html#Example
*/

import java_cup.runtime.*;

%%
/* -----------------Options and Declarations Section----------------- */

/*
   The name of the class JFlex will create will be Scanner.
   Will write the code to the file Scanner.java.
*/
%class Scanner

/*
  The current line number can be accessed with the variable yyline
  and the current column number with the variable yycolumn.
*/
%line
%column

/*
   Will switch to a CUP compatibility mode to interface with a CUP
   generated parser.
*/
%cup
%unicode

/*
  Declarations

  Code between %{ and %}, both of which must be at the beginning of a
  line, will be copied letter to letter into the lexer class source.
  Here you declare member variables and functions that are used inside
  scanner actions.
*/

%{
    /**
        The following two methods create java_cup.runtime.Symbol objects
    **/
    private Symbol symbol(int type) {
       return new Symbol(type, yyline, yycolumn);
    }
    private Symbol symbol(int type, Object value) {
       return new Symbol(type, yyline, yycolumn, value);
    }

    /** A StringBuffer object to hold the strings **/
    StringBuffer stringBuffer = new StringBuffer();
%}

/*
  Macro Declarations

  These declarations are regular expressions that will be used latter
  in the Lexical Rules Section.
*/

/* A line terminator is a \r (carriage return), \n (line feed), or
   \r\n. */
LineTerminator = \r|\n|\r\n

/* White space is a line terminator, space, tab, or line feed. */
WhiteSpace = {LineTerminator} | [ \t\f]

Identifier = [:jletter:] [:jletterdigit:]*

%state STRING

%%
/* ------------------------Lexical Rules Section---------------------- */

<YYINITIAL> {
 /* operators */
 "+"      { return symbol(sym.PLUS); }
 "("      { return symbol(sym.LPAREN); }
 ")"      { return symbol(sym.RPAREN); }
 "{"      { return symbol(sym.LCURLY); }
 "}"      { return symbol(sym.RCURLY); }
 ","      { return symbol(sym.COMMA); }
 "if"           { return symbol(sym.IF); }
 "else"         { return symbol(sym.ELSE); }
 "prefix"       { return symbol(sym.PREFIX); }
 "reverse"      { return symbol(sym.REVERSE); }
 /* If the scanner matches a double quote in state YYINITIAL we have recognised the start of a string literal */
 \"       { stringBuffer.setLength(0); stringBuffer.append('\"'); yybegin(STRING); }
 {Identifier}      { return symbol(sym.IDENTIFIER, yytext()); }
 {WhiteSpace}      { /* ignore */ }
}

/* The STRING lexical state */
<STRING> {
  \"                             { stringBuffer.append('\"');
                                   yybegin(YYINITIAL);
                                   return symbol(sym.STRING_LITERAL,
                                   stringBuffer.toString()); }
  [^\n\r\"\\]+                   { stringBuffer.append( yytext() ); }
  \\t                            { stringBuffer.append('\t'); }
  \\n                            { stringBuffer.append('\n'); }

  \\r                            { stringBuffer.append('\r'); }
  \\\"                           { stringBuffer.append('\"'); }
  \\                             { stringBuffer.append('\\'); }
}

/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }