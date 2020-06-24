package foundation.pEp.jniadapter.test.templateAliceBob;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.Pair;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Vector;

public class TransportMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fromAddress = null;
    private List<String> toAddresses = new ArrayList<String>();
    private Vector<Blob> attachments = new Vector<>();
    private String shortMessage = null;
    private String longMessage = null;

    TransportMessage() {
    }

    TransportMessage(Message msg) {
        fromAddress = msg.getFrom().address;
        for (Identity i : msg.getTo()) {
            toAddresses.add(i.address);
        }
        for (Blob b : msg.getAttachments()) {
            attachments.add(b);
        }
        shortMessage = msg.getShortmsg();
        longMessage = msg.getLongmsg();
    }

    // Deep Copy
    TransportMessage(TransportMessage msg) {
        fromAddress = msg.fromAddress;
        toAddresses = new ArrayList<>(msg.toAddresses);
        attachments = new Vector<>(msg.attachments);
        shortMessage = msg.shortMessage;
        longMessage = msg.longMessage;
    }


    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public List<String> getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(List<String> toAddresses) {
        this.toAddresses = toAddresses;
    }

    public Vector<Blob> getAttachments() {
        return attachments;
    }

    public void setAttachments(Vector<Blob> attachments) {
        this.attachments = attachments;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    public String getLongMessage() {
        return longMessage;
    }

    public void setLongMessage(String longMessage) {
        this.longMessage = longMessage;
    }

    public Message toMessage() {
        Message ret = new Message();

        // from
        Identity from = new Identity();
        from.address = fromAddress;
        ret.setFrom(from);

        // to
        Vector<Identity> toIdents = new Vector<>();
        for (String addr : toAddresses) {
            Identity i = new Identity();
            i.address = addr;
            toIdents.add(i);
        }
        ret.setTo(toIdents);

        // attachments
        ret.setAttachments(new Vector<>(attachments));

        // shortMessage
        if (shortMessage != null) {
            ret.setShortmsg(shortMessage);
        }

        // longMessage
        if (longMessage != null) {
            ret.setLongmsg(longMessage);
        }
        return ret;
    }

    public String toString() {
        String ret = "";
        ArrayList<Pair<String, String>> kvs = new ArrayList<>();
        String key = "";
        String value = "";
        boolean full = false;

        key = "from";
        value = fromAddress;
        kvs.add(new Pair<>(key, value));

        key = "to";
        value = toAddresses.toString();
        kvs.add(new Pair<>(key, value));

        key = "shortMessage";
        value = shortMessage;
        kvs.add(new Pair<>(key, value));

        key = "longMessage";
        value = longMessage;
        kvs.add(new Pair<>(key, value));

        key = "getAttachments";
        value = AdapterTestUtils.blobListToString(attachments, full) + "\n";
        kvs.add(new Pair<>(key, value));

        if (!full) {
            kvs = AdapterTestUtils.clipStrings(kvs, 200, "clipped...");
        }

        ret = AdapterTestUtils.stringPairListToString(kvs);
        ret = ret.trim();

        return ret;
    }


    public static TransportMessage deserialize(String serializedMsg) throws IOException, ClassNotFoundException {
        TransportMessage ret = null;
        byte[] data = Base64.getDecoder().decode(serializedMsg);
//        byte[] data = serializedMsg.getBytes();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object obj = ois.readObject();
        ois.close();
        if (!(obj instanceof TransportMessage)) {
            throw new ClassNotFoundException("Invalid serialized string");
        } else {
            ret = (TransportMessage) obj;
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
