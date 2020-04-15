package foundation.pEp.jniadapter.test.jni92;
import foundation.pEp.jniadapter.test.utils.TestUtils;
import foundation.pEp.jniadapter.*;

class SyncCallbacks implements Sync.MessageToSendCallback, Sync.NotifyHandshakeCallback {
    public void messageToSend(Message message)
    {
        TestUtils.logH2("Message to send called");
        TestUtils.log("From: " + message.getFrom());
        TestUtils.log("To: " + message.getTo());
        TestUtils.log("Subject: " + message.getShortmsg());
        TestUtils.log("Attachement[0]: " + message.getAttachments().get(0).toString());
        TestUtils.logSectEnd();
    }

    public void notifyHandshake(Identity myself, Identity partner, SyncHandshakeSignal signal)
    {
        TestUtils.logH2("Notify handshake called");
        TestUtils.log("Myself: " + myself);
        TestUtils.log("Partner: " + partner);
        TestUtils.log("Signal: " + signal);
        TestUtils.logSectEnd();
    }
}