package Statements;

import Utils.TablaSimbolos;

import Expressions.Expression;

public class StmtLoop extends Statement {
    final Expression condition;
    final Statement body;

    public StmtLoop(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Object ejecutar(TablaSimbolos tabla){
        while ((boolean)condition.resolver(tabla)){
            body.ejecutar(tabla);
        }
        return null;
    }

}
