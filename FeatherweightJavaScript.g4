grammar FeatherweightJavaScript;


@header { package edu.sjsu.fwjs.parser; }

// Reserved words
IF        : 'if' ;
ELSE      : 'else' ;
WHILE	  : 'while' ;
FUNCTION  : 'function' ;
VAR		  : 'var' ;
PRINT	  : 'print' ;

// Literals
INT       : [1-9][0-9]* | '0' ;
BOOL	  : 'true' | 'false' ;
NULL	  : 'null';
ID		  : [a-zA-Z_][a-zA-Z0-9_]* ;

// Symbols
ADD		  : '+' ;
SUB		  : '-' ;
MUL       : '*' ;
DIV       : '/' ;
MODULO	  : '%' ;
GRTRTHAN  : '>' ;
LESSTHAN  : '<' ;
GRTREQAL  : '>=' ;
LESSEQAL  : '<=' ;
EQUAL	  : '==' ;
ASSIGN	  : '=' ;
SEPARATOR : ';' ;

// Whitespace and comments
NEWLINE   : '\r'? '\n' -> skip ;
BLOCK_COMMENT : '/*' ~'*/' -> skip ;
LINE_COMMENT  : '//' ~[\n\r]* -> skip ;
WS            : [ \t]+ -> skip ; // ignore whitespace


// ***Parsing rules ***

/** The start rule */
prog: stat+ ;

stat: expr SEPARATOR                                    # bareExpr
    | IF '(' expr ')' block ELSE block                  # ifThenElse
    | IF '(' expr ')' block                             # ifThen
    | WHILE '(' expr ')' block 							# while
    | PRINT '(' expr ')' SEPARATOR						# print
    | SEPARATOR											# empty
    ;

expr: expr op=( '*' | '/' | '%' ) expr                  # MulDivMod
    | INT                                               # int
    | '(' expr ')'                                      # parens
    ;

block: '{' stat* '}'                                    # fullBlock
     | stat                                             # simpBlock
     ;

