package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager;

public class FsMQIdentity implements java.io.Serializable {
    private String address = null;
    private String qDir = null;

    public FsMQIdentity(String address, String qDir) {
        this.address = address;
        this.qDir = qDir;
    }

    public String getAddress() {
        return address;
    }

    public String getqDir() {
        return qDir;
    }
}