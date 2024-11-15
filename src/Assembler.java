/**
 * Project Name: CPU Simulator
 * Description: This Simulates a 32 bit CPU and with Assembler
 * Author: Liam Lowry
 * Date: 01/16/2024
 */

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Assembler {
    public static void main(String[] args) throws Exception {
        Path inputFilePath = Paths.get(args[0]);
        String input = new String(Files.readAllBytes(inputFilePath));
        Lexer lexer = new Lexer(input);
        LinkedList<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        String bitcode = parser.parse();
        File file = new File(args[1]);
        PrintWriter printWriter= new PrintWriter(file.getAbsoluteFile());
        printWriter.print(bitcode);
        printWriter.flush();
        printWriter.close();
    }
}
