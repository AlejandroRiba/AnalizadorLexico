package Utils;

import Principal.Main;

import java.util.*;

public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;
    public static final Map<String, TipoToken> simbolos;
    //Constantes para imprimir texto de errores en rojo
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

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

        simbolos = new HashMap<>();
        simbolos.put("+",   TipoToken.PLUS);
        simbolos.put("-",   TipoToken.MINUS);
        simbolos.put("*",   TipoToken.STAR);
        simbolos.put("{",   TipoToken.LEFT_BRACE);
        simbolos.put("}",   TipoToken.RIGHT_BRACE);
        simbolos.put("(",   TipoToken.LEFT_PAREN);
        simbolos.put(")",   TipoToken.RIGHT_PAREN);
        simbolos.put(",",   TipoToken.COMMA);
        simbolos.put(".",   TipoToken.DOT);
        simbolos.put(";",   TipoToken.SEMICOLON);
    }

    private final String source;

    private final List<Token> tokens = new ArrayList<>();

    private final List<Character> caracteres = Arrays.asList('+', '-', '*', '{', '}', '(', ')', ',', '.', ';');

    public Scanner(String source){
        this.source = source + " ";
    }

    public List<Token> scan() throws Exception {
        String lexema = "";
        int estado = 0;
        int linea = 1;
        char c;

        for(int i=0; i<source.length(); i++){
            c = source.charAt(i);
            if(i>=1){
                if(source.charAt(i - 1) == '\n'){  //solo aumentamos el número de linea después de analizar el salto con su respectiva linea
                    linea += 1; //si el caracter anterior era salto, entonces es otra linea
                }
            }
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
                    else if(c == '>'){
                        estado = 1;
                        lexema += c;
                    }
                    else if(c == '<'){
                        estado = 4;
                        lexema += c;
                    }
                    else if(c == '='){
                        estado = 7;
                        lexema += c;
                    }
                    else if(c == '!'){
                        estado = 10;
                        lexema += c;
                    }
                    else if(c == '/'){
                        estado = 26;
                        lexema += c;
                    }
                    else if(c == '"'){
                        estado = 24;
                        lexema += c;
                    }
                    else if(caracteres.contains(c)){
                        estado = 33;
                        lexema += c;
                    }
                    else if(c > 32){ //Si la variable c es diferente a cualquier caracter especial como el espacio, '\n' u otro
                        System.out.println(ANSI_RED + "[linea " + linea + "] Error: Se encontró un caracter ajeno al lenguaje '" + c +"'" + ANSI_RESET);
                        estado = -1;
                    }

                    break;
                case 1:
                    if(c == '='){
                        lexema += c;
                        Token t = new Token(TipoToken.GREATER_EQUAL, lexema);
                        tokens.add(t);
                    }
                    else{
                        Token t = new Token(TipoToken.GREATER, lexema);
                        tokens.add(t);
                        i--;
                    }

                    estado = 0;
                    lexema = "";
                    break;
                case 4:
                    if(c == '='){
                        lexema += c;
                        Token t = new Token(TipoToken.LESS_EQUAL, lexema);
                        tokens.add(t);
                    }
                    else{
                        Token t = new Token(TipoToken.LESS, lexema);
                        tokens.add(t);
                        i--;
                    }

                    estado = 0;
                    lexema = "";
                    break;
                case 7:
                    if(c == '='){
                        lexema += c;
                        Token t = new Token(TipoToken.EQUAL_EQUAL, lexema);
                        tokens.add(t);
                    }
                    else{
                        Token t = new Token(TipoToken.EQUAL, lexema);
                        tokens.add(t);
                        i--;
                    }

                    estado = 0;
                    lexema = "";
                    break;
                case 10:
                    if(c == '='){
                        lexema += c;
                        Token t = new Token(TipoToken.BANG_EQUAL, lexema);
                        tokens.add(t);
                    }
                    else{
                        Token t = new Token(TipoToken.BANG, lexema);
                        tokens.add(t);
                        i--;
                    }

                    estado = 0;
                    lexema = "";
                    break;
                case 13:
                    if(Character.isLetter(c) || Character.isDigit(c)){
                        estado = 13;
                        lexema += c;
                    }
                    else{
                        // Vamos a crear el Utils.Token de identificador o palabra reservada
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
                        System.out.println(ANSI_RED + "[linea " + linea + "] Error: Se esperaba un número para parte decimal" + ANSI_RESET);
                        //Main.error(linea,"Se esperaba un número para parte decimal");
                        estado = -1; //No generamos token y lo mandamos al estado de error
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
                        Token t = new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema));
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
                        System.out.println(ANSI_RED + "[linea " + linea + "] Error: Se esperaba un '+', un '-' o un número para exponente" + ANSI_RESET);
                        estado = -1; //No generamos token y lo mandamos al estado de error
                    }
                    break;
                case 19:
                    if(Character.isDigit(c)){
                        estado = 20;
                        lexema += c;
                    }
                    else{
                        System.out.println(ANSI_RED + "[linea " + linea + "] Error: Se esperaba un número para parte exponente" + ANSI_RESET);
                        estado = -1; //No generamos token y lo mandamos al estado de error
                    }
                    break;
                case 20:
                    if(Character.isDigit(c)){
                        estado = 20;
                        lexema += c;
                    }
                    else{
                        Token t = new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema));
                        tokens.add(t); //Aquí se mandaría al estado 21 siguiendo el afd

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 24:
                    if(c == '\n' || i == source.length()-1){
                        System.out.println(ANSI_RED + "[linea " + linea + "] Error: Se esperaban comillas para el cierre de la cadena" + ANSI_RESET);
                        estado = -1;
                    } else if (c == '"') {
                        //aceptado
                        lexema += c; //agregamos las ultimas comillas al lexema
                        Token t = new Token(TipoToken.STRING, lexema, String.valueOf(lexema.substring(1, lexema.length()-1)));
                        tokens.add(t); //considerado edo. 25

                        estado = 0;
                        lexema = "";
                    } else{
                        estado = 24;
                        lexema += c;
                    }
                    break;
                case 26:
                    if(c == '*'){
                        estado = 27;
                    } else if (c == '/') {
                        estado = 30;
                    } else{
                        Token t = new Token(TipoToken.SLASH, lexema);
                        tokens.add(t); //estado 32

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 27:
                    if(c == '*'){
                        estado = 28;
                    } else if(i == source.length()-1){
                        System.out.println(ANSI_RED + "[linea " + linea + "] Error: Comentario multilinea no cerrado" + ANSI_RESET);
                        estado = -1;
                    } else{
                        estado = 27;
                    }
                    break;
                case 28:
                    if(c == '*'){
                        estado = 28;
                    } else if (c == '/') {
                        estado = 0;
                        lexema = ""; //aquí se acepta, pero no genera token, reiniciamos el lexema solo por si acaso, edo. 29
                    } else if(i == source.length()-1){
                        System.out.println(ANSI_RED + "[linea " + linea + "] Error: Comentario multilinea no cerrado" + ANSI_RESET);
                        estado = -1;
                    } else{
                        estado = 27;
                    }
                    break;
                case 30:
                    if(c == '\n' || i == source.length()-1){
                        estado = 0;
                        lexema = ""; //se acepta y no genera token. edo. 31
                    }
                    else{
                        estado = 30;
                    }
                    break;
                case 33:
                    TipoToken tt = simbolos.get(lexema);
                    Token t = new Token(tt, lexema);
                    tokens.add(t);

                    estado = 0;
                    lexema = "";
                    i--;
                    break;
                default: //Se usa el default como estado muerto o de error
                    lexema = "";
                    break;
            }

            if(estado==-1) {
                Main.error(1, "Error");
                break;
            }
        }

        if(estado == 0){
            Token t = new Token(TipoToken.EOF,"");
            tokens.add(t);
        }
        return tokens;
    }
}

