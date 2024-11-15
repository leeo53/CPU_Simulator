public class Bit {
    private boolean value;

    public Bit(){
        value=false;
    }

    /**
     * change the value of bit to match value
     * @param value
     */
    public void set(boolean value){
        this.value=value;
    }

    /**
     * toggle bit true to false and false to true
     */
    public void toggle(){
        if(value){
            value=false;
        }else
            value=true;
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
        if(value==false){
            if(other.getValue()==false){
                newBit.clear();
            }
        }
        return newBit;
    }

    public Bit xor(Bit other){
        Bit newBit = new Bit();
        newBit.clear();
        if(value){
            if(other.getValue()==false)
                newBit.set();
        }
        if(value==false){
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
