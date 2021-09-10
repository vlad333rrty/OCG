%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "lexer.h"
#include "TokenTags.h"

#define MEM(size) ((char *)malloc( (size + 1) * sizeof(char)));
#define INPUT_FILE_PATH "/home/vlad333rrty/IdeaProjects/OCG4/data/input.txt"
#define RESULT_FILE_PATH "/home/vlad333rrty/IdeaProjects/OCG4/data/output.txt"

char *getPair(char* value,int tag) {
	const int digitNumber = 2;
	char *result = (char*)malloc((strlen(value) + 1 + digitNumber + 1) + 1);
	sprintf(result,"%s %d\n",value,tag);
	return result;
}


%}

%define api.pure
%locations
%lex-param {yyscan_t scanner}
%parse-param {yyscan_t scanner}
%parse-param {long env[26]}

%union {
	char* ident;
	long num;
}

%token <ident> IDENT
%token <num> NUMBER
%token COMMA SEMICOLON LPAREN RPAREN LBRACE RBRACE ASSIGN PLUS MINUS MUL DIV
%token IF_SPEC ELSE_SPEC WHILE_SPEC MAIN_SPEC RETURN_SPEC

%type <ident> start params ident_val body optional_body ident_or_number expr operation return_stmt

%{
int yylex(YYSTYPE * yylval_param, YYLTYPE * yylloc_param , yyscan_t scanner);
void yyerror(YYLTYPE *yylloc, yyscan_t scanner, long env[26], const char *msg);
%}

%%

start:
	MAIN_SPEC LPAREN params RPAREN LBRACE body return_stmt RBRACE {
		char* p1 = getPair("main",MAIN_TAG);
		char* p2 = getPair("(",LPAREN_TAG);
		char* p3 = getPair(")",RPAREN_TAG);
		char* p4 = getPair("{",LBRACE_TAG);
		char* p5 = getPair("}",RBRACE_TAG);

		FILE* file = fopen(RESULT_FILE_PATH,"w");
		if (file){
			fprintf(file,"%s%s%s%s%s%s%s%s",p1,p2,$3,p3,p4,$6,$7,p5);
		}else{
			fprintf(stderr,"Cannot open file for writing");
		}

		// printf("%s%s%s%s%s%s%s%s",p1,p2,$3,p3,p4,$6,$7,p5);

		free(p1);
                free(p2);
                free(p3);
                free(p4);
                free(p5);
	}
	;
params:
	%empty{
		$$="";
	}
	|
	ident_val{
		$$ = MEM(strlen($1));
		sprintf($$,"%s",$1);
	}
	|
	ident_val COMMA params{
		char* p2 = getPair(",",COMMA_TAG);
		$$ = MEM(strlen($1)+strlen(p2)+strlen($3));
                sprintf($$,"%s%s%s",$1,p2,$3);
                free(p2);
	}
	;
ident_val:
	IDENT{
        	char* pair = getPair($1,IDENT_TAG);
                $$ = MEM(strlen(pair));
                sprintf($$,"%s",pair);
               	free(pair);
        }
        ;
body:
	%empty{
		$$="";
	}
	|
	ident_val ASSIGN expr SEMICOLON body{
		char* p1 =getPair("=",ASSIGN_TAG);
		char* p2 = getPair(";",SEMICOLON_TAG);
		$$ = MEM(strlen($1)+strlen(p1)+strlen($3)+strlen(p2)+strlen($5));
		sprintf($$,"%s%s%s%s%s",$1,p1,$3,p2,$5);
		free(p1);
		free(p2);
	}
	|
	IF_SPEC LPAREN expr RPAREN LBRACE body RBRACE optional_body body{
		char* p1 = getPair("if",IF_TAG);
		char* p2 = getPair("(",LPAREN_TAG);
		char* p3 = getPair(")",RPAREN_TAG);
		char* p4 = getPair("{",LBRACE_TAG);
		char* p5 = getPair("}",RBRACE_TAG);
		$$ = MEM(strlen(p1) + strlen(p2) + strlen($3) + strlen(p3) + strlen(p4) + strlen($6) + strlen(p5) + strlen($8)+strlen($9));
		sprintf($$,"%s%s%s%s%s%s%s%s%s",p1,p2,$3,p3,p4,$6,p5,$8,$9);
		free(p1);
		free(p2);
		free(p3);
		free(p4);
		free(p5);
	}
	|
	WHILE_SPEC LPAREN expr RPAREN LBRACE body RBRACE body{
		char* p1 = getPair("while",WHILE_TAG);
        	char* p2 = getPair("(",LPAREN_TAG);
        	char* p3 = getPair(")",RPAREN_TAG);
       		char* p4 = getPair("{",LBRACE_TAG);
       		char* p5 = getPair("}",RBRACE_TAG);
       		$$ = MEM(strlen(p1) + strlen(p2) + strlen($3) + strlen(p3) + strlen(p4) + strlen($6) + strlen(p5) + strlen($8));
       		sprintf($$,"%s%s%s%s%s%s%s%s",p1,p2,$3,p3,p4,$6,p5,$8);
       		free(p1);
       		free(p2);
       		free(p3);
       		free(p4);
       		free(p5);
	}
	;
optional_body:
	%empty {
		$$ = "";
	}
	|
	ELSE_SPEC LBRACE body RBRACE{
		char* p1 = getPair("else",ELSE_TAG);
		char* p2 = getPair("{",LBRACE_TAG);
		char* p3 = getPair("}",RBRACE_TAG);
		$$ = MEM(strlen(p1)+strlen(p2)+strlen($3)+strlen(p3));
		sprintf($$,"%s%s%s%s",p1,p2,$3,p3);
		free(p1);
		free(p2);
		free(p3);
	}
	;
ident_or_number:
	ident_val {
		$$ = MEM(strlen($1));
		sprintf($$,"%s",$1);
	}
	|
	NUMBER{
		int len = snprintf(NULL,0,"%d",yylval.num);
		char* str = MEM(len+1)
		sprintf(str,"%d",yylval.num);
		char* p = getPair(str,NUMBER_TAG);
		$$ = MEM(strlen(p));
		sprintf($$,"%s",p);
		free(str);
		free(p);
	}
	;
expr:
	ident_or_number{
		$$ = MEM(strlen($1));
		sprintf($$,"%s",$1);
	}
	|
	ident_or_number operation expr{
		$$ = MEM(strlen($1)+strlen($2)+strlen($3));
		sprintf($$,"%s%s%s",$1,$2,$3);
	}
	;
operation:
	PLUS{
		char* p =getPair("+",PLUS_TAG);
		$$ = MEM(strlen(p));
		sprintf($$,"%s",p);
		free(p);
	}
	|
	MINUS{
		char* p =getPair("-",MINUS_TAG);
        	$$ = MEM(strlen(p));
        	sprintf($$,"%s",p);
        	free(p);
	}
	|
	MUL{
		char* p =getPair("*",MUL_TAG);
        	$$ = MEM(strlen(p));
        	sprintf($$,"%s",p);
       		free(p);
	}
	|
	DIV{
		char* p =getPair("/",DIV_TAG);
		$$ = MEM(strlen(p));
		sprintf($$,"%s",p);
		free(p);
	}
	;
return_stmt:
	RETURN_SPEC expr SEMICOLON{
		char* p1 =getPair("return",RETURN_TAG);
		char* p2 = getPair(";",SEMICOLON_TAG);
		$$ = MEM(strlen(p1) + strlen($2) + strlen(p2));
		sprintf($$,"%s%s%s",p1,$2,p2);
		free(p1);
		free(p2);
	}
	;
%%

int main()
{
	yyscan_t scanner;
	struct Extra extra;
	long env[26];
    	env[0] = 0;
    	char * buffer = 0;
    	long length;
    	FILE * f = fopen (INPUT_FILE_PATH, "rb");

    	if (f) {
    		fseek (f, 0, SEEK_END);
    		length = ftell (f);
    		fseek (f, 0, SEEK_SET);
    		buffer = malloc (length);
    		if (buffer) fread (buffer, 1, length, f);
      		fclose (f);
   	}
	init_scanner(buffer, &scanner, &extra);
	yyparse(scanner, env);
	destroy_scanner(scanner);
    	free(buffer);
	return 0;
}