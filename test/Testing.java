import org.pEp.jniadapter.*;
import java.util.Vector;
import java.net.URL;
import java.net.URLClassLoader;
class Testing {

    public void printClassPath() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
        	System.out.println(url.getFile());
        }
    }
    public static void main(String[] args) {
        Engine e;

        // load
        try {
            e = new Engine();
            SyncCallbacks callbacks = new SyncCallbacks();
            e.setNotifyHandshakeCallback(callbacks);
            e.setMessageToSendCallback(callbacks);        
        }
        catch (pEpException ex) {
            System.out.println("Cannot load");
            return;
        }
        System.out.println("Test loaded");

        // Keygen
        System.out.println("Generating keys: ");
        Identity user = new Identity();
        user.user_id = "pEp_own_userId";
        user.me = true;
        user.username = "Test User";
        user.address = "jniTestUser@peptest.ch";
        user = e.myself(user);
        System.out.print("Keys generated: ");
        System.out.println(user.fpr);

       
        // trustwords
        Identity vb = new Identity();
        vb.fpr = "DB4713183660A12ABAFA7714EBE90D44146F62F4";
        String t = e.trustwords(vb);
        System.out.print("Trustwords: ");
        System.out.println(t);

        // message
        Message msg = new Message();

        msg.setFrom(user);

        Vector<Identity> to = new Vector<Identity>();
        Identity to1 = new Identity();
        //to1.username = "Volker Birk";
        //to1.address = "vb@pep-project.org";
        //to1.address = "android01@peptest.ch";
        //to1.user_id = "42";
        //to.add(to1);
        to.add(user);
        msg.setTo(to);

        msg.setShortmsg("hello, world");
        msg.setLongmsg("this is a test");
        msg.setDir(Message.Direction.Outgoing);

        Message enc = null;
        try {
            enc = e.encrypt_message(msg, null, Message.EncFormat.PEP);
            System.out.println("encrypted");
        }
        catch (pEpException ex) {
            System.out.println("cannot encrypt");
            ex.printStackTrace();
        }

        
        System.out.println(enc.getLongmsg());
        Vector<Blob> attachments = enc.getAttachments();
        System.out.println(e.toUTF16(attachments.get(1).data));

        msg.setDir(Message.Direction.Outgoing);
        try {
            System.out.println("Rating preview: " + e.outgoing_message_rating_preview(msg));
            System.out.println("Rating" + e.outgoing_message_rating(msg));
        }
        catch (pEpException ex) {
            System.out.println("cannot measure outgoing message rating");
        }

        Engine.decrypt_message_Return result = null;
        try {
            result = e.decrypt_message(enc, new Vector<>(), 0);
            System.out.println("decrypted");
        }
        catch (pEpException ex) {
            System.out.println("cannot decrypt");
            ex.printStackTrace();
        }
        
        System.out.println(result.dst.getShortmsg());
        System.out.println(result.dst.getLongmsg());
	System.out.println("TEST DONE - FINISHED");

	try {
		e.key_reset(null, null);
	} 
	catch (pEpException ex) {
		System.out.println("cannot reset all own keys");
        	ex.printStackTrace();
	}
	System.exit(0);
    }

    
        
}

