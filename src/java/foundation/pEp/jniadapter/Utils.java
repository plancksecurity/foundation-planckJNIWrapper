package foundation.pEp.jniadapter;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private Utils() { }

    public static byte[] toUTF8(String str) {
        if (str == null)
            return null;

        try {
            String _str = Normalizer.normalize(str, Normalizer.Form.NFC);
            byte _buf[] = _str.getBytes("UTF-8");
            byte _cpy[] = new byte[_buf.length];
            System.arraycopy(_buf,0,_cpy,0,_buf.length);
            return _cpy;
        }
        catch (UnsupportedEncodingException e) {
            assert false;
            return new byte[0];
        }
    }

    public static Vector<byte[]> toUTF8(Vector<String> list) {
        if (list == null)
            return null;

        Vector<byte[]> result = new Vector<byte[]>(list.size());

        for (int i=0; i<list.size(); i++)
            result.add(toUTF8(list.get(i)));

        return result;
    }

    public static Pair<byte[], byte[]> toUTF8(Pair<String, String> pair) {
        if (pair == null)
            return null;

        Pair<byte[], byte[]> result = new Pair<byte[], byte[]>();

        result.first = toUTF8(pair.first);
        result.second = toUTF8(pair.second);

        return result;
    }

    public static ArrayList<Pair<byte[], byte[]>> toUTF8(ArrayList<Pair<String, String>> list) {
        if (list == null)
            return null;

        ArrayList<Pair<byte[], byte[]>> result = new ArrayList<Pair<byte[], byte[]>>(list.size());

        for (int i=0; i<list.size(); i++)
            result.set(i, toUTF8(list.get(i)));

        return result;
    }

    public static String toUTF16(byte[] utf8) {
        if (utf8 == null)
            return null;

        try {
            byte newUtf8[] = new byte[utf8.length];
            System.arraycopy(utf8,0,newUtf8,0,utf8.length);

            return new String(newUtf8, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            assert false;
            return new String();
        }
    }

    public static Vector<String> toUTF16(Vector<byte[]> list) {
        if (list == null)
            return null;

        Vector<String> result = new Vector<String>(list.size());

        for (int i=0; i<list.size(); i++)
            result.add(toUTF16(list.get(i)));

        return result;
    }

    public static Pair<String, String> toUTF16(Pair<byte[], byte[]> pair) {
        if (pair == null)
            return null;

        Pair<String, String> result = new Pair<String,String>();

        result.first = toUTF16(pair.first);
        result.second = toUTF16(pair.second);

        return result;
    }

    public static ArrayList<Pair<String, String>> toUTF16(ArrayList<Pair<byte[], byte[]>> list) {
        if (list == null)
            return null;

        ArrayList<Pair<String, String>> result = new ArrayList<Pair<String, String>>(list.size());

        for (int i=0; i<list.size(); i++)
            result.set(i, toUTF16(list.get(i)));

        return result;
    }

    public static boolean URIEqual(String left, String right) {
        if(left == right) return true;
        if(left == null) return false;
        if(right == null) return false;
        return URITrim(left).equals(URITrim(right));
    }

    // Returns the hash of a string that represents a URI
    // Returns 0 if uri i null
    public static int URIHash(String uri) {
        if(uri == null) {
            return 0;
        }
        return URITrim(uri).hashCode();
    }

    private static String URITrim(String uri) {
        Pattern pattern = Pattern.compile("^.*?://");
        Matcher leftMatcher = pattern.matcher(uri.trim());
        return leftMatcher.replaceAll("");
    }
}
