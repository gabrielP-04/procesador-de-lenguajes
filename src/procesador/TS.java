package procesador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Symbol {

    private String lexema;
    private Tipo tipo = new Tipo("entero");
    private int deslp;
    private Tipo tipoParamFun;
    private Tipo tipoRetorno;
    private String etiqFuncion;
    private boolean global;

    private int pos = 0;

    Symbol(String lexema, int pos, boolean global) {
        this.lexema = lexema;
        this.pos = pos;
        this.global = global;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public void setDeslp(int deslp) {
        this.deslp = deslp;
    }

    public void setTipoParamFun(Tipo tipoParam) {
        this.tipoParamFun = tipoParam;
    }

    public void setTipoRetorno(Tipo tipoRetorno) {
        this.tipoRetorno = tipoRetorno;
    }

    public void setEtiqFuncion(String etiqFuncion) {
        this.etiqFuncion = etiqFuncion;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public String getLexema() {
        return lexema;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public int getDeslp() {
        return deslp;
    }

    public Tipo getTipoParam() {
        return tipoParamFun;
    }

    public Tipo getTipoRetorno() {
        return tipoRetorno;
    }

    public String getEtiqFuncion() {
        return etiqFuncion;
    }

    public int getPos() {
        return pos;
    }

    public boolean getGlobal() {
        return global;
    }
}

public class TS {

    private Map<String, Symbol> ts = new LinkedHashMap<>();
    private Map<Integer, String> id_lex;

    private TS tsGlobal;
    private int pos;
    private int despl;
    private int numberTS;
    private boolean varGlobal;

    private BufferedWriter bw;
    private String cwd = System.getProperty("user.dir");
    private RandomAccessFile bwR;

    TS() {
        this.tsGlobal = this;
        this.id_lex = new HashMap<>();
        this.numberTS = 0;
        this.pos = 0;
        this.varGlobal = true;
        bwR = writeFichPadre("ts.txt");
    }

    TS(TS tsGlobal, TS tsPadre, int numberTS, int pos, Map<Integer, String> id_lex) {
        this.tsGlobal = tsGlobal;
        this.numberTS = numberTS;
        this.id_lex = new HashMap<>(id_lex);
        this.pos = pos;
        this.varGlobal = false;
        bw = writeFich("ts.txt");
    }

    public void setVarGlobal(boolean varGlobal) {
        this.varGlobal = varGlobal;
    }

    public void setDeslp(int despl) {
        if (varGlobal && !isTSGlobal()) {
            tsGlobal.setDeslp(despl);
        } else {
            this.despl += despl;
        }
    }

    public int getDeslp() {
        return despl;
    }

    private RandomAccessFile writeFichPadre(String nameFich) {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(cwd + "/data/aLex/" + nameFich, "rw");
            FileWriter fw = new FileWriter(cwd + "/data/aLex/" + nameFich, false);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } // crea FileWriter para crear el archivo de salida
        return file;
    }

    private BufferedWriter writeFich(String nameFich) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(cwd + "/data/aLex/" + nameFich, false);
        } catch (IOException e) {
            e.printStackTrace();
        } // crea FileWriter para crear el archivo de salida
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }

    public int addSymbol(String lex) {
        int p = pos;
        if (isTSGlobal()) {
            ts.put(lex, new Symbol(lex, pos, true));
            id_lex.put(pos, lex);
        } else {
            ts.put(lex, new Symbol(lex, pos, false));
            id_lex.put(pos, lex);
        }
        pos++;

        return p;
    }

    public int addSymbolGlobal(String lex) {
        int pos = tsGlobal.addSymbol(lex);
        tsGlobal.insertarDespl(pos);
        tsGlobal.setDeslp(1);
        return pos;
    }

    public TS creatTSChild() {
        TS tsChild = new TS(tsGlobal, this, numberTS + 1, pos, id_lex);
        return tsChild;
    }

    public boolean isTSGlobal() {
        return tsGlobal.equals(this);
    }

    public int findSymbolCurrent(String lex) {
        int pos = -1;
        if (ts.containsKey(lex)) {
            pos = ts.get(lex).getPos();
        }
        return pos;
    }

    public int findSymbol(String lex) {
        int pos = -1;
        if (ts.containsKey(lex)) {
            pos = ts.get(lex).getPos();
        } else if (!isTSGlobal())
            pos = tsGlobal.findSymbol(lex);
        return pos;
    }

    private Symbol getSymbol(int pos) {
        String lex = id_lex.get(pos);
        Symbol symbol = null;

        if (ts.containsKey(lex))
            symbol = ts.get(lex);
        else if (!isTSGlobal())
            symbol = tsGlobal.getSymbol(pos);

        return symbol;
    }

    public void insertarTipoTS(int pos, Tipo t) {
        Symbol var = getSymbol(pos);
        var.setTipo(t);
    }

    public void insertarTipoParamTS(int pos, Tipo t) {
        Symbol var = getSymbol(pos);
        var.setTipoParamFun(t);
    }

    public void insertarTipoRetTS(int pos, Tipo t) {
        Symbol var = getSymbol(pos);
        var.setTipoRetorno(t);
    }

    public void insertarEtiqTS(int pos, String etiq) {
        Symbol var = getSymbol(pos);
        var.setEtiqFuncion(etiq);
    }

    public void insertarDespl(int pos) {
        Symbol var = getSymbol(pos);
        if (varGlobal) {
            var.setDeslp(tsGlobal.getDeslp());
        } else {
            var.setDeslp(despl);
        }
    }

    public Tipo buscarTipoTS(int pos) {
        Symbol var = getSymbol(pos);
        return var.getTipo();
    }

    public Tipo buscarTipoParamTS(int pos) {
        Symbol var = getSymbol(pos);
        return var.getTipoParam();
    }

    public Tipo buscarTipoRet(int pos) {
        Symbol var = getSymbol(pos);
        return var.getTipoRetorno();
    }

    public String nuevaEtiq(int pos) {
        Symbol var = getSymbol(pos);
        return "Et" + numberTS + "_" + var.getLexema();
    }

    public TS destroyTs() throws IOException {
        writeTS();
        if (isTSGlobal())
            bwR.close();
        else
            bw.close();
        return tsGlobal;
    }

    private void writeTS() throws IOException {
        StringBuilder token = new StringBuilder();

        //Se contruye el la tabla de simbolos
        token.append("CONTENIDO DE LA TABLA #" + numberTS + " :\n\n");
        for (Symbol s : ts.values()) {
            Tipo t = s.getTipo();
            token.append(" * LEXEMA: '" + s.getLexema() + "'\n");
            token.append("   ATRIBUTOS :\n");
            token.append("\t+ tipo: '" + t.getTipo() + "'\n");
            if (t.equals(new Tipo("funcion"))) {
                if (s.getTipoParam().equals(new Tipo("producto"))) {
                    List<Tipo> listaParam = s.getTipoParam().getProducto();
                    int i = 1;
                    token.append("\t+ numParam: " + listaParam.size() + "\n");
                    for (Tipo tparam : listaParam) {
                        token.append("\t\t+ TipoParam" + i + ": '" + tparam + "'\n");
                        i++;
                    }
                } else if (s.getTipoParam().equals(new Tipo("vacio"))) {
                    token.append("\t+ numParam: 0\n");
                    token.append("\t\t+ TipoParam: vacío'\n");

                } else {
                    token.append("\t+ numParam: 1\n");
                    token.append("\t\t+ TipoParam1: " + s.getTipoParam() + "\n");
                }

                token.append("\t+ TipoRetorno: '" + s.getTipoRetorno() + "'\n");
                token.append("\t+ EtiqFuncion: '" + s.getEtiqFuncion() + "'\n");
            }
            else {
                token.append("\t+ despl: " + s.getDeslp() + "\n");
            }
            token.append("---------   ---------\n");
        }

        if (isTSGlobal()) {
            byte[] content = new byte[(int) bwR.length()];
            bwR.read(content);
            bwR.seek(0);
            bwR.write(token.toString().getBytes());
            bwR.write(content);
        } else
            this.bw.write(token.toString());
    }

}
