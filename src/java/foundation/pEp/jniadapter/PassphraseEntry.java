package foundation.pEp.jniadapter;

public class PassphraseEntry {
    public String email;
    public String passphrase;

    public PassphraseEntry(_PassphraseEntry entry) {
        email = Utils.toUTF16(entry.email);
        passphrase = Utils.toUTF16(entry.passphrase);
    }

    public PassphraseEntry(String email, String passphrase) {
        this.email = email;
        this.passphrase = passphrase;
    }
}
