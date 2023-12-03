package Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Statements.*;
import Expressions.*;

public class AST {
    private final List<Token> tokens;
    private int i = 0;
    private Token preanalisis;

    public AST(List<Token> tokens) {
        this.tokens = tokens;
    }

    // ...
    // ...
    // ...
    private Statement funDecl(){
        match(TipoToken.FUN);
        return function();
    }

    private Statement varDecl(){
        match(TipoToken.VAR);
        match(TipoToken.IDENTIFIER);
        Token id = previous();
        Statement stmt = varInit(id);
        match(TipoToken.SEMICOLON);
        return stmt;
    }

    private Statement varInit(Token id){
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            Expression expr = expression();
            return new StmtVar(id,expr);
        }
        return new StmtVar(id,null);
    }

    private Expression expression(){
        return assignment();
    }

    private Expression assignment(){
        Expression expr = logicOr();
        expr = assignmentOpc(expr);
        return expr;
    }

    private Expression assignmentOpc(Expression expr){
        Expression expr2;

        if(preanalisis.tipo == TipoToken.EQUAL){
            ExprVariable exprVar = (ExprVariable) expr;
            match(TipoToken.EQUAL);
            expr2 = expression();
            return new ExprAssign(exprVar, expr2);
        }
        return expr;
    }

    private Expression logicOr(){
        Expression expr = logicAnd();
        expr = logicOr2(expr);
        return expr;
    }

    private Expression logicOr2(Expression expr){
        Token operador;
        Expression expr2, expb;

        if(preanalisis.tipo == TipoToken.OR){
            match(TipoToken.OR);
            operador = previous();
            expr2 = logicAnd();
            expb = new ExprLogical(expr, operador, expr2);
            return logicOr2(expb);
        }
        return expr;
    }

    private Expression logicAnd(){
        Expression expr = equality();
        expr = logicAnd2(expr);
        return expr;
    }

    private Expression logicAnd2(Expression expr){
        Token operador;
        Expression expr2, expb;

        if(preanalisis.tipo == TipoToken.AND){
            match(TipoToken.AND);
            operador = previous();
            expr2 = equality();
            expb = new ExprLogical(expr, operador, expr2);
            return logicAnd2(expb);
        }
        return expr;
    }

    private Expression equality(){
        Expression expr = comparison();
        expr = equality2(expr);
        return expr;
    }

    private Expression equality2(Expression expr){
        Token operador;
        Expression expr2, expb;

        switch (preanalisis.tipo){
            case BANG_EQUAL -> {
                match(TipoToken.BANG_EQUAL);
                operador = previous();
                expr2 = comparison();
                expb = new ExprBinary(expr, operador, expr2);
                return equality2(expb);
            }
            case EQUAL_EQUAL -> {
                match(TipoToken.EQUAL_EQUAL);
                operador = previous();
                expr2 = comparison();
                expb = new ExprBinary(expr, operador, expr2);
                return equality2(expb);
            }
        }
        return expr;
    }

    private Expression comparison(){
        Expression expr = term();
        expr = comparison2(expr);
        return expr;
    }

    private Expression comparison2(Expression expr){
        Token operador;
        Expression expr2, expb;
        switch (preanalisis.tipo){
            case GREATER -> {
                match(TipoToken.GREATER);
                operador = previous();
                expr2 = term();
                expb = new ExprBinary(expr, operador, expr2);
                return comparison2(expb);
            }
            case GREATER_EQUAL -> {
                match(TipoToken.GREATER_EQUAL);
                operador = previous();
                expr2 = term();
                expb = new ExprBinary(expr, operador, expr2);
                return comparison2(expb);
            }
            case LESS -> {
                match(TipoToken.LESS);
                operador = previous();
                expr2 = term();
                expb = new ExprBinary(expr, operador, expr2);
                return comparison2(expb);
            }
            case LESS_EQUAL -> {
                match(TipoToken.LESS_EQUAL);
                operador = previous();
                expr2 = term();
                expb = new ExprBinary(expr, operador, expr2);
                return comparison2(expb);
            }
        }
        return expr;
    }

    private Expression term(){
        Expression expr = factor();
        expr = term2(expr);
        return expr;
    }

    private Expression term2(Expression expr){
        Token operador;
        Expression expr2, expb;
        switch (preanalisis.tipo){
            case MINUS -> {
                match(TipoToken.MINUS);
                operador = previous();
                expr2 = factor();
                expb = new ExprBinary(expr, operador, expr2);
                return term2(expb);
            }
            case PLUS -> {
                match(TipoToken.PLUS);
                operador = previous();
                expr2 = factor();
                expb = new ExprBinary(expr, operador, expr2);
                return term2(expb);
            }
        }
        return expr;
    }

    private Expression factor(){
        Expression expr = unary();
        expr = factor2(expr);
        return expr;
    }

    private Expression factor2(Expression expr){
        Token operador;
        Expression expr2, expb;

        switch (preanalisis.tipo) {
            case SLASH -> {
                match(TipoToken.SLASH);
                operador = previous();
                expr2 = unary();
                expb = new ExprBinary(expr, operador, expr2);
                return factor2(expb);
            }
            case STAR -> {
                match(TipoToken.STAR);
                operador = previous();
                expr2 = unary();
                expb = new ExprBinary(expr, operador, expr2);
                return factor2(expb);
            }
        }
        return expr;
    }

    private Expression unary(){
        Token operador;
        Expression expr;
        switch (preanalisis.tipo) {
            case BANG -> {
                match(TipoToken.BANG);
                operador = previous();
                expr = unary();
                return new ExprUnary(operador, expr);
            }
            case MINUS -> {
                match(TipoToken.MINUS);
                operador = previous();
                expr = unary();
                return new ExprUnary(operador, expr);
            }
            default -> {
                return call();
            }
        }
    }

    private Expression call(){
        Expression expr = primary();
        expr = call2(expr);
        return expr;
    }

    private Expression call2(Expression expr){
        if (preanalisis.tipo == TipoToken.LEFT_PAREN) {
            match(TipoToken.LEFT_PAREN);
            List<Expression> lstArguments = argumentsOpc();
            match(TipoToken.RIGHT_PAREN);
            ExprCallFunction ecf = new ExprCallFunction(expr, lstArguments);
            return call2(ecf);
        }
        return expr;
    }

    private Expression primary(){
        switch (preanalisis.tipo) {
            case TRUE -> {
                match(TipoToken.TRUE);
                return new ExprLiteral(true);
            }
            case FALSE -> {
                match(TipoToken.FALSE);
                return new ExprLiteral(false);
            }
            case NULL -> {
                match(TipoToken.NULL);
                return new ExprLiteral(null);
            }
            case NUMBER -> {
                match(TipoToken.NUMBER);
                Token numero = previous();
                return new ExprLiteral(numero.literal);
            }
            case STRING -> {
                match(TipoToken.STRING);
                Token cadena = previous();
                return new ExprLiteral(cadena.literal);
            }
            case IDENTIFIER -> {
                match(TipoToken.IDENTIFIER);
                Token id = previous();
                return new ExprVariable(id);
            }
            case LEFT_PAREN -> {
                match(TipoToken.LEFT_PAREN);
                Expression expr = expression();

                // Tiene que ser cachado aquello que retorna
                match(TipoToken.RIGHT_PAREN);
                return new ExprGrouping(expr);
            }
        }
        return null;
    }

    private Statement function(){
        match(TipoToken.IDENTIFIER);
        Token id = previous();
        match(TipoToken.LEFT_PAREN);
        List<Token> params = parametersOpc();
        match(TipoToken.RIGHT_PAREN);
        StmtBlock body = block();
        return new StmtFunction(id, params, body);
    }

    private List<Token> parametersOpc(){
        List<Token> params = new ArrayList<>();

        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            params = parameters(params);
            return params;
        }
        return null;
    }

    private List<Token> parameters(List<Token> params){
        match(TipoToken.IDENTIFIER);
        Token id = previous();
        params.add(id);
        params = parameters2(params);
        return params;
    }

    private List<Token> parameters2(List<Token> params){
        if(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            Token id = previous();
            params.add(id);
            return parameters2(params);
        }
        return params;
    }

    private List<Expression> argumentsOpc(){
        List<Expression> args = new ArrayList<>();

        if(isEXPR()){
            Expression expr = expression();
            args.add(expr);
            arguments(args);
            return args;
        }
        return null;
    }

    private void arguments(List<Expression> args){
        if(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            Expression expr = expression();
            args.add(expr);
            arguments(args);
        }
    }

    private boolean isEXPR(){
        return preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN;
    }

    private void match(TipoToken tt){
        if(preanalisis.tipo ==  tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            String message = "Error. Se esperaba " + preanalisis.tipo +
                    " pero se encontró " + tt;
        }
    }


    private Token previous() {
        return this.tokens.get(i - 1);
    }
}
