package procesador;

import java.io.IOException;

public class Procesador {

    static AnalizadorSintactico aSin;

    public static void main(String[] args) {

        //Verificacón de argumentos
        if (args.length != 1) {
            if (args.length == 0)
                System.err.print("Es necesario añadir un archivo.\n");
            else
                System.err.print("Número de argumentos erroneo.\n");
        } else {

            try {
                aSin = new AnalizadorSintactico(args[0]);
                aSin.analisis();
                System.out.println("\u001B[32mAnalisis completado con exito");
            } catch (IOException e) {
                System.err.println("Error de fichero");
            } catch (Exception e) {
                System.err.println(e.getMessage()); 
            }
        }
        // lexico no se imprima el id
        // errores que diga que caraceter esta mal
    }
}
