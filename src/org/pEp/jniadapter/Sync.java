package org.pEp.jniadapter;

import org.pEp.jniadapter.Identity;
import org.pEp.jniadapter.Message;
import org.pEp.jniadapter.pEpException;

/**
 * Created by huss on 02/09/16.
 */

public interface Sync {

     
    interface NeedsFastPollCallback {
        void needsFastPollCallFromC(Boolean fast_poll_needed);
    }

    interface MessageToSendCallback {
        void messageToSend(Message message);
    }

    interface notifyHandshakeCallback {
        void notifyHandshake(Identity myself, Identity partner, SyncHandshakeSignal signal);
    }

}
