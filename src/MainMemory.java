public class MainMemory {
    private static Word[] words = new Word[1024];

    /**
     * get data stored at address
     * @param address where data is stored
     * @return data
     */
    public static Word read(Word address){
        Word newWord = new Word();
        if(!(words[(int) address.getUnsigned()]==null))
            newWord.copy(words[(int) address.getUnsigned()]);
        return newWord;
    }

    /**
     * write value to address
     * @param address
     * @param value
     */
    public static void write(Word address, Word value){
        if(words[(int) address.getUnsigned()]==null)
            words[(int) address.getUnsigned()] = new Word();
        words[(int) address.getUnsigned()].copy(value);
    }

    /**
     * write data to memory from a list (array)
     * @param data array of strings of 1s and 0s
     */
    public static void load(String[] data){
        int i=0;
        Bit trueBit = new Bit();
        trueBit.set();
        Bit falseBit = new Bit();
        for(String string : data){
            for(int j=0;j<string.length();j++){
                if(words[i]==null)
                    words[i] = new Word();
                if(string.charAt(j)=='1')
                    words[i].setBit(31-j,trueBit);
                else
                    words[i].setBit(31-j,falseBit);
            }
            i++;
        }
    }
}
