package org.pEp.jniadapter;

import android.content.Context;

public class AndroidHelper {
    static {
        System.loadLibrary("pEpJNIAndroidHelper");
    }

    private static native int setenv(String key, String value, boolean overwrite);

    public static void setup(Context c) {
        setenv("HOME", 
               c.getDir("home", Context.MODE_PRIVATE).getAbsolutePath(),
               true);
    }
}
