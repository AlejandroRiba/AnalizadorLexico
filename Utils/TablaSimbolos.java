package Utils;

import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {
    private final Map<String, Object> values;
    private final TablaSimbolos superior;

    public TablaSimbolos(){
        this.superior = null;
        this.values = new HashMap<>();
    }
    public TablaSimbolos(TablaSimbolos superior){
        this.superior = superior;
        this.values = new HashMap<>();
    }

    public boolean existeIdentificador(String identificador){
        if(superior == null){
            return values.containsKey(identificador);
        } else{
            return values.containsKey(identificador) || superior.existeIdentificador(identificador);
        }
    }

    public Object obtener(String identificador) {
        if (values.containsKey(identificador)) {
            return values.get(identificador);
        } else if(superior != null) {
            return superior.obtener(identificador);
        }
        throw new RuntimeException("Variable no definida '" + identificador + "'.");
    }

    public void asignar(String identificador, Object valor){

        if(superior != null){
            if(superior.existeIdentificador(identificador))
                superior.asignar(identificador,valor);
            else
                values.put(identificador, valor);
        } else{
            values.put(identificador, valor);
        }

    }

    public TablaSimbolos getSuperior(){ return this.superior; }

}
