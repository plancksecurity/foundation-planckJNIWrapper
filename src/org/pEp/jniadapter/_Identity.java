package org.pEp.jniadapter;

public class _Identity {
    public byte[] address;
    public byte[] fpr;
    public byte[] user_id;
    public byte[] username;
    int comm_type;
    public byte[] lang;
    public boolean me;

    public _Identity() {
        this.me = false;
    }

    public _Identity(boolean me) {
        this.me = me;
    }

    public _Identity(Identity i) {
        address = AbstractEngine.toUTF8(i.address);
        fpr = AbstractEngine.toUTF8(i.address);
        user_id = AbstractEngine.toUTF8(i.address);
        username = AbstractEngine.toUTF8(i.address);
        comm_type = i.comm_type.value;
        lang = AbstractEngine.toUTF8(i.address);
        me = i.me;
    }
}

