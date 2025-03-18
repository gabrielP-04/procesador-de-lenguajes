package procesador;

public class Token {

    private TokenType type;
    private Object atribute;

    Token(TokenType type) {
        this.type = type;
    }

    Token(TokenType type, int atribute) {
        this.type = type;
        this.atribute = atribute;
    }

    Token(TokenType type, String atribute) {
        this.type = type;
        this.atribute = atribute;
    }

    public TokenType getType() {
        return this.type;
    }

    public Object getAtribute() {
        return this.atribute;
    }

    public String toString() {
        if(atribute == null)
            return "<" + type + ", >\n";
        else
            return "<" + type + ", " + atribute + ">\n";
    }

}
