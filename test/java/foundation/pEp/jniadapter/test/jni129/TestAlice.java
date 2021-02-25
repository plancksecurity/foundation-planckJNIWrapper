package foundation.pEp.jniadapter.test.jni129;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.AdapterBaseTestContext;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.util.Date;
import java.util.Vector;

class Jni129TestContext extends AdapterBaseTestContext {
    @Override
    public AdapterBaseTestContext init() throws Throwable {
        super.init();
        return this;
    }
}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        AdapterBaseTestContext jni129Ctx = new Jni129TestContext();

        new TestUnit<AdapterBaseTestContext>("setDir() == getDir() ", new Jni129TestContext(), ctx -> {
            Message msg = new Message();
            Message.Direction inVal = ctx.msgDirOutgoing;
            msg.setDir(inVal);
            Message.Direction outVal = msg.getDir();
            assert outVal == inVal : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        new TestUnit<AdapterBaseTestContext>("setId() == getId() ", new Jni129TestContext(), ctx -> {
            Message msg = new Message();
            String inVal = "23";
            msg.setId(inVal);
            String outVal = msg.getId();
            assert outVal.equals(inVal) : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        new TestUnit<AdapterBaseTestContext>("setShortmsg() == getShortmsg() ", new Jni129TestContext(), ctx -> {
            Message msg = new Message();
            String inVal = "23";
            msg.setShortmsg(inVal);
            String outVal = msg.getShortmsg();
            assert outVal.equals(inVal) : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        new TestUnit<AdapterBaseTestContext>("setLongmsg() == getLongmsg() ", new Jni129TestContext(), ctx -> {
            Message msg = new Message();
            String inVal = "23";
            msg.setLongmsg(inVal);
            String outVal = msg.getLongmsg();
            assert outVal.equals(inVal) : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        new TestUnit<AdapterBaseTestContext>("setLongmsgFormatted() == getLongmsgFormatted() ", new Jni129TestContext(), ctx -> {
            Message msg = new Message();
            String inVal = "23";
            msg.setLongmsgFormatted(inVal);
            String outVal = msg.getLongmsgFormatted();
            assert outVal.equals(inVal) : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        new TestUnit<AdapterBaseTestContext>("setAttachments() == getAttachments() ", new Jni129TestContext(), ctx -> {
            Message msg = new Message();
            Vector<Blob> inVal = ctx.attachments;
            msg.setAttachments(inVal);
            Vector<Blob> outVal = msg.getAttachments();
            for (int i = 0; i < 3; i++) {
                Blob inElem = inVal.get(i);
                Blob outElem = outVal.get(i);
                assert  inElem == inElem : "\nreturned '" + outElem + "' instead of '" + inElem + "'";
            }
        });

        new TestUnit<AdapterBaseTestContext>("setSent() == getSent() ", new Jni129TestContext(), ctx -> {
            Message msg = new Message();
            Date inVal = new Date();
            msg.setSent(inVal);
            Date outVal = msg.getSent();
            assert outVal == inVal : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        new TestUnit<AdapterBaseTestContext>("setRecv() == getRecv() ", new Jni129TestContext(), ctx -> {
            Message msg = new Message();
            Date inVal = new Date();
            msg.setRecv(inVal);
            Date outVal = msg.getRecv();
            assert outVal == inVal : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        new TestUnit<AdapterBaseTestContext>("setFrom() == getFrom() ", new Jni129TestContext(), ctx -> {
            Message msg = new Message();
            Identity inVal = ctx.alice;
            msg.setFrom(inVal);
            Identity outVal = msg.getFrom();
            assert outVal.equals(inVal) : "\nreturned '" + outVal + "'\nexpected '" + inVal + "'";
        });

        TestSuite.getDefault().run();
    }
}


