import foundation.pEp.jniadapter.*;
import java.util.Vector;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.Thread;
import java.lang.InterruptedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Step2 {

    public static void main(String[] args) {
        Engine e = null;

        // load
        try {
            e = new Engine();
            SyncCallbacks callbacks = new SyncCallbacks();
            //e.setNotifyHandshakeCallback(callbacks);
            e.setMessageToSendCallback(callbacks);
        }
        catch (pEpException ex) {
            System.out.println("Cannot load");
            System.exit(-1);
        }
        System.out.println("Test loaded");

            
        e.startSync();
        
        //It should crash here. 
    
        try {
            Thread.sleep(200);
        }
        catch (InterruptedException ex) { }

        System.exit(0);
    }
}
