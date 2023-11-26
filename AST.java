import java.util.List;

public class AST {
    private final List<Token> tokens;
    private int i = 0;
    private Token preanalisis;

    public AST(List<Token> tokens) {
        this.tokens = tokens;
    }

    // ...
    // ...
    // ...

    private void term(){
        factor();
        //term2();
    }


    private Expression factor(){
        Expression expr = unary();
        expr = factor2(expr);
        return expr;
    }

    private Expression factor2(Expression expr){
        Token operador;
        Expression expr2, expb;

        switch (preanalisis.tipo){
            case SLASH:
                match(TipoToken.SLASH);
                operador = previous();
                expr2 = unary();
                expb = new ExprBinary(expr, operador, expr2);
                return factor2(expb);
            case STAR:
                match(TipoToken.STAR);
                operador = previous();
                expr2 = unary();
                expb = new ExprBinary(expr, operador, expr2);
                return factor2(expb);
        }
        return expr;
    }

    private Expression unary(){
        switch (preanalisis.tipo){
            case BANG:
                match(TipoToken.BANG);
                Token operador = previous();
                Expression expr = unary();
                return new ExprUnary(operador, expr);
            case MINUS:
                match(TipoToken.MINUS);
                operador = previous();
                expr = unary();
                return new ExprUnary(operador, expr);
            default:
                return call();
        }
    }

    private Expression call(){
        Expression expr = primary();
        expr = call2(expr);
        return expr;
    }

    private Expression call2(Expression expr){
        switch (preanalisis.tipo){
            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                List<Expression> lstArguments = null; //= argumentsOptional();
                match(TipoToken.RIGHT_PAREN);
                ExprCallFunction ecf = new ExprCallFunction(expr, lstArguments);
                return call2(ecf);
        }
        return expr;
    }

    private Expression primary(){
        switch (preanalisis.tipo){
            case TRUE:
                match(TipoToken.TRUE);
                return new ExprLiteral(true);
            case FALSE:
                match(TipoToken.FALSE);
                return new ExprLiteral(false);
            case NULL:
                match(TipoToken.NULL);
                return new ExprLiteral(null);
            case NUMBER:
                match(TipoToken.NUMBER);
                Token numero = previous();
                return new ExprLiteral(numero.literal);
            case STRING:
                match(TipoToken.STRING);
                Token cadena = previous();
                return new ExprLiteral(cadena.literal);
            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                Token id = previous();
                return new ExprVariable(id);
            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                Expression expr = null;//expression();
                // Tiene que ser cachado aquello que retorna
                match(TipoToken.RIGHT_PAREN);
                return new ExprGrouping(expr);
        }
        return null;
    }


    private void match(TipoToken tt){
        if(preanalisis.tipo ==  tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            String message = "Error. Se esperaba " + preanalisis.tipo +
                    " pero se encontró " + tt;
        }
    }


    private Token previous() {
        return this.tokens.get(i - 1);
    }
}
