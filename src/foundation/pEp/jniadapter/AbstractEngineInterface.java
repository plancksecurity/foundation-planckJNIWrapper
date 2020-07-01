package foundation.pEp.jniadapter;

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

    public Message incomingMessageFromPGPText(String pgpText, Message.EncFormat encFormat);
}
