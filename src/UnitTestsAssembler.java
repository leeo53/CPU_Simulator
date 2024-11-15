import org.junit.Test;

import java.util.LinkedList;
import static org.junit.Assert.*;

public class UnitTestsAssembler {
    @Test
    public void test1Lexer() throws Exception {
        String testInput = "MATH ADD R1 R2";
        Lexer lexer = new Lexer(testInput);
        LinkedList<Token> tokens = lexer.lex();
        assertEquals("MATH",tokens.removeFirst().toString());
        assertEquals("ADD",tokens.removeFirst().toString());
        assertEquals("REGISTER(1)",tokens.removeFirst().toString());
        assertEquals("REGISTER(2)",tokens.removeFirst().toString());
    }

    @Test
    public void test2Lexer() throws Exception{
        String testInput = "MATH R12 243\n" +
                "BRANCH R15";
        Lexer lexer = new Lexer(testInput);
        LinkedList<Token> tokens = lexer.lex();
        assertEquals("MATH",tokens.removeFirst().toString());
        assertEquals("REGISTER(12)",tokens.removeFirst().toString());
        assertEquals("NUMBER(243)",tokens.removeFirst().toString());
        assertEquals("NEWLINE",tokens.removeFirst().toString());
        assertEquals("BRANCH",tokens.removeFirst().toString());
        assertEquals("REGISTER(15)",tokens.removeFirst().toString());
    }

    @Test
    public void testParserMath() throws Exception{
        String testInput="COPY R1 5\n" +
                "MATH ADD R1 R1 R2\n" +
                "MATH ADD R2 R2\n"+
                "MATH ADD R2 R1 R3\n" +
                "HALT";
        Lexer lexer = new Lexer(testInput);
        LinkedList<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        String output = parser.parse();
        assertEquals("00000000000000010100000000100001\n" +
                "00000000000010000111100001000010\n" +
                "00000000000000001011100001000011\n" +
                "00000000000100000111100001100010\n" +
                "00000000000000000000000000000000", output);
    }

    @Test
    public void testParserBranch() throws Exception {
        String testInput="COPY R1 5\n" +
                "COPY R2 4\n" +
                "BRANCH UNEQUAL R1 R2 R0 2\n"+
                "COPY R1 6\n" +
                "BRANCH LESS R2 R1 4\n" +
                "JUMP R1 1\n" +
                "COPY R2 12\n" +
                "COPY R1 4\n" +
                "JUMP 2\n" +
                "HALT";
        Lexer lexer = new Lexer(testInput);
        LinkedList<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        String output = parser.parse();
        assertEquals("00000000000000010100000000100001\n" +
                "00000000000000010000000001000001\n" +
                "00000010000010001000010000000110\n" +
                "00000000000000011000000000100001\n" +
                "00000000001000001000100000100111\n" +
                "00000000000000000100000000000101\n" +
                "00000000000000110000000001000001\n" +
                "00000000000000010000000000100001\n" +
                "00000000000000000000000001000100\n" +
                "00000000000000000000000000000000",output);
    }

    @Test
    public void testParserCallandReturn() throws Exception{
        String testInput="COPY R3 5\n" +
                "COPY R4 4\n" +
                "CALL UNEQUAL R3 R4 6\n" +
                "COPY R6 1\n" +
                "CALL UNEQUAL R3 R4 R0 4\n"+
                "CALL R0 3\n"+
                "CALL 2\n"+
                "MATH ADD R6 R5\n" +
                "JUMP 8\n" +
                "MATH ADD R3 R4 R5\n" +
                "RETURN\n" +
                "HALT";
        Lexer lexer = new Lexer(testInput);
        LinkedList<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        String output = parser.parse();
        assertEquals("00000000000000010100000001100001\n" +
                "00000000000000010000000010000001\n" +
                "00000000001100001100010010001011\n" +
                "00000000000000000100000011000001\n" +
                "00000100000110010000010000001010\n" +
                "00000000000000001100000000001001\n" +
                "00000000000000000000000001001000\n" +
                "00000000000000011011100010100011\n" +
                "00000000000000000000000100000100\n" +
                "00000000000110010011100010100010\n" +
                "00000000000000000000000000010000\n" +
                "00000000000000000000000000000000",output);
    }

    @Test
    public void testParserPushPop() throws Exception{
        String testInput="COPY R7 50\n" +
                "COPY R8 48\n" +
                "PUSH MULTIPLY R7 R8\n" +
                "PEEK R0 R9 1\n" +
                "PUSH AND R7 R8 R0\n" +
                "PEEK R2 R1 R9\n" +
                "PUSH ADD R9 200\n" +
                "POP R10\n" +
                "POP R11\n" +
                "HALT";
        Lexer lexer = new Lexer(testInput);
        LinkedList<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        String output = parser.parse();
        assertEquals("00000000000011001000000011100001\n" +
                "00000000000011000000000100000001\n" +
                "00000000000000011101110100001111\n" +
                "00000000000010000000000100111011\n" +
                "00000000001110100010000000001110\n" +
                "00000000000100000100000100111010\n" +
                "00000000001100100011100100101101\n" +
                "00000000000000000000000101011001\n" +
                "00000000000000000000000101111001\n" +
                "00000000000000000000000000000000",output);
    }

    @Test
    public void testParserLoadStore() throws Exception {
        String testInput="COPY R12 34\n" +
                "STORE R12 R0 32\n" +
                "COPY R13 -4\n" +
                "LOAD R0 R14 32 \n" +
                "MATH SUBTRACT R14 R13 R15\n" +
                "STORE R12 R15 R0\n" +
                "LOAD R12 R0 R1\n" +
                "COPY R16 32\n" +
                "STORE R16 12\n" +
                "LOAD R17 32\n" +
                "HALT";
        Lexer lexer = new Lexer(testInput);
        LinkedList<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        String output = parser.parse();
        assertEquals("00000000000010001000000110000001\n" +
                "00000001000000110000000000010111\n" +
                "11111111111111110000000110100001\n" +
                "00000001000000000000000111010011\n" +
                "00000000011100110111110111100010\n" +
                "00000000011000111100000000010110\n" +
                "00000000011000000000000000110010\n" +
                "00000000000010000000001000000001\n" +
                "00000000000000110000001000010101\n" +
                "00000000000010000000001000110001\n" +
                "00000000000000000000000000000000",output);
    }

    @Test
    public void testAssembler() throws Exception{
        java.net.URL inputPath = UnitTestsAssembler.class.getResource("input.txt");
        java.net.URL outputPath = UnitTestsAssembler.class.getResource("output.txt");
        String[] args = new String[]{inputPath.getPath().substring(3).replace("%20"," "),outputPath.getPath().substring(3).replace("%20"," ")};
        Assembler.main(args);
    }
}
