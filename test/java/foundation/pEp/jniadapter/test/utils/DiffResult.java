package foundation.pEp.jniadapter.test.utils;

public class DiffResult {
    private String diff = "";
    private int count = 0;
    private int firstDiffByte = 0;

    DiffResult() {
    }


    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFirstDiffByte() {
        return firstDiffByte;
    }

    public void setFirstDiffByte(int firstDiffByte) {
        this.firstDiffByte = firstDiffByte;
    }

    public String toString() {
        String ret = "";
        ret += "Nr bytes differ\t: " + getCount() + "\n";
        ret += "first byte\t: " + getFirstDiffByte() + "\n";
        ret += "diff:\n";
        ret += getDiff();
        ret += "\n";

        return ret;
    }
}
