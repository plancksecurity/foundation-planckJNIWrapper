package org.pEp.jniadapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Scanner;

public class AndroidHelper {
    static {
        System.loadLibrary("pEpJNIAndroidHelper");
    }

    public static final String TAG = "AndroidHelper";

    private static native int setenv(String key, String value, boolean overwrite);
    private static native int nativeSetup(String debugflag);

    private static File homeDir;
    public static File gnupgHomeDir;
    private static File optDir;
    private static File versionFile;
    public static File binDir;
    public static File libDir;
    private static File tmpDir;

    // TODO : Increment when needed.
    public static int ENGINE_VERSION_CODE = 3052;

    private static File shareDir;

    private static final String dBFileName = "system.db";
    private static File dBfile;

    private static boolean already = false;

    public static void envSetup(Context c) {
        // "/opt" like dir to unpack GnuPG assets
        optDir = c.getDir("opt", Context.MODE_PRIVATE);

        // Add GnuPG's bin to PATH
        binDir = new File(optDir, "bin");
        setenv("PATH", System.getenv("PATH") + ":" + 
                       binDir.getAbsolutePath(), true);

        tmpDir = new File(c.getCacheDir(), "tmp");
        setenv("TEMP", tmpDir.getAbsolutePath(), true);

        // Tell dynamic loader where to find libs
        // Add GnuPG's bin to PATH
        String appLibDir = "";
        try {
            appLibDir = new File(c.getApplicationInfo().nativeLibraryDir).getCanonicalPath();
        } catch (Exception e) {
            e.printStackTrace();
            appLibDir = new File(c.getApplicationInfo().nativeLibraryDir).getAbsolutePath();
        }

        libDir = new File(optDir, "lib");
        setenv("LD_LIBRARY_PATH", appLibDir + ":" + 
                libDir.getAbsolutePath() + ":" +
                System.getenv("LD_LIBRARY_PATH"), true);

        // Set HOME environment variable pointing to 
        // something like "/data/data/app.package.name/home"
        // pEpEngine use it to find management DB and gpg home
        homeDir = c.getDir("home", Context.MODE_PRIVATE);
        gnupgHomeDir = new File(homeDir, ".gnupg");
        setenv("HOME", homeDir.getAbsolutePath(), true);
        setenv("GNUPGHOME", gnupgHomeDir.getAbsolutePath(), true);

        // pEpEngine need to find the safe words database
        shareDir = c.getDir("trustwords", Context.MODE_PRIVATE);
        dBfile = new File(shareDir, dBFileName);

        // TRUSTWORDS is absolute path of dir containig system.db
        setenv("TRUSTWORDS", shareDir.getAbsolutePath(), true);

        // Check version file retains latest installed version
        versionFile = new File(c.getFilesDir(), "VERSION");
    }

    public static void assetsSetup(Context c) {
        envSetup(c);
        boolean needUpgrade = needNewAssets();
        Log.i(TAG, "assetsSetup: " + needUpgrade);

        // If system.db still not here, then go get it in the assets.
        if (dBfile.exists() && needUpgrade){
            dBfile.delete();
        }
        if (!dBfile.exists()){
            assetFileExtract(c, dBFileName, shareDir);
        }

        // Copy GnuPG binaries
        if (optDir.exists() && needUpgrade){
            try {
                FileUtils.deleteDirectory(optDir);
            } catch (IOException e) {
                Log.e(TAG, "Couldn't delete existing gpg binaries");
            }
        }
        if (!optDir.exists()){
            optDir.mkdirs();
            assetPathExtract(c, "lib", optDir);
            assetPathExtract(c, "bin", optDir);
            assetPathExtract(c, "libexec", optDir);
            new File(optDir, "var/cache/gnupg").mkdirs();
            new File(optDir, "var/lib/gnupg").mkdirs();
            new File(optDir, "var/run/gnupg").mkdirs();
            chmod("0755", optDir, true);
        }

        // Fill version file
        setInstalledVersion(c);

        // Clean and creat tempdir
        if (tmpDir.exists()){
            try {
                FileUtils.deleteDirectory(tmpDir);
            } catch (IOException e) {
                Log.e(TAG, "Couldn't delete temp dir");
            }
        }
        tmpDir.mkdirs();
    }

    public static void nativeSetup(Context c) {
        // pre-load libs for pepengine, as
        // android cannot solve lib dependencies on its own
        System.loadLibrary("gpg-error");
        System.loadLibrary("assuan");
        System.loadLibrary("gpgme");

        if (BuildConfig.DEBUG) {
            // With lots of log
            nativeSetup("9:" + new File(c.getFilesDir(), "gpgme.log").getAbsolutePath());
        } else {
            // With almost no log
            nativeSetup("0:" + new File(c.getFilesDir(), "gpgme.log").getAbsolutePath());
        }

    }

    public static void setup(Context c) {
        if(!already){
            already = true;
            assetsSetup(c);
            nativeSetup(c);
        }
    }

    private static void assetPathExtract(Context c, String assetPath, File targetDir) {
        AssetManager assetManager = c.getAssets();

        try {
            String items[] = assetManager.list(assetPath);
            if (items.length > 0) {
                File newDir = new File(targetDir, new File(assetPath).getName());
                if (!newDir.exists())
                    newDir.mkdirs();
                for (int i = 0; i < items.length; ++i) {
                    assetPathExtract(c, new File(assetPath, items[i]).getPath(), newDir);
                }
            } else {
                assetFileExtract(c, assetPath, targetDir);
            }
        } catch (IOException ex) {
            Log.e(TAG, assetPath + " : ", ex);
        }
    }

    private static void assetFileExtract(Context c, String assetPath, File targetDir) {
        AssetManager assetManager = c.getAssets();

        try {

            InputStream inputStream = assetManager.open(assetPath);
            String targetFileName = 
                new File(targetDir, 
                         new File(assetPath).getName()).getAbsolutePath();
            OutputStream outputStream = new FileOutputStream(targetFileName);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();
            Log.i(TAG, "asset " + assetPath + " extracted as " + targetFileName);
        } catch (Exception e) {
            Log.e(TAG, assetPath + ": " + e.getMessage());
        }
    }

    private static int getInstalledVersion() {
        int versionCode = -1;
        if (versionFile.exists()){
            try {
                Scanner scan = new Scanner(versionFile);
                versionCode = Integer.parseInt(scan.next());
                scan.close();
            } catch (Exception e) {
                Log.e(TAG, "getInstalledVersion: " + e.getMessage());
            }
        }
        return versionCode;
    }

    private static void setInstalledVersion(Context context) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(versionFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(String.valueOf(ENGINE_VERSION_CODE) + "\n");
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "setInstalledVersion: " + e.getMessage());
        }
    }

    public static boolean needNewAssets() {
        return ENGINE_VERSION_CODE != getInstalledVersion();
    }

    // TODO: replace with native impl, less prone to failure.
    public static void chmod(String modestr, File path) {
        int err = 1;
        try {
            Class<?> fileUtils = Class.forName("android.os.FileUtils");
            Method setPermissions = fileUtils.getMethod("setPermissions", 
                    String.class, int.class, int.class, int.class);
            err = (Integer) setPermissions.invoke(
                    null, path.getAbsolutePath(),
                    Integer.parseInt(modestr, 8),
                    -1, -1);
            if (err != 0) {
                Log.i(TAG, "android.os.FileUtils.setPermissions() returned " + err
                        + " for '" + path + "'");
            }
        } catch (Exception e) {
            Log.i(TAG, "chmod:", e);
        } 
    }

    public static void chmod(String mode, File path, boolean recursive) {
        chmod(mode, path);
        if (recursive) {
            File[] paths = path.listFiles();
            for (File pth : paths) {
                if (pth.isDirectory()) {
                    chmod(mode, pth, true);
                } else {
                    chmod(mode, pth);
                }
            }
        }
    }
}
