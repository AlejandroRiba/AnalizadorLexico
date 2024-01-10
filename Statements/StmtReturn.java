package Statements;

import Utils.TablaSimbolos;

import Expressions.Expression;

public class StmtReturn extends Statement {
    final Expression value;

    public StmtReturn(Expression value) {
        this.value = value;
    }

    @Override
    public Object ejecutar(TablaSimbolos tabla){
        return value.resolver(tabla);
    }

}
