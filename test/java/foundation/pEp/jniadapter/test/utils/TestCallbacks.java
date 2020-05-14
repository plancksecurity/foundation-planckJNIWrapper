package foundation.pEp.jniadapter.test.utils;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.*;

public class TestCallbacks implements Sync.MessageToSendCallback, Sync.NotifyHandshakeCallback {
    public void messageToSend(Message message) {
        TestLogger.logH1("Message to send called");
        TestLogger.log("From: " + message.getFrom());
        TestLogger.log("To: " + message.getTo());
        TestLogger.log("Subject: " + message.getShortmsg());
        TestLogger.log("Attachement[0]: " + message.getAttachments().get(0).toString());
    }

    public void notifyHandshake(Identity myself, Identity partner, SyncHandshakeSignal signal) {
        TestLogger.logH1("Notify handshake called");
        TestLogger.log("myself: " + AdapterTestUtils.identityToString(myself, false));
        TestLogger.log("Partner: " + AdapterTestUtils.identityToString(partner, false));
        TestLogger.log("Signal: " + signal);
    }
}


