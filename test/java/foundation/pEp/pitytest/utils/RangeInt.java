package foundation.pEp.pitytest.utils;

import java.util.HashSet;
import java.util.Set;

public class RangeInt {
    private int min;
    private int max;

    public RangeInt(int min, int max) {
        setMinMax(min,max);
    }

    public RangeInt(int oneOnly) {
        setMinMax(oneOnly);
    }

    public void setMinMax(int min, int max) {
        setMin(min);
        setMax(max);
    }

    public void setMinMax(int oneOnly) {
        setMin(oneOnly);
        setMax(oneOnly);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public boolean isInverted() {
        if (min > max) {
            return true;
        } else {
            return false;
        }
    }

    public int effectiveMin() {
        return Math.min(min, max);
    }

    public int effectiveMax() {
        return Math.max(min, max);
    }

    // Always > 0
    public int getSize() {
        return Math.abs(max - min) + 1; // + 1 because range includes min and max
    }

    public Set<Integer> toSet() {
        Set<Integer> ret = new HashSet();
        if (!this.isInverted()) {
            for (int i = min; i <= max; i++) {
                ret.add(i);
            }
        } else {
            for (int i = min; i >= max; i--) {
                ret.add(i);
            }
        }
        return ret;
    }

    // includes min and max
    // Works for inverted ranges too
    public boolean contains(int val) {
        boolean ret = false;
        if (!this.isInverted()) {
            if (val >= min && val <= max) {
                ret = true;
            }
        } else {
            if (val >= max && val <= min) {
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        String ret = "";
        ret += "min: " + getMin()+ "\n";
        ret += "max: " + getMax()+ "\n";
        ret += "size: " + getSize()+ "\n";
        ret += "isInverted: " + isInverted()+ "\n";
        ret += "effectiveMin: " + effectiveMin()+ "\n";
        ret += "effectiveMax: " + effectiveMax()+ "\n";

        return ret;
    }
}