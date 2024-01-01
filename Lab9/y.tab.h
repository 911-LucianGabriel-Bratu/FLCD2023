
/* A Bison parser, made by GNU Bison 2.4.1.  */

/* Skeleton interface for Bison's Yacc-like parsers in C
   
      Copyright (C) 1984, 1989, 1990, 2000, 2001, 2002, 2003, 2004, 2005, 2006
   Free Software Foundation, Inc.
   
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.
   
   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */


/* Tokens.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     INTEGER = 258,
     STRING = 259,
     CHARACTER = 260,
     READ = 261,
     IF = 262,
     ELSE = 263,
     PRINT = 264,
     WHILE = 265,
     FOR = 266,
     AND = 267,
     OR = 268,
     NOT = 269,
     PLUS = 270,
     MINUS = 271,
     MUL = 272,
     DIV = 273,
     MOD = 274,
     LESS = 275,
     LESSEQ = 276,
     EQ = 277,
     NEQ = 278,
     GREATEREQ = 279,
     EQQ = 280,
     GREATER = 281,
     SQBRACKETOPEN = 282,
     SQBRACKETCLOSE = 283,
     SEMICOLON = 284,
     ROUNDOPEN = 285,
     ROUNDCLOSE = 286,
     BRACKETOPEN = 287,
     BRACKETCLOSE = 288,
     COMMA = 289,
     IDENTIFIER = 290,
     INTEGER_CONSTANT = 291,
     STRING_CONSTANT = 292,
     CHARACTER_CONSTANT = 293
   };
#endif
/* Tokens.  */
#define INTEGER 258
#define STRING 259
#define CHARACTER 260
#define READ 261
#define IF 262
#define ELSE 263
#define PRINT 264
#define WHILE 265
#define FOR 266
#define AND 267
#define OR 268
#define NOT 269
#define PLUS 270
#define MINUS 271
#define MUL 272
#define DIV 273
#define MOD 274
#define LESS 275
#define LESSEQ 276
#define EQ 277
#define NEQ 278
#define GREATEREQ 279
#define EQQ 280
#define GREATER 281
#define SQBRACKETOPEN 282
#define SQBRACKETCLOSE 283
#define SEMICOLON 284
#define ROUNDOPEN 285
#define ROUNDCLOSE 286
#define BRACKETOPEN 287
#define BRACKETCLOSE 288
#define COMMA 289
#define IDENTIFIER 290
#define INTEGER_CONSTANT 291
#define STRING_CONSTANT 292
#define CHARACTER_CONSTANT 293




#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef int YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
#endif

extern YYSTYPE yylval;


