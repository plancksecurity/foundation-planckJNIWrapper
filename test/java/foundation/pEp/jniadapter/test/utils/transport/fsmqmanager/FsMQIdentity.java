package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager;


class FsMQIdentity implements java.io.Serializable {
    private String address = null;
    private String qDir = null;

    FsMQIdentity(String address, String qDir) {
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