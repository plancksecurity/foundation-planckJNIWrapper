package foundation.pEp.jniadapter.test.utils;
import foundation.pEp.jniadapter.*;

public class TestCallbacks implements Sync.MessageToSendCallback, Sync.NotifyHandshakeCallback {
    public void messageToSend(Message message) {
        TestUtils.logH1("Message to send called");
        TestUtils.log("From: " + message.getFrom());
        TestUtils.log("To: " + message.getTo());
        TestUtils.log("Subject: " + message.getShortmsg());
        TestUtils.log("Attachement[0]: " + message.getAttachments().get(0).toString());
    }

    public void notifyHandshake(Identity myself, Identity partner, SyncHandshakeSignal signal) {
        TestUtils.logH1("Notify handshake called");
        TestUtils.log("myself: " + TestUtils.identityToString(myself, false));
        TestUtils.log("Partner: " + TestUtils.identityToString(partner, false));
        TestUtils.log("Signal: " + signal);
    }
}


