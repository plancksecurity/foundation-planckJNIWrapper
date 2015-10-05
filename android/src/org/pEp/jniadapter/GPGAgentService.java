package org.pEp.jniadapter;

import java.io.File;

import android.content.Intent;
import android.app.Service;
import android.util.Log;
import android.os.IBinder;

public class GPGAgentService extends Service {
    public static final String TAG = "GPGAgentService";
    private AgentProcessThread process;

    class AgentProcessThread extends Thread {

        @Override
        public void run() {
            Log.i(TAG, "execute GPG agent");
            try {
                Runtime.getRuntime().exec(
                    "gpg-agent" +
                    " --pinentry-program " + 
                    new File(AndroidHelper.binDir, "pinentry.sh").getAbsolutePath() +
                    " --no-detach" +
                    " --daemon --write-env-file" +
                    " --batch" + 
                    " --debug-level basic --log-file "
                    + new File(GPGAgentService.this.getFilesDir(), "gpg-agent.log")).waitFor();
                Log.i(TAG, "execution terminated");
            } catch (Exception e) {
                Log.e(TAG, "could not execute process", e);
            } finally {
                stopSelf();
                // eradicate process in critical section
                synchronized (GPGAgentService.this) {
                    process = null;
                }
            }
        }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        // Prepare environment for agent
        AndroidHelper.envSetup(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        // use critical section to avoid race conditions
        synchronized (this) {
            process = new AgentProcessThread();
            process.start();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // onBind() must return null, even if binder unused
        return null;
    }
}
