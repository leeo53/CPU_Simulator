import com.sun.org.apache.xpath.internal.operations.Neg;

public class InstructionCache {
    private static Word[] words = new Word[9];
    public static int clockCycleCost; //to store cost of after each operation not to be added to

    /**
     * returns the data with that address in the instruction cache if it's not there read in from L2 and fill
     * cache
     * @param address of data to find
     * @return data with address
     */
    public static Word read(Word address){
        if(words[0]==null){
            for(int i=0;i<9;i++){
                words[i]=new Word();
            }
            clear();
        }
        clockCycleCost=10;
        Word newWord = new Word();
        if(address.equals(words[0])) {
            newWord.copy(words[1]);
        }else{
            Word tempWord = new Word();
            tempWord.copy(words[0]);
            Bit found = new Bit();
            found.clear();
            for(int i=2;i<9;i++){
                tempWord.increment();
                if(tempWord.equals(address)) {
                    newWord.copy(words[i]);
                    found.set();
                    break;
                }
            }
            if(found.not().getValue()){
                words[0].copy(address);
                newWord.copy(L2.read(address));
                for(int i=1;i<9;i++){
                    words[i].copy(L2.read(address));
                    address.increment();
                }
                address.copy(words[0]);
                clockCycleCost=50+350;
            }
        }
        return newWord;
    }

    /**
     * clears the cache
     */
    public static void clear(){
        Bit bit = new Bit();
        bit.set();
        words[0].setBit(31,bit);
    }
}
