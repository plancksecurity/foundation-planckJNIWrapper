package com.pep.k9;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.pEp.jniadapter.AndroidHelper;
import org.pEp.jniadapter.Blob;
import org.pEp.jniadapter.Engine;
import org.pEp.jniadapter.Identity;
import org.pEp.jniadapter.Message;
import org.pEp.jniadapter.Pair;
import org.pEp.jniadapter.Rating;
import org.pEp.jniadapter.pEpException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String PASSED = "PASSED";
    public static final String TESTING = "TESTING";
    public static final String NOT_TESTED = "NOT TESTED";
    public static final String FAILED = "FAILED";
    public static final String TESTED = "TESTED";
    private String PEP_OWN_USER_ID = "pEp_own_userId";

    private StringBuilder text;
    private Integer testingTimes;
    private List<Identity> generatedIdentities;

    @BindView(R.id.content) ViewGroup rootView;
    private long outgoingColorAccumulative = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Context c = getApplicationContext();

        Dexter.initialize(getApplication());
        PermissionListener feedbackViewPermissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        };

        CompositePermissionListener storagePermissionListener = new CompositePermissionListener(feedbackViewPermissionListener,
                SnackbarOnDeniedPermissionListener.Builder.with(rootView, R.string.hello_world)
                        .withOpenSettingsButton("SETTINGS")
                        .build());
        Dexter.checkPermission(storagePermissionListener, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        generatedIdentities = new ArrayList<>();
        text = new StringBuilder();
        log("PEPTEST", "Helper Setup");
        AndroidHelper.setup(c);
    }

    private void writeToFile(String data) {
        text.append(data);
        text.append("\n");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void generateNoteOnSD(String filename) {
        try {
            File rootParent = new File(Environment.getExternalStorageDirectory(), "pEpTest");
            if (!rootParent.exists()) {
                rootParent.mkdirs();
            }

            File root = new File(rootParent.getAbsolutePath(), String.valueOf(new Date()));
            if (!root.exists()) {
                root.mkdirs();
            }

            File file = new File(getApplicationInfo().dataDir);
            copyDirectory(file, root);

            Log.d("file", root.getAbsolutePath());
            File filepath = new File(root, filename + ".txt");  // file path to save
            filepath.createNewFile();
            Log.d("file", filepath.getAbsolutePath());
            FileWriter writer = new FileWriter(filepath);
            writer.append(text.toString());
            writer.flush();
            writer.close();
            archiveDir(root.getAbsolutePath(), root.getName());
            removeDirectory(root);
            Toast.makeText(this, "Dump generated", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Dump not generated, give storage permissions", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void removeDirectory(File directory) {
        if (directory.isDirectory()) {
            String[] children = directory.list();
            for (String child : children) {
                File file = new File(directory, child);
                if (file.isDirectory()) {
                    removeDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    private void archiveDir(String path, String filename) {
        try {
            // Initiate ZipFile object with the path/name of the zip file.
            ZipFile zipFile = new ZipFile(path + "_" + filename + ".zip");

            // Folder to add
            String folderToAdd = path;

            // Initiate Zip Parameters which define various properties such
            // as compression method, etc.
            ZipParameters parameters = new ZipParameters();

            // set compression method to store compression
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

            // Set the compression level
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

            // Add folder to the zip file
            zipFile.addFolder(folderToAdd, parameters);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
            for (String child : children) {
                copyDirectory(new File(sourceLocation, child),
                        new File(targetLocation, child));
            }
        } else {

            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private void log(String tag, String added) {
        Log.d(tag, added);
        writeToFile(tag + " " + added);
    }

    private void logStart(String tag, String added) {
        added = "started at " + added;
        Log.d(tag, added);
        writeToFile(tag + " " + added);
    }

    private void logEnd(String tag, String added) {
        added = "completed in " + added;
        Log.d(tag, added);
        writeToFile(tag + " " + added);
    }

    @BindView(R.id.bRunTypes) Button runTypes;
    @BindView(R.id.bRunAliceBob) Button runIntegration;
    @BindView(R.id.bRunGenKey) Button runGenKey;
    @BindView(R.id.encrypt_and_decrypt) Button runEncryptAndDecrypt;
    @BindView(R.id.encrypt_and_decrypt_without_key) Button runEncryptAndDecryptWithoutKey;
    @BindView(R.id.ratings) Button runRatings;
    @BindView(R.id.black_list) Button runBlackList;
    @BindView(R.id.black_list_and_send) Button runBlackListAndSendMessage;
    @BindView(R.id.black_list_and_delete) Button runBlackListAndDelete;
    @BindView(R.id.unencrypted_subject) Button runUnencryptedSubject;
    @BindView(R.id.passive_mode) Button runPassiveMode;
    @BindView(R.id.message_from_me_green) Button runMessageFromMeIsGreen;
    @BindView(R.id.message_me) Button runMessageMe;
    @BindView(R.id.times_to_test) EditText timesToTest;
    @BindView(R.id.outgoing_color) Button runOutgoingColor;
    @BindView(R.id.identity_rating) Button runIdentityRating;
    @BindView(R.id.deblacklist) Button runDeblacklist;

    @OnClick(R.id.bRunTypes)
    public void runTypes() {
        runTypes.setText(TESTING);
        new RunTestTask().execute(1);
    }
    @OnClick(R.id.bRunAliceBob)
    public void runIntegration() {
        runIntegration.setText(TESTING);
        new RunTestTask().execute(6);
    }
    @OnClick(R.id.bRunGenKey)
    public void runGenKey() {
        runGenKey.setText(TESTING);
        new RunTestTask().execute(0);
    }
    @OnClick(R.id.encrypt_and_decrypt)
    public void runEncryptAndDecrypt() {
        runEncryptAndDecrypt.setText(TESTING);
        new RunTestTask().execute(3);
    }
    @OnClick(R.id.encrypt_and_decrypt_without_key)
    public void runEncryptAndDecryptWithoutKey() {
        runEncryptAndDecryptWithoutKey.setText(TESTING);
        new RunTestTask().execute(4);
    }
    @OnClick(R.id.ratings)
    public void runRatings() {
        runRatings.setText(TESTING);
        new RunTestTask().execute(5);
    }

    @OnClick(R.id.deblacklist)
    public void runDeblack() {
        runDeblacklist.setText(TESTING);
        new RunTestTask().execute(16);
    }

    @OnClick(R.id.test_everything)
    public void runAllTests() {
        Toast.makeText(this, "Testing started. Please, don't touch anything ò.ó", Toast.LENGTH_LONG).show();
        testingTimes = Integer.valueOf(timesToTest.getText().toString());
        runGenKey.setText(TESTING);
        new RunAllTestsTask().execute(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            startActivity(getIntent());
            return true;
        }

        if (id == R.id.action_unitary) {
            Intent intent = new Intent(this, UnitActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void runIntegrationTest() throws IOException, pEpException {
        integrationTest();
    }

    private void runEncryptAndDecryptTest() throws IOException, pEpException {
        encryptAndDecryptAMessage();
    }

    private void runDeblacklistRatingTest() throws IOException, pEpException {
        deblacklistRating();
    }

    private void runEncryptAndDecryptAMessageFromMyselft() throws IOException, pEpException {
        encryptAndDecryptAMessageFromMyselfTest();
    }

    private void runMessageForMeIsAlwaysGreenTest() throws IOException, pEpException {
        messageForMeIsAlwaysGreen();
    }

    private void runUnencryptedSubjectTest() throws IOException, pEpException {
        unencryptedSubjectTest();
    }

    private void runEncryptAndDecryptWithoutKeyTest() throws IOException, pEpException {
        encryptAndDecryptAMessageWithoutKey();
    }

    private void runColorRatingsTest() throws IOException, pEpException {
        ratingsTest();
    }

    private void runTestPEpTypes() throws IOException, pEpException {
        testPEpTypes();
    }

    private void runTestKeyGen() throws pEpException, InterruptedException, IOException {
        testKeyGen();
    }

    private void runAddToBlacklistTest() throws pEpException, InterruptedException, IOException {
        addToBlacklistTest();
    }

    private void runAddToBlacklistAndSendMessageTest() throws pEpException, InterruptedException, IOException {
        addToBlacklistAndSendMessageTest();
    }

    private void runAddAndRemoveFromBlacklistTest() throws pEpException, InterruptedException, IOException {
        addAndRemoveFromBlacklistTest();
    }

    private void runPassiveModeTest() throws pEpException, InterruptedException, IOException {
        passiveModeTest();
    }

    private void runOutgoingColorTest() throws pEpException, InterruptedException, IOException {
        testOutgoingColor();
    }

    private void runIdetntityRatingTest() throws pEpException, InterruptedException, IOException {
        testIdetntityRating();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private byte[] LoadAssetAsBuffer(String fname) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream input;

        input = assetManager.open(fname);

        int size = input.available();
        byte[] buffer = new byte[size];
        input.read(buffer);
        input.close();

        // byte buffer
        return buffer;

    }

    private String LoadAssetAsString(String fname) throws IOException {
        // byte buffer into a string
        return new String(LoadAssetAsBuffer(fname));
    }

    /*
    Just checks the correct creation of an email
     */
    public void testPEpTypes() throws pEpException, IOException, AssertionError {
        log("TEST: ", "Test pep types loaded");
        Engine engine;

        engine = new Engine();

        Message msg = new Message();

        // Note : this looks like some target code, ins't it ?

        // TEST : Call getter before call to getter

        if (!(msg.getDir() == Message.Direction.Incoming)) throw new AssertionError();
        if (!(msg.getId() == null)) throw new AssertionError();
        if (!(msg.getLongmsg() == null)) throw new AssertionError();
        if (!(msg.getLongmsgFormatted() == null)) throw new AssertionError();
        if (!(msg.getAttachments() == null)) throw new AssertionError();
        if (!(msg.getSent() == null)) throw new AssertionError();
        if (!(msg.getRecv() == null)) throw new AssertionError();
        if (!(msg.getFrom() == null)) throw new AssertionError();
        if (!(msg.getTo() == null)) throw new AssertionError();
        if (!(msg.getRecvBy() == null)) throw new AssertionError();
        if (!(msg.getCc() == null)) throw new AssertionError();
        if (!(msg.getBcc() == null)) throw new AssertionError();
        if (!(msg.getInReplyTo() == null)) throw new AssertionError();
        if (!(msg.getReferences() == null)) throw new AssertionError();
        if (!(msg.getKeywords() == null)) throw new AssertionError();
        if (!(msg.getComments() == null)) throw new AssertionError();
        if (!(msg.getOptFields() == null)) throw new AssertionError();
        if (!(msg.getEncFormat() == Message.EncFormat.None)) throw new AssertionError();

        // TEST : Call setter with non-null
        // and check getter returns the same

        msg.setDir(Message.Direction.Outgoing);
        if (!(msg.getDir() == Message.Direction.Outgoing)) throw new AssertionError();

        msg.setId("1234ID");
        if (!(msg.getId().equals("1234ID"))) throw new AssertionError();


        msg.setShortmsg("ShrtMsg");
        if (!(msg.getShortmsg().equals("ShrtMsg"))) throw new AssertionError();

        msg.setLongmsg("Loooooooooooooonger Message");
        if (!(msg.getLongmsg().equals("Loooooooooooooonger Message"))) throw new AssertionError();

        msg.setLongmsgFormatted("<html/>");
        if (!(msg.getLongmsgFormatted().equals("<html/>"))) throw new AssertionError();

        {
            Vector<Blob> attachments = new Vector<>();
            Blob blb = new Blob();
            blb.data = LoadAssetAsBuffer("0xC9C2EE39.asc");
            blb.filename = "0xC9C2EE39.asc";
            attachments.add(blb);
            msg.setAttachments(attachments);
            Vector<Blob> detach = msg.getAttachments();
            Blob dblb = detach.firstElement();
            if (!(dblb.filename.equals(blb.filename))) throw new AssertionError();
            if (!(Arrays.equals(dblb.data, blb.data))) throw new AssertionError();
        }

        {
            Date now = new Date();

            msg.setSent(now);
            Date res = msg.getSent();
            // Conversion rounds to the second, java's Date is in millisecond.
            if (!(java.lang.Math.abs(res.getTime() - now.getTime()) < 1000))
                throw new AssertionError();
        }

        {
            Date now = new Date();

            msg.setRecv(now);
            Date res = msg.getRecv();
            // Conversion rounds to the second, java's Date is in millisecond.
            if (!(java.lang.Math.abs(res.getTime() - now.getTime()) < 1000))
                throw new AssertionError();
        }

        {
            Identity alice = new Identity();
            alice.username = "Alice Test";
            alice.address = "pep.test.alice@pep-project.org";
            alice.user_id = PEP_OWN_USER_ID;
            alice.fpr = null;

            msg.setFrom(alice);
            Identity _alice = msg.getFrom();

            if (!(_alice.username.equals("Alice Test"))) throw new AssertionError();
            if (!(_alice.address.equals("pep.test.alice@pep-project.org")))
                throw new AssertionError();
            if (!(_alice.user_id.equals(PEP_OWN_USER_ID))) throw new AssertionError();
            if (!(Objects.equals(_alice.user_id, PEP_OWN_USER_ID))) throw new AssertionError();
            if (!(_alice.fpr == null)) throw new AssertionError();
        }

        {
            Vector<Identity> rcpts = new Vector<>();
            Identity alice = new Identity();
            alice.username = "Alice Test";
            alice.address = "pep.test.alice@pep-project.org";
            alice.user_id = PEP_OWN_USER_ID;
            alice.fpr = null;
            rcpts.add(alice);

            msg.setTo(rcpts);
            Vector<Identity> _rcpts = msg.getTo();
            Identity _alice = _rcpts.firstElement();

            if (!(_alice.username.equals("Alice Test"))) throw new AssertionError();
            if (!(_alice.address.equals("pep.test.alice@pep-project.org")))
                throw new AssertionError();
            if (!(_alice.user_id.equals(PEP_OWN_USER_ID))) throw new AssertionError();
            if (!(_alice.fpr == null)) throw new AssertionError();
        }

        {
            Identity alice = new Identity();
            alice.username = "Alice Test";
            alice.address = "pep.test.alice@pep-project.org";
            alice.user_id = PEP_OWN_USER_ID;
            alice.fpr = null;

            msg.setRecvBy(alice);
            Identity _alice = msg.getRecvBy();

            if (!(_alice.username.equals("Alice Test"))) throw new AssertionError();
            if (!(_alice.address.equals("pep.test.alice@pep-project.org")))
                throw new AssertionError();
            if (!(_alice.user_id.equals(PEP_OWN_USER_ID))) throw new AssertionError();
            if (!(_alice.fpr == null)) throw new AssertionError();
        }

        {
            Vector<Identity> rcpts = new Vector<>();
            Identity alice = new Identity();
            alice.username = "Alice Test";
            alice.address = "pep.test.alice@pep-project.org";
            alice.user_id = PEP_OWN_USER_ID;
            alice.fpr = null;
            rcpts.add(alice);

            msg.setCc(rcpts);
            Vector<Identity> _rcpts = msg.getCc();
            Identity _alice = _rcpts.firstElement();

            if (!(_alice.username.equals("Alice Test"))) throw new AssertionError();
            if (!(_alice.address.equals("pep.test.alice@pep-project.org")))
                throw new AssertionError();
            if (!(_alice.user_id.equals(PEP_OWN_USER_ID))) throw new AssertionError();
            if (!(_alice.fpr == null)) throw new AssertionError();
        }

        {
            Vector<Identity> rcpts = new Vector<>();
            Identity alice = new Identity();
            alice.username = "Alice Test";
            alice.address = "pep.test.alice@pep-project.org";
            alice.user_id = PEP_OWN_USER_ID;
            alice.fpr = null;
            rcpts.add(alice);

            msg.setBcc(rcpts);
            Vector<Identity> _rcpts = msg.getBcc();
            Identity _alice = _rcpts.firstElement();

            if (!(_alice.username.equals("Alice Test"))) throw new AssertionError();
            if (!(_alice.address.equals("pep.test.alice@pep-project.org")))
                throw new AssertionError();
            if (!(_alice.user_id.equals(PEP_OWN_USER_ID))) throw new AssertionError();
            if (!(_alice.fpr == null)) throw new AssertionError();
        }

        {
            Vector<String> strvec = new Vector<>();
            strvec.add("Blub");

            msg.setInReplyTo(strvec);
            Vector<String> _strvec = msg.getInReplyTo();

            if (!(_strvec.firstElement().equals("Blub"))) throw new AssertionError();
        }

        {
            Vector<String> strvec = new Vector<>();
            strvec.add("Blub");

            msg.setReferences(strvec);
            Vector<String> _strvec = msg.getReferences();

            if (!(_strvec.firstElement().equals("Blub"))) throw new AssertionError();
        }

        {
            Vector<String> strvec = new Vector<>();
            strvec.add("Blub");

            msg.setKeywords(strvec);
            Vector<String> _strvec = msg.getKeywords();

            if (!(_strvec.firstElement().equals("Blub"))) throw new AssertionError();
        }

        msg.setComments("No comment.");
        if (!(msg.getComments().equals("No comment."))) throw new AssertionError();

        {
            ArrayList<Pair<String, String>> pairs = new ArrayList<>();
            Pair<String, String> pair = new Pair<>("left", "right");
            pairs.add(pair);

            msg.setOptFields(pairs);
            ArrayList<Pair<String, String>> _pairs = msg.getOptFields();
            Pair<String, String> _pair = _pairs.get(0);
            if (!(_pair.first.equals("left"))) throw new AssertionError();
            if (!(_pair.second.equals("right"))) throw new AssertionError();
        }

        msg.setEncFormat(Message.EncFormat.PEP);
        if (!(msg.getEncFormat() == Message.EncFormat.PEP)) throw new AssertionError();

        // TEST : Call setter with null and then call getter

        msg.setDir(null);
        if (!(msg.getDir() == Message.Direction.Incoming)) throw new AssertionError();

        msg.setId(null);
        if (!(msg.getId() == null)) throw new AssertionError();

        msg.setShortmsg(null);
        if (!(msg.getShortmsg() == null)) throw new AssertionError();

        msg.setLongmsg(null);
        if (!(msg.getLongmsg() == null)) throw new AssertionError();

        msg.setLongmsgFormatted(null);
        if (!(msg.getLongmsgFormatted() == null)) throw new AssertionError();

        msg.setAttachments(null);
        if (!(msg.getAttachments() == null)) throw new AssertionError();

        msg.setSent(null);
        if (!(msg.getSent() == null)) throw new AssertionError();

        msg.setRecv(null);
        if (!(msg.getRecv() == null)) throw new AssertionError();

        msg.setFrom(null);
        if (!(msg.getFrom() == null)) throw new AssertionError();

        msg.setTo(null);
        if (!(msg.getTo() == null)) throw new AssertionError();

        msg.setRecvBy(null);
        if (!(msg.getRecvBy() == null)) throw new AssertionError();

        msg.setCc(null);
        if (!(msg.getCc() == null)) throw new AssertionError();

        msg.setBcc(null);
        if (!(msg.getBcc() == null)) throw new AssertionError();

        msg.setInReplyTo(null);
        if (!(msg.getInReplyTo() == null)) throw new AssertionError();

        msg.setReferences(null);
        if (!(msg.getReferences() == null)) throw new AssertionError();

        msg.setKeywords(null);
        if (!(msg.getKeywords() == null)) throw new AssertionError();

        msg.setComments(null);
        if (!(msg.getComments() == null)) throw new AssertionError();

        msg.setOptFields(null);
        if (!(msg.getOptFields() == null)) throw new AssertionError();

        msg.setEncFormat(null);
        if (!(msg.getEncFormat() == Message.EncFormat.None)) throw new AssertionError();

        engine.close();
        log("TEST: ", "Test pep types finished");
    }

    /*
    Tests the Ratings and Encrypts-Decrypts message
     */
    public void integrationTest() throws pEpException, IOException, AssertionError {
        log("TEST: ", "integration test loaded");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        // trustwords
        testTrustwords(engine);

        Identity alice = loadFromAliceFromEngine(engine);

        Identity bob = loadToBobFromEngine(engine);

        Identity john = loadJohnFromEngine(engine);

        // message
        Message msg = setupMessage(alice, bob);

        setupRatingToMyself(engine, msg);

        testRatingStatuses(engine, bob, msg);

        //Vector<Identity> bcc = new Vector<>();
        //bcc.add(john);
        //msg.setBcc(bcc);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("Received", "in time"));
        pairs.add(new Pair<>("X-Foobaz", "of course"));
        msg.setOptFields(pairs);

        byte[] gif = LoadAssetAsBuffer("spinner.gif");
        byte[] png = LoadAssetAsBuffer("pep.png");
        byte[] tbz = LoadAssetAsBuffer("yml2.tar.bz2");

        attachToMessage(msg, gif, png, tbz);

        encrypAndDecryptMessage(engine, msg);

        detachFromMessage(msg, gif, png, tbz);

        engine.close();
        log("TEST: ", "integration test finished");
    }

    public void addToBlacklistTest() throws pEpException, IOException, AssertionError {
        log("TEST: ", "blacklist test started");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        // trustwords
        testTrustwords(engine);

        Identity alice = loadFromAliceFromEngine(engine);

        String fingerprint = alice.fpr;
        addToBlacklistInEngine(engine, fingerprint);

        alice = myselfInEngine(engine, alice);

        //if (!fingerprint.equals(PEP_OWN_USER_ID)) {
        //    throw new AssertionError("fingerprint was " + fingerprint + " instead of PEP_OWN_USER_ID");
        //}

        removeFromBlacklistOnEngine(engine, alice.fpr);
        engine.close();
        log("TEST: ", "blacklist test finished");
    }

    public void testOutgoingColor() throws pEpException, IOException, AssertionError {
        log("TEST: ", "testOutgoingColor start");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        // trustwords
        testTrustwords(engine);

        Identity alice = loadFromAliceFromEngine(engine);

        Identity bob = loadToBobFromEngine(engine);

        // message
        Message msg = setupMessage(alice, bob);

        setupRatingToMyself(engine, msg);

        testRatingStatuses(engine, bob, msg);

        long time = System.currentTimeMillis();
        logStart("outgoing_message_rating", String.valueOf(time));
        engine.outgoing_message_rating(msg);
        long outgoingColorCalculus = System.currentTimeMillis() - time;
        outgoingColorAccumulative +=outgoingColorCalculus;
        logEnd("outgoing_message_rating", String.valueOf(outgoingColorCalculus));

        engine.close();
        log("TEST: ", "testOutgoingColor finished");
    }

    public void testIdetntityRating() throws pEpException, IOException, AssertionError {
        log("TEST: ", "testIdetntityRating start");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        // trustwords
        testTrustwords(engine);

        Identity alice = loadFromAliceFromEngine(engine);

        Identity bob = loadToBobFromEngine(engine);

        // message
        long time = System.currentTimeMillis();
        logStart("identity_rating", String.valueOf(time));
        engine.identity_rating(bob);
        long identityRatingCalculus = System.currentTimeMillis() - time;
        logEnd("identity_rating", String.valueOf(identityRatingCalculus));

        engine.close();
        log("TEST: ", "testIdetntityRating finished");
    }

    public void addToBlacklistAndSendMessageTest() throws pEpException, IOException, AssertionError {
        log("TEST: ", "blacklist + send message test started");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        // trustwords
        testTrustwords(engine);

        Identity aliceFrom = loadFromAliceFromEngine(engine);
        Identity bobTo = loadToBobFromEngine(engine);
        String fingerprint = bobTo.fpr;
        addToBlacklistInEngine(engine, fingerprint);
//        myselfInEngine(engine, aliceFrom);
  //      updateIdentityOnEngine(engine, bobTo);

        // message
        Message msg = setupMessage(aliceFrom, bobTo);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("Received", "in time"));
        pairs.add(new Pair<>("X-Foobaz", "of course"));
        msg.setOptFields(pairs);

        byte[] gif = LoadAssetAsBuffer("spinner.gif");
        byte[] png = LoadAssetAsBuffer("pep.png");
        byte[] tbz = LoadAssetAsBuffer("yml2.tar.bz2");

        attachToMessage(msg, gif, png, tbz);

        Message encriptedMessage;
        encriptedMessage = encryptMessageOnEngine(engine, msg);

        if (encriptedMessage != null) throw new AssertionError();

        //if (!(encriptedMessage.getShortmsg().equals("pEp"))) throw new AssertionError();
        //if (!(encriptedMessage.getLongmsg().contains("pEp-project.org")))
         //   throw new AssertionError();

        removeFromBlacklistOnEngine(engine, fingerprint);

        engine.close();
        log("TEST: ", "blacklist + send message test finished");
    }

    public void addAndRemoveFromBlacklistTest() throws pEpException, IOException, AssertionError {
        log("TEST: ", "blacklist + delete from blacklist test started");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        // trustwords
        testTrustwords(engine);

        Identity bob = loadToBobFromEngine(engine);

        String fingerprint = bob.fpr;
        addToBlacklistInEngine(engine, fingerprint);

        removeFromBlacklistOnEngine(engine, fingerprint);

        Boolean isBlacklisted = isBlacklistedOnEngine(engine, bob);

        if (isBlacklisted) {
            throw new AssertionError();
        }
        // message

        getBlackList(engine);

        engine.close();
        log("TEST: ", "blacklist + delete from blacklist finished");
    }

    private Vector<String> getBlackList(Engine engine) throws pEpException {
        long lastTime = System.currentTimeMillis();
        logStart("blacklist_retrieve", String.valueOf(lastTime));
        Vector<String> blacklist = engine.blacklist_retrieve();
        logEnd("blacklist_retrieve", String.valueOf(System.currentTimeMillis() - lastTime));
        return blacklist;
    }

    private Boolean isBlacklistedOnEngine(Engine engine, Identity bob) {
        long lastTime = System.currentTimeMillis();
        logStart("blacklist_is_listed", String.valueOf(lastTime));
        Boolean isBlacklisted = engine.blacklist_is_listed(bob.fpr);
        logEnd("blacklist_is_listed", String.valueOf(System.currentTimeMillis() - lastTime));
        return isBlacklisted;
    }

    private void removeFromBlacklistOnEngine(Engine engine, String fingerprint) {
        long lastTime = System.currentTimeMillis();
        logStart("blacklist_delete", String.valueOf(lastTime));
        engine.blacklist_delete(fingerprint);
        logEnd("backlist_delete", String.valueOf(System.currentTimeMillis() - lastTime));
    }

    private Identity myselfInEngine(Engine engine, Identity identity) {
        long lastTime = System.currentTimeMillis();
        logStart("engine.addToBlacklist", String.valueOf(lastTime));
        Identity myself = engine.myself(identity);
        logEnd("engine.addToBlacklist", String.valueOf(System.currentTimeMillis() - lastTime));
        return myself;
    }

    private void addToBlacklistInEngine(Engine engine, String fingerprint) {
        long lastTime = System.currentTimeMillis();
        logStart("engine.addToBlacklist", String.valueOf(lastTime));
        engine.blacklist_add(fingerprint);
        logEnd("engine.addToBlacklist", String.valueOf(System.currentTimeMillis() - lastTime));
    }

    private void ratingsTest() throws pEpException, IOException, AssertionError {
        log("TEST: ", "Test ratings loaded");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        // trustwords
        testTrustwords(engine);

        Identity alice = loadFromAliceFromEngine(engine);

        Identity bob = loadToBobFromEngine(engine);

        // message
        Message msg = setupMessage(alice, bob);

        setupRatingToMyself(engine, msg);

        testRatingStatuses(engine, bob, msg);

        engine.close();
        log("TEST: ", "Test ratings finished");
    }

    public void encryptAndDecryptAMessage() throws pEpException, IOException, AssertionError {
        log("TEST: ", "Test encrypt and decrypt loaded");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        Identity alice = loadFromAliceFromEngine(engine);

        Identity bob = loadToBobFromEngine(engine);

        // message
        Message msg = setupMessage(alice, bob);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("Received", "in time"));
        pairs.add(new Pair<>("X-Foobaz", "of course"));
        msg.setOptFields(pairs);

        encrypAndDecryptMessage(engine, msg);

        engine.close();
        log("TEST: ", "Test encrypt and decrypt finished");
    }

    public void deblacklistRating() throws pEpException, IOException, AssertionError {
        log("TEST: ", "Test deblacklistRating loaded");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        Identity alice = loadFromAliceFromEngine(engine);

        Identity bob = loadToBobFromEngine(engine);

        removeFromBlacklistOnEngine(engine, bob.fpr);
        // message
        Message msg = setupMessage(alice, bob);

        log("Test deblacklistRating after remove blacklist", getOutgoingMessageRatingFromEngine(engine, msg).name());

        if (!(getOutgoingMessageRatingFromEngine(engine, msg).value >= Rating.pEpRatingReliable.value)) {
            throw new AssertionError();
        }

        String fingerprint = bob.fpr;
        addToBlacklistInEngine(engine, fingerprint);

        Message msgBlacklisted = setupMessage(alice, bob);

        log("Test deblacklistRating after blacklist", getOutgoingMessageRatingFromEngine(engine, msgBlacklisted).name());

        if (getOutgoingMessageRatingFromEngine(engine, msgBlacklisted).value > 4) {
            throw new AssertionError();
        }

        removeFromBlacklistOnEngine(engine, fingerprint);

        Message msgDeBlacklisted = setupMessage(alice, bob);

        log("Test deblacklistRating after remove blacklist", getOutgoingMessageRatingFromEngine(engine, msg).name());
        if (!(getOutgoingMessageRatingFromEngine(engine, msgDeBlacklisted).equals(Rating.pEpRatingReliable))) {
            throw new AssertionError();
        }

        engine.close();
        log("TEST: ", "Test deblacklistRating finished");
    }

    public void encryptAndDecryptAMessageFromMyselfTest() throws pEpException, IOException, AssertionError {
        log("TEST: ", "Test encrypt and decrypt from myself loaded");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        Identity alice = loadFromAliceFromEngine(engine);

        // message
        Message msg = setupMessage(alice, alice);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("Received", "in time"));
        pairs.add(new Pair<>("X-Foobaz", "of course"));
        msg.setOptFields(pairs);

        Message encryptedMessage;
        encryptedMessage = encryptMessageOnEngine(engine, msg);

        if (encryptedMessage == null) throw new AssertionError();

        if (!(encryptedMessage.getShortmsg().equals("p≡p"))) throw new AssertionError();
        if (!(encryptedMessage.getLongmsg().contains("pEp-project.org")))
            throw new AssertionError();

        Vector<Blob> attachments = encryptedMessage.getAttachments();
        if (!(Engine.toUTF16(attachments.get(1).data).startsWith("-----BEGIN PGP MESSAGE-----")))
            throw new AssertionError();

        Engine.decrypt_message_Return result = null;
        decryptMessageOnEngine(engine, encryptedMessage);

        engine.close();
        log("TEST: ", "Test encrypt and decrypt from myself finished");
    }

    public void messageForMeIsAlwaysGreen() throws pEpException, IOException, AssertionError {
        log("TEST: ", "Test message from me is green loaded");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        Identity alice = loadFromAliceFromEngine(engine);

        // message
        Message msg = setupMessage(alice, alice);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("Received", "in time"));
        pairs.add(new Pair<>("X-Foobaz", "of course"));
        msg.setOptFields(pairs);

        Engine.decrypt_message_Return decrypt_message_return = encrypAndDecryptMessage(engine, msg);

        if(decrypt_message_return.rating.value < 6) {
            throw new AssertionError();
        }

        engine.close();
        log("TEST: ", "Test message from me is green finished");
    }

    public void unencryptedSubjectTest() throws pEpException, IOException, AssertionError {
        log("TEST: ", "Test unencrypted subject loaded");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        Identity alice = loadFromAliceFromEngine(engine);

        Identity bob = loadToBobFromEngine(engine);
        updateIdentityOnEngine(engine, bob);
        // message
        Message msg = setupMessage(alice, bob);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("Received", "in time"));
        pairs.add(new Pair<>("X-Foobaz", "of course"));
        msg.setOptFields(pairs);

        long time = System.currentTimeMillis();
        logStart("config_unencrypted_subject", String.valueOf(time));
        engine.config_unencrypted_subject(true);
        logEnd("config_unencrypted_subject", String.valueOf(System.currentTimeMillis() - time));

        Message encryptedMessage;
        encryptedMessage = encryptMessageOnEngine(engine, msg);

        if (!encryptedMessage.getShortmsg().equals(msg.getShortmsg())) throw new AssertionError();

        engine.close();
        log("TEST: ", "Test unencrypted subject finished");
    }

    public void passiveModeTest() throws pEpException, IOException, AssertionError {
        log("TEST: ", "Test passive mode loaded");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        Identity alice = loadFromAliceFromEngine(engine);

        Identity bob = loadToBobFromEngine(engine);

        // message
        Message msg = setupMessage(alice, bob);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("Received", "in time"));
        pairs.add(new Pair<>("X-Foobaz", "of course"));
        msg.setOptFields(pairs);

        long time = System.currentTimeMillis();
        logStart("config_passive_mode", String.valueOf(time));
        engine.config_passive_mode(true);
        logEnd("config_passive_mode", String.valueOf(System.currentTimeMillis()));

        if (msg.getAttachments() != null) throw new AssertionError();

        engine.close();
        log("TEST: ", "Test passive mode finished");
    }

    public void encryptAndDecryptAMessageWithoutKey() throws pEpException, IOException, AssertionError {
        log("TEST: ", "Test encrypt and decrypt without key loaded");
        long lastTime = System.currentTimeMillis();
        Engine engine;
        engine = new Engine();
        log("engine.new Engine()", String.valueOf(System.currentTimeMillis() - lastTime));

        Identity alice = loadFromAliceFromEngine(engine);

        Identity bob = loadBobFromEngineWithoutKey(engine);

        // message
        Message msg = setupMessage(alice, bob);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("Received", "in time"));
        pairs.add(new Pair<>("X-Foobaz", "of course"));
        msg.setOptFields(pairs);

        encrypAndDecryptMessage(engine, msg);

        if (msg.getAttachments() == null) {
            throw new AssertionError();
        }

        engine.close();
        log("TEST: ", "Test encrypt and decrypt without key finished");
    }

    private void testTrustwords(Engine engine) {
        Identity vb = new Identity();
        vb.fpr = "DB4713183660A12ABAFA7714EBE90D44146F62F4";
        String t = engine.trustwords(vb);
        if (!(t.equals("BAPTISMAL BERTRAND DIVERSITY SCOTSWOMAN TRANSDUCER MIGNONNE CETACEAN AUSTRAL BIPARTISAN JINNAH")))
            throw new AssertionError();
    }

    @NonNull
    private Identity loadJohnFromEngine(Engine engine) throws IOException {
        //
        // pEp Test John (test key, don't use) <pep.test.john@pep-project.org>
        //         70DCF575
        // 135CD6D170DCF575
        importKeyFromEngine(engine, "0x70DCF575.asc");

        Identity john = new Identity();
        john.username = "john Test";
        john.address = "pep.test.john@pep-project.org";
        john.user_id = "113";
        john.fpr = "AA2E4BEB93E5FE33DEFD8BE1135CD6D170DCF575";
        updateIdentityOnEngine(engine, john);
        return john;
    }

    @NonNull
    private Identity loadToBobFromEngine(Engine engine) throws IOException {
        //
        // Other peers :
        // pEp Test Bob (test key, don't use) <pep.test.bob@pep-project.org> 
        //         C9C2EE39
        // 59BFF488C9C2EE39
        importKeyFromEngine(engine, "0xC9C2EE39.asc");

        Identity bob = new Identity();
        bob.username = "bob Test";
        bob.address = "pep.test.bob@pep-project.org";
        bob.user_id = "112";
        bob.fpr = "BFCDB7F301DEEEBBF947F29659BFF488C9C2EE39";

        updateIdentityOnEngine(engine, bob);
        log("bob fpr", bob.fpr);
        return bob;
    }

    @NonNull
    private Identity loadBobFromEngineWithoutKey(Engine engine) throws IOException {
        //
        // Other peers :
        // pEp Test Bob (test key, don't use) <pep.test.bob@pep-project.org> 
        //         C9C2EE39
        // 59BFF488C9C2EE39
        Identity bob = new Identity();
        bob.username = "bob Test";
        bob.address = "pep.test.bob@pep-project.org";
        bob.user_id = "112";

        updateIdentityOnEngine(engine, bob);
        return bob;
    }

    private void updateIdentityOnEngine(Engine engine, Identity identity) {
        long lastTime = System.currentTimeMillis();
        logStart("engine.updateIdentity", String.valueOf(lastTime));
        engine.updateIdentity(identity);
        logEnd("engine.updateIdentity", String.valueOf(System.currentTimeMillis() - lastTime));
    }

    private void importKeyFromEngine(Engine engine, String filename) throws IOException {
        long lastTime = System.currentTimeMillis();
        logStart("engine.importKey", String.valueOf(lastTime));
        engine.importKey(LoadAssetAsString(filename));
        logEnd("engine.importKey", String.valueOf(System.currentTimeMillis() - lastTime));
    }

    @NonNull
    private Identity loadFromAliceFromEngine(Engine engine) throws IOException {
        // Our test user :
        // pEp Test Alice (test key don't use) <pep.test.alice@pep-project.org>
        //         6FF00E97
        // A9411D176FF00E97
        importKeyFromEngine(engine, "6FF00E97_sec.asc");

        Identity alice = new Identity();
        alice.username = "Alice Test";
        alice.address = "pep.test.alice@pep-project.org";
        alice.user_id = PEP_OWN_USER_ID;
        alice.fpr = "4ABE3AAF59AC32CFE4F86500A9411D176FF00E97";

        long lastTime = System.currentTimeMillis();
        alice = myselfInEngine(engine, alice);
        log("engine.myself", String.valueOf(System.currentTimeMillis() - lastTime));

        log("alice fpr", alice.fpr);
        return alice;
    }

    @NonNull
    private Message setupMessage(Identity alice, Identity bob) {
        Message msg = new Message();
        msg.setFrom(alice);

        Vector<Identity> to = new Vector<>();
        to.add(bob);
        msg.setTo(to);

        msg.setShortmsg("hello, world");
        msg.setLongmsg("this is a test");

        msg.setDir(Message.Direction.Outgoing);

        Vector<Identity> cc = new Vector<>();
        cc.add(alice);
        msg.setCc(cc);

        return msg;
    }

    private void setupRatingToMyself(Engine engine, Message msg) throws pEpException {
        Rating aliceRating = getOutgoingMessageRatingFromEngine(engine, msg);
        if (!(aliceRating.equals(Rating.pEpRatingReliable))) throw new AssertionError("Alice rating was "+ aliceRating.name() + " instead of reliable");
    }

    private synchronized Rating getOutgoingMessageRatingFromEngine(Engine engine, Message msg) throws pEpException {
        long lastTime = System.currentTimeMillis();
        logStart("engine.outgoing", String.valueOf(lastTime));
        Rating rating = engine.outgoing_message_rating(msg);
        logEnd("engine.outgoing", String.valueOf(System.currentTimeMillis() - lastTime));
        return rating;
    }

    private void testRatingStatuses(Engine engine, Identity bob, Message msg) throws pEpException {
        trustKeyOnEngine(engine, bob);
        Rating bobTrustRating = getOutgoingMessageRatingFromEngine(engine, msg);
        if (!(bobTrustRating.equals(Rating.pEpRatingTrusted))) throw new AssertionError();

        resetKeyOnEngine(engine, bob);
        Rating bobResetRating = getOutgoingMessageRatingFromEngine(engine, msg);
        if (!(bobResetRating.equals(Rating.pEpRatingReliable))) throw new AssertionError();

        mistrustJeyOnEngine(engine, bob);
        Rating bobMistrustRating = getOutgoingMessageRatingFromEngine(engine, msg);
        if (!(bobMistrustRating.equals(Rating.pEpRatingUnencrypted))) throw new AssertionError();

        resetKeyOnEngine(engine, bob);
        Rating bobResetTrustAgain = getOutgoingMessageRatingFromEngine(engine, msg);
        if (!(bobResetTrustAgain.equals(Rating.pEpRatingReliable))) throw new AssertionError();
    }

    private void mistrustJeyOnEngine(Engine engine, Identity bob) {
        long lastTime = System.currentTimeMillis();
        logStart("engine.keyMistrusted", String.valueOf(lastTime));
        engine.keyMistrusted(bob);
        logEnd("engine.keyMistrusted", String.valueOf(System.currentTimeMillis() - lastTime));
    }

    private void resetKeyOnEngine(Engine engine, Identity bob) {
        long lastTime = System.currentTimeMillis();
        logStart("engine.keyResetTrust", String.valueOf(lastTime));
        engine.keyResetTrust(bob);
        logEnd("engine.keyResetTrust", String.valueOf(System.currentTimeMillis() - lastTime));
    }

    private void trustKeyOnEngine(Engine engine, Identity bob) {
        long lastTime = System.currentTimeMillis();
        logStart("engine.trustPersonalKey", String.valueOf(lastTime));
        engine.trustPersonalKey(bob);
        logEnd("engine.trustPersonalKey", String.valueOf(System.currentTimeMillis() - lastTime));
    }

    @NonNull
    private Engine.decrypt_message_Return encrypAndDecryptMessage(Engine engine, Message msg) throws pEpException {
        Message encryptedMessage;
        encryptedMessage = encryptMessageOnEngine(engine, msg);

        if (encryptedMessage == null) throw new AssertionError();

        if (!(encryptedMessage.getShortmsg().equals("p≡p"))) throw new AssertionError("short message was " + encryptedMessage.getShortmsg());
        if (!(encryptedMessage.getLongmsg().contains("pEp-project.org")))
            throw new AssertionError();

        Vector<Blob> attachments = encryptedMessage.getAttachments();
        if (!(Engine.toUTF16(attachments.get(1).data).startsWith("-----BEGIN PGP MESSAGE-----")))
            throw new AssertionError();

        Engine.decrypt_message_Return result;
        result = decryptMessageOnEngine(engine, encryptedMessage);

        if (!(result.dst.getShortmsg().equals("hello, world"))) throw new AssertionError();
        if (!(result.dst.getLongmsg().equals("this is a test"))) throw new AssertionError();
        return result;
    }

    private Engine.decrypt_message_Return decryptMessageOnEngine(Engine engine, Message encriptedMessage) throws pEpException {
        long lastTime = System.currentTimeMillis();
        logStart("engine.decrypt_message", String.valueOf(lastTime));
        Engine.decrypt_message_Return decrypt_message_return = engine.decrypt_message(encriptedMessage, 0);
        logEnd("engine.decrypt_message", String.valueOf(System.currentTimeMillis() - lastTime));
        return decrypt_message_return;
    }

    private Message encryptMessageOnEngine(Engine engine, Message msg) throws pEpException {
        long lastTime = System.currentTimeMillis();
        logStart("engine.encrypt_message", String.valueOf(lastTime));
        Message message = engine.encrypt_message(msg, null, Message.EncFormat.PEP);
        logEnd("engine.encrypt_message", String.valueOf(System.currentTimeMillis() - lastTime));
        return message;
    }

    private void detachFromMessage(Message msg, byte[] gif, byte[] png, byte[] tbz) {
        Vector<Blob> detach = msg.getAttachments();
        byte msk = 0;
        for (Blob dblb : detach) {
            switch (dblb.filename) {
                case "pep.png":
                    if (!(Arrays.equals(dblb.data, png))) throw new AssertionError();
                    if (!(dblb.mime_type.equals("image/png"))) throw new AssertionError();
                    msk |= 1;
                    break;
                case "spinner.gif":
                    if (!(Arrays.equals(dblb.data, gif))) throw new AssertionError();
                    if (!(dblb.mime_type.equals("image/gif"))) throw new AssertionError();
                    msk |= 2;
                    break;
                case "yml2.tar.bz2":
                    if (!(Arrays.equals(dblb.data, tbz))) throw new AssertionError();
                    if (!(dblb.mime_type.equals("application/octet-stream")))
                        throw new AssertionError();
                    msk |= 4;
                    break;
            }
        }
        if (!(msk == 7)) throw new AssertionError();
    }

    private void attachToMessage(Message msg, byte[] gif, byte[] png, byte[] tbz) {
        Vector<Blob> attachments = new Vector<>();
        {
            Blob b = new Blob();
            b.data = png;
            b.filename = "pep.png";
            b.mime_type = "image/png";
            attachments.add(b);
        }
        {
            Blob b = new Blob();
            b.data = gif;
            b.filename = "spinner.gif";
            b.mime_type = "image/gif";
            attachments.add(b);
        }
        {
            Blob b = new Blob();
            b.data = tbz;
            b.filename = "yml2.tar.bz2";
            b.mime_type = "application/octet-stream";
            attachments.add(b);
        }
        msg.setAttachments(attachments);
    }

    /*
    tests I can get my own fingerprint
     */
    public void testKeyGen() throws pEpException, IOException, AssertionError, InterruptedException {
        log("TEST: ", "Test key generation loaded");

        Engine e;
        e = new Engine();

        Identity newid = new Identity();
        newid.username = "Name, User Name";
        newid.address = "test.gen.key@pep-project.org";
        newid.user_id = "P0l1231";

        int count = 0;
        while (count++ < 5000) {
            Thread.sleep(1);
        }

        Identity identity = myselfInEngine(e, newid);
        generatedIdentities.add(identity);

        String fpr = identity.fpr;

        log("PEPTEST", "keygen test fpr");
        log("PEPTEST", fpr != null ? fpr : "NULL");if (fpr == null) throw new AssertionError();

        e.close();
        log("TEST: ", "Test key generation finished");
    }

    private Set<Integer> executedTasks = new HashSet<>();

    @SuppressLint("StaticFieldLeak")
    private class RunAllTestsTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {
            Integer integer = integers[0];
            try {
                switch (integer) {
                    case 0:
                        if (!executedTasks.contains(0)) {
                            /*
                            for (int i = 0; i< testingTimes; i++) {
                                runTestKeyGen();
                            }
                            */
                            executedTasks.add(0);
                            return 0;
                        }
                        break;
                    case 1:
                        if (!executedTasks.contains(1)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runTestPEpTypes();
                            }
                            executedTasks.add(1);
                            return 1;
                        }
                        break;
                    case 2:
                        if (!executedTasks.contains(2)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runTestPEpTypes();
                            }
                            executedTasks.add(2);
                            return 2;
                        }
                        break;
                    case 3:
                        if (!executedTasks.contains(3)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runEncryptAndDecryptTest();
                            }
                            executedTasks.add(3);
                            return 3;
                        }
                        break;
                    case 4:
                        if (!executedTasks.contains(4)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runEncryptAndDecryptWithoutKeyTest();
                            }
                            executedTasks.add(4);
                            return 4;
                        }
                        break;
                    case 5:
                        if (!executedTasks.contains(5)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runColorRatingsTest();
                            }
                            executedTasks.add(5);
                            return 5;
                        }
                        break;
                    case 6:
                        if (!executedTasks.contains(6)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runIntegrationTest();
                            }
                            executedTasks.add(6);
                            return 6;
                        }
                        break;
                    case 7:
                        if (!executedTasks.contains(7)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runAddToBlacklistTest();
                            }
                            executedTasks.add(7);
                            return 7;
                        }
                        break;
                    case 8:
                        if (!executedTasks.contains(8)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runAddToBlacklistAndSendMessageTest();
                            }
                            executedTasks.add(8);
                            return 8;
                        }
                        break;
                    case 9:
                        if (!executedTasks.contains(9)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runAddAndRemoveFromBlacklistTest();
                            }
                            executedTasks.add(9);
                            return 9;
                        }
                        break;
                    case 10:
                        if (!executedTasks.contains(10)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runUnencryptedSubjectTest();
                            }
                            executedTasks.add(10);
                            return 10;
                        }
                        break;
                    case 11:
                        if (!executedTasks.contains(11)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runPassiveModeTest();
                            }
                            executedTasks.add(11);
                            return 11;
                        }
                        break;
                    case 12:
                        if (!executedTasks.contains(12)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runEncryptAndDecryptAMessageFromMyselft();
                            }
                            executedTasks.add(12);
                            return 12;
                        }
                        break;
                    case 13:
                        if (!executedTasks.contains(13)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runMessageForMeIsAlwaysGreenTest();
                            }
                            executedTasks.add(13);
                            return 13;
                        }
                        break;
                    case 14:
                        if (!executedTasks.contains(14)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runOutgoingColorTest();
                            }
                            log("outgoing average", String.valueOf(outgoingColorAccumulative /testingTimes));
                            executedTasks.add(14);
                            return 14;
                        }
                        break;
                    case 15:
                        if (!executedTasks.contains(15)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runIdetntityRatingTest();
                            }
                            executedTasks.add(15);
                            return 15;
                        }
                        break;
                    case 16:
                        if (!executedTasks.contains(16)) {
                            for (int i = 0; i< testingTimes; i++) {
                                runDeblacklistRatingTest();
                            }
                            executedTasks.add(16);
                            return 16;
                        }
                        break;
                }
            } catch (AssertionError | Exception ex) {
                Log.e("PEPTEST", "##################### TEST Exception ####################", ex);
                publishProgress(integers);
            }
            return -1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("RunAllTestsTask", "onPreExecute " + "Starting test");
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case 0:
                    runGenKey.setText(NOT_TESTED);
                    runTypes.setText(TESTING);
                    new RunAllTestsTask().execute(1);
                    break;
                case 1:
                    runTypes.setText(PASSED);
                    runLookup.setText(TESTING);
                    new RunAllTestsTask().execute(2);
                    break;
                case 2:
                    runLookup.setText(PASSED);
                    runEncryptAndDecrypt.setText(TESTING);
                    new RunAllTestsTask().execute(3);
                    break;
                case 3:
                    runEncryptAndDecrypt.setText(PASSED);
                    runEncryptAndDecryptWithoutKey.setText(TESTING);
                    new RunAllTestsTask().execute(4);
                    break;
                case 4:
                    runEncryptAndDecryptWithoutKey.setText(PASSED);
                    runRatings.setText(TESTING);
                    new RunAllTestsTask().execute(5);
                    break;
                case 5:
                    runRatings.setText(PASSED);
                    runIntegration.setText(TESTING);
                    new RunAllTestsTask().execute(6);
                    break;
                case 6:
                    runIntegration.setText(PASSED);
                    runBlackList.setText(TESTING);
                    new RunAllTestsTask().execute(7);
                    break;
                case 7:
                    runBlackList.setText(PASSED);
                    runBlackListAndSendMessage.setText(TESTING);
                    new RunAllTestsTask().execute(8);
                    break;
                case 8:
                    runBlackListAndSendMessage.setText(PASSED);
                    runBlackListAndDelete.setText(TESTING);
                    new RunAllTestsTask().execute(9);
                    break;
                case 9:
                    runBlackListAndDelete.setText(PASSED);
                    runUnencryptedSubject.setText(TESTING);
                    new RunAllTestsTask().execute(10);
                    break;
                case 10:
                    runUnencryptedSubject.setText(PASSED);
                    runPassiveMode.setText(TESTING);
                    new RunAllTestsTask().execute(11);
                    break;
                case 11:
                    runPassiveMode.setText(PASSED);
                    runMessageMe.setText(TESTING);
                    new RunAllTestsTask().execute(12);
                    break;
                case 12:
                    runMessageMe.setText(PASSED);
                    runMessageFromMeIsGreen.setText(TESTING);
                    new RunAllTestsTask().execute(13);
                    break;
                case 13:
                    runMessageFromMeIsGreen.setText(PASSED);
                    runOutgoingColor.setText(TESTING);
                    new RunAllTestsTask().execute(14);
                    break;
                case 14:
                    runOutgoingColor.setText(PASSED);
                    runIdentityRating.setText(TESTING);
                    new RunAllTestsTask().execute(15);
                    break;
                case 15:
                    runIdentityRating.setText(PASSED);
                    runDeblacklist.setText(TESTING);
                    new RunAllTestsTask().execute(16);
                    break;
                case 16:
                    runDeblacklist.setText(PASSED);
                    generateNoteOnSD("dump_test_engine");
                    break;
            }
            Log.i("RunAllTestsTask", "onPostExecute " + "Ended test");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]) {
                case 0:
                    runGenKey.setText(FAILED);
                    new RunAllTestsTask().execute(1);
                    break;
                case 1:
                    runLookup.setText(FAILED);
                    new RunAllTestsTask().execute(2);
                    break;
                case 2:
                    runLookup.setText(FAILED);
                    new RunAllTestsTask().execute(3);
                    break;
                case 3:
                    runEncryptAndDecryptWithoutKey.setText(FAILED);
                    new RunAllTestsTask().execute(4);
                    break;
                case 4:
                    runEncryptAndDecrypt.setText(FAILED);
                    new RunAllTestsTask().execute(5);
                    break;
                case 5:
                    runEncryptAndDecryptWithoutKey.setText(FAILED);
                    new RunAllTestsTask().execute(6);
                case 6:
                    runRatings.setText(FAILED);
                    new RunAllTestsTask().execute(7);
                    break;
                case 7:
                    runBlackList.setText(FAILED);
                    new RunAllTestsTask().execute(8);
                    break;
                case 8:
                    runBlackListAndSendMessage.setText(FAILED);
                    new RunAllTestsTask().execute(9);
                    break;
                case 9:
                    runBlackListAndDelete.setText(FAILED);
                    new RunAllTestsTask().execute(10);
                    break;
                case 10:
                    runUnencryptedSubject.setText(FAILED);
                    new RunAllTestsTask().execute(11);
                    break;
                case 11:
                    runMessageMe.setText(FAILED);
                    new RunAllTestsTask().execute(12);
                    break;
                case 12:
                    runMessageMe.setText(FAILED);
                    new RunAllTestsTask().execute(13);
                    break;
                case 13:
                    runMessageFromMeIsGreen.setText(FAILED);
                    new RunAllTestsTask().execute(14);
                    break;
                case 14:
                    runMessageFromMeIsGreen.setText(FAILED);
                    new RunAllTestsTask().execute(14);
                    break;
                case 15:
                    runIdentityRating.setText(FAILED);
                    new RunAllTestsTask().execute(15);
                    break;
                case 16:
                    runDeblacklist.setText(FAILED);
                    generateNoteOnSD("dump_test_engine");
                    break;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RunTestTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... integers) {
            Integer integer = integers[0];
            try {
                switch (integer) {
                    case 0:
                        runTestKeyGen();
                        return 0;
                    case 1:
                        runTestPEpTypes();
                        return 1;
                    case 2:
                        runEncryptAndDecryptTest();
                        return 3;
                    case 3:
                        runEncryptAndDecryptWithoutKeyTest();
                        return 4;
                    case 4:
                        runColorRatingsTest();
                        return 5;
                    case 5:
                        runIntegrationTest();
                        return 6;
                    case 16:
                        runDeblacklistRatingTest();
                        return 6;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("RunAllTestsTask", "onPreExecute " + "Starting test");
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            switch (integer) {
                case 0:
                    runGenKey.setText(TESTED);
                    break;
                case 1:
                    runTypes.setText(TESTED);
                    break;
                case 2:
                    runLookup.setText(TESTED);
                    break;
                case 3:
                    runEncryptAndDecrypt.setText(TESTED);
                    break;
                case 4:
                    runEncryptAndDecryptWithoutKey.setText(TESTED);
                    break;
                case 5:
                    runRatings.setText(TESTED);
                    break;
                case 6:
                    runIntegration.setText(TESTED);
                    break;
                case 16:
                    runDeblacklist.setText(TESTED);
                    break;
            }
            Log.i("RunAllTestsTask", "onPostExecute " + "Ended test");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

}
