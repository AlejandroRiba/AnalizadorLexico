package Statements;

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
}
