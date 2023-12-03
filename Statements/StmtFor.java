package Statements;

import Expressions.Expression;

public class StmtFor extends Statement{
    final Statement forStmt1;
    final Expression forStmt2;
    final Expression forStmt3;
    final Statement body;

    StmtFor(Statement forStmt1, Expression forStmt2, Expression forStmt3, Statement body){
        this.forStmt1 = forStmt1;
        this.forStmt2 = forStmt2;
        this.forStmt3 = forStmt3;
        this.body = body;
    }

}
