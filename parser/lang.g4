grammar lang;
@parser::header{
package parser;
}
@lexer::header{
package parser;
}

prog : data* | func+ ;
data : 'data' TYPE '{' decl* '}' ;
decl : ID '::' type ';' ;
func : ID '(' params? ')' ( ':' type ( ',' type )* )? '{' cmd* '}' ;
params : ID '::' type ( ',' ID '::' type )* ;
type : type '[' ']' | btype ;
btype : 'Int' | 'Char' | 'Bool' | 'Float' | TYPE ;
cmd : '{' cmd* '}'
    | 'if' '(' exp ')' cmd
    | 'if' '(' exp ')' cmd 'else' cmd
    | 'iterate' '(' exp ')' cmd
    | 'read' lvalue ';'
    | 'print' exp ';'
    | 'return' exp ( ',' exp )* ';'
    | lvalue '=' exp ';'
    | ID '(' exps? ')' ( '<' lvalue ( ',' lvalue )* '>' )? ';' ;
exp : exp '&&' exp
    | rexp ;
rexp : aexp '<' aexp
    | rexp '==' aexp
    | rexp '!=' aexp
    | aexp ;
aexp : aexp '+' mexp
    | aexp '-' mexp
    | mexp ;
mexp : mexp '*' sexp
    | mexp '/' sexp
    | mexp '%' sexp
    | sexp ;
sexp : '!' sexp
    | '-' sexp
    | BOOL
    | NULL
    | INT
    | FLOAT
    | CHAR
    | pexp ;
pexp : lvalue
    | '(' exp ')'
    | 'new' type '[' exp ']'
    | ID '(' exps? ')' '[' exp ']' ;
lvalue : ID
    | lvalue '[' exp ']'
    | lvalue '.' ID ;
exps : exp ( ',' exp )* ;


//Expressões regulares
BOOL: 'true' | 'false';
NULL: 'null';
ID : [a-z][a-zA-Z0-9_]*;
TYPE: [A-Z][a-zA-Z0-9_]*;
INT : [0-9]+;
FLOAT: INT* '.' INT+;
CHAR: '\'' ( '\\' . | ~['\\'] ) '\'';
NEWLINE: '\r' ? '\n' -> skip;
WS : [ \t\r\n]+ -> skip;
LINE_COMMENT: '--' ~('\r' | '\n')* NEWLINE -> skip;
COMMENT: '{-' .*? '-}' -> skip;