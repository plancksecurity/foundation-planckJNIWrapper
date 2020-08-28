package foundation.pEp.jniadapter.test.templateAliceBob;

import static foundation.pEp.pitytest.TestLogger.*;

import foundation.pEp.jniadapter.*;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQMessage;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TestBob {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.YELLOW);

        MultiPeerCTX mpctx = new MultiPeerCTX("Bob");

        new TestUnit<MultiPeerCTX>("Bob rx msg", mpctx, ctx -> {
            log("myself()");
            ctx.bob = ctx.engine.myself(ctx.bob);
            log(AdapterTestUtils.identityToString(ctx.bob, false));

//            log(AdapterTestUtils.identityToString(ctx.alice, false));
//            log("update()");
//            ctx.alice = ctx.engine.updateIdentity(ctx.alice);
//            log(AdapterTestUtils.identityToString(ctx.alice, false));

            try {
                FsMQMessage msgRxSerialized = null;
                while ((msgRxSerialized = ctx.qm.receiveMessage(3)) != null) {
//                    log("MSG RX from [" + msgRxSerialized.getFrom().getAddress() + "]: " + msgRxSerialized.getMsg());

//                    Message msgRx = Utils.deserializepEpMessage(ctx, msgRxSerialized, Message.EncFormat.PEP);
                    Message msgRx = Utils.deserializepEpMessage(ctx, msgRxSerialized, Message.EncFormat.PEPEncInlineEA);
                    log("ENCRYPTED IN: \n" + AdapterTestUtils.msgToString(msgRx, false));

                    decrypt_message_Return result = ctx.engine.decrypt_message(msgRx, null, 0);
                    log("DECRYPTED msg: \n" + AdapterTestUtils.msgToString(result.dst, false));
                    log("DECRYPTED rating:" + result.rating.toString());
                    log("DECRYPTED flags:" + result.flags);

                    log(AdapterTestUtils.identityToString(ctx.alice, false));
                    ctx.alice = ctx.engine.updateIdentity(msgRx.getFrom());
                    log(AdapterTestUtils.identityToString(ctx.alice, false));
                }
            } catch (Exception e) {
                assert false : e.toString();
            }
            log("Stop Receiving, no more messages...");
        });

        new TestUnit<MultiPeerCTX>("Bob tx msg", mpctx, ctx -> {
            String payloadPlain = "PONG";
            List<TransportMessage> msgTx = new ArrayList<>();
//            msgTx.add(Utils.encryptPEP(ctx, ctx.bob, ctx.alice, payloadPlain));
            msgTx = Utils.encryptInlineEA(ctx, ctx.bob, ctx.alice, payloadPlain);

            for (TransportMessage out : msgTx) {
                log("MSG TX: \n" + out.toString());
                try {
                    String msgTxSerialized = out.serialize();
                    ctx.qm.sendMessage("Alice", msgTxSerialized);
                } catch (IOException e) {
                    assert false : e.toString();
                }
            }
            log("Sending messages finished...");
        });


        TestSuite.getDefault().run();
    }


}


