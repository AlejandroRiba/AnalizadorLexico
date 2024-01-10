package Expressions;

import Utils.TablaSimbolos;
import Utils.Token;

public class ExprUnary extends Expression{
    final Token operator;
    final Expression right;

    public ExprUnary(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object resolver(TablaSimbolos tabla){
        Object derecha = right.resolver(tabla);

        switch (operator.getTipo()){
            case BANG -> {
                if(derecha instanceof Boolean){
                    return !(boolean) derecha;
                } else{
                    throw new RuntimeException("El operador '!' solo se usa en expresiones booleanas");
                }
            }
            case MINUS -> {
                if(derecha instanceof Integer || derecha instanceof Double){
                    return -(double)derecha;
                } else{
                    throw new RuntimeException("El operador menos solo se usa en números");
                }
            }
            default -> throw new RuntimeException("Operador no reconocido");
        }
    }
}
