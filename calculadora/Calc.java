import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * Programa principal de la calculadora.
 *
 * El "camino" que sigue un texto de entrada es siempre el mismo en ANTLR:
 *
 *   texto  -->  LEXER  -->  tokens  -->  PARSER  -->  árbol  -->  VISITOR  -->  resultado
 *
 * 1) El LEXER (Calculadora Lexer.java, generado por ANTLR) parte el texto
 *    en "palabras" llamadas tokens: números, '+', '-', nombres de variables, etc.
 * 2) El PARSER (CalculadoraParser.java, generado por ANTLR) agrupa esos tokens
 *    siguiendo las reglas que escribimos en Calculadora.g4, y arma un árbol.
 * 3) El VISITOR (EvalVisitor.java, lo escribimos nosotros) recorre ese árbol
 *    y va calculando los resultados.
 */
public class Calc {
    public static void main(String[] args) throws Exception {

        // --- Paso 1: leer la entrada ---
        // Si se pasa un archivo como argumento (ej: pruebas/prueba1.txt), lo leemos.
        // Si no se pasa nada, leemos lo que el usuario escriba por teclado.
        CharStream entrada;
        if (args.length > 0) {
            entrada = CharStreams.fromFileName(args[0]);
        } else {
            entrada = CharStreams.fromStream(System.in);
        }

        // --- Paso 2: convertir el texto en tokens ---
        CalculadoraLexer lexer = new CalculadoraLexer(entrada);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // --- Paso 3: convertir los tokens en un árbol, siguiendo la gramática ---
        CalculadoraParser parser = new CalculadoraParser(tokens);
        ParseTree arbol = parser.prog(); // "prog" es la regla inicial de la gramática

        // --- Paso 4: recorrer el árbol y calcular resultados ---
        // Solo evaluamos si la entrada no tuvo errores de escritura (sintaxis).
        if (parser.getNumberOfSyntaxErrors() == 0) {
            EvalVisitor evaluador = new EvalVisitor();
            evaluador.visit(arbol);
        } else {
            System.err.println("Se encontraron errores de sintaxis en la entrada.");
        }
    }
}
