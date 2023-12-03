package Statements;

import Expressions.Expression;

public class StmtReturn extends Statement {
    final Expression value;

    public StmtReturn(Expression value) {
        this.value = value;
    }
}
