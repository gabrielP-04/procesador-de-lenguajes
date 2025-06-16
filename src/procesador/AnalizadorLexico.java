package procesador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class AnalizadorLexico {

    // Variables para manejar el fichero
    private int linea = 0;
    private int puntero = 0;

    // Variables internas del ALex
    private int state = 0;
    private char[] string;
    private char c;

    // Varibales para ASin
    private Token token;
    private boolean zonaDeclarativa = false;

    // Tabla de símbolos
    private TS ts = new TS();

    // Variables de lectura y escritura del fichero
    private BufferedReader br;
    private BufferedWriter bwTokens;
    private String cwd = System.getProperty("user.dir");

    private GestorErrores GE = new GestorErrores();

    private Set<String> palabrasReservas = Set.of("boolean", "function", "if", "input", "int", "output",
            "return", "string", "var", "void", "while");

    AnalizadorLexico(String fichToRead) throws IOException {
        this.br = openRFich(fichToRead); // abrimos el fichero
        this.bwTokens = writeFich("tokens.txt"); // Creamos el fichero para tokens
        this.string = readFich().toCharArray(); // Leemos la primera línea
    }

    public int getPuntero() {
        return puntero;
    }

    public int getLinea() {
        return linea;
    }

    public void setTs(TS ts) {
        this.ts = ts;
    }

    public TS getTs() {
        return ts;
    }

    public GestorErrores getGE() {
        return GE;
    }

    public void setZonaDeclarativa(boolean zonaDeclarativa) {
        this.zonaDeclarativa = zonaDeclarativa;
    }

    public boolean getZonaDeclarativa() {
        return zonaDeclarativa;
    }

    Token getToken() {
        return token;
    }

    /**
     * Método para abrir un archivo para lectura.
     * 
     * @param path El nombre del fichero con el texto a analizar.
     * @return El buffer donde se leera el contenido del fichero.
     * 
     */

    private BufferedReader openRFich(String path) throws IOException {
        BufferedReader br = null;
        // Abrir archivo de entrada para lectura
        FileReader fr = new FileReader(path); // crea FileReader para el archivo de entrada
        br = new BufferedReader(fr); // usamos BufferReader para leer el archivo de manera eficiente
        return br;
    }

    /**
     * Método para leer un una línea del fichero.
     * 
     * @return La línea a leer.
     */

    private String readFich() {
        String string = "";
        try {
            string = br.readLine(); // Lee la línea
        } catch (Exception e) {
            e.printStackTrace();
        }

        linea++;

        return string;
    }

    /**
     * Método para crear un fichero de escritura en el directorio data del
     * proyecto.
     * Se usará para crear el fichero de tokens
     * 
     * @param nameFich Nombre del fichero
     * @return Buffer donde se introducirá los caracteres para que se escriban en el
     *         fichero.
     * 
     */
    private BufferedWriter writeFich(String nameFich) throws IOException {
        FileWriter fw = null;
        fw = new FileWriter(cwd + "/data/aLex/" + nameFich);
        // crea FileWriter para crear el archivo de salida
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }

    /**
     * Función principal del analizador léxico.
     * Escribe el token en el fichero.
     * 
     * @return Token del fichero
     */
    Token getTokenFich() throws IOException {
        token = ALex();

        if (token == null) {
            state = 0;
            return token;

        } else if (token.getType() == TokenType.finFich) {
            bwTokens.write(token.toString());
            br.close();
            bwTokens.close();
            return token;
        } else {
            bwTokens.write(token.toString());

            return token;
        }
    }

    /**
     * Función que maneja la matriz de estado y las acciones
     * 
     * @return Token del fichero
     */
    Token ALex() {

        state = 0;

        int num = 0;
        int cont = 0;
        String lex = "";
        Token token = null;

        // Cambia de línea a al final del array de caracteres
        while (token == null && state != -1) {
            if (puntero >= string.length) {
                String str = readFich();
                if (str != null) {

                    if (str.isEmpty()) // Si la línea esta vacía sigue leyendo del fichero.
                        continue;

                    string = str.toCharArray();
                } else {
                    state = 24; // EOF
                    string = null;
                }
                puntero = 0;
            }

            if (string != null) {
                c = string[puntero]; // Obtiene un caracter.
                char accion = MT_AFD(c); // Verifica la matriz del autómata

                switch (accion) {
                    case 'A':
                        puntero++; // Avanza el puntero.
                        break;

                    case 'B':
                        puntero++;
                        num = Character.getNumericValue(c);
                        break;

                    case 'C':
                        puntero++;
                        num = num * 10 + Character.getNumericValue(c);
                        break;

                    case 'D':
                        if (num > 32767) {
                            GE.selgErrorAnalizador("Lx-50", puntero, linea); // entero fuera del rango permitido
                            state = 0;
                        } else {

                            return token = new Token(TokenType.entero, num);
                        }
                        break;

                    case 'E':
                        lex += c;
                        cont++;
                        puntero++;
                        break;

                    case 'F':
                        lex += c;
                        puntero++;

                        if (cont > 64) {
                            GE.selgErrorAnalizador("Lx-51", puntero, linea); // cadena fuera del rango permitido
                            state = 0;
                        } else {

                            return token = new Token(TokenType.cadena, lex);
                        }
                        break;

                    case 'G':
                        puntero++;
                        lex += c;
                        break;

                    case 'H': {
                        int index = isPalabraReservada(lex);
                        int p;
                        if (index >= 0)
                            return token = new Token(TokenType.values()[index]);
                        else if (zonaDeclarativa) {
                            p = ts.findSymbolCurrent(lex); // tabla actual?
                            if (p == -1) {
                                p = ts.addSymbol(lex);
                                return token = new Token(TokenType.id, p);
                            } else {
                                GE.selgErrorAnalizador("Lx-52", puntero, linea); // variable ya declarada
                                state = -1;
                                break;
                            }
                        } else {
                            p = ts.findSymbol(lex);
                            if (p == -1) {
                                p = ts.addSymbolGlobal(lex);
                                return token = new Token(TokenType.id, p);
                            } else
                                return token = new Token(TokenType.id, p); // Tabla de simbolos
                        }
                    }

                    case 'I': {
                        puntero++;
                        return token = new Token(TokenType.opIgual);
                    }

                    case 'J': {
                        return token = new Token(TokenType.opAsig);
                    }

                    case 'K': {
                        puntero++;
                        return token = new Token(TokenType.opOr);
                    }

                    case 'L':
                        puntero++;
                        return token = new Token(TokenType.opAsigDiv);

                    case 'M':
                        puntero++;
                        return token = new Token(TokenType.opSuma);

                    case 'N': {
                        puntero++;
                        return token = new Token(TokenType.puntoComa);
                    }

                    case 'O':
                        puntero++;
                        return token = new Token(TokenType.coma);

                    case 'P':
                        puntero++;
                        return token = new Token(TokenType.llave, 1);

                    case 'Q':
                        puntero++;
                        return token = new Token(TokenType.llave, 2);

                    case 'R': {
                        puntero++;
                        return token = new Token(TokenType.paren, 1);
                    }

                    case 'S': {
                        puntero++;
                        return token = new Token(TokenType.paren, 2);
                    }

                    case 'T': {
                        return new Token(TokenType.finFich);

                    }
                    default: {
                        state = -1;
                        break;
                    }
                }

            } else
                return token = new Token(TokenType.finFich);
        }
        return token;

    }

    private int isPalabraReservada(String word) {
        boolean isPalabraReservada = palabrasReservas.contains(word);
        int index = -1;
        if (isPalabraReservada) {
            for (TokenType prs : TokenType.values()) {
                if (prs.getPR().equals(word)) {
                    index = prs.ordinal();
                    break;
                }
            }
        }
        return index;
    }

    char MT_AFD(char c) {

        switch (this.state) {
            case 0:
                if (isDelemiter(c))
                    return 'A';

                else if (isNumber(c)) {
                    state = 1;
                    return 'B';

                } else if (c == '\'') {
                    state = 2;
                    return 'E';

                } else if (isletter(c)) {
                    state = 3;
                    return 'G';

                } else if (c == '=') {
                    state = 4;
                    return 'A';

                } else if (c == '|') {
                    state = 5;
                    return 'A';

                } else if (c == '/') {
                    state = 6;
                    return 'A';

                } else if (c == '+') {
                    state = 17;
                    return 'M';

                } else if (c == ';') {
                    state = 18;
                    return 'N';

                } else if (c == ',') {
                    state = 19;
                    return 'O';

                } else if (c == '{') {
                    state = 20;
                    return 'P';

                } else if (c == '}') {
                    state = 21;
                    return 'Q';

                } else if (c == '(') {
                    state = 22;
                    return 'R';

                } else if (c == ')') {
                    state = 23;
                    return 'S';

                } else if (state == 24) {
                    return 'T';

                } else
                    GE.selgErrorAnalizador("Lx-55", puntero, linea);
                puntero++;
                return 0;

            case 1:
                if (isNumber(c)) {
                    return 'C';
                } else {
                    state = 10;
                    return 'D';
                }
            case 2:
                if (c == '\'') {
                    state = 11;
                    return 'F';
                } else
                    return 'E';

            case 3:
                if (isletter(c) || isNumber(c) || c == '_') {
                    return 'G';
                } else {
                    state = 12;
                    return 'H';
                }

            case 4:
                if (c == '=') {
                    state = 13;
                    return 'I';
                } else {
                    state = 14;
                    return 'J';
                }

            case 5:
                if (c == '|') {
                    state = 15;
                    return 'K';
                } else {
                    return 0;
                }

            case 6:
                if (c == '=') {
                    state = 17;
                    return 'L';
                } else if (c == '*') {
                    state = 7;
                    return 'A';
                } else {
                    GE.selgErrorAnalizador("Lx-54", puntero, linea);
                    return 0;
                }

            case 7:
                if (c == '*') {
                    state = 8;
                    return 'A';
                } else
                    return 'A';

            case 8:
                if (c == '/') {
                    state = 0;
                    return 'A';
                } else if (c == '*'){
                    return 'A';
                    
                } else {
                    state = 7;
                    return 'A';
                }

            default:
                puntero++;
                GE.selgErrorAnalizador("Lx-50", puntero, linea);
                return 0;
        }

    }

    private boolean isletter(char c) {
        return c >= 65 && c <= 90 || c >= 97 && c <= 122;
    }

    private boolean isNumber(char c) {
        return c >= 48 && c <= 57;
    }

    private boolean isDelemiter(char c) {
        return c == ' ' || c == '\t' || c == '\n';
    }

}
