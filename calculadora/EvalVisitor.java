import java.util.HashMap;
import java.util.Map;

/**
 * Este es el "cerebro" de la calculadora.
 *
 * ANTLR genera automáticamente la clase CalculadoraBaseVisitor con un método
 * "visitAlgo" vacío por cada etiqueta que pusimos en la gramática (#printExpr,
 * #assign, #clear, #blank, #MulDiv, #AddSub, #int, #id, #parens).
 *
 * Nosotros heredamos de esa clase y SOBRESCRIBIMOS (@Override) cada método
 * para decirle qué hacer en cada caso. Por eso el código queda tan ordenado:
 * cada método se encarga de UNA sola cosa.
 *
 * <Integer> significa que cada método "visit..." devuelve un número entero
 * (el resultado de evaluar ese pedazo del árbol).
 */
public class EvalVisitor extends CalculadoraBaseVisitor<Integer> {

    // "Memoria" de la calculadora: aquí se guardan las variables, por ejemplo
    // si el usuario escribe "x = 5", queda memory = { "x" -> 5 }
    private final Map<String, Integer> memoria = new HashMap<>();

    // Caso: ID '=' expr NEWLINE   (ejemplo: "x = 5")
    @Override
    public Integer visitAssign(CalculadoraParser.AssignContext ctx) {
        String nombre = ctx.ID().getText();       // el nombre de la variable, ej: "x"
        Integer valor = visit(ctx.expr());         // calculamos el valor de la derecha del "="
        if (valor != null) {
            memoria.put(nombre, valor);             // guardamos el par nombre/valor
            System.out.println(nombre + " = " + valor);
        }
        return valor;
    }

    // Caso: expr NEWLINE   (ejemplo: "3+4", solo se imprime el resultado)
    @Override
    public Integer visitPrintExpr(CalculadoraParser.PrintExprContext ctx) {
        Integer valor = visit(ctx.expr());
        if (valor != null) {
            System.out.println(valor);
        }
        return valor;
    }

    // Caso: 'clear' NEWLINE   -> borra todas las variables guardadas
    @Override
    public Integer visitClear(CalculadoraParser.ClearContext ctx) {
        memoria.clear();
        System.out.println("Memoria borrada.");
        return 0;
    }

    // Caso: NEWLINE (una línea vacía) -> no hacemos nada
    @Override
    public Integer visitBlank(CalculadoraParser.BlankContext ctx) {
        return 0;
    }

    // Caso: INT (un número, ej: "42") -> lo convertimos de texto a número
    @Override
    public Integer visitInt(CalculadoraParser.IntContext ctx) {
        return Integer.valueOf(ctx.INT().getText());
    }

    // Caso: ID (una variable, ej: "x") -> buscamos su valor en la memoria
    @Override
    public Integer visitId(CalculadoraParser.IdContext ctx) {
        String nombre = ctx.ID().getText();
        if (memoria.containsKey(nombre)) {
            return memoria.get(nombre);
        }
        // Si la variable no existe todavía, avisamos y asumimos 0
        System.err.println("Aviso: la variable '" + nombre + "' no existe todavía, se asume 0.");
        return 0;
    }

    // Caso: expr ('*' | '/') expr
    @Override
    public Integer visitMulDiv(CalculadoraParser.MulDivContext ctx) {
        Integer izquierda = visit(ctx.expr(0)); // valor del lado izquierdo
        Integer derecha = visit(ctx.expr(1));   // valor del lado derecho

        if (izquierda == null || derecha == null) {
            return null;
        }

        // ctx.op nos dice cuál de los dos operadores fue: '*' o '/'
        if (ctx.op.getType() == CalculadoraParser.MUL) {
            return izquierda * derecha;
        } else {
            // Es división. Cuidamos el caso de dividir entre 0.
            if (derecha == 0) {
                System.err.println("Error: división entre cero en '" + ctx.getText() + "'.");
                return null;
            }
            return izquierda / derecha;
        }
    }

    // Caso: expr ('+' | '-') expr
    @Override
    public Integer visitAddSub(CalculadoraParser.AddSubContext ctx) {
        Integer izquierda = visit(ctx.expr(0));
        Integer derecha = visit(ctx.expr(1));

        if (izquierda == null || derecha == null) {
            return null;
        }

        if (ctx.op.getType() == CalculadoraParser.ADD) {
            return izquierda + derecha;
        } else {
            return izquierda - derecha;
        }
    }

    // Caso: '(' expr ')'  -> simplemente evaluamos lo que está adentro
    @Override
    public Integer visitParens(CalculadoraParser.ParensContext ctx) {
        return visit(ctx.expr());
    }
}
