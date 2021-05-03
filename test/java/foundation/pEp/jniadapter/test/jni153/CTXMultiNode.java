package foundation.pEp.jniadapter.test.jni153;

import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Identity;
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
import java.util.Vector;
import java.util.stream.Collectors;

import static foundation.pEp.pitytest.TestLogger.log;

public class CTXMultiNode extends AbstractTestContext {
    public Engine engine;
    public TestCallbacks callbacks;
    public FsMQManager transport;

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
        transport = new FsMQManager(transportIdent);

        // Add all transport identities of the model
        for (TestIdentity ti : model.getAllIdents()) {
            transport.identities.addAll(ti.getAllTransportIdents());
        }

        return this;
    }

    public void send(String pEpAddress, String msg) {
        //Find identity for address
        List<TestIdentity> res = model.getAllIdents().stream().filter(i -> {
            return i.pEpIdent.address.equals(pEpAddress);
        }).collect(Collectors.toList());

        if (res.size() > 1) {
            throw new RuntimeException("Unknown Error");
        } else if (res.size() <= 0) {
            throw new RuntimeException("Unknown address");
        }
        TestIdentity id = res.get(0);

        for (FsMQIdentity tID : id.getAllTransportIdents()) {
            transport.sendMessage(tID.getAddress(), msg);
//            log("send() to: " + tID.getAddress());
        }
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

    public Message reveiveMessage() {
        FsMQMessage rx = null;
        rx = transport.receiveMessage(2000);

        // Receive
        Message msgIn = new Message(rx.getMsg());
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

    public void sendMessage(Identity toIdent, String longMessage) {
        // Reply
        Message reply = new Message();
        Vector<Identity> to = new Vector<>();
        to.add(toIdent);
        reply.setTo(to);
        reply.setFrom(myself.pEpIdent);
        reply.setShortmsg("Reply");
        reply.setLongmsg(longMessage);
        reply.setDir(Message.Direction.Outgoing);

        Message replyEnc = engine.encrypt_message(reply, null, Message.EncFormat.PEP);

        String encFormat;
        longMessage = reply.getLongmsg();
        String transportMsg;
        if (replyEnc != null) {
            encFormat = "CRYPT";
            transportMsg = replyEnc.encodeMIME();
        } else {
            encFormat = "PLAIN";
            transportMsg = reply.encodeMIME();
        }
        log("<- : [" + encFormat + "] - " + longMessage);
        send(toIdent.address, transportMsg);
    }
}
