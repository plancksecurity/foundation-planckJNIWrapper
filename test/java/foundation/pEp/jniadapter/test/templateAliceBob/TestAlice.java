package foundation.pEp.jniadapter.test.templateAliceBob;

import static foundation.pEp.pitytest.TestLogger.*;

import foundation.pEp.jniadapter.*;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQMessage;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        MultiPeerCTX mpctx = new MultiPeerCTX("Alice");

        new TestUnit<MultiPeerCTX>("Alice tx msg", mpctx, ctx -> {
            log("myself()");
            ctx.alice = ctx.engine.myself(ctx.alice);
            log(AdapterTestUtils.identityToString(ctx.alice, false));

//            ctx.engine.importKey(ctx.keyBobPub);

//            log(AdapterTestUtils.identityToString(ctx.bob, false));
//            log("update()");
//            ctx.bob = ctx.engine.updateIdentity(ctx.bob);
//            log(AdapterTestUtils.identityToString(ctx.bob, false));

            String payloadPlain = "PING";
            List<TransportMessage> msgTx = new ArrayList<>();
//            msgTx.add(Utils.encryptPEP(ctx, ctx.alice, ctx.bob, payloadPlain));
            msgTx = Utils.encryptInlineEA(ctx, ctx.alice, ctx.bob, payloadPlain);

            for (TransportMessage out : msgTx) {
                log("MSG TX: \n" + out.toString());
                try {
                    String msgTxSerialized = out.serialize();
                    ctx.qm.sendMessage("Bob", msgTxSerialized);
                } catch (IOException e) {
                    assert false : e.toString();
                }
            }
            log("Sending messages finished...");
        });

        new TestUnit<MultiPeerCTX>("Alice rx msg", mpctx, ctx -> {
            try {
                FsMQMessage msgRxSerialized = null;
                while ((msgRxSerialized = ctx.qm.receiveMessage(6)) != null) {
//                    log("MSG RX from [" + msgRxSerialized.getFrom().getAddress() + "]: " + msgRxSerialized.getMsg());

//                    Message msgRx = Utils.deserializepEpMessage(ctx, msgRxSerialized, Message.EncFormat.PEP);
                    Message msgRx = Utils.deserializepEpMessage(ctx, msgRxSerialized, Message.EncFormat.PEPEncInlineEA);
                    log("ENCRYPTED IN: \n" + AdapterTestUtils.msgToString(msgRx, false));

                    decrypt_message_Return result = ctx.engine.decrypt_message(msgRx, null, 0);
                    log("DECRYPTED msg: \n" + AdapterTestUtils.msgToString(result.dst, false));
                    log("DECRYPTED rating:" + result.rating.toString());
                    log("DECRYPTED flags:" + result.flags);

                    log(AdapterTestUtils.identityToString(ctx.bob, false));
                    ctx.bob = ctx.engine.updateIdentity(msgRx.getFrom());
                    log(AdapterTestUtils.identityToString(ctx.bob, false));
                }
            } catch (Exception e) {
                assert false : e.toString();
            }
            log("Stop Receiving, no more messages...");
        });

        TestSuite.getDefault().run();
    }


}

