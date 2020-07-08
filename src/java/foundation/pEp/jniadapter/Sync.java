package foundation.pEp.jniadapter;

public interface Sync {


    interface NeedsFastPollCallback {
        void needsFastPollCallFromC(Boolean fast_poll_needed);
    }

    interface MessageToSendCallback {
        void messageToSend(Message message);
    }

    interface NotifyHandshakeCallback {
        void notifyHandshake(Identity myself, Identity partner, SyncHandshakeSignal signal);
    }

    interface PassphraseRequiredCallback {
        String passphraseRequired();
    }

    public class DefaultCallback implements Sync.MessageToSendCallback, Sync.NotifyHandshakeCallback, Sync.NeedsFastPollCallback {
        @Override
        public void needsFastPollCallFromC(Boolean fast_poll_needed) {
            System.out.println("Need fast Poll");
        }

        @Override
        public void messageToSend(Message message) {
            System.out.println("messageToSend Defualt Callback");
        }

        @Override
        public void notifyHandshake(Identity myself, Identity partner, SyncHandshakeSignal signal) {
            System.out.println("notifyHandshake Default Callback");
        }
    }

}
