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
        if (i.address != null)
            address = AbstractEngine.toUTF8(i.address);
        if (i.fpr != null)
            fpr = AbstractEngine.toUTF8(i.fpr);
        if (i.user_id != null)
            user_id = AbstractEngine.toUTF8(i.user_id);
        if (i.username != null)
            username = AbstractEngine.toUTF8(i.username);
        comm_type = i.comm_type.value;
        if (i.lang != null)
            lang = AbstractEngine.toUTF8(i.lang);
        me = i.me;
    }
}

