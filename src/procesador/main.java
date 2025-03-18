package procesador;

public class main {

    static AnalizadorSintactico aSin;
    public static void main(String[] args) {
        
        aSin = new AnalizadorSintactico("/home/gabriel/Descargas/Caso2.txt");
        try {
            aSin.ASin();
            System.out.println("\u001B[32mAnalisis completado con exito");
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());;
        }
        
        //lexico no se imprima el id 
        //errores que diga que caraceter esta mal
        
        
    }
}
