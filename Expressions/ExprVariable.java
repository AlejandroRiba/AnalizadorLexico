package Expressions;

import Utils.TablaSimbolos;
import Utils.Token;

public class ExprVariable extends Expression {
    public final Token name;

    public ExprVariable(Token name) {
        this.name = name;
    }

    public String getNombre(){
        return name.getLexema();
    }

    @Override
    public Object resolver(TablaSimbolos tabla){
        if(tabla.existeIdentificador(name.getLexema())){
            return tabla.obtener(name.getLexema());
        }else{
            throw new RuntimeException("Variable no definida '" + name.getLexema() + "'.");
        }
    }
}