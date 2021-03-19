package foundation.pEp.pitytest.utils;

import java.util.ArrayList;
import java.util.List;

public class SlowFilteredNoise {
    private List<RangeInt> selection = new ArrayList<>();
    private List<RangeInt> filter = new ArrayList<>();

    private RangeInt bandwidth = new RangeInt(0);

    public SlowFilteredNoise() {
    }

    public SlowFilteredNoise(List<RangeInt> selection, List<RangeInt> filter) {
        setSelection(selection);
        setFilter(filter);
    }

    public SlowFilteredNoise(RangeInt selection, RangeInt filter) {
        addSelection(selection);
        addFilter(filter);
    }

    public List<RangeInt> getSelection() {
        return selection;
    }

    public void setSelection(List<RangeInt> selection) {
        if (selection != null) {
            this.selection = selection;
        } else {
            this.selection.clear();
        }
        calculateBandwitdth();
    }

    public List<RangeInt> getFilter() {
        return filter;
    }

    public void setFilter(List<RangeInt> filter) {
        if (filter != null) {
            this.filter = filter;
        } else {
            this.filter.clear();
        }
    }

    public void addSelection(RangeInt rng) {
        if (rng != null) {
            this.selection.add(rng);
        }
        bandwidth = calculateBandwitdth();
    }

    public void addFilter(RangeInt rng) {
        if (rng != null) {
            this.filter.add(rng);
        }
    }

    public RangeInt getBandwidth() {
        return bandwidth;
    }

    public RangeInt calculateBandwitdth() {
        RangeInt ret = new RangeInt(0);
        int min = 0;
        int max = 0;
        if (this.selection.isEmpty()) {
            ret.setMinMax(0);
        } else {
            min = selection.get(0).effectiveMin();
            max = selection.get(0).effectiveMax();
            for (RangeInt sel : this.selection) {
                if (sel.effectiveMin() < min) {
                    min = sel.effectiveMin();
                }
                if (sel.effectiveMax() > max) {
                    max = sel.effectiveMax();
                }
            }
            ret.setMinMax(min, max);
        }
        return ret;
    }

    public int getNext() {
        int ret = 0;
        boolean candidatePositive = false;
        while (!candidatePositive) {
            int candidate = TestUtils.randomInt(bandwidth);
            if (this.selection.isEmpty()) {
                candidatePositive = true;
                ret = candidate;
            } else {
                if (this.contains(selection, candidate)) {
                    if (!this.contains(filter, candidate)) {
                        candidatePositive = true;
                        ret = candidate;
                    }
                }
            }
        }
        return ret;
    }

    private boolean contains(List<RangeInt> rngList, int val) {
        boolean ret = false;
        for (RangeInt rng : rngList) {
            if (rng.contains(val)) {
                ret = true;
                continue;
            }
        }
        return ret;
    }

    private String rangeListToString(List<RangeInt> rngl) {
        String ret = "";
        int i = 0;
        for (RangeInt rng : rngl) {
            ret += "Selection nr: " + i + "\n";
            ret += rng.toString();
            i++;
        }
        return ret;
    }


    @Override
    public String toString() {
        String ret = "";
        ret += "Selection Ranges\n";
        ret += rangeListToString(selection);
        ret += "\nFilter Ranges\n";
        ret += rangeListToString(filter);
        ret += "\nBandwidth\n";
        ret += getBandwidth() + "\n\n";
        return ret;
    }
}
