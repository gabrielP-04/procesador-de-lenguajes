package procesador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GestorErrores {

    private BufferedWriter bwErrores;
    private String cwd = System.getProperty("user.dir");

    private boolean error = false;

    private int linea;
    private int puntero;

    GestorErrores() {
        bwErrores = writeFich("errores.txt");
    }

    boolean getError () {
        return error;
    }

    public void selgErrorAnalizador(String codigo, int puntero, int linea) {
        this.puntero = puntero + 1;
        this.linea = linea;
        gErrorAnalizador(codigo);
    }

    private void gErrorAnalizador(String codigo) {
        error = true;
        String[] partes = codigo.split("-");
        switch (analizador(partes[0])) {
            case 1:
                gErrorALex(partes[1]);
                try {
                    terminarGE();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException("\u001B[31mError léxico. \u001B[0mVerificar el fichero data/errores.txt para más detalles.");
            case 2:
                gErrorSin(partes[1]);
                try {
                    terminarGE();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                throw new RuntimeException("\u001B[31mError sinctáctico. \u001B[0mVerificar el fichero data/errores.txt para más detalles.");
            case 3:
                gErrorASem(partes[1]);
                break;
            default:

                throw new IllegalArgumentException("Código error inválido");
        }
    }

    private int analizador(String tipoAnalizador) {
        if (tipoAnalizador.equals("Lx")) {
            return 1;
        } else if (tipoAnalizador.equals("Sx")) {
            return 2;
        } else if (tipoAnalizador.equals("Sm")) {
            return 3;
        } else {
            return -1;
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
            fw = new FileWriter(cwd + "/data/" + nameFich);
        } catch (IOException e) {
            e.printStackTrace();
        } // crea FileWriter para crear el archivo de salida
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }

    private void gErrorALex(String num) {
        error = true;
        int code = Integer.parseInt(num);
        String message = "ERROR LÉXICO (" + code + "): ";

        switch (code) {

            case 50:
                message += "entero demasiado largo en la posición " + puntero + " de la línea " + linea + ".\n";
                break;
            case 51:
                message += "cadena demasiado larga en la posición " + puntero + " de la línea " + linea + ".\n";
                break;
            case 52:
                message += "variable ya declarada previamente en la posición " + puntero + " de la línea " + linea
                        + ".\n";
                break;
            default:
                message += "caracter no reconocido en la posición " + puntero + " de la línea " + linea + ".\n";
                break;

        }

        try {
            bwErrores.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void gErrorSin(String codigo) {

        String message = "ERROR SINTÁCTICO: ";

        if (codigo.equals("0")) {
            message += "Caracter inesperado en la posición " + puntero + " de la línea " + linea + ".\n";
        } else {
            message += codigo + " esperado en la posición " + puntero + " de la línea " + linea + ".\n";
        }

        try {
            bwErrores.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void gErrorASem(String num) {
        error = true;
        int code = Integer.parseInt(num);
        String message = "ERROR SEMÁNTICO (" + code + "): ";

        switch (code) {

            case 1:
                message += "Operación || solo definido para tipos lógicos en la posición " + puntero + " de la línea "
                        + linea + ".\n";
                break;
            case 2:
                message += "Operación == solo definido para tipos enteros en la posición " + puntero + " de la línea "
                        + linea + ".\n";
                break;
            case 3:
                message += "Operación + solo definida para tipos enteros en la posición " + puntero + " de la línea "
                        + linea + ".\n";
                break;

            case 4:
                message += "Los parámetros no coinciden con la llamada de la función en la posición " + puntero
                        + " de la línea " + linea + ".\n";
                break;
            case 5:
                message += "Las sentencias input y output solo pueden operar con una variable tipo entero o cadena en la posición "
                        + puntero + " de la línea " + linea + ".\n";
                break;

            case 6:
                message += "Se esperaba un tipo boolean en la posición " + puntero + " de la línea " + linea + ".\n";
                break;

            case 7:
                message += "La sentencia while debe tener una condicion de tipo lógico en la posición " + puntero
                        + " de la línea " + linea + ".\n";
                break;

             case 8:
                message += "El tipo de las sentencia return no coinciden en la línea " + linea + ".\n";
                break;

            case 9:
                message += "El tipo que devuelve la función no es correcto en la línea " + linea + ".\n";
                break;

            case 10:
                message += "La sentencia return está mal ubicado en la línea " + linea + ".\n";
                break;

            case 11:
                message += "La asignación no coincide con el tipo de la variable en la posición " + puntero
                        + " de la línea " + linea + ".\n";
                break;

            default:
                break;
        }

        try {
            bwErrores.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void terminarGE() throws IOException {
        if (!error) {
            bwErrores.write("No se han econtrado errores en el análisis.");
        }
        bwErrores.close();
    }

}
