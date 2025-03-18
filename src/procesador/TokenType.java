package procesador;

public enum TokenType{
    PRboolean("boolean"), PRfun("function"), PRif("if"), PRinput("input"), PRint("int"), PRoutput("output"), PRreturn("return"), PRstring("string"), 
    PRvar("var"), PRvoid("void"), PRwhile("while"), entero, cadena, id, opAsig, opAsigDiv, coma, puntoComa, paren, 
    llave, opSuma, opOr, opIgual, finFich, tipoError, tipoOk;

    private final String PR;
    TokenType(String palabraReservada){
        PR = palabraReservada;
    }

    TokenType(){
        this.PR = "";
        
    }

    public String getPR() {
        return PR;
    }
}

