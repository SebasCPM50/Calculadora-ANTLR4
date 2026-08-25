# Calculadora con ANTLR4

## Estructura del repositorio

```
calculadora/
├── Calculadora.g4      # Gramática: define el lenguaje de la calculadora
├── Calc.java            # Punto de entrada (main)
├── EvalVisitor.java     # Lógica de evaluación de las expresiones
└── pruebas/             # Archivos .txt con casos de prueba
```

Al compilar, ANTLR y Java generan automáticamente varios archivos más
dentro de la misma carpeta (`CalculadoraLexer.java`, `CalculadoraParser.java`,
los `.class`, etc.). No se suben al repositorio porque se regeneran solos
cada vez que se corren los comandos de instalación — más abajo se explica
qué hace cada uno.

## Cómo correr el proyecto

Requiere tener Java (JDK) y el `.jar` de ANTLR 4.13.2 ya instalados, con el
alias `antlr4` configurado.
Ubicarse dentro de la carpeta del proyecto y ejecutar, en este orden:

```bash
cd calculadora
antlr4 -no-listener -visitor Calculadora.g4
javac *.java
java Calc pruebas/prueba1_basica.txt
```

Para probar los demás casos, se cambia solo el nombre del archivo:

```bash
java Calc pruebas/prueba2_precedencia.txt
java Calc pruebas/prueba3_variables.txt
java Calc pruebas/prueba4_division_cero.txt
java Calc pruebas/prueba5_clear.txt
```

También se puede usar sin ningún archivo, escribiendo las expresiones
directamente en la terminal:

```bash
java Calc
```
(para salir: `Ctrl+D`).

## Qué hace cada archivo

### Los que escribimos nosotros

**`Calculadora.g4`**
Es la gramática: el archivo donde se define qué se puede escribir en la
calculadora. Ahí quedan las reglas de las sumas, restas,
multiplicaciones, divisiones, la asignación de variables (`x = 5`) y el
comando `clear`.

**`Calc.java`**
Es el programa principal, el que se ejecuta con `java Calc`. Se encarga
de leer la entrada y pasarla por las tres etapas en orden: primero el
lexer, luego el parser, y al final el visitor, que es quien entrega el
resultado.

**`EvalVisitor.java`**
Es el que hace los cálculos. Recorre el árbol que arma el parser y va
resolviendo cada operación. Tiene un método distinto para cada tipo de
instrucción: uno para asignar variables, uno para multiplicar/dividir,
uno para sumar/restar, uno para el comando `clear`, etc.

**`pruebas/*.txt`**
Son los archivos de prueba, ya listos para correr: aritmética básica,
precedencia y paréntesis, variables, división entre cero, y el comando
`clear`.

### Los que se generan solos al correr `antlr4 -no-listener -visitor Calculadora.g4`

Estos archivos no se escriben a mano — ANTLR los crea automáticamente a
partir de `Calculadora.g4` cada vez que se corre ese comando.

- **`CalculadoraLexer.java`** — parte el texto de entrada en piezas
  sueltas (tokens): números, `+`, `-`, nombres de variables, etc.
- **`CalculadoraParser.java`** — toma esas piezas y arma con ellas un
  árbol, siguiendo las reglas escritas en el `.g4`.
- **`CalculadoraVisitor.java`** — una lista de métodos vacíos, uno por
  cada tipo de instrucción de la gramática.
- **`CalculadoraBaseVisitor.java`** — una versión de esa lista con los
  métodos ya implementados de forma básica. `EvalVisitor.java` parte de
  aquí y sobrescribe cada método con la lógica real.
- **`CalculadoraParser$AlgoContext.java`** (hay varios, uno por cada
  regla de la gramática) — representan cada tipo de nodo del árbol, por
  ejemplo `AssignContext` para una asignación o `MulDivContext` para una
  multiplicación/división.
- **`Calculadora.tokens`** y **`Calculadora.interp`** — archivos internos
  que usa ANTLR para su propio funcionamiento. No hace falta abrirlos ni
  editarlos.

### Los que se generan al correr `javac *.java`

Java compila cada archivo `.java` y crea su versión ya lista para
ejecutar, con extensión `.class` (`Calc.class`, `EvalVisitor.class`,
`CalculadoraLexer.class`, `CalculadoraParser.class`, etc.). Estos son los
que realmente corren cuando se escribe `java Calc ...`.

## Lexer, Parser y Visitor

El flujo siempre es el mismo: **texto → Lexer → tokens → Parser → árbol →
Visitor → resultado**.

**Lexer (`CalculadoraLexer`)** — es el primero en tocar el texto. Su único
trabajo es partirlo en pedacitos llamados tokens, como subrayar cada
palabra sin entender todavía qué significan juntas. Por ejemplo, en
`x=5+3` reconoce `x` como token `ID`, `5` y `3` como `INT`, y `+` como
`ADD`. Las reglas de qué es cada token están al final de `Calculadora.g4`.
El lexer no sabe de matemáticas ni de gramática, solo reconoce símbolos
sueltos.

**Parser (`CalculadoraParser`)** — recibe la fila de tokens y los agrupa
según las reglas de `prog`, `stat` y `expr` en `Calculadora.g4`, armando
un árbol que representa la estructura de la expresión. Es el que entiende
que en `x=5+3` hay una asignación (`#assign`) con una suma adentro
(`#AddSub`), y el que resuelve la precedencia: como `MulDiv` está escrita
antes que `AddSub` en la gramática, el parser arma el árbol dejando la
multiplicación "más adentro", por eso `2+3*4` da 14 y no 20. El parser
entiende estructura, pero todavía no calcula nada.

**Visitor (`EvalVisitor`)** — recorre el árbol de abajo hacia arriba:
primero resuelve las hojas (números y variables) y va combinando
resultados hasta llegar al valor final. Por eso tiene un método por cada
tipo de nodo — `visitAssign`, `visitMulDiv`, `visitAddSub`, `visitInt`,
`visitId`, `visitClear` — y cada uno sabe hacer exactamente una cosa.

En una frase: el Lexer reconoce símbolos, el Parser entiende la
estructura, y el Visitor calcula el resultado.

## Pruebas realizadas

**Prueba 1 — Aritmética básica** (`prueba1_basica.txt`)
```
Entrada:          Salida:
3+4                7
10-2               8
6*7                42
20/4               5
```

**Prueba 2 — Precedencia y paréntesis** (`prueba2_precedencia.txt`)
```
Entrada:          Salida:
2+3*4              14
(2+3)*4            20
100/2/5            10
```
`2+3*4` da 14 porque el `*` se resuelve primero; con paréntesis,
`(2+3)*4` da 20 porque se fuerza a sumar antes.

**Prueba 3 — Variables** (`prueba3_variables.txt`)
```
Entrada:          Salida:
x = 5              x = 5
y = 6              y = 6
x+y*2              17
z                  Aviso: la variable 'z' no existe todavía, se asume 0.
                   0
```

**Prueba 4 — División entre cero** (`prueba4_division_cero.txt`)
```
Entrada:          Salida:
10/0               Error: división entre cero en '10/0'.
5+5                10
```
El programa no se rompe: avisa el error y sigue con la siguiente línea.

**Prueba 5 — Comando `clear`** (`prueba5_clear.txt`)
```
Entrada:          Salida:
x = 100            x = 100
x                  100
clear              Memoria borrada.
x                  Aviso: la variable 'x' no existe todavía, se asume 0.
                   0
```

Cada una se corre así, cambiando el nombre del archivo:
```bash
java Calc pruebas/prueba1_basica.txt
```

---

Alejandro Poveda Sandoval — Juan Pablo Bejarano Arévalo — Sebastián Chaux Palencia
