package procesador;

import java.io.IOException;

public class Procesador {

    static AnalizadorSintactico aSin;

    public static void main(String[] args) {

        //Verificacón de argumentos
        if (args.length != 1) {
            if (args.length == 0)
                System.err.print("\u001B[31mEs necesario añadir un archivo.\n");
            else
                System.err.print("\u001B[31mNúmero de argumentos erroneo.\n");
        } else {

            boolean error = false;
            try {
                aSin = new AnalizadorSintactico(args[0]);
                error = aSin.analisis();
                if (!error) {
                    System.out.println("\u001B[32mAnalisis completado con exito");
                } else {
                    System.out.println("\u001B[31mSe han encontrado errores durante el análisis. \u001B[0mVerificar el fichero data/errores.txt para más detalles.");
                }
                
            } catch (IOException e) {
                System.err.println("\u001B[31mError de fichero");
            } catch (Exception e) {
                System.err.println(e.getMessage()); 
            }
        }
        // lexico no se imprima el id
        // errores que diga que caraceter esta mal
    }
}
