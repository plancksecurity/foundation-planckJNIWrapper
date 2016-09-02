package org.pEp.jniadapter;

import org.pEp.jniadapter.Identity;
import org.pEp.jniadapter.Message;
import org.pEp.jniadapter.pEpException;

/**
 * Created by huss on 02/09/16.
 */

public interface Sync {

    interface MessageToSendCallback {
        void messageToSend(Message message);
    }

    interface showHandshakeCallback {
        void showHandshake(Identity myself, Identity partner);
    }

}
