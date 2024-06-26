package foundation.pEp.jniadapter;

public class _PassphraseEntry {
    public byte[] email;
    public byte[] passphrase;

    public _PassphraseEntry(PassphraseEntry entry) {
        email = Utils.toUTF8(entry.email);
        passphrase = Utils.toUTF8(entry.passphrase);
    }
}
