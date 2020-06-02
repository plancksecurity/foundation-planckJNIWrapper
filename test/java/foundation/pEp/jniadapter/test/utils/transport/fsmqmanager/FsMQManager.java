package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager;

import foundation.pEp.jniadapter.test.framework.TestUtils;
import foundation.pEp.jniadapter.test.utils.transport.fsmsgqueue.FsMsgQueue;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static foundation.pEp.jniadapter.test.framework.TestLogger.log;

public class FsMQManager {
    private FsMQIdentity self = null;

    private List<FsMQIdentity> identities = new ArrayList<>();
    private Map<String, FsMsgQueue> identityAddressQueues = new HashMap<String, FsMsgQueue>();

    private static String SYNMSG = "SYN";
    private static String SYNACKMSG = "SYNACK";
    private static String ACKMSG = "ACK";

    public FsMQManager(FsMQIdentity self) {
        this.self = self;
        addOrUpdateIdentity(self);
    }

    // Identity address must be unique
    // Returns
    // - true for added
    // - false for updated
    public boolean addOrUpdateIdentity(FsMQIdentity ident) {
        try {
            getIdentityForAddress(ident.getAddress());
        } catch (UnknownIdentityException e) {
            // Good, add new ident
            addIdent(ident);
            return true;
        }
        // Ok, update ident
        removeIdent(ident);
        addIdent(ident);
        return false;
    }

    public void sendMsgToIdentity(FsMQIdentity ident, String msg) throws UnknownIdentityException, IOException {
        FsMQMessage mqMsg = new FsMQMessage(self, msg);
        String serializedStr = mqMsg.serialize();
        getQueueForIdentity(ident).add(serializedStr);
    }

    public void clearOwnQueue() {
        getQueueForIdentity(self).clear();
    }

    public String waitForMsg() throws UnknownIdentityException, IOException, ClassNotFoundException, TimeoutException {
        return waitForMsg(0);
    }

    public String waitForMsg(int timeoutSec) throws UnknownIdentityException, IOException, ClassNotFoundException, TimeoutException {
        String ret = null;
        FsMsgQueue onwQueue = getQueueForIdentity(self);
        FsMQMessage mqMsg = null;
        int pollInterval = 100;
        int pollRepeats = timeoutSec * 1000 / pollInterval;
        int pollCounter = 0;
        do {
            while (onwQueue.isEmpty()) {
                TestUtils.sleep(100);
                pollCounter++;
                if (pollCounter >= pollRepeats) {
                    throw new TimeoutException("");
                }
            }
            String serializedMsg = onwQueue.remove();
            mqMsg = FsMQMessage.deserialize(serializedMsg);
        } while (doHandshakeProtocol(mqMsg));
        ret = mqMsg.msg;
        return ret;
    }

    // undefined behaviour if already existing
    private void addIdent(FsMQIdentity ident) {
        identities.add(ident);
        createQueueForIdent(ident);
    }

    // Removes the identity from identities and identityQueues by address
    private void removeIdent(FsMQIdentity ident) {
        identities.removeIf(i -> i.getAddress().equals(ident.getAddress()));
        identityAddressQueues.entrySet().removeIf(iq -> iq.getKey().equals(ident.getAddress()));
    }

    private void createQueueForIdent(FsMQIdentity ident) {
        FsMsgQueue q = new FsMsgQueue(ident.getqDir());
        identityAddressQueues.put(ident.getAddress(), q);
    }

    private FsMsgQueue getQueueForIdentity(FsMQIdentity ident) throws UnknownIdentityException {
        FsMsgQueue ret = null;
        ret = identityAddressQueues.get(ident.getAddress());
        if (ret == null) {
            throw new UnknownIdentityException("Unknown identity address: " + ident.getAddress());
        }
        return ret;
    }

    public FsMQIdentity getIdentityForAddress(String address) throws UnknownIdentityException, IllegalStateException {
        FsMQIdentity ret = null;
        List<FsMQIdentity> matches = identities.stream().filter(i -> i.getAddress().equals(address)).collect(Collectors.toList());
        if (matches.size() <= 0) {
            throw new UnknownIdentityException("No identity with address:" + address);
        }
        if (matches.size() > 1) {
            throw new IllegalStateException("Identity address not unique: " + address);
        }
        ret = matches.get(0);
        return ret;
    }


//    public void handshake(FsMQIdentity ident) {
//        String msg = "";
//        sendSYN(ident);
//        while (msg != SYNACKMSG + " " + ident.getAddress()) {
//            log("Waiting for SYNACK from " + ident.getAddress());
//            msg = waitForMsg();
//        }
//        sendACK(ident);
//    }

    private boolean doHandshakeProtocol(FsMQMessage msg) {
        boolean ret = false;
//
//        if(msg.matches(SYNMSG)) {
//
//        }
//        if(msg.matches(SYNACK)) {
//
//        }

        return ret;
    }

//    public void sendSYN(FsMQIdentity ident) {
//        String msg = SYNMSG + " " + self.getAddress();
//        log("Sending SYN to: " + ident.getAddress());
//        sendMsgToIdentity(ident, msg);
//    }
//
//    public void sendACK(FsMQIdentity ident) {
//        String msg = ACKMSG + " " + self.getAddress();
//        log("Sending ACK to: " + ident.getAddress());
//        sendMsgToIdentity(ident, msg);
//    }

}

class FsMQMessage implements java.io.Serializable {
    FsMQIdentity from = null;
    FsMQHandshakeHeader header = null;
    String msg = null;

    FsMQMessage(FsMQIdentity from, String msg) throws IllegalStateException {
        if (from == null || msg == null) {
            throw new IllegalStateException("from and msg cant be null");
        }
        this.from = from;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public FsMQHandshakeHeader getHeader() {
        return header;
    }

    public void setHeader(FsMQHandshakeHeader header) {
        this.header = header;
    }

    public static FsMQMessage deserialize(String serializedMsg) throws IOException, ClassNotFoundException {
        FsMQMessage ret = null;
        byte[] data = Base64.getDecoder().decode(serializedMsg);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object obj = ois.readObject();
        ois.close();
        if (!(obj instanceof FsMQMessage)) {
            throw new ClassNotFoundException("Unvalid serialized string");
        } else {
            ret = (FsMQMessage) obj;
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


