grammar Calculadora;

// ============================================================
//  ¿QUÉ ES ESTE ARCHIVO?
//  Es la "gramática": aquí le decimos a ANTLR cómo se ve un
//  texto válido para nuestra calculadora (qué es una suma, qué
//  es asignar una variable, etc). ANTLR lee este archivo y
//  genera automáticamente el Lexer y el Parser en Java.
//  Nosotros NO escribimos ese código generado a mano.
// ============================================================


// ------------------------------------------------------------
// REGLAS SINTÁCTICAS (empiezan con minúscula)
// Describen cómo se combinan los "tokens" (las palabras/símbolos)
// ------------------------------------------------------------

// Un programa es UNA O MÁS instrucciones seguidas (el "+" significa "1 o más")
prog
    : stat+
    ;

// Una instrucción (stat = "statement") puede ser de 4 tipos.
// Lo que va después de "#" es una ETIQUETA. Sirve para que ANTLR
// genere un método distinto en Java por cada caso (visitPrintExpr,
// visitAssign, visitClear, visitBlank). Así no toca usar un solo
// método gigante con "if" para todo.
stat
    : expr NEWLINE            # printExpr   // ej: "3+4"  -> calcula e imprime el resultado
    | ID '=' expr NEWLINE     # assign      // ej: "x=5"  -> guarda 5 en la variable x
    | 'clear' NEWLINE         # clear       // ej: "clear" -> borra todas las variables guardadas
    | NEWLINE                 # blank       // una línea vacía, no hace nada
    ;

// Una expresión aritmética.
// IMPORTANTE: el ORDEN de las alternativas define la PRECEDENCIA.
// Como "MulDiv" está escrita ANTES que "AddSub", ANTLR entiende que
// la multiplicación/división se resuelven primero que la suma/resta,
// tal como en matemáticas normales (2+3*4 = 14, no 20).
expr
    : expr op=('*'|'/') expr  # MulDiv
    | expr op=('+'|'-') expr  # AddSub
    | INT                     # int
    | ID                      # id
    | '(' expr ')'            # parens
    ;


// ------------------------------------------------------------
// REGLAS LÉXICAS (empiezan con MAYÚSCULA)
// Describen cómo se forman los tokens a partir de caracteres sueltos
// ------------------------------------------------------------

MUL     : '*' ;
DIV     : '/' ;
ADD     : '+' ;
SUB     : '-' ;

ID      : [a-zA-Z]+ ;       // una variable: una o más letras (ej: x, total, n)
INT     : [0-9]+ ;          // un número entero: uno o más dígitos
NEWLINE : '\r'? '\n' ;      // fin de línea (para saber dónde termina cada instrucción)
WS      : [ \t]+ -> skip ;  // espacios y tabulaciones se ignoran (no son tokens)
