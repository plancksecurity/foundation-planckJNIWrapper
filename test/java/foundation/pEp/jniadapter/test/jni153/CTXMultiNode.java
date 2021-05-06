package foundation.pEp.jniadapter.test.jni153;

import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.decrypt_message_Return;
import foundation.pEp.jniadapter.test.utils.TestCallbacks;
import foundation.pEp.jniadapter.test.utils.model.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQIdentity;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQManager;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQMessage;
import foundation.pEp.pitytest.AbstractTestContext;
import foundation.pEp.pitytest.TestContextInterface;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static foundation.pEp.pitytest.TestLogger.log;

public class CTXMultiNode extends AbstractTestContext {
    public Engine engine;
    public TestCallbacks callbacks;
    public Transport transport;

    // Model
    public TestModel model;

    // Mappings
    private NodeName ownNodeName;
    private TestNode ownNode;
    public TestIdentity myself;
    public TestIdentity partner;


    CTXMultiNode(NodeName ownNodeName) {
        this.ownNodeName = ownNodeName;
    }

    @Override
    public TestContextInterface init() throws Throwable {
        // pEp
        callbacks = new TestCallbacks();
        engine = new Engine();
        engine.setMessageToSendCallback(callbacks);
        engine.setNotifyHandshakeCallback(callbacks);

        // Model
        model = setupModel();

        // Setup Perspective
        ownNode = model.getNode(ownNodeName);
        myself = ownNode.getIdent();
        partner = myself.getDefaultPartner();

        // Transport
        // Create own transport identity and Transport
        FsMQIdentity transportIdent = myself.getTransportIdent(ownNodeName);
        transport = new Transport(transportIdent, model.getAllIdents());

        return this;
    }

    private TestModel setupModel() {
        TestModel ret = new TestModel();

        ret.getNode(NodeName.NODE_A1).setRole(Role.ALICE);
        ret.getNode(NodeName.NODE_B1).setRole(Role.BOB);

        ret.getIdent(Role.ALICE).setDefaultPartner(Role.BOB);
        ret.getIdent(Role.BOB).setDefaultPartner(Role.ALICE);
//        ret.getIdent(Role.CAROL).setDefaultPartner(Role.ALICE);

        return ret;
    }

}

class Transport {
    private FsMQManager fsMQTransport = null;
    private EncryptingSenderThread sender = null;
    private DecryptingReceiverThread receiver = null;
    private FsMQIdentity myself = null;
    private List<TestIdentity> peers = null;

    // Message queues
    private LinkedBlockingQueue<Message> txQueue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Message> rxQueue = new LinkedBlockingQueue<>();

    public Transport(FsMQIdentity ownIdent, List<TestIdentity> peers) {
        this.myself = ownIdent;
        this.peers = peers;
        this.sender = new EncryptingSenderThread(this, txQueue);
        this.receiver = new DecryptingReceiverThread(this, rxQueue);
        this.fsMQTransport = new FsMQManager(ownIdent);

        for (TestIdentity ti : peers) {
            fsMQTransport.identities.addAll(ti.getAllTransportIdents());
        }
    }

    public void clearOwnQueue() {
        fsMQTransport.clearOwnQueue();
    }

    public boolean canReceiveAsync() {
        return !rxQueue.isEmpty();
    }

    public void sendAsync(Message msg) {
        txQueue.add(msg);
    }

    public Message receiveAsyncNonBlocking() {
        return rxQueue.remove();
    }

    public void sendRaw(String pEpAddress, String msg) {
        //Find identity for address
        List<TestIdentity> res = peers.stream().filter(i -> {
            return i.pEpIdent.address.equals(pEpAddress);
        }).collect(Collectors.toList());

        if (res.size() > 1) {
            throw new RuntimeException("Unknown Error");
        } else if (res.size() <= 0) {
            throw new RuntimeException("Unknown address");
        }
        TestIdentity id = res.get(0);

        for (FsMQIdentity tID : id.getAllTransportIdents()) {
            fsMQTransport.sendMessage(tID.getAddress(), msg);
//            log("send() to: " + tID.getAddress());
        }
    }

    public String receiveRaw() {
        FsMQMessage rx = fsMQTransport.receiveMessage(2000);
        return rx.getMsg();
    }

    public void start() {
        sender.start();
        receiver.start();
    }
}


class EncryptingSenderThread extends Thread {
    private Engine engine = null;
    private Transport transport = null;
    private LinkedBlockingQueue<Message> queue;

    public EncryptingSenderThread(Transport transport, LinkedBlockingQueue queue) {
        this.transport = transport;
        this.queue = queue;
    }

    @Override
    public void run() {
        engine = new Engine();
        Message msg;
        String msgEnc;
        while (true) {
            try {
                msg = queue.take();
                if (msg.getTo().size() != 1) {
                    throw new RuntimeException("Sorry, msg.To has to have exactly 1 receiver for now");
                }
                String to = msg.getTo().get(0).address;
                msgEnc = encryptMessage(msg);
                transport.sendRaw(to, msgEnc);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String encryptMessage(Message msg) {
        String longMessage = msg.getLongmsg();

        Message msgEnc = engine.encrypt_message(msg, null, Message.EncFormat.PEP);
        String encFormat;
        String transportMsg;
        if (msgEnc != null) {
            encFormat = "CRYPT";
            transportMsg = msgEnc.encodeMIME();
        } else {
            encFormat = "PLAIN";
            transportMsg = msg.encodeMIME();
        }
        log("<- : [" + encFormat + "] - " + longMessage);
        return transportMsg;
    }
}


class DecryptingReceiverThread extends Thread {
    private Engine engine = null;
    private Transport transport = null;
    private LinkedBlockingQueue<Message> queue;

    public DecryptingReceiverThread(Transport transport, LinkedBlockingQueue queue) {
        this.transport = transport;
        this.queue = queue;
    }

    @Override
    public void run() {
        engine = new Engine();
        String msg;
        Message msgDec;
        while (true) {
            msg = transport.receiveRaw();
            msgDec = decryptMessage(msg);
            try {
                queue.put(msgDec);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Message decryptMessage(String msg) {
        Message msgIn = new Message(msg);
        Message msgInDec = null;

        decrypt_message_Return decRet = engine.decrypt_message(msgIn, null, 0);

        msgInDec = decRet.dst;

        String encFormat = "PLAIN";
        if (!msgIn.getLongmsg().equals(msgInDec.getLongmsg())) {
            encFormat = "CRYPT";
        }

        log("-> : [" + encFormat + "] - " + msgInDec.getLongmsg());
        return msgInDec;
    }
}