%{
#include <stdio.h>
#include <stdlib.h>
extern int yylex();
extern int yyleng;
extern FILE* yyin;
extern FILE* yyout;
extern char* yytext;
int lines = 1;
extern int yyerror(char *s);

#define YYDEBUG 1
%}

%token INTEGER;
%token STRING;
%token CHARACTER;
%token READ;
%token IF;
%token ELSE;
%token PRINT;
%token WHILE;
%token FOR;

%token AND;
%token OR;
%token NOT;

%token PLUS;
%token MINUS;
%token MUL;
%token DIV;
%token MOD;
%token LESS;
%token LESSEQ;
%token EQ;
%token NEQ;
%token GREATEREQ;
%token EQQ;
%token GREATER;

%token SQBRACKETOPEN;
%token SQBRACKETCLOSE;
%token SEMICOLON;
%token ROUNDOPEN;
%token ROUNDCLOSE;
%token BRACKETOPEN;
%token BRACKETCLOSE;
%token COMMA;

%token IDENTIFIER;
%token INTEGER_CONSTANT;
%token STRING_CONSTANT;
%token CHARACTER_CONSTANT;

%start program

%%

program : BRACKETOPEN declist stmtlist BRACKETCLOSE {printf("program -> {declist stmtlist}\n");}
        | BRACKETOPEN declist BRACKETCLOSE {printf("program -> {declist}\n");}
        ;

declist : declaration SEMICOLON {printf("Declaration -> declaration\n");}
        | declaration SEMICOLON declist {printf("Declaration -> declist\n");}
        ;

declaration : type IDENTIFIER {printf("Declaration -> type IDENTIFIER\n");}
            | type IDENTIFIER EQ expression {printf("Stmt -> type IDENTIFIER = expression ;\n");}
            ;
type  : INTEGER {printf("Type -> integer\n");}
      | STRING {printf("Type -> string\n");}
      | CHARACTER {printf("Type -> character\n");}
      ;

stmtlist : stmt stmtlist {printf("Stmtlist -> stmtlist\n");} 
         | stmt {printf("Stmtlist -> stmt\n");}
         ;

stmt : simplstmt SEMICOLON {printf("Stmt -> simplstmt\n");}
     | structstmt {printf("Stmt -> structstmtlist\n");}
     | declist {printf("Stmt -> declist\n");}
     ;

simplstmt : assignstmt {printf("Simplstmt -> assignstmt\n");}
          | iostmt {printf("Simplstmt -> iostmt\n");}
          ;

assignstmt : IDENTIFIER EQ expression {printf("Assignstmt -> IDENTIFIER = expression\n");}
           ;
expression : expression PLUS term     { printf("Expression -> Expression + Term\n"); }
           | expression MINUS term     { printf("Expression -> Expression - Term\n"); }
           | term     { printf("Expression -> Term\n"); }
           ;

term : term MUL factor     { printf("term -> term * factor\n"); }
     | term DIV factor     { printf("term -> term / factor\n"); }
     | term MOD factor     { printf("term -> term % factor\n"); }
     | factor     { printf("term -> factor\n"); }
     ;

factor : ROUNDOPEN expression ROUNDCLOSE     { printf("factor -> ( expression )\n"); }
       | IDENTIFIER     { printf("factor -> IDENTIFIER\n"); }
       | INTEGER_CONSTANT     { printf("factor -> INTEGER_CONSTANT\n"); }
       | MINUS IDENTIFIER     { printf("factor -> - IDENTIFIER\n"); }
       ;

iostmt : READ ROUNDOPEN IDENTIFIER ROUNDCLOSE { printf("iostmt -> read( IDENTIFIER )\n"); }
       | PRINT ROUNDOPEN IDENTIFIER ROUNDCLOSE { printf("iostmt -> print( IDENTIFIER )\n"); }
       | PRINT ROUNDOPEN STRING_CONSTANT ROUNDCLOSE     { printf("iostmt -> print ( STRING_CONSTANT )\n"); }
       ;

structstmt : stmtlist
           | ifstmt
           | whilestmt
           | forstmt
           ; 

forstmt : FOR ROUNDOPEN assignstmt SEMICOLON condition SEMICOLON assignstmt ROUNDCLOSE BRACKETOPEN stmtlist BRACKETCLOSE { printf("forstmt -> for( assignstmt ; condition ; assignstmt ) { structstmt }\n"); }
        ;

ifstmt : IF ROUNDOPEN condition ROUNDCLOSE BRACKETOPEN stmtlist BRACKETCLOSE  { printf("ifstmt -> if ( expression ) { stmtlist }\n"); }
       | IF ROUNDOPEN condition ROUNDCLOSE BRACKETOPEN stmtlist BRACKETCLOSE ELSE BRACKETOPEN stmtlist BRACKETCLOSE  { printf("ifstmt -> if ( expression ) { stmtlist } else { stmtlist }\n"); }
       ;

whilestmt : WHILE ROUNDOPEN condition ROUNDCLOSE BRACKETOPEN stmtlist BRACKETCLOSE  { printf("whilestmt -> while ( expression ) { stmtlist }\n"); }

condition : expression relation expression { printf("condition -> expression relation expression\n"); }
          ;

relation : LESS     { printf("relation -> <\n"); }
         | LESSEQ     { printf("relation -> <=\n"); }
         | EQQ     { printf("relation -> ==\n"); }
         | NEQ     { printf("relation -> <>\n"); }
         | GREATEREQ     { printf("relation -> >=\n"); }
         | GREATER    { printf("relation -> >\n"); }
         ;
%%

int yyerror(char *s) {
    printf("Error: %s", s);
}

extern FILE *yyin;

int main(int argc, char** argv) {
    if (argc > 1) 
        yyin = fopen(argv[1], "r");
    if (!yyparse()) 
        fprintf(stderr, "\tOK\n");
}
