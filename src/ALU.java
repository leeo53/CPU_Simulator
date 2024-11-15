public class ALU {
    public Word operand1;
    public Word operand2;
    public Word result;

    public ALU(){
        result = new Word();
        operand1 = new Word();
        operand2 = new Word();
    }

    /**
     *  Perform Operation on op1 and op2 changing result to the result
      */
    public void doOperation(Bit[] operation){
        if(operation[0].getValue()){ //1
            if(operation[1].getValue()){ //11
                if(operation[2].getValue()){ //111
                    if(operation[3].getValue()){ //1111
                        //subtract
                        Word one = new Word();
                        Bit trueBit = new Bit();
                        trueBit.set();
                        one.setBit(0,trueBit);
                        result.copy(add2(operand1,add2(operand2.not(),one)));
                    }else{ //1110
                        //add
                        result.copy(add2(operand1,operand2));
                    }
                }else{
                    if(operation[3].getValue()){ //1101
                        //rightShift
                        int amount=0;
                        for(int i=0; i<5;i++){
                            if(operand2.getBit(i).getValue())
                                amount+= (int) Math.pow(2,i);
                        }
                        result.copy(operand1.rightShift(amount));
                    }else { //1100
                        //leftShift
                        int amount=0;
                        for(int i=0; i<5;i++){
                            if(operand2.getBit(i).getValue())
                                amount+= (int) Math.pow(2,i);
                        }
                        result.copy(operand1.leftShift(amount));
                    }
                }
            }else{ //10
                if(operation[2].getValue()){ //101
                    if(operation[3].getValue()){ //1011
                        //not
                        result.copy(operand1.not());
                    }else{ //1010
                        //xor
                        result.copy(operand1.xor(operand2));
                    }
                }else{ //100
                    if(operation[3].getValue()){ //1001
                        //or
                        result.copy(operand1.or(operand2));
                    }else { //1000
                        //and
                        result.copy(operand1.and(operand2));
                    }
                }
            }
        }else if(operation[0].not().and(operation[1]).and(operation[2]).and(operation[3]).getValue()){ //0111
            //multiply
            Word[] words = new Word[32];
            for(int i=0; i<32;i++){
                if(operand2.getBit(i).getValue()) {
                    words[i] = operand1.leftShift(i);
                }else{
                    words[i] = new Word();
                }
            }
            result.copy(add2(add4(add4(words[0],words[1],words[2],words[3]),add4(words[4],words[5],words[6],words[7]),add4(words[8],
                    words[9],words[10],words[11]),add4(words[12],words[13],words[14],words[15])),add4(add4(words[16],
                    words[17],words[18],words[19]),add4(words[20],words[21],words[22],words[23]),add4(words[24], words[25],
                    words[26],words[27]),add4(words[28],words[29],words[30],words[31]))));
        }
    }

    //self-explanatory
    private Word add2(Word word1, Word word2){
        Bit carryIn = new Bit();
        Word sum = new Word();
        for(int i=0;i<32;i++){
            //S = W1 XOR W2 XOR Cin
            sum.setBit(i,word1.getBit(i).xor(word2.getBit(i)).xor(carryIn));
            //cOut = W1 AND W2 OR ((W1 XOR W2) AND Cin)
            carryIn.set(word1.getBit(i).and(word2.getBit(i)).or(word1.getBit(i).xor(word2.getBit(i)).and(carryIn)).getValue());
        }
        return sum;
    }

    private Word add4(Word word1, Word word2, Word word3, Word word4){
        Bit carryIn1 = new Bit();
        Bit carryIn2 = new Bit();
        Bit carryIn3 = new Bit();
        Word sum1 = new Word();
        Word sum2 = new Word();
        Word sum = new Word();
        for(int i=0;i<32;i++){
            //S1 = W1 XOR W2 XOR cIn1
            sum1.setBit(i,word1.getBit(i).xor(word2.getBit(i)).xor(carryIn1));
            //cOut1 = W1 AND W2 OR ((W1 XOR W2) AND CIn1)
            carryIn1.set(word1.getBit(i).and(word2.getBit(i)).or(word1.getBit(i).xor(word2.getBit(i)).and(carryIn1)).getValue());
            //S2 = S1 XOR W3 XOR cIn2
            sum2.setBit(i,sum1.getBit(i).xor(word3.getBit(i)).xor(carryIn2));
            //cOut2 = S1 AND W3 OR ((S1 XOR W3) AND CIn2)
            carryIn2.set(sum1.getBit(i).and(word3.getBit(i)).or(sum1.getBit(i).xor(word3.getBit(i)).and(carryIn2)).getValue());
            //S = S2 XOR W4 XOR cIn3
            sum.setBit(i,sum2.getBit(i).xor(word4.getBit(i)).xor(carryIn3));
            //cOut3 = S2 AND W4 OR ((S2 XOR W4) AND CIn3)
            carryIn3.set(sum2.getBit(i).and(word4.getBit(i)).or(sum2.getBit(i).xor(word4.getBit(i)).and(carryIn3)).getValue());
        }
        return sum;
    }
}
