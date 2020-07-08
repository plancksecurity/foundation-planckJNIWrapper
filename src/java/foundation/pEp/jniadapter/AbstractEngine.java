package foundation.pEp.jniadapter;

import java.util.ArrayList;
import java.util.Vector;

import foundation.pEp.jniadapter.Sync.DefaultCallback;
import foundation.pEp.jniadapter.interfaces.*;
import foundation.pEp.jniadapter.exceptions.*;


abstract class AbstractEngine extends UniquelyIdentifiable implements AbstractEngineInterface, AutoCloseable {
    static {
        System.loadLibrary("pEpJNI");
    }

    private Sync.MessageToSendCallback messageToSendCallback;
    private Sync.NotifyHandshakeCallback notifyHandshakeCallback;
    private Sync.NeedsFastPollCallback needsFastPollCallback;
    private Sync.PassphraseRequiredCallback passphraseRequiredCallback;

    private final static DefaultCallback defaultCallback = new DefaultCallback();

    private native void init();
    private native void release();

    private long keyserverThread;
    private long keyserverQueue;

    public AbstractEngine() throws pEpException {
        synchronized (AbstractEngine.class) {
            init();
        }
    }

    final public void close() {
        synchronized (AbstractEngine.class){
            release();
        }
    }

    public String getVersion() {
        return _getVersion();
    }

    private native String _getVersion();


    public String getProtocolVersion() {
        return _getProtocolVersion();
    }

    private native String _getProtocolVersion();


    public void startKeyserverLookup() {
        _startKeyserverLookup();
    }

    private native void _startKeyserverLookup();

    public void stopKeyserverLookup() {
        _startKeyserverLookup();
    }

    private native void _stopKeyserverLookup();


    public void startSync() {
        _startSync();
    }

    private native void _startSync();

    public void stopSync() {
        _stopSync();
    }

    private native void _stopSync();

    public boolean isSyncRunning() {
        return _isSyncRunning();
    }

    private native boolean _isSyncRunning();

    // Callbacks
    public void setMessageToSendCallback(Sync.MessageToSendCallback messageToSendCallback) {
        this.messageToSendCallback = messageToSendCallback;
    }

    public void setNotifyHandshakeCallback(Sync.NotifyHandshakeCallback notifyHandshakeCallback) {
        this.notifyHandshakeCallback = notifyHandshakeCallback;
    }

    public void setNeedsFastPollCallback(Sync.NeedsFastPollCallback needsFastPollCallback) {
        this.needsFastPollCallback = needsFastPollCallback;
    }

    public void setPassphraseRequiredCallback(Sync.PassphraseRequiredCallback passphraseRequiredCallback) {
        System.out.println("passphraseRequiredCallback has been registered to:" + passphraseRequiredCallback.toString() + " on engine ObjID: " + getId());

        this.passphraseRequiredCallback = passphraseRequiredCallback;
    }

    private int needsFastPollCallFromC(boolean fast_poll_needed) {
        if (needsFastPollCallback != null) {
            needsFastPollCallback.needsFastPollCallFromC(fast_poll_needed);
        } else {
            defaultCallback.needsFastPollCallFromC(fast_poll_needed);
        }
        return 0;
    }

    private int notifyHandshakeCallFromC(_Identity _myself, _Identity _partner, SyncHandshakeSignal _signal) {
        Identity myself = new Identity(_myself);
        Identity partner = new Identity(_partner);
        System.out.println("pEpSync" +"notifyHandshakeCallFromC: " + notifyHandshakeCallback);
        if (notifyHandshakeCallback != null) {
            notifyHandshakeCallback.notifyHandshake(myself, partner, _signal);
        } else {
            defaultCallback.notifyHandshake(myself, partner, _signal);
        }
        return 0;
    }

    private byte[] passphraseRequiredFromC() {
        String ret = "";
        if (passphraseRequiredCallback != null) {
            System.out.println("calling passphraseRequiredCallback on engine ObjID:" + getId());
            ret = passphraseRequiredCallback.passphraseRequired();
        } else {
            System.out.println("no callback registered on engine ObjID:" + getId());
            // if this happens (no callback registered
            // we simply return ""
            // it will fail
            // this repeats MaxRetries times (currentluy hardcoded to 3)
            // Then the orig call will return with the PEP_STATUS (most likely PEP_PASSPHRASE_REQUIRED)
        }
        return Utils.toUTF8(ret);
    }

    private int messageToSendCallFromC (Message message) {
        System.out.println("pEpSync" + "messageToSendCallFromC: " + messageToSendCallback );
        if (messageToSendCallback != null) {
            messageToSendCallback.messageToSend(message);
        } else {
            defaultCallback.messageToSend(message);
        }
        return 0;
    }

    public Message incomingMessageFromPGPText(String pgpText, Message.EncFormat encFormat) {
        Message msg = new Message();
        msg.setDir(Message.Direction.Incoming);
        msg.setEncFormat(encFormat);

        // Opts
        ArrayList<Pair<String, String>> opts = new ArrayList<>();
        Pair<String, String> xpEp = new Pair<>();
        xpEp.first = "X-pEp-Version";
        xpEp.second = this.getProtocolVersion();
        opts.add(xpEp);
        msg.setOptFields(opts);

        if (encFormat == Message.EncFormat.PEP) {
            // For EncFormat.PEP
            // The pgpText goes into the attachment index 1
            msg.setShortmsg("p≡p");
            msg.setLongmsg("this message was encrypted with p≡p https://pEp-project.org");

            // Attachments
            Blob att0 = new Blob();
            att0.mime_type = "application/pgp-encrypted";
            att0.filename = null;
            att0.data = "Version: 1".getBytes();

            Blob att1 = new Blob();
            att1.mime_type = "application/octet-stream";
            att1.filename = "file://msg.asc";
            att1.data = pgpText.getBytes();

            Vector<Blob> attachments = new Vector<>();
            attachments.add(att0);
            attachments.add(att1);
            msg.setAttachments(attachments);
        } else if (encFormat == Message.EncFormat.PEPEncInlineEA) {
            // For EncFormat.PEPEncInlineEA
            // The pgpText goes into the longMessage
            msg.setShortmsg("");
            msg.setLongmsg(pgpText);
        } else {
            throw new pEpCannotEncode("Message.Encformat not supported: " + encFormat.toString());
        }

        return msg;
    }
}