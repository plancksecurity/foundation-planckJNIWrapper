package org.pEp.jniadapter;

public class Identity {
    public String address;
    public String fpr;
    public String user_id;
    public String username;
    CommType comm_type;
    public String lang;
    public boolean me;

    public Identity() {
        this.me = false;
    }

    public Identity(boolean me) {
        this.me = me;
    }

    public Identity(_Identity i) {
        address = AbstractEngine.toUTF16(i.address);
        fpr = AbstractEngine.toUTF16(i.address);
        user_id = AbstractEngine.toUTF16(i.address);
        username = AbstractEngine.toUTF16(i.address);
        comm_type = CommType.Management.tag.get(i.comm_type);
        lang = AbstractEngine.toUTF16(i.address);
        me = i.me;
    }
}

