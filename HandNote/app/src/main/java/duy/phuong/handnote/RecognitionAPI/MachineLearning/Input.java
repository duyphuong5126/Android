package duy.phuong.handnote.RecognitionAPI.MachineLearning;

/**
 * Created by Phuong on 01/03/2016.
 */
public class Input {
    public static final int VECTOR_DIMENSIONS = 560;

    public byte[] mInputData;

    public Input() {
        mInputData = new byte[VECTOR_DIMENSIONS];
    }
}
