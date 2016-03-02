package com.pep.k9;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.pEp.jniadapter.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context c = getApplicationContext();

        Log.d("PEPTEST", "Helper Setup");
        AndroidHelper.setup(c);
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
            try {
                //testPEpTypes();
                testPEpAliceBobJohn();
                //testKeyserverLookup();
                //testKeyGen();
            }
            catch (Exception ex) {
                Log.e("PEPTEST", "##################### TEST Exception ####################",ex);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
    public void testPEpTypes() throws pEpException, IOException, AssertionError {

        Engine e;

        Log.d("PEPTEST", "Test loaded");

        e = new Engine();

        Message msg = new Message();

        // Note : this looks like some target code, ins't it ?

        // TEST : Call getter before call to getter

        if(!(msg.getDir() == Message.Direction.Incoming)) throw new AssertionError();
        if(!(msg.getId()==null)) throw new AssertionError();
        if(!(msg.getLongmsg()==null)) throw new AssertionError();
        if(!(msg.getLongmsgFormatted()==null)) throw new AssertionError();
        if(!(msg.getAttachments()==null)) throw new AssertionError();
        if(!(msg.getSent()==null)) throw new AssertionError();
        if(!(msg.getRecv()==null)) throw new AssertionError();
        if(!(msg.getFrom()==null)) throw new AssertionError();
        if(!(msg.getTo()==null)) throw new AssertionError();
        if(!(msg.getRecvBy()==null)) throw new AssertionError();
        if(!(msg.getCc()==null)) throw new AssertionError();
        if(!(msg.getBcc()==null)) throw new AssertionError();
        if(!(msg.getInReplyTo()==null)) throw new AssertionError();
        if(!(msg.getReferences()==null)) throw new AssertionError();
        if(!(msg.getKeywords()==null)) throw new AssertionError();
        if(!(msg.getComments()==null)) throw new AssertionError();
        if(!(msg.getOptFields()==null)) throw new AssertionError();
        if(!(msg.getEncFormat()==Message.EncFormat.None)) throw new AssertionError();

        // TEST : Call setter with non-null
        // and check getter returns the same

        msg.setDir(Message.Direction.Outgoing);
        if(!(msg.getDir()==Message.Direction.Outgoing)) throw new AssertionError();

        msg.setId("1234ID");
        if(!(msg.getId().equals("1234ID"))) throw new AssertionError();


        msg.setShortmsg("ShrtMsg");
        if(!(msg.getShortmsg().equals("ShrtMsg"))) throw new AssertionError();

        msg.setLongmsg("Loooooooooooooonger Message");
        if(!(msg.getLongmsg().equals("Loooooooooooooonger Message"))) throw new AssertionError();

        msg.setLongmsgFormatted("<html/>");
        if(!(msg.getLongmsgFormatted().equals("<html/>"))) throw new AssertionError();

        {
            Vector<Blob> attachments = new Vector<Blob>();
            Blob blb = new Blob();
            blb.data = LoadAssetAsBuffer("0xC9C2EE39.asc");
            blb.filename = "0xC9C2EE39.asc";
            attachments.add(blb);
            msg.setAttachments(attachments);
            Vector<Blob> detach = msg.getAttachments();
            Blob dblb = detach.firstElement();
            if(!(dblb.filename.equals(blb.filename))) throw new AssertionError();
            if(!(Arrays.equals(dblb.data, blb.data))) throw new AssertionError();
        }

        {
            Date now = new Date();

            msg.setSent(now);
            Date res = msg.getSent();
            // Conversion rounds to the second, java's Date is in millisecond.
            if(!(java.lang.Math.abs(res.getTime() - now.getTime()) < 1000)) throw new AssertionError();
        }

        {
            Date now = new Date();

            msg.setRecv(now);
            Date res = msg.getRecv();
            // Conversion rounds to the second, java's Date is in millisecond.
            if(!(java.lang.Math.abs(res.getTime() - now.getTime()) < 1000)) throw new AssertionError();
        }

        {
            Identity alice = new Identity();
            alice.username = "Alice Test";
            alice.address = "pep.test.alice@pep-project.org";
            alice.user_id = "111";
            alice.me = true;
            alice.fpr = null;

            msg.setFrom(alice);
            Identity _alice = msg.getFrom();

            if(!(_alice.username.equals("Alice Test"))) throw new AssertionError();
            if(!(_alice.address.equals("pep.test.alice@pep-project.org"))) throw new AssertionError();
            if(!(_alice.user_id.equals("111"))) throw new AssertionError();
            if(!(_alice.me == true)) throw new AssertionError();
            if(!(_alice.fpr == null)) throw new AssertionError();
        }

        {
            Vector<Identity> rcpts = new Vector<Identity>();
            Identity alice = new Identity();
            alice.username = "Alice Test";
            alice.address = "pep.test.alice@pep-project.org";
            alice.user_id = "111";
            alice.me = true;
            alice.fpr = null;
            rcpts.add(alice);

            msg.setTo(rcpts);
            Vector<Identity> _rcpts = msg.getTo();
            Identity _alice = _rcpts.firstElement();

            if(!(_alice.username.equals("Alice Test"))) throw new AssertionError();
            if(!(_alice.address.equals("pep.test.alice@pep-project.org"))) throw new AssertionError();
            if(!(_alice.user_id.equals("111"))) throw new AssertionError();
            if(!(_alice.me == true)) throw new AssertionError();
            if(!(_alice.fpr == null)) throw new AssertionError();
        }

        {
            Identity alice = new Identity();
            alice.username = "Alice Test";
            alice.address = "pep.test.alice@pep-project.org";
            alice.user_id = "111";
            alice.me = true;
            alice.fpr = null;

            msg.setRecvBy(alice);
            Identity _alice = msg.getRecvBy();

            if(!(_alice.username.equals("Alice Test"))) throw new AssertionError();
            if(!(_alice.address.equals("pep.test.alice@pep-project.org"))) throw new AssertionError();
            if(!(_alice.user_id.equals("111"))) throw new AssertionError();
            if(!(_alice.me == true)) throw new AssertionError();
            if(!(_alice.fpr == null)) throw new AssertionError();
        }

        {
            Vector<Identity> rcpts = new Vector<Identity>();
            Identity alice = new Identity();
            alice.username = "Alice Test";
            alice.address = "pep.test.alice@pep-project.org";
            alice.user_id = "111";
            alice.me = true;
            alice.fpr = null;
            rcpts.add(alice);

            msg.setCc(rcpts);
            Vector<Identity> _rcpts = msg.getCc();
            Identity _alice = _rcpts.firstElement();

            if(!(_alice.username.equals("Alice Test"))) throw new AssertionError();
            if(!(_alice.address.equals("pep.test.alice@pep-project.org"))) throw new AssertionError();
            if(!(_alice.user_id.equals("111"))) throw new AssertionError();
            if(!(_alice.me == true)) throw new AssertionError();
            if(!(_alice.fpr == null)) throw new AssertionError();
        }

        {
            Vector<Identity> rcpts = new Vector<Identity>();
            Identity alice = new Identity();
            alice.username = "Alice Test";
            alice.address = "pep.test.alice@pep-project.org";
            alice.user_id = "111";
            alice.me = true;
            alice.fpr = null;
            rcpts.add(alice);

            msg.setBcc(rcpts);
            Vector<Identity> _rcpts = msg.getBcc();
            Identity _alice = _rcpts.firstElement();

            if(!(_alice.username.equals("Alice Test"))) throw new AssertionError();
            if(!(_alice.address.equals("pep.test.alice@pep-project.org"))) throw new AssertionError();
            if(!(_alice.user_id.equals("111"))) throw new AssertionError();
            if(!(_alice.me == true)) throw new AssertionError();
            if(!(_alice.fpr == null)) throw new AssertionError();
        }

        {
            Vector<String> strvec = new Vector<String>();
            strvec.add("Blub");

            msg.setInReplyTo(strvec);
            Vector<String> _strvec = msg.getInReplyTo();

            if(!(_strvec.firstElement().equals("Blub"))) throw new AssertionError();
        }

        {
            Vector<String> strvec = new Vector<String>();
            strvec.add("Blub");

            msg.setReferences(strvec);
            Vector<String> _strvec = msg.getReferences();

            if(!(_strvec.firstElement().equals("Blub"))) throw new AssertionError();
        }

        {
            Vector<String> strvec = new Vector<String>();
            strvec.add("Blub");

            msg.setKeywords(strvec);
            Vector<String> _strvec = msg.getKeywords();

            if(!(_strvec.firstElement().equals("Blub"))) throw new AssertionError();
        }

        msg.setComments("No comment.");
        if(!(msg.getComments().equals("No comment."))) throw new AssertionError();

        {
            ArrayList<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();
            Pair<String,String> pair = new Pair<String,String>("left","right");
            pairs.add(pair);

            msg.setOptFields(pairs);
            ArrayList<Pair<String, String>> _pairs = msg.getOptFields();
            Pair<String,String> _pair = _pairs.get(0);
            if(!(_pair.first.equals("left"))) throw new AssertionError();
            if(!(_pair.second.equals("right"))) throw new AssertionError();
        }

        msg.setEncFormat(Message.EncFormat.PEP);
        if(!(msg.getEncFormat()==Message.EncFormat.PEP)) throw new AssertionError();

        // TEST : Call setter with null and then call getter

        msg.setDir(null);
        if(!(msg.getDir() == Message.Direction.Incoming)) throw new AssertionError();

        msg.setId(null);
        if(!(msg.getId()==null)) throw new AssertionError();

        msg.setShortmsg(null);
        if(!(msg.getShortmsg()==null)) throw new AssertionError();

        msg.setLongmsg(null);
        if(!(msg.getLongmsg()==null)) throw new AssertionError();

        msg.setLongmsgFormatted(null);
        if(!(msg.getLongmsgFormatted()==null)) throw new AssertionError();

        msg.setAttachments(null);
        if(!(msg.getAttachments()==null)) throw new AssertionError();

        msg.setSent(null);
        if(!(msg.getSent()==null)) throw new AssertionError();

        msg.setRecv(null);
        if(!(msg.getRecv()==null)) throw new AssertionError();

        msg.setFrom(null);
        if(!(msg.getFrom()==null)) throw new AssertionError();

        msg.setTo(null);
        if(!(msg.getTo()==null)) throw new AssertionError();

        msg.setRecvBy(null);
        if(!(msg.getRecvBy()==null)) throw new AssertionError();

        msg.setCc(null);
        if(!(msg.getCc()==null)) throw new AssertionError();

        msg.setBcc(null);
        if(!(msg.getBcc()==null)) throw new AssertionError();

        msg.setInReplyTo(null);
        if(!(msg.getInReplyTo()==null)) throw new AssertionError();

        msg.setReferences(null);
        if(!(msg.getReferences()==null)) throw new AssertionError();

        msg.setKeywords(null);
        if(!(msg.getKeywords()==null)) throw new AssertionError();

        msg.setComments(null);
        if(!(msg.getComments()==null)) throw new AssertionError();

        msg.setOptFields(null);
        if(!(msg.getOptFields()==null)) throw new AssertionError();

        msg.setEncFormat(null);
        if(!(msg.getEncFormat()== Message.EncFormat.None)) throw new AssertionError();

        Log.d("PEPTEST", "Test finished");

        e.close();
    }

    public void testPEpAliceBobJohn() throws pEpException, IOException, AssertionError {
        Engine e;

        // load
        e = new Engine();

        Log.d("PEPTEST", "Test loaded");

        // trustwords
        Identity vb = new Identity();
        vb.fpr = "DB4713183660A12ABAFA7714EBE90D44146F62F4";
        String t = e.trustwords(vb);
        if(!(t.equals("BAPTISMAL BERTRAND DIVERSITY SCOTSWOMAN TRANSDUCER MIGNONNE CETACEAN AUSTRAL BIPARTISAN JINNAH"))) throw new AssertionError();

        // Our test user :
        // pEp Test Alice (test key don't use) <pep.test.alice@pep-project.org>
        //         6FF00E97
        // A9411D176FF00E97
        e.importKey(LoadAssetAsString("6FF00E97_sec.asc"));

        Identity alice = new Identity();
        alice.username = "Alice Test";
        alice.address = "pep.test.alice@pep-project.org";
        alice.user_id = "111";
        alice.me = true;
        alice.fpr = "4ABE3AAF59AC32CFE4F86500A9411D176FF00E97";
        e.myself(alice);

        //
        // Other peers :
        // pEp Test Bob (test key, don't use) <pep.test.bob@pep-project.org> 
        //         C9C2EE39
        // 59BFF488C9C2EE39
        e.importKey(LoadAssetAsString("0xC9C2EE39.asc"));

        Identity bob = new Identity();
        bob.username = "bob Test";
        bob.address = "pep.test.bob@pep-project.org";
        bob.user_id = "112";
        bob.fpr = "BFCDB7F301DEEEBBF947F29659BFF488C9C2EE39";
        e.updateIdentity(bob);

        //
        // pEp Test John (test key, don't use) <pep.test.john@pep-project.org>
        //         70DCF575
        // 135CD6D170DCF575
        e.importKey(LoadAssetAsString("0x70DCF575.asc"));

        Identity john = new Identity();
        john.username = "john Test";
        john.address = "pep.test.john@pep-project.org";
        john.user_id = "113";
        john.fpr = "AA2E4BEB93E5FE33DEFD8BE1135CD6D170DCF575";
        e.updateIdentity(john);

        // message
        Message msg = new Message();
        msg.setFrom(alice);

        Vector<Identity> to = new Vector<Identity>();
        to.add(bob);
        msg.setTo(to);

        msg.setShortmsg("hello, world");
        msg.setLongmsg("this is a test");

        msg.setDir(Message.Direction.Outgoing);

        Color aclr = e.outgoing_message_color(msg);
        if(!(aclr.equals(Color.pEpRatingReliable))) throw new AssertionError();

        e.trustPersonalKey(bob);
        Color bclr = e.outgoing_message_color(msg);
        if(!(bclr.equals(Color.pEpRatingTrusted))) throw new AssertionError();

        e.keyResetTrust(bob);
        Color cclr = e.outgoing_message_color(msg);
        if(!(cclr.equals(Color.pEpRatingReliable))) throw new AssertionError();

        e.keyCompromized(bob);
        Color dclr = e.outgoing_message_color(msg);
        if(!(dclr.equals(Color.pEpRatingUnencrypted))) throw new AssertionError();

        e.keyResetTrust(bob);
        Color oclr = e.outgoing_message_color(msg);
        if(!(oclr.equals(Color.pEpRatingReliable))) throw new AssertionError();

        Vector<Identity> cc = new Vector<Identity>();
        cc.add(alice);
        msg.setCc(cc);

        Vector<Identity> bcc = new Vector<Identity>();
        bcc.add(john);
        msg.setBcc(bcc);

        {
            ArrayList<Pair<String, String>> pairs = new ArrayList<Pair<String, String>>();
            pairs.add(new Pair<String,String>("Received","in time"));
            pairs.add(new Pair<String,String>("X-Foobaz","of course"));

            msg.setOptFields(pairs);
        }

        byte[] gif = LoadAssetAsBuffer("spinner.gif");
        byte[] png = LoadAssetAsBuffer("pep.png");
        byte[] tbz = LoadAssetAsBuffer("yml2.tar.bz2");
        {
            Vector<Blob> attachments = new Vector<Blob>();
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

        if(!(e.outgoing_message_color(msg).equals(Color.pEpRatingReliable))) throw new AssertionError();

        Message enc = null;
        enc = e.encrypt_message(msg, null);

        if(!(enc != null)) throw new AssertionError();

        if(!(enc.getShortmsg().equals("pEp"))) throw new AssertionError();
        if(!(enc.getLongmsg().contains("pEp-project.org"))) throw new AssertionError();

        Vector<Blob> attachments = enc.getAttachments();
        if(!(e.toUTF16(attachments.get(1).data).startsWith("-----BEGIN PGP MESSAGE-----"))) throw new AssertionError();

        Engine.decrypt_message_Return result = null;
        result = e.decrypt_message(enc);

        if(!(result.dst.getShortmsg().equals("hello, world"))) throw new AssertionError();
        if(!(result.dst.getLongmsg().equals("this is a test"))) throw new AssertionError();

        ArrayList<Pair<String, String>> _pairs = result.dst.getOptFields();
        /* FIXME ?
        {
            byte msk = 0;
            for (Pair<String, String> _pair : _pairs) {
                if (_pair.first.equals("Received")) {
                    if(!(_pair.second.equals("in time"))) throw new AssertionError();
                    msk |= 1;
                }else if (_pair.first.equals("X-Foobaz")) {
                    if(!(_pair.second.equals("of course"))) throw new AssertionError();
                    msk |= 2;
                }
            }
            if(!(msk == 3)) throw new AssertionError();
        }
        */

        {
            Vector<Blob> detach = msg.getAttachments();
            byte msk = 0;
            for (Blob dblb : detach) {
                if (dblb.filename.equals("pep.png")) {
                    if(!(Arrays.equals(dblb.data, png))) throw new AssertionError();
                    if(!(dblb.mime_type.equals("image/png"))) throw new AssertionError();
                    msk |= 1;
                }else if (dblb.filename.equals("spinner.gif")) {
                    if(!(Arrays.equals(dblb.data, gif))) throw new AssertionError();
                    if(!(dblb.mime_type.equals("image/gif"))) throw new AssertionError();
                    msk |= 2;
                }else if (dblb.filename.equals("yml2.tar.bz2")) {
                    if(!(Arrays.equals(dblb.data, tbz))) throw new AssertionError();
                    if(!(dblb.mime_type.equals("application/octet-stream"))) throw new AssertionError();
                    msk |= 4;
                }
            }
            if(!(msk == 7)) throw new AssertionError();
        }
        e.close();
    }

    public void testKeyserverLookup() throws pEpException, IOException, AssertionError, InterruptedException {

        Engine e;

        Log.d("PEPTEST", "Test keyserver lookup loaded");

        e = new Engine();

        e.startKeyserverLookup();

        Identity vb = new Identity();
        vb.username = "pEpDontAssert";
        vb.address = "vb@ulm.ccc.de";
        vb.user_id = "SsI6H9";
        e.updateIdentity(vb);

        int count = 0;
        while (count++ < 5000) {
            Thread.sleep(1);
        }

        String fpr = e.updateIdentity(vb).fpr;

        Log.d("PEPTEST", "keyserver test fpr");
        Log.d("PEPTEST", fpr != null ? fpr : "NULL");
        if(!(fpr != null)) throw new AssertionError();

        e.stopKeyserverLookup();

        e.close();
    }

    public void testKeyGen() throws pEpException, IOException, AssertionError, InterruptedException {

        Engine e;

        Log.d("PEPTEST", "Test gen key loaded");

        e = new Engine();

        Identity newid = new Identity();
        newid.username = "Name, User Name";
        newid.address = "test.gen.key@pep-project.org";
        newid.user_id = "P0l1231";

        String fpr = e.myself(newid).fpr;

        Log.d("PEPTEST", "keygen test fpr");
        Log.d("PEPTEST", fpr != null ? fpr : "NULL");
        if(!(fpr != null)) throw new AssertionError();

        e.close();
    }
}
