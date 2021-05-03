package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager;

import foundation.pEp.jniadapter.test.utils.transport.fsmsgqueue.FsMsgQueue;
import foundation.pEp.pitytest.utils.TestUtils;

import java.io.*;
import java.util.Base64;

public class FsMQManager {
    public FsMQIdentities identities = null;

    public FsMQManager(FsMQIdentity self) {
        identities = new FsMQIdentities(self);
    }

    public void clearOwnQueue() {
        identities.getQueueForIdentity(identities.self.getAddress()).clear();
    }

    public void sendMessage(String address, String msg) throws UnknownIdentityException, NullPointerException {
        if (address != null) {
            if (msg != null) {
                FsMQMessageInternal mqMsg = new FsMQMessageInternal(identities.self, msg);
                try {
                    String serializedStr = mqMsg.serialize();
                    identities.getQueueForIdentity(address).add(serializedStr);
                } catch (IOException e) {
                    throw new RuntimeException(e.toString());
                }
            } else {
                throw new NullPointerException("msg cant be null");
            }
        } else {
            throw new NullPointerException("address cant be null");
        }
    }

    // Non blocking read
    // Returns null if no messages available
    public FsMQMessage receiveMessage() {
        return receiveMessage(0);
    }

    // Blocking read
    // Returns null if no messages available within timeout
    public FsMQMessage receiveMessage(int timeoutSec) {
        FsMQMessage ret = null;
        FsMsgQueue onwQueue = identities.getQueueForIdentity(identities.self.getAddress());
        FsMQMessageInternal mqMsg = null;
        int pollInterval = 100;
        int pollRepeats = timeoutSec * 1000 / pollInterval;
        int pollCounter = 0;

        while (onwQueue.isEmpty()) {
            TestUtils.sleep(pollInterval);
            pollCounter++;
            if (pollCounter >= pollRepeats) {
                return ret;
            }
        }
        String serializedMsg = onwQueue.remove();
        try {
            mqMsg = FsMQMessageInternal.deserialize(serializedMsg);
            ret = mqMsg.toFsMQMessage();
        } catch (Exception e) {
            throw  new RuntimeException(e.getMessage());
        }
        return ret;
    }
}

class FsMQMessageInternal extends FsMQMessage implements java.io.Serializable {
    FsMQHandshakeHeader header = null;

    FsMQMessageInternal(FsMQIdentity from, String msg) throws IllegalStateException {
        super(from, msg);
    }

    public FsMQHandshakeHeader getHeader() {
        return header;
    }

    public void setHeader(FsMQHandshakeHeader header) {
        this.header = header;
    }

    public FsMQMessage toFsMQMessage() throws NullPointerException {
        FsMQMessage ret = new FsMQMessage(this.getFrom(), this.getMsg());
        return ret;
    }


    public static FsMQMessageInternal deserialize(String serializedMsg) throws IOException, ClassNotFoundException {
        FsMQMessageInternal ret = null;
        byte[] data = Base64.getDecoder().decode(serializedMsg);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object obj = ois.readObject();
        ois.close();
        if (!(obj instanceof FsMQMessageInternal)) {
            throw new ClassNotFoundException("Invalid serialized string");
        } else {
            ret = (FsMQMessageInternal) obj;
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
        return ret;
    }

    class FsMQHandshakeHeader implements java.io.Serializable {
        String operation = null;
    }
}


