package duy.phuong.handnote.DTO;

import java.io.Serializable;

/**
 * Created by Phuong on 04/03/2016.
 */
public abstract class DataTransfer implements Serializable {
    public static final String TRAINING_SET = "TRAINING_SET";
    private Object mData;

    public DataTransfer(Object object) {
        this.mData = object;
    }

    public Object getData() {
        return mData;
    }

    public abstract String identify();
}
