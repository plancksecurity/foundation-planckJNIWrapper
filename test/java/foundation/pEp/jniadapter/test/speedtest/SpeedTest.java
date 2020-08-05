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

    private static long decodingCount = 0;
    private static long encodingCount = 0;
    private static int deth = 0;
    private static int enth = 0;

    private static String testData = null;


    protected static void decodingTest(Engine eng, long n, String testDataEnc) {
        for (long i = 0; i < n; ++i) {
            try {
                Message[] msgs = codec.decode(testDataEnc);
                Vector<String> keys = new Vector<String>();
                Engine.decrypt_message_Return ret = eng.decrypt_message(msgs[0], keys, 0);
                String txt = ret.dst.getLongmsg();
            } catch (ParseException ex) {
                System.err.println("error: parsing test data");
                System.exit(3);
            }
        }
    }

    protected class DecodingThread extends Thread {
        private long _n;
        private String _testDataEnc;
        private Engine _localpEp;

        public DecodingThread(long n, String testDataEnc) {
            _n = n;
            _testDataEnc = testDataEnc;
            _localpEp = new Engine();
        }

        public void run() {
            decodingTest(_localpEp, _n, _testDataEnc);
        }
    }

    private static Message encrypt(Engine eng, String data) {
        Message m = new Message();
        m.setDir(Message.Direction.Outgoing);
        m.setFrom(me);
        Vector<Identity> to = new Vector<Identity>();
        to.add(you);
        m.setTo(to);
        m.setLongmsg(data);
        return eng.encrypt_message(m, null, Message.EncFormat.Inline);
    }

    protected static void encodingTest(Engine eng, long n, String testData) {
        for (long i = 0; i < n; ++i) {
            Message enc = encrypt(eng, testData);
            String txt = codec.encode(enc, null);
        }
    }

    protected class EncodingThread extends Thread {
        private long _n;
        private String _testData;
        private Engine _localpEp;

        public EncodingThread(long n, String testData) {
            _n = n;
            _testData = testData;
            _localpEp = new Engine();
        }

        public void run() {
            encodingTest(_localpEp, _n, _testData);
        }
    }

    private static void parseOpts(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].compareTo("-h") == 0 || args[i].compareTo("--help") == 0) {
                System.out.println("SpeedTest [-e |--encode NUMBER] [-d | --decode NUMBER] [-f | --file TESTDATA] [-jd | --decoding-threads DT] [-je | --encoding-threads]  [-h | --help]\n"
                        + "\nEncodes and/or decodes messages to measure the speed.\n\n"
                        + " -d, --decode NUMBER         decode NUMBER messages per thread\n"
                        + " -e, --encode NUMBER         encode NUMBER messages per thread\n"
                        + " -f, --file TESTDATA         file with test data as UTF-8 encoded text\n"
                        + " -jd, --decoding-threads DT  starting DT threads for decoding\n"
                        + " -je, --encoding-threads ET  starting ET threads for encoding\n"
                        + " -h, --help                  show this help message\n"
                        + "\nThis program encrypts and encodes, and decrypts and decodes test data\n"
                        + "NUMBER times, respectively. If you omit -f it will encode a default data set.\n"
                );
                System.exit(0);
            } else if (args[i].compareTo("-d") == 0 || args[i].compareTo("--decode") == 0) {
                try {
                    decodingCount = Long.parseLong(args[i + 1]);
                    ++i;
                } catch (NumberFormatException ex) {
                    System.err.println(String.format("error: decimal number expected but found %s", args[i + 1]));
                    System.exit(1);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println(String.format("error: %s is requiring a decimal number as argument", args[i]));
                    System.exit(1);
                }
            } else if (args[i].compareTo("-e") == 0 || args[i].compareTo("--encode") == 0) {
                try {
                    encodingCount = Long.parseLong(args[i + 1]);
                    ++i;
                } catch (NumberFormatException ex) {
                    System.err.println(String.format("error: decimal number expected but found %s", args[i + 1]));
                    System.exit(1);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println(String.format("error: %s is requiring a decimal number as argument", args[i]));
                    System.exit(1);
                }
            } else if (args[i].compareTo("-f") == 0 || args[i].compareTo("--file") == 0) {
                String filename = "";

                try {
                    filename = args[i + 1];
                    ++i;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println(String.format("error: %s is requiring a filename as argument", args[i]));
                    System.exit(1);
                }

                try {
                    if (filename.compareTo("-") == 0) {
                        Scanner s = new Scanner(System.in).useDelimiter("\\A");
                        testData = s.hasNext() ? s.next() : "";
                    } else {
                        testData = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
                    }

                } catch (Exception ex) {
                    System.err.println(String.format("error: cannot read file %s", args[i]));
                    System.exit(2);
                }
            } else if (args[i].compareTo("-jd") == 0 || args[i].compareTo("----decoding-threads") == 0) {
                try {
                    deth = Integer.parseInt(args[i + 1]);
                    ++i;
                } catch (NumberFormatException ex) {
                    System.err.println(String.format("error: decimal number expected but found %s", args[i + 1]));
                    System.exit(1);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println(String.format("error: %s is requiring a decimal number as argument", args[i]));
                    System.exit(1);
                }
            } else if (args[i].compareTo("-je") == 0 || args[i].compareTo("----encoding-threads") == 0) {
                try {
                    enth = Integer.parseInt(args[i + 1]);
                    ++i;
                } catch (NumberFormatException ex) {
                    System.err.println(String.format("error: decimal number expected but found %s", args[i + 1]));
                    System.exit(1);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.err.println(String.format("error: %s is requiring a decimal number as argument", args[i]));
                    System.exit(1);
                }
            } else {
                System.err.println(String.format("illegal parameter: %s", args[i]));
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Initializing...");
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println(String.format("Number of cores: %d", cores));

        encodingCount = 10;
        decodingCount = 10;
        enth = cores;
        deth = cores;

        MT999 testMessage = new MT999("232323232323", "424242424242", "O", "23", "", "Hello, world");
        testData = testMessage.toString();

        parseOpts(args);

        if (decodingCount < 0 || encodingCount < 0 || !(encodingCount > 0 || decodingCount > 0)) {
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

        Message enc = encrypt(pEp, testData);
        String testDataEnc = codec.encode(enc, null);

        Thread[] dts = new Thread[deth];
        Thread[] ets = new Thread[enth];

        System.out.println("Creating " + deth + " decoding threads");

        // create threads
        if (deth > 1) {
            SpeedTest st = new SpeedTest();
            for (int i = 0; i < deth; ++i) {
                dts[i] = st.new DecodingThread(decodingCount, testDataEnc);
            }
        }

        System.out.println("Creating " + enth + " encoding threads");
        if (enth > 1) {
            SpeedTest st = new SpeedTest();
            for (int i = 0; i < enth; ++i) {
                ets[i] = st.new EncodingThread(encodingCount, testData);
            }
        }


        // Benchmark starting
        System.out.println("Starting benchmark...");
        System.out.println("decoding " + decodingCount + " msgs per thread");
        long startTime = System.nanoTime();

        if (deth == 1) {
            decodingTest(pEp, decodingCount, testDataEnc);
        } else {
            SpeedTest st = new SpeedTest();
            for (int i = 0; i < deth; ++i) {
                dts[i].start();
            }
            for (int i = 0; i < deth; ++i) {
                try {
                    dts[i].join();
                } catch (InterruptedException ex) {
                }
            }
        }


        long decodingTime = System.nanoTime();
        long decodingDelta = decodingTime - startTime;

        System.out.println("encoding " + decodingCount + " msgs per thread");
        if (enth == 1) {
            encodingTest(pEp, decodingCount, testData);
        } else {
            SpeedTest st = new SpeedTest();
            for (int i = 0; i < enth; ++i) {
                ets[i].start();
            }
            for (int i = 0; i < enth; ++i) {
                try {
                    ets[i].join();
                } catch (InterruptedException ex) {
                }
            }
        }

        long encodingDelta = System.nanoTime() - decodingTime;

        double encTimeSecs = (double) encodingDelta / 1000000000;
        double decTimeSecs = (double) decodingDelta / 1000000000;

        long encTotal = encodingCount * enth;
        long decTotal = decodingCount * deth;

        double enr = (double) encTotal / encTimeSecs;
        double der = (double) decTotal / decTimeSecs;

        System.out.println(String.format(
                "encrypted and encoded %d messages in %.3f sec. (%.1f msgs/sec) using %d threads\n"
                        + "decrypted and decoded %d messages in %.3f sec. (%.1f msgs/sec) using %d threads",
                encTotal, encTimeSecs, enr, enth, decTotal, decTimeSecs, der, deth));
    }
}
