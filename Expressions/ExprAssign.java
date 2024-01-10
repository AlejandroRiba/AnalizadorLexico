package Expressions;

import Utils.TablaSimbolos;
import Utils.Token;

public class ExprAssign extends Expression{
    final Token name;
    final Expression value;

    public ExprAssign(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Object resolver(TablaSimbolos tabla){
        if(tabla.existeIdentificador(name.getLexema())){
            Object valor = value.resolver(tabla);
            tabla.asignar(name.getLexema(), valor);
            return valor;
        }else{
            throw new RuntimeException("Variable no definida '" + name.getLexema() + "'");
        }
    }
}
