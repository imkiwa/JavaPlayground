grammar Hi;

program : statement* EOF;

statement : definition
          ;

definition : FUNC ID tele* (':' returnExpr)? funcBody  # defFunc
           ;

funcBody : '=>' expr  # withoutElim
         ;

returnExpr : expr  # returnExprExpr
           ;

expr : appExpr                         # app
     | '\\Pi' tele+ '->' expr          # pi
     | '\\lam' tele+ '=>' expr         # lam
     ;

typedExpr : expr          # notTyped
          | expr ':' expr # typed
          ;

appExpr : atom argument*   # argumentApp
        | UNIVERSE         # universe
        ;

argument : atom                         # argumentExplicit
         | universeAtom                 # argumentUniverse
         | '{' expr '}'                 # argumentImplicit
         ;

tele : literal           # teleLiteral
     | universeAtom      # teleUniverse
     | '(' typedExpr ')' # explicit
     | '{' typedExpr '}' # implicit
     ;

universeAtom : UNIVERSE
             ;

atom  : literal                 # atomLiteral
      | NUMBER                  # atomNumber
      ;

literal : ID             # name
        | '_'            # unknown
        ;

FUNC : '\\func';
LAM  : '\\lam';
NUMBER : '-'? [0-9]+;
UNIVERSE : '\\Type' [0-9]*;
COLON : ':';
ARROW : '->';
UNDERSCORE : '_';

WS : [ \t\r\n]+ -> skip;
LINE_COMMENT : '--' '-'* (~[~!@#$%^&*\-+=<>?/|:[\u005Da-zA-Z_0-9'\r\n] ~[\r\n]* | ) -> skip;
COMMENT : '{-' (COMMENT|.)*? '-}' -> skip;

fragment START_CHAR : [~!@#$%^&*\-+=<>?/|:[\u005Da-zA-Z_];
ID : START_CHAR (START_CHAR | [0-9'])*;
INVALID_KEYWORD : '\\' ID;
ERROR_CHAR : .;

