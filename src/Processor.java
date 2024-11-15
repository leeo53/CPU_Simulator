import java.util.Arrays;

public class Processor {
    private Word PC;
    private Word SP;
    public Word[] registers;
    private Word currentInstruction;
    private Bit haltedBit;
    private int currentClockCycle;

    private Word sourceRegister1;
    private Word sourceRegister2;
    private Word destinationRegister;
    private Word immediateValue;
    private Word executionResult;
    private Word registerMask;
    private ALU alu;



    public Processor(){
        PC=new Word(); //new word is automatically zero
        SP=new Word();
        SP.set(1023);
        registers= new Word[32];
        for(int i=0;i<32;i++){
            registers[i]=new Word();
        }
        currentClockCycle=0;
        alu = new ALU();
        sourceRegister1=new Word();
        sourceRegister2=new Word();
        destinationRegister=new Word();
        immediateValue=new Word();
        haltedBit = new Bit();
        registerMask= new Word();
        setBits(0,4,registerMask);
    }

    public void run(){
        while(true){
            fetch();
            decode();
            execute();
            if(haltedBit.getValue()) {
                InstructionCache.clear();
                L2.clear();
                System.out.println(currentClockCycle);
                break;
            }
            store();
        }
    }

    /**
     * load the current instuction from main memory and increment PC
     */
    private void fetch(){
        currentInstruction = InstructionCache.read(PC);
        currentClockCycle+=InstructionCache.clockCycleCost;
        PC.increment();
    }

    /**
     * find the instruction format and get needed registers and immediate value
     */
    private void decode(){
        clearStorageWords();
        if(currentInstruction.getBit(1).and(currentInstruction.getBit(0)).getValue()){ //11 - 2R
            if(getRegisterIndex(currentInstruction.rightShift(14).and(registerMask))!=0){
                sourceRegister1.copy(registers[getRegisterIndex(currentInstruction.rightShift(14).and(registerMask))]);
            }
            if(getRegisterIndex(currentInstruction.rightShift(5).and(registerMask))!=0){
                destinationRegister.copy(registers[getRegisterIndex(currentInstruction.rightShift(5).and(registerMask))]);
            }
            immediateValue.copy(currentInstruction.rightShift(19));
        } else if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()){ //10 - 3R
            if(getRegisterIndex(currentInstruction.rightShift(19).and(registerMask))!=0){
                sourceRegister1.copy(registers[getRegisterIndex(currentInstruction.rightShift(19).and(registerMask))]);
            }
            if(getRegisterIndex(currentInstruction.rightShift(14).and(registerMask))!=0){
                sourceRegister2.copy(registers[getRegisterIndex(currentInstruction.rightShift(14).and(registerMask))]);
            }
            if(getRegisterIndex(currentInstruction.rightShift(5).and(registerMask))!=0){
                destinationRegister.copy(registers[getRegisterIndex(currentInstruction.rightShift(5).and(registerMask))]);
            }
            immediateValue.copy(currentInstruction.rightShift(24));
        } else if(currentInstruction.getBit(1).not().and(currentInstruction.getBit(0)).getValue()){ //01 - 1R - Dest only
            if(getRegisterIndex(currentInstruction.rightShift(5).and(registerMask))!=0){
                destinationRegister.copy(registers[getRegisterIndex(currentInstruction.rightShift(5).and(registerMask))]);
            }
            immediateValue.copy(currentInstruction.rightShift(14));
        } else { //00 - 0R - No R
            immediateValue.copy(currentInstruction.rightShift(5));
        }
    }

    /**
     * Get the opcode from current instruction and execute operation
     */
    private void execute(){
        Bit[] add = new Bit[4];
        Bit[] subtract = new Bit[4];
        for(int i=0;i<add.length;i++){
            add[i] = new Bit();
            subtract[i] = new Bit();
            subtract[i].set();
        }
        add[0].set();
        add[1].set();
        add[2].set();
        if(currentInstruction.getBit(2).not().and(currentInstruction.getBit(3).not()).and(currentInstruction.getBit(4).not()).getValue()){
            //Math
            executionResult=new Word();
            if(currentInstruction.getBit(1).and(currentInstruction.getBit(0)).getValue()){ //11 - 2R
                if(getFunction(currentInstruction)[0].not().getValue())
                    currentClockCycle+=10;
                else
                    currentClockCycle+=2;
                alu.operand1.copy(destinationRegister);
                alu.operand2.copy(sourceRegister1);
                alu.doOperation(getFunction(currentInstruction));
                executionResult.copy(alu.result);
            } else if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()){ //10 - 3R
                if(getFunction(currentInstruction)[0].not().getValue())
                    currentClockCycle+=10;
                else
                    currentClockCycle+=2;
                alu.operand1.copy(sourceRegister1);
                alu.operand2.copy(sourceRegister2);
                alu.doOperation(getFunction(currentInstruction));
                executionResult.copy(alu.result);
            } else if(currentInstruction.getBit(1).not().and(currentInstruction.getBit(0)).getValue()){ //01 - 1R - Dest only
                executionResult.copy(immediateValue);
            } else { //00 - 0R - No R
                haltedBit.set();
            }
        }else if(currentInstruction.getBit(2).and(currentInstruction.getBit(3).not()).and(currentInstruction.getBit(4).not()).getValue()){
            //Branch
            executionResult=null;
            if(currentInstruction.getBit(1).and(currentInstruction.getBit(0)).or( //11 - 2R
                    currentInstruction.getBit(1).and(currentInstruction.getBit(0).not())).getValue()){ //OR 10 - 3R
                Bit[] BOP = getFunction(currentInstruction);
                alu.operand1.copy(sourceRegister1);
                alu.operand2.copy(destinationRegister);
                if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()) // 10 - 3R
                    alu.operand2.copy(sourceRegister2);
                alu.doOperation(subtract);
                currentClockCycle+=2;
                int compare=0;
                if(alu.result.getBit(31).not().getValue()) {
                    for (int i = 0; i <= 31; i++) {
                        if (alu.result.getBit(i).getValue()) {
                            compare = 1;
                            break;
                        }
                    }
                }else{
                    compare=-1;
                }
                currentClockCycle+=2;
                if(BOP[0].not().and(BOP[1].not()).and(BOP[2].not()).and(BOP[3].not()).getValue()){
                    //equals
                    if(compare==0){
                            alu.operand1.copy(PC);
                            alu.operand2.copy(immediateValue);
                            alu.doOperation(add);
                            PC.copy(alu.result);
                    }
                }else if(BOP[0].not().and(BOP[1].not()).and(BOP[2].not()).and(BOP[3]).getValue()){
                    //not equal
                    if(compare!=0){
                        alu.operand1.copy(PC);
                        alu.operand2.copy(immediateValue);
                        alu.doOperation(add);
                        PC.copy(alu.result);
                    }
                }else if(BOP[0].not().and(BOP[1].not()).and(BOP[2]).and(BOP[3].not()).getValue()){
                    //less than
                    if(compare==-1){
                        alu.operand1.copy(PC);
                        alu.operand2.copy(immediateValue);
                        alu.doOperation(add);
                        PC.copy(alu.result);
                    }
                }else if(BOP[0].not().and(BOP[1].not()).and(BOP[2]).and(BOP[3]).getValue()){
                    //greater than or equal
                    if(compare==1 || compare==0){
                        alu.operand1.copy(PC);
                        alu.operand2.copy(immediateValue);
                        alu.doOperation(add);
                        PC.copy(alu.result);
                    }
                }else if(BOP[0].not().and(BOP[1]).and(BOP[2].not()).and(BOP[3].not()).getValue()){
                    //greater than
                    if(compare==1){
                        alu.operand1.copy(PC);
                        alu.operand2.copy(immediateValue);
                        alu.doOperation(add);
                        PC.copy(alu.result);
                    }
                }else{
                    //less than or equal
                    if(compare==-1 || compare==0){
                        alu.operand1.copy(PC);
                        alu.operand2.copy(immediateValue);
                        alu.doOperation(add);
                        PC.copy(alu.result);
                    }
                }
            } else if(currentInstruction.getBit(1).not().and(currentInstruction.getBit(0)).getValue()){ //01 - 1R - Dest only
                alu.operand1.copy(immediateValue);
                alu.operand2.copy(PC);
                alu.doOperation(add);
                currentClockCycle+=2;
                PC.copy(alu.result);
            } else { //00 - 0R - No R
                PC.copy(immediateValue);
            }
        }else if(currentInstruction.getBit(2).not().and(currentInstruction.getBit(3)).and(currentInstruction.getBit(4).not()).getValue()){
            //Call
            executionResult=null;
            currentClockCycle+=50;
            if(currentInstruction.getBit(1).and(currentInstruction.getBit(0)).or( //11 - 2R
                    currentInstruction.getBit(1).and(currentInstruction.getBit(0).not())).getValue()){ //OR 10 - 3R
                Bit[] BOP = getFunction(currentInstruction);
                alu.operand1.copy(sourceRegister1);
                alu.operand2.copy(destinationRegister);
                if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()) // 10 - 3R
                    alu.operand2.copy(sourceRegister2);
                alu.doOperation(subtract);
                currentClockCycle+=2;
                int compare=0;
                if(alu.result.getBit(31).not().getValue()) {
                    for (int i = 0; i <= 31; i++) {
                        if (alu.result.getBit(i).getValue()) {
                            compare = 1;
                            break;
                        }
                    }
                }else{
                    compare=-1;
                }
                currentClockCycle+=2;
                if(BOP[0].not().and(BOP[1].not()).and(BOP[2].not()).and(BOP[3].not()).getValue()){
                    //equals
                    if(compare==0){
                        L2.write(SP,PC);
                        SP.decrement();
                        alu.operand1.copy(PC);
                        if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()) // 10 - 3R
                            alu.operand1.copy(destinationRegister);
                        alu.operand2.copy(immediateValue);
                        alu.doOperation(add);
                        PC.copy(alu.result);
                    }
                }else if(BOP[0].not().and(BOP[1].not()).and(BOP[2].not()).and(BOP[3]).getValue()){
                    //not equal
                    if(compare!=0){
                        L2.write(SP,PC);
                        SP.decrement();
                        alu.operand1.copy(PC);
                        if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()) // 10 - 3R
                            alu.operand1.copy(destinationRegister);
                        alu.operand2.copy(immediateValue);
                        alu.doOperation(add);
                        PC.copy(alu.result);
                    }
                }else if(BOP[0].not().and(BOP[1].not()).and(BOP[2]).and(BOP[3].not()).getValue()){
                    //less than
                    if(compare==-1){
                        L2.write(SP,PC);
                        SP.decrement();
                        alu.operand1.copy(PC);
                        if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()) // 10 - 3R
                            alu.operand1.copy(destinationRegister);
                        alu.operand2.copy(immediateValue);
                        alu.doOperation(add);
                        PC.copy(alu.result);
                    }
                }else if(BOP[0].not().and(BOP[1].not()).and(BOP[2]).and(BOP[3]).getValue()){
                    //greater than or equal
                    if(compare==1 || compare==0){
                        L2.write(SP,PC);
                        SP.decrement();
                        alu.operand1.copy(PC);
                        if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()) // 10 - 3R
                            alu.operand1.copy(destinationRegister);
                        alu.operand2.copy(immediateValue);
                        alu.doOperation(add);
                        PC.copy(alu.result);
                    }
                }else if(BOP[0].not().and(BOP[1]).and(BOP[2].not()).and(BOP[3].not()).getValue()){
                    //greater than
                    if(compare==1){
                        L2.write(SP,PC);
                        SP.decrement();
                        alu.operand1.copy(PC);
                        if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()) // 10 - 3R
                            alu.operand1.copy(destinationRegister);
                        alu.operand2.copy(immediateValue);
                        alu.doOperation(add);
                        PC.copy(alu.result);
                    }
                }else{
                    //less than or equal
                    if(compare==-1 || compare==0){
                        L2.write(SP,PC);
                        SP.decrement();
                        alu.operand1.copy(PC);
                        if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()) // 10 - 3R
                            alu.operand1.copy(destinationRegister);
                        alu.operand2.copy(immediateValue);
                        alu.doOperation(add);
                        PC.copy(alu.result);
                    }
                }
            } else if(currentInstruction.getBit(1).not().and(currentInstruction.getBit(0)).getValue()){ //01 - 1R - Dest only
                L2.write(SP,PC);
                SP.decrement();
                alu.operand1.copy(immediateValue);
                alu.operand2.copy(destinationRegister);
                alu.doOperation(add);
                PC.copy(alu.result);
            } else { //00 - 0R - No R
                L2.write(SP,PC);
                SP.decrement();
                PC.copy(immediateValue);
            }
        }else if(currentInstruction.getBit(2).and(currentInstruction.getBit(3)).and(currentInstruction.getBit(4).not()).getValue()){
            //Push
            executionResult=null;
            currentClockCycle+=50;
            Bit[] function = getFunction(currentInstruction);
            if(function[0].not().and(function[1]).and(function[2]).and(function[3]).getValue())
                currentClockCycle+=10;
            else
                currentClockCycle+=2;
            if(currentInstruction.getBit(1).and(currentInstruction.getBit(0)).getValue()){ //11 - 2R
                alu.operand1.copy(destinationRegister);
                alu.operand2.copy(sourceRegister1);
                alu.doOperation(getFunction(currentInstruction));
                L2.write(SP,alu.result);
                SP.decrement();
            } else if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()){ //10 - 3R
                alu.operand1.copy(sourceRegister1);
                alu.operand2.copy(sourceRegister2);
                alu.doOperation(getFunction(currentInstruction));
                L2.write(SP,alu.result);
                SP.decrement();
            } else if(currentInstruction.getBit(1).not().and(currentInstruction.getBit(0)).getValue()){ //01 - 1R - Dest only
                alu.operand1.copy(destinationRegister);
                alu.operand2.copy(immediateValue);
                alu.doOperation(getFunction(currentInstruction));
                L2.write(SP,alu.result);
                SP.decrement();
            }
        }else if(currentInstruction.getBit(2).not().and(currentInstruction.getBit(3).not()).and(currentInstruction.getBit(4)).getValue()){
            //Load
            currentClockCycle+=50;
            executionResult = new Word();
            if(currentInstruction.getBit(1).and(currentInstruction.getBit(0)).getValue()){ //11 - 2R
                alu.operand1.copy(sourceRegister1);
                alu.operand2.copy(immediateValue);
                alu.doOperation(add);
                currentClockCycle+=2;
                executionResult.copy(L2.read(alu.result));
            } else if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()){ //10 - 3R
                alu.operand1.copy(sourceRegister1);
                alu.operand2.copy(sourceRegister2);
                alu.doOperation(add);
                currentClockCycle+=2;
                executionResult.copy(L2.read(alu.result));
            } else if(currentInstruction.getBit(1).not().and(currentInstruction.getBit(0)).getValue()){ //01 - 1R - Dest only
                alu.operand1.copy(destinationRegister);
                alu.operand2.copy(immediateValue);
                alu.doOperation(add);
                currentClockCycle+=2;
                executionResult.copy(L2.read(alu.result));
            } else { //00 - 0R - No R
                SP.increment();
                PC.copy(L2.read(SP));
            }
        }else if(currentInstruction.getBit(2).and(currentInstruction.getBit(3).not()).and(currentInstruction.getBit(4)).getValue()){
            //Store
            executionResult=null;
            currentClockCycle+=50;
            if(currentInstruction.getBit(1).and(currentInstruction.getBit(0)).getValue()){ //11 - 2R
                alu.operand1.copy(destinationRegister);
                alu.operand2.copy(immediateValue);
                alu.doOperation(add);
                currentClockCycle+=2;
                L2.write(alu.result,sourceRegister1);
            } else if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()){ //10 - 3R
                alu.operand1.copy(destinationRegister);
                alu.operand2.copy(sourceRegister1);
                alu.doOperation(add);
                currentClockCycle+=2;
                L2.write(alu.result,sourceRegister2);
            } else if(currentInstruction.getBit(1).not().and(currentInstruction.getBit(0)).getValue()){ //01 - 1R - Dest only
                L2.write(destinationRegister,immediateValue);
            }
        }else{
            //Pop
            executionResult = new Word();
            currentClockCycle+=50;
            if(currentInstruction.getBit(1).and(currentInstruction.getBit(0)).getValue()){ //11 - 2R
                alu.operand1.copy(sourceRegister1);
                alu.operand2.copy(immediateValue);
                alu.doOperation(add);
                alu.operand1.copy(SP);
                alu.operand2.copy(alu.result);
                alu.doOperation(add);
                currentClockCycle+=4;
                executionResult.copy(L2.read(alu.result));
            } else if(currentInstruction.getBit(1).and(currentInstruction.getBit(0).not()).getValue()){ //10 - 3R
                alu.operand1.copy(sourceRegister1);
                alu.operand2.copy(sourceRegister2);
                alu.doOperation(add);
                alu.operand1.copy(SP);
                alu.operand2.copy(alu.result);
                alu.doOperation(add);
                currentClockCycle+=4;
                executionResult.copy(L2.read(alu.result));
            } else if(currentInstruction.getBit(1).not().and(currentInstruction.getBit(0)).getValue()){ //01 - 1R - Dest only
                SP.increment();
                executionResult.copy(L2.read(SP));
            }
        }
    }

    /**
     * store the execution result in the destination register
     */
    private void store(){
        Word destinationRegisterMask = new Word();
        setBits(5,9,destinationRegisterMask);
        if(getRegisterIndex(currentInstruction.rightShift(5).and(registerMask))!=0 && executionResult != null){
            registers[getRegisterIndex(currentInstruction.and(destinationRegisterMask).rightShift(5))].copy(executionResult);
        }
    }

    /**
     * returns a bit array representing the function in the current instruction;
     * @param word the current instruction
     * @return a bit array representing the function.
     */
    private Bit[] getFunction(Word word){
        Word temp = new Word();
        temp.copy(word.rightShift(10));
        Bit[] function = new Bit[4];
        for(int i=0;i<4;i++){
            function[3-i]=temp.getBit(i);
        }
        return function;
    }

    /**
     * get register index from bits in word
     * @param word with value for index
     * @return index value from word bits
     */
    private int getRegisterIndex(Word word){
        if(word.getBit(0).not().and(word.getBit(1).not()).and(word.getBit(2).not()).and(word.getBit(3).not()).and(word.getBit(4).not()).getValue()){
            return 0;
        }else if(word.getBit(0).and(word.getBit(1).not()).and(word.getBit(2).not()).and(word.getBit(3).not()).and(word.getBit(4).not()).getValue()){
            return 1;
        }else if(word.getBit(0).not().and(word.getBit(1)).and(word.getBit(2).not()).and(word.getBit(3).not()).and(word.getBit(4).not()).getValue()){
            return 2;
        }else if(word.getBit(0).and(word.getBit(1)).and(word.getBit(2).not()).and(word.getBit(3).not()).and(word.getBit(4).not()).getValue()){
            return 3;
        }else if(word.getBit(0).not().and(word.getBit(1).not()).and(word.getBit(2)).and(word.getBit(3).not()).and(word.getBit(4).not()).getValue()){
            return 4;
        }else if(word.getBit(0).and(word.getBit(1).not()).and(word.getBit(2)).and(word.getBit(3).not()).and(word.getBit(4).not()).getValue()){
            return 5;
        }else if(word.getBit(0).not().and(word.getBit(1)).and(word.getBit(2)).and(word.getBit(3).not()).and(word.getBit(4).not()).getValue()){
            return 6;
        }else if(word.getBit(0).and(word.getBit(1)).and(word.getBit(2)).and(word.getBit(3).not()).and(word.getBit(4).not()).getValue()){
            return 7;
        }else if(word.getBit(0).not().and(word.getBit(1).not()).and(word.getBit(2).not()).and(word.getBit(3)).and(word.getBit(4).not()).getValue()){
            return 8;
        }else if(word.getBit(0).and(word.getBit(1).not()).and(word.getBit(2).not()).and(word.getBit(3)).and(word.getBit(4).not()).getValue()){
            return 9;
        }else if(word.getBit(0).not().and(word.getBit(1)).and(word.getBit(2).not()).and(word.getBit(3)).and(word.getBit(4).not()).getValue()){
            return 10;
        }else if(word.getBit(0).and(word.getBit(1)).and(word.getBit(2).not()).and(word.getBit(3)).and(word.getBit(4).not()).getValue()){
            return 11;
        }else if(word.getBit(0).not().and(word.getBit(1).not()).and(word.getBit(2)).and(word.getBit(3)).and(word.getBit(4).not()).getValue()){
            return 12;
        }else if(word.getBit(0).and(word.getBit(1).not()).and(word.getBit(2)).and(word.getBit(3)).and(word.getBit(4).not()).getValue()){
            return 13;
        }else if(word.getBit(0).not().and(word.getBit(1)).and(word.getBit(2)).and(word.getBit(3)).and(word.getBit(4).not()).getValue()){
            return 14;
        }else if(word.getBit(0).and(word.getBit(1)).and(word.getBit(2)).and(word.getBit(3)).and(word.getBit(4).not()).getValue()){
            return 15;
        }else if(word.getBit(0).not().and(word.getBit(1).not()).and(word.getBit(2).not()).and(word.getBit(3).not()).and(word.getBit(4)).getValue()){
            return 16;
        }else if(word.getBit(0).and(word.getBit(1).not()).and(word.getBit(2).not()).and(word.getBit(3).not()).and(word.getBit(4)).getValue()){
            return 17;
        }else if(word.getBit(0).not().and(word.getBit(1)).and(word.getBit(2).not()).and(word.getBit(3).not()).and(word.getBit(4)).getValue()){
            return 18;
        }else if(word.getBit(0).and(word.getBit(1)).and(word.getBit(2).not()).and(word.getBit(3).not()).and(word.getBit(4)).getValue()){
            return 19;
        }else if(word.getBit(0).not().and(word.getBit(1).not()).and(word.getBit(2)).and(word.getBit(3).not()).and(word.getBit(4)).getValue()){
            return 20;
        }else if(word.getBit(0).and(word.getBit(1).not()).and(word.getBit(2)).and(word.getBit(3).not()).and(word.getBit(4)).getValue()){
            return 21;
        }else if(word.getBit(0).not().and(word.getBit(1)).and(word.getBit(2)).and(word.getBit(3).not()).and(word.getBit(4)).getValue()){
            return 22;
        }else if(word.getBit(0).and(word.getBit(1)).and(word.getBit(2)).and(word.getBit(3).not()).and(word.getBit(4)).getValue()){
            return 23;
        }else if(word.getBit(0).not().and(word.getBit(1).not()).and(word.getBit(2).not()).and(word.getBit(3)).and(word.getBit(4)).getValue()){
            return 24;
        }else if(word.getBit(0).and(word.getBit(1).not()).and(word.getBit(2).not()).and(word.getBit(3)).and(word.getBit(4)).getValue()){
            return 25;
        }else if(word.getBit(0).not().and(word.getBit(1)).and(word.getBit(2).not()).and(word.getBit(3)).and(word.getBit(4)).getValue()){
            return 26;
        }else if(word.getBit(0).and(word.getBit(1)).and(word.getBit(2).not()).and(word.getBit(3)).and(word.getBit(4)).getValue()){
            return 27;
        }else if(word.getBit(0).not().and(word.getBit(1).not()).and(word.getBit(2)).and(word.getBit(3)).and(word.getBit(4)).getValue()){
            return 28;
        }else if(word.getBit(0).and(word.getBit(1).not()).and(word.getBit(2)).and(word.getBit(3)).and(word.getBit(4)).getValue()){
            return 29;
        }else if(word.getBit(0).not().and(word.getBit(1)).and(word.getBit(2)).and(word.getBit(3)).and(word.getBit(4)).getValue()){
            return 30;
        }else{
            return 31;
        }
    }

    private void setBits(int startIndex,int endIndex, Word word){
        Bit trueBit = new Bit();
        trueBit.set();
        for(int i=startIndex; i <= endIndex; i++){
            word.setBit(i,trueBit);
        }
    }

    private void clearStorageWords(){
        Word emptyWord = new Word();
        sourceRegister1.copy(emptyWord);
        sourceRegister2.copy(emptyWord);
        immediateValue.copy(emptyWord);
        destinationRegister.copy(emptyWord);
    }

}
