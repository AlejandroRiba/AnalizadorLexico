package Expressions;

import Statements.StmtFunction;
import Utils.TablaSimbolos;
import Utils.Token;

import java.util.ArrayList;
import java.util.List;

public class ExprCallFunction extends Expression{
    final Expression callee;
    // final Utils.Token paren;
    final List<Expression> arguments;

    public ExprCallFunction(Expression callee, /*Utils.Token paren,*/ List<Expression> arguments) {
        this.callee = callee;
        // this.paren = paren;
        this.arguments = arguments;
    }

    @Override
    public Object resolver(TablaSimbolos tabla){
        if(!(callee instanceof ExprVariable)){
            throw new RuntimeException("No fue posible llamar a la funcion.");
        }

        Object estrFunc = callee.resolver(tabla);
        if(!(estrFunc instanceof StmtFunction)){
            throw new RuntimeException("Identificador inválido para llamar a una función.");
        }

        List<Object> argumentos = new ArrayList<>();
        for(Expression argument : arguments){
            argumentos.add( argument.resolver(tabla));
        }

        if(argumentos.size() != ((StmtFunction) estrFunc).params.size()){
            throw new RuntimeException("La llamada a función tiene más o menos argumentos de los necesarios.");
        }
        int n = 0;
        for (Token token : ((StmtFunction) estrFunc).params){
            tabla.asignar(token.getLexema(), argumentos.get(n));
            n++;
        }

        return ((StmtFunction) estrFunc).body.ejecutar(tabla);
    }
}
