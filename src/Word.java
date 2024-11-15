

public class Word {
    private Bit[] word = new Bit[32];

    public Word(){
        for(int i=0;i< word.length;i++){
            word[i] = new Bit();
        }
    }

    /**
     * get a copy of the bit in the word at position i
     * @param i position for bit, bit 0 is the rightmost bit
     * @return ith bit
     */
    public Bit getBit(int i){
        Bit newBit = new Bit();
        newBit.set(word[31-i].getValue());
        return newBit;
    }

    /**
     * set the bit at postition i to value of provided bit
     * @param i position for bit, bit 0 is the rightmost bit
     * @param value provided bit
     */
    public void setBit(int i, Bit value){
        word[31-i].set(value.getValue());
    }

    public Word and(Word other){
        Word newWord= new Word();
        for(int i = 0; i < word.length;i++){
            newWord.setBit(i,getBit(i).and(other.getBit(i)));
        }
        return newWord;
    }

    public Word or(Word other){
        Word newWord= new Word();
        for(int i = 0; i < word.length;i++){
            newWord.setBit(i,getBit(i).or(other.getBit(i)));
        }
        return newWord;
    }

    public Word xor(Word other){
        Word newWord= new Word();
        for(int i = 0; i < word.length;i++){
            newWord.setBit(i,getBit(i).xor(other.getBit(i)));
        }
        return newWord;
    }

    public Word not(){
        Word newWord= new Word();
        for(int i = 0; i < word.length;i++){
            newWord.setBit(i,getBit(i).not());
        }
        return newWord;
    }

    public Word rightShift(int amount){
        Word newWord= new Word();
        for(int i = 0; i < word.length;i++){
            if(i>=word.length-amount){
                newWord.setBit(i,getBit(31));
            }else
                newWord.setBit(i,getBit(i+amount));
        }
        return newWord;
    }

    public Word leftShift(int amount){
        Word newWord= new Word();
        for(int i = 0; i < word.length-amount;i++){
            newWord.setBit(i+amount,getBit(i));
        }
        return newWord;
    }

    public String toString(){
        String string = "";
        for(int i = 0; i < word.length;i++){
            if(i< 31)
                string += word[i].toString()+",";
            else
                string += word[i].toString();
        }
        return string;
    }

    public long getUnsigned(){
        long value=0;
        for(int i=0;i<word.length;i++){
            if(word[i].getValue()){
                value+=Math.pow(2,(31-i));
            }
        }
        return value;
    }

    public int getSigned(){
        int value=0;
        Word negated;
        if(getBit(31).not().getValue()) {
            for(int i=0;i<word.length;i++){
                if(getBit(i).getValue()){
                    value+=Math.pow(2,i);
                }
            }
        }else {
            negated = not();
            boolean carry = true;
            for(int i=0; i<32; i++){
                Bit newBit = new Bit();
                if(carry){
                    if(negated.getBit(i).getValue()){
                        newBit.clear();
                        negated.setBit(i,newBit);
                    }else{
                        newBit.set();
                        negated.setBit(i, newBit);
                        carry=false;
                    }
                }else{
                   break;
                }
            }
            value= (int) (negated.getUnsigned()*-1);
        }
        return value;
    }

    public void copy(Word other){
        for(int i=0;i<word.length;i++){
            setBit(i,other.getBit(i));
        }
    }

    public void set(int value) {
        boolean negative = false;
        if (value < 0) {
            value = Math.abs(value);
            negative = true;
        }
        int i = 0;
        while (31 - i >= 0) {
            Bit newBit = new Bit();
            if (value % 2 != 0) {
                newBit.set();
                setBit(i, newBit);
            } else {
                newBit.clear();
                word[31 - i] = newBit;
            }
            value = value / 2;
            i++;
        }
        if (negative) {
            copy(not());
            increment();
        }
    }

    /**
     * add 1 to the word
     */
    public void increment(){
        Bit carry = new Bit();
        carry.set();
        Bit newBit = new Bit();
        int i=0;
        while(carry.getValue()){
            newBit.set(carry.xor(getBit(i)).getValue());
            carry.set(carry.and(getBit(i)).getValue());
            setBit(i,newBit);
            i++;
        }

    }


    /**
     * subtract 1 from word
     */
    public void decrement(){
        Bit carry = new Bit();
        Bit newBit = new Bit();
        for(int i=0;i< word.length;i++){
            newBit.set(getBit(i).xor(carry).not().getValue());
            carry.set(carry.or(getBit(i)).getValue());
            setBit(i,newBit);
        }
    }


    public boolean equals(Word other){
        for(int i =0; i<word.length;i++){
            if(getBit(i).and(other.getBit(i)).or(getBit(i).not().and(other.getBit(i).not())).not().getValue())
                return false;
        }
        return true;
    }
}
