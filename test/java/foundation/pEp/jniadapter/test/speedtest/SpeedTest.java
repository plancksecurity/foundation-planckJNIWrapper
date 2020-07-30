package foundation.pEp.jniadapter.test.speedtest;

import java.text.ParseException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
import java.util.Scanner;
import foundation.pEp.jniadapter.*;

public class SpeedTest {
    private static Engine pEp = new Engine();
    private static MTMsgCodec codec = new MTMsgCodec(pEp);
    private static Identity me = new Identity(true);
    private static Identity you = new Identity();

    protected static void decodingTest(long n, String testDataEnc) {
        for (long i=0; i<n; ++i) {
            try {
                Message[] msgs = codec.decode(testDataEnc);
                Vector<String> keys = new Vector<String>();
                Engine.decrypt_message_Return ret = pEp.decrypt_message(msgs[0], keys, 0);
                String txt = ret.dst.getLongmsg();
            }
            catch (ParseException ex) {
                System.err.println("error: parsing test data");
                System.exit(3);
            }
        }
    }

    protected class DecodingThread extends Thread {
        private long _n;
        private String _testDataEnc;

        public DecodingThread(long n, String testDataEnc)
        {
            _n = n;
            _testDataEnc = testDataEnc;
        }

        public void run() {
            decodingTest(_n, _testDataEnc);
        }
    }

    private static Message encrypt(String data) {
        Message m = new Message();
        m.setDir(Message.Direction.Outgoing);
        m.setFrom(me);
        Vector<Identity> to = new Vector<Identity>();
        to.add(you);
        m.setTo(to);
        m.setLongmsg(data);
        return pEp.encrypt_message(m, null, Message.EncFormat.Inline);
    }

    protected static void encodingTest(long n, String testData) {
        for (long i=0; i<n; ++i) {
            Message enc = encrypt(testData);
            String txt = codec.encode(enc, null);
        }
    }

    protected class EncodingThread extends Thread {
        private long _n;
        private String _testData;

        public EncodingThread(long n, String testData)
        {
            _n = n;
            _testData = testData;
        }

        public void run() {
            encodingTest(_n, _testData);
        }
    }

    public static void main(String[] args) {
        long decoding = 0;
        long encoding = 0;
        int deth = 1;
        int enth = 1;

        MT999 testMessage = new MT999("232323232323", "424242424242", "O", "23", "", "Hello, world");
        String testData = testMessage.toString();

        for (int i=0; i<args.length; ++i) {
            if (args[i].compareTo("-h") == 0 || args[i].compareTo("--help") ==0)
            {
                System.out.println("SpeedTest [-e |--encode NUMBER] [-d | --decode NUMBER] [-f | --file TESTDATA] [-jd | --decoding-threads DT] [-je | --encoding-threads]  [-h | --help]\n"
                        + "\nEncodes and/or decodes messages to measure the speed.\n\n"
                        + " -d, --decode NUMBER         decode NUMBER messages\n"
                        + " -e, --encode NUMBER         encode NUMBER messages\n"
                        + " -f, --file TESTDATA         file with test data as UTF-8 encoded text\n"
                        + " -jd, --decoding-threads DT  starting DT threads for decoding\n"
                        + " -je, --encoding-threads ET  starting ET threads for encoding\n"
                        + " -h, --help                  show this help message\n"
                        + "\nThis program encrypts and encodes, and decrypts and decodes test data\n"
                        + "NUMBER times, respectively. If you omit -f it will encode a default data set.\n"
                );
                System.exit(0);
            }
            else if (args[i].compareTo("-d") == 0 || args[i].compareTo("--decode") == 0) {
                try {
                    decoding = Long.parseLong(args[i+1]);
                    ++i;
                }
                catch (NumberFormatException ex) {
                    System.err.println(String.format("error: decimal number expected but found %s", args[i+1]));
                    System.exit(1);
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println(String.format("error: %s is requiring a decimal number as argument", args[i]));
                    System.exit(1);
                }
            }
            else if (args[i].compareTo("-e") == 0 || args[i].compareTo("--encode") == 0) {
                try {
                    encoding = Long.parseLong(args[i+1]);
                    ++i;
                }
                catch (NumberFormatException ex) {
                    System.err.println(String.format("error: decimal number expected but found %s", args[i+1]));
                    System.exit(1);
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println(String.format("error: %s is requiring a decimal number as argument", args[i]));
                    System.exit(1);
                }
            }
            else if (args[i].compareTo("-f") == 0 || args[i].compareTo("--file") == 0) {
                String filename = "";

                try {
                    filename = args[i+1];
                    ++i;
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println(String.format("error: %s is requiring a filename as argument", args[i]));
                    System.exit(1);
                }

                try {
                    if (filename.compareTo("-") == 0) {
                        Scanner s = new Scanner(System.in).useDelimiter("\\A");
                        testData = s.hasNext() ? s.next() : "";
                    }
                    else {
                        testData = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
                    }

                }
                catch (Exception ex) {
                    System.err.println(String.format("error: cannot read file %s", args[i]));
                    System.exit(2);
                }
            }
            else if (args[i].compareTo("-jd") == 0 || args[i].compareTo("----decoding-threads") == 0) {
                try {
                    deth = Integer.parseInt(args[i+1]);
                    ++i;
                }
                catch (NumberFormatException ex) {
                    System.err.println(String.format("error: decimal number expected but found %s", args[i+1]));
                    System.exit(1);
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println(String.format("error: %s is requiring a decimal number as argument", args[i]));
                    System.exit(1);
                }
            }
            else if (args[i].compareTo("-je") == 0 || args[i].compareTo("----encoding-threads") == 0) {
                try {
                    enth = Integer.parseInt(args[i+1]);
                    ++i;
                }
                catch (NumberFormatException ex) {
                    System.err.println(String.format("error: decimal number expected but found %s", args[i+1]));
                    System.exit(1);
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println(String.format("error: %s is requiring a decimal number as argument", args[i]));
                    System.exit(1);
                }
            }
            else {
                System.err.println(String.format("illegal parameter: %s", args[i]));
                System.exit(1);
            }
        }

        if (decoding < 0 || encoding < 0 || !(encoding > 0 || decoding > 0)) {
            System.err.println("arguments error: -d or -e (or both) must be used with a positive number");
            System.exit(1);
        }

        if (deth < 1 || enth < 1 || deth > 1024 || enth > 1024) {
            System.err.println("arguments error: -jd or -je must be used with a positive number (max 1024)");
            System.exit(1);
        }

        me.address = MTMsgCodec.encodeBIC("232323232323");
        me.username = "sender";
        me.user_id = "23";
        pEp.myself(me);

        you.address = MTMsgCodec.encodeBIC("424242424242");
        you.username = "receiver";
        you.user_id = me.user_id;
        pEp.myself(you); // make a key for it

        Message enc = encrypt(testData);
        String testDataEnc = codec.encode(enc, null);

        long startTime = System.nanoTime();

        if (decoding > 0) {
            if (deth == 1) {
                decodingTest(decoding, testDataEnc);
            }
            else {
                SpeedTest st = new SpeedTest();
                Thread[] dts = new Thread[deth];
                for (int i=0; i < deth; ++i) {
                    dts[i] = st.new DecodingThread(decoding, testDataEnc);
                    dts[i].start();
                }
                for (int i=0; i < deth; ++i) {
                    try {
                        dts[i].join();
                    }
                    catch (InterruptedException ex) { }
                }
            }
        }

        long decodingTime = System.nanoTime();
        long decodingDelta = decodingTime - startTime;

        if (encoding > 0) {
            if (enth == 1) {
                encodingTest(decoding, testData);
            }
            else {
                SpeedTest st = new SpeedTest();
                Thread[] ets = new Thread[enth];
                for (int i=0; i < enth; ++i) {
                    ets[i] = st.new EncodingThread(encoding, testData);
                    ets[i].start();
                }
                for (int i=0; i < enth; ++i) {
                    try {
                        ets[i].join();
                    }
                    catch (InterruptedException ex) { }
                }
            }
        }

        long encodingDelta = System.nanoTime() - decodingTime;

        double ent = (double) encodingDelta / 1000000000;
        double det = (double) decodingDelta / 1000000000;

        double enr = (double) encoding / ent;
        double der = (double) decoding / det;

        System.out.println(String.format(
                "encrypted and encoded %d messages in %.3f sec. (%.1f msg./sec. per core)\n"
                        + "decrypted and decoded %d messages in %.3f sec. (%.1f msg./sec. per core)",
                encoding, ent, enr, decoding, det, der));
    }
}
