package procesador;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoTerminal {

    private Set<TokenType> first = new HashSet<>();
    private Set<TokenType> follow = new HashSet<>();

    NoTerminal(List<TokenType> first, List<TokenType> follow){
        for(TokenType c : first){
            this.first.add(c);
        }
        for(TokenType c : follow){
            this.follow.add(c);
        }
        
    }

    public Set<TokenType> getFirst() {
        return first;
    }

    public Set<TokenType> getFollow() {
        return follow;
    }
}
