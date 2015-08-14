package org.pEp.jniadapter;

public class Pair<F, S> {
    public F first;
    public S second;

    Pair<F, S>() { }

    Pair<F, S>(F f, S s) {
        first = f;
        second = s;
    }
}

