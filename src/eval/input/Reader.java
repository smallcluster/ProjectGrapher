package eval.input;

public abstract class Reader {
    protected int pos;

    public Reader(){
        pos = 0;
    }
    public int getPos(){
        return pos;
    }
    public void setPos(int pos){
        this.pos = pos;
    }
    public int readChar(){
        if(endOfStream())
            return -1;
        return readFromStream();
    }
    protected abstract boolean endOfStream();
    protected abstract int readFromStream();
}
