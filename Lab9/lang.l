%{
    #include <stdio.h>
    #include <stdlib.h>
    #include <string.h>
    #include "y.tab.h"
    extern int yylex();
    extern int yyleng;
    extern FILE* yyin;
    extern FILE* yyout;
    extern char* yytext;
    extern int lines;
%}

%option noyywrap
%option caseless

LETTER [a-zA-Z_]
NON_ZERO_DIGIT [1-9]
DIGIT [0-9]
IDENTIFIER {LETTER}({LETTER}|{DIGIT})*
WRONG_IDENTIFIER ({DIGIT})+({LETTER})+({LETTER}|{DIGIT})*
INTEGER_CONSTANT 0|[+-]?{NON_ZERO_DIGIT}{DIGIT}*
CHARACTER_CONSTANT ({LETTER}|{DIGIT})*
SPECIAL_CHAR [ ?:*\^+=.!]
STRING_CONSTANT (\"({LETTER}|{DIGIT}|{SPECIAL_CHAR})*\")

%%

"integer" { printf("%s - reserved word\n", yytext); return INTEGER; }
"string" { printf("%s - reserved word\n", yytext); return STRING; }
"character" { printf("%s - reserved word\n", yytext); return CHARACTER; }
"read" { printf("%s - reserved word\n", yytext); return READ; }
"if" { printf("%s - reserved word\n", yytext); return IF; }
"else" { printf("%s - reserved word\n", yytext); return ELSE; }
"print" { printf("%s - reserved word\n", yytext); return PRINT; }
"while" { printf("%s - reserved word\n", yytext); return WHILE; }
"for" { printf("%s - reserved word\n", yytext); return FOR; }
"&&" { printf("%s - operator\n", yytext); return AND; }
"||" { printf("%s - operator\n", yytext); return OR; }
"!" { printf("%s - operator\n", yytext); return NOT; }
"+" { printf("%s - operator\n", yytext); return PLUS; }
"-" { printf("%s - operator\n", yytext); return MINUS; }
"*" { printf("%s - operator\n", yytext); return MUL; }
"/" { printf("%s - operator\n", yytext); return DIV; }
"%" { printf("%s - operator\n", yytext); return MOD; }
"<" { printf("%s - operator\n", yytext); return LESS; }
"<=" { printf("%s - operator\n", yytext); return LESSEQ; }
"=" { printf("%s - operator\n", yytext); return EQ; }
">=" { printf("%s - operator\n", yytext); return GREATEREQ; }
"==" { printf("%s - operator\n", yytext); return EQQ; }
"!=" { printf("%s - operator\n", yytext); return NEQ; }
">" { printf("%s - operator\n", yytext); return GREATER; }
"[" { printf("%s - separator\n", yytext); return SQBRACKETOPEN; }
"]" { printf("%s - separator\n", yytext); return SQBRACKETCLOSE; } 
";" { printf("%s - separator\n", yytext); return SEMICOLON; } 
"(" { printf("%s - separator\n", yytext); return ROUNDOPEN; } 
")" { printf("%s - separator\n", yytext); return ROUNDCLOSE; }
"{" { printf("%s - separator\n", yytext); return BRACKETOPEN; } 
"}" { printf("%s - separator\n", yytext); return BRACKETCLOSE; } 
"," { printf("%s - separator\n", yytext); return COMMA; }
{IDENTIFIER} { printf("%s - identifier\n", yytext); return IDENTIFIER;}
{WRONG_IDENTIFIER} {printf("%s - wrong identifier\n", yytext); return -1;}
{INTEGER_CONSTANT} {printf("%s - integer\n", yytext); return INTEGER_CONSTANT;}
{CHARACTER_CONSTANT} {printf("%s - character\n", yytext); return CHARACTER_CONSTANT;}
{STRING_CONSTANT} {printf("%s - string\n", yytext); return STRING_CONSTANT;}

[ \t]+ {}

[\n]+ {++lines;}

. {printf("Error with token %s at line %d\n", yytext, lines); exit(1);}

%%