package foundation.pEp.jniadapter;

public class Pair<F, S> {
    public F first;
    public S second;

    public Pair() { }

    public Pair(F f, S s) {
        first = f;
        second = s;
    }

    public String toString() {
        String ret="";
        ret += "'" + first.toString() + "' : '" + second.toString() + "'";
        return ret;
    }
}

