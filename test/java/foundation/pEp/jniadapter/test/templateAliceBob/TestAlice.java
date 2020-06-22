package foundation.pEp.jniadapter.test.templateAliceBob;

import static foundation.pEp.pitytest.TestLogger.*;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.test.templateAliceBob.MultiPeerCTX;

import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQMessage;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.ctx.FsMQManagerTestContext;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Vector;

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        MultiPeerCTX mpctx = new MultiPeerCTX("Alice");

        new TestUnit<MultiPeerCTX>("Alice tx msg", mpctx, ctx -> {
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log(AdapterTestUtils.identityToString(ctx.alice, true));
            log("update_ident");
            ctx.alice = ctx.engine.updateIdentity(ctx.alice);
            log(AdapterTestUtils.identityToString(ctx.alice, true));

            log("myself()");
            ctx.alice = ctx.engine.myself(ctx.alice);
            log(AdapterTestUtils.identityToString(ctx.alice, true));


            log(AdapterTestUtils.identityToString(ctx.bob, true));
            log("update()");
            ctx.engine.updateIdentity(ctx.bob);
            log(AdapterTestUtils.identityToString(ctx.bob, true));

            Message pEpMsg = AdapterTestUtils.makeNewTestMessage(ctx.alice, ctx.bob, Message.Direction.Outgoing);
//            pEpMsg.setEncFormat(Message.EncFormat.None);

            log("Orig IN: " + AdapterTestUtils.msgToString(pEpMsg, false));

            Message pEpMsgEnc = ctx.engine.encrypt_message(pEpMsg, null, Message.EncFormat.PEP);

            Message txMsgpEp = null;
            if (pEpMsgEnc == null) {
                log("UNENCRYPTED");
                txMsgpEp = pEpMsg;
            } else {
                log("ENCRYPTED");
                txMsgpEp = pEpMsgEnc;
            }
            log("Orig OUT: " + AdapterTestUtils.msgToString(txMsgpEp, false));

            pEpMessage pEpMessageOut = new pEpMessage(txMsgpEp);

            log("Serializing");
            String txMsg = null;
            try {
                txMsg = pEpMessageOut.serialize();
            } catch (IOException e) {
                log("Exception while serializing: " + e.toString());
            }
            log("Serialized Msg: " + txMsg);

            log("deserializing");
            pEpMessage deserialized = null;
            try {
                deserialized = pEpMessage.deserialize(txMsg);
            } catch (Exception e) {
                log("Exception while deserializing: " + e.toString());
            }

            Message reconstr = deserialized.toMessage();
            log("Reconstr: " + AdapterTestUtils.msgToString(reconstr, false));

            try {
                ctx.qm.sendMessage("Bob", txMsg);
            } catch (IOException e) {
                assert false : e.toString();
            }
        });

        new TestUnit<MultiPeerCTX>("Alice rx msg", mpctx, ctx -> {
            FsMQMessage rxMsg = null;
            try {
                rxMsg = ctx.qm.receiveMessage(200000);
            } catch (Exception e) {
                assert false : e.toString();
            }

            log("Msg rx from [" + rxMsg.getFrom() + "]:" + rxMsg.getMsg());
        });

        TestSuite.getDefault().run();
    }
}

