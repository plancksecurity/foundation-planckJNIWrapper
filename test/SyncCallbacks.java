import org.pEp.jniadapter.*;

class SyncCallbacks implements Sync.MessageToSendCallback, Sync.NotifyHandshakeCallback {
    public void messageToSend(Message message)
    {
        System.out.println(message.getFrom());
    }

    public void notifyHandshake(Identity myself, Identity partner, SyncHandshakeSignal signal)
    {

    }
}

