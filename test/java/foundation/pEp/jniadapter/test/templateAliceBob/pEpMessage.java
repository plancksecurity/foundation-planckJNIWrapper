package foundation.pEp.jniadapter.test.templateAliceBob;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Vector;

public class pEpMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    String fromAddress = null;
    List<String> toAddresses = new ArrayList<String>();
    List<Blob> attachments = new ArrayList<Blob>();
    String shortMessage = null;
    String longMessage = null;

    pEpMessage(Message msg) {
        fromAddress = msg.getFrom().address;
        for(Identity i : msg.getTo() ) {
            toAddresses.add(i.address);
        }
        for(Blob b : msg.getAttachments()) {
            attachments.add(b);
        }
        shortMessage = msg.getShortmsg();
        longMessage = msg.getLongmsg();
    }

    public Message toMessage() {
        Message ret = new Message();
        Identity from = new Identity();
        from.address = fromAddress;
        ret.setFrom(from);
        Vector<Identity> toIdents = new Vector<>();
        for( String addr : toAddresses) {
            Identity i = new Identity();
            i.address = addr;
            toIdents.add(i);
        }
        ret.setTo(toIdents);
        Vector<Blob> atts = new Vector<>();
        for(Blob b : attachments) {
            atts.add(b);
        }
        ret.setAttachments(atts);
        ret.setShortmsg(shortMessage);
        ret.setLongmsg(longMessage);
        return ret;
    }

    public static pEpMessage deserialize(String serializedMsg) throws IOException, ClassNotFoundException {
        pEpMessage ret = null;
        byte[] data = Base64.getDecoder().decode(serializedMsg);
//        byte[] data = serializedMsg.getBytes();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object obj = ois.readObject();
        ois.close();
        if (!(obj instanceof pEpMessage)) {
            throw new ClassNotFoundException("Invalid serialized string");
        } else {
            ret = (pEpMessage) obj;
        }
        return ret;
    }

    public String serialize() throws IOException {
        String ret = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();
        ret = Base64.getEncoder().encodeToString(baos.toByteArray());
//        ret = baos.toString();
        return ret;
    }
}
