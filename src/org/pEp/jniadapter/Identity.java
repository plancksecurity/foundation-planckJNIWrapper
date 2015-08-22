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
        comm_type = CommType.PEP_ct_unknown;
    }

    public Identity(boolean me) {
        this.me = me;
        comm_type = CommType.PEP_ct_unknown;
    }

    public Identity(_Identity i) {
        if (i.address != null)
            address = AbstractEngine.toUTF16(i.address);
        if (i.fpr != null)
            fpr = AbstractEngine.toUTF16(i.fpr);
        if (i.user_id != null)
            user_id = AbstractEngine.toUTF16(i.user_id);
        if (i.username != null)
            username = AbstractEngine.toUTF16(i.username);
        comm_type = CommType.Management.tag.get(i.comm_type);
        if (i.lang != null)
            lang = AbstractEngine.toUTF16(i.lang);
        me = i.me;
    }
}

