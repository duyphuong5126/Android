package duy.phuong.handnote.Listener;

import android.graphics.Bitmap;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Phuong on 26/11/2015.
 */
public interface RecognitionCallback {
    void onRecognizeSuccess(ArrayList<Bitmap> listBitmaps);
}
