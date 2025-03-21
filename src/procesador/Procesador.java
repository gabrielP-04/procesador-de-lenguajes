package procesador;

public class Procesador {

    static AnalizadorSintactico aSin;

    public static void main(String[] args) {

        //Verificacón de argumentos
        if (args.length != 1) {
            if (args.length == 0)
                System.err.print("Es necesario añadir un archivo.\n");
            else
                System.err.print("Número de argumentos erronio.\n");
        } else {

            aSin = new AnalizadorSintactico(args[0]);
            try {
                aSin.analisis();
                System.out.println("\u001B[32mAnalisis completado con exito");
            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
        // lexico no se imprima el id
        // errores que diga que caraceter esta mal
    }
}
