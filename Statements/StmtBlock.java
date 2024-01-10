package Statements;

import Utils.TablaSimbolos;

import java.util.List;

public class StmtBlock extends Statement{
    final List<Statement> statements;

    public StmtBlock(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public Object ejecutar(TablaSimbolos tabla){
        TablaSimbolos inferior = new TablaSimbolos(tabla);
        for(Statement stmt : statements){
            if(stmt != null){
                if(stmt instanceof StmtReturn){
                    return stmt.ejecutar(inferior);
                }
                stmt.ejecutar(inferior);
            }
        }
        return null;
    }

}
