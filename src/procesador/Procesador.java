package procesador;

public class Procesador {

    static AnalizadorSintactico aSin;

    public static void main(String[] args) {

        if (args.length != 2) {
            if (args.length == 1)
                System.err.print("Es necesario añadir un archivo.");
            else
                System.err.print("Número de argumentos erronio.");
        } else {

            aSin = new AnalizadorSintactico(args[1]);
            try {
                aSin.ASin();
                System.out.println("\u001B[32mAnalisis completado con exito");
            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
        // lexico no se imprima el id
        // errores que diga que caraceter esta mal
    }
}
