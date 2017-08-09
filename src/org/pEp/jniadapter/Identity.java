package org.pEp.jniadapter;

import java.io.Serializable;

public class Identity implements Serializable{
    public String address;
    public String fpr;
    public String user_id;
    public String username;
    public CommType comm_type;
    public String lang;
    public int flags;

    public Identity() {
        comm_type = CommType.PEP_ct_unknown;
    }

    public Identity(_Identity i) {
        address = AbstractEngine.toUTF16(i.address);
        fpr = AbstractEngine.toUTF16(i.fpr);
        user_id = AbstractEngine.toUTF16(i.user_id);
        username = AbstractEngine.toUTF16(i.username);
        comm_type = CommType.Management.tag.get(i.comm_type);
        lang = AbstractEngine.toUTF16(i.lang);
        flags = i.flags;
    }

    @Override
    public String toString() {
        return address + "::" + username + "\n" +
                user_id + "::" + fpr;
    }
}

