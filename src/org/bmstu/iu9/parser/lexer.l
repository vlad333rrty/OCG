%option noyywrap bison-bridge bison-locations

%{
#include <stdio.h>
#include <stdlib.h>
#include "TokenTags.h"

#define INPUT_FILE_PATH "/home/vlad333rrty/IdeaProjects/OCG4/data/input.txt"
#define RESULT_FILE_PATH "/home/vlad333rrty/IdeaProjects/OCG4/data/output.txt"

struct Position{
    int line, pos, index;
};

typedef struct Position Position;

void printPos(Position *p){
    printf("(%d, %d)",p->line, p->pos);
}

struct Fragment{
    Position start,follow;
};

typedef struct Fragment YYLTYPE;

void printFragment(YYLTYPE *type){
    printPos(&(type->start));
    printf(" - ");
    printPos(&(type->follow));
}

union Token{
    char *ident;
    long num;
    char ch;
};

typedef union Token YYSTYPE;

int continued;
Position curPos;

#define YY_USER_ACTION {   \
        int i;\
        if (!continued) yylloc -> start = curPos;\
        continued=0;\
        for (i=0;i<yyleng;i++){\
            if (yytext[i]=='\n'){\
                curPos.line++;\
                curPos.pos=1;\
            }else{\
                curPos.pos++;\
            }\
            curPos.index++;\
        }\
        yylloc->follow=curPos;\
    }

void init(char *program){
    continued=0;
    curPos.line=1;
    curPos.pos=1;
    curPos.index=0;
    yy_scan_string(program);
}

%}

LETTER   [a-zA-Z]
DIGIT    [0-9]
IDENT    {LETTER}({LETTER}|{DIGIT})*
NUMBER   {DIGIT}+
RETURN   ret
%%

[\n\t ]+

=               {
                    yylval->ident=yytext;
                    return ASSIGN_TAG;
                }
\{              {
                    yylval->ident=yytext;
                    return LBRACE_TAG;
                }
\}              {
                    yylval->ident=yytext;
                    return RBRACE_TAG;
                }
\(              {
                    yylval->ident=yytext;
                    return LPAREN_TAG;
                }
\)              {
                    yylval->ident=yytext;
                    return RPAREN_TAG;
                }
,               {
                    yylval->ident=yytext;
                    return COMMA_TAG;
                }
;               {
                    yylval->ident=yytext;
                    return SEMICOLON_TAG;
                }
\+              {
                    yylval->ident=yytext;
                    return PLUS_TAG;
                }
-               {
                    yylval->ident=yytext;
                    return MINUS_TAG;
                }
\*              {
                    yylval->ident=yytext;
                    return MUL_TAG;
                }
[/]             {
                    yylval->ident=yytext;
                    return DIV_TAG;
                }
\<              {
                    yylval->ident=yytext;
                    return LESS_TAG;
                }
\>              {
                    yylval->ident=yytext;
                    return GREATER_TAG;
                }
\<=
                {
                    yylval->ident=yytext;
                    return LEQ_TAG;
                }
\>=             {
                    yylval->ident=yytext;
                    return GEQ_TAG;
                }

if              {
                    yylval->ident=yytext;
                    return IF_TAG;
                }
else            {
                    yylval->ident=yytext;
                    return ELSE_TAG;
                }
while           {
                    yylval->ident=yytext;
                    return WHILE_TAG;
                }
main            {
                    yylval->ident=yytext;
                    return MAIN_TAG;
                }
{RETURN}        {
                    yylval->ident=yytext;
                    return RETURN_TAG;
                }
{IDENT}			{
					yylval->ident=yytext;
					return IDENT_TAG;
				}

{NUMBER}		{
                    yylval->ident=yytext;
					return NUMBER_TAG;
				}

%%

#define SIZE 1<<10

int main(){
    FILE* out = fopen(RESULT_FILE_PATH,"w");
    if (!out){
        printf("Can not open the file\n");
        return 0;
    }
    FILE *file=fopen(INPUT_FILE_PATH,"r");
    if (!file){
        printf("Can not open the file\n");
        return 0;
    }
    char buf[SIZE];
    char c;
    int i=0;
    while ((c=fgetc(file))!=EOF && i < SIZE){
        buf[i++]=c;
    }
    if (i == SIZE) {
        printf("File is too long!\n");
        return 1;
    }
    buf[i]='\0';
    if (ferror(file)){
        printf("An error occurred while reading a file\n");
        return 0;
    }
    fclose(file);

    int tag;
    YYSTYPE value;
    YYLTYPE coords;

    init(buf);

     do{
           tag = yylex(&value,&coords);
           if (tag>0){
                fprintf(out,"%s %d\n",value.ident,tag - 1);
           }
     }while(tag!=0);


    fclose(out);

    return 0;
}