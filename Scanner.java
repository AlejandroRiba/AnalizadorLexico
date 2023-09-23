import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and",    TipoToken.AND);
        palabrasReservadas.put("else",   TipoToken.ELSE);
        palabrasReservadas.put("false",  TipoToken.FALSE);
        palabrasReservadas.put("for",    TipoToken.FOR);
        palabrasReservadas.put("fun",    TipoToken.FUN);
        palabrasReservadas.put("if",     TipoToken.IF);
        palabrasReservadas.put("null",   TipoToken.NULL);
        palabrasReservadas.put("or",     TipoToken.OR);
        palabrasReservadas.put("print",  TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true",   TipoToken.TRUE);
        palabrasReservadas.put("var",    TipoToken.VAR);
        palabrasReservadas.put("while",  TipoToken.WHILE);
    }

    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    public Scanner(String source){
        this.source = source + " ";
    }

    public List<Token> scan() throws Exception {
        String lexema = "";
        int estado = 0;
        char c;

        for(int i=0; i<source.length(); i++){
            c = source.charAt(i);

            switch (estado){
                case 0:
                    if(Character.isLetter(c)){
                        estado = 13;
                        lexema += c;
                    }
                    else if(Character.isDigit(c)){
                        estado = 15;
                        lexema += c;

                    }

                    break;

                case 13:
                    if(Character.isLetter(c) || Character.isDigit(c)){
                        estado = 13;
                        lexema += c;
                    }
                    else{
                        // Vamos a crear el Token de identificador o palabra reservada
                        TipoToken tt = palabrasReservadas.get(lexema);

                        if(tt == null){
                            Token t = new Token(TipoToken.IDENTIFIER, lexema);
                            tokens.add(t);
                        }
                        else{
                            Token t = new Token(tt, lexema);
                            tokens.add(t);
                        }

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 15:
                    if(Character.isDigit(c)){
                        estado = 15;
                        lexema += c;
                    }
                    else if(c == '.'){
                        estado = 16;
                        lexema += c;
                    }
                    else if(c == 'E'){
                        estado = 18;
                        lexema += c;
                    }
                    else{
                        Token t = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(lexema));
                        tokens.add(t); //Aquí se mandaría al estado 22 siguiendo el afd

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 16:
                    if(Character.isDigit(c)){
                        estado = 17;
                        lexema += c;
                    }
                    else{
                        //tenemos que ver si hay algún error aquí): no sé cómo aún
                        estado = 0;
                        lexema = ""; //No generamos token y lo mandamos de regreso
                    }
                    break;
                case 17:
                    if(Character.isDigit(c)){
                        estado = 17;
                        lexema += c;
                    }
                    else if(c == 'E'){
                        estado = 18;
                        lexema += c;
                    }
                    else {
                        Token t = new Token(TipoToken.NUMBER, lexema);
                        tokens.add(t); //Aquí se mandaría al estado 23 siguiendo el afd

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 18:
                    if(c == '+' || c == '-'){
                       estado = 19;
                       lexema += c;
                    }
                    else if(Character.isDigit(c)){
                        estado = 20;
                        lexema += c;
                    }
                    else{
                        //tenemos que ver si hay algún error aquí): no sé cómo aún
                        estado = 0;
                        lexema = ""; //No generamos token y lo mandamos de regreso
                    }
                    break;
                case 19:
                    if(Character.isDigit(c)){
                        estado = 20;
                        lexema += c;
                    }
                    else{
                        //tenemos que ver si hay algún error aquí): no sé cómo aún
                        estado = 0;
                        lexema = ""; //No generamos token y lo mandamos de regreso
                    }
                    break;
                case 20:
                    if(Character.isDigit(c)){
                        estado = 20;
                        lexema += c;
                    }
                    else{
                        Token t = new Token(TipoToken.NUMBER, lexema);
                        tokens.add(t); //Aquí se mandaría al estado 21 siguiendo el afd

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
            }
        }


        return tokens;
    }
}

