package foundation.pEp.jniadapter.test.jni132;


import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.util.Arrays;
/*
JNI-132 - implement java.object.equals() for Blob

tests equality definition of Blob

equality definition:
- bytewise equality of data
- URI equality of filename
- bytewise equality of mime_type
*/

class CTXJNI132 extends CTXBase {
    public Blob attachment1MB;
    public Blob attachment10MB;

    @Override
    public CTXBase init() throws Throwable {
        attachment1MB = AdapterTestUtils.makeNewTestBlob(1000000, "att with size 1MB", null);
        attachment10MB = AdapterTestUtils.makeNewTestBlob(10000000, "att with size 10MB", null);

        return this;
    }
}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        // SAME
        new TestUnit<CTXJNI132>("Blob.equals() equality", new CTXJNI132(), ctx -> {
            Blob one = AdapterTestUtils.makeNewTestBlob("testBlob data", "testblobfilename", null);
            Blob two = AdapterTestUtils.makeNewTestBlob("testBlob data", "testblobfilename", null);
            assert one.equals(two) : "\n" + one.toString() + "\n" + "does not equal:\n" + two.toString();
        });

        new TestUnit<CTXJNI132>("Blob.equals() equality", new CTXJNI132(), ctx -> {
            Blob one = AdapterTestUtils.makeNewTestBlob("testBlob data", "file://testblobfilename", null);
            Blob two = AdapterTestUtils.makeNewTestBlob("testBlob data", "testblobfilename", null);
            assert one.equals(two) : "\n" + one.toString() + "\n" + "does not equal:\n" + two.toString();
        });

        new TestUnit<CTXJNI132>("Blob.equals() equality", new CTXJNI132(), ctx -> {
            Blob one = AdapterTestUtils.makeNewTestBlob(1000000, "testfilename", "anything goes");
            Blob two = new Blob();
            two.mime_type = new String(one.mime_type);
            two.filename = new String(one.filename);
            two.data = Arrays.copyOf(one.data, one.data.length);
            assert one.equals(two) : "\n" + one.toString() + "\n" + "does not equal:\n" + two.toString();
        });

        new TestUnit<CTXJNI132>("Blob.equals() equality", new CTXJNI132(), ctx -> {
            Blob one = AdapterTestUtils.makeNewTestBlob(1000000, "file://testfilename", "anything goes");
            Blob two = new Blob();
            two.mime_type = new String(one.mime_type);
            two.filename = new String(one.filename);
            two.data = Arrays.copyOf(one.data, one.data.length);
            assert one.equals(two) : "\n" + one.toString() + "\n" + "does not equal:\n" + two.toString();
        });

        // NOT SAME
        new TestUnit<CTXJNI132>("Blob.equals() - diff mime_type", new CTXJNI132(), ctx -> {
            Blob one = ctx.attachment1KB;
            Blob two = new Blob();
            two.mime_type = "diff";
            two.filename = ctx.attachment1KB.filename;
            two.data = ctx.attachment1KB.data;
            assert !one.equals(two) : "\n" + one.toString() + "\n" + "equals:\n" + two.toString();
        });

        new TestUnit<CTXJNI132>("Blob.equals() diff filename", new CTXJNI132(), ctx -> {
            Blob one = ctx.attachment1KB;
            Blob two = new Blob();
            two.mime_type = ctx.attachment1KB.mime_type;
            two.filename = "diff";
            two.data = ctx.attachment1KB.data;
            assert !one.equals(two) : "\n" + one.toString() + "\n" + "equals:\n" + two.toString();
        });

        new TestUnit<CTXJNI132>("Blob.equals() diff data", new CTXJNI132(), ctx -> {
            Blob one = ctx.attachment1KB;
            Blob two = new Blob();
            two.mime_type = ctx.attachment1KB.mime_type;
            two.filename = ctx.attachment1KB.filename;
            two.data = ctx.attachment1MB.data;
            assert !one.equals(two) : "\n" + one.toString() + "\n" + "equals:\n" + two.toString();
        });

        TestSuite.getDefault().run();
    }
}


