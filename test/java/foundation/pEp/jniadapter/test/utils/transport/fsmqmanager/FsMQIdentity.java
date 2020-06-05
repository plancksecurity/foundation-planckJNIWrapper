package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager;

import java.util.Objects;

public class FsMQIdentity implements java.io.Serializable {
    private String address = null;
    private String qDir = null;

    public FsMQIdentity(String address, String qDir) {
        this.address = address;
        this.qDir = qDir;
    }

    public FsMQIdentity(FsMQIdentity ident) {
        this.address = ident.address;
        this.qDir = ident.qDir;
    }


    public String getAddress() {
        return address;
    }

    public String getqDir() {
        return qDir;
    }

    public void setqDir(String qDir) {
        this.qDir = qDir;
    }

    public String toString(){
        String ret = "";

        ret += "Address: '" + address + "'\n";
        ret += "qDir   : '" + qDir + "'";
        return  ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FsMQIdentity that = (FsMQIdentity) o;
        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}