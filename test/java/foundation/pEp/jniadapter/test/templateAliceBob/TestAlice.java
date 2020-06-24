package foundation.pEp.jniadapter.test.templateAliceBob;

import static foundation.pEp.pitytest.TestLogger.*;

import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQMessage;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

import java.io.*;
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

            log(AdapterTestUtils.identityToString(ctx.bob, false));
            log("update()");
            ctx.bob = ctx.engine.updateIdentity(ctx.bob);
            log(AdapterTestUtils.identityToString(ctx.bob, false));

            String payloadPlain = "PING";
            List<TransportMessage> msgTx = Utils.encryptInlineEA(ctx, ctx.alice, ctx.bob, payloadPlain);

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

                    Message msgRx = Utils.deserializepEpMessageEA(ctx, msgRxSerialized);
                    log("ENCRYPTED IN: \n" + AdapterTestUtils.msgToString(msgRx, false));

                    Engine.decrypt_message_Return result = ctx.engine.decrypt_message(msgRx, null, 0);
                    log("DECRYPTED msg: \n" + AdapterTestUtils.msgToString(result.dst, false));
                    log("DECRYPTED rating:" + result.rating.toString());
                    log("DECRYPTED flags:" + result.flags);
                }
            } catch (Exception e) {
                assert false : e.toString();
            }
            log(AdapterTestUtils.identityToString(ctx.bob, false));
            ctx.bob = ctx.engine.updateIdentity(ctx.bob);
            log(AdapterTestUtils.identityToString(ctx.bob, false));
            log("Stop Receiving, no more messages...");
        });

        TestSuite.getDefault().run();
    }


}

