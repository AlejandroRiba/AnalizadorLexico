import java.util.List;
public class ASDR implements Parser{
    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public ASDR(List<Token> tokens){
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }

    @Override
    public boolean parse() {
        i=0;
        PROGRAM();

        if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
            System.out.println("Codigo sintacticamente correcto");
            return  true;
        }else {
            return false;
        }
    }

    private void PROGRAM(){ //PROGRAM -> DECLARATION
        if(isEXPR_STMTderiv() || preanalisis.tipo == TipoToken.FUN || preanalisis.tipo == TipoToken.VAR || preanalisis.tipo == TipoToken.FOR || preanalisis.tipo == TipoToken.IF || preanalisis.tipo == TipoToken.PRINT || preanalisis.tipo == TipoToken.RETURN || preanalisis.tipo == TipoToken.WHILE || preanalisis.tipo == TipoToken.LEFT_BRACE || preanalisis.tipo == TipoToken.EOF) {
            DECLARATION();
        } else{
            System.out.println(ANSI_RED + "Error sintactico encontrado, se esperaba 'fun', 'var', 'for', 'if', 'print', 'return', 'while', '{', '!', '-', 'true', 'false', 'null', un numero, una cadena, un identificador o '('" + ANSI_RESET);
            Main.error(1,"Error");
        }
    }

    private void DECLARATION(){ //DECLARATION -> FUN_DECL DECLARATION | VAR_DECL DECLARATION | STATEMENT DECLARATION | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.FUN){
            FUN_DECL();
            DECLARATION();
        } else if(preanalisis.tipo == TipoToken.VAR){
            VAR_DECL();
            DECLARATION();            
        } else if (isEXPR_STMTderiv() || preanalisis.tipo == TipoToken.FOR || preanalisis.tipo == TipoToken.IF || preanalisis.tipo == TipoToken.PRINT || preanalisis.tipo == TipoToken.RETURN || preanalisis.tipo == TipoToken.WHILE || preanalisis.tipo == TipoToken.LEFT_BRACE) {
            STATEMENT();
            DECLARATION();
        }
    }

    private void FUN_DECL(){ //FUN_DECL -> fun FUNCTION
        if(hayErrores)
            return;

        coincidir(TipoToken.FUN);
        FUNCTION();
    }

    private void VAR_DECL(){ //VAR_DECL -> var id VAR_INIT;
        if(hayErrores)
            return;

        coincidir(TipoToken.VAR);
        coincidir(TipoToken.IDENTIFIER);
        VAR_INIT();
        coincidir(TipoToken.SEMICOLON);
    }

    private void VAR_INIT(){ //VAR_INIT -> = EXPRESSION | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.EQUAL) {
            coincidir(TipoToken.EQUAL);
            EXPRESSION();
        }
    }

    private void STATEMENT(){ //STATEMENT -> EXPR_STMT | FOR_STMT | IF_STMT | PRINT_STMT | RETURN_STMT | WHILE_STMT | BLOCK
        if(hayErrores)
            return;

        if(isEXPR_STMTderiv()){
            EXPR_STMT();
        } else if(preanalisis.tipo == TipoToken.FOR){
            FOR_STMT();
        } else if(preanalisis.tipo == TipoToken.IF){
            IF_STMT();
        } else if(preanalisis.tipo == TipoToken.PRINT){
            PRINT_STMT();
        } else if(preanalisis.tipo == TipoToken.RETURN){
            RETURN_STMT();
        } else if(preanalisis.tipo == TipoToken.WHILE){
            WHILE_STMT();
        } else if(preanalisis.tipo == TipoToken.LEFT_BRACE){
            BLOCK();
        } else{
            hayErrores = true;
            System.out.println(ANSI_RED + "Error sintactico encontrado, se esperaba 'if', 'for', 'print', 'return', 'while', '{', '!', '-', 'true', 'false', 'null', un numero, una cadena, un identificador o '('" + ANSI_RED);
            Main.error(1,"Error");
        }
    }

    private void EXPR_STMT(){ //EXPR_STMT -> EXPRESSION;
        if(hayErrores)
            return;

        EXPRESSION();
        coincidir(TipoToken.SEMICOLON);
    }

    private void FOR_STMT(){ //FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
        if(hayErrores)
            return;

        coincidir(TipoToken.FOR);
        coincidir(TipoToken.LEFT_PAREN);
        FOR_STMT_1();
        FOR_STMT_2();
        FOR_STMT_3();
        coincidir(TipoToken.RIGHT_PAREN);
        STATEMENT();
    }

    private void FOR_STMT_1(){ //FOR_STMT_1 -> VAR_DECL | EXPR_STMT | ;
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.VAR){
            VAR_DECL();
        } else if(isEXPR_STMTderiv()){
            EXPR_STMT();
        } else if(preanalisis.tipo == TipoToken.SEMICOLON){
            coincidir(TipoToken.SEMICOLON);
        } else{
            hayErrores = true;
            System.out.println(ANSI_RED + "Error sintactico encontrado, se esperaba 'var', ';', '!', '-', 'true', 'false', 'null', un numero, una cadena, un identificador o '('" + ANSI_RESET);
            Main.error(1,"Error");
        }
    }

    private void FOR_STMT_2(){ //FOR_STMT_2 -> EXPRESSION | ;
        if(hayErrores)
            return;

        if(isEXPR_STMTderiv()){
            EXPRESSION();
            coincidir(TipoToken.SEMICOLON);
        } else if(preanalisis.tipo == TipoToken.SEMICOLON){
            coincidir(TipoToken.SEMICOLON);
        } else{
            hayErrores = true;
            System.out.println(ANSI_RED + "Error sintactico encontrado, se esperaba ';', '!', '-', 'true', 'false', 'null', un numero, una cadena, un identificador o '('" + ANSI_RESET);
            Main.error(1,"Error");
        }
    }

    private void FOR_STMT_3(){ //FOR_STMT_3 -> EXPRESSION | Ɛ
        if(hayErrores)
            return;
        
        if(isEXPR_STMTderiv()){
            EXPRESSION();
        }
    }

    private void IF_STMT(){ //IF_STMT -> if ( EXPRESSION ) STATEMENT ELSE_STATEMENT
        if(hayErrores)
            return;

        coincidir(TipoToken.IF);
        coincidir(TipoToken.LEFT_PAREN);
        EXPRESSION();
        coincidir(TipoToken.RIGHT_PAREN);
        STATEMENT();
        ELSE_STATEMENT();
    }

    private void ELSE_STATEMENT(){ //ELSE_STATEMENT -> else STATEMENT | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.ELSE){
            coincidir(TipoToken.ELSE);
            STATEMENT();
        }
    }

    private void PRINT_STMT(){ //PRINT_STMT -> print EXPRESSION;
        if(hayErrores)
            return;

        coincidir(TipoToken.PRINT);
        EXPRESSION();
        coincidir(TipoToken.SEMICOLON);
    }

    private void RETURN_STMT(){ //RETURN_STMT -> return RETURN_EXP_OPC;
        if(hayErrores)
            return;

        coincidir(TipoToken.RETURN);
        RETURN_EXP_OPC();
        coincidir(TipoToken.SEMICOLON);
    }

    private void RETURN_EXP_OPC(){ //RETURN_EXP_OPC -> EXPRESSION | Ɛ
        if(hayErrores)
            return;

        if(isEXPR_STMTderiv()){
            EXPRESSION();
        }
    }

    private void WHILE_STMT(){ //WHILE_STMT -> while ( EXPRESSION ) STATEMENT
        if(hayErrores)
            return;

        coincidir(TipoToken.WHILE);
        coincidir(TipoToken.LEFT_PAREN);
        EXPRESSION();
        coincidir(TipoToken.RIGHT_PAREN);
        STATEMENT();
    }

    private void BLOCK(){ //BLOCK -> { DECLARATION }
        if(hayErrores)
            return;

        coincidir(TipoToken.LEFT_BRACE);
        DECLARATION();
        coincidir(TipoToken.RIGHT_BRACE);
    }

    private void EXPRESSION(){ //EXPRESSION -> ASSIGNMENT
        if(hayErrores)
            return;

        ASSIGNMENT();
    }

    private void ASSIGNMENT(){ //ASSIGNMENT -> LOGIC_OR ASSIGNMENT_OPC
        if(hayErrores)
            return;

        LOGIC_OR();
        ASSIGNMENT_OPC();
    }

    private void ASSIGNMENT_OPC(){ //ASSIGNMENT_OPC -> = EXPRESSION | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.EQUAL) {
            coincidir(TipoToken.EQUAL);
            EXPRESSION();
        }
    }

    private void LOGIC_OR(){ //LOGIC_OR -> LOGIC_AND LOGIC_OR_2
        if(hayErrores)
            return;

        LOGIC_AND();
        LOGIC_OR_2();
    }

    private void LOGIC_OR_2(){ //LOGIC_OR_2 -> or LOGIC_AND LOGIC_OR_2 | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.OR){
            coincidir(TipoToken.OR);
            LOGIC_AND();
            LOGIC_OR_2();
        }
    }

    private void LOGIC_AND(){ //LOGIC_AND -> EQUALITY LOGIC_AND_2
        if(hayErrores)
            return;

        EQUALITY();
        LOGIC_AND_2();
    }

    private void LOGIC_AND_2(){ //LOGIC_AND_2 -> and EQUALITY LOGIC_AND_2 | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.AND){
            coincidir(TipoToken.AND);
            EQUALITY();
            LOGIC_AND_2();
        }
    }

    private void EQUALITY(){ //EQUALITY -> COMPARISON EQUALITY_2
        if(hayErrores)
            return;

        COMPARISON();
        EQUALITY_2();
    }

    private void EQUALITY_2(){ //EQUALITY_2 -> != COMPARISON EQUALITY_2 | == COMPARISON EQUALITY_2 | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.BANG_EQUAL){
            coincidir(TipoToken.BANG_EQUAL);
            COMPARISON();
            EQUALITY_2();
        } else if(preanalisis.tipo == TipoToken.EQUAL_EQUAL){
            coincidir(TipoToken.EQUAL_EQUAL);
            COMPARISON();
            EQUALITY_2();
        }
    }

    private void COMPARISON(){ //COMPARISON -> TERM COMPARISON_2
        if(hayErrores)
            return;

        TERM();
        COMPARISON_2();
    }

    private void COMPARISON_2(){ //COMPARISON_2 -> > TERM COMPARISON_2 | >= TERM COMPARISON_2 | < TERM COMPARISON_2 | <= TERM COMPARISON_2 | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.GREATER || preanalisis.tipo == TipoToken.GREATER_EQUAL || preanalisis.tipo == TipoToken.LESS || preanalisis.tipo == TipoToken.LESS_EQUAL){
            coincidir(preanalisis.tipo);
            TERM();
            COMPARISON_2();
        }
    }

    private void TERM(){ //TERM -> FACTOR TERM_2
        if(hayErrores)
            return;

        FACTOR();
        TERM_2();
    }

    private void TERM_2(){ //TERM_2 -> - FACTOR TERM_2 | + FACTOR TERM_2 | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.MINUS){
            coincidir(TipoToken.MINUS);
            FACTOR();
            TERM_2();
        } else if(preanalisis.tipo == TipoToken.PLUS){
            coincidir(TipoToken.PLUS);
            FACTOR();
            TERM_2();
        }
    }

    private void FACTOR(){ //FACTOR -> UNARY FACTOR_2
        if(hayErrores)
            return;

        UNARY();
        FACTOR_2();
    }

    private void FACTOR_2(){ //FACTOR_2 -> / UNARY FACTOR_2 | * UNARY FACTOR_2 | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.SLASH){
            coincidir(TipoToken.SLASH);
            UNARY();
            FACTOR_2();
        } else if(preanalisis.tipo == TipoToken.STAR){
            coincidir(TipoToken.STAR);
            UNARY();
            FACTOR_2();
        }
    }

    private void UNARY(){ //UNARY -> ! UNARY | - UNARY | CALL
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.BANG){
            coincidir(TipoToken.BANG);
            UNARY();
        } else if(preanalisis.tipo == TipoToken.MINUS){
            coincidir(TipoToken.MINUS);
            UNARY();
        } else if(preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN){
            CALL();
        } else{
            hayErrores = true;
            System.out.println(ANSI_RED + "Error sintactico encontrado, se esperaba '!', '-', 'true', 'false', 'null', un numero, una cadena, un identificador o '('" + ANSI_RESET);
            Main.error(1,"Error");
        }
    }

    private void CALL(){ //CALL -> PRIMARY CALL_2
        if(hayErrores)
            return;

        PRIMARY();
        CALL_2();
    }

    private void CALL_2(){ //CALL_2 -> ( ARGUMENTS_OPC ) CALL_2 | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            coincidir(TipoToken.LEFT_PAREN);
            ARGUMENTS_OPC();
            coincidir(TipoToken.RIGHT_PAREN);
            CALL_2();
        }
    }

    private void PRIMARY(){ //PRIMARY -> true | false | null | number | string | id | ( EXPRESSION )
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER){
            coincidir(preanalisis.tipo);
        } else if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            coincidir(TipoToken.LEFT_PAREN);
            EXPRESSION();
            coincidir(TipoToken.RIGHT_PAREN);
        } else{
            hayErrores = true;
            System.out.println(ANSI_RED + "Error sintactico encontrado, se esperaba 'true', 'false', 'null', un numero, una cadena, un identificador o '('" + ANSI_RESET);
            Main.error(1,"Error");
        }
    }

    private void FUNCTION(){ //FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
        if(hayErrores)
            return;

        coincidir(TipoToken.IDENTIFIER);
        coincidir(TipoToken.LEFT_PAREN);
        PARAMETERS_OPC();
        coincidir(TipoToken.RIGHT_PAREN);
        BLOCK();
    }

    /*private void FUNCTIONS(){ //FUNCTIONS -> FUN_DECL FUNCTIONS | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.FUN){
            FUN_DECL();
            FUNCTIONS();
        }
    }*/

    private void PARAMETERS_OPC(){ //PARAMETERS_OPC -> PARAMETERS | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            PARAMETERS();
        }
    }

    private void PARAMETERS(){ //PARAMETERS -> id PARAMETERS_2
        if(hayErrores)
            return;

        coincidir(TipoToken.IDENTIFIER);
        PARAMETERS_2();
    }

    private void PARAMETERS_2(){ //PARAMETERS_2 -> , id PARAMETERS_2 | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.COMMA){
            coincidir(TipoToken.COMMA);
            coincidir(TipoToken.IDENTIFIER);
            PARAMETERS_2();
        }
    }

    private void ARGUMENTS_OPC(){ //ARGUMENTS_OPC -> EXPRESSION ARGUMENTS | Ɛ
        if(hayErrores)
            return;

        if(isEXPR_STMTderiv()){
            EXPRESSION();
            ARGUMENTS();
        }
    }

    private void ARGUMENTS(){ //ARGUMENTS -> , EXPRESSION ARGUMENTS | Ɛ
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.COMMA){
            coincidir(TipoToken.COMMA);
            EXPRESSION();
            ARGUMENTS();
        }
    }

    private boolean isEXPR_STMTderiv(){
        return preanalisis.tipo == TipoToken.BANG || preanalisis.tipo == TipoToken.MINUS || preanalisis.tipo == TipoToken.TRUE || preanalisis.tipo == TipoToken.FALSE || preanalisis.tipo == TipoToken.NULL || preanalisis.tipo == TipoToken.NUMBER || preanalisis.tipo == TipoToken.STRING || preanalisis.tipo == TipoToken.IDENTIFIER || preanalisis.tipo == TipoToken.LEFT_PAREN;
    }
    private void coincidir(TipoToken t) {
        if (hayErrores)
            return;

        if (preanalisis.tipo == t) {
            i++;
            preanalisis = tokens.get(i);
        } else {
            hayErrores = true;
            System.out.println(ANSI_RED + "Error sintactico encontrado, se esperaba " + t + ANSI_RESET);
            Main.error(1,"Error");

        }
    }
}
