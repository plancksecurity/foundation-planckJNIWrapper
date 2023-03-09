package foundation.pEp.jniadapter.interfaces;

import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.Pair;
import foundation.pEp.jniadapter.Sync;
import java.util.ArrayList;

public interface AbstractEngineInterface extends AutoCloseable {
    public String getVersion();

    public String getProtocolVersion();

    public void startSync();

    public void stopSync();

    public void config_media_keys(ArrayList<Pair<String, String>> value);

    public boolean isSyncRunning();

    public void setMessageToSendCallback(Sync.MessageToSendCallback messageToSendCallback);

    public void setNotifyHandshakeCallback(Sync.NotifyHandshakeCallback notifyHandshakeCallback);

    public void setNeedsFastPollCallback(Sync.NeedsFastPollCallback needsFastPollCallback);

    public void setPassphraseRequiredCallback(Sync.PassphraseRequiredCallback passphraseRequiredCallback);

    public Message incomingMessageFromPGPText(String pgpText, Message.EncFormat encFormat);
}
