package duy.phuong.handnote.DTO;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Phuong on 10/03/2016.
 */
public class ClusterLabel {
    private static int CLUSTER_COUNT;
    private ArrayList<Label> mListLabel;

    public ClusterLabel() {
        mListLabel = new ArrayList<>();
    }

    public ClusterLabel(ArrayList<Label> ListLabel) {
        mListLabel = new ArrayList<>();
        mListLabel.addAll(ListLabel);
    }

    public boolean updateLabel(String label) {
        boolean result = false;
        CLUSTER_COUNT = 0;
        if (label != null && !"".equals(label)) {
            for (Label label1 : mListLabel) {
                if (label.equals(label1.getLabel())) {
                    label1.setCount(label1.getCount() + 1);
                    result = true;
                }

                CLUSTER_COUNT += label1.getCount();
            }
        }
        return result;
    }

    public boolean addNewLabel(Label label) {
        if (label == null || label.getCount() == 0 || "".equals(label.getLabel())) {
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

        return true;
    }

    public double getLabelPercent(String label) {
        for (Label label1 : mListLabel) {
            if (label.equals(label1.getLabel())) {
                return (((double) label1.getCount()) / CLUSTER_COUNT);
            }
        }

        return 0;
    }

    public Label getLabel(String label) {
        for (Label label1 : mListLabel) {
            if (label.equals(label1.getLabel())) {
                return label1;
            }
        }
        return null;
    }

    public ArrayList<Label> getListLabel() {
        return mListLabel;
    }

    public int getTotal() {
        int sum = 0;
        for (Label label : mListLabel) {
            sum += label.getCount();
        }
        return sum;
    }

    @Override
    public String toString() {
        String s = "";
        for (Label label : mListLabel) {
            s += label.getLabel();
        }
        return s;
    }
}
