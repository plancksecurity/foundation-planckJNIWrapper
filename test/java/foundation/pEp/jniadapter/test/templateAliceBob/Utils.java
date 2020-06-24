package foundation.pEp.jniadapter.test.templateAliceBob;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;

public class Utils {
    public static List<TransportMessage> encryptInlineEA(MultiPeerCTX ctx, Identity from, Identity to, String payloadPlain) throws RuntimeException {
        List<TransportMessage> ret = new ArrayList<>();

        // 1. put payload into .longmsg
        Message msgPlain = AdapterTestUtils.makeNewTestMessage(from, to, Message.Direction.Outgoing);
        msgPlain.setLongmsg(payloadPlain);
        msgPlain.setEncFormat(Message.EncFormat.PEPEncInlineEA);
        log("MSG PLAIN: " + AdapterTestUtils.msgToString(msgPlain, false));

        // 2. call encrypt_message() with enc_format = PEP_enc_inline_EA
        Message msgEnc = ctx.engine.encrypt_message(msgPlain, null, Message.EncFormat.PEPEncInlineEA);
        Message msgTx = null;
        if (msgEnc == null) {
            log("UNENCRYPTED");
            msgTx = msgPlain;
        } else {
            log("ENCRYPTED");
            msgTx = msgEnc;
        }
        log("MSG AFTER ENCRYPT: \n" + AdapterTestUtils.msgToString(msgTx, false));

        // 3. you’re getting back crypto text in .longmsg and possibly ASCII encoded crypto text in .value of attachments in .attachments; .enc_format is PEP_enc_inline_EA
        // 4. send one message with the crypto text of .longmsg
        TransportMessage tmp = new TransportMessage(msgTx);
        tmp.setAttachments(new Vector<>());
        ret.add(new TransportMessage(tmp));

        // 5. send messages for each attachment in .attachments with the crypto text of .value, respectively
        for (Blob b : msgTx.getAttachments()) {
            if(b != null) {
                tmp.setLongMessage(new String(b.data));
                ret.add(new TransportMessage(tmp));
            } else {
                throw new RuntimeException("NULL ATTACHMENT");
            }
        }
        return ret;
    }

    public static String serializepEpMessage(Message msgTx) {
        TransportMessage msgTransport = new TransportMessage(msgTx);
        String msgTxSerialized = null;
        try {
            msgTxSerialized = msgTransport.serialize();
        } catch (IOException e) {
            log("Exception while serializing: " + e.toString());
        }
        return msgTxSerialized;
    }

    public static Message deserializepEpMessageEA(MultiPeerCTX ctx, FsMQMessage msgRxSerialized) {
        Message ret = null;
        try {
            TransportMessage msgTransportRx = TransportMessage.deserialize(msgRxSerialized.getMsg());
            ret = ctx.engine.incomingMessageFromPGPText(msgTransportRx.getLongMessage(), Message.EncFormat.PEPEncInlineEA);
            // From
            Identity from = new Identity();
            from.address = msgTransportRx.getFromAddress();
            from = ctx.engine.updateIdentity(from);
            ret.setFrom(from);

            // To
            Vector<Identity> toList = new Vector<>();
            for (String addr: msgTransportRx.getToAddresses()) {
                Identity to = new Identity();
                to.address = addr;
                to = ctx.engine.myself(to);
                toList.add(to);
            }
            ret.setTo(toList);

        } catch (Exception e) {
            log("Exception while deserializing: " + e.toString());
        }
        return ret;
    }
}