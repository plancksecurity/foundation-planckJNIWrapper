package foundation.pEp.jniadapter.test.utils.fsmsgqueue;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javafx.util.Pair;


public class FsMsgQueue implements Queue<String> {
    File qDir;

    public FsMsgQueue(String qDirPath) throws RuntimeException {
        qDir = new File(qDirPath);
        if (!qDir.exists()) {
            if (!qDir.mkdirs()) {
                throw new RuntimeException("Dir not existing, could not create:" + qDirPath);
            } else {
                // Dir created
            }
        } else {
            // Dir already exists
        }
        // Dir now definitely exists
    }

    @Override
    public int size() {
        return qDir.listFiles().length;
    }

    @Override
    public boolean isEmpty() {
        return size() <= 0;
    }

    @Override
    public boolean add(String msg) {
        boolean ret = true;
        String filename = UUID.randomUUID().toString() + ".msg";
        String path = qDir + "/" + filename;
        try {
            Files.write(Paths.get(path), msg.getBytes());
        } catch (IOException e) {
            log(e.toString());
        }
        return ret;
    }

    @Override
    public boolean offer(String msg) {
        boolean ret = true;
        try {
            add(msg);
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    private Pair<File, String> get() throws Exception {
        Pair<File, String> ret = null;
        ArrayList<File> files = listFilesByMtime(qDir);
        File oldestFile = null;
        if (files == null || (files.size() <= 0)) {
            throw new NoSuchElementException("Dir empty: " + qDir);
        } else {
            oldestFile = files.get(0);
            if(oldestFile != null) {
                String fContent = null;
                try {
                    fContent = readFile(oldestFile.toPath(), Charset.defaultCharset());
                } catch (Exception e) {
                    throw new Exception("Error reading file: " + oldestFile.getAbsolutePath());
                }
                if(fContent != null) {
                    ret = new Pair<>(oldestFile,fContent);
                } else {
                    throw new Exception("Error reading file: " + oldestFile.getAbsolutePath());
                }
            } else {
                throw new NoSuchElementException("Dir empty: " + qDir);
            }
        }
        return ret;
    }

    @Override
    public String remove() throws NoSuchElementException {
        String ret = null;
        Pair<File, String> pair = null;
        File file = null;
        try {
            pair = get();
        } catch (Exception e) {
            throw new NoSuchElementException(e.toString());
        }
        // Successful read
        // remove now
        if (pair != null ) {
            file = pair.getKey();
            ret = pair.getValue();
            if(file != null && ret != null) {
                file.delete();
                if (file.exists()) {
                    throw new RuntimeException("Cant remove msg from queue: " + file.getAbsolutePath());
                }
            } else {
                throw new NoSuchElementException("Unknown Error");
            }
        }
        return ret;
    }

    @Override
    public String poll() {
        String ret = null;
        try {
            ret = remove();
        } catch (Exception e) {
        }
        return ret;
    }

    @Override
    public String element() throws NoSuchElementException {
        String ret = null;
        Pair<File, String> pair = null;
        try {
           pair = get();
        } catch (Exception e) {
            throw new NoSuchElementException(e.toString());
        }
        if(pair != null) {
            ret = pair.getValue();
        } else {
            throw new NoSuchElementException("Unknown Error");
        }
        return ret;
    }

    @Override
    public String peek() {
        String ret = null;
        try {
            ret = element();
        } catch (Exception e) {
        }
        return ret;
    }


    @Override
    public boolean addAll(Collection<? extends String> c) {
        return false;
    }


    @Override
    public void clear() {

    }

    // Not implemented
    @Override
    public boolean contains(Object o) {
        return false;
    }

    // Not implemented
    @Override
    public Iterator<String> iterator() {
        return null;
    }

    // Not implemented
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    // Not implemented
    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    // Not implemented
    @Override
    public boolean remove(Object o) {
        return false;
    }

    // Not implemented
    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    // Not implemented
    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    // Not implemented
    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }


    //Math Utils
    private int clip(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private long clip(long val, long min, long max) {
        return Math.max(min, Math.min(max, val));
    }


    // File Utils

    // Possibly returns an empty ArrayList
    private ArrayList<File> listFilesByMtime(File dir) {
        ArrayList<File> ret = new ArrayList<>();
        File[] listOfFiles = dir.listFiles();
        if (listOfFiles != null) {
            Collections.addAll(ret, listOfFiles);
            ret = sortFilesByMtime(ret);
        }
        return ret;
    }

    // null in null out
    private ArrayList<File> sortFilesByMtime(ArrayList<File> files) {
        ArrayList<File> ret = null;
        if (files != null) {
            ret = new ArrayList(files);
            Collections.sort(ret, (o1, o2) -> {
                long ret1 = 0;
                ret1 = o1.lastModified() - o2.lastModified();
                return (int) clip(ret1, -1, 1);
            });
        }
        return ret;
    }

    static String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }

}


