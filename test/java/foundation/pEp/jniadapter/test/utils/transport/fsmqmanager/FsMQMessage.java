package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager;

public class FsMQMessage implements java.io.Serializable {
    private FsMQIdentity from = null;
    private String msg = null;

    public FsMQMessage(FsMQIdentity from, String msg) throws NullPointerException {
        if (from == null || msg == null) {
            throw new IllegalStateException("from and msg cant be null");
        }
        this.from = from;
        this.msg = msg;
    }

    public FsMQIdentity getFrom() {
        return from;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "from: '" + from.getAddress() + "'\n";
        ret += "msg : '" + msg + "'";
        return ret;
    }
}
