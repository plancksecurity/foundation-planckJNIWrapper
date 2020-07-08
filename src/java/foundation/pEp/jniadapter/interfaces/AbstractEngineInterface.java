package foundation.pEp.jniadapter.interfaces;

import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.Sync;

public interface AbstractEngineInterface {
    public String getVersion();

    public String getProtocolVersion();

    public void startKeyserverLookup();

    public void stopKeyserverLookup();

    public void startSync();

    public void stopSync();

    public boolean isSyncRunning();

    public void setMessageToSendCallback(Sync.MessageToSendCallback messageToSendCallback);

    public void setNotifyHandshakeCallback(Sync.NotifyHandshakeCallback notifyHandshakeCallback);

    public void setNeedsFastPollCallback(Sync.NeedsFastPollCallback needsFastPollCallback);

    public void setPassphraseRequiredCallback(Sync.PassphraseRequiredCallback passphraseRequiredCallback);

    public Message incomingMessageFromPGPText(String pgpText, Message.EncFormat encFormat);
}
