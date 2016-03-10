package duy.phuong.handnote.DTO;

/**
 * Created by Phuong on 09/03/2016.
 */
public class Label {
    private String mLabel;
    private int mCount;

    public Label(String mLabel, int mCount) {
        this.mLabel = mLabel;
        this.mCount = mCount;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String Label) {
        this.mLabel = Label;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int Count) {
        this.mCount = Count;
    }
}
