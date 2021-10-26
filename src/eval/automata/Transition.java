package eval.automata;

class Transition {
    private State nextState;
    private String exp;
    private boolean match;

    Transition(State dest, String exp){
        this.nextState = dest;
        this.exp = exp;
        match = true;
    }

    Transition(State dest, String exp, boolean match){
        this.nextState = dest;
        this.exp = exp;
        this.match = match;
    }

    Transition(State dest){
        this.nextState = dest;
        this.exp = "[:any:]";
        this.match = true;
    }

    public State getNextState(){return nextState;}


    public boolean eval(int c){
        String tmpexp = exp;

        if(tmpexp.contains("[:any:]"))
            return match;

        if(tmpexp.contains("[:letter:]")){
            tmpexp = tmpexp.replace("[:letter:]", "");
            if( (c <= 'z' && c >= 'a') || (c <= 'Z' && c >= 'A'))
                return match;
        }
        if(tmpexp.contains("[:digit:]")){
            tmpexp = tmpexp.replace("[:digit:]", "");
            if(c <= '9' && c>='0')
                return match;
        }
        if(tmpexp.contains("[:EOL:]")){
            tmpexp = tmpexp.replace("[:EOL:]", "");
            if(c == -1)
                return match;
        }
        for (int i = 0; i < tmpexp.length(); i++)
            if (c == tmpexp.charAt(i))
                return match;
        return !match;
    }
}
