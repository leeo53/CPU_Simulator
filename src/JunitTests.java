import static org.junit.Assert.*;

import org.junit.Test;
import sun.applet.Main;

import javax.annotation.processing.SupportedAnnotationTypes;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JunitTests {
    @Test
    public void testBit() throws Exception{ //assignment 1
        Bit bit1 = new Bit();
        Bit bit2 = new Bit();
        bit1.set(true);
        assertTrue(bit1.getValue());
        bit1.toggle();
        assertFalse(bit1.getValue());
        bit1.toggle();
        assertTrue(bit1.getValue());
        bit1.clear();
        assertFalse(bit1.getValue());
        bit1.set();
        assertTrue(bit1.getValue());
        bit2.clear();
        assertFalse(bit1.and(bit2).getValue());
        assertTrue(bit1.or(bit2).getValue());
        assertTrue(bit1.xor(bit2).getValue());
        bit1.clear();
        assertFalse(bit1.and(bit2).getValue());
        assertFalse(bit1.or(bit2).getValue());
        assertFalse(bit1.xor(bit2).getValue());
        bit2.set();
        assertFalse(bit1.and(bit2).getValue());
        assertTrue(bit1.or(bit2).getValue());
        assertTrue(bit1.xor(bit2).getValue());
        bit1.set();
        assertTrue(bit1.and(bit2).getValue());
        assertTrue(bit1.or(bit2).getValue());
        assertFalse(bit1.xor(bit2).getValue());
        assertFalse(bit1.not().getValue());
        assertEquals("t",bit1.toString());
        bit1.clear();
        assertEquals("f",bit1.toString());
    }

    @Test
    public void testWord() { //assignment 1
        Word testWord1 = new Word();
        Word testWord2 = new Word();
        Bit testBit = new Bit();
        testBit.set();
        testWord1.setBit(1, testBit);
        assertTrue(testWord1.getBit(1).getValue());
        assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,t,f", testWord1.toString());
        testWord1.set(3);
        assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,t,t", testWord1.toString());
        testWord1.set(4);
        assertEquals("f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,t,f,f", testWord1.toString());
        assertEquals(4,testWord1.getUnsigned());
        testWord1.set(31);
        assertEquals(31,testWord1.getUnsigned());
        testWord1.set(3);
        testWord2.copy(testWord1);
        assertEquals(3,testWord2.getUnsigned());
        testWord2.set(5);
        assertEquals(1,testWord1.and(testWord2).getUnsigned());
        assertEquals(7,testWord1.or(testWord2).getUnsigned());
        assertEquals(6,testWord1.xor(testWord2).getUnsigned());
        testWord1.set(4);
        assertEquals("t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,f,t,t",testWord1.not().toString());
        testWord1.set(-7);
        assertEquals(-7,testWord1.getSigned());
        testWord1.set(-2);
        assertEquals(-2,testWord1.getSigned());
        testWord1 = new Word();
        testBit.set();
        testWord1.setBit(31,testBit);
        assertEquals("t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t,t",testWord1.rightShift(31).toString());
        assertEquals("t,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f,f", testWord1.rightShift(31).leftShift(31).toString());
    }

    @Test
    public void testALUZero(){ //assignment 2
        ALU testALUZero = new ALU();
        testALUZero.operand1.set(0);
        testALUZero.operand2.set(0);
        Bit[] operation = new Bit[4];
        operation[0] = new Bit();
        operation[1] = new Bit();
        operation[2] = new Bit();
        operation[3] = new Bit();
        operation[0].set();
        operation[1].set();
        operation[2].set();
        operation[3].set();
        testALUZero.doOperation(operation); //subtract
        assertEquals(0,testALUZero.result.getUnsigned());
        operation[3].clear();
        testALUZero.doOperation(operation); //add
        assertEquals(0,testALUZero.result.getUnsigned());
        operation[3].set();
        operation[2].clear();
        testALUZero.doOperation(operation); //rightshift
        assertEquals(0,testALUZero.result.getUnsigned());
        operation[3].clear();
        testALUZero.doOperation(operation); //leftshift
        assertEquals(0,testALUZero.result.getUnsigned());
        operation[3].set();
        operation[2].set();
        operation[1].clear();
        testALUZero.doOperation(operation); //not
        assertEquals(4294967295L,testALUZero.result.getUnsigned());
        operation[3].clear();
        testALUZero.doOperation(operation); //xor
        assertEquals(0,testALUZero.result.getUnsigned());
        operation[3].set();
        operation[2].clear();
        testALUZero.doOperation(operation); //or
        assertEquals(0,testALUZero.result.getUnsigned());
        operation[3].clear();
        testALUZero.doOperation(operation); //and
        assertEquals(0,testALUZero.result.getUnsigned());
        operation[0].clear();
        operation[1].set();
        operation[2].set();
        operation[3].set();
        testALUZero.doOperation(operation); //multiply
        assertEquals(0,testALUZero.result.getUnsigned());
    }

    @Test
    public void testALULow(){ //assignment 2
        ALU testALULow = new ALU();
        testALULow.operand1.set(4);
        testALULow.operand2.set(6);
        Bit[] operation = new Bit[4];
        operation[0] = new Bit();
        operation[1] = new Bit();
        operation[2] = new Bit();
        operation[3] = new Bit();
        operation[0].set();
        operation[1].set();
        operation[2].set();
        operation[3].set();
        testALULow.doOperation(operation); //subtract
        assertEquals(-2,testALULow.result.getSigned());
        operation[3].clear();
        testALULow.doOperation(operation); //add
        assertEquals(10,testALULow.result.getUnsigned());
        operation[3].set();
        operation[2].clear();
        testALULow.doOperation(operation); //right shift
        assertEquals(0,testALULow.result.getUnsigned());
        operation[3].clear();
        testALULow.doOperation(operation); //left shift
        assertEquals(256,testALULow.result.getUnsigned());
        operation[3].set();
        operation[2].set();
        operation[1].clear();
        testALULow.doOperation(operation); //not
        assertEquals(4294967291L,testALULow.result.getUnsigned());
        operation[3].clear();
        testALULow.doOperation(operation); //xor
        assertEquals(2,testALULow.result.getUnsigned());
        operation[3].set();
        operation[2].clear();
        testALULow.doOperation(operation); //or
        assertEquals(6,testALULow.result.getUnsigned());
        operation[3].clear();
        testALULow.doOperation(operation); //and
        assertEquals(4,testALULow.result.getUnsigned());
        operation[0].clear();
        operation[1].set();
        operation[2].set();
        operation[3].set();
        testALULow.doOperation(operation); //multiply
        assertEquals(24,testALULow.result.getUnsigned());
    }

    @Test
    public void testALUHigh(){ //assignment 2
        ALU testALUHigh = new ALU();
        testALUHigh.operand1.set(46563);
        testALUHigh.operand2.set(38872);
        Bit[] operation = new Bit[4];
        operation[0] = new Bit();
        operation[1] = new Bit();
        operation[2] = new Bit();
        operation[3] = new Bit();
        operation[0].set();
        operation[1].set();
        operation[2].set();
        operation[3].set();
        testALUHigh.doOperation(operation); //subtract
        assertEquals(7691,testALUHigh.result.getUnsigned());
        operation[3].clear();
        testALUHigh.doOperation(operation); //add
        assertEquals(85435,testALUHigh.result.getUnsigned());
        operation[3].set();
        operation[2].clear();
        testALUHigh.doOperation(operation); //right shift
        assertEquals(0,testALUHigh.result.getUnsigned());
        operation[3].clear();
        testALUHigh.doOperation(operation); //left shift
        assertEquals(3808428032L,testALUHigh.result.getUnsigned());
        operation[3].set();
        operation[2].set();
        operation[1].clear();
        testALUHigh.doOperation(operation); //not
        assertEquals(4294920732L,testALUHigh.result.getUnsigned());
        operation[3].clear();
        testALUHigh.doOperation(operation); //xor
        assertEquals(8763,testALUHigh.result.getUnsigned());
        operation[3].set();
        operation[2].clear();
        testALUHigh.doOperation(operation); //or
        assertEquals(47099,testALUHigh.result.getUnsigned());
        operation[3].clear();
        testALUHigh.doOperation(operation); //and
        assertEquals(38336,testALUHigh.result.getUnsigned());
        operation[0].clear();
        operation[1].set();
        operation[2].set();
        operation[3].set();
        testALUHigh.doOperation(operation); //multiply
        assertEquals(1809996936,testALUHigh.result.getUnsigned());
    }

    @Test
    public void testALUNegative(){ //assignment 2
        ALU testALUNegative = new ALU();
        testALUNegative.operand1.set(-24);
        testALUNegative.operand2.set(-56);
        Bit[] operation = new Bit[4];
        operation[0] = new Bit();
        operation[1] = new Bit();
        operation[2] = new Bit();
        operation[3] = new Bit();
        operation[0].set();
        operation[1].set();
        operation[2].set();
        operation[3].set();
        testALUNegative.doOperation(operation); //subtract
        assertEquals(32,testALUNegative.result.getUnsigned());
        operation[3].clear();
        testALUNegative.doOperation(operation); //add
        assertEquals(-80,testALUNegative.result.getSigned());
        operation[3].set();
        operation[2].clear();
        testALUNegative.doOperation(operation); //rightshift
        assertEquals(-1,testALUNegative.result.getSigned());
        operation[3].clear();
        testALUNegative.doOperation(operation); //leftshift
        assertEquals(-6144,testALUNegative.result.getSigned());
        operation[3].set();
        operation[2].set();
        operation[1].clear();
        testALUNegative.doOperation(operation); //not
        assertEquals(23,testALUNegative.result.getUnsigned());
        operation[3].clear();
        testALUNegative.doOperation(operation); //xor
        assertEquals(32,testALUNegative.result.getUnsigned());
        operation[3].set();
        operation[2].clear();
        testALUNegative.doOperation(operation); //or
        assertEquals(-24,testALUNegative.result.getSigned());
        operation[3].clear();
        testALUNegative.doOperation(operation); //and
        assertEquals(-56,testALUNegative.result.getSigned());
        operation[0].clear();
        operation[1].set();
        operation[2].set();
        operation[3].set();
        testALUNegative.doOperation(operation); //multiply
        assertEquals(1344,testALUNegative.result.getUnsigned());
    }

    @Test
    public void testMemory(){ //assignment 3
        String[] data = new String[2];
        data[0] = "01010101010101010101010101010101";
        data[1] = "00000000000000000000000000000101";
        MainMemory.load(data);
        Word address = new Word();
        address.set(1);
        assertEquals(5,MainMemory.read(address).getUnsigned());
        address.set(0);
        assertEquals(1431655765,MainMemory.read(address).getUnsigned());
        data[0] = "00000000000000010101010101010101";
        data[1] = "00000000000000000000000000010001";
        MainMemory.load(data);
        address.set(1);
        assertEquals(17,MainMemory.read(address).getUnsigned());
        address.set(0);
        assertEquals(87381,MainMemory.read(address).getUnsigned());

        address.set(3);
        Word word = new Word();
        word.set(150);
        MainMemory.write(address,word);
        assertEquals(150,MainMemory.read(address).getUnsigned());
    }

    @Test
    public void testIncrement() { //assignment 3
        Word word = new Word();
        word.set(0);
        word.increment();
        assertEquals(1,word.getUnsigned());
        word.set(-12);
        word.increment();
        assertEquals(-11,word.getSigned());
        word.set(2);
        word.increment();
        assertEquals(3,word.getUnsigned());
        word.set(3349234);
        word.increment();
        assertEquals(3349235,word.getUnsigned());
    }

    @Test
    public void test1MATH(){ //assignment 4
        Processor processor = new Processor();
        String[] data = new String[5];
        data[0]="00000000000000010100000000100001"; // COPY R1 5
        data[1]="00000000000010000111100001000010"; // MATH ADD R1 R1 R2
        data[2]="00000000000000001011100001000011"; // MATH ADD R2 R2
        data[3]="00000000000100000111100001100010"; // MATH ADD R2 R1 R3
        data[4]="00000000000000000000000000000000"; // HALT
        MainMemory.load(data);
        processor.run();
        assertEquals(25L,processor.registers[3].getUnsigned());
    }

    @Test
    public void test2MATH(){ //assignment 4
        Processor processor = new Processor();
        String[] data = new String[5];
        data[0]="00000000000001010100000000100001"; //COPY R1 21
        data[1]="00000000000010000101110001000010"; //MATH MULTIPLY R1 R1 R2
        data[2]="00000000000100000111110001100010"; //MATH SUBTRACT R2 R1 R3
        data[3]="00000000000000000111110001100011"; //MATH SUBTRACT R3 R1
        data[4]="00000000000000000000000000000000"; //HALT
        MainMemory.load(data);
        processor.run();
        assertEquals(399,processor.registers[3].getUnsigned());
    }

    @Test
    public void testBranch(){ //assignment 5
        Processor processor = new Processor();
        String[] data = new String[10];
        data[0]="00000000000000010100000000100001"; //COPY R1 5
        data[1]="00000000000000010000000001000001"; //COPY R2 4
        data[2]="00000010000010001000010000000110"; //BRANCH UNEQUAL R1 R2 R0 2
        data[3]="00000000000000011000000000100001"; //COPY R1 6
        data[4]="00000000001000001000100000100111"; //BRANCH LESS R2 R1 4
        data[5]="00000000000000000100000000000101"; //JUMP R0 1
        data[6]="00000000000000110000000001000001"; //COPY R2 12
        data[7]="00000000000000010000000000100001"; //COPY R1 4
        data[8]="00000000000000000000000001000100"; //JUMP 2
        data[9]="00000000000000000000000000000000"; //HALT
        MainMemory.load(data);
        processor.run();
        assertEquals(6,processor.registers[1].getUnsigned());
    }

    @Test
    public void testCallandReturn(){ //assignment 5
        Processor processor = new Processor();
        String[] data = new String[9];
        data[0]="00000000000000010100000001100001"; //COPY R3 5
        data[1]="00000000000000010000000010000001"; //COPY R4 4
        data[2]="00000000000110001100010010001011"; //CALL UNEQUAL R4 R3 3
        data[3]="00000000000000000100000011000001"; //CALL R6 1
        data[4]="00000000000000011011100010100011"; //MATH R6 R5
        data[5]="00000000000000000000000100000100"; //JUMP 8
        data[6]="00000000000110010011100010100010"; //MATH R3 R4 R5
        data[7]="00000000000000000000000000010000"; //RETURN
        data[8]="00000000000000000000000000000000"; //HALT
        MainMemory.load(data);
        processor.run();
        assertEquals(10,processor.registers[5].getUnsigned());
    }

    @Test
    public void testPushPop(){ //assignment 5
        Processor processor = new Processor();
        String[] data = new String[8];
        data[0]="00000000000011001000000011100001"; //COPY R7 50
        data[1]="00000000000011000000000100000001"; //COPY R8 48
        data[2]="00000000000000011101110100001111"; //PUSH MULTIPLY R7 R8
        data[3]="00000000000010000000000100111011"; //PEEK R0 R9 0
        data[4]="00000000001100100011100100101101"; //PUSH ADD R9 200
        data[5]="00000000000000000000000101011001"; //POP R10
        data[6]="00000000000000000000000101111001"; //POP R11
        data[7]="00000000000000000000000000000000"; //HALT
        MainMemory.load(data);
        processor.run();
        assertEquals(2400,processor.registers[9].getUnsigned());
        assertEquals(2600,processor.registers[10].getUnsigned());
        assertEquals(2400,processor.registers[11].getUnsigned());
    }

    @Test
    public void testLoadStore(){ //assignment 5
        Processor processor = new Processor();
        String[] data = new String[8];
        data[0]="00000000000010001000000110000001"; //COPY R12 34
        data[1]="00000001000000110000000000010111"; //STORE R12 32
        data[2]="11111111111111110000000110100001"; //COPY R13 -4
        data[3]="00000001000000000000000111010011"; //LOAD R14 32
        data[4]="00000000011100110111110111100010"; //MATH SUBTRACT R14 R13 R15
        data[5]="00000000011000111100000000010110"; //STORE R15 R0 12
        data[6]="00000000011000000000000000110010"; //LOAD R0 R1 12
        data[7]="00000000000000000000000000000000"; //HALT
        MainMemory.load(data);
        processor.run();
        assertEquals(34,processor.registers[14].getUnsigned());
        assertEquals(38,processor.registers[1].getSigned());
    }

    @Test
    public void testInstructionCacheAndL2(){ //assignment 7
        String[] data = new String[2];
        data[0] = "01010101010101010101010101010101";
        data[1] = "00000000000000000000000000000101";
        MainMemory.load(data);
        Word address = new Word();
        address.set(0);
        assertEquals(1431655765,InstructionCache.read(address).getUnsigned());
        assertEquals(400,InstructionCache.clockCycleCost);
        address.set(1);
        assertEquals(5,InstructionCache.read(address).getUnsigned());
        assertEquals(10,InstructionCache.clockCycleCost);
        InstructionCache.clear();
        L2.clear();
        data[0] = "00000000000000010101010101010101";
        data[1] = "00000000000000000000000000010001";
        MainMemory.load(data);
        address.set(0);
        assertEquals(87381,InstructionCache.read(address).getUnsigned());
        assertEquals(400,InstructionCache.clockCycleCost);
        address.set(1);
        assertEquals(17,InstructionCache.read(address).getUnsigned());
        assertEquals(10,InstructionCache.clockCycleCost);
        InstructionCache.clear();
        L2.clear();
    }

    @Test
    public void testL2(){ //assignment 7
        String[] data = new String[2];
        Word address = new Word();
        data[0] = "00000000000000010101010101010101";
        data[1] = "00000000000000000000000000010001";
        MainMemory.load(data);
        address.set(0);
        assertEquals(87381,L2.read(address).getUnsigned());
        address.set(1);
        assertEquals(17,L2.read(address).getUnsigned());
        address.set(3);
        Word word = new Word();
        word.set(150);
        L2.write(address,word);
        assertEquals(150,L2.read(address).getUnsigned());
        assertEquals(150,MainMemory.read(address).getUnsigned());
        L2.clear();
    }

    @Test
    public void testProgram1() throws Exception{ //assignment 7
        Processor processor = new Processor();
        java.net.URL inputPath = JunitTests.class.getResource("TestProgram1.txt");
        String[] args = new String[]{inputPath.getPath().substring(3).replace("%20"," "),"Test1Output.txt"};
        Assembler.main(args);
        Path instructionPath = Paths.get("Test1Output.txt");
        String instructionString = new String(Files.readAllBytes(instructionPath));
        String[] instructions = instructionString.split("\n");
        MainMemory.load(instructions);
        System.out.print("TestProgram1: ");
        processor.run();
        assertEquals(700,processor.registers[5].getUnsigned());
    }

    @Test
    public void testProgram2() throws Exception{ //assignment 7
        Processor processor = new Processor();
        java.net.URL inputPath = JunitTests.class.getResource("TestProgram2.txt");
        String[] args = new String[]{inputPath.getPath().substring(3).replace("%20"," "),"Test2Output.txt"};
        Assembler.main(args);
        Path instructionPath = Paths.get("Test2Output.txt");
        String instructionString = new String(Files.readAllBytes(instructionPath));
        String[] instructions = instructionString.split("\n");
        MainMemory.load(instructions);
        System.out.print("TestProgram2: ");
        processor.run();
        assertEquals(700,processor.registers[5].getUnsigned());
    }

    @Test
    public void testProgram3() throws Exception{ //assignment 7
        Processor processor = new Processor();
        java.net.URL inputPath = JunitTests.class.getResource("TestProgram3.txt");
        String[] args = new String[]{inputPath.getPath().substring(3).replace("%20"," "),"Test3Output.txt"};
        Assembler.main(args);
        Path instructionPath = Paths.get("Test3Output.txt");
        String instructionString = new String(Files.readAllBytes(instructionPath));
        String[] instructions = instructionString.split("\n");
        MainMemory.load(instructions);
        System.out.print("TestProgram3: ");
        processor.run();
        assertEquals(700,processor.registers[5].getUnsigned());
    }
}
