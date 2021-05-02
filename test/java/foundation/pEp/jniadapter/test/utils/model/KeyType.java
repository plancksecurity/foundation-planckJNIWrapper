package foundation.pEp.jniadapter.test.utils.model;

public enum KeyType {
    NORMAL() {
        public String toString() {
            return "normal";
        }
    },
    PASSPHRASE() {
        public String toString() {
            return "passphrase";
        }
    }
}
