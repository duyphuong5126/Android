package duy.phuong.handnote.Recognizer.MachineLearning;

import java.io.Serializable;

import duy.phuong.handnote.DTO.StandardImage;

/**
 * Created by Phuong on 01/03/2016.
 */
public class Input implements Serializable{
    public static final int VECTOR_DIMENSIONS = StandardImage.WIDTH * StandardImage.HEIGHT;

    public byte[] mInputData;

    public String mLabel;

    public Input() {
        mInputData = new byte[VECTOR_DIMENSIONS];
    }
}
