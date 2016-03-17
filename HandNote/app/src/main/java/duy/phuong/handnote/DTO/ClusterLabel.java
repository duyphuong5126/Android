package duy.phuong.handnote.DTO;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Phuong on 10/03/2016.
 */
public class ClusterLabel {
    private int mClusterCount;
    private ArrayList<Label> mListLabel;

    public ClusterLabel() {
        mListLabel = new ArrayList<>();

        computeClusterCount();
    }

    public ClusterLabel(ArrayList<Label> ListLabel) {
        mListLabel = new ArrayList<>();
        mListLabel.addAll(ListLabel);

        computeClusterCount();
    }

    private void computeClusterCount() {
        mClusterCount = 0;
        for (Label label1 : mListLabel) {
            mClusterCount += label1.getCount();
        }
    }

    public String getClusterLabel() {
        double percent = 0.89d;
        String l = "";
        for (Label label : mListLabel) {
            double p = getLabelPercentage(label);
            if (p > percent) {
                percent = p;
                l = label.getLabel();
            }
        }

        if (l.length() != 1) {
            Log.d("Label", toString());
            return toString();
        }

        Log.d("Label", l);
        return l;
    }

    public boolean addNewLabel(Label label) {
        if (label == null || label.getCount() == 0 || label.getLabel() == null || label.getLabel().length() <= 0) {
            return false;
        }

        boolean exits = false;
        for (Label label1 : mListLabel) {
            if (label.getLabel().equals(label1.getLabel())) {
                label1.setCount(label1.getCount() + label.getCount());
                exits = true;
            }
        }

        if (!exits) {
            mListLabel.add(label);
        }

        computeClusterCount();

        return true;
    }

    public ArrayList<Label> getListLabel() {
        return mListLabel;
    }

    public double getLabelPercentage(Label label) {
        return label.getCount() / ((double) mClusterCount);
    }

    public int getTotal() {
        computeClusterCount();
        return mClusterCount;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Label label : mListLabel) {
            builder.append(label.getLabel());
        }
        return builder.toString();
    }
}
