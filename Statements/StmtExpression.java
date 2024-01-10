package Statements;

import Expressions.Expression;
import Utils.TablaSimbolos;

public class StmtExpression extends Statement {
    final Expression expression;

    public StmtExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object ejecutar(TablaSimbolos tabla){
        return expression.resolver(tabla);
    }

}
