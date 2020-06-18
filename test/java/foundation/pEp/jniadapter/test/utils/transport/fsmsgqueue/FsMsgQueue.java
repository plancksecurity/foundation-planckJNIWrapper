package foundation.pEp.jniadapter.test.utils.transport.fsmsgqueue;

import foundation.pEp.pitytest.utils.Pair;

import static foundation.pEp.pitytest.TestLogger.*;
import static foundation.pEp.pitytest.utils.TestUtils.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.*;



public class FsMsgQueue implements Queue<String> {
    File qDir;
    Charset fileEncoding;

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
//        log("Available Charsets: " + getAvailableCharsetNames());
        fileEncoding = Charset.forName("UTF-8");
        log("Using charset: " + fileEncoding.name());
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
    public boolean add(String msg) throws NullPointerException, IllegalStateException {
        boolean ret = true;
        String readBack = null;
        if (msg != null) {
            String filename = getNewFilename();
            String path = qDir + "/" + filename;
            // check file not existing yet.
//            log("Adding msg file:" + filename);
            File file = new File(path);

            if(!file.exists()) {
                try {
                    writeFile(file.toPath(), msg, fileEncoding);
                    readBack = readFile(Paths.get(path), fileEncoding);
                } catch (IOException e) {
                    log(e.toString());
                    throw new IllegalStateException();
                }
                if (!readBack.equals(msg)) {
                    throw new IllegalStateException("Readback failed:\nwrite:\"" + msg + "\"\nread :\"" + readBack + "\"");
                }
            } else {
                throw new IllegalStateException("Cant create new msg file, already exists:" + path);
            }
        } else {
            throw new NullPointerException();
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
        if (pair != null) {
            file = pair.getKey();
            ret = pair.getValue();
            if (file != null && ret != null) {
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
        if (pair != null) {
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
    public void clear() {
        deleteContentsRecursively(qDir);
    }

    // Not implemented
    @Override
    public boolean addAll(Collection<? extends String> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Not implemented
    @Override
    public boolean contains(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Not implemented
    @Override
    public Iterator<String> iterator() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Not implemented
    @Override
    public Object[] toArray() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Not implemented
    @Override
    public <T> T[] toArray(T[] a) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Not implemented
    @Override
    public boolean remove(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Not implemented
    @Override
    public boolean containsAll(Collection<?> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Not implemented
    @Override
    public boolean removeAll(Collection<?> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    // Not implemented
    @Override
    public boolean retainAll(Collection<?> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }



    private String getNewFilename() {
        String ret = "";
        List<File> msgFiles = dotMsgFilesSorted(qDir, true);
        int newNumber = 0;
        if(msgFiles.size() > 0) {
            File latest = msgFiles.get(msgFiles.size() - 1);
            newNumber = Integer.parseInt(latest.getName().replace(".msg", "")) + 1;
        }
        ret = padOrClipString(String.valueOf(newNumber),"0",12,Alignment.Right,"");
        ret += ".msg";

        return ret;
    }

    private File getOldestMsgFilename() {
        File ret = null;
        List<File> msgFiles = dotMsgFilesSorted(qDir, false);
        if(msgFiles.size() > 0) {
            File oldest = msgFiles.get(msgFiles.size() - 1);
            ret = oldest;
        }
        return ret;
    }

    private Pair<File, String> get() throws Exception {
        Pair<File, String> ret = null;
        File oldestFile = getOldestMsgFilename();
//        log("reading file:" + oldestFile.getName());
        if (oldestFile == null) {
            throw new NoSuchElementException("No .msg file in dir: " + qDir);
        } else {
            String fContent = null;
            fContent = readFile(oldestFile.toPath(), fileEncoding);
            ret = new Pair<>(oldestFile, fContent);
        }
        return ret;
    }

    // Possibly returns an empty List
    private List<File> dotMsgFilesSorted(File dir, boolean ascending) {
        List<File> ret = new ArrayList<>();
        File[] listOfFiles = dir.listFiles();
        if (listOfFiles != null) {
            Collections.addAll(ret, listOfFiles);
            if (ret.size() > 0) {
                ret = filterbyFilename(ret, ".*\\.msg$");
                Collections.sort(ret, (o1, o2) -> {
                    long ret1 = 0;
                    int f1 = Integer.parseInt(o1.getName().replace(".msg", ""));
                    int f2 = Integer.parseInt(o2.getName().replace(".msg", ""));
                    ret1 = f1 - f2;
                    if (!ascending) {
                        ret1 = ret1 * -1;
                    }
                    return (int) clip(ret1, -1, 1);
                });
            }
        }
        return ret;
    }


}

