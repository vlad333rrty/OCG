# shellcheck disable=SC2046

path=org/bmstu/iu9/parser

flex  --outfile=$path/lex.yy.c $path/lexer.l
gcc -o $path/a.out $path/lex.yy.c
chmod +x $path/a.out
./$path/a.out