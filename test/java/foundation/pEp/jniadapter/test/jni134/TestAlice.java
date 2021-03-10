package foundation.pEp.jniadapter.test.jni134;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.Pair;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;

class Jni134TestContext extends CTXBase {
    @Override
    public CTXBase init() throws Throwable {
        super.init();
        return this;
    }
}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        CTXBase jni134Ctx = new Jni134TestContext();

        new TestUnit<CTXBase>("setDir() == getDir() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Message.Direction inVal = ctx.msgDirOutgoing;
            msg.setDir(inVal);
            Message.Direction outVal = msg.getDir();
            assert outVal == inVal : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        new TestUnit<CTXBase>("setId() == getId() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            String inVal = "23";
            msg.setId(inVal);
            String outVal = msg.getId();
            assert outVal.equals(inVal) : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        new TestUnit<CTXBase>("setShortmsg() == getShortmsg() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            String inVal = "23";
            msg.setShortmsg(inVal);
            String outVal = msg.getShortmsg();
            assert outVal.equals(inVal) : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        new TestUnit<CTXBase>("setLongmsg() == getLongmsg() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            String inVal = "23";
            msg.setLongmsg(inVal);
            String outVal = msg.getLongmsg();
            assert outVal.equals(inVal) : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        new TestUnit<CTXBase>("setLongmsgFormatted() == getLongmsgFormatted() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            String inVal = "23";
            msg.setLongmsgFormatted(inVal);
            String outVal = msg.getLongmsgFormatted();
            assert outVal.equals(inVal) : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        // TODO: use .equals()
        new TestUnit<CTXBase>("setAttachments() == getAttachments() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Vector<Blob> inVal = ctx.attachmentList.getAttachments();
            msg.setAttachments(inVal);
            Vector<Blob> outVal = msg.getAttachments();
            for (int i = 0; i < ctx.attachmentList.getCount(); i++) {
                Blob inElem = inVal.get(i);
                Blob outElem = outVal.get(i);
                assert inElem == outElem : "\nreturned '" + outElem + "' instead of '" + inElem + "'";
            }
        });

        // TODO: Date precision is truncated to seconds in the message struct
        new TestUnit<CTXBase>("setSent() == getSent() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Date inVal = new Date();
            msg.setSent(inVal);
            Date outVal = msg.getSent();
            log("EPOCH: " + Long.toString(inVal.getTime()));
            log("EPOCH: " + Long.toString(inVal.toInstant().getEpochSecond()));
            log("EPOCH: " + Long.toString(outVal.getTime()));
            assert outVal.equals(inVal) : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        // TODO: Date precision is truncated to seconds in the message struct
        new TestUnit<CTXBase>("setRecv() == getRecv() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Date inVal = new Date();
            msg.setRecv(inVal);
            Date outVal = msg.getRecv();
            assert outVal == inVal : "\nreturned '" + outVal + "' instead of '" + inVal + "'";
        });

        // TODO: use .equals()
        new TestUnit<CTXBase>("setFrom() == getFrom() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Identity inVal = ctx.alice;
            msg.setFrom(inVal);
            Identity outVal = msg.getFrom();
            assert outVal.equals(inVal) : "\nreturned:\n'" + outVal + "'\nexpected:\n'" + inVal + "'";
        });

        // TODO: use .equals()
        new TestUnit<CTXBase>("setTo() == getTo() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Vector<Identity> inVal = new Vector<Identity>();
            inVal.add(ctx.alice);
            inVal.add(ctx.bob);
            msg.setTo(inVal);
            Vector<Identity> outVal = msg.getTo();
            for (int i = 0; i < 2; i++) {
                Identity inElem = inVal.get(i);
                Identity outElem = outVal.get(i);
                assert outElem.equals(inElem) : "\nreturned:\n '" + outElem + "'\nexpected:\n '" + inElem + "'";
            }
        });

        // TODO: use .equals()
        new TestUnit<CTXBase>("setRecvBy() == getRecvBy() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Identity inVal = ctx.alice;
            msg.setRecvBy(inVal);
            Identity outVal = msg.getRecvBy();
            assert outVal.equals(inVal) : "\nreturned:\n '" + outVal + "'\nexpected:\n '" + inVal + "'";
        });

        // TODO: use .equals()
        new TestUnit<CTXBase>("setCc() == getCc() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Vector<Identity> inVal = new Vector<Identity>();
            inVal.add(ctx.alice);
            inVal.add(ctx.bob);
            msg.setCc(inVal);
            Vector<Identity> outVal = msg.getCc();
            for (int i = 0; i < 2; i++) {
                Identity inElem = inVal.get(i);
                Identity outElem = outVal.get(i);
                assert outElem.equals(inElem) : "\nreturned:\n '" + outElem + "'\nexpected:\n '" + inElem + "'";
            }
        });

        // TODO: use .equals()
        new TestUnit<CTXBase>("setBcc() == getBcc() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Vector<Identity> inVal = new Vector<Identity>();
            inVal.add(ctx.alice);
            inVal.add(ctx.bob);
            msg.setCc(inVal);
            Vector<Identity> outVal = msg.getBcc();
            for (int i = 0; i < 2; i++) {
                Identity inElem = inVal.get(i);
                Identity outElem = outVal.get(i);
                assert outElem.equals(inElem) : "\nreturned:\n '" + outElem + "'\nexpected:\n '" + inElem + "'";
            }
        });

        // TODO: use .equals()
        new TestUnit<CTXBase>("setReplyTo() == getReplyTo() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Vector<Identity> inVal = new Vector<Identity>();
            inVal.add(ctx.alice);
            inVal.add(ctx.bob);
            msg.setReplyTo(inVal);
            Vector<Identity> outVal = msg.getReplyTo();
            for (int i = 0; i < 2; i++) {
                Identity inElem = inVal.get(i);
                Identity outElem = outVal.get(i);
                assert outElem.equals(inElem) : "\nreturned:\n '" + outElem + "'\nexpected:\n '" + inElem + "'";
            }
        });

        // TODO: use .equals()
        new TestUnit<CTXBase>("setInReplyTo() == getInReplyTo() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Vector<String> inVal = new Vector<String>();
            inVal.add("pEp-auto-consume@pEp.foundation");
            inVal.add("any_other_message_id@blabla.bubu");
            msg.setInReplyTo(inVal);
            Vector<String> outVal = msg.getInReplyTo();
            for (int i = 0; i < 2; i++) {
                String inElem = inVal.get(i);
                String outElem = outVal.get(i);
                assert outElem.equals(inElem) : "\nreturned:\n '" + outElem + "'\nexpected:\n '" + inElem + "'";
            }
        });

        // TODO: use .equals()
        new TestUnit<CTXBase>("setReferences() == getReferences() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Vector<String> inVal = new Vector<String>();
            inVal.add("pEp-auto-consume@pEp.foundation");
            inVal.add("any_other_message_id@blabla.bubu");
            msg.setReferences(inVal);
            Vector<String> outVal = msg.getReferences();
            for (int i = 0; i < 2; i++) {
                String inElem = inVal.get(i);
                String outElem = outVal.get(i);
                assert outElem.equals(inElem) : "\nreturned:\n '" + outElem + "'\nexpected:\n '" + inElem + "'";
            }
        });

        // TODO: use .equals()
        new TestUnit<CTXBase>("setKeywords() == getKeywords() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Vector<String> inVal = new Vector<String>();
            inVal.add("pEp-auto-consume@pEp.foundation");
            inVal.add("any_other_message_id@blabla.bubu");
            msg.setKeywords(inVal);
            Vector<String> outVal = msg.getKeywords();
            for (int i = 0; i < 2; i++) {
                String inElem = inVal.get(i);
                String outElem = outVal.get(i);
                assert outElem.equals(inElem) : "\nreturned:\n '" + outElem + "'\nexpected:\n '" + inElem + "'";
            }
        });

        // TODO: use .equals()
        new TestUnit<CTXBase>("setComments() == getComments() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            String inVal = "pEp-auto-consume@pEp.foundation";
            msg.setComments(inVal);
            String outVal = msg.getComments();
            assert outVal.equals(inVal) : "\nreturned:\n '" + outVal + "'\nexpected:\n '" + inVal + "'";
        });

        new TestUnit<CTXBase>("setOptFields() == getOptFields() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            ArrayList<Pair<String, String>> inVal = new ArrayList<>();
            inVal.add(new Pair<>("Received", "in time"));
            inVal.add(new Pair<>("X-Foobaz", "of course"));
            msg.setOptFields(inVal);
            ArrayList<Pair<String, String>> outVal = msg.getOptFields();
            assert AdapterTestUtils.optFieldsEqual(inVal, outVal) : "\nreturned:\n '" + outVal + "'\nexpected:\n '" + inVal + "'";
        });

        new TestUnit<CTXBase>("setEncFormat() == getEncFormat() ", new Jni134TestContext(), ctx -> {
            Message msg = new Message();
            Message.EncFormat inVal = Message.EncFormat.PEP;
            msg.setEncFormat(inVal);
            Message.EncFormat outVal = msg.getEncFormat();
            assert inVal == outVal : "\nreturned:\n '" + outVal + "'\nexpected:\n '" + inVal + "'";
        });

        TestSuite.getDefault().run();
    }
}


