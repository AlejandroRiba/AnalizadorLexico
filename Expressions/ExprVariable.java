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
            Object valor = tabla.obtener(name.getLexema());
            if(valor == null){
                throw new RuntimeException("Variable " + name.getLexema() + " no inicializada");
            }
            return tabla.obtener(name.getLexema());
        }else{
            throw new RuntimeException("Variable no definida '" + name.getLexema() + "'.");
        }
    }
}