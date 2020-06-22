package foundation.pEp.jniadapter.test.templateAliceBob;

import static foundation.pEp.pitytest.TestLogger.*;

import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQMessage;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

import java.io.IOException;
import java.util.Vector;

class TestBob {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.YELLOW);

        MultiPeerCTX mpctx = new MultiPeerCTX("Bob");

        new TestUnit<MultiPeerCTX>("Bob rx msg", mpctx, ctx -> {
            log(AdapterTestUtils.identityToString(ctx.bob, true));
            log("myself()");
            ctx.bob = ctx.engine.myself(ctx.bob);
            log(AdapterTestUtils.identityToString(ctx.bob, true));

            log(AdapterTestUtils.identityToString(ctx.alice, true));
            log("update()");
            ctx.engine.updateIdentity(ctx.alice);
            log(AdapterTestUtils.identityToString(ctx.alice, true));

            FsMQMessage msgRx = null;

            while (true) {
                try {
                    msgRx = ctx.qm.receiveMessage(1000000);
                } catch (Exception e) {
                    e.printStackTrace();
                    assert false : e.toString();
                }
                log("Msg Rx from[" + msgRx.getFrom().getAddress() + "]: " + msgRx.getMsg());

                Message rxMsg = null;
                if(false) {
                    rxMsg = ctx.engine.incomingMessageFromPGPText(msgRx.getMsg(), Message.EncFormat.PEP);
                } else {
                    pEpMessage rxMsgpEp = null;
                    try {
                        rxMsgpEp = pEpMessage.deserialize(msgRx.getMsg());
                    } catch (Exception e) {
                        log("Exception while deserializing: " + e.toString());
                    }
                    rxMsg = rxMsgpEp.toMessage();
                }
                log("Orig IN: " + AdapterTestUtils.msgToString(rxMsg, false));

                // decrypt
                Engine.decrypt_message_Return result = ctx.engine.decrypt_message(rxMsg, null, 0);
                log("Msg Rx from[" + msgRx.getFrom().getAddress() + "]: " + AdapterTestUtils.msgToString(result.dst,false));

            }
        });

        new TestUnit<MultiPeerCTX>("Bob tx msg", mpctx, ctx -> {
            ctx.bob = ctx.engine.myself(ctx.bob);

            if (ctx.bob.fpr == null) {
                throw new RuntimeException();
            }

            //send message
        });


        TestSuite.getDefault().run();
    }
}


