package Expressions;

import Utils.TablaSimbolos;
import Utils.Token;

public class ExprBinary extends Expression{
    final Expression left;
    final Token operator;
    final Expression right;

    public ExprBinary(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object resolver(TablaSimbolos tabla){
        Object izquierda = left.resolver(tabla);
        Object derecha = right.resolver(tabla);

        switch (operator.getTipo()){
            case PLUS -> {
                if(izquierda instanceof String || derecha instanceof String){
                    return izquierda.toString() + derecha.toString();
                } else{
                    if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                        return Double.parseDouble(izquierda.toString()) + Double.parseDouble(derecha.toString());
                    } else{
                        throw new RuntimeException("Solo se pueden sumar números o concatenar cadenas");
                    }
                }
            }
            case MINUS -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) - Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se pueden operar números enteros o flotantes");
                }
            }
            case STAR -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) * Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se pueden operar números enteros o flotantes");
                }
            }
            case SLASH -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) / Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se pueden operar números enteros o flotantes");
                }
            }
            case GREATER -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) > Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se pueden operar números enteros o flotantes");
                }
            }
            case LESS -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) < Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se pueden operar números enteros o flotantes");
                }
            }
            case LESS_EQUAL -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) <= Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se pueden operar números enteros o flotantes");
                }
            }
            case GREATER_EQUAL -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) >= Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se pueden operar números enteros o flotantes");
                }
            }
            case EQUAL_EQUAL -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) == Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se pueden operar números enteros o flotantes");
                }
            }
            case BANG_EQUAL -> {
                if((izquierda instanceof Integer || izquierda instanceof Double) && (derecha instanceof Integer || derecha instanceof Double)){
                    return Double.parseDouble(izquierda.toString()) != Double.parseDouble(derecha.toString());
                } else{
                    throw new RuntimeException("Solo se pueden operar números enteros o flotantes");
                }
            }
            default -> throw new RuntimeException("Operador no reconocido");
        }
    }

}
