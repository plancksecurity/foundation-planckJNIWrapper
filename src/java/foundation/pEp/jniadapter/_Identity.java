package foundation.pEp.jniadapter;

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
        address = Utils.toUTF8(i.address);
        fpr = Utils.toUTF8(i.fpr);
        user_id = Utils.toUTF8(i.user_id);
        username = Utils.toUTF8(i.username);
        comm_type = i.comm_type.value;
        lang = Utils.toUTF8(i.lang);
        me = i.me;
        flags = i.flags;
    }
}

