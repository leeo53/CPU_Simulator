public class Token {
    public enum TokenType{REGISTER, NUMBER, MATH, ADD, SUBTRACT, MULTIPLY, AND, OR, NOT, XOR, COPY, HALT, BRANCH, JUMP,
    CALL, PUSH, LOAD, RETURN, STORE, PEEK, POP, EQUAL, UNEQUAL, GREATER, LESS, GREATEROREQUAL, LESSOREQUAL, SHIFT, RIGHT,
    LEFT, NEWLINE}
    private TokenType tokenType;
    private String value="NA";

    public Token(TokenType tokenType, String value ){
        this.tokenType=tokenType;
        this.value=value;
    }

    public Token(TokenType tokenType){
        this.tokenType=tokenType;
    }

    public TokenType getTokenType(){
        return tokenType;
    }

    public String getValue(){
        return value;
    }

    @Override
    public String toString(){
        if(value.equals("NA"))
            return tokenType.toString();
        else
            return tokenType.toString()+"("+value+")";
    }
}
