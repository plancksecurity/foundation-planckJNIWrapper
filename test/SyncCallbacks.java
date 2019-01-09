import org.pEp.jniadapter.*;

class SyncCallbacks implements Sync.MessageToSendCallback, Sync.NotifyHandshakeCallback {
    public void messageToSend(Message message)
    {
	System.out.println("================================");
	System.out.println("Message to send called");
        System.out.println("From: " + message.getFrom());
        System.out.println("To: " + message.getTo());
        System.out.println("Subject: " + message.getShortmsg());
	System.out.println("================================");
    }

    public void notifyHandshake(Identity myself, Identity partner, SyncHandshakeSignal signal)
    {
	System.out.println("================================");
	System.out.println("Notify handshake called");
	System.out.println("Myself: " + myself);
	System.out.println("Partner: " + partner);
	System.out.println("Signal: " + signal);
	System.out.println("================================");
    }
}

