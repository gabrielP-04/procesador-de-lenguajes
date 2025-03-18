package procesador;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GestorErrores {

    private BufferedWriter bwErrores;
    private boolean error = false;
    private String cwd = System.getProperty("user.dir");
    private int linea = 0;
    private int puntero = 0;
    private Token token;
    private char car;

    GestorErrores(){
        bwErrores = writeFich("errores.txt");
        bwErrores = writeFich("errores.txt");
        try {
            bwErrores.write("Errores analizador lexico:\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selgErrorAnalizador (String codigo, int puntero, int linea){
        this.puntero = puntero;
        this.linea = linea;
        gErrorAnalizador(codigo);
    }

    public void selgErrorAnalizador (String codigo, int puntero, int linea, Token token){
        this.puntero = puntero;
        this.linea = linea;
        this.token = token;
        gErrorAnalizador(codigo);
    }

    public void selgErrorAnalizador (String codigo){
        gErrorAnalizador(codigo);
    }

    private void gErrorAnalizador (String codigo){
        String[] partes = codigo.split("-");
        switch (analizador(partes[0])) {
            case 1:
                gErrorALex(partes[1]);
                
                break;
            case 2:
                gErrorSin(partes[1]);
                throw new RuntimeException("Error sinctáctico");
            case 3:
                gErrorASem(partes[1]);
                break;
            default:

                throw new IllegalArgumentException("Codigo error invalido");
        }
    }

    private int analizador(String tipoAnalizador){
        if (tipoAnalizador.equals("Lx")) {
            return 1;
        } else if (tipoAnalizador.equals("Sx")){
            return 2;
        } else if (tipoAnalizador.equals("Sm")) {
            return 3;
        } else{
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
            fw = new FileWriter(cwd + "/data/aLex/" + nameFich);
        } catch (IOException e) {
            e.printStackTrace();
        } // crea FileWriter para crear el archivo de salida
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }
    
    private void gErrorALex(String num) {
        error = true;
        int code = Integer.parseInt(num);
        String message = "ERROR LÉLXICO:(" + code + "): ";

        switch (code) {

            case 50:
                message += "entero demasiado largo en la posición " + puntero + " de la linea " + linea + "\n";
                break;
            case 51:
                message += "cadena demasiado larga en la posición " + puntero + " de la linea " + linea + "\n";
                break;
            case 52:
                message += "variable ya declarada previamente en la posicion " + puntero + " de la linea " + linea + "\n";
                break;
            default:
                message += "caracter no reconocido en la posición " + puntero + " de la linea " + linea + "\n";
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

        if (codigo.equals("1")) {
            message +=  token + "inesperado en la posicion "
                + puntero + " de la línea " + linea + ".\n";
        }
        else{
        message += car + " esperado al contrario de " + token + " en a posición "
        + puntero + " de la línea " + linea + ".\n";
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
        String message = "ERROR SEMÁNTICO:(" + code + "): ";

        switch (code) {

            case 1:
                message += "Operación || solo definido para tipos lógicos en la posición " + puntero + " de la linea " + linea + "\n";
                break;
            case 2:
                message += "Operación == solo definido para tipos enteros en la posición " + puntero + " de la linea " + linea + "\n";
                break;
            case 3:
                message += "Operación + solo definido para tipos enteros en la posicion " + puntero + " de la linea " + linea + "\n";
                break;

            case 4: 
                message += "Los parámetros no coinciden con la llamada de la función en la posicion " + puntero + " de la linea " + linea + "\n";
                break;
            case 5: 
                message += "La sentencia input solo puede estar operando con una variable tipo entero o cadena en la posicion " + puntero + " de la linea " + linea + "\n";
                break;

            case 6: 
                message += "Se esperaba un tipo boolean en la posicion " + puntero + " de la linea " + linea + "\n";
                break;

            case 7: 
                message += "La sentencia while debe tener una condicion de tipo lógico en la posicion " + puntero + " de la linea " + linea + "\n";
                break;

            case 8: 
                message += "La funcion no devuelve nada en la posicion " + puntero + " de la linea " + linea + "\n";
                break;

            case 9: 
                message += "El tipo que devuelve la funcion no es correcto en la posicion " + puntero + " de la linea " + linea + "\n";
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


    public void terminarGE() throws IOException{
        if(!error){
                bwErrores.write("\tSuccess!");
                bwErrores.close();
        }
    }

}
