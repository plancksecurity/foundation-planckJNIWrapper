package foundation.pEp.jniadapter.test.jni153;

import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.decrypt_message_Return;
import foundation.pEp.jniadapter.test.utils.TestCallbacks;
import foundation.pEp.jniadapter.test.utils.model.*;
import foundation.pEp.jniadapter.test.utils.transport.Transport;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQIdentity;
import foundation.pEp.pitytest.AbstractTestContext;
import foundation.pEp.pitytest.TestContextInterface;

import java.util.ArrayList;
import java.util.List;

import static foundation.pEp.pitytest.TestLogger.log;

public class CTXMultiNode extends AbstractTestContext {
    public Engine engine;
    public TestCallbacks callbacks;
    public Transport transport;

    // Model
    public TestModel<pEpTestIdentity, TestNode<pEpTestIdentity>> model;

    // Mappings
    private NodeName ownNodeName;
    private TestNode<pEpTestIdentity> ownNode;
    public pEpTestIdentity myself;
    public pEpTestIdentity partner;

    CTXMultiNode(NodeName ownNodeName) {
        this.ownNodeName = ownNodeName;
    }

    private TestModel<pEpTestIdentity, TestNode<pEpTestIdentity>> setupModel() {
        TestModel<pEpTestIdentity, TestNode<pEpTestIdentity>> ret = new TestModel(pEpTestIdentity::new, TestNode<pEpTestIdentity>::new);

        ret.getNode(NodeName.NODE_A1).setDefaultRole(Role.ALICE);
        ret.getNode(NodeName.NODE_B1).setDefaultRole(Role.BOB);

        ret.getIdent(Role.ALICE).setDefaultPartner(Role.BOB);
        ret.getIdent(Role.BOB).setDefaultPartner(Role.ALICE);
//        ret.getIdent(Role.CAROL).setDefaultPartner(Role.ALICE);

        return ret;
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
        partner = (pEpTestIdentity) myself.getDefaultPartner();

        // Transport
        // Create own transport identity and Transport
        FsMQIdentity transportIdent = myself.getTransportIdent(ownNodeName);
        List<TestIdentity> peers = new ArrayList<>();
        peers.addAll(model.getAllIdents());
        transport = new Transport(transportIdent, peers);
        transport.setAsyncTxProcessor(this::asyncEnc);
        transport.setAsyncRxProcessor(this::asyncDec);
        return this;
    }

    private String asyncEnc(String mime_text) {
        Message msg = new Message(mime_text);
        msg.setDir(Message.Direction.Outgoing);
        String longMessage = msg.getLongmsg();
//        log(AdapterTestUtils.msgToString(msg, true));
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

    private String asyncDec(String msg) {
        Message msgIn = new Message(msg);
        Message msgInDec = null;

        decrypt_message_Return decRet = engine.decrypt_message(msgIn, null, 0);

        msgInDec = decRet.dst;

        String encFormat = "PLAIN";
        if (!msgIn.getLongmsg().equals(msgInDec.getLongmsg())) {
            encFormat = "CRYPT";
        }

        log("-> : [" + encFormat + "] - " + msgInDec.getLongmsg());
        return msgInDec.encodeMIME();
    }


}