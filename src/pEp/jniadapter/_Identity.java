package pEp.jniadapter;

public class _Identity {
    public byte[] address;
    public byte[] fpr;
    public byte[] user_id;
    public byte[] username;
    public int comm_type;
    public byte[] lang;
    public boolean me;
    public int flags;

    public _Identity() {
        this.me = false;
    }

    public _Identity(boolean me) {
        this.me = me;
    }

    public _Identity(Identity i) {
        address = AbstractEngine.toUTF8(i.address);
        fpr = AbstractEngine.toUTF8(i.fpr);
        user_id = AbstractEngine.toUTF8(i.user_id);
        username = AbstractEngine.toUTF8(i.username);
        comm_type = i.comm_type.value;
        lang = AbstractEngine.toUTF8(i.lang);
        me = i.me;
        flags = i.flags;
    }
}

