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
        boolean ret = false;
        if(!identityExists(ident.getAddress())) {
            // Good, add new ident
            addIdent(ident);
            ret = true;
        } else{
            // Ok, update ident
            removeIdentity(ident.getAddress());
            addIdent(ident);
            ret = false;
        }
        return ret;
    }

    // Removes the identity from identities and identityQueues by address
    public void removeIdentity(String address) {
        identities.removeIf(i -> i.getAddress().equals(address));
        identityAddressQueues.entrySet().removeIf(iq -> iq.getKey().equals(address));
    }

    public void sendMessage(String address, String msg) throws UnknownIdentityException, IOException {
        FsMQMessage mqMsg = new FsMQMessage(self, msg);
        String serializedStr = mqMsg.serialize();
        getQueueForIdentity(address).add(serializedStr);
    }

    public void clearOwnQueue() {
        getQueueForIdentity(self.getAddress()).clear();
    }

    public String receiveMessage() throws UnknownIdentityException, IOException, ClassNotFoundException, TimeoutException {
        return receiveMessage(0);
    }

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
        List<FsMQIdentity> matches = identities.stream().filter(i -> i.getAddress().equals(address)).collect(Collectors.toList());
        if (matches.size() > 1) {
            throw new IllegalStateException("Internal Error: Identity address not unique: " + address);
        }
        if (matches.size() == 1) {
            ret = true;
        }
        return ret;
    }


    // Return null if not existing
    public FsMQIdentity getIdentityForAddress(String address) throws UnknownIdentityException, IllegalStateException {
        FsMQIdentity ret = null;
        if (identityExists(address)) {
            ret  = identities.stream().filter(i -> i.getAddress().equals(address)).collect(Collectors.toList()).get(0);
        }
        return ret;
    }

    public List<FsMQIdentity> getIdentities() {
        return new ArrayList<FsMQIdentity>(identities);
    }

    public List<String> getIdentityAddresses() {
        List <String> ret = new ArrayList<>();
        for(FsMQIdentity i : identities) {
            ret.add(i.getAddress());
        }
        return ret;
    }

    // undefined behaviour if already existing
    private void addIdent(FsMQIdentity ident) {
        identities.add(ident);
        createQueueForIdent(ident);
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


