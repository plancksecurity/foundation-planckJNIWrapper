package foundation.pEp.jniadapter.test.utils;
import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.*;

public class TestCallbacks implements Sync.MessageToSendCallback, Sync.NotifyHandshakeCallback {
    public void messageToSend(Message message) {
        logH1("Message to send called");
        log("From: " + message.getFrom());
        log("To: " + message.getTo());
        log("Subject: " + message.getShortmsg());
        log("Attachement[0]: " + message.getAttachments().get(0).toString());
    }

    public void notifyHandshake(Identity myself, Identity partner, SyncHandshakeSignal signal) {
        logH1("Notify handshake called");
        log("myself: " + AdapterTestUtils.identityToString(myself, false));
        log("Partner: " + AdapterTestUtils.identityToString(partner, false));
        log("Signal: " + signal);
    }
}


