import java.util.HashMap;
import java.util.LinkedList;

public class Lexer {
    private StringHandler stringHandler;
    private LinkedList<Token> tokens;
    private HashMap<String, Token.TokenType> keywords;

    private class StringHandler {
        private String input;
        private int index = 0;

        public StringHandler(String input){
            this.input=input;
        }

        /**
         * returns the character i ahead of the current index without increasing the current index
         * @param i amount ahead of the current index
         * @return the character at index+i
         */
        public char peek(int i){
            return input.charAt(index+i);
        }

        /**
         * returns a substring starting at the currrent index and ending i after
         * @param i the size of the substring
         * @return the substring from current index to index+i
         */
        public String peekString(int i){
            return input.substring(index,index+i);
        }

        /**
         * increases the index by i
         */
        public void swallow(int i){
            index += i;
        }

        /**
         * to see if the index has reached the end of the input
         * @return true if the index is greater or equal to the end
         */
        public boolean atEnd(){
            return index >= input.length()-1;
        }

        /**
         * gets the rest of the input after the current index as a substring
         */
        public String remainder(){
            return input.substring(index);
        }

    }

    public Lexer(String input){
        stringHandler = new StringHandler(input);
        tokens = new LinkedList<>();
        populateKeywords();
    }

    public LinkedList<Token> lex() throws Exception {
        while(!stringHandler.atEnd()){
            if(stringHandler.peek(0)=='R' && Character.isDigit(stringHandler.peek(1))) {
                if(stringHandler.remainder().length()>2 && Character.isDigit(stringHandler.peek(2))){
                    tokens.add(new Token(Token.TokenType.REGISTER,String.valueOf(stringHandler.peek(1))+stringHandler.peek(2)));
                    stringHandler.swallow(3);
                }else {
                    tokens.add(new Token(Token.TokenType.REGISTER, String.valueOf(stringHandler.peek(1))));
                    stringHandler.swallow(2);
                }
            }else if(Character.isLetter(stringHandler.peek(0))){
                tokens.add(processKeyword());
            }else if(Character.isDigit(stringHandler.peek(0)) || stringHandler.peek(0)=='-'){
                tokens.add(processNumber());
            }else if(stringHandler.peek(0)=='\n'){
                tokens.add(new Token(Token.TokenType.NEWLINE));
                stringHandler.swallow(1);
            }else{
                stringHandler.swallow(1);
            }
        }
        return tokens;
    }

    private Token processKeyword() throws Exception {
        String word = new String();
        int i=0;
        while(stringHandler.remainder().length()>i && Character.isLetter(stringHandler.peek(i))){
            i++;
        }
        word=stringHandler.peekString(i);
        stringHandler.swallow(i);
        if(keywords.containsKey(word)){
            return new Token(keywords.get(word));
        }else{
            throw new Exception("unsupported keyword");
        }
    }

    private Token processNumber(){
        String number = new String();
        int i=0;
        if(stringHandler.peek(0)=='-')
            i++;
        while(stringHandler.remainder().length()>i && Character.isDigit(stringHandler.peek(i))){
            i++;
        }
        number=stringHandler.peekString(i);
        stringHandler.swallow(i);
        return new Token(Token.TokenType.NUMBER,number);
    }

    private void populateKeywords(){
        keywords = new HashMap<>();
        keywords.put("MATH", Token.TokenType.MATH);
        keywords.put("ADD", Token.TokenType.ADD);
        keywords.put("SUBTRACT", Token.TokenType.SUBTRACT);
        keywords.put("MULTIPLY", Token.TokenType.MULTIPLY);
        keywords.put("AND", Token.TokenType.AND);
        keywords.put("OR", Token.TokenType.OR);
        keywords.put("NOT", Token.TokenType.NOT);
        keywords.put("XOR", Token.TokenType.XOR);
        keywords.put("COPY", Token.TokenType.COPY);
        keywords.put("HALT", Token.TokenType.HALT);
        keywords.put("BRANCH", Token.TokenType.BRANCH);
        keywords.put("JUMP", Token.TokenType.JUMP);
        keywords.put("CALL", Token.TokenType.CALL);
        keywords.put("PUSH", Token.TokenType.PUSH);
        keywords.put("LOAD", Token.TokenType.LOAD);
        keywords.put("RETURN", Token.TokenType.RETURN);
        keywords.put("STORE", Token.TokenType.STORE);
        keywords.put("PEEK", Token.TokenType.PEEK);
        keywords.put("POP", Token.TokenType.POP);
        keywords.put("EQUAL", Token.TokenType.EQUAL);
        keywords.put("UNEQUAL", Token.TokenType.UNEQUAL);
        keywords.put("GREATER", Token.TokenType.GREATER);
        keywords.put("LESS", Token.TokenType.LESS);
        keywords.put("GREATEROREQUAL", Token.TokenType.GREATEROREQUAL);
        keywords.put("LESSOREQUAL", Token.TokenType.LESSOREQUAL);
        keywords.put("SHIFT", Token.TokenType.SHIFT);
        keywords.put("LEFT", Token.TokenType.LEFT);
        keywords.put("RIGHT", Token.TokenType.RIGHT);
    }

}
