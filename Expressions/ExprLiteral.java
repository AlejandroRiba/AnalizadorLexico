package Expressions;

import Utils.TablaSimbolos;

public class ExprLiteral extends Expression {
    final Object value;

    public ExprLiteral(Object value) {
        this.value = value;
    }

    @Override
    public Object resolver(TablaSimbolos tabla){
        return value;
    }
}
