package Statements;

import Utils.TablaSimbolos;
import Utils.Token;

import java.util.List;

public class StmtFunction extends Statement {
    final Token name;
    public final List<Token> params;
    public final StmtBlock body;

    public StmtFunction(Token name, List<Token> params, StmtBlock body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }

    public String getName(){ return this.name.getLexema(); }

    @Override
    public Object ejecutar(TablaSimbolos tabla){
        tabla.asignar(name.getLexema(), this);
        return null;
    }

}
