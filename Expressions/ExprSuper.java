package Expressions;

import Utils.Token;

public class ExprSuper extends Expression {
    // final Utils.Token keyword;
    final Token method;

    ExprSuper(Token method) {
        // this.keyword = keyword;
        this.method = method;
    }
}