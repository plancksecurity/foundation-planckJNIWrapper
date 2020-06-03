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

    public FsMQManager(FsMQIdentity self) throws NullPointerException {
        if (self != null) {
            this.self = self;
            addOrUpdateIdentity(self);
        } else {
            throw new NullPointerException("self cant be null");
        }
    }

    // Identity address must be unique
    // Returns
    // - true for added
    // - false for updated or own ident (which cant be updated)
    public boolean addOrUpdateIdentity(FsMQIdentity ident) throws NullPointerException {
        boolean ret = false;
        if (ident != null) {
            if (addIdent(ident)) {
                // Good, add new ident
                ret = true;
            } else {
                // Ok, update ident
                updateIdentity(ident);
                ret = false;
            }
        } else {
            throw new NullPointerException("ident cant be null");
        }
        return ret;
    }

    // cant update own identity
    // True - Success
    // False - ident not existing or own identity
    public boolean updateIdentity(FsMQIdentity ident) throws NullPointerException {
        boolean ret = false;
        if (ident != null) {
            if (!isOwnIdentity(ident.getAddress()) && identityExists(ident.getAddress())) {
                removeIdentity(ident.getAddress());
                addIdent(ident);
                ret = true;
            }
        } else {
            throw new NullPointerException("ident cant be null");
        }
        return ret;
    }

    // Removes the identity from identities and identityQueues by address
    public boolean removeIdentity(String address) throws NullPointerException {
        boolean ret = false;
        if (address != null) {
            if (identityExists(address) && !isOwnIdentity(address)) {
                identities.removeIf(i -> i.getAddress().equals(address));
                identityAddressQueues.entrySet().removeIf(iq -> iq.getKey().equals(address));
                ret = true;
            }
        } else {
            throw new NullPointerException("address cant be null");
        }
        return ret;
    }

    // cant fail haha
    public void removeAllIdentities() {
        for (FsMQIdentity i : getIdentities()) {
            removeIdentity(i.getAddress());
        }
    }

    // Returns number of identities added
    public int addIdentities(List<FsMQIdentity> idents) throws NullPointerException {
        int ret = 0;
        if (idents != null) {
            for (FsMQIdentity i : idents) {
                if (addOrUpdateIdentity(i)) {
                    ret++;
                }
            }
        } else {
            throw new NullPointerException("idents cant be null");
        }
        return ret;
    }

    public boolean isOwnIdentity(String address) throws NullPointerException {
        boolean ret = false;
        if (address != null) {
            ret = self.getAddress() == address;
        } else {
            throw new NullPointerException("address cant be null");
        }
        return ret;
    }

    public void sendMessage(String address, String msg) throws UnknownIdentityException, IOException, NullPointerException {
        if (address != null) {
            if (msg != null) {
                FsMQMessage mqMsg = new FsMQMessage(self, msg);
                String serializedStr = mqMsg.serialize();
                getQueueForIdentity(address).add(serializedStr);
            } else {
                throw new NullPointerException("msg cant be null");
            }
        } else {
            throw new NullPointerException("address cant be null");
        }
    }

    public void clearOwnQueue() {
        getQueueForIdentity(self.getAddress()).clear();
    }

    public String receiveMessage() throws UnknownIdentityException, IOException, ClassNotFoundException, TimeoutException {
        return receiveMessage(0);
    }

    // Blocks until timeout
    public String receiveMessage(int timeoutSec) throws UnknownIdentityException, IOException, ClassNotFoundException, TimeoutException {
        String ret = null;
        FsMsgQueue onwQueue = getQueueForIdentity(self.getAddress());
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

    // True if existing
    // False if not
    // Exception on not unique
    public boolean identityExists(String address) {
        boolean ret = false;
        if (address != null) {
            List<FsMQIdentity> matches = identities.stream().filter(i -> i.getAddress().equals(address)).collect(Collectors.toList());
            if (matches.size() > 1) {
                throw new IllegalStateException("Internal Error: Identity address not unique: " + address);
            }
            if (matches.size() == 1) {
                ret = true;
            }
        } else {
            throw new NullPointerException("address cant be null");
        }
        return ret;
    }


//    // Return null if not existing
//    public FsMQIdentity getIdentityForAddress(String address) throws UnknownIdentityException, IllegalStateException {
//        FsMQIdentity ret = null;
//        if (identityExists(address)) {
//            ret  = identities.stream().filter(i -> i.getAddress().equals(address)).collect(Collectors.toList()).get(0);
//        }
//        return ret;
//    }

    public List<FsMQIdentity> getIdentities() {
        return new ArrayList<FsMQIdentity>(identities);
    }

    public List<String> getIdentityAddresses() {
        List<String> ret = new ArrayList<>();
        for (FsMQIdentity i : identities) {
            ret.add(i.getAddress());
        }
        return ret;
    }

    // True  - success
    // False - already existing
    private boolean addIdent(FsMQIdentity ident) {
        boolean ret = false;
        if (!identityExists(ident.getAddress())) {
            identities.add(ident);
            createQueueForIdent(ident);
            ret = true;
        }
        return ret;
    }

    private void createQueueForIdent(FsMQIdentity ident) {
        FsMsgQueue q = new FsMsgQueue(ident.getqDir());
        identityAddressQueues.put(ident.getAddress(), q);
    }

    private FsMsgQueue getQueueForIdentity(String address) throws UnknownIdentityException {
        FsMsgQueue ret = null;
        ret = identityAddressQueues.get(address);
        if (ret == null) {
            throw new UnknownIdentityException("Unknown identity address: " + address);
        }
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


