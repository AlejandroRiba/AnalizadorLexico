package Expressions;

import Utils.Token;

public class ExprAssign extends Expression{
    final ExprVariable name;
    final Expression value;

    public ExprAssign(ExprVariable name, Expression value) {
        this.name = name;
        this.value = value;
    }
}
