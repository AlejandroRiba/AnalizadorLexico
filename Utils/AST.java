package Utils;

import java.util.ArrayList;
import java.util.Arrays;
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
        preanalisis = this.tokens.get(i);
    }

    public List<Statement> program(){
        List<Statement> program = new ArrayList<>();
        if(preanalisis.tipo != TipoToken.EOF){
            List<Statement>  res = declaration(program);

            return res;
        }
        return null;
    }
    private List<Statement> declaration(List<Statement> program){
        if(preanalisis.tipo == TipoToken.FUN){
            Statement stmt = funDecl();
            program.add(stmt);
            return declaration(program);
        } else if(preanalisis.tipo == TipoToken.VAR){
            Statement stmt = varDecl();
            program.add(stmt);
            return declaration(program);
        } else if (isEXPR() || preanalisis.tipo == TipoToken.FOR || preanalisis.tipo == TipoToken.IF || preanalisis.tipo == TipoToken.PRINT || preanalisis.tipo == TipoToken.RETURN || preanalisis.tipo == TipoToken.WHILE || preanalisis.tipo == TipoToken.LEFT_BRACE) {
            Statement stmt = statement();
            program.add(stmt);
            return declaration(program);
        }
        return program;
    }

    private Statement funDecl(){
        match(TipoToken.FUN);
        return function();
    }

    private Statement varDecl(){
        match(TipoToken.VAR);
        match(TipoToken.IDENTIFIER);
        Token id = previous();
        Expression expr = varInit();
        match(TipoToken.SEMICOLON);
        return new StmtVar(id,expr);
    }

    private Expression varInit(){
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            return expression();
        }
        return null;
    }

    private Statement statement(){
        if(isEXPR()){
            return exprStmt();
        } else if(preanalisis.tipo == TipoToken.FOR){
            return forStmt();
        } else if(preanalisis.tipo == TipoToken.IF){
            return ifStmt();
        } else if(preanalisis.tipo == TipoToken.PRINT){
            return printStmt();
        } else if(preanalisis.tipo == TipoToken.RETURN){
            return returnStmt();
        } else if(preanalisis.tipo == TipoToken.WHILE){
            return whileStmt();
        } else if(preanalisis.tipo == TipoToken.LEFT_BRACE){
            return block();
        }
        return null;
    }

    private Statement exprStmt(){
        Expression expr = expression();
        match(TipoToken.SEMICOLON);
        return new StmtExpression(expr);
    }

    private Statement forStmt(){
        match(TipoToken.FOR);
        match(TipoToken.LEFT_PAREN);
        Statement initializer = forStmt1();
        Expression condition = forStmt2();
        Expression increment = forStmt3();
        match(TipoToken.RIGHT_PAREN);
        Statement body = statement();
        //return new StmtFor(stmt1,expr2,expr3,body);
        //"Desugar" incremento
        if (increment != null){
            body = new StmtBlock(Arrays.asList(
                        body,
                        new StmtExpression(increment)
                )
            );
        }

        //"Desugar" condición
        if (condition == null){
            condition = new ExprLiteral(true);
        }
        body = new StmtLoop(condition,body);

        //"Desugar" inicialización
        if (initializer != null){
            body = new StmtBlock(Arrays.asList(initializer, body));
        }
        return body;
    }

    private Statement forStmt1(){
        if(preanalisis.tipo == TipoToken.VAR){
            return varDecl();
        } else if(isEXPR()){
            return exprStmt();
        }
        match(TipoToken.SEMICOLON);
        return null;
    }

    private Expression forStmt2(){
        if(isEXPR()){
            Expression expr =  expression();
            match(TipoToken.SEMICOLON);
            return expr;
        }
        match(TipoToken.SEMICOLON);
        return null;
    }

    private Expression forStmt3(){
        if(isEXPR()){
            return expression();
        }
        return null;
    }

    private Statement ifStmt(){
        match(TipoToken.IF);
        match(TipoToken.LEFT_PAREN);
        Expression cond = expression();
        match(TipoToken.RIGHT_PAREN);
        Statement thenBr = statement();
        Statement elseBr = elseStmt();
        return new StmtIf(cond,thenBr,elseBr);
    }

    private Statement elseStmt(){
        if(preanalisis.tipo == TipoToken.ELSE){
            match(TipoToken.ELSE);
            return statement();
        }
        return null;
    }

    private Statement printStmt(){
        match(TipoToken.PRINT);
        Expression expr = expression();
        match(TipoToken.SEMICOLON);
        return new StmtPrint(expr);
    }

    private Statement returnStmt(){
        match(TipoToken.RETURN);
        Expression retExp = retExpOpc();
        match(TipoToken.SEMICOLON);
        return new StmtReturn(retExp);
    }

    private Expression retExpOpc(){
        if(isEXPR()){
            return expression();
        }
        return null;
    }

    private Statement whileStmt(){
        match(TipoToken.WHILE);
        match(TipoToken.LEFT_PAREN);
        Expression expr = expression();
        match(TipoToken.RIGHT_PAREN);
        Statement body = statement();
        return new StmtLoop(expr,body);
    }

    private Statement block(){
        match(TipoToken.LEFT_BRACE);
        List<Statement> stmts = new ArrayList<>();
        stmts = declaration(stmts);
        match(TipoToken.RIGHT_BRACE);
        return new StmtBlock(stmts);
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
        Statement body = block();
        return new StmtFunction(id, params, (StmtBlock) body);
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
            System.out.println(message);
        }
    }


    private Token previous() {
        return this.tokens.get(i - 1);
    }
}
