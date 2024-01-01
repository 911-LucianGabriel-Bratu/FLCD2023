%{
    #include <stdio.h>
    #include <stdlib.h>
    #include <string.h>

    int lines = 1;
%}

%option noyywrap
%option caseless

LETTER [a-zA-Z_]
NON_ZERO_DIGIT [1-9]
DIGIT [0-9]
IDENTIFIER {LETTER}({LETTER}|{DIGIT})*
WRONG_IDENTIFIER ({DIGIT})+({LETTER})+({LETTER}|{DIGIT})*
INTEGER 0|[+-]?{NON_ZERO_DIGIT}{DIGIT}*
CHARACTER ({LETTER}|{DIGIT})*
SPECIAL_CHAR [ ?:*\^+=.!]
STRING (\"({LETTER}|{DIGIT}|{SPECIAL_CHAR})*\")

%%

"+"|"-"|"*"|"/" printf("%s - arithmetic operator\n", yytext);
"&&"|"||"|"!" printf("%s - logical operator\n", yytext);
"<"|">"|">="|"<="|"=="|"!=" printf("%s - relational operator\n", yytext);
"=" printf("%s - assignment operator\n", yytext);
"["|"]"|"{"|"}"|"("|")" printf("%s - delimiter\n", yytext);
","|";" printf("%s - separator\n", yytext);
{IDENTIFIER} printf("%s - identifier\n", yytext);
{WRONG_IDENTIFIER} printf("%s - wrong identifier\n", yytext);
{INTEGER} printf("%s - integer\n", yytext);
{CHARACTER} printf("%s - character\n", yytext);
{STRING} printf("%s - string\n", yytext);

[ \t]+ {}

[\n]+ {++lines;}

. {printf("Error with token %s at line %d\n", yytext, lines); exit(1);}

%%

int main(int argc, char** argv) {
    if (argc > 1) 
        yyin = fopen(argv[1], "r");
    else 
        yyin = stdin;
    yylex();
}