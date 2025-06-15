package procesador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class AnalizadorSintactico {

    // private final int OPCION_RUTA = 0; //0 gabi , 1 María
    // private final String[] RUTA = {"/data/aSin/", "\\data\\aSin\\"};

    private AnalizadorLexico aLex;
    private GestorErrores GE;
    private TS ts;
    private String parse = "Descendente\n  ";

    private Map<Character, NoTerminal> noTerminales = new LinkedHashMap<>();

    private Token sigToken;

    private int puntero;
    private int linea;

    private Predicate<Character> isFirst = x -> noTerminales.get(x).getFirst().contains(sigToken.getType());
    private Predicate<Character> isFollow = x -> noTerminales.get(x).getFollow().contains(sigToken.getType());

    private BufferedWriter bwParse;
    private String cwd = System.getProperty("user.dir");

    private Tipo entero = new Tipo("entero", 1), bool = new Tipo("boolean", 1), cadena = new Tipo("cadena", 64);
    private Tipo vacio = new Tipo("vacio"), funcion = new Tipo("funcion");
    private Tipo tipoOk = new Tipo("tipoOk"), tipoError = new Tipo("tipoError");

    AnalizadorSintactico(String fichToRead) throws IOException {

        // Se inizializa el Analizador Léxico y gestor de errores
        aLex = new AnalizadorLexico(fichToRead);
        GE = aLex.getGE();

        bwParse = writeFich("parse.txt"); // Se crea el fichero donde se escribira el parse del árbol

        // Se inicializan los conjuntos first y follow de los caracteres no terminales

        // ---------------------------P-----------------------------
        List<TokenType> first = Arrays.asList(TokenType.finFich, TokenType.PRfun, TokenType.id,
                TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile);
        List<TokenType> follow = Arrays.asList(TokenType.finFich);

        NoTerminal noTerminal = new NoTerminal(first, follow);
        noTerminales.put('P', noTerminal);

        // ---------------------------B-----------------------------
        first = Arrays.asList(TokenType.id, TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile);
        follow = Arrays.asList(TokenType.finFich, TokenType.PRfun, TokenType.id,
                TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile, TokenType.llave);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('B', noTerminal);

        // ---------------------------T-----------------------------
        first = Arrays.asList(TokenType.PRint, TokenType.PRboolean, TokenType.PRstring);
        follow = Arrays.asList(TokenType.id);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('T', noTerminal);

        // ---------------------------S-----------------------------
        first = Arrays.asList(TokenType.id, TokenType.PRinput, TokenType.PRoutput, TokenType.PRreturn);
        follow = Arrays.asList(TokenType.finFich, TokenType.PRfun, TokenType.id,
                TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile, TokenType.llave);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('S', noTerminal);

        // ---------------------------S1-----------------------------
        first = Arrays.asList(TokenType.opAsig, TokenType.opAsigDiv, TokenType.paren);
        follow = Arrays.asList(TokenType.finFich, TokenType.PRfun, TokenType.id,
                TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile, TokenType.llave);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('s', noTerminal); // S1

        // ---------------------------L-----------------------------
        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('L', noTerminal);

        // ---------------------------Q-----------------------------
        first = Arrays.asList(TokenType.coma);
        follow = Arrays.asList(TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('Q', noTerminal);

        // ---------------------------X-----------------------------
        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.puntoComa);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('X', noTerminal);

        // ---------------------------F-----------------------------
        first = Arrays.asList(TokenType.PRfun);
        follow = Arrays.asList(TokenType.finFich, TokenType.PRfun, TokenType.id,
                TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile, TokenType.llave);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('F', noTerminal);

        // ---------------------------H-----------------------------
        first = Arrays.asList(TokenType.PRint, TokenType.PRboolean, TokenType.PRstring, TokenType.PRvoid);
        follow = Arrays.asList(TokenType.id);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('H', noTerminal);

        // ---------------------------A-----------------------------
        first = Arrays.asList(TokenType.PRint, TokenType.PRboolean, TokenType.PRstring, TokenType.PRvoid);
        follow = Arrays.asList(TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('A', noTerminal);

        // ---------------------------K-----------------------------
        first = Arrays.asList(TokenType.coma);
        follow = Arrays.asList(TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('K', noTerminal);

        // ---------------------------C-----------------------------
        first = Arrays.asList(TokenType.id, TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile);
        follow = Arrays.asList(TokenType.llave);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('C', noTerminal);

        // ---------------------------E-----------------------------
        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('E', noTerminal);

        // ---------------------------E1-----------------------------
        first = Arrays.asList(TokenType.opOr);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('e', noTerminal); // E1

        // --------------------------------------------------------
        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('R', noTerminal);

        // ---------------------------R1-----------------------------
        first = Arrays.asList(TokenType.opIgual);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('r', noTerminal); // R1

        // ---------------------------U-----------------------------
        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr, TokenType.opIgual);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('U', noTerminal);

        // ---------------------------U1-----------------------------
        first = Arrays.asList(TokenType.opSuma);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr, TokenType.opIgual);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('u', noTerminal); // U1

        // ---------------------------V-----------------------------
        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr, TokenType.opIgual,
                TokenType.opSuma);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('V', noTerminal);

        // ---------------------------V1-----------------------------
        first = Arrays.asList(null, TokenType.paren);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr, TokenType.opIgual,
                TokenType.opSuma);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('v', noTerminal); // V1

    }

    boolean analisis() throws IOException {
        this.sigToken = aLex.getTokenFich();
        p1();

        boolean error = GE.getError();

        // Escribir el resultado del parse en un archivo
        try {
            parse += "\n";
            bwParse.write(parse);
            bwParse.close();
            GE.terminarGE();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return error;
    }

    /**
     * Método para crear un fichero de escritura en el directorio data del
     * proyecto.
     * Se usará para crear el fichero de parse
     * 
     * @param nameFich Nombre del fichero
     * @return Buffer donde se introducirá los caracteres para que se escriban en el
     *         fichero.
     * 
     */
    private BufferedWriter writeFich(String nameFich) throws IOException {
        FileWriter fw = null;
        fw = new FileWriter(cwd + "/data/aSin/" + nameFich);
        // crea FileWriter para crear el archivo de salida
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }

    // Axioma
    private void p1() throws IOException {

        this.ts = aLex.getTs();
        aLex.setZonaDeclarativa(false);

        p();

        ts.destroyTs();
    }

    private void p() throws IOException {
        if (isFirst.test('B')) {
            parse += " 1";
            Tipo tipoR;

            tipoR = b();
            p();

            if (tipoR != null) {
                GE.selgErrorAnalizador("Sm-10", puntero, linea);
            }
        }

        else if (isFirst.test('F')) {
            parse += " 2";
            f();
            p();
        }

        else if (sigToken.getType().equals(TokenType.finFich)) {
            parse += " 3";
            return;

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
        }
    }

    // Expresiones (||, ==, +)
    private Tipo e() {
        if (isFirst.test('R')) {
            parse += " 4";

            Tipo tipo1;
            Tipo tipo2;

            tipo1 = r();
            tipo2 = e1();

            if (tipo2.equals(vacio)) {
                return tipo1;

            } else if (tipo2.equals(bool) && tipo1.equals(bool)) {
                return tipo1;

            } else {
                if (!(tipo1.equals(tipoError) || tipo2.equals(tipoError))) {
                    GE.selgErrorAnalizador("Sm-1", puntero, linea);
                }
                return tipoError;
            }
        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea); // Error sintáctico
            return null;
        }
    }

    private Tipo e1() {
        if (sigToken.getType().equals(TokenType.opOr)) {
            parse += " 5";

            Tipo tipo1;
            Tipo tipo2;

            equiparar(TokenType.opOr, "||");
            tipo1 = r();
            tipo2 = e1();

            if (tipo2.equals(vacio)) {
                if (tipo1.equals(bool)) {
                    return tipo1;

                } else {
                    return tipoError;
                }

            } else if (tipo2.equals(bool) && tipo1.equals(bool)) {
                return tipo1;

            } else {
                if (!(tipo1.equals(tipoError) || tipo2.equals(tipoError))) {
                    GE.selgErrorAnalizador("Sm-1", puntero, linea);
                }
                return tipoError;
            }
        } else if (isFollow.test('e')) {
            parse += " 6";
            return vacio;
        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo r() {
        if (isFirst.test('U')) {
            parse += " 7";

            Tipo tipo1;
            Tipo tipo2;

            tipo1 = u();
            tipo2 = r1();

            if (tipo2.equals(vacio)) {
                return tipo1;

            } else if (tipo2.equals(entero) && tipo1.equals(entero)) {
                return bool;

            } else {
                if (!(tipo1.equals(tipoError) || tipo2.equals(tipoError))) {
                    GE.selgErrorAnalizador("Sm-2", puntero, linea);
                }
                return tipoError;
            }
        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea); // Error sintáctico
            return null;
        }
    }

    private Tipo r1() {
        if (sigToken.getType().equals(TokenType.opIgual)) {
            parse += " 8";

            Tipo tipo1;
            Tipo tipo2;

            equiparar(TokenType.opIgual, "=");
            tipo1 = u();
            tipo2 = r1();

            if (tipo2.equals(vacio)) {
                if (tipo1.equals(entero)) {
                    return tipo1;

                } else {
                    return tipoError;
                }

            } else if (tipo2.equals(bool) && tipo1.equals(entero)) {
                return tipo2;

            } else {
                if (!(tipo1.equals(tipoError) || tipo2.equals(tipoError))) {
                    GE.selgErrorAnalizador("Sm-2", puntero, linea);
                }
                return tipoError;
            }
        } else if (isFollow.test('r')) {
            parse += " 9";
            return vacio;

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo u() {
        if (isFirst.test('V')) {
            parse += " 10";

            Tipo tipo1;
            Tipo tipo2;

            tipo1 = v();
            tipo2 = u1();

            if (tipo2.equals(vacio)) {
                return tipo1;

            } else if (tipo2.equals(entero) && tipo1.equals(entero)) {
                return tipo2;

            } else {
                if (!(tipo1.equals(tipoError) || tipo2.equals(tipoError))) {
                    GE.selgErrorAnalizador("Sm-3", puntero, linea);
                }
                return tipoError;
            }
        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo u1() {
        if (sigToken.getType().equals(TokenType.opSuma)) {
            parse += " 11";

            Tipo tipo1;
            Tipo tipo2;

            equiparar(TokenType.opSuma, "+");
            tipo1 = v();
            tipo2 = u1();

            if (tipo2.equals(vacio)) {
                if (tipo1.equals(entero)) {
                    return tipo1;

                } else {
                    GE.selgErrorAnalizador("Sm-3", puntero, linea);
                    return tipoError;
                }

            } else if (tipo2.equals(entero) && tipo1.equals(entero)) {
                return tipo2;

            } else {
                if (!(tipo1.equals(tipoError) || tipo2.equals(tipoError))) {
                    GE.selgErrorAnalizador("Sm-3", puntero, linea);
                }
                return tipoError;
            }
        } else if (isFollow.test('u')) {
            parse += " 12";
            return vacio;
        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo v() {
        if (sigToken.getType().equals(TokenType.paren) && (int) sigToken.getAtribute() == 1) {
            parse += " 13";

            Tipo tipo;

            equiparar(TokenType.paren, 1, "(");
            tipo = e();
            equiparar(TokenType.paren, 2, ")");

            return tipo;

        } else if (sigToken.getType().equals(TokenType.entero)) {
            parse += " 14";

            equiparar(TokenType.entero, "valor");

            return entero;

        } else if (sigToken.getType().equals(TokenType.cadena)) {
            parse += " 15";

            equiparar(TokenType.cadena,"cadena");

            return cadena;

        } else if (sigToken.getType().equals(TokenType.id)) {
            parse += " 16";

            Tipo tipo;
            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id, "identificador");
            tipo = v1();

            if (tipo.equals(vacio)) {
                return ts.buscarTipoTS(pos);

            } else if (ts.buscarTipoParamTS(pos).equals(tipo)) {
                return ts.buscarTipoRet(pos);

            } else {
                GE.selgErrorAnalizador("Sm-4", puntero, linea);
                return new Tipo("tipoError");
            }

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo v1() {
        if (sigToken.getType().equals(TokenType.paren) && (int) sigToken.getAtribute() == 1) {
            parse += " 17";

            Tipo tipo;

            equiparar(TokenType.paren, 1, "(");
            tipo = l();
            equiparar(TokenType.paren, 2, ")");

            return tipo;

        } else if (isFollow.test('v')) {
            parse += " 18";
            return vacio;

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    // Sentencias simples
    private Tipo[] s() { // Tipo[0] = tipo Tipo[1] = tipoRet
        if (sigToken.getType().equals(TokenType.PRinput)) {
            parse += " 19";

            Tipo tipoS;

            equiparar(TokenType.PRinput, "input");

            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id, "identificador");

            if (ts.buscarTipoTS(pos).equals(entero) || ts.buscarTipoTS(pos).equals(entero)) {
                tipoS = tipoOk;
                
            } else {
                GE.selgErrorAnalizador("Sm-5", puntero, linea);
                tipoS = tipoError;
            }
            
            equiparar(TokenType.puntoComa, ";");

            return new Tipo[] { tipoS, null };

        } else if (sigToken.getType().equals(TokenType.PRoutput)) {
            parse += " 20";

            Tipo tipo, tipoS;

            equiparar(TokenType.PRoutput, "output");
            tipo = e();

            if (tipo.equals(entero) || tipo.equals(cadena)) {
                tipoS = tipoOk;

            } else {
                GE.selgErrorAnalizador("Sm-5", puntero, linea);
                tipoS = tipoError;
            }

            equiparar(TokenType.puntoComa, ";");

            return new Tipo[] { tipoS, null };

        } else if (sigToken.getType().equals(TokenType.PRreturn)) {
            parse += " 21";

            Tipo tipo;

            equiparar(TokenType.PRreturn, "return");
            tipo = x();
            equiparar(TokenType.puntoComa, ";");

            if (!tipo.equals(tipoError)) {
                return new Tipo[] { tipoOk, tipo };

            } else {
                return new Tipo[] { tipoError, tipo };
            }

        } else if (sigToken.getType().equals(TokenType.id)) {
            parse += " 22";

            Tipo tipo;
            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id, "identificador");
            tipo = s1();

            if (ts.buscarTipoTS(pos).equals(funcion)) { // id.tipo == funcion
                if (ts.buscarTipoParamTS(pos).equals(tipo)) { // id.tipoParam == s1.tipo
                    return new Tipo[] { tipoOk, null };

                } else {
                    GE.selgErrorAnalizador("Sm-4", puntero, linea);
                    return new Tipo[] { tipoError, vacio };
                }
            } else if (ts.buscarTipoTS(pos).equals(tipo)) {
                return new Tipo[] { tipoOk, null };

            } else {
                GE.selgErrorAnalizador("Sm-11", puntero, linea);
                return new Tipo[] { tipoError, null };
            }

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo s1() {
        if (sigToken.getType().equals(TokenType.opAsig)) {
            parse += " 23";

            Tipo tipo;

            equiparar(TokenType.opAsig, "=");
            tipo = e();
            equiparar(TokenType.puntoComa, ";");

            return tipo;

        } else if (sigToken.getType().equals(TokenType.paren) && (int) sigToken.getAtribute() == 1) {
            parse += " 24";

            Tipo tipo;

            equiparar(TokenType.paren, 1, "(");
            tipo = l();
            equiparar(TokenType.paren, 2, ")");
            equiparar(TokenType.puntoComa, ";");

            return tipo;

        } else if (sigToken.getType().equals(TokenType.opAsigDiv)) {
            parse += " 25";

            Tipo tipo;

            equiparar(TokenType.opAsigDiv, "/=");
            tipo = e();
            equiparar(TokenType.puntoComa, ";");

            return tipo;

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo l() {
        if (isFirst.test('E') && !(sigToken.getType().equals(TokenType.paren) && (int) sigToken.getAtribute() == 2)) {
            parse += " 26";

            Tipo tipo1;
            Tipo tipo2;

            tipo1 = e();
            tipo2 = q();

            if (tipo2.equals(vacio)) {
                return tipo1; // L.tipo = E.tipo

            } else {
                Tipo producto = new Tipo("producto", tipo1); // L.tipo = E.tipo x Q.tipo
                producto.añadirProducto(tipo2); // Independiente de que Q.tipo sea otro producto o no, E.tipo se
                return producto; // convierte en producto y se combinan ambos tipos.
            }

        } else if (isFollow.test('L')) {
            parse += " 27";
            return vacio;
        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo q() {
        if (sigToken.getType().equals(TokenType.coma)) {
            parse += " 28";

            Tipo tipo1;
            Tipo tipo2;

            equiparar(TokenType.coma, ",");
            tipo1 = e();
            tipo2 = q();

            if (tipo2.equals(vacio)) {
                return tipo1;

            } else {
                Tipo producto = new Tipo("producto", tipo1);
                producto.añadirProducto(tipo2);
                return producto;
            }

        } else if (isFollow.test('Q')) {
            parse += " 29";
            return vacio;

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo x() {
        if (isFirst.test('E')) {
            parse += " 30";

            Tipo tipo;

            tipo = e();

            return tipo;

        } else if (isFollow.test('X')) {
            parse += " 31";
            return vacio;

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    // Sentencias compuestas y declaración de variables
    private Tipo b() { // ret = B.tipoRet
        if (sigToken.getType().equals(TokenType.PRif)) {
            parse += " 32";

            Tipo tipo;
            Tipo[] tipo2;

            equiparar(TokenType.PRif, "if");
            equiparar(TokenType.paren, 1, "(");
            tipo = e();

            if (!tipo.equals(bool)) {
                GE.selgErrorAnalizador("Sm-6", puntero, linea);
            }

            equiparar(TokenType.paren, 2, ")");
            tipo2 = s();

            return tipo2[1];

        } else if (sigToken.getType().equals(TokenType.PRvar)) {
            parse += " 33";

            Tipo tipo;

            equiparar(TokenType.PRvar, "var");

            tipo = t();
            aLex.setZonaDeclarativa(false);

            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id, "identificador");

            ts.insertarTipoTS(pos, tipo);
            ts.insertarDespl(pos);
            ts.setDeslp(tipo.getAncho());

            equiparar(TokenType.puntoComa, ";");

            return null;
        }

        else if (sigToken.getType().equals(TokenType.PRwhile)) {
            parse += " 34";

            Tipo tipo1;
            Tipo tipo2;

            equiparar(TokenType.PRwhile, "while");
            equiparar(TokenType.paren, 1, "(");
            tipo1 = e();

            if (!tipo1.equals(bool)) {
                GE.selgErrorAnalizador("Sm-7", puntero, linea);
            }

            equiparar(TokenType.paren, 2, ")");
            equiparar(TokenType.llave, 1, "{");
            tipo2 = c();
            equiparar(TokenType.llave, 2, "}");

            return tipo2;

        } else if (isFirst.test('S')) {
            parse += " 35";
            Tipo[] tipo;

            tipo = s();

            return tipo[1];

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo t() {
        aLex.setZonaDeclarativa(true);

        if (sigToken.getType().equals(TokenType.PRint)) {
            parse += " 36";

            equiparar(TokenType.PRint, "int");

            return entero;

        } else if (sigToken.getType().equals(TokenType.PRboolean)) {
            parse += " 37";

            equiparar(TokenType.PRboolean, "boolean");

            return bool;

        } else if (sigToken.getType().equals(TokenType.PRstring)) {
            parse += " 38";

            equiparar(TokenType.PRstring, "string");

            return cadena;

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    // Declaracion de funciones
    private void f() throws IOException {
        if (sigToken.getType().equals(TokenType.PRfun)) {
            parse += " 39";

            Tipo tipo1;
            Tipo tipo2;
            Tipo tipo3;

            equiparar(TokenType.PRfun, "funcion");
            tipo1 = h();
            aLex.setZonaDeclarativa(false);

            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id, "identificador");

            ts = ts.creatTSChild();
            aLex.setTs(ts);

            equiparar(TokenType.paren, 1, "(");
            tipo2 = a();

            aLex.setZonaDeclarativa(false);

            ts.insertarTipoTS(pos, funcion);
            ts.insertarTipoParamTS(pos, tipo2);
            ts.insertarTipoRetTS(pos, tipo1);
            ts.insertarEtiqTS(pos, ts.nuevaEtiq(pos));

            equiparar(TokenType.paren, 2, ")");

            equiparar(TokenType.llave, 1, "{");
            tipo3 = c();

            if (tipo3 != null && !tipo1.equals(tipo3)) {
                if (tipo1.equals(vacio)) {
                    GE.selgErrorAnalizador("Sm-8", puntero, linea);

                } else {
                    GE.selgErrorAnalizador("Sm-9", puntero, linea);
                }
            }

            equiparar(TokenType.llave, 2, "}");

            ts = ts.destroyTs();

            aLex.setTs(ts);
        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
        }
    }

    private Tipo h() {
        if (isFirst.test('T')) {
            parse += " 40";

            Tipo tipo;

            tipo = t();

            return tipo;

        } else if (sigToken.getType().equals(TokenType.PRvoid)) {
            parse += " 41";
            aLex.setZonaDeclarativa(true);

            equiparar(TokenType.PRvoid, "void");

            return vacio;

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo a() {
        if (isFirst.test('T')) {
            parse += " 42";

            Tipo tipoT, tipoK;

            tipoT = t();

            aLex.setZonaDeclarativa(false);
            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id, "identificador");

            ts.insertarTipoTS(pos, tipoT);
            ts.insertarDespl(pos);
            ts.setDeslp(tipoT.getAncho());

            tipoK = k();

            if (tipoK.equals(vacio)) {
                return tipoT;

            } else {
                Tipo producto = new Tipo("producto", tipoT);
                producto.añadirProducto(tipoK);
                return producto;
            }

        } else if (sigToken.getType().equals(TokenType.PRvoid)) {
            parse += " 43";

            equiparar(TokenType.PRvoid, "void");

            return vacio;

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo k() {
        if (sigToken.getType().equals(TokenType.coma)) {
            parse += " 44";

            Tipo tipoT, tipoK;

            equiparar(TokenType.coma,",");
            tipoT = t();

            aLex.setZonaDeclarativa(false);
            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id, "identificador");

            ts.insertarTipoTS(pos, tipoT);
            ts.insertarDespl(pos);
            ts.setDeslp(tipoT.getAncho());

            tipoK = k();

            if (tipoK.equals(vacio)) {
                return tipoT;

            } else {
                Tipo producto = new Tipo("producto", tipoT);
                producto.añadirProducto(tipoK);
                return producto;
            }

        } else if (isFollow.test('K')) {
            parse += " 45";
            return vacio;
        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private Tipo c() {
        if (isFirst.test('B')) {
            parse += " 46";

            Tipo tipoB, tipoC;

            tipoB = b();
            tipoC = c();

            if (tipoB == null) {
                return tipoC;

            } else if (tipoC == null) {
                return tipoB;

            } else if (tipoB.equals(tipoC)) {
                return tipoB;

            } else {
                GE.selgErrorAnalizador("Sm-12", puntero, linea);
                return tipoError;
            }

        } else if (isFollow.test('C')) {
            parse += " 47";

            return null;

        } else {
            GE.selgErrorAnalizador("Sx-0", puntero, linea);
            return null;
        }
    }

    private void equiparar(TokenType tipo, String esperado) {
        if (tipo.equals(sigToken.getType())) {
            try {
                this.puntero = aLex.getPuntero();
                this.linea = aLex.getLinea();
                this.sigToken = aLex.getTokenFich();
            } catch (IOException e) {
                throw new RuntimeException("Error al leer token del fichero");
            }
        } else
            GE.selgErrorAnalizador("Sx-" + esperado, puntero, linea);
    }

    private void equiparar(TokenType tipo, int atributo, String esperado) {
        if (tipo.equals(sigToken.getType()) && (int) sigToken.getAtribute() == atributo) {
            try {
                this.puntero = aLex.getPuntero();
                this.linea = aLex.getLinea();
                this.sigToken = aLex.getTokenFich();
            } catch (Exception e) {
                throw new RuntimeException("Error al leer token del fichero");
            }
        } else
            GE.selgErrorAnalizador("Sx-" + esperado ,puntero, linea);
    }

}
