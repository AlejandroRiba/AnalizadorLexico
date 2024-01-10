package Statements;

import Utils.TablaSimbolos;

import Expressions.Expression;

public class StmtPrint extends Statement {
    final Expression expression;

    public StmtPrint(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object ejecutar(TablaSimbolos tabla){
        Object expresion = expression.resolver(tabla);

        System.out.println(expresion.toString());
        return null;
    }

}
