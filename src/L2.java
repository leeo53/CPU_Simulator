public class L2 {
    private static Word[] words = new Word[36];
    private static int recentGroup;
    public static int clockCycleCost; //to store cost of after each operation not to be added to

    /**
     * get data stored at address if it's not there read it from main memory and fill cache group
     * @param address of data
     * @return data at address
     */
    public static Word read(Word address){
        if(words[0]==null){
            for(int i=0;i<36;i++){
                words[i]=new Word();
            }
            clear();
            recentGroup=3;
        }
        clockCycleCost=20;
        Word newWord = new Word();
        if(address.equals(words[0])) {
            newWord.copy(words[4]);
        }else if(address.equals(words[1])){
            newWord.copy(words[12]);
        }else if(address.equals(words[2])){
            newWord.copy(words[20]);
        }else if(address.equals(words[3])){
            newWord.copy(words[28]);
        }else{
            Bit found = new Bit();
            found.clear();
            Word tempWord = new Word();
            for(int i=0; i<4;i++){
                tempWord.copy(words[i]);
                int x = 5;
                if(tempWord.getBit(31).not().getValue()) {
                    if(i==1)
                        x=13;
                    else if(i==2)
                        x=21;
                    else if(i==3)
                        x=29;
                    for (int j = x; j < x+7; j++) {
                        tempWord.increment();
                        if (tempWord.equals(address)) {
                            newWord.copy(words[j]);
                            found.set();
                            break;
                        }
                    }
                    if (found.getValue()) break;
                }
            }
            if(found.not().getValue()){
                if(recentGroup>=3){
                    recentGroup=-1;
                }
                recentGroup++;
                int x=4;
                if(recentGroup==1)
                    x=12;
                else if(recentGroup==2)
                    x=20;
                else if(recentGroup==3)
                    x=28;
                words[recentGroup].copy(address);
                newWord.copy(MainMemory.read(address));
                for(int i=x;i<x+8;i++){
                    words[i].copy(MainMemory.read(address));
                    address.increment();
                }
                address.copy(words[recentGroup]);
            }else clockCycleCost=20;
        }
        return newWord;
    }

    /**
     * uses write-through to write data to cache and memory
     * @param address
     * @param value
     */
    public static void write(Word address,Word value){
        if(recentGroup>=3){
            recentGroup=-1;
        }
        recentGroup++;
        int x=4;
        if(recentGroup==1)
            x=12;
        else if(recentGroup==2)
            x=20;
        else if(recentGroup==3)
            x=28;
        words[recentGroup].copy(address);
        words[x].copy(value);
        MainMemory.write(address,value);
    }

    /**
     * clears the cache
     */
    public static void clear(){
        Bit bit = new Bit();
        bit.set();
        words[0].setBit(31,bit);
        words[1].setBit(31,bit);
        words[2].setBit(31,bit);
        words[3].setBit(31,bit);
    }

}
