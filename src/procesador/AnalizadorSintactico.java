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

    private Predicate<Character> isFirst = x -> noTerminales.get(x).getFirst().contains(sigToken.getType());
    private Predicate<Character> isFollow = x -> noTerminales.get(x).getFollow().contains(sigToken.getType());

    private BufferedWriter bwParse;
    private String cwd = System.getProperty("user.dir");

    private Tipo entero = new Tipo("entero", 1), bool = new Tipo("boolean", 1), cadena = new Tipo("cadena", 64);
    private Tipo vacio = new Tipo("vacio"), funcion = new Tipo("funcion");
    private Tipo tipoOk = new Tipo("tipoOk"), tipoError = new Tipo("tipoError");

    AnalizadorSintactico(String fichToRead) {

        aLex = new AnalizadorLexico(fichToRead);
        GE = aLex.getGE();
        bwParse = writeFich("parse.txt");

        List<TokenType> first = Arrays.asList(TokenType.finFich, TokenType.PRfun, TokenType.id,
                TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile);
        List<TokenType> follow = Arrays.asList(TokenType.finFich);

        NoTerminal noTerminal = new NoTerminal(first, follow);
        noTerminales.put('P', noTerminal);

        first = Arrays.asList(TokenType.id, TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile);
        follow = Arrays.asList(TokenType.finFich, TokenType.PRfun, TokenType.id,
                TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile, TokenType.llave);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('B', noTerminal);

        first = Arrays.asList(TokenType.PRint, TokenType.PRboolean, TokenType.PRstring);
        follow = Arrays.asList(TokenType.id);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('T', noTerminal);

        first = Arrays.asList(TokenType.id, TokenType.PRinput, TokenType.PRoutput, TokenType.PRreturn);
        follow = Arrays.asList(TokenType.finFich, TokenType.PRfun, TokenType.id,
                TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile, TokenType.llave);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('S', noTerminal);

        first = Arrays.asList(TokenType.opAsig, TokenType.opAsigDiv, TokenType.paren);
        follow = Arrays.asList(TokenType.finFich, TokenType.PRfun, TokenType.id,
                TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile, TokenType.llave);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('s', noTerminal); // S1

        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('L', noTerminal);

        first = Arrays.asList(TokenType.coma);
        follow = Arrays.asList(TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('Q', noTerminal);

        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.puntoComa);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('X', noTerminal);

        first = Arrays.asList(TokenType.PRfun);
        follow = Arrays.asList(TokenType.finFich, TokenType.PRfun, TokenType.id,
                TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile, TokenType.llave);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('F', noTerminal);

        first = Arrays.asList(TokenType.PRint, TokenType.PRboolean, TokenType.PRstring, TokenType.PRvoid);
        follow = Arrays.asList(TokenType.id);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('H', noTerminal);

        first = Arrays.asList(TokenType.PRint, TokenType.PRboolean, TokenType.PRstring, TokenType.PRvoid);
        follow = Arrays.asList(TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('A', noTerminal);

        first = Arrays.asList(TokenType.coma);
        follow = Arrays.asList(TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('K', noTerminal);

        first = Arrays.asList(TokenType.id, TokenType.PRif, TokenType.PRinput, TokenType.PRoutput,
                TokenType.PRreturn, TokenType.PRvar, TokenType.PRwhile);
        follow = Arrays.asList(TokenType.llave);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('C', noTerminal);

        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('E', noTerminal);

        first = Arrays.asList(TokenType.opOr);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('e', noTerminal); // E1

        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('R', noTerminal);

        first = Arrays.asList(TokenType.opIgual);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('r', noTerminal); // R1

        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr, TokenType.opIgual);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('U', noTerminal);

        first = Arrays.asList(TokenType.opSuma);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr, TokenType.opIgual);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('u', noTerminal); // U1

        first = Arrays.asList(TokenType.paren, TokenType.entero, TokenType.cadena, TokenType.id);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr, TokenType.opIgual,
                TokenType.opSuma);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('V', noTerminal);

        first = Arrays.asList(null, TokenType.paren);
        follow = Arrays.asList(TokenType.puntoComa, TokenType.coma, TokenType.paren, TokenType.opOr, TokenType.opIgual,
                TokenType.opSuma);

        noTerminal = new NoTerminal(first, follow);
        noTerminales.put('v', noTerminal); // V1

    }

    String ASin() {
        this.sigToken = aLex.getTokens();
        p1();

        // Escribir el resultado del parse en un archivo
        try {
            parse += "\n";
            bwParse.write(parse);
            bwParse.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parse;
    }

    private BufferedWriter writeFich(String nameFich) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(cwd + "/data/aSin/" + nameFich);
        } catch (IOException e) {
            e.printStackTrace();
        } // crea FileWriter para crear el archivo de salida
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }

    // Axioma
    private void p1() {

        this.ts = aLex.getTs();
        ts.setDeslp(0);
        aLex.setZonaDeclarativa(false);

        p();

        ts.destroyTs();
    }

    private void p() {
        if (isFirst.test('B')) {
            parse += " 1";
            b();
            p();
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
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
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
                    GE.selgErrorAnalizador("Sm-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
                }
                return tipoError;
            }
        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken); // Error sintáctico
            return null;
        }
    }

    private Tipo e1() {
        if (sigToken.getType().equals(TokenType.opOr)) {
            parse += " 5";

            Tipo tipo1;
            Tipo tipo2;

            equiparar(TokenType.opOr);
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
                    GE.selgErrorAnalizador("Sm-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
                }
                return tipoError;
            }
        } else if (isFollow.test('e')) {
            parse += " 6";
            return vacio;
        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
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
                    GE.selgErrorAnalizador("Sm-2", aLex.getPuntero(), aLex.getLinea(), sigToken);
                }
                return tipoError;
            }
        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken); // Error sintáctico
            return null;
        }
    }

    private Tipo r1() {
        if (sigToken.getType().equals(TokenType.opIgual)) {
            parse += " 8";

            Tipo tipo1;
            Tipo tipo2;

            equiparar(TokenType.opIgual);
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
                    GE.selgErrorAnalizador("Sm-2", aLex.getPuntero(), aLex.getLinea(), sigToken);
                }
                return tipoError;
            }
        } else if (isFollow.test('r')) {
            parse += " 9";
            return vacio;

        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
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
                    GE.selgErrorAnalizador("Sm-3", aLex.getPuntero(), aLex.getLinea(), sigToken);
                }
                return tipoError;
            }
        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    private Tipo u1() {
        if (sigToken.getType().equals(TokenType.opSuma)) {
            parse += " 11";

            Tipo tipo1;
            Tipo tipo2;

            equiparar(TokenType.opSuma);
            tipo1 = v();
            tipo2 = u1();

            if (tipo2.equals(vacio)) {
                if (tipo1.equals(entero)) {
                    return tipo1;

                } else {
                    return tipoError;
                }

            } else if (tipo2.equals(entero) && tipo1.equals(entero)) {
                return tipo2;

            } else {
                if (!(tipo1.equals(tipoError) || tipo2.equals(tipoError))) {
                    GE.selgErrorAnalizador("Sm-3", aLex.getPuntero(), aLex.getLinea(), sigToken);
                }
                return tipoError;
            }
        } else if (isFollow.test('u')) {
            parse += " 12";
            return vacio;
        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    private Tipo v() {
        if (sigToken.getType().equals(TokenType.paren) && (int) sigToken.getAtribute() == 1) {
            parse += " 13";

            Tipo tipo;

            equiparar(TokenType.paren, 1);
            tipo = e();
            equiparar(TokenType.paren, 2);

            return tipo;

        } else if (sigToken.getType().equals(TokenType.entero)) {
            parse += " 14";

            equiparar(TokenType.entero);

            return entero;

        } else if (sigToken.getType().equals(TokenType.cadena)) {
            parse += " 15";

            equiparar(TokenType.cadena);

            return cadena;

        } else if (sigToken.getType().equals(TokenType.id)) {
            parse += " 16";

            Tipo tipo;
            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id);
            tipo = v1();

            if (tipo.equals(vacio)) {
                return ts.buscarTipoTS(pos);

            } else if (ts.buscarTipoParamTS(pos).equals(tipo)) {
                return ts.buscarTipoRet(pos);
               
            } else {
                GE.selgErrorAnalizador("Sm-4", aLex.getPuntero(), aLex.getLinea(), sigToken);
                return new Tipo("tipoError");
            }

        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    private Tipo v1() {
        if (sigToken.getType().equals(TokenType.paren) && (int) sigToken.getAtribute() == 1) {
            parse += " 17";

            Tipo tipo;

            equiparar(TokenType.paren, 1);
            tipo = l();
            equiparar(TokenType.paren, 2);

            return tipo;

        } else if (isFollow.test('v')) {
            parse += " 18";
            return vacio;

        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    // Sentencias simples
    private Tipo[] s() { // Tipo[0] = tipo Tipo[1] = tipoRet
        if (sigToken.getType().equals(TokenType.PRinput)) {
            parse += " 19";

            equiparar(TokenType.PRinput);

            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id);
            equiparar(TokenType.puntoComa);

            if (ts.buscarTipoTS(pos).equals(entero) || ts.buscarTipoTS(pos).equals(entero)) {
                return new Tipo[] { tipoOk, vacio };
            } else {
                GE.selgErrorAnalizador("Sm-5", aLex.getPuntero(), aLex.getLinea(), sigToken);
                return new Tipo[] { tipoError, vacio };
            }

        } else if (sigToken.getType().equals(TokenType.PRoutput)) {
            parse += " 20";

            Tipo tipo;

            equiparar(TokenType.PRoutput);
            tipo = e();
            equiparar(TokenType.puntoComa);

            if (tipo.equals(entero) || tipo.equals(cadena)) {
                return new Tipo[] { tipoOk, vacio };

            } else {
                GE.selgErrorAnalizador("Sx-5", aLex.getPuntero(), aLex.getLinea(), sigToken);
                return new Tipo[] { tipoError, vacio };
            }

        } else if (sigToken.getType().equals(TokenType.PRreturn)) {
            parse += " 21";

            Tipo tipo;

            equiparar(TokenType.PRreturn);
            tipo = x();
            equiparar(TokenType.puntoComa);

            if (!tipo.equals(tipoError)) {
                return new Tipo[] { tipoOk, vacio };

            } else {
                return new Tipo[] { tipoError, vacio };
            }

        } else if (sigToken.getType().equals(TokenType.id)) {
            parse += " 22";

            Tipo tipo;
            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id);
            tipo = s1();

            if (ts.buscarTipoTS(pos).equals(funcion)) { // id.tipo == funcion
                if (ts.buscarTipoParamTS(pos).equals(tipo)) { // id.tipoParam == s1.tipo
                    return new Tipo[] { tipoOk, vacio };

                } else {
                    GE.selgErrorAnalizador("Sm-4", aLex.getPuntero(), aLex.getLinea(), sigToken);
                    return new Tipo[] { tipoError, vacio };
                }
            } else if (ts.buscarTipoTS(pos).equals(tipo)) {
                return new Tipo[] { tipoOk, vacio };

            } else {
                GE.selgErrorAnalizador("Sm-5", aLex.getPuntero(), aLex.getLinea(), sigToken);
                return new Tipo[] { tipoError, vacio };
            }

        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    private Tipo s1() {
        if (sigToken.getType().equals(TokenType.opAsig)) {
            parse += " 23";

            Tipo tipo;

            equiparar(TokenType.opAsig);
            tipo = e();
            equiparar(TokenType.puntoComa);

            return tipo;

        } else if (sigToken.getType().equals(TokenType.paren) && (int) sigToken.getAtribute() == 1) {
            parse += " 24";

            Tipo tipo;

            equiparar(TokenType.paren, 1);
            tipo = l();
            equiparar(TokenType.paren);
            equiparar(TokenType.puntoComa);

            return tipo;

        } else if (sigToken.getType().equals(TokenType.opAsigDiv)) {
            parse += " 25";

            Tipo tipo;

            equiparar(TokenType.opAsigDiv);
            tipo = e();
            equiparar(TokenType.puntoComa);

            return tipo;

        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    private Tipo l() {
        if (isFirst.test('E')) {
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
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    private Tipo q() {
        if (sigToken.getType().equals(TokenType.coma)) {
            parse += " 28";

            Tipo tipo1;
            Tipo tipo2;

            equiparar(TokenType.coma);
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
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
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
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    // Sentencias compuestas y declaración de variables
    private Tipo b() { // ret = B.tipoRet
        if (sigToken.getType().equals(TokenType.PRif)) {
            parse += " 32";

            Tipo tipo;
            Tipo[] tipo2;

            equiparar(TokenType.PRif);
            equiparar(TokenType.paren, 1);
            tipo = e();
            equiparar(TokenType.paren, 2);
            tipo2 = s();

            if (!tipo.equals(bool)) {
                GE.selgErrorAnalizador("Sm-6", aLex.getPuntero(), aLex.getLinea(), sigToken);
            }
            return tipo2[1];

        } else if (sigToken.getType().equals(TokenType.PRvar)) {
            parse += " 33";

            Tipo tipo;

            equiparar(TokenType.PRvar);
            aLex.setZonaDeclarativa(true);
            tipo = t();

            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id);

            aLex.setZonaDeclarativa(false);
            ts.insertarTipoTS(pos, tipo);
            ts.insertarDespl(pos);
            ts.setDeslp(tipo.getAncho());
            aLex.setZonaDeclarativa(false);

            equiparar(TokenType.puntoComa);

            return vacio;
        }

        else if (sigToken.getType().equals(TokenType.PRwhile)) {
            parse += " 34";

            Tipo tipo1;
            Tipo tipo2;

            equiparar(TokenType.PRwhile);
            equiparar(TokenType.paren, 1);
            tipo1 = e();
            equiparar(TokenType.paren, 2);
            equiparar(TokenType.llave, 1);
            tipo2 = c();
            equiparar(TokenType.llave, 2);

            if (!tipo1.equals(bool)) {
                GE.selgErrorAnalizador("Sm-7", aLex.getPuntero(), aLex.getLinea(), sigToken);
            }
            return tipo2;

        } else if (isFirst.test('S')) {
            parse += " 35";
            Tipo[] tipo;

            tipo = s();

            return tipo[1];

        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    private Tipo t() {
        if (sigToken.getType().equals(TokenType.PRint)) {
            parse += " 36";

            equiparar(TokenType.PRint);

            return entero;

        } else if (sigToken.getType().equals(TokenType.PRboolean)) {
            parse += " 37";

            equiparar(TokenType.PRboolean);

            return bool;

        } else if (sigToken.getType().equals(TokenType.PRstring)) {
            parse += " 38";

            equiparar(TokenType.PRstring);

            return cadena;

        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    // Declaracion de funciones
    private void f() {
        if (sigToken.getType().equals(TokenType.PRfun)) {
            parse += " 39";

            Tipo tipo1;
            Tipo tipo2;
            Tipo tipo3;

            aLex.setZonaDeclarativa(true);
            equiparar(TokenType.PRfun);
            tipo1 = h();

            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id);

            ts = ts.creatTSChild();
            aLex.setTs(ts);
            ts.setDeslp(0);
            aLex.setZonaDeclarativa(false);

            equiparar(TokenType.paren, 1);
            tipo2 = a();

            aLex.setZonaDeclarativa(false);
            ts.setVarGlobal(true);
            ts.insertarTipoTS(pos, funcion);
            ts.insertarTipoParamTS(pos, tipo2);
            ts.insertarTipoRetTS(pos, tipo1);
            ts.insertarEtiqTS(pos, ts.nuevaEtiq(pos));

            equiparar(TokenType.paren, 2);

            ts.setVarGlobal(false);

            equiparar(TokenType.llave, 1);
            tipo3 = c();
            equiparar(TokenType.llave, 2);

            if (!tipo1.equals(tipo3)) {
                if (tipo1.equals(vacio)) {
                    GE.selgErrorAnalizador("Sm-8", aLex.getPuntero(), aLex.getLinea(), sigToken);

                } else {
                    GE.selgErrorAnalizador("Sm-9", aLex.getPuntero(), aLex.getLinea(), sigToken);
                }
            }
            ts = ts.destroyTs();
        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
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

            equiparar(TokenType.PRvoid);

            return vacio;

        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    private Tipo a() {
        if (isFirst.test('T')) {
            parse += " 42";

            Tipo tipoT, tipoK;

            tipoT = t();

            aLex.setZonaDeclarativa(true);
            int pos = (int) sigToken.getAtribute();

            equiparar(TokenType.id);

            ts.insertarTipoTS(pos, tipoT);

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

            equiparar(TokenType.PRvoid);

            return vacio;

        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    private Tipo k() {
        if (sigToken.getType().equals(TokenType.coma)) {
            parse += " 44";

            Tipo tipoT, tipoK;

            equiparar(TokenType.coma);
            tipoT = t();
            equiparar(TokenType.id);
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
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    private Tipo c() {
        if (isFirst.test('B')) {
            parse += " 46";

            Tipo tipoB, tipoC;

            tipoB = b();
            tipoC = c();

            if (tipoB.equals(tipoC)) {
                return tipoB;

            } else if (tipoB.equals(vacio)) {
                return tipoC;

            } else if (tipoC.equals(vacio)) {
                return tipoB;

            } else {
                return tipoError;
            }

        } else if (isFollow.test('C')) {
            parse += " 47";

            return vacio;

        } else {
            GE.selgErrorAnalizador("Sx-1", aLex.getPuntero(), aLex.getLinea(), sigToken);
            return null;
        }
    }

    private void equiparar(TokenType tipo) {
        if (tipo.equals(sigToken.getType())) {
            this.sigToken = aLex.getTokens();
        } else
            GE.selgErrorAnalizador("Sx-" + tipo.toString(),aLex.getPuntero(),aLex.getLinea());
    }

    private void equiparar(TokenType tipo, int atributo) {
        if (tipo.equals(sigToken.getType()) && (int) sigToken.getAtribute() == atributo) {
            this.sigToken = aLex.getTokens();
        } else
        GE.selgErrorAnalizador("Sx-" + tipo.toString() + "_" + atributo,aLex.getPuntero(),aLex.getLinea());
    }

    

}
