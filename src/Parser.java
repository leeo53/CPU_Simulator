import java.util.LinkedList;

public class Parser {
    private TokenHandler tokenHandler;
    private class TokenHandler{
        private LinkedList<Token> tokens;

        public TokenHandler(LinkedList<Token> tokens){
            this.tokens=tokens;
        }

        /**
         * peek i tokens ahead
         * @param i number of tokens to peek ahead
         * @return the token i ahead
         */
        public Token peek(int i){
            if(tokens.size()<i){
                return null;
            }
            return tokens.get(i);
        }

        /**
         * if there are still tokens in the list returns true
         * @return true if there are still tokens in the list
         */
        public boolean hasTokens(){
            return !(tokens.isEmpty());
        }

        /**
         * checks if the current token has the same token type and removes it if it does and returns the token, otherwise returns null
         * @param tokenType token type to compare to current
         * @return the token if it matches, null if not
         */
        public Token matchAndRemove(Token.TokenType tokenType){
            Token current = tokens.peek();
            if(current.getTokenType().equals(tokenType)){
                tokens.remove();
                return current;
            }
            return null;
        }
    }

    public Parser(LinkedList<Token> tokens){
        tokenHandler = new TokenHandler(tokens);
    }
    public String parse() throws Exception {
        String bitcode="";
        while (tokenHandler.hasTokens()){
            bitcode+=parseStatement();
        }
        return bitcode;
    }

    /**
     * checks the type of statement and calls method
     * @return the statement on that line in bytecode
     * @throws Exception if the statement is incorrect
     */
    private String parseStatement() throws Exception {
        if(tokenHandler.matchAndRemove(Token.TokenType.MATH)!=null || tokenHandler.matchAndRemove(Token.TokenType.SHIFT)!=null){
            return parseMath();
        }else if(tokenHandler.matchAndRemove(Token.TokenType.BRANCH)!=null){
            return parseBranch();
        }else if(tokenHandler.matchAndRemove(Token.TokenType.HALT)!=null){
            return "00000000000000000000000000000000";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.COPY)!=null){
            return parseCopy();
        }else if(tokenHandler.matchAndRemove(Token.TokenType.JUMP)!=null){
            return parseJump();
        }else if(tokenHandler.matchAndRemove(Token.TokenType.CALL)!=null){
            return parseCall();
        }else if(tokenHandler.matchAndRemove(Token.TokenType.PUSH)!=null){
            return parsePush();
        }else if(tokenHandler.matchAndRemove(Token.TokenType.POP)!=null){
            return parsePop();
        }else if(tokenHandler.matchAndRemove(Token.TokenType.LOAD)!=null){
            return parseLoad();
        }else if(tokenHandler.matchAndRemove(Token.TokenType.STORE)!=null){
            return parseStore();
        }else if(tokenHandler.matchAndRemove(Token.TokenType.RETURN)!=null){
            return "00000000000000000000000000010000";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.PEEK)!=null){
            return parsePeek();
        }else if(tokenHandler.matchAndRemove(Token.TokenType.NEWLINE)!=null){
            return "\n";
        }else{
            throw new Exception("Incorrect Instruction");
        }
    }

    private String parseMath() throws Exception {
        String instruction="00000000";
        String function = parseFunction();
        String registers = parseThreeR();
        if(registers.length()==15)
            return instruction+registers.substring(0,10)+function+registers.substring(10)+"00010";
        else {
            instruction += "00000";
            return instruction + registers.substring(0, 5) + function + registers.substring(5) + "00011";
        }
    }

    private String parseBranch() throws Exception {
        String function = parseFunction();
        String registers = parseThreeR();
        Token token = tokenHandler.matchAndRemove(Token.TokenType.NUMBER);
        if (token == null) throw new Exception("Number Expected");
        if(registers.length()==15)
            return processNumber(token,8)+registers.substring(0,10)+function+"0000000110";
        else
            return processNumber(token,13)+registers.substring(0,5)+function+registers.substring(5)+"00111";
    }

    private String parseCopy() throws Exception {
        String destinationRegister = parseDestOnly();
        Token token = tokenHandler.matchAndRemove(Token.TokenType.NUMBER);
        if(token==null) throw new Exception("Incorrect Instruction");
        return processNumber(token,18)+"0000"+destinationRegister+"00001";
    }

    private String parseJump() throws Exception {
            String register=parseDestOnly();
            if(register.length()==5) {
                Token token = tokenHandler.matchAndRemove(Token.TokenType.NUMBER);
                if(token==null) throw new Exception("Number Expected");
                return processNumber(token, 18) + "00000000000101";
            } else
                return register+"00100";
    }

    private String parseCall() throws Exception{
        String function = parseFunction();
        String registers = parseThreeR();
        Token token = tokenHandler.matchAndRemove(Token.TokenType.NUMBER);
        if(token==null && registers.length()<27) throw new Exception("Number Expected");
        if(registers.length()==15)
            return processNumber(token,8)+registers.substring(0,10)+function+registers.substring(10)+"01010";
        else if(registers.length()==10)
            return processNumber(token,13)+registers.substring(0,5)+function+ registers.substring(5)+"01011";
        else if(registers.length()==5)
            return processNumber(token,18)+function+registers+"01001";
        else
            return registers+"01000";
    }

    private String parsePush() throws Exception{
        String function = parseFunction();
        String registers = parseThreeR();
        if(registers.length()==15)
            return "00000000"+registers.substring(0,10)+function+registers.substring(10)+"01110";
        else if(registers.length()==10)
            return "0000000000000" + registers.substring(0, 5) + function + registers.substring(5) + "01111";
        else {
            Token token = tokenHandler.matchAndRemove(Token.TokenType.NUMBER);
            if (token == null) throw new Exception("Number Expected");
            return processNumber(token,18) + function + registers + "01101";
        }
    }

    private String parsePop() throws Exception{
        return "0000000000000000000000"+parseDestOnly()+"11001";
    }

    private String parseLoad() throws Exception{
            String registers = parseThreeR();
            if(registers.length()==15)
                return "00000000"+registers.substring(0,10)+"0000"+registers.substring(10)+"10010";
            else if(registers.length()==10) {
                Token token = tokenHandler.matchAndRemove(Token.TokenType.NUMBER);
                if(token==null) throw new Exception("Number Expected");
                return processNumber(token, 13) + registers.substring(0, 5) + "0000" + registers.substring(5) + "10011";
            }else {
                Token token = tokenHandler.matchAndRemove(Token.TokenType.NUMBER);
                if(token==null) throw new Exception("Number Expected");
                return processNumber(token, 18) + "0000" + registers + "10001";
            }
    }

    private String parseStore() throws Exception{
            String registers = parseThreeR();
            if(registers.length()==15)
                return "00000000"+registers.substring(0,10)+"0000"+registers.substring(10)+"10110";
            else if(registers.length()==10) {
                Token token = tokenHandler.matchAndRemove(Token.TokenType.NUMBER);
                if (token == null) throw new Exception("Number Expected");
                return processNumber(token, 13) + registers.substring(0, 5) + "0000" + registers.substring(5) + "10111";
            }else {
                Token token = tokenHandler.matchAndRemove(Token.TokenType.NUMBER);
                if (token == null) throw new Exception("Number Expected");
                return processNumber(token, 18) + "0000" + registers + "10101";
            }
    }

    private String parsePeek() throws Exception{
            String registers = parseThreeR();
            if(registers.length()==15)
                return "00000000" + registers.substring(0, 10) + "0000" + registers.substring(10) + "11010";
            else {
                Token token = tokenHandler.matchAndRemove(Token.TokenType.NUMBER);
                if(token==null) throw new Exception("Number Expected");
                return processNumber(token,13)+registers.substring(0,5)+"0000"+registers.substring(5)+"11011";
            }
    }

    private String parseTwoR() throws Exception {
        if(tokenHandler.peek(0).getTokenType().equals(Token.TokenType.REGISTER) && tokenHandler.peek(1)!=null && tokenHandler.peek(1).getTokenType().equals(Token.TokenType.REGISTER)) {
            String registers = "";
            Token sourceRegister = tokenHandler.matchAndRemove(Token.TokenType.REGISTER);
            if (sourceRegister == null) {
                throw new Exception("Incorrect Instruction");
            }
            registers += processNumber(sourceRegister, 5);
            Token destinationRegister = tokenHandler.matchAndRemove(Token.TokenType.REGISTER);
            if (destinationRegister == null) {
                throw new Exception("Incorrect Instruction");
            }
            return registers + processNumber(destinationRegister, 5);
        }else return parseDestOnly();
    }

    private String parseThreeR() throws Exception {
        if(tokenHandler.peek(0).getTokenType().equals(Token.TokenType.REGISTER) && tokenHandler.peek(1)!=null && tokenHandler.peek(1).getTokenType().equals(Token.TokenType.REGISTER) && tokenHandler.peek(2)!=null && tokenHandler.peek(2).getTokenType().equals(Token.TokenType.REGISTER)) {
            String registers = "";
            Token Register1 = tokenHandler.matchAndRemove(Token.TokenType.REGISTER);
            if (Register1 == null) {
                throw new Exception("Incorrect Instruction");
            }
            registers += processNumber(Register1, 5);
            Token Register2 = tokenHandler.matchAndRemove(Token.TokenType.REGISTER);
            if (Register2 == null) {
                throw new Exception("Incorrect Instruction");
            }
            registers += processNumber(Register2, 5);
            Token destinationRegister = tokenHandler.matchAndRemove(Token.TokenType.REGISTER);
            if (destinationRegister == null) {
                throw new Exception("Incorrect Instruction");
            }
            return registers + processNumber(destinationRegister, 5);
        }else{
            return parseTwoR();
        }
    }

    private String parseNoR() throws Exception {
        Token token = tokenHandler.matchAndRemove(Token.TokenType.NUMBER);
        if(token!=null){
            String immediateValue="";
            long number = Long.parseLong(token.getValue());
            for(int i = 0; i<27;i++){
                immediateValue=number%2+immediateValue;
                number/=2;
            }
            return immediateValue;
        }else
            throw new Exception("Expected Number");
    }

    private String parseDestOnly() throws Exception {
        if(tokenHandler.peek(0).getTokenType().equals(Token.TokenType.REGISTER)){
            Token destinationRegister = tokenHandler.matchAndRemove(Token.TokenType.REGISTER);
            return processNumber(destinationRegister, 5);
        }else return parseNoR();
    }

    private String parseFunction() throws Exception {
        if(tokenHandler.matchAndRemove(Token.TokenType.ADD)!=null){
            return "1110";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.AND)!=null){
            return "1000";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.OR)!=null){
            return "1001";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.XOR)!=null){
            return "1010";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.NOT)!=null){
            return "1011";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.LEFT)!=null){
            return "1100";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.RIGHT)!=null){
            return "1101";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.SUBTRACT)!=null){
            return "1111";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.MULTIPLY)!=null){
            return "0111";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.EQUAL)!=null){
            return "0000";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.UNEQUAL)!=null){
            return "0001";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.LESS)!=null){
            return "0010";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.GREATER)!=null){
            return "0100";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.GREATEROREQUAL)!=null){
            return "0011";
        }else if(tokenHandler.matchAndRemove(Token.TokenType.LESSOREQUAL)!=null){
            return "0101";
        }else{
            return "0000";
        }
    }


    private String processNumber(Token numberToken, int length){
        long value = Long.parseLong(numberToken.getValue());
        String number="";
        boolean negative=false;
        if(value<0) {
            negative = true;
            value = Math.abs(value);
        }
        for(int i =0;i<length;i++){
            number=(Math.abs(value)%2)+number;
            value/=2;
        }
        if(!negative)
            return number;
        else{
            String notNumber="";
            for(int i=0;i<length;i++){
                if(number.charAt(i)=='1')
                    notNumber+="0";
                else
                    notNumber+="1";
            }
            char carry='1';
            String negativeNumber = "";
            for(int i=0;i<length;i++){
                if((notNumber.charAt(length-1-i)=='0' && carry=='1') || (notNumber.charAt(length-1-i)=='1' && carry=='0'))
                    negativeNumber="1"+negativeNumber;
                else
                    negativeNumber="0"+negativeNumber;
                if(!(notNumber.charAt(length-1-i)=='1' && carry=='1'))
                    carry='0';
            }
            return negativeNumber;
        }
    }

}
