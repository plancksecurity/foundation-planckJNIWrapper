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
import java.util.Vector;

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
        // Handle action b§§ar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            try {
                testPEpAliceBobJohn();
                //testPEpTypes();
            }
            catch (Exception ex) {
                Log.e("PEPTEST", "##################### TEST Exception ####################",ex);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String LoadAssetAsString(String fname) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream input;

        input = assetManager.open(fname);

        int size = input.available();
        byte[] buffer = new byte[size];
        input.read(buffer);
        input.close();

        // byte buffer into a string
        return new String(buffer);

    }
    public void testPEpTypes() throws pEpException, IOException {

        Engine e;

        Log.d("PEPTEST", "Test loaded");

        e = new Engine();

        Message msg = new Message();

        // Call getter before call to getter
        assert msg.getDir()==null;
        assert msg.getId()==null;
        assert msg.getLongmsg()==null;
        assert msg.getLongmsg()==null;
        assert msg.getLongmsgFormatted()==null;
        assert msg.getAttachments()==null;
        assert msg.getSent()==null;
        assert msg.getRecv()==null;
        assert msg.getFrom()==null;
        assert msg.getTo()==null;
        assert msg.getRecvBy()==null;
        assert msg.getCc()==null;
        assert msg.getBcc()==null;
        assert msg.getInReplyTo()==null;
        assert msg.getReferences()==null;
        assert msg.getKeywords()==null;
        assert msg.getComments()==null;
        assert msg.getOptFields()==null;
        assert msg.getEncFormat()==null;

        // Call setter with null call to getter
        msg.setDir(null);
        assert msg.getDir()==null;

        msg.setId(null);
        assert msg.getId()==null;

        msg.setShortmsg(null);
        assert msg.getLongmsg()==null;

        msg.setLongmsg(null);
        assert msg.getLongmsg()==null;

        msg.setLongmsgFormatted(null);
        assert msg.getLongmsgFormatted()==null;

        msg.setAttachments(null);
        assert msg.getAttachments()==null;

        msg.setSent(null);
        assert msg.getSent()==null;

        msg.setRecv(null);
        assert msg.getRecv()==null;

        msg.setFrom(null);
        assert msg.getFrom()==null;

        msg.setTo(null);
        assert msg.getTo()==null;

        msg.setRecvBy(null);
        assert msg.getRecvBy()==null;

        msg.setCc(null);
        assert msg.getCc()==null;

        msg.setBcc(null);
        assert msg.getBcc()==null;

        msg.setInReplyTo(null);
        assert msg.getInReplyTo()==null;

        msg.setReferences(null);
        assert msg.getReferences()==null;

        msg.setKeywords(null);
        assert msg.getKeywords()==null;

        msg.setComments(null);
        assert msg.getComments()==null;

        msg.setOptFields(null);
        assert msg.getOptFields()==null;

        msg.setEncFormat(null);
        assert msg.getEncFormat()==null;

        Log.d("PEPTEST", "Test finished");
    }

    public void testPEpAliceBobJohn() throws pEpException, IOException {
        Engine e;

        // load
        e = new Engine();

        Log.d("PEPTEST", "Test loaded");

        // trustwords
        Identity vb = new Identity();
        vb.fpr = "DB4713183660A12ABAFA7714EBE90D44146F62F4";
        String t = e.trustwords(vb);
        System.out.print("Trustwords: ");
        Log.d("PEPTEST", t);

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
        to.add(john);
        msg.setTo(to);

        msg.setShortmsg("hello, world");
        msg.setLongmsg("this is a test");

        msg.setDir(Message.Direction.Outgoing);
        Log.d("PEPTEST", e.outgoing_message_color(msg).toString());

        Message enc = null;
        enc = e.encrypt_message(msg, null);

        if(enc != null) {
            Log.d("PEPTEST", "encrypted OK");
            Log.d("PEPTEST", enc.getLongmsg());
            Vector<Blob> attachments = enc.getAttachments();
            Log.d("PEPTEST", e.toUTF16(attachments.get(1).data));

            Engine.decrypt_message_Return result = null;
            result = e.decrypt_message(enc);
            Log.d("PEPTEST", "decrypted");

            Log.d("PEPTEST", result.dst.getShortmsg());
            Log.d("PEPTEST", result.dst.getLongmsg());
        } else {
            Log.d("PEPTEST", "NOT encrypted !!!");

        }

    }

}
