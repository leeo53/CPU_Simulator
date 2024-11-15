public class Bit {
    private boolean value;

    public Bit(){
        value=false;
    }

    /**
     * change the value of bit to match value
     */
    public void set(boolean value){
        this.value=value;
    }

    /**
     * toggle bit true to false and false to true
     */
    public void toggle(){
        value = !value;
    }

    /**
     * change value to true
     */
    public void set(){
        value=true;
    }

    /**
     * change value to false
     */
    public void clear(){
        value=false;
    }

    public boolean getValue(){
        return value;
    }

    public Bit and(Bit other){
        Bit newBit = new Bit();
        newBit.clear();
        if(value) {
            if (other.getValue()) {
                newBit.set();
            }
        }
        return newBit;
    }

    public Bit or(Bit other){
        Bit newBit = new Bit();
        newBit.set();
        if(!value){
            if(!other.getValue()){
                newBit.clear();
            }
        }
        return newBit;
    }

    public Bit xor(Bit other){
        Bit newBit = new Bit();
        newBit.clear();
        if(value){
            if(!other.getValue())
                newBit.set();
        }
        if(!value){
            if(other.getValue())
                newBit.set();
        }
        return newBit;
    }

    public Bit not(){
        Bit newBit = new Bit();
        newBit.set();
        if(value){
            newBit.clear();
        }
        return newBit;
    }

    @Override
    public String toString() {
        if(value){
            return "t";
        }
        return "f";
    }
}
