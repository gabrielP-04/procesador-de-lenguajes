package procesador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AnalizadorLexico {

    private int linea = 0;
    private int puntero = 0;
    private int state = 0;
    private char[] string;
    private char c;

    private Token token;
    private boolean zonaDeclarativa = false; // Variable para viene del AS

    private TS ts = new TS();

    private BufferedReader br;
    private BufferedWriter bwTokens;
    private String cwd = System.getProperty("user.dir");

    private GestorErrores GE = new GestorErrores();

    private Set<String> palabrasReservas = new HashSet<>();

    final String[] PALABRAS_RESERVADAS = {
            "boolean", "function", "if", "input", "int", "output",
            "return", "string", "var", "void", "while"
    };

    AnalizadorLexico(String fichToRead) {
        br = openRFich(fichToRead);
        bwTokens = writeFich("tokens.txt");
        string = readFich().toCharArray();

        for (String word : PALABRAS_RESERVADAS) {
            palabrasReservas.add(word);
        }
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

    public void setZonaDeclarativa(boolean zonaDeclarativa) { // funcion que sustituye a analizador S
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

    private BufferedReader openRFich(String path) {
        BufferedReader br = null;
        try {
            // Abrir archivo de entrada para lectura
            FileReader fr = new FileReader(path); // crea FileReader para el archivo de entrada
            br = new BufferedReader(fr); // usamos BufferReader para leer el archivo de manera eficiente
        } catch (IOException e) {
            e.printStackTrace();

        }
        return br;
    }

    /**
     * Método para leer un una línea del fichero.
     * 
     * @return La línea a leer.
     */

    private String readFich() {
        String string = ""; // si quisieramos leer una linea creariamos una variable String para almacenarla
        // es un int porque la funcion read devuelve el caracter leido en formato ASCII
        try {
            string = br.readLine();
            if (string != null)
                string += "\n";
            linea++;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    Token getTokens() {
        token = ALex();
        if (token == null) {
            state = 0;
            return token;

        } else if (token.getType() == TokenType.finFich) {
            try {
                bwTokens.write(token.toString());
                br.close();
                bwTokens.close();
                GE.terminarGE();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return token;
        } else {
            try {
                bwTokens.write(token.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return token;
        }
    }

    /**
     * Método para crear un fichero para escritura en el directorio data del
     * proyecto.
     * Se usará para crear el fichero de tokens y la tabla de símbolos
     * 
     * @param nameFich Nombre del fichero
     * @return Buffer donde se introducirá los caracteres para que se escriban en el
     *         fichero.
     * 
     */
    private BufferedWriter writeFich(String nameFich) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(cwd + "/data/aLex/" + nameFich);
        } catch (IOException e) {
            e.printStackTrace();
        } // crea FileWriter para crear el archivo de salida
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }

    private void getChar() {
        c = string[puntero];

    }

    Token ALex() {

        state = 0;

        int num = 0;
        int cont = 0;
        String lex = "";
        Token token = null;

        while (token == null && state != -1) {
            if (puntero >= string.length) {
                string = null;
                String str = readFich();
                if (str != null) {

                    if (str.isEmpty())
                        continue;

                    string = str.toCharArray();
                } else {
                    state = 24; // EOF
                }

                puntero = 0;

            }

            if (string != null) {
                getChar();
                char accion = MT_AFD(c);

                switch (accion) {
                    case 'A':
                        puntero++;
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
                            p = ts.findSymbolCurrent(lex);
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
