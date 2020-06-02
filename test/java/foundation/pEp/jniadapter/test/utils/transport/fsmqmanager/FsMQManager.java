package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager;
import foundation.pEp.jniadapter.test.framework.TestUtils;
import foundation.pEp.jniadapter.test.utils.transport.fsmsgqueue.FsMsgQueue;

import java.util.HashMap;
import java.util.Map;

import static foundation.pEp.jniadapter.test.framework.TestLogger.log;

public class FsMQManager {
    private String ownAddress;
    private FsMsgQueue ownQueue;
    private Map<String, FsMsgQueue> peerQueues = new HashMap<>();

    private static String SIGNALONLINEMSG = "SIGONLINE";

    public FsMQManager(String ownAddr, String ownQueueDir) {
        ownAddress = ownAddr;
        ownQueue = new FsMsgQueue(ownQueueDir);
    }

    public void addPeer(String address, String queueDir) {
        FsMsgQueue q = new FsMsgQueue(queueDir);
        peerQueues.put(address, q);
    }

    public void sendMsgToPeer(String address, String msg) throws UnknownPeerException {
        getQueueForPeer(address).add(msg);
    }

    public void waitForPeerOnline(String address) {
        String msg = "";
        while (msg != "startup from " + address) {
            log("Waiting for " + address);
            msg = waitForMsg();
        }
    }

    public void clearOwnQueue()  {
        ownQueue.clear();
    }

    public String waitForMsg() {
        while (ownQueue.isEmpty()) {
            TestUtils.sleep(100);
        }
        return ownQueue.remove();
    }

    public void sendSigOnlineToPeer(String address) {
        String msg = SIGNALONLINEMSG + " " + ownAddress;
        log("Sending SIGONLINE to: " + address);
        sendMsgToPeer(address, msg);
    }

    public void broadcastSigOnline() {
        for (String k : peerQueues.keySet()) {
            sendSigOnlineToPeer(k);
        }
    }

    private FsMsgQueue getQueueForPeer(String address) throws UnknownPeerException {
        FsMsgQueue ret = peerQueues.get(address);
        if (ret == null) {
            throw new UnknownPeerException("No peer with address:" + address);
        }
        return ret;
    }
}


class UnknownPeerException extends RuntimeException {
    UnknownPeerException(String message) {
        super(message);
    }
}