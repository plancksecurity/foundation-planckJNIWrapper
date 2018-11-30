package org.pEp.jniadapter;

import org.pEp.jniadapter.Identity;
import org.pEp.jniadapter.Message;
import org.pEp.jniadapter.pEpException;

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


    public class DefaultCallback 
            implements Sync.MessageToSendCallback, Sync.NotifyHandshakeCallback,  Sync.NeedsFastPollCallback {
        //FIXME: Find where this Exceptions ara catched as they are called but on 
        //Testing.java there is no crash not crashing
        @Override
        public void needsFastPollCallFromC(Boolean fast_poll_needed) {
            System.out.println("Throwing illegal");
            throw new RuntimeException("Fast poll Callback not set");
        }

        @Override
        public void messageToSend(Message message) {
            System.out.println("Throwing illegal");
            throw new RuntimeException("Message to send not set");
        }
        
        @Override
        public void notifyHandshake(Identity myself, Identity partner, SyncHandshakeSignal signal) {
            System.out.println("Throwing illegal");
            throw new RuntimeException("Notify Handshake not set");
        }
    }

}
