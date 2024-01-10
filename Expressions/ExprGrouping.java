package Expressions;

import Utils.TablaSimbolos;

public class ExprGrouping extends Expression {
    final Expression expression;

    public ExprGrouping(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object resolver(TablaSimbolos tabla) {
        return expression.resolver(tabla);
    }
}
